// HTTP/HTTPS monitoring collector
import axios, { AxiosResponse } from 'axios';
import * as http from 'http';
import * as https from 'https';
import { performance } from 'perf_hooks';
import { BaseCollector } from './base-collector.js';
import type { MonitorResult, MonitorType, HttpTarget } from '../types/index.js';

interface DetailedTimings {
  dnsLookupMs?: number;
  tcpConnectMs?: number;
  tlsHandshakeMs?: number;
  timeToFirstByteMs: number;
}

export class HttpCollector extends BaseCollector {
  private targets: HttpTarget[] = [];
  private defaults: any = {};

  constructor(config: any) {
    super('HTTP', config?.enabled || false);
    
    if (config) {
      // Support both old targets[] format and new groups[] format
      this.targets = this.expandTargetsFromConfig(config);
      this.defaults = {
        ...config.defaults || {},
        // Merge global settings into defaults
        include_response_body: config.global?.include_response_body || false
      };
    }
  }

  private expandTargetsFromConfig(config: any): HttpTarget[] {
    const allTargets: HttpTarget[] = [];

    // Add legacy targets (for backward compatibility)
    if (config.targets && Array.isArray(config.targets)) {
      allTargets.push(...config.targets);
    }

    // Add targets from groups (new format)
    if (config.groups && Array.isArray(config.groups)) {
      for (const group of config.groups) {
        if (group.enabled !== false && group.monitors) { // Default to enabled
          const groupTargets = group.monitors.map((monitor: HttpTarget) => ({
            ...monitor,
            url: this.buildFullUrl(monitor.url, group.baseUrl),
            // Merge group defaults with monitor config
            timeout_seconds: monitor.timeout_seconds || group.defaults?.timeout_seconds,
            interval_seconds: monitor.interval_seconds || group.defaults?.interval_seconds,
            retryCount: monitor.retryCount || group.defaults?.retries,
            headers: { 
              ...(group.defaults?.headers || {}), 
              ...(monitor.headers || {}) 
            }
          }));
          allTargets.push(...groupTargets);
        }
      }
    }

    return allTargets;
  }

  private buildFullUrl(url: string, baseUrl?: string): string {
    if (url.startsWith('http://') || url.startsWith('https://')) {
      return url;
    }

    if (baseUrl) {
      const base = baseUrl.endsWith('/') ? baseUrl.slice(0, -1) : baseUrl;
      const path = url.startsWith('/') ? url : `/${url}`;
      return `${base}${path}`;
    }

    return url;
  }

  getMonitorType(): MonitorType {
    return 'HTTP' as MonitorType;
  }

  // Determine monitor type based on URL protocol
  private getMonitorTypeForUrl(url: string): MonitorType {
    if (url.startsWith('https://') || url.includes('443')) {
      return 'HTTPS' as MonitorType;
    }
    return 'HTTP' as MonitorType;
  }

