// Debug configuration transformation
const { readFileSync } = require('fs');
const YAML = require('yaml');

// Read the actual YAML file
const yamlContent = readFileSync('/Users/balu/OiO/Projects/infra-mirror/docker/config/monitors-us-east-1.yml', 'utf8');
const config = YAML.parse(yamlContent);

console.log('=== Original YAML Configuration ===');
console.log('serviceGroups:', JSON.stringify(config.serviceGroups, null, 2));

// Simulate the configuration manager's conversion logic
const infragentConfig = {
  global: {
    ...config.global,
    include_response_body: config.global?.include_response_body || false,
    response_time_thresholds: config.global?.response_time_thresholds
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
            .filter((m) => ['HTTP', 'HTTPS'].includes(m.type?.toUpperCase()))
            .map((m) => ({
              name: m.name,
              url: m.url,
              method: m.method || 'GET',
              headers: m.headers || {},
              timeout_seconds: m.timeout,
              interval_seconds: m.interval,
              retryCount: m.retryCount,
              include_response_body: m.include_response_body,
              thresholds: m.thresholds
            }))
        };
        if (httpGroup.monitors.length > 0) {
          infragentConfig.http.groups.push(httpGroup);
        }
      }
    }
  }
}

// Handle direct monitors array (outside of groups)
if (config.monitors && Array.isArray(config.monitors)) {
  for (const monitor of config.monitors) {
    const type = (monitor.type || 'HTTP').toUpperCase();
    if (type === 'DNS') {
      infragentConfig.dns.targets.push({
        name: monitor.name,
        domain: monitor.host,
        record_type: monitor.recordType || 'A',
        timeout_seconds: monitor.timeout,
        interval_seconds: monitor.interval,
        thresholds: monitor.thresholds
      });
    }
  }
}

if (infragentConfig.http.groups.length > 0) infragentConfig.http.enabled = true;
if (infragentConfig.dns.targets.length > 0) infragentConfig.dns.enabled = true;

console.log('\n=== Converted Infragent Configuration ===');
console.log('HTTP Groups:');
for (const group of infragentConfig.http.groups) {
  console.log(`Group: ${group.name}`);
  for (const monitor of group.monitors) {
    console.log(`  Monitor: ${monitor.name}`);
    console.log(`    URL: ${monitor.url}`);
    console.log(`    Thresholds: ${JSON.stringify(monitor.thresholds)}`);
  }
}

console.log('\nDNS Targets:');
for (const target of infragentConfig.dns.targets) {
  console.log(`  Target: ${target.name}`);
  console.log(`    Domain: ${target.domain}`);
  console.log(`    Thresholds: ${JSON.stringify(target.thresholds)}`);
}

console.log('\nGlobal Config:');
console.log('  Response Time Thresholds:', JSON.stringify(infragentConfig.global.response_time_thresholds));