# InfraMirror - Data Model & Entity Design

## Overview

This document provides a comprehensive data model for InfraMirror based on the analysis of existing Liquibase schemas and the complete requirements. The data model is organized into logical domains for better understanding and maintainability.

## Entity Relationship Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                    INFRASTRUCTURE HIERARCHY                         │
└─────────────────────────────────────────────────────────────────────┘
                            Region
                              │
                              │ 1:N
                              ▼
                         Datacenter ──────┐
                              │           │
                    ┌─────────┼───────────┼────────┐
                    │ 1:N     │ 1:N       │ 1:N    │ 1:N
                    ▼         ▼           ▼        ▼
                 Agent    Instance    Service   Cluster
                    │         │           │
                    │         │           │
                    └─────────┴───────────┘
                              │
                    ┌─────────┼─────────┐
                    │         │         │
                    ▼         ▼         ▼
              Application  Metrics   Logs
```

---

## 1. Infrastructure Domain

### 1.1 Region

**Purpose**: Geographical grouping of datacenters (e.g., US-East, EU-West, Asia-Pacific)

**Liquibase**: `20251025202838_added_entity_Region.xml`

```sql
CREATE TABLE regions (
    id BIGINT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    region_code VARCHAR(20),
    group_name VARCHAR(20),
    description TEXT,
    timezone VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_regions_code ON regions(region_code);
CREATE INDEX idx_regions_group ON regions(group_name);
```

**Fields:**
- `id`: Primary key
- `name`: Human-readable name (e.g., "US East Coast")
- `region_code`: Short code (e.g., "us-east", "eu-west")
- `group_name`: Optional grouping (e.g., "Americas", "EMEA", "APAC")
- `description`: Additional details
- `timezone`: Primary timezone for the region (e.g., "America/New_York")

**JHipster Entity Definition (JDL):**
```jdl
entity Region {
    name String required maxlength(50) unique
    regionCode String maxlength(20)
    groupName String maxlength(20)
    description TextBlob
    timezone String maxlength(50)
}
```

### 1.2 Datacenter

**Purpose**: Physical datacenter location

**Liquibase**: `20251025202839_added_entity_Datacenter.xml`

```sql
CREATE TABLE datacenters (
    id BIGINT PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL,
    region_id BIGINT REFERENCES regions(id) ON DELETE SET NULL,
    
    -- Location Information
    location VARCHAR(200),
    address TEXT,
    city VARCHAR(100),
    country VARCHAR(50),
    postal_code VARCHAR(20),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    
    -- Network Configuration
    private_cidr VARCHAR(50),
    public_cidr VARCHAR(50),
    dns_servers TEXT[], -- Array of DNS server IPs
    ntp_servers TEXT[], -- Array of NTP server IPs
    
    -- Capacity Information
    total_rack_count INTEGER DEFAULT 0,
    used_rack_count INTEGER DEFAULT 0,
    total_power_capacity_kw INTEGER,
    
    -- Status
    status VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, MAINTENANCE, OFFLINE
    
    -- Metadata
    tags JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_datacenters_region ON datacenters(region_id);
CREATE INDEX idx_datacenters_code ON datacenters(code);
CREATE INDEX idx_datacenters_status ON datacenters(status);
```

**JDL:**
```jdl
entity Datacenter {
    code String required maxlength(10) unique
    name String required maxlength(50)
    location String maxlength(200)
    address TextBlob
    city String maxlength(100)
    country String maxlength(50)
    postalCode String maxlength(20)
    latitude Double
    longitude Double
    privateCidr String maxlength(50)
    publicCidr String maxlength(50)
    totalRackCount Integer
    usedRackCount Integer
    totalPowerCapacityKw Integer
    status DatacenterStatus
    tags TextBlob
}

enum DatacenterStatus {
    ACTIVE, MAINTENANCE, OFFLINE
}

relationship ManyToOne {
    Datacenter{region} to Region
}
```

### 1.3 Rack

**Purpose**: Physical rack within datacenter (NEW entity)

```sql
CREATE TABLE racks (
    id BIGINT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    rack_number VARCHAR(20) NOT NULL,
    datacenter_id BIGINT NOT NULL REFERENCES datacenters(id) ON DELETE CASCADE,
    
    -- Location
    row_number VARCHAR(10),
    position VARCHAR(10),
    
    -- Capacity
    total_units INTEGER DEFAULT 42, -- Standard rack is 42U
    used_units INTEGER DEFAULT 0,
    
    -- Power
    power_capacity_watts INTEGER,
    power_usage_watts INTEGER,
    
    -- Status
    status VARCHAR(20) DEFAULT 'ACTIVE',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(datacenter_id, rack_number)
);

CREATE INDEX idx_racks_datacenter ON racks(datacenter_id);
```

**JDL:**
```jdl
entity Rack {
    name String required maxlength(50)
    rackNumber String required maxlength(20)
    rowNumber String maxlength(10)
    position String maxlength(10)
    totalUnits Integer
    usedUnits Integer
    powerCapacityWatts Integer
    powerUsageWatts Integer
    status RackStatus
}

enum RackStatus {
    ACTIVE, MAINTENANCE, FULL, OFFLINE
}

relationship ManyToOne {
    Rack{datacenter required} to Datacenter
}
```

### 1.4 Instance

**Purpose**: Represents any compute resource (physical server, VM, container)

**Liquibase**: `20251114000001_added_entity_Instance.xml`

```sql
CREATE TABLE instances (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    hostname VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(500),
    
    -- Classification
    instance_type VARCHAR(50) NOT NULL, -- PHYSICAL, VM, CONTAINER
    monitoring_type VARCHAR(50) NOT NULL, -- FULL, BASIC, CUSTOM, NONE
    
    -- Relationships
    datacenter_id BIGINT NOT NULL REFERENCES datacenters(id) ON DELETE RESTRICT,
    agent_id BIGINT REFERENCES agents(id) ON DELETE SET NULL,
    rack_id BIGINT REFERENCES racks(id) ON DELETE SET NULL,
    
    -- Network Information
    private_ip_address VARCHAR(50),
    public_ip_address VARCHAR(50),
    mac_address VARCHAR(17),
    fqdn VARCHAR(255),
    
    -- System Information
    operating_system VARCHAR(100),
    os_version VARCHAR(50),
    platform VARCHAR(100),
    architecture VARCHAR(50), -- x86_64, arm64, etc.
    kernel_version VARCHAR(100),
    
    -- Hardware Information (for physical/VM)
    cpu_model VARCHAR(200),
    cpu_cores INTEGER,
    cpu_threads INTEGER,
    memory_total_mb BIGINT,
    disk_total_gb BIGINT,
    
    -- Hypervisor Information (for VMs)
    hypervisor_type VARCHAR(50), -- VMWARE, KVM, HYPERV, XEN
    hypervisor_host VARCHAR(255),
    
    -- Container Information
    container_runtime VARCHAR(50), -- DOCKER, CONTAINERD, PODMAN
    container_id VARCHAR(100),
    image_name VARCHAR(255),
    image_tag VARCHAR(100),
    
    -- Monitoring Configuration
    ping_enabled BOOLEAN DEFAULT TRUE,
    ping_interval INTEGER DEFAULT 30,
    ping_timeout_ms INTEGER DEFAULT 3000,
    ping_retry_count INTEGER DEFAULT 2,
    
    hardware_monitoring_enabled BOOLEAN DEFAULT FALSE,
    hardware_monitoring_interval INTEGER DEFAULT 300,
    
    -- Threshold Configuration
    cpu_warning_threshold INTEGER DEFAULT 70,
    cpu_danger_threshold INTEGER DEFAULT 90,
    memory_warning_threshold INTEGER DEFAULT 75,
    memory_danger_threshold INTEGER DEFAULT 90,
    disk_warning_threshold INTEGER DEFAULT 80,
    disk_danger_threshold INTEGER DEFAULT 95,
    
    -- Status
    status VARCHAR(20) DEFAULT 'UNKNOWN', -- RUNNING, STOPPED, ERROR, UNKNOWN
    health_status VARCHAR(20) DEFAULT 'UNKNOWN', -- HEALTHY, WARNING, CRITICAL, UNKNOWN
    
    -- Timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_ping_at TIMESTAMP,
    last_hardware_check_at TIMESTAMP,
    last_seen_at TIMESTAMP,
    
    -- Metadata
    tags JSONB,
    metadata JSONB
);

CREATE INDEX idx_instances_datacenter ON instances(datacenter_id);
CREATE INDEX idx_instances_agent ON instances(agent_id);
CREATE INDEX idx_instances_hostname ON instances(hostname);
CREATE INDEX idx_instances_type ON instances(instance_type);
CREATE INDEX idx_instances_status ON instances(status);
CREATE INDEX idx_instances_ip ON instances(private_ip_address);
CREATE INDEX idx_instances_tags ON instances USING gin(tags);
```

**JDL:**
```jdl
entity Instance {
    name String required maxlength(255)
    hostname String required maxlength(255) unique
    description String maxlength(500)
    instanceType InstanceType required
    monitoringType MonitoringType required
    privateIpAddress String maxlength(50)
    publicIpAddress String maxlength(50)
    macAddress String maxlength(17)
    fqdn String maxlength(255)
    operatingSystem String maxlength(100)
    osVersion String maxlength(50)
    platform String maxlength(100)
    architecture String maxlength(50)
    kernelVersion String maxlength(100)
    cpuModel String maxlength(200)
    cpuCores Integer
    cpuThreads Integer
    memoryTotalMb Long
    diskTotalGb Long
    hypervisorType String maxlength(50)
    hypervisorHost String maxlength(255)
    containerRuntime String maxlength(50)
    containerId String maxlength(100)
    imageName String maxlength(255)
    imageTag String maxlength(100)
    pingEnabled Boolean
    pingInterval Integer
    pingTimeoutMs Integer
    pingRetryCount Integer
    hardwareMonitoringEnabled Boolean
    hardwareMonitoringInterval Integer
    cpuWarningThreshold Integer
    cpuDangerThreshold Integer
    memoryWarningThreshold Integer
    memoryDangerThreshold Integer
    diskWarningThreshold Integer
    diskDangerThreshold Integer
    status InstanceStatus
    healthStatus HealthStatus
    lastPingAt Instant
    lastHardwareCheckAt Instant
    lastSeenAt Instant
    tags TextBlob
    metadata TextBlob
}

enum InstanceType {
    PHYSICAL, VM, CONTAINER
}

enum MonitoringType {
    FULL, BASIC, CUSTOM, NONE
}

enum InstanceStatus {
    RUNNING, STOPPED, ERROR, UNKNOWN
}

enum HealthStatus {
    HEALTHY, WARNING, CRITICAL, UNKNOWN
}

relationship ManyToOne {
    Instance{datacenter required} to Datacenter
    Instance{agent} to Agent
    Instance{rack} to Rack
}
```

---

## 2. Agent & Monitoring Domain

### 2.1 Agent

**Purpose**: Software agents deployed in datacenters for monitoring and management

**Liquibase**: `20251025202840_added_entity_Agent.xml`

```sql
CREATE TABLE agents (
    id BIGINT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    agent_id VARCHAR(100) NOT NULL UNIQUE, -- UUID for agent identification
    datacenter_id BIGINT REFERENCES datacenters(id) ON DELETE SET NULL,
    
    -- Agent Information
    version VARCHAR(20),
    hostname VARCHAR(255),
    ip_address VARCHAR(50),
    platform VARCHAR(50), -- LINUX, WINDOWS, MACOS
    
    -- Capabilities
    capabilities TEXT[], -- SSH, METRICS, LOGS, COMMANDS, etc.
    
    -- Status
    status VARCHAR(20) DEFAULT 'OFFLINE', -- ONLINE, OFFLINE, ERROR, STARTING
    last_heartbeat TIMESTAMP,
    heartbeat_interval INTEGER DEFAULT 30,
    
    -- Configuration
    config JSONB,
    
    -- Statistics
    managed_instances_count INTEGER DEFAULT 0,
    active_sessions_count INTEGER DEFAULT 0,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    registered_at TIMESTAMP,
    
    CONSTRAINT chk_agent_heartbeat CHECK (heartbeat_interval >= 10 AND heartbeat_interval <= 300)
);

CREATE INDEX idx_agents_datacenter ON agents(datacenter_id);
CREATE INDEX idx_agents_agent_id ON agents(agent_id);
CREATE INDEX idx_agents_status ON agents(status);
CREATE INDEX idx_agents_last_heartbeat ON agents(last_heartbeat);
```

**JDL:**
```jdl
entity Agent {
    name String required maxlength(50)
    agentId String required maxlength(100) unique
    version String maxlength(20)
    hostname String maxlength(255)
    ipAddress String maxlength(50)
    platform AgentPlatform
    status AgentStatus
    lastHeartbeat Instant
    heartbeatInterval Integer
    config TextBlob
    managedInstancesCount Integer
    activeSessionsCount Integer
    registeredAt Instant
}

enum AgentPlatform {
    LINUX, WINDOWS, MACOS, BSD
}

enum AgentStatus {
    ONLINE, OFFLINE, ERROR, STARTING
}

relationship ManyToOne {
    Agent{datacenter} to Datacenter
}
```

### 2.2 Schedule

**Purpose**: Define monitoring schedules and intervals

**Liquibase**: `20251025202836_added_entity_Schedule.xml`

```sql
CREATE TABLE schedules (
    id BIGINT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    
    -- Schedule Configuration
    interval INTEGER NOT NULL, -- In seconds
    cron_expression VARCHAR(100),
    timezone VARCHAR(50) DEFAULT 'UTC',
    
    -- Monitoring Settings
    include_response_body BOOLEAN DEFAULT FALSE,
    thresholds_warning INTEGER,
    thresholds_critical INTEGER,
    
    -- State
    enabled BOOLEAN DEFAULT TRUE,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_schedule_interval CHECK (interval >= 10)
);
```

### 2.3 HttpMonitor

**Purpose**: HTTP/HTTPS endpoint monitoring

**Liquibase**: `20251025202837_added_entity_HttpMonitor.xml`

```sql
CREATE TABLE api_monitors (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    
    -- HTTP Configuration
    method VARCHAR(10) NOT NULL, -- GET, POST, PUT, DELETE, etc.
    type VARCHAR(10) NOT NULL, -- HTTP, HTTPS
    url TEXT NOT NULL,
    headers TEXT, -- JSON string
    body TEXT,
    
    -- Schedule
    schedule_id BIGINT REFERENCES schedules(id) ON DELETE SET NULL,
    
    -- Validation
    expected_status_code INTEGER DEFAULT 200,
    expected_response_pattern TEXT,
    
    -- Timeout
    timeout_ms INTEGER DEFAULT 5000,
    
    -- State
    enabled BOOLEAN DEFAULT TRUE,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_api_monitors_schedule ON api_monitors(schedule_id);
CREATE INDEX idx_api_monitors_enabled ON api_monitors(enabled);
```

### 2.4 HttpHeartbeat

**Purpose**: Store HTTP monitoring results

**Liquibase**: `20251025202841_added_entity_HttpHeartbeat.xml`

```sql
CREATE TABLE api_heartbeats (
    id BIGINT PRIMARY KEY,
    monitor_id BIGINT REFERENCES api_monitors(id) ON DELETE CASCADE,
    agent_id BIGINT REFERENCES agents(id) ON DELETE SET NULL,
    
    executed_at TIMESTAMP NOT NULL,
    
    -- Result
    success BOOLEAN,
    
    -- Timing Metrics
    response_time_ms INTEGER,
    dns_lookup_ms INTEGER,
    tcp_connect_ms INTEGER,
    tls_handshake_ms INTEGER,
    time_to_first_byte_ms INTEGER,
    
    -- Response Information
    response_status_code INTEGER,
    response_size_bytes INTEGER,
    response_content_type VARCHAR(50),
    response_server VARCHAR(50),
    response_cache_status VARCHAR(50),
    
    -- Thresholds
    warning_threshold_ms INTEGER,
    critical_threshold_ms INTEGER,
    
    -- Error Information
    error_type VARCHAR(50),
    error_message TEXT,
    
    -- Raw Data (optional)
    raw_request_headers TEXT,
    raw_response_headers TEXT,
    raw_response_body TEXT
);

CREATE INDEX idx_api_heartbeats_monitor ON api_heartbeats(monitor_id);
CREATE INDEX idx_api_heartbeats_executed_at ON api_heartbeats(executed_at);
CREATE INDEX idx_api_heartbeats_success ON api_heartbeats(success);

-- Partition by month for better performance
-- ALTER TABLE api_heartbeats PARTITION BY RANGE (executed_at);
```

### 2.5 PingHeartbeat

**Purpose**: Instance health and hardware monitoring results

**Liquibase**: `20251114000003_added_entity_PingHeartbeat.xml`

```sql
CREATE TABLE ping_heartbeats (
    id BIGINT PRIMARY KEY,
    instance_id BIGINT NOT NULL REFERENCES instances(id) ON DELETE CASCADE,
    agent_id BIGINT REFERENCES agents(id) ON DELETE SET NULL,
    
    executed_at TIMESTAMP NOT NULL,
    heartbeat_type VARCHAR(20) NOT NULL, -- PING, HARDWARE, COMBINED
    
    -- Result
    success BOOLEAN NOT NULL,
    status VARCHAR(20) NOT NULL, -- UP, DOWN, DEGRADED, TIMEOUT
    
    -- Ping Metrics
    response_time_ms INTEGER,
    packet_loss FLOAT,
    jitter_ms INTEGER,
    
    -- Hardware Metrics
    cpu_usage FLOAT, -- Percentage
    memory_usage FLOAT, -- Percentage
    disk_usage FLOAT, -- Percentage
    load_average FLOAT,
    process_count INTEGER,
    
    -- Network Metrics
    network_rx_bytes BIGINT,
    network_tx_bytes BIGINT,
    
    -- System Metrics
    uptime_seconds BIGINT,
    
    -- Error Information
    error_type VARCHAR(100),
    error_message TEXT,
    
    -- Metadata
    metadata JSONB
);

CREATE INDEX idx_ping_heartbeats_instance ON ping_heartbeats(instance_id);
CREATE INDEX idx_ping_heartbeats_executed_at ON ping_heartbeats(executed_at);
CREATE INDEX idx_ping_heartbeats_type ON ping_heartbeats(heartbeat_type);
CREATE INDEX idx_ping_heartbeats_status ON ping_heartbeats(status);

-- Partition for performance
-- ALTER TABLE ping_heartbeats PARTITION BY RANGE (executed_at);
```

---

## 3. Application Management Domain

### 3.1 Application

**Purpose**: Applications deployed on instances

```sql
CREATE TABLE applications (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    application_type VARCHAR(50) NOT NULL, -- JAVA, NODEJS, PYTHON, GO, DOTNET, etc.
    version VARCHAR(50),
    
    -- Deployment
    instance_id BIGINT NOT NULL REFERENCES instances(id) ON DELETE CASCADE,
    deployment_path VARCHAR(500),
    config_path VARCHAR(500),
    
    -- Process Information
    process_name VARCHAR(200),
    process_command TEXT,
    working_directory VARCHAR(500),
    environment_variables JSONB,
    
    -- Java/JVM Specific
    jvm_enabled BOOLEAN DEFAULT FALSE,
    jmx_enabled BOOLEAN DEFAULT FALSE,
    jmx_host VARCHAR(255),
    jmx_port INTEGER,
    jmx_username VARCHAR(100),
    jmx_password_encrypted VARCHAR(500),
    heap_size_mb INTEGER,
    heap_max_mb INTEGER,
    gc_type VARCHAR(50),
    
    -- Monitoring Configuration
    log_monitoring_enabled BOOLEAN DEFAULT TRUE,
    log_file_paths TEXT[], -- Array of log file paths
    log_parser_type VARCHAR(50), -- JSON, SYSLOG, CUSTOM
    
    metrics_enabled BOOLEAN DEFAULT TRUE,
    metrics_port INTEGER,
    metrics_path VARCHAR(200),
    
    health_check_enabled BOOLEAN DEFAULT TRUE,
    health_check_url VARCHAR(500),
    health_check_interval INTEGER DEFAULT 60,
    health_check_timeout_ms INTEGER DEFAULT 5000,
    
    -- Status
    status VARCHAR(20) DEFAULT 'UNKNOWN', -- RUNNING, STOPPED, STARTING, ERROR, UNKNOWN
    health_status VARCHAR(20) DEFAULT 'UNKNOWN',
    last_health_check TIMESTAMP,
    last_restart TIMESTAMP,
    
    -- Metadata
    tags JSONB,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deployed_at TIMESTAMP
);

CREATE INDEX idx_applications_instance ON applications(instance_id);
CREATE INDEX idx_applications_type ON applications(application_type);
CREATE INDEX idx_applications_status ON applications(status);
CREATE INDEX idx_applications_name ON applications(name);
```

**JDL:**
```jdl
entity Application {
    name String required maxlength(100)
    description String maxlength(500)
    applicationType ApplicationType required
    version String maxlength(50)
    deploymentPath String maxlength(500)
    configPath String maxlength(500)
    processName String maxlength(200)
    processCommand TextBlob
    workingDirectory String maxlength(500)
    environmentVariables TextBlob
    jvmEnabled Boolean
    jmxEnabled Boolean
    jmxHost String maxlength(255)
    jmxPort Integer
    heapSizeMb Integer
    heapMaxMb Integer
    gcType String maxlength(50)
    logMonitoringEnabled Boolean
    logParserType String maxlength(50)
    metricsEnabled Boolean
    metricsPort Integer
    metricsPath String maxlength(200)
    healthCheckEnabled Boolean
    healthCheckUrl String maxlength(500)
    healthCheckInterval Integer
    healthCheckTimeoutMs Integer
    status ApplicationStatus
    healthStatus HealthStatus
    lastHealthCheck Instant
    lastRestart Instant
    deployedAt Instant
    tags TextBlob
}

enum ApplicationType {
    JAVA, NODEJS, PYTHON, GO, DOTNET, RUBY, PHP, OTHER
}

enum ApplicationStatus {
    RUNNING, STOPPED, STARTING, STOPPING, ERROR, UNKNOWN
}

relationship ManyToOne {
    Application{instance required} to Instance
}
```

### 3.2 ApplicationProcess

**Purpose**: Track running processes for applications

```sql
CREATE TABLE application_processes (
    id BIGINT PRIMARY KEY,
    application_id BIGINT NOT NULL REFERENCES applications(id) ON DELETE CASCADE,
    
    -- Process Information
    pid INTEGER NOT NULL,
    parent_pid INTEGER,
    process_name VARCHAR(200),
    command_line TEXT,
    username VARCHAR(100),
    
    -- Resource Usage
    cpu_percent FLOAT,
    memory_mb BIGINT,
    memory_percent FLOAT,
    threads INTEGER,
    file_descriptors INTEGER,
    
    -- Timing
    start_time TIMESTAMP,
    cpu_time_seconds BIGINT,
    
    -- Status
    status VARCHAR(20), -- RUNNING, SLEEPING, ZOMBIE, STOPPED
    
    -- Timestamps
    discovered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_seen_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(application_id, pid)
);

CREATE INDEX idx_app_processes_application ON application_processes(application_id);
CREATE INDEX idx_app_processes_pid ON application_processes(pid);
CREATE INDEX idx_app_processes_status ON application_processes(status);
```

---

## 4. Distributed Services Domain

### 4.1 Service

**Purpose**: Distributed systems and clustered services

```sql
CREATE TABLE services (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    service_type VARCHAR(50) NOT NULL, 
    -- KAFKA, ELASTICSEARCH, MONGODB, CASSANDRA, REDIS, POSTGRESQL, 
    -- MYSQL, RABBITMQ, KONG, NGINX, HAPROXY, etc.
    version VARCHAR(50),
    
    datacenter_id BIGINT NOT NULL REFERENCES datacenters(id) ON DELETE RESTRICT,
    cluster_id BIGINT REFERENCES clusters(id) ON DELETE SET NULL,
    
    -- Cluster Information
    cluster_name VARCHAR(100),
    cluster_role VARCHAR(50), -- MASTER, SLAVE, COORDINATOR, WORKER, BROKER, etc.
    node_id VARCHAR(100),
    
    -- Connection Information
    connection_host VARCHAR(255),
    connection_port INTEGER,
    connection_protocol VARCHAR(20), -- HTTP, HTTPS, TCP, gRPC, etc.
    connection_url TEXT,
    
    -- Authentication
    auth_required BOOLEAN DEFAULT FALSE,
    auth_type VARCHAR(50), -- BASIC, TOKEN, CERTIFICATE, SASL, LDAP
    auth_username VARCHAR(200),
    auth_password_encrypted VARCHAR(500),
    
    -- TLS Configuration
    tls_enabled BOOLEAN DEFAULT FALSE,
    tls_cert_path VARCHAR(500),
    tls_key_path VARCHAR(500),
    tls_ca_path VARCHAR(500),
    
    -- Monitoring
    monitoring_enabled BOOLEAN DEFAULT TRUE,
    monitoring_endpoint VARCHAR(500),
    health_check_endpoint VARCHAR(500),
    metrics_endpoint VARCHAR(500),
    
    -- Status
    status VARCHAR(20) DEFAULT 'UNKNOWN', -- RUNNING, STOPPED, ERROR, UNKNOWN
    health_status VARCHAR(20) DEFAULT 'UNKNOWN',
    last_health_check TIMESTAMP,
    
    -- Configuration
    config_data JSONB,
    
    -- Metadata
    tags JSONB,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_services_datacenter ON services(datacenter_id);
CREATE INDEX idx_services_cluster ON services(cluster_id);
CREATE INDEX idx_services_type ON services(service_type);
CREATE INDEX idx_services_status ON services(status);
CREATE INDEX idx_services_name ON services(name);
```

### 4.2 Cluster

**Purpose**: Group related services into clusters

```sql
CREATE TABLE clusters (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    cluster_type VARCHAR(50) NOT NULL, 
    -- KAFKA_CLUSTER, ES_CLUSTER, MONGODB_REPLICA_SET, CASSANDRA_RING, 
    -- K8S_CLUSTER, DOCKER_SWARM, etc.
    
    datacenter_id BIGINT NOT NULL REFERENCES datacenters(id) ON DELETE RESTRICT,
    
    -- Cluster Information
    version VARCHAR(50),
    node_count INTEGER DEFAULT 0,
    
    -- Configuration
    config JSONB,
    
    -- Status
    status VARCHAR(20) DEFAULT 'UNKNOWN',
    health_status VARCHAR(20) DEFAULT 'UNKNOWN',
    
    -- Metadata
    description TEXT,
    tags JSONB,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_clusters_datacenter ON clusters(datacenter_id);
CREATE INDEX idx_clusters_type ON clusters(cluster_type);
```

### 4.3 ServiceInstance

**Purpose**: Link services to physical instances

```sql
CREATE TABLE service_instances (
    id BIGINT PRIMARY KEY,
    service_id BIGINT NOT NULL REFERENCES services(id) ON DELETE CASCADE,
    instance_id BIGINT NOT NULL REFERENCES instances(id) ON DELETE CASCADE,
    
    -- Role in service
    role VARCHAR(50), -- PRIMARY, SECONDARY, ARBITER, etc.
    
    -- Status
    status VARCHAR(20) DEFAULT 'ACTIVE',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(service_id, instance_id)
);

CREATE INDEX idx_service_instances_service ON service_instances(service_id);
CREATE INDEX idx_service_instances_instance ON service_instances(instance_id);
```

---

## 5. Security & Access Control Domain

### 5.1 ApiKey

**Purpose**: API keys for programmatic access (agents, external systems)

**Liquibase**: `20251109000001_added_entity_ApiKey.xml`

```sql
CREATE TABLE api_keys (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    
    -- Key Information
    key_hash VARCHAR(500) NOT NULL UNIQUE, -- BCrypt/SHA256 hash
    key_prefix VARCHAR(20) NOT NULL, -- First few chars for identification (e.g., "sk_live_abc...")
    
    -- Status
    active BOOLEAN DEFAULT TRUE,
    revoked BOOLEAN DEFAULT FALSE,
    revoked_at TIMESTAMP,
    revoked_by VARCHAR(100),
    revoke_reason TEXT,
    
    -- Permissions
    permissions JSONB, -- Array of permission strings
    scope VARCHAR(50) DEFAULT 'FULL', -- FULL, DATACENTER, READ_ONLY
    
    -- Datacenter Access (for scoped keys)
    datacenter_access BIGINT[], -- Array of datacenter IDs, NULL = all
    
    -- Rate Limiting
    rate_limit_per_minute INTEGER DEFAULT 1000,
    
    -- Usage Tracking
    last_used_date TIMESTAMP,
    last_used_ip VARCHAR(50),
    usage_count BIGINT DEFAULT 0,
    
    -- Expiration
    expires_at TIMESTAMP,
    
    -- Audit
    created_by VARCHAR(50) NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_by VARCHAR(50),
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_api_keys_hash ON api_keys(key_hash);
CREATE INDEX idx_api_keys_prefix ON api_keys(key_prefix);
CREATE INDEX idx_api_keys_active ON api_keys(active);
CREATE INDEX idx_api_keys_expires ON api_keys(expires_at);
```

### 5.2 RemoteSession

**Purpose**: Track remote access sessions (SSH, command execution, log streaming)

```sql
CREATE TABLE remote_sessions (
    id BIGINT PRIMARY KEY,
    session_id VARCHAR(100) NOT NULL UNIQUE,
    session_type VARCHAR(50) NOT NULL, -- SSH, EXEC, LOG_STREAM, FILE_BROWSER
    
    -- Target
    instance_id BIGINT NOT NULL REFERENCES instances(id) ON DELETE CASCADE,
    application_id BIGINT REFERENCES applications(id) ON DELETE SET NULL,
    
    -- User Information
    user_id VARCHAR(100) NOT NULL,
    username VARCHAR(100),
    user_email VARCHAR(255),
    
    -- Connection Details
    client_ip VARCHAR(50),
    client_port INTEGER,
    user_agent VARCHAR(500),
    
    -- Status
    status VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, CLOSED, EXPIRED, ERROR
    
    -- Timing
    started_at TIMESTAMP NOT NULL,
    last_activity TIMESTAMP,
    ended_at TIMESTAMP,
    duration_seconds INTEGER,
    
    -- Session Details
    terminal_type VARCHAR(50),
    terminal_size VARCHAR(20),
    
    -- Audit Trail
    commands_executed TEXT[], -- Array of commands
    files_accessed TEXT[], -- Array of file paths
    data_uploaded_bytes BIGINT DEFAULT 0,
    data_downloaded_bytes BIGINT DEFAULT 0,
    
    -- Recording
    session_recorded BOOLEAN DEFAULT TRUE,
    recording_path VARCHAR(500),
    
    -- Metadata
    metadata JSONB
);

CREATE INDEX idx_remote_sessions_session_id ON remote_sessions(session_id);
CREATE INDEX idx_remote_sessions_instance ON remote_sessions(instance_id);
CREATE INDEX idx_remote_sessions_user ON remote_sessions(user_id);
CREATE INDEX idx_remote_sessions_status ON remote_sessions(status);
CREATE INDEX idx_remote_sessions_started ON remote_sessions(started_at);
```

### 5.3 SessionCommand

**Purpose**: Detailed audit of commands executed in remote sessions

```sql
CREATE TABLE session_commands (
    id BIGINT PRIMARY KEY,
    session_id BIGINT NOT NULL REFERENCES remote_sessions(id) ON DELETE CASCADE,
    
    -- Command Information
    command TEXT NOT NULL,
    arguments TEXT,
    working_directory VARCHAR(500),
    
    -- Execution
    executed_at TIMESTAMP NOT NULL,
    duration_ms INTEGER,
    exit_code INTEGER,
    
    -- Output (can be large, consider archival strategy)
    stdout TEXT,
    stderr TEXT,
    output_truncated BOOLEAN DEFAULT FALSE,
    
    -- Status
    status VARCHAR(20), -- SUCCESS, FAILED, TIMEOUT, KILLED
    
    -- Metadata
    environment JSONB
);

CREATE INDEX idx_session_commands_session ON session_commands(session_id);
CREATE INDEX idx_session_commands_executed ON session_commands(executed_at);
```

### 5.4 RolePermission

**Purpose**: Fine-grained RBAC (beyond Keycloak roles)

```sql
CREATE TABLE role_permissions (
    id BIGINT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL,
    
    -- Resource
    resource_type VARCHAR(50) NOT NULL, -- INSTANCE, APPLICATION, SERVICE, etc.
    resource_id BIGINT, -- NULL means all resources of this type
    
    -- Action
    action VARCHAR(50) NOT NULL, -- READ, WRITE, DELETE, EXECUTE, ADMIN
    
    -- Scope
    datacenter_scope BIGINT[], -- NULL means all datacenters
    
    -- Constraints
    constraints JSONB, -- Additional constraints (time-based, IP-based, etc.)
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(role_name, resource_type, resource_id, action)
);

CREATE INDEX idx_role_permissions_role ON role_permissions(role_name);
CREATE INDEX idx_role_permissions_resource ON role_permissions(resource_type, resource_id);
```

---

## 6. Audit & Logging Domain

### 6.1 AuditLog

**Purpose**: Comprehensive audit trail of all system changes

**Liquibase**: `20251025202842_added_entity_AuditLog.xml`

```sql
CREATE TABLE audit_logs (
    id BIGINT PRIMARY KEY,
    
    -- Event Information
    event_type VARCHAR(100) NOT NULL, -- CREATE, UPDATE, DELETE, LOGIN, ACCESS, etc.
    action VARCHAR(100) NOT NULL,
    description TEXT,
    
    -- Entity Information
    entity_type VARCHAR(100),
    entity_id BIGINT,
    entity_name VARCHAR(255),
    
    -- User Information
    user_id VARCHAR(100),
    username VARCHAR(100),
    user_email VARCHAR(255),
    
    -- Context
    ip_address VARCHAR(50),
    user_agent VARCHAR(500),
    datacenter_id BIGINT REFERENCES datacenters(id) ON DELETE SET NULL,
    
    -- Changes
    old_values JSONB,
    new_values JSONB,
    changed_fields TEXT[],
    
    -- Result
    success BOOLEAN DEFAULT TRUE,
    error_message TEXT,
    
    -- Timestamp
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_logs_event_type ON audit_logs(event_type);
CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_logs_user ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_created ON audit_logs(created_at);
CREATE INDEX idx_audit_logs_datacenter ON audit_logs(datacenter_id);

-- Partition by month
-- ALTER TABLE audit_logs PARTITION BY RANGE (created_at);
```

### 6.2 SecurityEvent

**Purpose**: Security-specific events (separate from general audit log)

```sql
CREATE TABLE security_events (
    id BIGINT PRIMARY KEY,
    
    -- Event Information
    event_type VARCHAR(100) NOT NULL, 
    -- FAILED_LOGIN, UNAUTHORIZED_ACCESS, API_KEY_USED, 
    -- SUSPICIOUS_ACTIVITY, RATE_LIMIT_EXCEEDED, etc.
    
    severity VARCHAR(20) NOT NULL, -- INFO, WARNING, CRITICAL
    
    -- User/Actor
    user_id VARCHAR(100),
    username VARCHAR(100),
    api_key_id BIGINT REFERENCES api_keys(id) ON DELETE SET NULL,
    
    -- Context
    ip_address VARCHAR(50),
    user_agent VARCHAR(500),
    endpoint VARCHAR(500),
    http_method VARCHAR(10),
    
    -- Details
    description TEXT,
    details JSONB,
    
    -- Response
    action_taken VARCHAR(100), -- BLOCKED, ALLOWED, RATE_LIMITED, etc.
    
    -- Timestamp
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_security_events_type ON security_events(event_type);
CREATE INDEX idx_security_events_severity ON security_events(severity);
CREATE INDEX idx_security_events_user ON security_events(user_id);
CREATE INDEX idx_security_events_created ON security_events(created_at);
CREATE INDEX idx_security_events_ip ON security_events(ip_address);
```

---

## 7. Logs & Metrics Domain (Elasticsearch)

### 7.1 LogSource

**Purpose**: Define log sources for collection

```sql
CREATE TABLE log_sources (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    
    -- Source
    application_id BIGINT REFERENCES applications(id) ON DELETE CASCADE,
    instance_id BIGINT REFERENCES instances(id) ON DELETE CASCADE,
    service_id BIGINT REFERENCES services(id) ON DELETE SET NULL,
    
    -- Log Configuration
    log_type VARCHAR(50) NOT NULL, -- APPLICATION, SYSTEM, ACCESS, ERROR, AUDIT
    log_path VARCHAR(500),
    log_pattern VARCHAR(200), -- Glob pattern for multiple files
    
    -- Parsing
    parser_type VARCHAR(50) DEFAULT 'AUTO', -- JSON, SYSLOG, CUSTOM, AUTO
    parser_config JSONB,
    multiline_pattern VARCHAR(500),
    
    -- Processing
    enabled BOOLEAN DEFAULT TRUE,
    tags TEXT[],
    
    -- Retention
    retention_days INTEGER DEFAULT 30,
    
    -- Statistics
    last_collected_at TIMESTAMP,
    total_lines_collected BIGINT DEFAULT 0,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_log_sources_application ON log_sources(application_id);
CREATE INDEX idx_log_sources_instance ON log_sources(instance_id);
CREATE INDEX idx_log_sources_enabled ON log_sources(enabled);
```

### 7.2 LogEntry (Elasticsearch)

**Purpose**: Individual log entries stored in Elasticsearch

**Elasticsearch Index Template:**
```json
{
  "index_patterns": ["logs-*"],
  "template": {
    "settings": {
      "number_of_shards": 5,
      "number_of_replicas": 1,
      "index.lifecycle.name": "logs_policy",
      "index.lifecycle.rollover_alias": "logs"
    },
    "mappings": {
      "properties": {
        "@timestamp": { "type": "date" },
        "level": { "type": "keyword" },
        "logger": { "type": "keyword" },
        "message": { "type": "text" },
        "thread": { "type": "keyword" },
        "source": {
          "properties": {
            "instance_id": { "type": "long" },
            "application_id": { "type": "long" },
            "hostname": { "type": "keyword" },
            "file": { "type": "keyword" },
            "line": { "type": "integer" }
          }
        },
        "datacenter": {
          "properties": {
            "id": { "type": "long" },
            "name": { "type": "keyword" },
            "region": { "type": "keyword" }
          }
        },
        "exception": {
          "properties": {
            "type": { "type": "keyword" },
            "message": { "type": "text" },
            "stacktrace": { "type": "text" }
          }
        },
        "tags": { "type": "keyword" },
        "metadata": { "type": "object" }
      }
    }
  }
}
```

### 7.3 MetricData (Elasticsearch)

**Purpose**: Time-series metrics stored in Elasticsearch

**Elasticsearch Index Template:**
```json
{
  "index_patterns": ["metrics-*"],
  "template": {
    "settings": {
      "number_of_shards": 5,
      "number_of_replicas": 1,
      "index.lifecycle.name": "metrics_policy"
    },
    "mappings": {
      "properties": {
        "@timestamp": { "type": "date" },
        "metric_name": { "type": "keyword" },
        "metric_type": { "type": "keyword" },
        "value": { "type": "double" },
        "unit": { "type": "keyword" },
        "source": {
          "properties": {
            "instance_id": { "type": "long" },
            "application_id": { "type": "long" },
            "service_id": { "type": "long" },
            "hostname": { "type": "keyword" }
          }
        },
        "datacenter": {
          "properties": {
            "id": { "type": "long" },
            "name": { "type": "keyword" }
          }
        },
        "tags": { "type": "keyword" },
        "dimensions": { "type": "object" }
      }
    }
  }
}
```

---

## 8. Plugin System Domain

### 8.1 Plugin

**Purpose**: Manage installed plugins

```sql
CREATE TABLE plugins (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    display_name VARCHAR(200),
    description TEXT,
    version VARCHAR(50) NOT NULL,
    
    -- Plugin Information
    plugin_type VARCHAR(50) NOT NULL, -- COLLECTOR, INTEGRATION, UI_EXTENSION, WORKFLOW
    author VARCHAR(200),
    homepage_url VARCHAR(500),
    
    -- Installation
    installed_at TIMESTAMP NOT NULL,
    installed_by VARCHAR(100),
    installation_path VARCHAR(500),
    
    -- Configuration
    configuration JSONB,
    default_config JSONB,
    config_schema JSONB, -- JSON Schema for validation
    
    -- Capabilities
    capabilities TEXT[], -- What the plugin can do
    required_permissions TEXT[], -- What permissions it needs
    
    -- Dependencies
    dependencies JSONB, -- Other plugins or system requirements
    
    -- Status
    enabled BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) DEFAULT 'INSTALLED', -- INSTALLED, ENABLED, DISABLED, ERROR
    
    -- Lifecycle
    initialized BOOLEAN DEFAULT FALSE,
    last_started TIMESTAMP,
    last_stopped TIMESTAMP,
    
    -- Metadata
    tags TEXT[],
    metadata JSONB,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_plugins_name ON plugins(name);
CREATE INDEX idx_plugins_type ON plugins(plugin_type);
CREATE INDEX idx_plugins_enabled ON plugins(enabled);
```

### 8.2 PluginExecution

**Purpose**: Track plugin executions

```sql
CREATE TABLE plugin_executions (
    id BIGINT PRIMARY KEY,
    plugin_id BIGINT NOT NULL REFERENCES plugins(id) ON DELETE CASCADE,
    
    -- Execution Information
    execution_type VARCHAR(50) NOT NULL, -- COLLECT_METRICS, RUN_ACTION, HEALTH_CHECK, etc.
    triggered_by VARCHAR(20) NOT NULL, -- SCHEDULE, MANUAL, EVENT
    triggered_by_user VARCHAR(100),
    
    -- Execution
    started_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    duration_ms INTEGER,
    
    -- Status
    status VARCHAR(20) NOT NULL, -- RUNNING, SUCCESS, FAILED, TIMEOUT
    
    -- Result
    result_summary TEXT,
    result_data JSONB,
    error_message TEXT,
    error_stacktrace TEXT,
    
    -- Statistics
    items_processed INTEGER DEFAULT 0,
    items_failed INTEGER DEFAULT 0
);

CREATE INDEX idx_plugin_executions_plugin ON plugin_executions(plugin_id);
CREATE INDEX idx_plugin_executions_started ON plugin_executions(started_at);
CREATE INDEX idx_plugin_executions_status ON plugin_executions(status);
```

---

## 9. Data Retention & Archival

### Retention Policies

```sql
-- Cleanup old heartbeat data (keep 30 days)
CREATE TABLE IF NOT EXISTS data_retention_policies (
    id BIGINT PRIMARY KEY,
    table_name VARCHAR(100) NOT NULL UNIQUE,
    retention_days INTEGER NOT NULL,
    last_cleanup TIMESTAMP,
    enabled BOOLEAN DEFAULT TRUE
);

INSERT INTO data_retention_policies (id, table_name, retention_days) VALUES
(1, 'api_heartbeats', 30),
(2, 'ping_heartbeats', 30),
(3, 'audit_logs', 365),
(4, 'security_events', 365),
(5, 'session_commands', 90),
(6, 'plugin_executions', 60);
```

### Scheduled Cleanup Job

```sql
-- Example cleanup function
CREATE OR REPLACE FUNCTION cleanup_old_heartbeats()
RETURNS INTEGER AS $$
DECLARE
    deleted_count INTEGER;
BEGIN
    -- Delete old API heartbeats
    DELETE FROM api_heartbeats 
    WHERE executed_at < NOW() - INTERVAL '30 days';
    GET DIAGNOSTICS deleted_count = ROW_COUNT;
    
    -- Delete old ping heartbeats
    DELETE FROM ping_heartbeats 
    WHERE executed_at < NOW() - INTERVAL '30 days';
    
    RETURN deleted_count;
END;
$$ LANGUAGE plpgsql;
```

---

## 10. Summary

### Total Entities Count

| Domain | Entity Count |
|--------|-------------|
| Infrastructure | 4 (Region, Datacenter, Rack, Instance) |
| Agent & Monitoring | 5 (Agent, Schedule, HttpMonitor, HttpHeartbeat, PingHeartbeat) |
| Application Management | 2 (Application, ApplicationProcess) |
| Distributed Services | 3 (Service, Cluster, ServiceInstance) |
| Security & Access | 4 (ApiKey, RemoteSession, SessionCommand, RolePermission) |
| Audit & Logging | 2 (AuditLog, SecurityEvent) |
| Logs & Metrics | 2 (LogSource, + Elasticsearch indices) |
| Plugin System | 2 (Plugin, PluginExecution) |
| **Total** | **24 PostgreSQL entities + 2 Elasticsearch indices** |

### Next Steps

1. **Create JDL file** with all entity definitions
2. **Generate entities** using JHipster CLI
3. **Review and customize** generated code
4. **Add custom business logic** in service layer
5. **Create migration scripts** using Liquibase
6. **Set up Elasticsearch** indices and mappings
7. **Implement agents** for data collection
8. **Build UI components** for each entity

This data model provides a solid foundation for the InfraMirror platform and can be extended as needed for additional features.
