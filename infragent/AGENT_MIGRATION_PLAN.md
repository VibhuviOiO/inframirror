# Agent Migration Plan: From YAML to Database-Driven Architecture

## Overview

This document outlines the migration plan from static YAML-based agent configuration to a dynamic database-driven architecture for the Infrastructure Monitoring Agent (Infragent).

## Current State

### Current Architecture ✅
- Agents run with `docker/infragent-compose.yml`
- Static YAML configuration files (`monitors-{region}.yml`)
- Manual agent deployment and configuration
- File-based monitor definitions

### Current Issues
- **Meaningless Metrics**: "672ms Avg Response" provides little monitoring value
- **Static Configuration**: No dynamic monitor updates without agent restart
- **Manual Management**: Agent deployment and monitor assignment is manual
- **Limited Visibility**: No centralized agent status tracking

## Migration Phases

### Phase 1: Smart Status Metrics (Quick Win) 🚀
**Timeline**: 2-3 hours  
**Impact**: Minimal agent changes, immediate monitoring value

#### Replace Average Response Time with Status Distribution
Instead of showing average response time, display:
- 🟢 **Green (Healthy)**: Response time < 500ms
- 🟡 **Yellow (Warning)**: Response time 500ms - 1000ms  
- 🔴 **Red (Critical)**: Response time > 1000ms or failed requests

#### Implementation
```typescript
// Add threshold checking to existing collector
if (responseTime < 500) status = 'healthy';
else if (responseTime < 1000) status = 'warning'; 
else status = 'critical';
```

#### Add Configurable Response Time Thresholds
```yaml
# In agent monitor configuration
monitors:
  - name: "api-monitor"
    url: "/api/health"
    thresholds:
      warning: 500    # Yellow threshold (ms)
      critical: 1000  # Red threshold (ms)
    
# Or global defaults
global:
  response_time_thresholds:
    warning: 500
    critical: 1000
```

### Phase 2: Database-Driven Configuration (Medium Term) 🔧
**Timeline**: 1-2 days  
**Impact**: Moderate agent changes, backward compatibility maintained

#### API Design
- `GET /api/agents/{agentId}/monitors` - Fetch monitor configuration
- `POST /api/agents/{agentId}/register` - Agent registration
- `PUT /api/agents/{agentId}/status` - Agent heartbeat and status updates

#### Hybrid Config System
```typescript
class DatabaseConfigLoader {
  async loadMonitors(agentId: string) {
    try {
      const response = await fetch(`/api/agents/${agentId}/monitors`);
      if (!response.ok) throw new Error('API unavailable');
      return response.json();
    } catch (error) {
      console.warn('Database config unavailable, falling back to YAML');
      return null;
    }
  }
}

// Fallback mechanism ensures reliability
const config = await dbLoader.loadMonitors(agentId) || yamlLoader.loadConfig();
```

#### Backend Database Schema
```sql
-- Agents table
CREATE TABLE agents (
  id VARCHAR(255) PRIMARY KEY,
  region VARCHAR(50) NOT NULL,
  status VARCHAR(20) DEFAULT 'active',
  last_seen TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  version VARCHAR(20),
  metadata JSONB
);

-- Agent monitor assignments
CREATE TABLE agent_monitors (
  id SERIAL PRIMARY KEY,
  agent_id VARCHAR(255) REFERENCES agents(id),
  monitor_config JSONB NOT NULL,
  enabled BOOLEAN DEFAULT true,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Monitor templates for reuse
CREATE TABLE monitor_templates (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  template_config JSONB NOT NULL,
  description TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Phase 3: Agent Management UI (Long Term) 🎨
**Timeline**: 3-5 days  
**Impact**: Complete management interface

#### Features
- **Agent Discovery**: Real-time agent registration and status
- **Monitor Assignment**: Drag-and-drop monitor deployment
- **Health Dashboard**: Live agent and monitor status
- **Template Library**: Reusable monitor configurations
- **Bulk Operations**: Deploy monitors to multiple agents

#### UI Components
```tsx
// Agent management dashboard
<AgentDashboard>
  <AgentList agents={agents} />
  <MonitorAssignment />
  <HealthMetrics />
  <TemplateLibrary />
