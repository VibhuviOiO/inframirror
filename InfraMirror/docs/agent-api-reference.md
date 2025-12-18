# InfraMirror Agent API Reference

## Overview
This document describes the REST APIs that InfraMirror agents use to communicate with the backend.

**Base URL**: `https://inframirror.example.com`  
**Authentication**: API Key via `X-API-Key` header  
**Content-Type**: `application/json`

---

## Authentication

### Master API Key
Used for initial agent registration. Generated from UI.

```
X-API-Key: master-key-generated-from-ui
```

### Agent API Key
Returned after registration. Used for all subsequent requests.

```
X-API-Key: agent-specific-key-xyz
```

---

## API Endpoints

### 1. Agent Registration

**Endpoint**: `POST /api/agents/register`  
**Auth**: Master API Key  
**Purpose**: Register new agent and auto-create Region/Datacenter

#### Request
```http
POST /api/agents/register HTTP/1.1
Host: inframirror.example.com
X-API-Key: master-key-generated-from-ui
Content-Type: application/json

{
  "name": "agent-aws-us-east-1",
  "hostname": "ip-10-0-1-5.ec2.internal",
  "ipAddress": "10.0.1.5",
  "osType": "Linux",
  "osVersion": "Ubuntu 22.04.3 LTS",
  "agentVersion": "1.0.0",
  "tags": {
    "region": "AWS US East",
    "datacenter": "Virginia DC1",
    "environment": "production",
    "cloud_provider": "aws"
  }
}
```

#### Response: 200 OK
```json
{
  "agentId": 123,
  "apiKey": "agent-abc123xyz789",
  "region": {
    "id": 1,
    "name": "AWS US East"
  },
  "datacenter": {
    "id": 5,
    "name": "Virginia DC1"
  },
  "status": "REGISTERED",
  "message": "Agent registered successfully"
}
```

#### Response: 400 Bad Request
```json
{
  "error": "INVALID_REQUEST",
  "message": "Agent name is required"
}
```

#### Response: 401 Unauthorized
```json
{
  "error": "INVALID_API_KEY",
  "message": "Invalid or expired API key"
}
```

---

### 2. Infrastructure Reporting

**Endpoint**: `POST /api/agents/{agentId}/report-infrastructure`  
**Auth**: Agent API Key  
**Purpose**: Report discovered instances and services

#### Request
```http
POST /api/agents/123/report-infrastructure HTTP/1.1
Host: inframirror.example.com
X-API-Key: agent-abc123xyz789
Content-Type: application/json

{
  "instances": [
    {
      "hostname": "web-server-1.internal",
      "privateIp": "10.0.1.10",
      "publicIp": "54.123.45.67",
      "osType": "Linux",
      "osVersion": "Ubuntu 22.04",
      "platform": "x86_64",
      "instanceType": "VM"
    },
    {
      "hostname": "db-server-1.internal",
      "privateIp": "10.0.1.20",
      "publicIp": null,
      "osType": "Linux",
      "osVersion": "Ubuntu 22.04",
      "platform": "x86_64",
      "instanceType": "VM"
    }
  ],
  "services": [
    {
      "instanceHostname": "web-server-1.internal",
      "serviceType": "NGINX",
      "port": 80,
      "version": "1.18.0"
    },
    {
      "instanceHostname": "db-server-1.internal",
      "serviceType": "POSTGRESQL",
      "port": 5432,
      "version": "14.9"
    },
    {
      "instanceHostname": "db-server-1.internal",
      "serviceType": "REDIS",
      "port": 6379,
      "version": "7.0.12"
    }
  ]
}
```

#### Response: 200 OK
```json
{
  "instancesCreated": 2,
  "instancesUpdated": 0,
  "servicesCreated": 3,
  "servicesUpdated": 0,
  "instances": [
    {
      "id": 101,
      "hostname": "web-server-1.internal",
      "status": "created"
    },
    {
      "id": 102,
      "hostname": "db-server-1.internal",
      "status": "created"
    }
  ],
  "services": [
    {
      "id": 201,
      "name": "NGINX on web-server-1",
      "serviceType": "NGINX",
      "status": "created"
    },
    {
      "id": 202,
      "name": "POSTGRESQL on db-server-1",
      "serviceType": "POSTGRESQL",
      "status": "created"
    },
    {
      "id": 203,
      "name": "REDIS on db-server-1",
      "serviceType": "REDIS",
      "status": "created"
    }
  ]
}
```

#### Response: 401 Unauthorized
```json
{
  "error": "INVALID_API_KEY",
  "message": "Invalid or expired API key"
}
```

#### Response: 404 Not Found
```json
{
  "error": "AGENT_NOT_FOUND",
  "message": "Agent with ID 123 not found"
}
```

---

### 3. Batch Heartbeat Ingestion

**Endpoint**: `POST /api/heartbeats/batch`  
**Auth**: Agent API Key  
**Purpose**: Submit monitoring results in bulk

