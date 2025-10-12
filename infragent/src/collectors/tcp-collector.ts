// TCP port monitoring collector
import { connect as netConnect, Socket } from 'net';
import { BaseCollector } from './base-collector.js';
import type { MonitorResult, MonitorType, TcpTarget } from '../types/index.js';

export class TcpCollector extends BaseCollector {
  private targets: TcpTarget[] = [];
  private defaults: any = {};

  constructor(config: any) {
    super('TCP', config?.enabled || false);
    
    if (config) {
      this.targets = config.targets || [];
      this.defaults = config.defaults || {};
    }
  }

  getMonitorType(): MonitorType {
    return 'TCP' as MonitorType;
  }

  async collect(): Promise<MonitorResult[]> {
    const results: MonitorResult[] = [];

    for (const target of this.targets) {
      try {
        const result = await this.monitorTcpTarget(target);
        results.push(result);
      } catch (error) {
        const baseResult = this.createBaseResult(target.name, target.host);
        results.push(this.markFailure(
          baseResult,
          error instanceof Error ? error.message : String(error),
          'COLLECTION_ERROR'
        ));
      }
    }

    return results;
  }

  private async monitorTcpTarget(target: TcpTarget): Promise<MonitorResult> {
    const startTime = Date.now();
    const baseResult = this.createBaseResult(target.name, target.host);
    
    const timeout = (target.timeout_seconds || this.defaults.timeout_seconds || 5) * 1000;

    return new Promise<MonitorResult>((resolve) => {
      const socket = new Socket();
      let resolved = false;
      let connectTime = 0;

      const timeoutHandler = setTimeout(() => {
        if (!resolved) {
          resolved = true;
          socket.destroy();
          
          this.addTiming(baseResult, startTime);
          resolve(this.markFailure(
            { ...baseResult, targetPort: target.port },
            `Connection timeout after ${timeout}ms`,
            'TIMEOUT'
          ));
        }
      }, timeout);

      socket.on('connect', () => {
        if (!resolved) {
          resolved = true;
          connectTime = Date.now() - startTime;
          
          clearTimeout(timeoutHandler);
          socket.destroy();

          this.addTiming(baseResult, startTime);
          resolve(this.markSuccess({
            ...baseResult,
            targetPort: target.port,
            tcpConnectMs: connectTime
          }));
        }
      });

      socket.on('error', (error) => {
        if (!resolved) {
          resolved = true;
          
          clearTimeout(timeoutHandler);
          socket.destroy();

          this.addTiming(baseResult, startTime);
          
          let errorMessage = error.message;
          let errorType = 'CONNECTION_ERROR';

          const errorCode = (error as any).code;
          if (errorCode === 'ECONNREFUSED') {
            errorMessage = 'Connection refused';
            errorType = 'CONNECTION_REFUSED';
          } else if (errorCode === 'EHOSTUNREACH') {
            errorMessage = 'Host unreachable';
            errorType = 'HOST_UNREACHABLE';
          } else if (errorCode === 'ENOTFOUND') {
            errorMessage = 'Host not found';
            errorType = 'DNS_ERROR';
          } else if (errorCode === 'ETIMEDOUT') {
            errorMessage = 'Connection timeout';
            errorType = 'TIMEOUT';
          }

          resolve(this.markFailure(
            { ...baseResult, targetPort: target.port },
            errorMessage,
            errorType
          ));
        }
      });

      // Attempt connection
      socket.connect(target.port, target.host);
    });
  }

  // Public methods for stats and management
  getTargetCount(): number {
    return this.targets.length;
  }

  getTargets(): TcpTarget[] {
    return this.targets;
  }
}