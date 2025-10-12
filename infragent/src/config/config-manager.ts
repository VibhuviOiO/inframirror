// Configuration manager for Infragent agent
import { readFileSync } from 'fs';
import { resolve, dirname } from 'path';
import { fileURLToPath } from 'url';
import YAML from 'yaml';
import type { AgentConfig, MonitorConfig } from '../types/index.js';

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

export class ConfigManager {
  private static instance: ConfigManager;
  private agentConfig!: AgentConfig;
  private monitorConfig!: MonitorConfig;

  private constructor() {
    this.loadConfigurations();
  }

  static getInstance(): ConfigManager {
    if (!ConfigManager.instance) {
      ConfigManager.instance = new ConfigManager();
    }
    return ConfigManager.instance;
  }

  private loadConfigurations() {
    try {
      // Load agent configuration
      const agentConfigPath = this.resolveConfigPath('agent.yml');
      const agentYaml = readFileSync(agentConfigPath, 'utf8');
      this.agentConfig = YAML.parse(this.substituteEnvVars(agentYaml));

      // Load monitor configuration - auto-detect format
      const monitorConfigPath = this.agentConfig.monitors?.configFile 
        ? this.resolveConfigPath(this.agentConfig.monitors.configFile.replace('./', ''))
        : this.resolveConfigPath('monitors.yml');
      this.monitorConfig = this.loadMonitorConfiguration(monitorConfigPath);

      this.validateConfigurations();
    } catch (error) {
      throw new Error(`Failed to load configurations: ${error instanceof Error ? error.message : String(error)}`);
    }
  }

  private loadMonitorConfiguration(configPath: string): MonitorConfig {
    const monitorYaml = readFileSync(configPath, 'utf8');
    const rawConfig = YAML.parse(this.substituteEnvVars(monitorYaml));

    // Auto-detect configuration format
    if (this.isGroupedHttpFormat(rawConfig)) {
      console.log('🔄 Detected grouped HTTP format, converting to Infragent format...');
      return this.convertGroupedFormat(rawConfig);
    }

    // Standard Infragent format
    return rawConfig as MonitorConfig;
  }

  private isGroupedHttpFormat(config: any): boolean {
    // Check if config has grouped HTTP monitoring structure
    return config && 
           typeof config === 'object' && 
           (('serviceGroups' in config && Array.isArray(config.serviceGroups)) ||
            ('groups' in config && Array.isArray(config.groups)));
  }

