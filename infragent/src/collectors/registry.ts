// Collector registry - manages all monitoring collectors
import type { BaseCollector, MonitorConfig, StorageAdapter, MonitorResult } from '../types/index.js';
import { HttpCollector } from './http-collector.js';
import { TcpCollector } from './tcp-collector.js';
import { PingCollector } from './ping-collector.js';
import { DnsCollector } from './dns-collector.js';

export class CollectorRegistry {
  private collectors: BaseCollector[] = [];
  private storageHandler?: (results: MonitorResult[]) => Promise<void>;

  constructor(
    private config: MonitorConfig,
    private storageManager: any, // We'll define this interface later
    private logger: any
  ) {}

  async initialize(): Promise<void> {
    this.logger.info('🔧 Initializing collectors...');

    // Initialize HTTP collector
    if (this.config.http?.enabled) {
      const httpConfig = { 
        ...this.config.http,
        global: this.config.global // Pass global config to collector
      };
      const httpCollector = new HttpCollector(httpConfig);
      this.registerCollector(httpCollector);
      this.logger.info(`✅ HTTP collector initialized with ${httpCollector.getTargetCount()} targets`);
    }

    // Initialize TCP collector
    if (this.config.tcp?.enabled) {
      const tcpCollector = new TcpCollector(this.config.tcp);
      this.registerCollector(tcpCollector);
      this.logger.info(`✅ TCP collector initialized with ${tcpCollector.getTargetCount()} targets`);
    }

    // Initialize PING collector
    if (this.config.ping?.enabled) {
      const pingCollector = new PingCollector(this.config.ping);
      this.registerCollector(pingCollector);
      this.logger.info(`✅ PING collector initialized with ${pingCollector.getTargetCount()} targets`);
    }

    // Initialize DNS collector
    if (this.config.dns?.enabled) {
      const dnsCollector = new DnsCollector(this.config.dns);
      this.registerCollector(dnsCollector);
      this.logger.info(`✅ DNS collector initialized with ${dnsCollector.getTargetCount()} targets`);
    }

    // TODO: Add UDP collector

    // Set up storage handler for all collectors
    this.setupStorageHandler();

    this.logger.info(`🎯 Collector registry initialized with ${this.collectors.length} collectors`);
  }

  private registerCollector(collector: BaseCollector): void {
    this.collectors.push(collector);
    
    // Override the collector's handleResults method to route to storage
    const originalHandleResults = (collector as any).handleResults.bind(collector);
    (collector as any).handleResults = async (results: MonitorResult[]) => {
      // Call original handler first (for logging)
      await originalHandleResults(results);
      
      // Then send to storage
      if (this.storageHandler) {
        await this.storageHandler(results);
      }
    };
  }

  private setupStorageHandler(): void {
    this.storageHandler = async (results: MonitorResult[]) => {
      try {
        if (this.storageManager && results.length > 0) {
          await this.storageManager.store(results);
        }
      } catch (error) {
        this.logger.error('❌ Failed to store monitoring results', { 
          error: error instanceof Error ? error.message : String(error),
          resultCount: results.length 
        });
      }
    };
  }

  getAllCollectors(): BaseCollector[] {
    return [...this.collectors];
  }

  getEnabledCollectors(): BaseCollector[] {
    return this.collectors.filter(collector => collector.enabled);
  }

  getCollectorByName(name: string): BaseCollector | undefined {
    return this.collectors.find(collector => collector.name === name);
  }

  async reload(newConfig: MonitorConfig): Promise<void> {
    this.logger.info('🔄 Reloading collectors...');
    
    // Stop all current collectors
    for (const collector of this.collectors) {
      await collector.stop();
    }

    // Clear collectors array
    this.collectors = [];

    // Reload with new config
    this.config = newConfig;
    await this.initialize();

    // Start all new collectors
    for (const collector of this.getEnabledCollectors()) {
      await collector.start();
    }

    this.logger.info('✅ Collectors reloaded successfully');
  }

  getCollectorStats() {
    return this.collectors.map(collector => ({
      name: collector.name,
      enabled: collector.enabled,
      type: (collector as any).getMonitorType?.() || 'unknown',
      targetCount: (collector as any).getTargetCount?.() || 0
    }));
  }

  async shutdown(): Promise<void> {
    this.logger.info('🔄 Shutting down all collectors...');
    
    for (const collector of this.collectors) {
      try {
        await collector.stop();
      } catch (error) {
        this.logger.error(`❌ Error stopping collector ${collector.name}`, { 
          error: error instanceof Error ? error.message : String(error) 
        });
      }
    }

    this.collectors = [];
    this.logger.info('✅ All collectors shut down');
  }
}