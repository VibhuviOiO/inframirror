# Multi-Region API Monitoring Deployment Guide

This guide shows how to deploy Infragent across multiple datacenters to provide region-specific API monitoring that matches your client experience. Examples use publicly available APIs (GitHub, JSONPlaceholder, HTTPBin, REST Countries, CoinGecko) to demonstrate real-world monitoring scenarios.

## Architecture Overview

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   us-east-1     │    │   us-west-2     │    │   eu-west-1     │
│                 │    │                 │    │                 │
│  ┌───────────┐  │    │  ┌───────────┐  │    │  ┌───────────┐  │
│  │ Infragent │  │    │  │ Infragent │  │    │  │ Infragent │  │
│  │ east-1    │  │    │  │ west-2    │  │    │  │ eu-west-1 │  │
│  └───────────┘  │    │  └───────────┘  │    │  └───────────┘  │
│                 │    │                 │    │                 │
│  Monitors APIs  │    │  Monitors APIs  │    │  Monitors APIs  │
│  from us-east-1 │    │  from us-west-2 │    │  from eu-west-1 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
                    ┌─────────────────────┐
                    │   Central Database  │
                    │   (PostgreSQL)      │
                    │                     │
                    │ - agentId: region   │
                    │ - agentRegion: dc   │ 
                    │ - monitorResults    │
                    └─────────────────────┘
```

## Region-Specific Agent Deployments

### 1. US East 1 Region Deployment

**Docker Compose for us-east-1:**
```yaml
# docker-compose-us-east-1.yml
version: '3.8'
services:
  infragent-us-east-1:
    build: .
    container_name: infragent-us-east-1
    hostname: us-east-1-agent-01
    environment:
      # Agent Identity
      - AGENT_NAME=api-monitor-us-east-1
      - DATACENTER=us-east-1
      - AGENT_HOST=us-east-1-agent-01
      
      # Database Connection (Central)
      - DATABASE_HOST=central-db.example.com
      - DATABASE_PORT=5432
      - DATABASE_NAME=inframirror
      - DATABASE_USER=inframirror
      - DATABASE_PASSWORD=${DB_PASSWORD}
      
      # Monitoring Configuration
      - MONITORS_CONFIG=/app/configs/monitors-us-east-1.yml
      - LOG_LEVEL=info
      
    volumes:
      - ./configs/monitors-us-east-1.yml:/app/configs/monitors-us-east-1.yml:ro
      - ./configs/agent.yml:/app/configs/agent.yml:ro
      - us-east-1-logs:/app/logs
    
    ports:
      - "8081:8081"  # Health endpoints
      
    restart: unless-stopped
    
    networks:
      - monitoring-network

volumes:
  us-east-1-logs:

networks:
  monitoring-network:
    external: true
```

**US East 1 Region Monitor Configuration:**
```yaml
# configs/monitors-us-east-1.yml
global:
  default_interval_seconds: 60
  default_timeout_seconds: 30
  default_retry_attempts: 3

serviceGroups:
  - name: "API Services us-east-1"
    description: "API monitoring from us-east-1 region"
    services:
      - name: "GitHub API"
        baseUrl: "https://api.github.com"
        monitors:
          - name: "us-east-1-github-status"
            url: "/status"
            type: "HTTPS"
            interval: 30
            include_response_body: true
            headers:
              User-Agent: "Infragent-Monitor/1.0"
              
          - name: "us-east-1-github-rate-limit"
            url: "/rate_limit"
            type: "HTTPS"
            interval: 60
            expectedStatusCodes: [200, 403]
            
      - name: "JSONPlaceholder API"
        baseUrl: "https://jsonplaceholder.typicode.com"
        monitors:
          - name: "us-east-1-posts-api"
            url: "/posts/1"
            type: "HTTPS"
            interval: 120
            include_response_body: true
            
          - name: "us-east-1-users-api"
            url: "/users/1"
            type: "HTTPS" 
            interval: 180
            
      - name: "HTTPBin Testing API"
        baseUrl: "https://httpbin.org"
        monitors:
          - name: "us-east-1-httpbin-status"
            url: "/status/200"
            type: "HTTPS"
            interval: 90

# Infrastructure monitoring for us-east-1
monitors:
  - name: "us-east-1-github-ping"
    host: "github.com"
    type: "PING"
    interval: 60
    
  - name: "us-east-1-google-dns"
    host: "8.8.8.8"
    type: "PING"
    interval: 90
