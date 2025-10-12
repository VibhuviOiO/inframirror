// Main Infragent orchestrator
import { ConfigManager } from '../config/config-manager.js';
import { Logger } from '../utils/logger.js';
import { CollectorRegistry } from '../collectors/registry.js';
import { StorageManager } from '../storage/manager.js';
import { Scheduler } from '../scheduler/scheduler.js';
import { HealthServer } from '../health/health-server.js';
import type { BaseCollector, AgentHealth } from '../types/index.js';

export class Infragent {
  private config: ConfigManager;
  private logger: Logger;
  private collectorRegistry!: CollectorRegistry;
  private storageManager!: StorageManager;
  private scheduler!: Scheduler;
  private healthServer!: HealthServer;
  private startTime: Date;
  private isRunning: boolean = false;

  constructor() {
    this.config = ConfigManager.getInstance();
    this.logger = Logger.getInstance();
    this.startTime = new Date();
  }

  async start(): Promise<void> {
    try {
      this.logger.info('🚀 Initializing Infragent agent...');

      // Initialize storage manager
      this.storageManager = new StorageManager(
        this.config.getStorageConfig(),
        this.logger
      );
      await this.storageManager.initialize();

      // Initialize collector registry
      this.collectorRegistry = new CollectorRegistry(
        this.config.getMonitorConfig(),
        this.storageManager,
        this.logger
      );
      await this.collectorRegistry.initialize();

      // Initialize scheduler
      this.scheduler = new Scheduler(
        this.collectorRegistry,
        this.config.getSchedulerConfig(),
        this.logger
      );

      // Initialize health server
      if (this.config.getHealthConfig().enabled) {
        const healthConfig = this.config.getHealthConfig();
        this.healthServer = new HealthServer(
          {
            port: healthConfig.port,
            endpoints: {
              health: healthConfig.endpoints.health || '/health',
              status: healthConfig.endpoints.status || '/status',
              metrics: healthConfig.endpoints.metrics || '/metrics'
            }
          },
          this.storageManager,
          this.collectorRegistry?.getAllCollectors() || [],
          '1.0.0',
          this.logger
        );
        await this.healthServer.start();
      }

      // Start all collectors
      const collectors = this.collectorRegistry.getAllCollectors();
      for (const collector of collectors) {
        if (collector.enabled) {
          await collector.start();
          this.scheduler.scheduleCollector(collector);
          this.logger.info(`✅ Started collector: ${collector.name}`);
        }
      }

      this.isRunning = true;
      this.logger.info('🎉 Infragent agent started successfully', {
        collectors: collectors.filter(c => c.enabled).length,
        storage_adapters: this.storageManager.getActiveAdapters().length,
        health_server: this.config.getHealthConfig().enabled
      });

    } catch (error) {
      this.logger.error('❌ Failed to start Infragent agent', { 
        error: error instanceof Error ? error.message : String(error) 
      });
      throw error;
    }
  }

  async stop(): Promise<void> {
    if (!this.isRunning) {
      return;
    }

    try {
      this.logger.info('🔄 Stopping Infragent agent...');

      // Stop scheduler first
      if (this.scheduler) {
        await this.scheduler.stop();
        this.logger.info('✅ Scheduler stopped');
      }

      // Stop all collectors
      if (this.collectorRegistry) {
        const collectors = this.collectorRegistry.getAllCollectors();
        for (const collector of collectors) {
          await collector.stop();
          this.logger.info(`✅ Stopped collector: ${collector.name}`);
        }
      }

      // Flush and stop storage
      if (this.storageManager) {
        await this.storageManager.flush();
        await this.storageManager.shutdown();
        this.logger.info('✅ Storage manager stopped');
      }

      // Stop health server
      if (this.healthServer) {
        await this.healthServer.stop();
        this.logger.info('✅ Health server stopped');
      }

      this.isRunning = false;
      this.logger.info('✅ Infragent agent stopped gracefully');

    } catch (error) {
      this.logger.error('❌ Error during shutdown', { 
        error: error instanceof Error ? error.message : String(error) 
      });
      throw error;
    }
  }

  private getAgentHealth(): AgentHealth {
    const memUsage = process.memoryUsage();
    const cpuUsage = process.cpuUsage();
    
    return {
      status: this.isRunning ? 'healthy' : 'unhealthy',
      uptime: Date.now() - this.startTime.getTime(),
      memory_usage_mb: Math.round(memUsage.heapUsed / 1024 / 1024),
      cpu_usage_percent: this.calculateCpuUsage(cpuUsage),
      active_collectors: this.collectorRegistry ? 
        this.collectorRegistry.getAllCollectors().filter(c => c.enabled).length : 0,
      storage_status: this.storageManager ? 
        this.storageManager.getHealthStatus() : {},
      last_flush: this.storageManager ? 
        this.storageManager.getLastFlushTime() : new Date(),
      metrics_buffer_size: this.storageManager ? 
        this.storageManager.getBufferSize() : 0
    };
  }

  private calculateCpuUsage(cpuUsage: NodeJS.CpuUsage): number {
    // Simple CPU usage calculation - can be improved
    const totalUsage = cpuUsage.user + cpuUsage.system;
    return Math.round((totalUsage / 1000000) * 100) / 100; // Convert to percentage
  }

  // Public API for health checks and status
  public getStatus() {
    return {
      running: this.isRunning,
      startTime: this.startTime,
      collectors: this.collectorRegistry?.getCollectorStats() || [],
      storage: this.storageManager?.getStatus() || {},
      health: this.getAgentHealth()
    };
  }

  public async reloadConfig(): Promise<void> {
    this.logger.info('🔄 Reloading configuration...');
    
    try {
      await this.config.reloadConfigurations();
      
      // Restart collectors with new configuration
      await this.collectorRegistry.reload(this.config.getMonitorConfig());
      
      this.logger.info('✅ Configuration reloaded successfully');
    } catch (error) {
      this.logger.error('❌ Failed to reload configuration', { 
        error: error instanceof Error ? error.message : String(error) 
      });
      throw error;
    }
  }
}