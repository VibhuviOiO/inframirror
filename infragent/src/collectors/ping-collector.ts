// PING monitoring collector
import { exec } from 'child_process';
import { promisify } from 'util';
import { BaseCollector } from './base-collector.js';
import type { MonitorResult, MonitorType, PingTarget } from '../types/index.js';

const execAsync = promisify(exec);

export class PingCollector extends BaseCollector {
  private targets: PingTarget[] = [];
  private defaults: any = {};

  constructor(config: any) {
    super('PING', config?.enabled || false);
    
    if (config) {
      this.targets = config.targets || [];
      this.defaults = config.defaults || {};
    }
  }

  getMonitorType(): MonitorType {
    return 'PING' as MonitorType;
  }

  async collect(): Promise<MonitorResult[]> {
    const results: MonitorResult[] = [];

    for (const target of this.targets) {
      try {
        const result = await this.monitorPingTarget(target);
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

  private async monitorPingTarget(target: PingTarget): Promise<MonitorResult> {
    const startTime = Date.now();
    const baseResult = this.createBaseResult(target.name, target.host);

    const packetCount = target.packet_count || this.defaults.packet_count || 3;
    const timeout = target.timeout_seconds || this.defaults.timeout_seconds || 3;

    try {
      // Build ping command based on OS
      const pingCommand = this.buildPingCommand(target.host, packetCount, timeout);
      
      const { stdout, stderr } = await execAsync(pingCommand, {
        timeout: (timeout + 2) * 1000 // Add buffer to command timeout
      });

      if (stderr && stderr.length > 0) {
        this.addTiming(baseResult, startTime);
        return this.markFailure(
          baseResult,
          `Ping error: ${stderr}`,
          'PING_ERROR'
        );
      }

      // Parse ping output
      const pingStats = this.parsePingOutput(stdout);
      
      this.addTiming(baseResult, startTime);

      if (pingStats.packetLoss === 100) {
        return this.markFailure(
          baseResult,
          '100% packet loss',
          'TOTAL_PACKET_LOSS'
        );
      }

      return this.markSuccess(baseResult, {
        packetLoss: pingStats.packetLoss,
        jitterMs: pingStats.jitter,
        responseTime: pingStats.avgTime,
        rawNetworkData: {
          min_time: pingStats.minTime,
          max_time: pingStats.maxTime,
          avg_time: pingStats.avgTime,
          packet_count: packetCount,
          packets_transmitted: pingStats.transmitted,
          packets_received: pingStats.received
        }
      });

    } catch (error) {
      this.addTiming(baseResult, startTime);
      
      let errorMessage = 'Unknown ping error';
      let errorType = 'PING_ERROR';

      if (error instanceof Error) {
        if (error.message.includes('timeout')) {
          errorMessage = 'Ping timeout';
          errorType = 'TIMEOUT';
        } else if (error.message.includes('not found') || error.message.includes('Name or service not known')) {
          errorMessage = 'Host not found';
          errorType = 'DNS_ERROR';
        } else if (error.message.includes('Network is unreachable')) {
          errorMessage = 'Network unreachable';
          errorType = 'NETWORK_UNREACHABLE';
        } else {
          errorMessage = error.message;
        }
      }

      return this.markFailure(baseResult, errorMessage, errorType);
    }
  }

  private buildPingCommand(host: string, count: number, timeout: number): string {
    const isWindows = process.platform === 'win32';
    
    if (isWindows) {
      return `ping -n ${count} -w ${timeout * 1000} ${host}`;
    } else {
      // Unix-like systems (Linux, macOS)
      return `ping -c ${count} -W ${timeout} ${host}`;
    }
  }

  private parsePingOutput(output: string): PingStats {
    const lines = output.split('\n');
    
    // Initialize default values
    let transmitted = 0;
    let received = 0;
    let minTime = 0;
    let maxTime = 0;
    let avgTime = 0;
    let jitter = 0;

    // Parse packet statistics
    for (const line of lines) {
      // Unix format: "3 packets transmitted, 3 received, 0% packet loss"
      const unixMatch = line.match(/(\d+) packets transmitted, (\d+) received, (\d+\.?\d*)% packet loss/);
      if (unixMatch) {
        transmitted = parseInt(unixMatch[1]);
        received = parseInt(unixMatch[2]);
        break;
      }

      // Windows format: "Packets: Sent = 3, Received = 3, Lost = 0 (0% loss)"
      const windowsMatch = line.match(/Packets: Sent = (\d+), Received = (\d+), Lost = \d+ \((\d+)% loss\)/);
      if (windowsMatch) {
        transmitted = parseInt(windowsMatch[1]);
        received = parseInt(windowsMatch[2]);
        break;
      }
    }

    // Parse timing statistics
    for (const line of lines) {
      // Unix format: "round-trip min/avg/max/stddev = 1.234/2.345/3.456/0.123 ms"
      const unixTimingMatch = line.match(/round-trip min\/avg\/max\/stddev = ([\d.]+)\/([\d.]+)\/([\d.]+)\/([\d.]+) ms/);
      if (unixTimingMatch) {
        minTime = parseFloat(unixTimingMatch[1]);
        avgTime = parseFloat(unixTimingMatch[2]);
        maxTime = parseFloat(unixTimingMatch[3]);
        jitter = parseFloat(unixTimingMatch[4]);
        break;
      }

      // macOS format: "round-trip min/avg/max/stddev = 1.234/2.345/3.456/0.123 ms"
      const macTimingMatch = line.match(/round-trip min\/avg\/max\/stddev = ([\d.]+)\/([\d.]+)\/([\d.]+)\/([\d.]+) ms/);
      if (macTimingMatch) {
        minTime = parseFloat(macTimingMatch[1]);
        avgTime = parseFloat(macTimingMatch[2]);
        maxTime = parseFloat(macTimingMatch[3]);
        jitter = parseFloat(macTimingMatch[4]);
        break;
      }

      // Windows format: "Minimum = 1ms, Maximum = 3ms, Average = 2ms"
      const windowsTimingMatch = line.match(/Minimum = (\d+)ms, Maximum = (\d+)ms, Average = (\d+)ms/);
      if (windowsTimingMatch) {
        minTime = parseInt(windowsTimingMatch[1]);
        maxTime = parseInt(windowsTimingMatch[2]);
        avgTime = parseInt(windowsTimingMatch[3]);
        jitter = maxTime - minTime; // Approximate jitter for Windows
        break;
      }
    }

    const packetLoss = transmitted > 0 ? ((transmitted - received) / transmitted) * 100 : 100;

    return {
      transmitted,
      received,
      packetLoss: Math.round(packetLoss * 100) / 100, // Round to 2 decimal places
      minTime,
      maxTime,
      avgTime,
      jitter
    };
  }

  // Public methods for stats and management
  getTargetCount(): number {
    return this.targets.length;
  }

  getTargets(): PingTarget[] {
    return this.targets;
  }
}

interface PingStats {
  transmitted: number;
  received: number;
  packetLoss: number;
  minTime: number;
  maxTime: number;
  avgTime: number;
  jitter: number;
}