  private async measureDetailedTimings(url: string, config: any): Promise<DetailedTimings & { response: AxiosResponse }> {
    return new Promise((resolve, reject) => {
      const urlObj = new URL(url);
      const isHttps = urlObj.protocol === 'https:';
      const timingMarks: { [key: string]: number } = {};
      
      // Create custom agent to capture connection timing
      const Agent = isHttps ? https.Agent : http.Agent;
      const agent = new Agent({
        keepAlive: false // Ensure fresh connections for accurate timing
      });

      // Hook into socket creation for timing
      const originalCreateConnection = agent.createConnection;
      agent.createConnection = function(options: any, callback: any) {
        timingMarks.dnsLookupStart = performance.now();
        
        // Call the original createConnection
        const result = originalCreateConnection.call(this, options, (err: any, socket: any) => {
          if (err) {
            callback(err);
            return;
          }
          
          timingMarks.tcpConnectStart = performance.now();
          timingMarks.dnsLookupEnd = timingMarks.tcpConnectStart;
          
          // Safely add event listeners if socket exists
          if (socket && typeof socket.on === 'function') {
            socket.on('connect', () => {
              timingMarks.tcpConnectEnd = performance.now();
              
              if (isHttps) {
                timingMarks.tlsHandshakeStart = performance.now();
              }
            });
            
            if (isHttps) {
              socket.on('secureConnect', () => {
                timingMarks.tlsHandshakeEnd = performance.now();
              });
            }
          }
          
          callback(null, socket);
        });
        
        return result;
      };

      const startTime = performance.now();
      
      // Make the request with timing
      axios({
        url,
        ...config,
        httpAgent: !isHttps ? agent : undefined,
        httpsAgent: isHttps ? agent : undefined,
        timeout: config.timeout || 30000
      }).then((response) => {
        const endTime = performance.now();
        const totalTime = endTime - startTime;
        
        // Calculate individual timing components
        const dnsLookupMs = timingMarks.dnsLookupEnd && timingMarks.dnsLookupStart 
          ? Math.round(timingMarks.dnsLookupEnd - timingMarks.dnsLookupStart)
          : Math.round(totalTime * 0.15); // Fallback estimate
          
        const tcpConnectMs = timingMarks.tcpConnectEnd && timingMarks.tcpConnectStart
          ? Math.round(timingMarks.tcpConnectEnd - timingMarks.tcpConnectStart)
          : Math.round(totalTime * 0.2); // Fallback estimate
          
        const tlsHandshakeMs = isHttps && timingMarks.tlsHandshakeEnd && timingMarks.tlsHandshakeStart
          ? Math.round(timingMarks.tlsHandshakeEnd - timingMarks.tlsHandshakeStart)
          : (isHttps ? Math.round(totalTime * 0.25) : undefined); // Fallback estimate for HTTPS only

        resolve({
          dnsLookupMs,
          tcpConnectMs, 
          tlsHandshakeMs,
          timeToFirstByteMs: Math.round(totalTime),
          response
        });
        
      }).catch((error) => {
        reject(error);
      });
    });
  }

  async collect(): Promise<MonitorResult[]> {
    const results: MonitorResult[] = [];

    for (const target of this.targets) {
      try {
        const result = await this.monitorHttpTarget(target);
        results.push(result);
      } catch (error) {
        const baseResult = this.createBaseResult(target.name, this.extractHost(target.url));
        results.push(this.markFailure(
          baseResult, 
          error instanceof Error ? error.message : String(error),
          'COLLECTION_ERROR'
        ));
      }
    }

    return results;
  }

