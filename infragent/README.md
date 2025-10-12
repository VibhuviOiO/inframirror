# Infragent - Universal Infrastructure Monitoring Agent

A lightweight, modular monitoring agent that supports multiple protocols and provides flexible configuration options for infrastructure monitoring.

## Features

- **Multi-Protocol Support**: HTTP/HTTPS, TCP, PING, DNS monitoring
- **Universal Configuration**: Auto-detects serviceGroups, generic groups, and native formats
- **Granular Control**: Monitor-level settings for response body capture, intervals, timeouts
- **Production Ready**: Docker deployment, PostgreSQL storage, health endpoints
- **Modular Architecture**: Plugin-based collectors for easy extensibility

### Docker Deployment (Recommended)

```bash
# Build the image
docker build -t infragent .

# Run with mounted configurations
docker run -d \
  --name infragent \
  -p 8081:8081 \
  -v $(pwd)/configs:/app/configs:ro \
  -v $(pwd)/logs:/app/logs \
  --network infra-mirror_default \
  infragent
```

### Local Development

```bash
# Install dependencies
npm install

# Configure environment
cp .env.example .env
# Edit .env with your database settings

# Run the agent
npm start
```

## Monitor Types & Configuration

### HTTP/HTTPS Monitors
Monitor web services, APIs, and HTTP endpoints. Supports all HTTP methods (GET, POST, PUT, DELETE, HEAD, PATCH, OPTIONS).

```yaml
- name: "api-health-check"
  url: "/api/v1/health"
  method: "GET"                    # HTTP method (default: GET)
  type: "HTTPS"                    # HTTP or HTTPS (required)
  interval: 60                     # Check interval in seconds
  timeout: 10                      # Request timeout in seconds
  retryCount: 3                    # Number of retry attempts
  include_response_body: true      # Store response body (default: false)
  headers:                         # Custom HTTP headers
    Accept: "application/json"
    Authorization: "Bearer ${API_TOKEN}"
  expectedStatusCodes: [200, 201]  # Acceptable status codes
  bodyContains: "healthy"          # Required text in response
```

### TCP Monitors
Monitor TCP port connectivity and response times for databases, message queues, and services.

```yaml
- name: "database-port"
  host: "db.example.com"          # Target hostname or IP (required)
  port: 5432                      # TCP port number (required)
  type: "TCP"                     # Must be "TCP"
  interval: 30                    # Check interval in seconds
  timeout: 5                      # Connection timeout in seconds
  retryCount: 3                   # Number of retry attempts
```

### PING Monitors  
Monitor network connectivity using ICMP ping.

```yaml
- name: "server-ping"
  host: "server.example.com"      # Target hostname or IP (required)
  type: "PING"                    # Must be "PING"
  interval: 60                    # Check interval in seconds
  timeout: 10                     # Ping timeout in seconds
  retryCount: 3                   # Number of retry attempts
  packetSize: 64                  # ICMP packet size in bytes
```

### DNS Monitors
Monitor DNS resolution and query response times.

```yaml
- name: "dns-resolution"
  host: "example.com"             # Domain name to resolve (required)
  type: "DNS"                     # Must be "DNS"
  recordType: "A"                 # A, AAAA, CNAME, MX, TXT, NS
  nameServer: "8.8.8.8"          # DNS server to query
  interval: 120                   # Check interval in seconds
  timeout: 5                      # Query timeout in seconds
  retryCount: 2                   # Number of retry attempts
  expectedIP: "93.184.216.34"    # Expected IP for validation
```

## Configuration Formats

Infragent supports multiple configuration formats with auto-detection:

### 1. Native Format (Direct monitors array)
```yaml
global:
  default_interval_seconds: 60

monitors:
  - name: "web-server"
    url: "https://example.com/health"
    type: "HTTPS"
  - name: "database"
    host: "db.example.com"
    port: 5432
    type: "TCP"
```


### 3. Generic Groups Format  
```yaml
groups:
  - name: "Web Services"
    baseUrl: "https://example.com"
    monitors:
      - name: "homepage"
        url: "/"
        type: "HTTPS"
```

## Global Settings & Advanced Configuration

