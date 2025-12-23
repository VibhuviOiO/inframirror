# InfraMirror - System Architecture

## Architecture Overview

InfraMirror follows a **microservices architecture** pattern with centralized data stores and a distributed agent network. The system is designed for high scalability, security, and reliability.

```
┌────────────────────────────────────────────────────────────────────┐
│                          Client Layer                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐            │
│  │ Web Browser  │  │  Mobile App  │  │  CLI Tools   │            │
│  │   (React)    │  │  (Future)    │  │              │            │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘            │
│         │                  │                  │                    │
│         └──────────────────┴──────────────────┘                    │
│                            │                                        │
└────────────────────────────┼────────────────────────────────────────┘
                             │ HTTPS
┌────────────────────────────┼────────────────────────────────────────┐
│                  API Gateway / Load Balancer                       │
│  ┌────────────────────────────────────────────────────────┐       │
│  │  NGINX / Kong / AWS ALB                                 │       │
│  │  - SSL Termination                                      │       │
│  │  - Rate Limiting                                        │       │
│  │  - Request Routing                                      │       │
│  └────────────────────────────────────────────────────────┘       │
└────────────────────────────┼────────────────────────────────────────┘
                             │
┌────────────────────────────┼────────────────────────────────────────┐
│                    Application Layer                               │
│                                                                    │
│  ┌─────────────────────────────────────────────────────────────┐  │
│  │              InfraMirror Backend (Spring Boot)              │  │
│  │  ┌──────────────────┐  ┌──────────────────┐                │  │
│  │  │  Web API Server  │  │  WebSocket Server│                │  │
│  │  │  - REST APIs     │  │  - Real-time     │                │  │
│  │  │  - GraphQL       │  │  - Log streaming │                │  │
│  │  │  - OAuth2/OIDC   │  │  - Metrics       │                │  │
│  │  └──────────────────┘  └──────────────────┘                │  │
│  │                                                             │  │
│  │  ┌──────────────────────────────────────────────────────┐  │  │
│  │  │           Service Layer (Business Logic)             │  │  │
│  │  │  - Infrastructure Service                            │  │  │
│  │  │  - Monitoring Service                                │  │  │
│  │  │  - Application Service                               │  │  │
│  │  │  - Remote Access Service                             │  │  │
│  │  │  - Log Service                                       │  │  │
│  │  │  - Metrics Service                                   │  │  │
│  │  │  - Plugin Service                                    │  │  │
│  │  └──────────────────────────────────────────────────────┘  │  │
│  │                                                             │  │
│  │  ┌──────────────────────────────────────────────────────┐  │  │
│  │  │           Repository Layer (Data Access)             │  │  │
│  │  │  - JPA Repositories                                  │  │  │
│  │  │  - Elasticsearch Repositories                        │  │  │
│  │  │  - Redis Repositories                                │  │  │
│  │  └──────────────────────────────────────────────────────┘  │  │
│  └─────────────────────────────────────────────────────────────┘  │
│                                                                    │
│  ┌─────────────────────────────────────────────────────────────┐  │
│  │              Background Job Processor                       │  │
│  │  - Scheduled Monitoring                                     │  │
│  │  - Data Aggregation                                         │  │
│  │  - Alerting Engine                                          │  │
│  │  - Cleanup Jobs                                             │  │
│  └─────────────────────────────────────────────────────────────┘  │
└────────────────────────────┼────────────────────────────────────────┘
                             │
┌────────────────────────────┼────────────────────────────────────────┐
│                     Data Layer                                     │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐            │
│  │ PostgreSQL   │  │ Elasticsearch│  │    Redis     │            │
│  │              │  │              │  │              │            │
│  │ - Entities   │  │ - Logs       │  │ - Cache      │            │
│  │ - Relations  │  │ - Metrics    │  │ - Sessions   │            │
│  │ - Config     │  │ - Analytics  │  │ - Pub/Sub    │            │
│  └──────────────┘  └──────────────┘  └──────────────┘            │
└────────────────────────────────────────────────────────────────────┘
                             │
┌────────────────────────────┼────────────────────────────────────────┐
│                     Message Queue                                  │
│  ┌─────────────────────────────────────────────────────────────┐  │
│  │                       Apache Kafka                          │  │
│  │  - Agent Heartbeats                                         │  │
│  │  - Log Events                                               │  │
│  │  - Metric Events                                            │  │
│  │  - Alert Events                                             │  │
│  └─────────────────────────────────────────────────────────────┘  │
└────────────────────────────┼────────────────────────────────────────┘
                             │
┌────────────────────────────┼────────────────────────────────────────┐
│                     Security Layer                                 │
│  ┌─────────────────────────────────────────────────────────────┐  │
│  │                       Keycloak                              │  │
│  │  - OAuth2/OIDC                                              │  │
│  │  - User Management                                          │  │
│  │  - SSO                                                      │  │
│  │  - RBAC                                                     │  │
│  └─────────────────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────────────────┘
                             │
                             │ Agent Communication
                             │
┌────────────────────────────┴────────────────────────────────────────┐
│                     Agent Network                                  │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │                    Datacenter 1 (US-East)                    │  │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │  │
│  │  │  Agent-01    │  │  Agent-02    │  │  Agent-03    │      │  │
│  │  │  - Monitor   │  │  - Monitor   │  │  - Monitor   │      │  │
│  │  │  - Collect   │  │  - Collect   │  │  - Collect   │      │  │
│  │  │  - Execute   │  │  - Execute   │  │  - Execute   │      │  │
│  │  └──────────────┘  └──────────────┘  └──────────────┘      │  │
│  │         │                  │                  │              │  │
│  │  ┌──────┴──────────────────┴──────────────────┴──────┐      │  │
│  │  │          Physical Servers / VMs                   │      │  │
│  │  └───────────────────────────────────────────────────┘      │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                                                    │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │                    Datacenter 2 (EU-West)                    │  │
│  │  ┌──────────────┐  ┌──────────────┐                         │  │
│  │  │  Agent-04    │  │  Agent-05    │                         │  │
│  │  │  - Monitor   │  │  - Monitor   │                         │  │
│  │  │  - Collect   │  │  - Collect   │                         │  │
│  │  │  - Execute   │  │  - Execute   │                         │  │
│  │  └──────────────┘  └──────────────┘                         │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                                                    │
│  ... (10 more datacenters)                                        │
└────────────────────────────────────────────────────────────────────┘
```

