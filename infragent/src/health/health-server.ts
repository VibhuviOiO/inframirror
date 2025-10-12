// Health monitoring server for Infragent agent
import { createServer, IncomingMessage, ServerResponse, Server } from 'http';
import type { AgentHealth } from '../types/index.js';

interface HealthServerConfig {
  port: number;
  endpoints: {
    health: string;
    status: string;
    metrics: string;
  };
}

export class HealthServer {
  private server: Server;
  private config: HealthServerConfig;
  private isRunning = false;
  private storageManager?: any;
  private collectors?: any[];
  private version: string;
  private logger: any;

  constructor(
    config: HealthServerConfig, 
    storageManager?: any, 
    collectors?: any[], 
    version: string = '1.0.0',
    logger?: any
  ) {
    this.config = config;
    this.storageManager = storageManager;
    this.collectors = collectors;
    this.version = version;
    this.logger = logger || console;
    this.server = createServer((req, res) => this.handleRequest(req, res));
  }

  async start(): Promise<void> {
    const port = this.config.port || 8080;

    this.server = createServer((req: IncomingMessage, res: ServerResponse) => {
      this.handleRequest(req, res);
    });

    return new Promise((resolve, reject) => {
      this.server.listen(port, (err?: Error) => {
        if (err) {
          reject(err);
        } else {
          this.isRunning = true;
          this.logger.info(`🏥 Health server listening on port ${port}`);
          resolve(undefined);
        }
      });
    });
  }

  async stop(): Promise<void> {
    if (this.server) {
      return new Promise((resolve) => {
        this.server.close(() => {
          this.isRunning = false;
          this.logger.info('🏥 Health server stopped');
          resolve(undefined);
        });
      });
    }
  }

  private async handleRequest(req: IncomingMessage, res: ServerResponse): Promise<void> {
    const url = req.url || '';
    const method = req.method || 'GET';

    // Set CORS headers
    res.setHeader('Access-Control-Allow-Origin', '*');
    res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
    res.setHeader('Access-Control-Allow-Headers', 'Content-Type');

    if (method === 'OPTIONS') {
      res.writeHead(200);
      res.end();
      return;
    }

    try {
      if (url === this.config.endpoints.health || url === '/health') {
        this.handleHealthCheck(res);
      } else if (url === '/ready' || url === '/readiness') {
        await this.handleReadinessCheck(res);
      } else if (url === '/live' || url === '/liveness') {
        this.handleLivenessCheck(res);
      } else if (url === this.config.endpoints.status || url === '/status') {
        this.handleStatusCheck(res);
      } else if (url === this.config.endpoints.metrics || url === '/metrics') {
        this.handleMetrics(res);
      } else {
        this.send404(res);
      }
    } catch (error) {
      this.sendError(res, error);
    }
  }

  private handleHealthCheck(res: ServerResponse): void {
    const health = {
      status: 'ok',
      timestamp: new Date().toISOString(),
      uptime: process.uptime(),
      version: this.version
    };

    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify(health, null, 2));
  }

  private async handleReadinessCheck(res: ServerResponse): Promise<void> {
    try {
      const checks: any = {
        status: 'ready',
        timestamp: new Date().toISOString(),
        checks: {}
      };

      // Check storage health if available
      if (this.storageManager) {
        try {
          const storageHealth = await this.storageManager.getAsyncHealthStatus();
          checks.checks.storage = storageHealth;
        } catch (error) {
          checks.checks.storage = {
            status: 'error',
            error: error instanceof Error ? error.message : 'Unknown storage error'
          };
          checks.status = 'not_ready';
        }
      }

      // Check collectors if available
      if (this.collectors && this.collectors.length > 0) {
        checks.checks.collectors = {
          status: 'ready',
          count: this.collectors.length
        };
      }

      const statusCode = checks.status === 'ready' ? 200 : 503;
      res.writeHead(statusCode, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify(checks, null, 2));
    } catch (error) {
      const errorResponse = {
        status: 'not_ready',
        timestamp: new Date().toISOString(),
        error: error instanceof Error ? error.message : 'Unknown error'
      };
      res.writeHead(503, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify(errorResponse, null, 2));
    }
  }

  private handleLivenessCheck(res: ServerResponse): void {
    // Basic liveness check - if we can respond, we're alive
    const liveness = {
      status: 'alive',
      timestamp: new Date().toISOString(),
      uptime: process.uptime(),
      memory: process.memoryUsage()
    };

    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify(liveness, null, 2));
  }

  private handleStatusCheck(res: ServerResponse): void {
    const memUsage = process.memoryUsage();

    this.sendJson(res, {
      agent: {
        status: 'running',
        uptime_ms: Math.floor(process.uptime() * 1000),
        memory_usage_mb: Math.round(memUsage.heapUsed / 1024 / 1024),
        cpu_usage_percent: 0 // Would need additional monitoring for real CPU usage
      },
      collectors: {
        active: this.collectors?.length || 0
      },
      storage: {
        status: 'connected', // Would need storage manager health check
        buffer_size: 0,
        last_flush: new Date().toISOString()
      },
      timestamp: new Date().toISOString()
    });
  }

  private handleMetrics(res: ServerResponse): void {
    const memUsage = process.memoryUsage();
    const uptime = Math.floor(process.uptime());

    // Simple Prometheus-style metrics
    const metrics = [
      `# HELP infragent_uptime_seconds Agent uptime in seconds`,
      `# TYPE infragent_uptime_seconds counter`,
      `infragent_uptime_seconds ${uptime}`,
      ``,
      `# HELP infragent_memory_usage_bytes Memory usage in bytes`,
      `# TYPE infragent_memory_usage_bytes gauge`,
      `infragent_memory_usage_bytes ${memUsage.heapUsed}`,
      ``,
      `# HELP infragent_active_collectors Number of active collectors`,
      `# TYPE infragent_active_collectors gauge`,
      `infragent_active_collectors ${this.collectors?.length || 0}`,
      ``,
      `# HELP infragent_buffer_size Current metrics buffer size`,
      `# TYPE infragent_buffer_size gauge`,
      `infragent_buffer_size 0`,
      ``
    ].join('\n');

    res.writeHead(200, { 'Content-Type': 'text/plain; version=0.0.4' });
    res.end(metrics);
  }

  private sendJson(res: ServerResponse, data: any, statusCode: number = 200): void {
    res.writeHead(statusCode, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify(data, null, 2));
  }

  private send404(res: ServerResponse): void {
    res.writeHead(404, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ 
      error: 'Not Found',
      message: 'Available endpoints: /health, /status, /metrics'
    }));
  }

  private sendError(res: ServerResponse, error: unknown): void {
    this.logger.error('🏥 Health server error', { 
      error: error instanceof Error ? error.message : String(error) 
    });
    
    res.writeHead(500, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ 
      error: 'Internal Server Error',
      message: 'An error occurred processing your request'
    }));
  }
}