### Global Configuration
```yaml
global:
  default_interval_seconds: 60        # Default check interval
  default_timeout_seconds: 30         # Default request timeout  
  default_retry_attempts: 3           # Default retry count
  include_response_body: false        # Global response body capture
  
  # Performance tuning
  max_concurrent_monitors: 50         # Concurrent execution limit
  batch_size: 100                     # Database batch insert size
  flush_interval_seconds: 30          # Database flush interval
  
  # Protocol-specific settings
  protocols:
    http:
      enabled: true                   # Enable/disable HTTP monitoring
      user_agent: "Infragent/1.0"
    tcp:
      enabled: true                   # Enable/disable TCP monitoring
    ping:
      enabled: true                   # Enable/disable PING monitoring  
    dns:
      enabled: true                   # Enable/disable DNS monitoring
```

### Response Body Capture
Control response body storage globally or per monitor:
```yaml
# Global setting (applies to all monitors)
global:
  include_response_body: true

# Monitor-specific override  
monitors:
  - name: "critical-api"
    url: "/api/critical"
    type: "HTTPS"
    include_response_body: true     # Store response for this monitor only
```

### Authentication & Headers
```yaml
monitors:
  - name: "authenticated-api"
    url: "/api/secure"
    type: "HTTPS"
    headers:
      Authorization: "Bearer ${API_TOKEN}"
      X-API-Key: "${API_KEY}"
      Content-Type: "application/json"
```

## Environment Variables

```bash
# Database Configuration
DATABASE_URL=postgresql://user:password@localhost:5432/database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=inframirror
DB_USER=inframirror
DB_PASSWORD=password

# Agent Configuration
AGENT_PORT=8081
LOG_LEVEL=info
CONFIG_PATH=/app/configs/monitors.yml

# Performance Configuration
BATCH_SIZE=100
FLUSH_INTERVAL=30
```

## Health Endpoints

Kubernetes-style health endpoints on port 8081:

- **`/health`**: Overall health status  
- **`/ready`**: Readiness probe (database connectivity)
- **`/live`**: Liveness probe (agent responsiveness)
- **`/metrics`**: Basic metrics (monitor counts, success rates)

```bash
curl http://localhost:8081/health
curl http://localhost:8081/ready
curl http://localhost:8081/metrics
```

## Configuration Examples

### Basic Infrastructure Monitoring
```yaml
global:
  default_interval_seconds: 60

monitors:
  # Web services
  - name: "website-health"
    url: "https://example.com/health"
    type: "HTTPS"
    include_response_body: true
    
  - name: "api-endpoint"
    url: "https://api.example.com/status"
    type: "HTTPS"
    headers:
      Authorization: "Bearer ${API_TOKEN}"
    
  # Database connectivity
  - name: "primary-database"
    host: "db.example.com"
    port: 5432
    type: "TCP"
    interval: 30
    
  # Network connectivity  
  - name: "server-ping"
    host: "server.example.com"
    type: "PING"
    interval: 120
    
  # DNS resolution
  - name: "domain-resolution"
    host: "example.com"
    type: "DNS"
    recordType: "A"
    interval: 600
```


## Database Schema

Monitor results stored in PostgreSQL:
```sql
CREATE TABLE monitors (
  id                SERIAL PRIMARY KEY,
  monitorName       VARCHAR(255) NOT NULL,
  monitorType       VARCHAR(50) NOT NULL,
  success           BOOLEAN NOT NULL,
  responseTime      INTEGER NOT NULL,      -- milliseconds
  errorMessage      TEXT,
  executedAt        TIMESTAMP NOT NULL,
  rawResponseBody   TEXT                   -- Optional response capture
);
```

## Best Practices

### Interval Guidelines
- **Critical services**: 30-60 seconds
- **Important services**: 60-180 seconds  
- **Background services**: 180-600 seconds
- **Infrastructure checks**: 300-1800 seconds

### Timeout Settings
- **Fast APIs**: 5-15 seconds
- **Database connections**: 5-30 seconds
- **External services**: 15-60 seconds

### Response Body Capture
Enable `include_response_body: true` only for:
- Critical endpoints requiring detailed debugging
- Services with structured health responses
- Endpoints where response validation is needed

## Troubleshooting

### Common Issues
- **Agent won't start**: Check database connectivity and configuration syntax
- **Monitors not executing**: Verify protocol enablement and monitor configuration
- **Database connection issues**: Check DATABASE_URL format and network connectivity

### Debug Mode
```bash
export LOG_LEVEL=debug
# Or in Docker: -e LOG_LEVEL=debug
```

### Log Locations
- Console output (stdout/stderr)
- Docker logs: `docker logs infragent`
- Container: `/app/logs/infragent.log`

## License

This project is licensed under the MIT License.