</AgentDashboard>
```

## Implementation Roadmap

### Week 1: Phase 1 Implementation
- [x] **Day 1-2**: Replace avg response time with status distribution
- [x] **Day 2-3**: Add configurable thresholds to agent config
- [x] **Day 3**: Update frontend metrics display

### Week 2: Phase 2 Planning & Design
- [ ] **Day 1**: Design database schema and API endpoints
- [ ] **Day 2**: Plan agent-to-database communication protocol
- [ ] **Day 3-4**: Create backend APIs for agent management
- [ ] **Day 5**: Implement hybrid config loader in agents

### Week 3: Phase 2 Implementation
- [ ] **Day 1-2**: Build database-driven config system
- [ ] **Day 3-4**: Test hybrid configuration approach
- [ ] **Day 5**: Migration scripts and tooling

### Week 4: Phase 3 Planning
- [ ] **Day 1-2**: UI/UX design for agent management
- [ ] **Day 3-5**: Frontend component development

### Week 5-6: Phase 3 Implementation
- [ ] **Week 5**: Agent management interface development
- [ ] **Week 6**: Integration testing and deployment

## Migration Strategy

### Safe Migration Approach
1. **Dual Support**: Maintain both YAML and database config support
2. **Gradual Rollout**: Migrate agents one region at a time
3. **Validation**: Monitor continuity during migration
4. **Rollback Capability**: Quick revert to YAML if issues arise

### Migration Steps
```bash
# Step 1: Deploy new agent version with hybrid config
docker-compose -f infragent-compose.yml up -d

# Step 2: Register agents in database
curl -X POST /api/agents/us-east-1-agent-01/register

# Step 3: Migrate monitor config to database
./scripts/migrate-monitors.sh us-east-1

# Step 4: Validate monitoring continuity
./scripts/validate-monitoring.sh

# Step 5: Disable YAML config (optional)
# Remove volume mounts in docker-compose.yml
```

### Rollback Plan
- **Feature Flags**: Control config source via environment variables
- **YAML Backup**: Keep original YAML files as backup
- **Quick Revert**: Single command to switch back to YAML
- **Health Monitoring**: Track migration success metrics

## Benefits of New Architecture

### Operational Benefits
- **Dynamic Updates**: Change monitor configuration without agent restart
- **Centralized Management**: Single source of truth for all agents
- **Real-time Visibility**: Live agent status and health monitoring
- **Scalability**: Support for 100+ agents with efficient config distribution

### Development Benefits
- **API-Driven**: Programmatic agent and monitor management
- **Template System**: Reusable monitor configurations
- **Bulk Operations**: Deploy changes across multiple agents
- **Version Control**: Track configuration changes over time

### Monitoring Benefits
- **Meaningful Metrics**: Status distribution instead of averages
- **Threshold-Based Alerting**: Configurable warning/critical levels
- **Agent Health**: Monitor the monitoring infrastructure itself
- **Performance Analytics**: Per-agent and per-monitor insights

## Performance Considerations

### Agent Performance
- **Config Caching**: Agents cache config locally (5-10 minute refresh)
- **Fallback Mechanism**: Use cached config if API unavailable
- **Minimal Overhead**: Database calls only for config updates
- **Connection Pooling**: Efficient API connections

### Scalability
- **Database Indexing**: Optimize agent and monitor queries
- **Config Compression**: Minimize payload size
- **CDN Distribution**: Cache config at edge locations (future)
- **Horizontal Scaling**: Support multiple backend instances

### Monitoring the Monitors
- **Config Refresh Metrics**: Track successful config updates
- **API Response Times**: Monitor backend performance
- **Failed Fetch Alerting**: Alert on config retrieval failures
- **Agent Connectivity**: Track agent-to-backend health

## Risk Assessment

### Low Risk
- **Phase 1 Implementation**: Minimal changes, high value
- **Hybrid Config System**: Maintains backward compatibility
- **Gradual Migration**: Region-by-region rollout

### Medium Risk
- **Database Dependencies**: New failure mode if DB unavailable
- **API Latency**: Potential config fetch delays
- **Migration Complexity**: Coordinating multiple system changes

### Mitigation Strategies
- **Robust Fallbacks**: YAML config as backup
- **Health Checks**: Monitor all system components
- **Testing**: Comprehensive testing in staging environment
- **Documentation**: Clear rollback procedures

## Future Feature Requirements

### Retries Configuration
**Status**: Not currently implemented - requires future development

The monitoring system should support configurable retry logic for failed checks:

```yaml
# Proposed retry configuration structure
monitors:
  - name: "api-monitor"
    url: "/api/health"
    retry_policy:
      max_attempts: 3        # Total attempts (1 initial + 2 retries)
      backoff_strategy: "exponential"  # linear, exponential, fixed
      base_delay_ms: 1000    # Initial retry delay
      max_delay_ms: 10000    # Maximum delay between retries
      timeout_per_attempt: 5000  # Timeout for each individual attempt