#### Request
```http
POST /api/heartbeats/batch HTTP/1.1
Host: inframirror.example.com
X-API-Key: agent-abc123xyz789
Content-Type: application/json

{
  "agentId": 123,
  "timestamp": "2025-01-21T10:00:00Z",
  "pingHeartbeats": [
    {
      "instanceId": 101,
      "executedAt": "2025-01-21T10:00:00Z",
      "success": true,
      "responseTimeMs": 5,
      "packetLoss": 0.0,
      "jitterMs": 1,
      "status": "UP"
    },
    {
      "instanceId": 102,
      "executedAt": "2025-01-21T10:00:00Z",
      "success": true,
      "responseTimeMs": 3,
      "packetLoss": 0.0,
      "jitterMs": 0,
      "status": "UP"
    }
  ],
  "hardwareHeartbeats": [
    {
      "instanceId": 101,
      "executedAt": "2025-01-21T10:00:00Z",
      "success": true,
      "cpuUsage": 45.2,
      "memoryUsage": 68.5,
      "diskUsage": 72.0,
      "loadAverage": 2.5,
      "processCount": 156,
      "networkRxBytes": 1048576,
      "networkTxBytes": 524288,
      "uptimeSeconds": 864000,
      "status": "WARNING"
    }
  ],
  "serviceHeartbeats": [
    {
      "serviceId": 201,
      "serviceInstanceId": 301,
      "executedAt": "2025-01-21T10:00:00Z",
      "success": true,
      "status": "UP",
      "responseTimeMs": 2,
      "metadata": {
        "connections": 12,
        "version": "1.18.0"
      }
    },
    {
      "serviceId": 202,
      "serviceInstanceId": 302,
      "executedAt": "2025-01-21T10:00:00Z",
      "success": true,
      "status": "UP",
      "responseTimeMs": 10,
      "metadata": {
        "connections": 45,
        "version": "14.9",
        "database_size_mb": 2048
      }
    }
  ],
  "httpHeartbeats": [
    {
      "monitorId": 401,
      "executedAt": "2025-01-21T10:00:00Z",
      "success": true,
      "statusCode": 200,
      "responseTimeMs": 150,
      "dnsLookupMs": 5,
      "tcpConnectMs": 20,
      "tlsHandshakeMs": 30,
      "timeToFirstByteMs": 120,
      "responseSizeBytes": 1024
    }
  ]
}
```

#### Response: 202 Accepted
```json
{
  "accepted": true,
  "pingCount": 2,
  "hardwareCount": 1,
  "serviceCount": 2,
  "httpCount": 1,
  "totalCount": 6,
  "message": "Heartbeats accepted for processing"
}
```

#### Response: 400 Bad Request
```json
{
  "error": "INVALID_REQUEST",
  "message": "agentId is required"
}
```

#### Response: 401 Unauthorized
```json
{
  "error": "INVALID_API_KEY",
  "message": "Invalid or expired API key"
}
```

---

### 4. Get HTTP Monitors

**Endpoint**: `GET /api/agents/{agentId}/http-monitors`  
**Auth**: Agent API Key  
**Purpose**: Retrieve HTTP monitors assigned to agent

#### Request
```http
GET /api/agents/123/http-monitors HTTP/1.1
Host: inframirror.example.com
X-API-Key: agent-abc123xyz789
```

#### Response: 200 OK
```json
[
  {
    "id": 401,
    "name": "API Health Check",
    "url": "https://api.example.com/health",
    "method": "GET",
    "intervalSeconds": 60,
    "timeoutSeconds": 10,
    "retryCount": 2,
    "retryDelaySeconds": 5,
    "headers": {
      "Authorization": "Bearer token123",
      "User-Agent": "InfraMirror-Agent/1.0"
    },
    "body": null,
    "expectedStatusCode": 200,
    "enabled": true
  },
  {
    "id": 402,
    "name": "Payment Gateway Check",
    "url": "https://payment.example.com/status",
    "method": "POST",
    "intervalSeconds": 30,
    "timeoutSeconds": 5,
    "retryCount": 3,
    "retryDelaySeconds": 3,
    "headers": {
      "Content-Type": "application/json",
      "X-API-Key": "payment-key"
    },
    "body": "{\"check\": \"status\"}",
    "expectedStatusCode": 200,
    "enabled": true
  }
]
```

#### Response: 200 OK (No monitors)
```json
[]
```

#### Response: 401 Unauthorized
```json
{
  "error": "INVALID_API_KEY",
  "message": "Invalid or expired API key"
}
```

---

## Data Models

### ServiceType Enum
```
TCP, NGINX, APACHE, POSTGRESQL, MYSQL, MONGODB, REDIS, 
CASSANDRA, ELASTICSEARCH, KAFKA, RABBITMQ, CUSTOM
```

### InstanceType Enum
```
VM, BARE_METAL, CONTAINER, CLOUD_INSTANCE
```

### ServiceStatus Enum
```
UP, DOWN, WARNING, CRITICAL, DEGRADED, TIMEOUT
```

### PingStatus Enum
```
UP, DOWN, DEGRADED, WARNING, DANGER, TIMEOUT
```

