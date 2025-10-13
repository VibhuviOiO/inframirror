// Core type definitions for Infragent monitoring agent

export enum MonitorType {
  HTTP = 'HTTP',
  HTTPS = 'HTTPS',
  TCP = 'TCP', 
  UDP = 'UDP',
  PING = 'PING',
  DNS = 'DNS'
}

export interface MonitorResult {
  // Monitor Identity
  monitorId: string;
  monitorName?: string;
  monitorType: MonitorType;
  
  // Target Information  
  targetHost: string;
  targetPort?: number;
  targetPath?: string;
  
  // Execution Context
  executedAt: Date;
  agentId?: string;
  agentRegion?: string;
  
  // Result Data
  success: boolean;
  responseTime?: number;        // milliseconds
  responseSizeBytes?: number;
  
  // HTTP-specific
  httpMethod?: string;
  expectedStatusCode?: number;
  responseStatusCode?: number;
  responseContentType?: string;
  responseServer?: string;
  responseCacheStatus?: string;
  
  // Network Performance
  dnsLookupMs?: number;
  tcpConnectMs?: number;
  tlsHandshakeMs?: number;
  timeToFirstByteMs?: number;
  
  // Performance Thresholds
  warningThresholdMs?: number;
  criticalThresholdMs?: number;
  
  // PING-specific
  packetLoss?: number;
  jitterMs?: number;
  
  // DNS-specific
  dnsQueryType?: string;
  dnsExpectedResponse?: string;
  dnsResponseValue?: string;
  
  // Error Information
  errorMessage?: string;
  errorType?: string;
  
  // Raw Data (conditional)
  rawResponseHeaders?: Record<string, any>;
  rawResponseBody?: string;
  rawRequestHeaders?: Record<string, any>;
  rawNetworkData?: Record<string, any>;
}

// Configuration Types
export interface AgentConfig {
  agent: {
    name: string;
    datacenter?: string;
    host?: string;
    version?: string;
    hostname?: string;
    region?: string;
    environment?: string;
    interval?: number;
    timeout?: number;
    performance?: {
      max_memory_mb: number;
      max_cpu_percent: number;
      worker_pool_size: number;
      concurrent_collectors: number;
    };
    buffering?: {
      max_buffer_size: number;
      flush_interval_seconds: number;
      batch_size: number;
    };
    scheduler?: {
      default_interval_seconds: number;
      max_jitter_seconds: number;
      retry_attempts: number;
      retry_backoff: 'linear' | 'exponential';
    };
    health: {
      enabled: boolean;
      port: number;
      endpoints: Record<string, string>;
    };
  };
  monitors?: {
    configFile: string;
  };
  storage: StorageConfig;
  logging?: LoggingConfig;
  features?: {
    hot_reload: boolean;
    self_monitoring: boolean;
    prometheus_metrics: boolean;
  };
}

export interface StorageConfig {
  postgresql?: {
    enabled: boolean;
    connection: {
      host: string;
      port: number;
      database: string;
      username: string;
      password: string;
      ssl: boolean;
    };
    pool: {
      min_connections: number;
      max_connections: number;
      idle_timeout_ms: number;
      connection_timeout_ms: number;
    };
    batching: {
      enabled: boolean;
      batch_size: number;
      flush_interval_ms: number;
      max_retries: number;
    };
  };
  file?: {
    enabled: boolean;
    path: string;
    rotation: {
      max_size_mb: number;
      max_files: number;
      compress: boolean;
    };
  };
}

export interface LoggingConfig {
  level: 'debug' | 'info' | 'warn' | 'error';
  format: 'json' | 'text';
  output: 'stdout' | 'file' | 'both';
  file?: {
    path: string;
    max_size_mb: number;
    max_files: number;
  };
}

// Monitor Configuration Types
export interface MonitorConfig {
  http?: HttpMonitorConfig;
  tcp?: TcpMonitorConfig;
  udp?: UdpMonitorConfig;
  ping?: PingMonitorConfig;
  dns?: DnsMonitorConfig;
  global?: GlobalMonitorConfig;
}

