// DNS monitoring collector
import { promisify } from 'util';
import { resolve as dnsResolve, lookup as dnsLookup } from 'dns';
import { BaseCollector } from './base-collector.js';
import type { MonitorResult, MonitorType, DnsTarget } from '../types/index.js';

const resolve = promisify(dnsResolve);
const lookup = promisify(dnsLookup);

export class DnsCollector extends BaseCollector {
  private targets: DnsTarget[] = [];
  private defaults: any = {};

  constructor(config: any) {
    super('DNS', config?.enabled || false);
    
    if (config) {
      this.targets = config.targets || [];
      this.defaults = config.defaults || {};
    }
  }

  getTargetCount(): number {
    return this.targets.length;
  }

  getMonitorType(): MonitorType {
    return 'DNS' as MonitorType;
  }

  async collect(): Promise<MonitorResult[]> {
    return await this.executeMonitoring();
  }

  protected async executeMonitoring(): Promise<MonitorResult[]> {
    const results: MonitorResult[] = [];
    const promises = this.targets.map(target => this.monitorDnsTarget(target));
    
    const targetResults = await Promise.allSettled(promises);
    
    for (const result of targetResults) {
      if (result.status === 'fulfilled') {
        results.push(result.value);
      }
    }

    return results;
  }

  private getGlobalThresholds() {
    return {
      warning: this.defaults.thresholds?.warning || 500,
      critical: this.defaults.thresholds?.critical || 1000
    };
  }

  private async monitorDnsTarget(target: DnsTarget): Promise<MonitorResult> {
    const startTime = Date.now();
    const executedAt = new Date();

    try {
      const timeout = (target.timeout_seconds || this.defaults.timeout_seconds || 10) * 1000;
      
      // Perform DNS lookup with timeout
      const lookupPromise = this.performDnsLookup(target);
      const timeoutPromise = new Promise((_, reject) => 
        setTimeout(() => reject(new Error('DNS lookup timeout')), timeout)
      );
      
      const dnsResult = await Promise.race([lookupPromise, timeoutPromise]) as any;
      const responseTime = Date.now() - startTime;

      // Get thresholds for this monitor
      const thresholds = target.thresholds || this.getGlobalThresholds();

      return {
        monitorId: `dns-${target.name.replace(/\s+/g, '-').toLowerCase()}`,
        monitorName: target.name,
        monitorType: 'DNS' as MonitorType,
        targetHost: target.domain,
        executedAt,
        success: true,
        responseTime,
        dnsQueryType: target.record_type || 'A',
        dnsResponseValue: JSON.stringify(dnsResult.addresses || []),
        
        // Performance thresholds
        warningThresholdMs: thresholds.warning,
        criticalThresholdMs: thresholds.critical,
        
        rawNetworkData: {
          recordType: target.record_type || 'A',
          recordCount: dnsResult.addresses?.length || 0
        }
      };

    } catch (error) {
      const responseTime = Date.now() - startTime;
      
      // Get thresholds for this monitor (even for failures)
      const thresholds = target.thresholds || this.getGlobalThresholds();
      
      return {
        monitorId: `dns-${target.name.replace(/\s+/g, '-').toLowerCase()}`,
        monitorName: target.name,
        monitorType: 'DNS' as MonitorType,
        targetHost: target.domain,
        executedAt,
        success: false,
        responseTime,
        dnsQueryType: target.record_type || 'A',
        errorMessage: error instanceof Error ? error.message : String(error),
        
        // Performance thresholds
        warningThresholdMs: thresholds.warning,
        criticalThresholdMs: thresholds.critical
      };
    }
  }

  private async performDnsLookup(target: DnsTarget): Promise<any> {
    const recordType = (target.record_type || 'A').toUpperCase();
    
    switch (recordType) {
      case 'A':
      case 'AAAA':
        // For A and AAAA records, use lookup which returns addresses
        const family = recordType === 'AAAA' ? 6 : 4;
        const lookupResult = await lookup(target.domain, { family, all: true }) as any;
        return {
          addresses: Array.isArray(lookupResult) 
            ? lookupResult.map((r: any) => r.address) 
            : [lookupResult.address],
          recordType
        };
        
      case 'MX':
        const mxRecords = await resolve(target.domain, 'MX');
        return {
          addresses: mxRecords.map((r: any) => `${r.priority} ${r.exchange}`),
          recordType: 'MX'
        };
        
      case 'TXT':
        const txtRecords = await resolve(target.domain, 'TXT');
        return {
          addresses: txtRecords.flat(),
          recordType: 'TXT'
        };
        
      case 'CNAME':
        const cnameRecords = await resolve(target.domain, 'CNAME');
        return {
          addresses: cnameRecords,
          recordType: 'CNAME'
        };
        
      case 'NS':
        const nsRecords = await resolve(target.domain, 'NS');
        return {
          addresses: nsRecords,
          recordType: 'NS'
        };
        
      default:
        // Default to A record lookup
        const defaultResult = await lookup(target.domain, { family: 4, all: true }) as any;
        return {
          addresses: Array.isArray(defaultResult) 
            ? defaultResult.map((r: any) => r.address) 
            : [defaultResult.address],
          recordType: 'A'
        };
    }
  }

  protected formatResults(results: MonitorResult[]): string {
    const successful = results.filter(r => r.success).length;
    const total = results.length;
    
    let output = `✅ DNS collected ${total} metrics in ${Date.now() - this.lastRunStart}ms\n`;
    
    for (const result of results) {
      const status = result.success ? '✅' : '❌';
      const recordInfo = result.rawNetworkData?.recordCount 
        ? ` (${result.rawNetworkData.recordCount} records)` 
        : '';
      output += `📊 ${result.monitorName}: ${status} ${result.responseTime}ms${recordInfo}\n`;
    }
    
    return output.trim();
  }

  // Store when the last run started for timing calculations
  private lastRunStart: number = Date.now();

  protected async executeMonitoringWithTiming(): Promise<MonitorResult[]> {
    this.lastRunStart = Date.now();
    return await this.executeMonitoring();
  }

  async start(): Promise<void> {
    if (!this.enabled) return;
    
    console.log(`🚀 Starting collector: ${this.name}`);
    await super.start();
  }
}