  private convertGroupedFormat(config: any): MonitorConfig {
    const infragentConfig: MonitorConfig = {
      global: {
        ...config.global,
        include_response_body: config.global?.include_response_body || false
      },
      http: {
        enabled: false,
        defaults: {
          interval_seconds: config.global?.default_interval_seconds || 60,
          timeout_seconds: config.global?.default_timeout_seconds || 10,
          retries: config.global?.default_retry_attempts || 2,
          follow_redirects: true,
          verify_ssl: true,
          user_agent: 'Infragent-Monitor/1.0'
        },
        groups: []
      },
      tcp: { enabled: false, defaults: { interval_seconds: 60, timeout_seconds: 10, retries: 2 }, targets: [] },
      ping: { enabled: false, defaults: { interval_seconds: 60, timeout_seconds: 10, packet_count: 3, packet_size: 64 }, targets: [] },
      dns: { enabled: false, defaults: { interval_seconds: 300, timeout_seconds: 10, retries: 2 }, targets: [] }
    };

    const serviceGroups = config.serviceGroups || config.groups || [];
    
    for (const group of serviceGroups) {
      if (group.services && Array.isArray(group.services)) {
        for (const service of group.services) {
          if (service.monitors && Array.isArray(service.monitors)) {
            const httpGroup = {
              name: `${group.name} - ${service.name}`,
              description: service.description || group.description,
              baseUrl: service.baseUrl,
              monitors: service.monitors
                .filter((m: any) => ['HTTP', 'HTTPS'].includes(m.type?.toUpperCase()))
                .map((m: any) => ({
                  name: m.name,
                  url: m.url,
                  method: m.method || 'GET',
                  headers: m.headers || {},
                  timeout_seconds: m.timeout,
                  interval_seconds: m.interval,
                  retryCount: m.retryCount,
                  include_response_body: m.include_response_body
                }))
            };
            if (httpGroup.monitors.length > 0) {
              infragentConfig.http!.groups!.push(httpGroup);
            }

            // Handle other protocol types
            for (const monitor of service.monitors) {
              const type = (monitor.type || 'HTTP').toUpperCase();
              if (type === 'PING') {
                infragentConfig.ping!.targets.push({
                  name: `${group.name} - ${service.name} - ${monitor.name}`,
                  host: monitor.url.replace(/^https?:\/\//, ''),
                  timeout_seconds: monitor.timeout,
                  interval_seconds: monitor.interval
                });
              } else if (type === 'DNS') {
                infragentConfig.dns!.targets.push({
                  name: `${group.name} - ${service.name} - ${monitor.name}`,
                  domain: monitor.url.replace(/^https?:\/\//, ''),
                  record_type: 'A',
                  timeout_seconds: monitor.timeout,
                  interval_seconds: monitor.interval
                });
              } else if (type === 'TCP') {
                const url = new URL(monitor.url.startsWith('http') ? monitor.url : `http://${monitor.url}`);
                infragentConfig.tcp!.targets.push({
                  name: `${group.name} - ${service.name} - ${monitor.name}`,
                  host: url.hostname,
                  port: parseInt(url.port) || (url.protocol === 'https:' ? 443 : 80),
                  timeout_seconds: monitor.timeout,
                  interval_seconds: monitor.interval
                });
              }
            }
          }
        }
      } else if (group.monitors && Array.isArray(group.monitors)) {
        // Direct monitors in group
        const httpMonitors = group.monitors
          .filter((m: any) => ['HTTP', 'HTTPS'].includes(m.type?.toUpperCase()))
          .map((m: any) => ({
            name: m.name,
            url: m.url,
            method: m.method || 'GET',
            headers: m.headers || {},
            timeout_seconds: m.timeout,
            interval_seconds: m.interval,
            retryCount: m.retryCount
          }));
        
        if (httpMonitors.length > 0) {
          infragentConfig.http!.groups!.push({
            name: group.name,
            description: group.description,
            baseUrl: group.baseUrl,
            monitors: httpMonitors
          });
        }
      }
    }

    // Enable protocols that have targets
    if (infragentConfig.http!.groups!.length > 0) infragentConfig.http!.enabled = true;
    if (infragentConfig.tcp!.targets.length > 0) infragentConfig.tcp!.enabled = true;
    if (infragentConfig.ping!.targets.length > 0) infragentConfig.ping!.enabled = true;
    if (infragentConfig.dns!.targets.length > 0) infragentConfig.dns!.enabled = true;

    return infragentConfig;
  }

  private resolveConfigPath(filename: string): string {
    // If it's an absolute path, use it directly
    if (filename.startsWith('/')) {
      try {
        readFileSync(filename);
        return filename;
      } catch {
        throw new Error(`Configuration file not found: ${filename}`);
      }
    }

    // Try different possible locations for relative paths
    const possiblePaths = [
      resolve(process.cwd(), 'configs', filename),
      resolve(process.cwd(), filename),
      resolve(__dirname, '../../configs', filename),
      resolve('/etc/infragent', filename)
    ];

    for (const path of possiblePaths) {
      try {
        readFileSync(path);
        return path;
      } catch {
        // Continue to next path
      }
    }

    throw new Error(`Configuration file not found: ${filename}`);
  }

  private substituteEnvVars(yamlContent: string): string {
    // Replace ${VAR} or ${VAR:-default} with environment variables
    return yamlContent.replace(/\$\{([^}]+)\}/g, (match, varExpression) => {
      const [varName, defaultValue] = varExpression.split(':-');
      return process.env[varName] || defaultValue || match;
    });
  }

  private validateConfigurations() {
    // Basic validation - can be extended
    if (!this.agentConfig?.agent?.name) {
      throw new Error('Agent name is required in configuration');
    }

    if (!this.agentConfig?.storage) {
      throw new Error('Storage configuration is required');
    }

    // Validate at least one storage adapter is enabled
    const storageEnabled = Object.values(this.agentConfig.storage)
      .some((config: any) => config?.enabled === true);
      
    if (!storageEnabled) {
      throw new Error('At least one storage adapter must be enabled');
    }

    // Validate at least one monitor type is enabled
    if (!this.monitorConfig) {
      throw new Error('Monitor configuration is required');
    }

    const monitorsEnabled = Object.values(this.monitorConfig)
      .some((config: any) => config?.enabled === true);
      
    if (!monitorsEnabled) {
      console.warn('⚠️  No monitor types are enabled');
    }
  }

  getAgentConfig(): AgentConfig {
    return this.agentConfig;
  }

  getMonitorConfig(): MonitorConfig {
    return this.monitorConfig;
  }

  // Hot reload configuration (future feature)
  async reloadConfigurations(): Promise<void> {
    try {
      this.loadConfigurations();
      console.log('✅ Configuration reloaded successfully');
    } catch (error) {
      console.error('❌ Failed to reload configuration:', error instanceof Error ? error.message : String(error));
      throw error;
    }
  }

  // Get specific configuration sections
  getStorageConfig() {
    return this.agentConfig.storage;
  }

  getPerformanceConfig() {
    return this.agentConfig.agent.performance;
  }

  getBufferingConfig() {
    return this.agentConfig.agent.buffering;
  }

  getSchedulerConfig() {
    return this.agentConfig.agent.scheduler;
  }

  getHealthConfig() {
    return this.agentConfig.agent.health;
  }

  // Monitor configuration getters
  getHttpMonitors() {
    return this.monitorConfig.http;
  }

  getTcpMonitors() {
    return this.monitorConfig.tcp;
  }

  getUdpMonitors() {
    return this.monitorConfig.udp;
  }

  getPingMonitors() {
    return this.monitorConfig.ping;
  }

  getDnsMonitors() {
    return this.monitorConfig.dns;
  }

  getGlobalConfig() {
    return this.monitorConfig.global;
  }

  // Agent identity methods for multi-datacenter support
  getAgentName(): string {
    return this.agentConfig.agent.name || 'infragent-unknown';
  }

  getDatacenter(): string {
    return this.agentConfig.agent.datacenter || 'unknown-dc';
  }

  getAgentHost(): string {
    return this.agentConfig.agent.host || 'unknown-host';
  }

  getAgentVersion(): string {
    return this.agentConfig.agent.version || '1.0.0';
  }
}