---

## Component Details

### 1. Frontend Layer (React + TypeScript)

**Technology Stack:**
- React 18+ with TypeScript
- Redux Toolkit for state management
- Material-UI or Ant Design for UI components
- React Router for navigation
- Socket.io-client for WebSocket connections
- Chart.js or Recharts for visualizations
- Axios for HTTP requests

**Key Features:**
- Dashboard with real-time updates
- Infrastructure tree view (Region → Datacenter → Instance)
- Service topology visualization
- Log search and viewer
- Metrics dashboards and charts
- Web-based terminal (xterm.js)
- Configuration management UI
- User/role management

**Structure:**
```
src/
├── components/
│   ├── common/           # Reusable components
│   ├── dashboard/        # Dashboard components
│   ├── infrastructure/   # Infrastructure management
│   ├── monitoring/       # Monitoring views
│   ├── logs/            # Log viewer
│   ├── metrics/         # Metrics dashboards
│   ├── terminal/        # Web terminal
│   └── admin/           # Admin components
├── pages/               # Page components
├── services/            # API client services
├── store/               # Redux store
├── hooks/               # Custom React hooks
├── utils/               # Utility functions
└── types/               # TypeScript types
```

---

### 2. API Gateway Layer

**Options:**
1. **NGINX** (Recommended for simple deployments)
2. **Kong** (Advanced features, plugins)
3. **AWS ALB** (Cloud deployments)

**Responsibilities:**
- SSL/TLS termination
- Load balancing across backend instances
- Rate limiting per API key/user
- Request/response logging
- CORS handling
- Static file serving (React build)
- WebSocket proxy
- Circuit breaker patterns

