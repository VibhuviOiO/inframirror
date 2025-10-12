// Base collector interface and abstract class
import type { MonitorResult, MonitorType } from '../types/index.js';
import { ConfigManager } from '../config/config-manager.js';

export abstract class BaseCollector {
  public readonly name: string;
  public readonly enabled: boolean;
  protected interval: number;
  private intervalId?: NodeJS.Timeout;

  constructor(name: string, enabled: boolean = true, interval: number = 30000) {
    this.name = name;
    this.enabled = enabled;
    this.interval = interval;
  }

  // Abstract methods that must be implemented by concrete collectors
  abstract collect(): Promise<MonitorResult[]>;
  abstract getMonitorType(): MonitorType;

  // Base lifecycle methods
  async start(): Promise<void> {
    if (!this.enabled) {
      return;
    }

    console.log(`🚀 Starting collector: ${this.name}`);
    
    // Run initial collection
    await this.runCollection();
    
    // Schedule periodic collections
    this.intervalId = setInterval(async () => {
      await this.runCollection();
    }, this.interval);
  }

  async stop(): Promise<void> {
    if (this.intervalId) {
      clearInterval(this.intervalId);
      this.intervalId = undefined;
    }
    console.log(`🔄 Stopped collector: ${this.name}`);
  }

  private async runCollection(): Promise<void> {
    try {
      const startTime = Date.now();
      const results = await this.collect();
      const duration = Date.now() - startTime;
      
      console.log(`✅ ${this.name} collected ${results.length} metrics in ${duration}ms`);
      
      // Results will be handled by the storage manager
      await this.handleResults(results);
      
    } catch (error) {
      console.error(`❌ Error in collector ${this.name}:`, error);
    }
  }

  protected async handleResults(results: MonitorResult[]): Promise<void> {
    // This will be connected to the storage manager in the registry
    // For now, just log the results
    results.forEach(result => {
      console.log(`📊 ${result.monitorId}: ${result.success ? '✅' : '❌'} ${result.responseTime}ms`);
    });
  }

  // Helper methods for concrete collectors
  protected createBaseResult(monitorId: string, targetHost: string): Partial<MonitorResult> {
    return {
      monitorId,
      monitorName: monitorId, // Use monitorId as monitorName for display
      targetHost,
      monitorType: this.getMonitorType(),
      executedAt: new Date(),
      agentId: this.getAgentIdentifier(),
      agentRegion: this.getDatacenterIdentifier()
    };
  }

  private getAgentIdentifier(): string {
    const configManager = ConfigManager.getInstance();
    return `${configManager.getAgentName()}@${configManager.getAgentHost()}`;
  }

  private getDatacenterIdentifier(): string {
    const configManager = ConfigManager.getInstance();
    return configManager.getDatacenter();
  }

  protected addTiming(result: Partial<MonitorResult>, startTime: number): void {
    result.responseTime = Date.now() - startTime;
  }

  protected markSuccess(result: Partial<MonitorResult>, additionalData?: Partial<MonitorResult>): MonitorResult {
    return {
      ...result,
      ...additionalData,
      success: true
    } as MonitorResult;
  }

  protected markFailure(result: Partial<MonitorResult>, error: string, errorType?: string): MonitorResult {
    return {
      ...result,
      success: false,
      errorMessage: error,
      errorType: errorType || 'UNKNOWN'
    } as MonitorResult;
  }
}