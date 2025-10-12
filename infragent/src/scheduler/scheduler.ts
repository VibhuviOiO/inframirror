// Simple scheduler for managing collector execution
import type { BaseCollector } from '../types/index.js';

export class Scheduler {
  private scheduledCollectors: Map<string, NodeJS.Timeout> = new Map();
  private isRunning: boolean = false;

  constructor(
    private collectorRegistry: any,
    private config: any,
    private logger: any
  ) {}

  scheduleCollector(collector: BaseCollector): void {
    if (!collector.enabled) {
      return;
    }

    // For now, collectors manage their own scheduling
    // This class can be expanded later for more sophisticated scheduling
    this.logger.debug(`📅 Collector ${collector.name} is self-scheduling`);
  }

  async start(): Promise<void> {
    this.isRunning = true;
    this.logger.info('📅 Scheduler started');
  }

  async stop(): Promise<void> {
    // Clear all scheduled tasks
    for (const [name, timer] of this.scheduledCollectors.entries()) {
      clearInterval(timer);
      this.logger.debug(`📅 Stopped scheduling for ${name}`);
    }
    
    this.scheduledCollectors.clear();
    this.isRunning = false;
    this.logger.info('📅 Scheduler stopped');
  }

  getStatus() {
    return {
      running: this.isRunning,
      scheduled_collectors: this.scheduledCollectors.size
    };
  }
}