**NGINX Configuration Example:**
```nginx
upstream backend {
    least_conn;
    server backend-1:8080;
    server backend-2:8080;
    server backend-3:8080;
}

server {
    listen 443 ssl http2;
    server_name inframirror.example.com;
    
    ssl_certificate /etc/nginx/certs/cert.pem;
    ssl_certificate_key /etc/nginx/certs/key.pem;
    
    # Rate limiting
    limit_req_zone $binary_remote_addr zone=api:10m rate=100r/s;
    
    # Static files
    location / {
        root /usr/share/nginx/html;
        try_files $uri $uri/ /index.html;
    }
    
    # API endpoints
    location /api/ {
        limit_req zone=api burst=20;
        proxy_pass http://backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
    
    # WebSocket
    location /ws/ {
        proxy_pass http://backend;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_read_timeout 86400;
    }
}
```

---

### 3. Backend Application Layer (Spring Boot + JHipster)

**Technology Stack:**
- Spring Boot 3.x
- Spring Security with OAuth2/OIDC
- Spring Data JPA for PostgreSQL
- Spring Data Elasticsearch
- Spring Data Redis
- Spring WebSocket
- Spring Kafka
- Liquibase for database migrations
- Micrometer for metrics
- MapStruct for DTO mapping
- Lombok for boilerplate reduction

**Architecture Pattern:**
- **Layered Architecture**: Controller → Service → Repository
- **Domain-Driven Design**: Entities organized by domain
- **DTO Pattern**: Separate DTOs for API requests/responses
- **Repository Pattern**: Data access abstraction

**Package Structure:**
```
com.inframirror/
├── config/               # Configuration classes
│   ├── SecurityConfig.java
│   ├── WebSocketConfig.java
│   ├── ElasticsearchConfig.java
│   ├── KafkaConfig.java
│   └── CacheConfig.java
├── domain/              # Entity classes
│   ├── Region.java
│   ├── Datacenter.java
│   ├── Instance.java
│   ├── Agent.java
│   └── ...
├── repository/          # Data access
│   ├── RegionRepository.java
│   ├── DatacenterRepository.java
│   ├── elasticsearch/
│   └── redis/
├── service/             # Business logic
│   ├── InfrastructureService.java
│   ├── MonitoringService.java
│   ├── AgentService.java
│   ├── MetricsService.java
│   ├── LogService.java
│   └── RemoteAccessService.java
├── web/                 # REST controllers
│   ├── rest/
│   │   ├── RegionResource.java
│   │   ├── DatacenterResource.java
│   │   └── ...
│   └── websocket/
│       ├── MetricsWebSocketHandler.java
│       └── TerminalWebSocketHandler.java
├── security/            # Security components
│   ├── AuthoritiesConstants.java
│   ├── SecurityUtils.java
│   └── ApiKeyAuthenticationFilter.java
├── dto/                 # Data Transfer Objects
├── mapper/              # MapStruct mappers
└── util/                # Utility classes
```

---

### 4. Service Layer Details

#### 4.1 Infrastructure Service

**Responsibilities:**
- Manage regions, datacenters, racks, instances
- Hierarchical queries and navigation
- Capacity planning calculations
- Instance lifecycle management

**Key Methods:**
```java
public interface InfrastructureService {
    // Region management
    List<RegionDTO> getAllRegions();
    RegionDTO createRegion(RegionDTO regionDTO);
    
    // Datacenter management
    List<DatacenterDTO> getDatacentersByRegion(Long regionId);
    DatacenterDTO createDatacenter(DatacenterDTO datacenterDTO);
    
    // Instance management
    Page<InstanceDTO> getInstances(InstanceFilter filter, Pageable pageable);
    InstanceDTO createInstance(InstanceDTO instanceDTO);
    InstanceDTO updateInstance(Long id, InstanceDTO instanceDTO);
    void deleteInstance(Long id);
    
    // Hierarchy navigation
    InfrastructureTreeDTO getInfrastructureTree();
    
    // Capacity planning
    CapacityReportDTO getCapacityReport(Long datacenterId);
}
```

#### 4.2 Monitoring Service

**Responsibilities:**
- Agent registration and heartbeat processing
- Health check execution and scheduling
- Threshold evaluation and alerting
- Hardware metrics collection