---

## Error Codes

| Code | HTTP Status | Description |
|------|-------------|-------------|
| `INVALID_API_KEY` | 401 | API key is invalid or expired |
| `INVALID_REQUEST` | 400 | Request body validation failed |
| `AGENT_NOT_FOUND` | 404 | Agent ID not found |
| `INSTANCE_NOT_FOUND` | 404 | Instance ID not found |
| `SERVICE_NOT_FOUND` | 404 | Service ID not found |
| `MONITOR_NOT_FOUND` | 404 | HTTP monitor ID not found |
| `RATE_LIMIT_EXCEEDED` | 429 | Too many requests |
| `INTERNAL_ERROR` | 500 | Server error |

---

## Rate Limiting

- **Registration**: 10 requests per hour per IP
- **Infrastructure Reporting**: 100 requests per hour per agent
- **Heartbeat Ingestion**: 1000 requests per hour per agent
- **HTTP Monitors**: 100 requests per hour per agent

---

## Best Practices

### 1. Batch Heartbeats
Submit heartbeats in batches every 30-60 seconds instead of individual requests.

### 2. Retry Logic
Implement exponential backoff: 1s, 2s, 4s, 8s, 16s (max 5 retries).

### 3. Local Queue
Queue heartbeats locally if backend is unreachable. Max queue size: 1000.

### 4. Error Handling
Log errors but continue monitoring. Don't crash on API failures.

### 5. API Key Security
- Store API key securely (encrypted file or environment variable)
- Never log API key
- Rotate API key periodically

### 6. Connection Pooling
Reuse HTTP connections for better performance.

---

## Example Agent Flow

```python
# 1. Register agent
response = requests.post(
    f"{API_URL}/api/agents/register",
    headers={"X-API-Key": MASTER_API_KEY},
    json={
        "name": "agent-1",
        "hostname": socket.gethostname(),
        "ipAddress": get_local_ip(),
        "osType": platform.system(),
        "osVersion": platform.version(),
        "agentVersion": "1.0.0",
        "tags": {
            "region": "US East",
            "datacenter": "DC1"
        }
    }
)
agent_id = response.json()["agentId"]
agent_api_key = response.json()["apiKey"]

# 2. Discover infrastructure
instances = discover_instances()
services = discover_services()

response = requests.post(
    f"{API_URL}/api/agents/{agent_id}/report-infrastructure",
    headers={"X-API-Key": agent_api_key},
    json={
        "instances": instances,
        "services": services
    }
)

# 3. Start monitoring loop
while True:
    # Collect heartbeats
    ping_heartbeats = collect_ping_heartbeats()
    hardware_heartbeats = collect_hardware_heartbeats()
    service_heartbeats = collect_service_heartbeats()
    http_heartbeats = collect_http_heartbeats()
    
    # Submit batch
    requests.post(
        f"{API_URL}/api/heartbeats/batch",
        headers={"X-API-Key": agent_api_key},
        json={
            "agentId": agent_id,
            "timestamp": datetime.utcnow().isoformat(),
            "pingHeartbeats": ping_heartbeats,
            "hardwareHeartbeats": hardware_heartbeats,
            "serviceHeartbeats": service_heartbeats,
            "httpHeartbeats": http_heartbeats
        }
    )
    
    time.sleep(30)
```

---

## Testing

### Postman Collection
Import the Postman collection from `agent-api-postman-collection.json` for easy testing.

### cURL Examples

**Register Agent**:
```bash
curl -X POST https://inframirror.example.com/api/agents/register \
  -H "X-API-Key: master-key" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "test-agent",
    "hostname": "test-host",
    "ipAddress": "10.0.1.5",
    "osType": "Linux",
    "osVersion": "Ubuntu 22.04",
    "agentVersion": "1.0.0",
    "tags": {"region": "Test", "datacenter": "Test DC"}
  }'
```

**Report Infrastructure**:
```bash
curl -X POST https://inframirror.example.com/api/agents/123/report-infrastructure \
  -H "X-API-Key: agent-key" \
  -H "Content-Type: application/json" \
  -d '{
    "instances": [{"hostname": "test-instance", "privateIp": "10.0.1.10", "osType": "Linux", "instanceType": "VM"}],
    "services": [{"instanceHostname": "test-instance", "serviceType": "POSTGRESQL", "port": 5432}]
  }'
```

**Submit Heartbeats**:
```bash
curl -X POST https://inframirror.example.com/api/heartbeats/batch \
  -H "X-API-Key: agent-key" \
  -H "Content-Type: application/json" \
  -d '{
    "agentId": 123,
    "timestamp": "2025-01-21T10:00:00Z",
    "pingHeartbeats": [{"instanceId": 101, "executedAt": "2025-01-21T10:00:00Z", "success": true, "responseTimeMs": 5, "status": "UP"}]
  }'
```

---

## Support

For questions or issues:
- GitHub Issues: https://github.com/your-org/inframirror/issues
- Documentation: https://docs.inframirror.com
- Email: support@inframirror.com
