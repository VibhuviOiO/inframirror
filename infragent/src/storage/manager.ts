// Storage manager - handles multiple storage adapters with buffering
import type { MonitorResult, StorageConfig } from '../types/index.js';

export class StorageManager {
  private adapters: Map<string, any> = new Map();
  private buffer: MonitorResult[] = [];
  private flushTimer?: NodeJS.Timeout;
  private lastFlushTime: Date = new Date();

  constructor(
    private config: StorageConfig,
    private logger: any
  ) {}

  async initialize(): Promise<void> {
    this.logger.info('💾 Initializing storage adapters...');

    // Initialize PostgreSQL adapter if enabled
    if (this.config.postgresql?.enabled) {
      try {
        const { PostgreSQLAdapter } = await import('./postgresql-adapter.js');
        const pgAdapter = new PostgreSQLAdapter(this.config.postgresql, this.logger);
        await pgAdapter.connect();
        this.adapters.set('postgresql', pgAdapter);
        this.logger.info('✅ PostgreSQL adapter initialized');
      } catch (error) {
        this.logger.error('❌ Failed to initialize PostgreSQL adapter', { 
          error: error instanceof Error ? error.message : String(error) 
        });
        throw error;
      }
    }

    // TODO: Add other storage adapters (File, Elasticsearch, etc.)

    if (this.adapters.size === 0) {
      throw new Error('No storage adapters were successfully initialized');
    }

    // Start flush timer
    this.startFlushTimer();
    this.logger.info(`💾 Storage manager initialized with ${this.adapters.size} adapters`);
  }

  async store(results: MonitorResult[]): Promise<void> {
    if (results.length === 0) {
      return;
    }

    // Add to buffer
    this.buffer.push(...results);
    this.logger.debug(`📊 Added ${results.length} results to buffer (total: ${this.buffer.length})`);

    // Check if we should flush immediately
    const maxBufferSize = 100; // This should come from config
    if (this.buffer.length >= maxBufferSize) {
      await this.flush();
    }
  }

  async flush(): Promise<void> {
    if (this.buffer.length === 0) {
      return;
    }

    const resultsToFlush = [...this.buffer];
    this.buffer = [];

    this.logger.info(`💾 Flushing ${resultsToFlush.length} results to storage...`);

    const promises = Array.from(this.adapters.entries()).map(async ([name, adapter]) => {
      try {
        const startTime = Date.now();
        await adapter.store(resultsToFlush);
        const duration = Date.now() - startTime;
        
        this.logger.debug(`✅ ${name} stored ${resultsToFlush.length} results in ${duration}ms`);
        return { adapter: name, success: true, duration };
      } catch (error) {
        this.logger.error(`❌ ${name} storage failed`, { 
          error: error instanceof Error ? error.message : String(error),
          resultCount: resultsToFlush.length 
        });
        return { adapter: name, success: false, error };
      }
    });

    const results = await Promise.allSettled(promises);
    const successful = results.filter(r => r.status === 'fulfilled').length;
    
    this.lastFlushTime = new Date();
    
    this.logger.info(`💾 Flush completed: ${successful}/${this.adapters.size} adapters successful`);
  }

  private startFlushTimer(): void {
    const flushInterval = 10000; // 10 seconds - should come from config
    
    this.flushTimer = setInterval(async () => {
      try {
        await this.flush();
      } catch (error) {
        this.logger.error('❌ Scheduled flush failed', { 
          error: error instanceof Error ? error.message : String(error) 
        });
      }
    }, flushInterval);
  }

  async shutdown(): Promise<void> {
    this.logger.info('🔄 Shutting down storage manager...');

    // Stop flush timer
    if (this.flushTimer) {
      clearInterval(this.flushTimer);
      this.flushTimer = undefined;
    }

    // Flush any remaining data
    await this.flush();

    // Disconnect all adapters
    for (const [name, adapter] of this.adapters.entries()) {
      try {
        await adapter.disconnect();
        this.logger.info(`✅ ${name} adapter disconnected`);
      } catch (error) {
        this.logger.error(`❌ Error disconnecting ${name} adapter`, { 
          error: error instanceof Error ? error.message : String(error) 
        });
      }
    }

    this.adapters.clear();
    this.logger.info('✅ Storage manager shut down');
  }

  // Public API for health checks and stats
  getActiveAdapters(): string[] {
    return Array.from(this.adapters.keys());
  }

  getBufferSize(): number {
    return this.buffer.length;
  }

  getLastFlushTime(): Date {
    return this.lastFlushTime;
  }

  getHealthStatus(): Record<string, boolean> {
    const status: Record<string, boolean> = {};
    
    for (const [name, adapter] of this.adapters.entries()) {
      try {
        // Use synchronous healthCheck for quick status
        status[name] = adapter.healthCheck ? adapter.healthCheck() : true;
      } catch {
        status[name] = false;
      }
    }
    
    return status;
  }

  // Async health check for deeper database connectivity testing
  async getAsyncHealthStatus(): Promise<Record<string, boolean>> {
    const status: Record<string, boolean> = {};
    
    for (const [name, adapter] of this.adapters.entries()) {
      try {
        if (adapter.isHealthy) {
          status[name] = await adapter.isHealthy();
        } else if (adapter.healthCheck) {
          status[name] = adapter.healthCheck();
        } else {
          status[name] = true;
        }
      } catch (error) {
        this.logger.error(`Health check failed for ${name}`, { 
          error: error instanceof Error ? error.message : String(error) 
        });
        status[name] = false;
      }
    }
    
    return status;
  }

  getStatus() {
    return {
      adapters: this.getActiveAdapters(),
      buffer_size: this.getBufferSize(),
      last_flush: this.getLastFlushTime(),
      health: this.getHealthStatus()
    };
  }
}