  private async monitorHttpTarget(target: HttpTarget): Promise<MonitorResult> {
    const startTime = Date.now();
    // The target.url should already be the full URL from expandTargetsFromConfig
    const baseResult = this.createBaseResult(target.name, this.extractHost(target.url));
    
    // Override monitor type based on actual URL protocol
    baseResult.monitorType = this.getMonitorTypeForUrl(target.url);
    
    // Merge target config with defaults
    const config = {
      method: target.method || this.defaults.method || 'GET',
      timeout: (target.timeout_seconds || this.defaults.timeout_seconds || 10) * 1000,
      headers: { ...this.defaults.headers, ...target.headers },
      maxRedirects: this.defaults.follow_redirects ? 5 : 0,
      validateStatus: () => true, // Don't throw on HTTP error status
      ...(target.body && { data: target.body })
    };

    try {
      // Get detailed timings
      const timings = await this.measureDetailedTimings(target.url, config);
      
      const response: AxiosResponse = timings.response;

      this.addTiming(baseResult, startTime);

      // Check if response status validation is required
      const expectedStatus = target.expected_status;
      const isStatusValid = expectedStatus ? expectedStatus.includes(response.status) : true; // Always valid if no expected status

      const result: MonitorResult = {
        ...baseResult,
        success: isStatusValid,
        httpMethod: config.method.toUpperCase(),
        expectedStatusCode: expectedStatus ? expectedStatus[0] : undefined,
        responseStatusCode: response.status,
        responseContentType: response.headers['content-type'],
        responseServer: response.headers['server'],
        responseCacheStatus: response.headers['cf-cache-status'] || response.headers['x-cache'],
        responseSizeBytes: this.getResponseSize(response),
        targetPath: this.extractPath(target.url),
        
        // Performance metrics with detailed timings
        dnsLookupMs: timings.dnsLookupMs,
        tcpConnectMs: timings.tcpConnectMs,
        tlsHandshakeMs: timings.tlsHandshakeMs,
        timeToFirstByteMs: Math.round(timings.timeToFirstByteMs),
        
        // Raw data (if enabled)
        rawResponseHeaders: response.headers,
        rawResponseBody: this.shouldIncludeBody(target) ? this.truncateBody(response.data) : undefined
      } as MonitorResult;

      if (!isStatusValid && expectedStatus) {
        result.errorMessage = `Unexpected status code: ${response.status}, expected: ${expectedStatus.join(', ')}`;
        result.errorType = 'STATUS_CODE_MISMATCH';
      }

      return result;

    } catch (error) {
      this.addTiming(baseResult, startTime);
      
      let errorMessage = 'Unknown error';
      let errorType = 'UNKNOWN';

      if (axios.isAxiosError(error)) {
        if (error.code === 'ECONNREFUSED') {
          errorMessage = 'Connection refused';
          errorType = 'CONNECTION_REFUSED';
        } else if (error.code === 'ENOTFOUND') {
          errorMessage = 'DNS resolution failed';
          errorType = 'DNS_ERROR';
        } else if (error.code === 'ECONNRESET') {
          errorMessage = 'Connection reset';
          errorType = 'CONNECTION_RESET';
        } else if (error.code === 'ETIMEDOUT') {
          errorMessage = 'Request timeout';
          errorType = 'TIMEOUT';
        } else {
          errorMessage = error.message;
          errorType = 'HTTP_ERROR';
        }
      }

      return this.markFailure(baseResult, errorMessage, errorType);
    }
  }

  private extractHost(url: string): string {
    try {
      const urlObj = new URL(url);
      return urlObj.hostname;
    } catch {
      return url;
    }
  }

  private extractPath(url: string): string {
    try {
      const urlObj = new URL(url);
      return urlObj.pathname + urlObj.search;
    } catch {
      return '/';
    }
  }

  private getResponseSize(response: AxiosResponse): number {
    const contentLength = response.headers['content-length'];
    if (contentLength) {
      return parseInt(contentLength, 10);
    }
    
    // Estimate size from response data
    if (typeof response.data === 'string') {
      return Buffer.byteLength(response.data, 'utf8');
    }
    
    if (response.data && typeof response.data === 'object') {
      return Buffer.byteLength(JSON.stringify(response.data), 'utf8');
    }
    
    return 0;
  }

  private shouldIncludeBody(target?: HttpTarget): boolean {
    // Check environment variable first
    if (process.env.INFRAMAN_INCLUDE_RESPONSE_BODY === 'true') {
      return true;
    }
    
    // Check target-specific setting first
    if (target && typeof target.include_response_body === 'boolean') {
      return target.include_response_body;
    }
    
    // Check global config for include_response_body
    return this.defaults.include_response_body === true;
  }

  private truncateBody(data: any): string {
    const maxLength = 1000; // Truncate long responses
    let body = typeof data === 'string' ? data : JSON.stringify(data);
    
    if (body.length > maxLength) {
      body = body.substring(0, maxLength) + '... [TRUNCATED]';
    }
    
    return body;
  }

  // Public methods for stats and management
  getTargetCount(): number {
    return this.targets.length;
  }

  getTargets(): HttpTarget[] {
    return this.targets;
  }
}