**Key Methods:**
```java
public interface MonitoringService {
    // Agent management
    AgentDTO registerAgent(AgentRegistrationDTO registration);
    void processAgentHeartbeat(String agentId, AgentHeartbeatDTO heartbeat);
    
    // Health monitoring
    void scheduleHealthChecks(Long instanceId);
    HealthStatusDTO getInstanceHealth(Long instanceId);
    List<HealthStatusDTO> getUnhealthyInstances();
    
    // Metrics
    void recordPingHeartbeat(PingHeartbeatDTO heartbeat);
    void recordHttpHeartbeat(HttpHeartbeatDTO heartbeat);
    
    // Alerting
    List<AlertDTO> evaluateThresholds();
    void sendAlert(AlertDTO alert);
}
```

#### 4.3 Application Service

**Responsibilities:**
- Application registration and discovery
- Process monitoring
- JVM metrics collection
- Application lifecycle management

**Key Methods:**
```java
public interface ApplicationService {
    // Application management
    ApplicationDTO registerApplication(ApplicationDTO applicationDTO);
    List<ApplicationDTO> getApplicationsByInstance(Long instanceId);
    
    // Process monitoring
    List<ProcessDTO> discoverProcesses(Long instanceId);
    ProcessDTO getProcessDetails(Long processId);
    
    // JVM management
    JvmMetricsDTO collectJvmMetrics(Long applicationId);
    byte[] captureHeapDump(Long applicationId);
    String captureThreadDump(Long applicationId);
    
    // Lifecycle
    void startApplication(Long applicationId);
    void stopApplication(Long applicationId);
    void restartApplication(Long applicationId);
}
```

#### 4.4 Remote Access Service

**Responsibilities:**
- Remote session management
- Command execution
- File operations
- Session recording and audit

**Key Methods:**
```java
public interface RemoteAccessService {
    // Session management
    RemoteSessionDTO createSession(SessionRequestDTO request);
    void closeSession(String sessionId);
    List<RemoteSessionDTO> getActiveSessions();
    
    // Command execution
    CommandResultDTO executeCommand(String sessionId, String command);
    
    // File operations
    List<FileInfoDTO> listFiles(String sessionId, String path);
    byte[] downloadFile(String sessionId, String filePath);
    void uploadFile(String sessionId, String filePath, MultipartFile file);
    
    // Audit
    List<SessionCommandDTO> getSessionHistory(String sessionId);
}
```

#### 4.5 Log Service

**Responsibilities:**
- Log ingestion from agents
- Log parsing and enrichment
- Log search and retrieval
- Log-based alerting

**Key Methods:**
```java
public interface LogService {
    // Ingestion
    void ingestLogs(List<LogEntryDTO> logs);
    
    // Search
    SearchResultDTO<LogEntryDTO> searchLogs(LogSearchCriteria criteria);
    
    // Sources
    LogSourceDTO createLogSource(LogSourceDTO logSource);
    List<LogSourceDTO> getLogSourcesByApplication(Long applicationId);
    
    // Streaming
    Publisher<LogEntryDTO> streamLogs(Long instanceId);
    
    // Alerts
    void createLogAlert(LogAlertDTO alert);
}
```

#### 4.6 Metrics Service

**Responsibilities:**
- Metrics ingestion and storage
- Metrics aggregation and query
- Time-series data management
- Dashboard data preparation

**Key Methods:**
```java
public interface MetricsService {
    // Ingestion
    void ingestMetrics(List<MetricDataDTO> metrics);
    
    // Query
    List<MetricDataDTO> queryMetrics(MetricsQuery query);
    TimeSeriesDTO getTimeSeries(String metricName, TimeRange range);
    
    // Aggregation
    AggregatedMetricsDTO aggregateMetrics(AggregationRequest request);
    
    // Dashboards
    DashboardDataDTO getDashboardData(Long dashboardId);
}
```

---

### 5. Data Layer

#### 5.1 PostgreSQL

**Purpose**: Primary data store for all relational data

**Configuration:**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/inframirror
    username: inframirror
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
  jpa:
    properties:
      hibernate:
        jdbc:
          batch_size: 20
        order_inserts: true
        order_updates: true
        query:
          in_clause_parameter_padding: true
    show-sql: false