export interface HttpMonitorConfig {
  enabled: boolean;
  defaults: {
    interval_seconds: number;
    timeout_seconds: number;
    retries: number;
    follow_redirects: boolean;
    verify_ssl: boolean;
    user_agent: string;
  };
  targets?: HttpTarget[];  // For backward compatibility
  groups?: HttpGroup[];    // New grouped structure
}

export interface HttpGroup {
  name: string;
  description?: string;
  baseUrl?: string;
  enabled?: boolean;
  defaults?: {
    interval_seconds?: number;
    timeout_seconds?: number;
    retries?: number;
    headers?: Record<string, string>;
  };
  monitors: HttpTarget[];
}

export interface HttpTarget {
  name: string;
  url: string;  // Can be full URL or relative path (when used in groups)
  method: string;
  expected_status?: number[];  // Optional - when not provided, records actual response codes
  headers?: Record<string, string>;
  body?: string;
  timeout_seconds?: number;
  interval_seconds?: number;
  retryCount?: number;  // For RR compatibility
  include_response_body?: boolean;  // Monitor-specific response body inclusion
  thresholds?: ResponseTimeThresholds;  // Performance thresholds
}

export interface ResponseTimeThresholds {
  warning: number;   // Warning threshold in milliseconds
  critical: number;  // Critical threshold in milliseconds
}

export interface TcpMonitorConfig {
  enabled: boolean;
  defaults: {
    interval_seconds: number;
    timeout_seconds: number;
    retries: number;
  };
  targets: TcpTarget[];
}

export interface TcpTarget {
  name: string;
  host: string;
  port: number;
  timeout_seconds?: number;
  interval_seconds?: number;
}

export interface UdpMonitorConfig {
  enabled: boolean;
  defaults: {
    interval_seconds: number;
    timeout_seconds: number;
    retries: number;
  };
  targets: UdpTarget[];
}

export interface UdpTarget {
  name: string;
  host: string;
  port: number;
  test_query?: string;
  timeout_seconds?: number;
  interval_seconds?: number;
}

export interface PingMonitorConfig {
  enabled: boolean;
  defaults: {
    interval_seconds: number;
    timeout_seconds: number;
    packet_count: number;
    packet_size: number;
  };
  targets: PingTarget[];
}

export interface PingTarget {
  name: string;
  host: string;
  packet_count?: number;
  timeout_seconds?: number;
  interval_seconds?: number;
}

export interface DnsMonitorConfig {
  enabled: boolean;
  defaults: {
    interval_seconds: number;
    timeout_seconds: number;
    retries: number;
  };
  targets: DnsTarget[];
}

export interface DnsTarget {
  name: string;
  domain: string;
  record_type: string;
  expected_ips?: string[];
  expected_cname?: string;
  nameserver?: string;
  timeout_seconds?: number;
  interval_seconds?: number;
  thresholds?: {
    warning: number;
    critical: number;
  };
}

export interface GlobalMonitorConfig {
  jitter_enabled: boolean;
  jitter_max_seconds: number;
  failure_threshold: number;
  recovery_threshold: number;
  alerting: {
    enabled: boolean;
  };
  tags: Record<string, string>;
  response_time_thresholds?: ResponseTimeThresholds;  // Global default thresholds
}

// Base interfaces for extensibility
export interface BaseCollector {
  name: string;
  enabled: boolean;
  collect(): Promise<MonitorResult[]>;
  start(): Promise<void>;
  stop(): Promise<void>;
}

export interface StorageAdapter {
  name: string;
  connect(): Promise<void>;
  disconnect(): Promise<void>;
  store(results: MonitorResult[]): Promise<void>;
  healthCheck(): Promise<boolean>;
}

// Performance and Health Types
export interface AgentHealth {
  status: 'healthy' | 'degraded' | 'unhealthy';
  uptime: number;
  memory_usage_mb: number;
  cpu_usage_percent: number;
  active_collectors: number;
  storage_status: Record<string, boolean>;
  last_flush: Date;
  metrics_buffer_size: number;
}

export interface CollectorStats {
  collector_name: string;
  enabled: boolean;
  last_run: Date;
  success_count: number;
  failure_count: number;
  avg_response_time_ms: number;
  active_monitors: number;
}