```

**Implementation Priority**: Medium - Add after database-driven configuration is complete

**Database Schema Addition**:
```sql
-- Add retry configuration to monitor config JSONB
ALTER TABLE agent_monitors 
ADD COLUMN retry_config JSONB DEFAULT '{
  "max_attempts": 1,
  "backoff_strategy": "fixed", 
  "base_delay_ms": 1000,
  "max_delay_ms": 5000,
  "timeout_per_attempt": 5000
}';
```

**Frontend Filter Impact**: 
- Currently retries filter is placeholder in UI
- Will need retry count tracking in monitor results
- Add retry statistics to monitoring dashboard

## Success Criteria

### Phase 1 Success
- ✅ Status distribution replaces average response time
- ✅ Configurable thresholds working correctly  
- ✅ No monitoring functionality regression
- ✅ Improved monitoring insights for operations team

### Phase 2 Success
- ✅ Agents successfully fetch config from database
- ✅ Fallback to YAML config when DB unavailable
- ✅ Zero monitoring downtime during migration
- ✅ All existing monitors continue to function

### Phase 3 Success
- ✅ UI provides complete agent management capability
- ✅ Monitor deployment through UI working correctly
- ✅ Real-time agent status accurately displayed
- ✅ Operations team trained on new management interface

## Documentation Deliverables

### Technical Documentation
- [ ] **API Documentation**: Complete endpoint specifications
- [ ] **Database Schema**: Table structures and relationships
- [ ] **Migration Guide**: Step-by-step migration instructions
- [ ] **Troubleshooting Guide**: Common issues and solutions

### User Documentation
- [ ] **Agent Deployment Guide**: Docker setup with database
- [ ] **UI User Manual**: Agent management interface walkthrough
- [ ] **Best Practices**: Monitor configuration recommendations
- [ ] **Performance Tuning**: Optimization guidelines

### Training Materials
- [ ] **Video Tutorials**: Screen recordings of key workflows
- [ ] **Interactive Demos**: Hands-on training environments
- [ ] **Quick Reference**: Cheat sheets for common tasks
- [ ] **Migration Checklist**: Verification steps for rollout

---

## Next Steps

1. **Immediate**: Begin Phase 1 implementation (status distribution)
2. **This Week**: Complete Phase 1 and validate improvements
3. **Next Sprint**: Start Phase 2 planning and database design
4. **Following Sprint**: Implement hybrid config system

This migration plan provides a clear path from the current YAML-based approach to a modern, scalable, database-driven architecture while maintaining system reliability throughout the transition.