```

**Optimization Strategies:**
- Connection pooling (HikariCP)
- Query optimization with proper indexes
- Batch inserts/updates
- Prepared statement caching
- Database partitioning for large tables (heartbeats, audit logs)

#### 5.2 Elasticsearch

**Purpose**: Log storage, metrics storage, full-text search

**Configuration:**
```yaml
spring:
  elasticsearch:
    uris: http://localhost:9200
    username: elastic
    password: ${ES_PASSWORD}
    connection-timeout: 5s
    socket-timeout: 60s
```

**Index Strategy:**
- Time-based indices (logs-2024-12-03, metrics-2024-12-03)
- Index templates for consistent mapping
- Index lifecycle management (ILM) for retention
- Alias for current active index

**ILM Policy Example:**
```json
{
  "policy": {
    "phases": {
      "hot": {
        "min_age": "0ms",
        "actions": {
          "rollover": {
            "max_size": "50gb",
            "max_age": "1d"
          }
        }
      },
      "warm": {
        "min_age": "7d",
        "actions": {
          "readonly": {},
          "shrink": {
            "number_of_shards": 1
          }
        }
      },
      "delete": {
        "min_age": "30d",
        "actions": {
          "delete": {}
        }
      }
    }
  }
}
```

#### 5.3 Redis

**Purpose**: Caching, session storage, pub/sub

**Configuration:**
```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: ${REDIS_PASSWORD}
    lettuce:
      pool:
        max-active: 20
        max-idle: 10
        min-idle: 5
    timeout: 2000ms
  cache:
    type: redis
    redis:
      time-to-live: 600000  # 10 minutes
```

**Cache Strategy:**
- L1 Cache: Caffeine (in-memory, per instance)
- L2 Cache: Redis (shared across instances)
- Cache-aside pattern for most queries
- Write-through for critical data

**Cache Keys:**
```
instances:{id}
instances:datacenter:{datacenterId}
agents:{agentId}
users:{username}
permissions:{userId}
```

#### 5.4 Apache Kafka

**Purpose**: Event streaming, real-time data pipeline

**Configuration:**
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      acks: all
      retries: 3
    consumer:
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      group-id: inframirror
      auto-offset-reset: earliest
```

**Topics:**
```
agent.heartbeats         - Agent heartbeat events
logs.raw                 - Raw log entries from agents
logs.parsed              - Parsed and enriched logs
metrics.raw              - Raw metrics from agents
metrics.aggregated       - Aggregated metrics
alerts.triggered         - Alert events
audit.events             - Audit trail events
```

---

### 6. Agent Architecture

**Agent Components:**
```
InfraMirror Agent/
├── Core Engine
│   ├── Registration & Authentication
│   ├── Heartbeat Manager
│   ├── Command Executor
│   └── Connection Manager
├── Collectors
│   ├── System Metrics Collector
│   ├── Process Collector
│   ├── Log Collector
│   ├── JVM Metrics Collector
│   └── Custom Collectors (Plugins)
├── Streaming
│   ├── Log Streamer
│   ├── Metrics Streamer
│   └── Event Streamer
├── Remote Access
│   ├── SSH Proxy
│   ├── Command Handler
│   └── File Manager
└── Configuration
    ├── Config Loader
    └── Config Watcher
```

**Agent Communication Protocol:**

1. **Registration:**
```json
POST /api/agents/register
{
  "agentId": "uuid",
  "datacenter": "us-east-1",
  "hostname": "agent-01.example.com",
  "version": "1.0.0",
  "capabilities": ["metrics", "logs", "ssh"]
}
```

2. **Heartbeat:**
```json
POST /api/agents/heartbeat
{
  "agentId": "uuid",
  "timestamp": "2024-12-03T10:00:00Z",
  "status": "online",
  "managedInstances": 45,
  "activeSessions": 3
}
```

3. **Metrics Push:**
```json
POST /api/metrics/batch
{
  "agentId": "uuid",
  "metrics": [
    {
      "instanceId": 123,
      "metricName": "cpu.usage",
      "value": 45.2,
      "timestamp": "2024-12-03T10:00:00Z"
    }
  ]
}
```

---

### 7. Security Architecture