```

### 2. US West 2 Region Deployment

**Docker Compose for us-west-2:**
```yaml
# docker-compose-us-west-2.yml
version: '3.8'
services:
  infragent-us-west-2:
    build: .
    container_name: infragent-us-west-2
    hostname: us-west-2-agent-01
    environment:
      # Agent Identity
      - AGENT_NAME=api-monitor-us-west-2
      - DATACENTER=us-west-2
      - AGENT_HOST=us-west-2-agent-01
      
      # Database Connection (Central)
      - DATABASE_HOST=central-db.example.com
      - DATABASE_PORT=5432
      - DATABASE_NAME=inframirror
      - DATABASE_USER=inframirror
      - DATABASE_PASSWORD=${DB_PASSWORD}
      
      # Monitoring Configuration
      - MONITORS_CONFIG=/app/configs/monitors-us-west-2.yml
      
    volumes:
      - ./configs/monitors-us-west-2.yml:/app/configs/monitors-us-west-2.yml:ro
      - ./configs/agent.yml:/app/configs/agent.yml:ro
      - us-west-2-logs:/app/logs
    
    ports:
      - "8081:8081"
      
    restart: unless-stopped

volumes:
  us-west-2-logs:
```

**Virginia Region Monitor Configuration:**
```yaml
# configs/monitors-virginia.yml
global:
  default_interval_seconds: 60

serviceGroups:
  - name: "US West 2 APIs"
    description: "API monitoring from US West 2 datacenter"
    services:
      - name: "REST Countries API"
        baseUrl: "https://restcountries.com"
        monitors:
          - name: "us-west-2-countries-all"
            url: "/v3.1/all"
            type: "HTTPS"
            interval: 30
            include_response_body: true
            
          - name: "us-west-2-country-usa"
            url: "/v3.1/name/united%20states"
            type: "HTTPS"
            interval: 60
            
      - name: "CoinGecko API"
        baseUrl: "https://api.coingecko.com"  
        monitors:
          - name: "us-west-2-crypto-ping"
            url: "/api/v3/ping"
            type: "HTTPS"
            interval: 30  # More frequent for latency-sensitive API
            include_response_body: true
            
          - name: "us-west-2-bitcoin-price"
            url: "/api/v3/simple/price?ids=bitcoin&vs_currencies=usd"
            type: "HTTPS"
            interval: 90

monitors:
  - name: "us-west-2-cloudflare-dns"
    host: "1.1.1.1"
    type: "PING"
    interval: 60
```

## Deployment Scripts

### Automated Multi-Region Deployment

### Region-Specific Environment Files

**Chicago Environment (.env.chicago):**
```bash
# US East 1 DC Configuration
AGENT_NAME=api-monitor-us-east-1
DATACENTER=us-east-1
AGENT_HOST=us-east-1-agent-01

# Regional API Endpoints
API_BASE_GITHUB=https://api.github.com
API_BASE_JSONPLACEHOLDER=https://jsonplaceholder.typicode.com
API_BASE_HTTPBIN=https://httpbin.org

# Database (Central)
DATABASE_HOST=central-db.example.com
DATABASE_PORT=5432
DATABASE_NAME=inframirror
DATABASE_USER=inframirror
DATABASE_PASSWORD=${DB_PASSWORD}

# Monitoring
MONITORS_CONFIG=/app/configs/monitors-us-east-1.yml
LOG_LEVEL=info
```

**Virginia Environment (.env.virginia):**
```bash
# US West 2 DC Configuration  
AGENT_NAME=api-monitor-us-west-2
DATACENTER=us-west-2
AGENT_HOST=us-west-2-agent-01

# Regional API Endpoints
API_BASE_RESTCOUNTRIES=https://restcountries.com
API_BASE_COINGECKO=https://api.coingecko.com
API_BASE_REQRES=https://reqres.in

# Database (Central)
DATABASE_HOST=central-db.example.com
DATABASE_PORT=5432
DATABASE_NAME=inframirror
DATABASE_USER=inframirror
DATABASE_PASSWORD=${DB_PASSWORD}

# Monitoring
MONITORS_CONFIG=/app/configs/monitors-us-west-2.yml
LOG_LEVEL=info
```

## Database Queries for Multi-Region Analysis

### Client Issue Correlation Queries

**US East 1 Client API Issue Analysis:**
```sql
-- When us-east-1 client reports API issues
SELECT 
    "monitorName",
    "agentRegion", 
    "success",
    AVG("responseTime") as avg_response_time,
    COUNT(*) as total_checks,
    SUM(CASE WHEN NOT "success" THEN 1 ELSE 0 END) as failed_checks,
    ROUND(AVG(CASE WHEN "success" THEN "responseTime" END), 2) as success_avg_time
FROM monitors 
WHERE 
    "agentRegion" = 'us-east-1'
    AND "monitorName" LIKE '%api%'
    AND "executedAt" > NOW() - INTERVAL '1 hour'
GROUP BY "monitorName", "agentRegion", "success"
ORDER BY failed_checks DESC, avg_response_time DESC;
```

**US West 2 Client API Latency Analysis:**
```sql
-- When us-west-2 client reports API latencies
SELECT 
    "monitorName",
    "agentRegion",
    DATE_TRUNC('minute', "executedAt") as minute_bucket,
    AVG("responseTime") as avg_latency,
    MIN("responseTime") as min_latency,
    MAX("responseTime") as max_latency,
    PERCENTILE_CONT(0.95) WITHIN GROUP (ORDER BY "responseTime") as p95_latency
