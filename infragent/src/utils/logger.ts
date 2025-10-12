// Structured logging utility for Infragent
import pino from 'pino';

export class Logger {
  private static instance: Logger;
  private logger: pino.Logger;

  constructor() {
    this.logger = pino({
      name: 'infragent',
      level: process.env.LOG_LEVEL || 'info',
      timestamp: pino.stdTimeFunctions.isoTime,
    });
  }

  static getInstance(): Logger {
    if (!Logger.instance) {
      Logger.instance = new Logger();
    }
    return Logger.instance;
  }

  info(message: string, meta?: any) {
    this.logger.info(meta || {}, message);
  }

  warn(message: string, meta?: any) {
    this.logger.warn(meta || {}, message);
  }

  error(message: string, meta?: any) {
    this.logger.error(meta || {}, message);
  }

  debug(message: string, meta?: any) {
    this.logger.debug(meta || {}, message);
  }

  child(meta: any) {
    return this.logger.child(meta);
  }

  // Performance logging helpers
  logCollectorStart(collectorName: string, targetCount: number) {
    this.debug('🔄 Collector starting', {
      collector: collectorName,
      targets: targetCount
    });
  }

  logCollectorEnd(collectorName: string, duration: number, results: { success: number, failed: number }) {
    this.info('✅ Collector completed', {
      collector: collectorName,
      duration_ms: duration,
      success_count: results.success,
      failed_count: results.failed,
      total: results.success + results.failed
    });
  }

  logStorageOperation(adapter: string, operation: string, count: number, duration: number) {
    this.info('💾 Storage operation', {
      adapter,
      operation,
      count,
      duration_ms: duration
    });
  }

  logMonitorResult(result: { monitorId: string, success: boolean, responseTime?: number }) {
    const level = result.success ? 'info' : 'warn';
    const message = result.success ? 'Monitor successful' : 'Monitor failed';
    
    this[level](message, {
      monitorId: result.monitorId,
      success: result.success,
      responseTime: result.responseTime
    });
  }
}