#### Authentication Flow

```
┌─────────┐              ┌──────────┐              ┌──────────┐
│  User   │              │ Frontend │              │ Keycloak │
└────┬────┘              └─────┬────┘              └─────┬────┘
     │                         │                         │
     │ 1. Login Request        │                         │
     ├────────────────────────>│                         │
     │                         │ 2. Redirect to Keycloak │
     │                         ├────────────────────────>│
     │                         │                         │
     │ 3. Show Login Page      │                         │
     │<──────────────────────────────────────────────────┤
     │                         │                         │
     │ 4. Enter Credentials    │                         │
     ├───────────────────────────────────────────────────>│
     │                         │                         │
     │ 5. Return Authorization Code                      │
     │<──────────────────────────────────────────────────┤
     │                         │                         │
     │ 6. Exchange for Token   │                         │
     │                         ├────────────────────────>│
     │                         │                         │
     │ 7. Return Access Token  │                         │
     │                         │<────────────────────────┤
     │                         │                         │
     │ 8. Store Token          │                         │
     │<────────────────────────┤                         │
```

#### Authorization

**Roles:**
- `ROLE_ADMIN`: Full system access
- `ROLE_OPERATOR`: Read/write access to infrastructure
- `ROLE_VIEWER`: Read-only access
- `ROLE_DEVELOPER`: Application management access
- `ROLE_AUDITOR`: Audit log access only

**Permissions:**
- Resource-based: `instance:read`, `instance:write`, `instance:delete`
- Datacenter-scoped: Can limit access to specific datacenters
- Instance-level: Fine-grained control

**Spring Security Configuration:**
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            )
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/**").authenticated()
            )
            .addFilterBefore(apiKeyAuthenticationFilter(), 
                             UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

---

### 8. Monitoring & Observability

**Platform Monitoring Stack:**
- **Metrics**: Prometheus + Grafana
- **Logs**: Elasticsearch + Kibana
- **Tracing**: Jaeger (optional)
- **Alerts**: AlertManager

**Exposed Metrics:**
```
# Application metrics
http_requests_total
http_request_duration_seconds
database_query_duration_seconds
cache_hits_total
cache_misses_total

# Business metrics
agents_online_count
instances_monitored_count
alerts_triggered_total
remote_sessions_active_count

# Infrastructure metrics
jvm_memory_used_bytes
jvm_gc_pause_seconds
database_connections_active
elasticsearch_requests_total
```

**Health Checks:**
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,info,prometheus
  health:
    livenessState:
      enabled: true
    readinessState:
      enabled: true
  endpoint:
    health:
      show-details: always
```

---

### 9. Deployment Architecture

#### Development Environment

**Starting Backend Services:**
```bash
# Start all backend services (PostgreSQL, Redis, Keycloak, Elasticsearch)
docker compose -f docker/services.yml up -d

# Start Spring Boot application
./mvnw
```

**Services in docker/services.yml:**
- PostgreSQL (Database)
- Redis (Caching)
- Keycloak (Authentication)
- Elasticsearch (Logs & Search)


### 10. Scalability Considerations

**Horizontal Scaling:**
- Backend: Stateless design, scale to N instances
- Agents: Distributed across datacenters
- Elasticsearch: Cluster with multiple nodes
- PostgreSQL: Read replicas for read-heavy workloads

**Vertical Scaling:**
- Increase JVM heap for backend
- More CPU/memory for Elasticsearch
- Larger database instance

**Data Partitioning:**
- Partition heartbeat tables by time
- Separate hot/cold data in Elasticsearch
- Archive old audit logs

**Caching Strategy:**
- Cache frequently accessed data (instances, datacenters)
- Invalidate cache on updates
- Use Redis for distributed caching

---

## Conclusion

This architecture provides:
- **Scalability**: Handle 10,000+ instances across 12+ datacenters
- **Reliability**: No single point of failure, HA setup
- **Security**: End-to-end encryption, comprehensive audit
- **Performance**: Optimized queries, caching, async processing
- **Extensibility**: Plugin system for custom integrations
- **Observability**: Complete monitoring and logging

The modular design allows for incremental implementation and easy maintenance.
