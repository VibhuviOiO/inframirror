// PostgreSQL storage adapter for Infragent
import pg from 'pg';
import type { MonitorResult } from '../types/index.js';

const { Pool } = pg;

export class PostgreSQLAdapter {
  private pool: pg.Pool;
  private connected: boolean = false;

  constructor(
    private config: any,
    private logger: any
  ) {
    this.pool = new Pool({
      host: this.config.connection.host,
      port: this.config.connection.port,
      database: this.config.connection.database,
      user: this.config.connection.username,
      password: this.config.connection.password,
      ssl: this.config.connection.ssl,
      min: this.config.pool.min_connections || 2,
      max: this.config.pool.max_connections || 10,
      idleTimeoutMillis: this.config.pool.idle_timeout_ms || 30000,
      connectionTimeoutMillis: this.config.pool.connection_timeout_ms || 5000
    });
  }

  async connect(): Promise<void> {
    try {
      // Test connection
      const client = await this.pool.connect();
      await client.query('SELECT 1');
      client.release();
      
      this.connected = true;
      this.logger.info('✅ PostgreSQL connected successfully');
    } catch (error) {
      this.logger.error('❌ PostgreSQL connection failed', { 
        error: error instanceof Error ? error.message : String(error) 
      });
      throw error;
    }
  }

  async disconnect(): Promise<void> {
    if (this.pool) {
      await this.pool.end();
      this.connected = false;
      this.logger.info('✅ PostgreSQL disconnected');
    }
  }

  async store(results: MonitorResult[]): Promise<void> {
    if (!this.connected || results.length === 0) {
      return;
    }

    const client = await this.pool.connect();
    
    try {
      await client.query('BEGIN');

      const insertQuery = `
        INSERT INTO "monitors" (
          "monitorId", "monitorName", "monitorType", "targetHost", "targetPort", "targetPath",
          "httpMethod", "expectedStatusCode", "dnsQueryType", "dnsExpectedResponse",
          "executedAt", "agentId", "agentRegion", "success", "responseTime", "responseSizeBytes",
          "responseStatusCode", "responseContentType", "responseServer", "responseCacheStatus",
          "dnsLookupMs", "tcpConnectMs", "tlsHandshakeMs", "timeToFirstByteMs",
          "packetLoss", "jitterMs", "dnsResponseValue", "errorMessage", "errorType",
          "rawResponseHeaders", "rawResponseBody", "rawRequestHeaders", "rawNetworkData"
        ) VALUES (
          $1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, $14, $15, $16,
          $17, $18, $19, $20, $21, $22, $23, $24, $25, $26, $27, $28, $29, $30, $31, $32, $33
        )
      `;

      for (const result of results) {
        const values = [
          result.monitorId,
          result.monitorName || null,
          result.monitorType,
          result.targetHost,
          result.targetPort || null,
          result.targetPath || null,
          result.httpMethod || null,
          result.expectedStatusCode || null,
          result.dnsQueryType || null,
          result.dnsExpectedResponse || null,
          result.executedAt,
          result.agentId || null,
          result.agentRegion || null,
          result.success,
          result.responseTime || null,
          result.responseSizeBytes || null,
          result.responseStatusCode || null,
          result.responseContentType || null,
          result.responseServer || null,
          result.responseCacheStatus || null,
          result.dnsLookupMs || null,
          result.tcpConnectMs || null,
          result.tlsHandshakeMs || null,
          result.timeToFirstByteMs || null,
          result.packetLoss || null,
          result.jitterMs || null,
          result.dnsResponseValue || null,
          result.errorMessage || null,
          result.errorType || null,
          result.rawResponseHeaders ? JSON.stringify(result.rawResponseHeaders) : null,
          result.rawResponseBody || null,
          result.rawRequestHeaders ? JSON.stringify(result.rawRequestHeaders) : null,
          result.rawNetworkData ? JSON.stringify(result.rawNetworkData) : null
        ];

        await client.query(insertQuery, values);
      }

      await client.query('COMMIT');
      
    } catch (error) {
      await client.query('ROLLBACK');
      throw error;
    } finally {
      client.release();
    }
  }

  healthCheck(): boolean {
    return this.connected;
  }

  async testConnection(): Promise<boolean> {
    try {
      const client = await this.pool.connect();
      await client.query('SELECT 1');
      client.release();
      return true;
    } catch {
      return false;
    }
  }

  // Full health check with async database connectivity test
  async isHealthy(): Promise<boolean> {
    if (!this.connected) {
      return false;
    }
    
    try {
      const client = await this.pool.connect();
      await client.query('SELECT 1');
      client.release();
      return true;
    } catch (error) {
      this.logger.error('PostgreSQL health check failed', { 
        error: error instanceof Error ? error.message : String(error) 
      });
      return false;
    }
  }
}