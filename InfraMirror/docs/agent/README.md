# InfraMirror Agent Implementation Guide

## Overview
Step-by-step guide for implementing Datadog-style agent self-registration with HA support.

**🔥 START HERE**: Read `0.UnderstandingTheFlow.md` first to understand who does what (UI vs Agent vs Backend).

## Implementation Sequence

### Phase 1: Foundation (Current Focus)

#### Step 1: Prepare Agent Entity ✅
**File**: `1.PrepareAgent.md`

**Goal**: Add all properties needed for agent self-registration

**Key Changes**:
- Add hostname, ipAddress, osType, osVersion, agentVersion
- Add lastSeenAt, status, tags (JSONB)
- Add datacenter relationship
- Single Liquibase changelog for all changes

**HA Support**:
- ✅ Multiple agents per datacenter
- ✅ Active-Passive HA (using AgentLock)
- ✅ Load balancing (multiple active agents)
- ✅ Specialized agents (different roles)

**Dependencies**: None

**Outcome**: Agent entity ready with all fields

---

#### Step 2: Create Agent Registration API ✅
**File**: `2.CreateAgentRegistration.md`

**Goal**: Dedicated endpoint for agent self-registration

**Key Features**:
- Single API key for all operations (no MASTER/AGENT types)
- Auto-creates Region/Datacenter from tags
- Stores all agent properties
- Supports multiple agents per datacenter

**Endpoint**: `POST /api/agents/register`

**Dependencies**: Step 1 must be complete

**Outcome**: Agents can self-register via API

---

### Phase 2: Agent APIs (Week 2)

#### Step 3: Infrastructure Reporting API
**Goal**: Allow agents to report discovered instances and services

**Endpoint**: `POST /api/agents/{agentId}/report-infrastructure`

**Tasks**:
- Create InfrastructureReportDTO
- Implement find-or-create logic for Instance
- Implement find-or-create logic for MonitoredService
- Link to agent's datacenter

---

#### Step 4: Batch Heartbeat Ingestion API
**Goal**: Efficient bulk heartbeat submission

**Endpoint**: `POST /api/heartbeats/batch`

**Tasks**:
- Create BatchHeartbeatDTO
- Bulk insert for InstanceHeartbeat
- Bulk insert for ServiceHeartbeat
- Bulk insert for HttpHeartbeat

---

#### Step 5: Get HTTP Monitors API
**Goal**: Return HTTP monitors assigned to agent

**Endpoint**: `GET /api/agents/{agentId}/http-monitors`

---

### Phase 3: Go Agent Development (Week 3-4)

#### Step 6-12: Build Go Agent
- Configuration loader
- Registration module
- Discovery module
- Monitoring modules
- Heartbeat submission
- Scheduler

---

## Quick Start

### 1. Prepare Agent Entity

```bash
# Read documentation
cat docs/agent/1.PrepareAgent.md

# Implement changes
# - Update Agent.java
# - Create Liquibase changelog
# - Update AgentDTO
# - Update AgentMapper

# Test
./mvnw
docker exec -it inframirror-postgres psql -U inframirror -d inframirror
\d agent
```

### 2. Create Registration API

```bash
# Read documentation
cat docs/agent/2.CreateAgentRegistration.md

# Implement changes
# - Create AgentRegistrationResource
# - Update SecurityConfiguration
# - Add API key validation

# Test with cURL
curl -X POST http://localhost:8080/api/agents/register \
  -H "X-API-Key: your-api-key" \
  -H "Content-Type: application/json" \
  -d '{...}'
```

---

## Key Design Decisions

### Single API Key
**Decision**: Use one API key for all operations (no MASTER/AGENT distinction)

**Rationale**:
- Simpler for users
- Less code complexity
- Easier key management
- Sufficient security for use case

### Multiple Agents Per Datacenter
**Decision**: Allow multiple agents in same datacenter

**Rationale**:
- Supports HA (active-passive)
- Supports load balancing
- Supports specialized agents
- Uses AgentLock for coordination

### Single Liquibase Changelog
**Decision**: One changelog file per entity

**Rationale**:
- Development mode - can re-run with clean DB
- Easier to track changes
- Simpler rollback
- Less file clutter

### Validation from UI Only
**Decision**: Minimal backend validation

**Rationale**:
- UI handles user input validation
- Backend just stores data
- Faster development
- Less code to maintain

---

## HA Scenarios

### Scenario 1: Active-Passive HA
```yaml
# Agent 1 (Primary)
agent:
  name: "agent-dc1-primary"
  tags:
    datacenter: "DC1"
    ha_mode: "active-passive"
    ha_group: "dc1-ha"

# Agent 2 (Backup)
agent:
  name: "agent-dc1-backup"
  tags:
    datacenter: "DC1"
    ha_mode: "active-passive"
    ha_group: "dc1-ha"
```

**How it works**:
- Both agents register in same datacenter
- Use AgentLock table for leader election
- Only ACTIVE agent submits heartbeats
- Backup takes over if primary fails

### Scenario 2: Load Balancing
```yaml
# Agent 1
agent:
  name: "agent-dc1-worker-1"
  tags:
    datacenter: "DC1"
    role: "worker"

# Agent 2
agent:
  name: "agent-dc1-worker-2"
  tags:
    datacenter: "DC1"
    role: "worker"
```

**How it works**:
- Both agents ACTIVE simultaneously
- Each monitors different instances
- Distribute load across agents

### Scenario 3: Specialized Agents
```yaml
# Network Agent
agent:
  name: "agent-dc1-network"
  tags:
    datacenter: "DC1"
    role: "network-monitoring"

# Database Agent
agent:
  name: "agent-dc1-database"
  tags:
    datacenter: "DC1"
    role: "database-monitoring"
```

**How it works**:
- Each agent has specific responsibility
- Different configurations
- No overlap in monitoring

---

## Testing

### Database Verification
```sql
-- Check agents in datacenter
SELECT a.id, a.name, a.hostname, a.status, d.name as datacenter
FROM agent a
JOIN datacenter d ON a.datacenter_id = d.id
WHERE d.name = 'DC1';

-- Check agent tags
SELECT name, tags FROM agent;
```

### cURL Testing
```bash
# Register agent
curl -X POST http://localhost:8080/api/agents/register \
  -H "X-API-Key: your-key" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "test-agent",
    "hostname": "test-host",
    "ipAddress": "10.0.1.5",
    "osType": "Linux",
    "agentVersion": "1.0.0",
    "tags": {
      "region": "Test",
      "datacenter": "Test DC"
    }
  }'
```

---

## Success Criteria

### Phase 1 Complete When:
- [ ] Agent entity has all required fields
- [ ] Database migration runs successfully
- [ ] `POST /api/agents/register` works
- [ ] Region/Datacenter auto-created
- [ ] Multiple agents can register in same datacenter
- [ ] Tags stored as JSONB
- [ ] cURL tests pass
- [ ] Integration tests pass

---

## Next Steps

1. ✅ Review documentation
2. ⏳ Implement Step 1 (Agent Entity)
3. ⏳ Implement Step 2 (Registration API)
4. ⏳ Test with cURL
5. ⏳ Move to Phase 2 (Infrastructure APIs)

---

## Notes

- **Development Mode**: Can re-run with clean database
- **Single API Key**: Simpler than MASTER/AGENT types
- **HA Support**: Built-in from day one
- **Validation**: From UI only, backend stores data
- **Go Agent**: Will be implemented in Phase 3
