# Agent API Documentation

## Overview
Agent APIs for infrastructure discovery and reporting. All endpoints require API key authentication except registration.

## API Files

### 0. API Key Authentication
**File**: `0-API-KEY-AUTH.md`
- How to get API key
- How to use API key in requests
- Public vs protected endpoints

### 1. Agent Self-Registration
**File**: `1-AGENT.md`
**Endpoint**: `POST /api/agent/register`
**Auth**: None (public)
- Register agent with backend
- Auto-create Region/Datacenter from tags
- Receive unique API key

### 2. Region Creation
**File**: `2-REGION.md`
**Endpoint**: `POST /api/agent/regions`
**Auth**: API Key required
- Create geographic region
- Properties: name, regionCode, groupName

### 3. Datacenter Creation
**File**: `3-DATACENTER.md`
**Endpoint**: `POST /api/agent/datacenters`
**Auth**: API Key required
- Create datacenter within region
- Properties: code, name, region.id

### 4. Instance Creation
**File**: `4-INSTANCE.md`
**Endpoint**: `POST /api/agent/instances`
**Auth**: API Key required
- Create server/VM instance
- 27 properties including ping/hardware monitoring config
- Properties: name, hostname, instanceType, monitoringType, etc.

### 5. HTTP Monitor Creation
**File**: `5-HTTP-MONITOR.md`
**Endpoint**: `POST /api/agent/http-monitors`
**Auth**: API Key required
- Create HTTP/HTTPS endpoint monitor
- 29 properties including timing, SSL, validation
- Properties: name, method, url, headers, body, etc.

### 6. Monitored Service Creation (with Service Instances)
**File**: `6-MONITORED-SERVICE.md`
**Endpoint**: `POST /api/agent/monitored-services`
**Auth**: API Key required
- Create service with multiple instances (hosts)
- Supports clusters (Elasticsearch, Redis, LDAP, etc.)
- Properties: name, serviceType, environment, serviceInstances[]
- ServiceInstances: instance.id, port, isActive

## Typical Agent Workflow

```
1. Register Agent (no API key needed)
   POST /api/agent/register
   → Receive API key

2. Create Infrastructure (use API key)
   POST /api/agent/regions
   POST /api/agent/datacenters
   POST /api/agent/instances

3. Create Monitors (use API key)
   POST /api/agent/http-monitors
   POST /api/agent/monitored-services (with serviceInstances)
```

## Example: Complete Setup

```bash
# 1. Register Agent
API_KEY=$(curl -X POST http://localhost:8080/api/agent/register \
  -H 'Content-Type: application/json' \
  -d '{"name":"agent-1","hostname":"host-1","ipAddress":"10.0.1.5","osType":"Linux","agentVersion":"1.0.0","tags":{"region":"AWS US East","datacenter":"Virginia DC1"}}' \
  | jq -r '.apiKey')

# 2. Create Instance
curl -X POST http://localhost:8080/api/agent/instances \
  -H 'Content-Type: application/json' \
  -H "X-API-Key: $API_KEY" \
  -d '{"name":"web-1","hostname":"web-1.example.com","instanceType":"VM","monitoringType":"AGENT_MONITORED","datacenter":{"id":1},"agent":{"id":1}}'

# 3. Create Monitored Service with Instances
curl -X POST http://localhost:8080/api/agent/monitored-services \
  -H 'Content-Type: application/json' \
  -H "X-API-Key: $API_KEY" \
  -d '{
    "name":"Elasticsearch Cluster",
    "serviceType":"ELASTICSEARCH",
    "environment":"PROD",
    "monitoringEnabled":true,
    "intervalSeconds":30,
    "timeoutMs":2000,
    "retryCount":2,
    "datacenter":{"id":1},
    "serviceInstances":[
      {"instance":{"id":1},"port":9200,"isActive":true},
      {"instance":{"id":2},"port":9200,"isActive":true}
    ]
  }'
```

## Status
✅ All agent creation APIs ready with API key authentication