FROM monitors 
WHERE 
    "agentRegion" = 'us-west-2'
    AND "monitorName" LIKE '%api%'
    AND "executedAt" > NOW() - INTERVAL '4 hours'
    AND "success" = true
GROUP BY "monitorName", "agentRegion", minute_bucket
ORDER BY minute_bucket DESC, avg_latency DESC;
```

**Cross-Region Performance Comparison:**
```sql
-- Compare API performance across regions
SELECT 
    "agentRegion",
    "monitorName",
    AVG("responseTime") as avg_response_time,
    SUM(CASE WHEN NOT "success" THEN 1 ELSE 0 END)::float / COUNT(*) * 100 as error_rate,
    COUNT(*) as total_checks
FROM monitors 
WHERE 
    "monitorName" IN ('github-status', 'posts-api', 'httpbin-status')
    AND "executedAt" > NOW() - INTERVAL '24 hours'
GROUP BY "agentRegion", "monitorName"
ORDER BY "monitorName", avg_response_time DESC;
```

## API Metrics Dashboard Queries

### Region-Specific API Status

**Current API Status by Region:**
```sql
-- Real-time API status for customer support
SELECT 
    m."agentRegion" as region,
    m."monitorName" as api_name,
    m."success",
    m."responseTime",
    m."executedAt",
    CASE 
        WHEN m."responseTime" < 100 THEN 'Excellent'
        WHEN m."responseTime" < 500 THEN 'Good' 
        WHEN m."responseTime" < 1000 THEN 'Fair'
        ELSE 'Poor'
    END as performance_rating
FROM monitors m
INNER JOIN (
    SELECT "agentRegion", "monitorName", MAX("executedAt") as latest_check
    FROM monitors 
    WHERE "executedAt" > NOW() - INTERVAL '10 minutes'
    GROUP BY "agentRegion", "monitorName"
) latest ON m."agentRegion" = latest."agentRegion" 
    AND m."monitorName" = latest."monitorName"
    AND m."executedAt" = latest.latest_check
ORDER BY m."agentRegion", m."monitorName";
```

### Agent Health Monitoring

**Agent Status Across Regions:**
```sql
-- Monitor agent health and data freshness
SELECT 
    "agentRegion" as region,
    COUNT(DISTINCT "monitorName") as active_monitors,
    MAX("executedAt") as last_data_received,
    NOW() - MAX("executedAt") as data_age,
    CASE 
        WHEN MAX("executedAt") > NOW() - INTERVAL '5 minutes' THEN 'Healthy'
        WHEN MAX("executedAt") > NOW() - INTERVAL '15 minutes' THEN 'Warning'
        ELSE 'Critical'
    END as agent_status
FROM monitors 
WHERE "executedAt" > NOW() - INTERVAL '1 hour'
GROUP BY "agentRegion"
ORDER BY data_age;
```

## Alerting Configuration

### Proactive Alerting Rules

**High Latency Alert (per region):**
```yaml
# alerting-rules.yml
- name: api-latency-alerts
  rules:
    - alert: HighAPILatency
      expr: avg_over_time(monitor_response_time{region="us-east-1"}[5m]) > 2000
      for: 2m
      labels:
        severity: warning
        region: us-east-1
      annotations:
        summary: "High API latency detected in us-east-1"
        description: "API {{ $labels.monitor_name }} in us-east-1 has average latency of {{ $value }}ms"
        
    - alert: APIDown
      expr: monitor_success{region="us-west-2"} == 0
      for: 1m
      labels:
        severity: critical
        region: us-west-2
      annotations:
        summary: "API Down in us-west-2"
        description: "API {{ $labels.monitor_name }} is down in us-west-2 region"
```

## Benefits of This Architecture

### ✅ **Regional Client Experience Matching**
- us-east-1 clients → us-east-1 agent monitors → us-east-1 API performance data
- us-west-2 clients → us-west-2 agent monitors → us-west-2 API performance data
- Same network path and latency as actual client requests

### ✅ **Proactive Issue Detection** 
- Agents detect API issues before clients report them
- Region-specific performance baselines and thresholds
- Automated alerting when performance degrades

### ✅ **Root Cause Analysis**
- When client reports issue, immediately query same region's agent data
- Compare performance across regions to identify regional vs global issues
- Historical data for trend analysis and capacity planning

### ✅ **Scalable Architecture**
- Add new regions by deploying new agent instances
- Central database consolidates all regional monitoring data
- Each agent runs independently - no single point of failure

### ✅ **Operational Visibility**
- Real-time dashboard showing API health across all regions
- Performance comparison between regions
- Agent health monitoring to ensure monitoring coverage

This architecture ensures your monitoring provides the same experience perspective as your clients, enabling proactive issue detection and rapid root cause analysis when problems occur.