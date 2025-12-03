# InfraMirror - Implementation Guide & Best Practices

## Getting Started

### Prerequisites

- JDK 17+
- Node.js 18+
- Docker & Docker Compose
- PostgreSQL 14+
- Redis 7+
- Elasticsearch 8+
- Keycloak 21+

### Initial Setup

1. **Clone and Initialize**
```bash
git clone https://github.com/yourorg/inframirror.git
cd inframirror
```

2. **Start Infrastructure Services**
```bash
cd docker
docker-compose up -d
```

3. **Configure Backend**
```bash
cd InfraMirror
./mvnw clean install
```

4. **Run Database Migrations**
```bash
./mvnw liquibase:update
```

5. **Start Backend**
```bash
./mvnw spring-boot:run
```

6. **Start Frontend**
```bash
cd frontend
npm install
npm run dev
```

---

## JHipster Entity Generation

### Using JDL (Recommended)

Create a file `entities.jdl`:

```jdl
// Infrastructure Domain
entity Region {
    name String required maxlength(50) unique
    regionCode String maxlength(20)
    groupName String maxlength(20)
    description TextBlob
    timezone String maxlength(50)
}

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

// Agent Domain
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

// Application Domain
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

// Service Domain
entity Service {
    name String required maxlength(100)
    serviceType String required maxlength(50)
    version String maxlength(50)
    clusterName String maxlength(100)
    clusterRole String maxlength(50)
    nodeId String maxlength(100)
    connectionHost String maxlength(255)
    connectionPort Integer
    connectionProtocol String maxlength(20)
    connectionUrl TextBlob
    authRequired Boolean
    authType String maxlength(50)
    tlsEnabled Boolean
    monitoringEnabled Boolean
    monitoringEndpoint String maxlength(500)
    healthCheckEndpoint String maxlength(500)
    metricsEndpoint String maxlength(500)
    status ServiceStatus
    healthStatus HealthStatus
    lastHealthCheck Instant
    configData TextBlob
    tags TextBlob
}

enum ServiceStatus {
    RUNNING, STOPPED, ERROR, UNKNOWN
}

entity Cluster {
    name String required maxlength(100)
    clusterType String required maxlength(50)
    version String maxlength(50)
    nodeCount Integer
    config TextBlob
    status ClusterStatus
    healthStatus HealthStatus
    description TextBlob
    tags TextBlob
}

enum ClusterStatus {
    ACTIVE, DEGRADED, OFFLINE, UNKNOWN
}

// Security Domain
entity ApiKey {
    name String required maxlength(100)
    description String maxlength(500)
    keyHash String required maxlength(500)
    keyPrefix String required maxlength(20)
    active Boolean
    revoked Boolean
    revokedAt Instant
    revokedBy String maxlength(100)
    revokeReason TextBlob
    permissions TextBlob
    scope String maxlength(50)
    rateLimitPerMinute Integer
    lastUsedDate Instant
    lastUsedIp String maxlength(50)
    usageCount Long
    expiresAt Instant
    createdBy String required maxlength(50)
    lastModifiedBy String maxlength(50)
}

entity RemoteSession {
    sessionId String required maxlength(100) unique
    sessionType String required maxlength(50)
    userId String required maxlength(100)
    username String maxlength(100)
    userEmail String maxlength(255)
    clientIp String maxlength(50)
    clientPort Integer
    userAgent String maxlength(500)
    status SessionStatus
    startedAt Instant required
    lastActivity Instant
    endedAt Instant
    durationSeconds Integer
    terminalType String maxlength(50)
    terminalSize String maxlength(20)
    dataUploadedBytes Long
    dataDownloadedBytes Long
    sessionRecorded Boolean
    recordingPath String maxlength(500)
    metadata TextBlob
}

enum SessionStatus {
    ACTIVE, CLOSED, EXPIRED, ERROR
}

// Relationships
relationship ManyToOne {
    Datacenter{region} to Region
    Rack{datacenter required} to Datacenter
    Instance{datacenter required} to Datacenter
    Instance{agent} to Agent
    Instance{rack} to Rack
    Agent{datacenter} to Datacenter
    Application{instance required} to Instance
    Service{datacenter required} to Datacenter
    Service{cluster} to Cluster
    Cluster{datacenter required} to Datacenter
    RemoteSession{instance required} to Instance
    RemoteSession{application} to Application
}

// Options
paginate all with pagination
dto all with mapstruct
service all with serviceImpl
filter all
```

**Generate Entities:**
```bash
jhipster jdl entities.jdl
```

---

## Liquibase Best Practices

### 1. Incremental Changes

Never modify existing changesets. Always create new ones:

```xml
<!-- Good: New changeset for adding column -->
<changeSet id="20241203-add-instance-uptime" author="developer">
    <addColumn tableName="instances">
        <column name="uptime_seconds" type="bigint"/>
    </addColumn>
</changeSet>

<!-- Bad: Modifying existing changeset -->
<!-- DON'T DO THIS -->
```

### 2. Rollback Support

Provide rollback for all changes:

```xml
<changeSet id="20241203-add-column" author="developer">
    <addColumn tableName="instances">
        <column name="new_column" type="varchar(100)"/>
    </addColumn>
    
    <rollback>
        <dropColumn tableName="instances" columnName="new_column"/>
    </rollback>
</changeSet>
```

### 3. Use Preconditions

Ensure safe execution:

```xml
<changeSet id="20241203-add-index" author="developer">
    <preConditions onFail="MARK_RAN">
        <not>
            <indexExists indexName="idx_instances_hostname"/>
        </not>
    </preConditions>
    
    <createIndex indexName="idx_instances_hostname" tableName="instances">
        <column name="hostname"/>
    </createIndex>
</changeSet>
```

### 4. Data Migration

Separate schema and data changes:

```xml
<!-- Schema change -->
<changeSet id="20241203-01-alter-table" author="developer">
    <addColumn tableName="instances">
        <column name="environment" type="varchar(20)"/>
    </addColumn>
</changeSet>

<!-- Data migration -->
<changeSet id="20241203-02-migrate-data" author="developer">
    <sql>
        UPDATE instances 
        SET environment = 'production' 
        WHERE tags::text LIKE '%prod%'
    </sql>
</changeSet>
```

---

## API Design Best Practices

### 1. RESTful Endpoints

```java
@RestController
@RequestMapping("/api/instances")
public class InstanceResource {
    
    // List with filtering and pagination
    @GetMapping
    public ResponseEntity<Page<InstanceDTO>> getInstances(
        InstanceFilter filter,
        Pageable pageable
    ) {
        Page<InstanceDTO> page = instanceService.findAll(filter, pageable);
        return ResponseEntity.ok(page);
    }
    
    // Get single resource
    @GetMapping("/{id}")
    public ResponseEntity<InstanceDTO> getInstance(@PathVariable Long id) {
        return instanceService.findOne(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    // Create
    @PostMapping
    public ResponseEntity<InstanceDTO> createInstance(
        @Valid @RequestBody InstanceDTO instanceDTO
    ) throws URISyntaxException {
        InstanceDTO result = instanceService.save(instanceDTO);
        return ResponseEntity.created(
            new URI("/api/instances/" + result.getId())
        ).body(result);
    }
    
    // Update
    @PutMapping("/{id}")
    public ResponseEntity<InstanceDTO> updateInstance(
        @PathVariable Long id,
        @Valid @RequestBody InstanceDTO instanceDTO
    ) {
        InstanceDTO result = instanceService.update(id, instanceDTO);
        return ResponseEntity.ok(result);
    }
    
    // Partial update
    @PatchMapping("/{id}")
    public ResponseEntity<InstanceDTO> partialUpdateInstance(
        @PathVariable Long id,
        @RequestBody InstanceDTO instanceDTO
    ) {
        Optional<InstanceDTO> result = instanceService.partialUpdate(id, instanceDTO);
        return result.map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInstance(@PathVariable Long id) {
        instanceService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    // Custom actions
    @PostMapping("/{id}/restart")
    public ResponseEntity<Void> restartInstance(@PathVariable Long id) {
        instanceService.restart(id);
        return ResponseEntity.accepted().build();
    }
}
```

### 2. Error Handling

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
        EntityNotFoundException ex
    ) {
        ErrorResponse error = new ErrorResponse(
            "NOT_FOUND",
            ex.getMessage(),
            HttpStatus.NOT_FOUND.value()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
        ValidationException ex
    ) {
        ErrorResponse error = new ErrorResponse(
            "VALIDATION_ERROR",
            ex.getMessage(),
            HttpStatus.BAD_REQUEST.value()
        );
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        log.error("Unexpected error", ex);
        ErrorResponse error = new ErrorResponse(
            "INTERNAL_ERROR",
            "An unexpected error occurred",
            HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(error);
    }
}
```

### 3. API Versioning

```java
@RestController
@RequestMapping("/api/v1/instances")
public class InstanceResourceV1 {
    // Version 1 implementation
}

@RestController
@RequestMapping("/api/v2/instances")
public class InstanceResourceV2 {
    // Version 2 with breaking changes
}
```

---

## Service Layer Patterns

### 1. Service Implementation

```java
@Service
@Transactional
public class InstanceServiceImpl implements InstanceService {
    
    private final InstanceRepository instanceRepository;
    private final InstanceMapper instanceMapper;
    private final AgentService agentService;
    private final AuditLogService auditLogService;
    
    public InstanceServiceImpl(
        InstanceRepository instanceRepository,
        InstanceMapper instanceMapper,
        AgentService agentService,
        AuditLogService auditLogService
    ) {
        this.instanceRepository = instanceRepository;
        this.instanceMapper = instanceMapper;
        this.agentService = agentService;
        this.auditLogService = auditLogService;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<InstanceDTO> findAll(InstanceFilter filter, Pageable pageable) {
        log.debug("Request to get Instances with filter: {}", filter);
        
        Specification<Instance> spec = InstanceSpecification.build(filter);
        Page<Instance> page = instanceRepository.findAll(spec, pageable);
        
        return page.map(instanceMapper::toDto);
    }
    
    @Override
    public InstanceDTO save(InstanceDTO instanceDTO) {
        log.debug("Request to save Instance: {}", instanceDTO);
        
        // Validate
        validateInstance(instanceDTO);
        
        // Convert to entity
        Instance instance = instanceMapper.toEntity(instanceDTO);
        
        // Assign agent if needed
        if (instance.getAgent() == null) {
            instance.setAgent(agentService.findBestAgent(
                instance.getDatacenter().getId()
            ));
        }
        
        // Save
        instance = instanceRepository.save(instance);
        
        // Audit log
        auditLogService.logCreate("Instance", instance.getId(), instanceDTO);
        
        // Trigger agent registration
        agentService.registerInstance(instance);
        
        return instanceMapper.toDto(instance);
    }
    
    @Override
    public InstanceDTO update(Long id, InstanceDTO instanceDTO) {
        log.debug("Request to update Instance: {}", id);
        
        Instance existing = instanceRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Instance not found"));
        
        // Capture old values for audit
        InstanceDTO oldValues = instanceMapper.toDto(existing);
        
        // Update fields
        instanceMapper.partialUpdate(existing, instanceDTO);
        
        // Save
        existing = instanceRepository.save(existing);
        
        // Audit log
        auditLogService.logUpdate("Instance", id, oldValues, instanceDTO);
        
        return instanceMapper.toDto(existing);
    }
    
    private void validateInstance(InstanceDTO instanceDTO) {
        // Business validation logic
        if (instanceDTO.getHostname() == null) {
            throw new ValidationException("Hostname is required");
        }
        
        // Check for duplicates
        if (instanceRepository.existsByHostname(instanceDTO.getHostname())) {
            throw new ValidationException("Hostname already exists");
        }
    }
}
```

### 2. Specification Pattern for Filtering

```java
public class InstanceSpecification {
    
    public static Specification<Instance> build(InstanceFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (filter.getDatacenterId() != null) {
                predicates.add(cb.equal(
                    root.get("datacenter").get("id"), 
                    filter.getDatacenterId()
                ));
            }
            
            if (filter.getInstanceType() != null) {
                predicates.add(cb.equal(
                    root.get("instanceType"), 
                    filter.getInstanceType()
                ));
            }
            
            if (filter.getStatus() != null) {
                predicates.add(cb.equal(
                    root.get("status"), 
                    filter.getStatus()
                ));
            }
            
            if (filter.getSearch() != null) {
                String search = "%" + filter.getSearch().toLowerCase() + "%";
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("name")), search),
                    cb.like(cb.lower(root.get("hostname")), search),
                    cb.like(cb.lower(root.get("privateIpAddress")), search)
                ));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
```

---

## Testing Strategy

### 1. Unit Tests

```java
@ExtendWith(MockitoExtension.class)
class InstanceServiceTest {
    
    @Mock
    private InstanceRepository instanceRepository;
    
    @Mock
    private InstanceMapper instanceMapper;
    
    @Mock
    private AgentService agentService;
    
    @InjectMocks
    private InstanceServiceImpl instanceService;
    
    @Test
    void shouldCreateInstance() {
        // Given
        InstanceDTO dto = createInstanceDTO();
        Instance entity = createInstance();
        
        when(instanceMapper.toEntity(dto)).thenReturn(entity);
        when(instanceRepository.save(entity)).thenReturn(entity);
        when(instanceMapper.toDto(entity)).thenReturn(dto);
        
        // When
        InstanceDTO result = instanceService.save(dto);
        
        // Then
        assertThat(result).isNotNull();
        verify(instanceRepository).save(entity);
        verify(agentService).registerInstance(entity);
    }
    
    @Test
    void shouldThrowExceptionWhenHostnameExists() {
        // Given
        InstanceDTO dto = createInstanceDTO();
        when(instanceRepository.existsByHostname(dto.getHostname()))
            .thenReturn(true);
        
        // When/Then
        assertThatThrownBy(() -> instanceService.save(dto))
            .isInstanceOf(ValidationException.class)
            .hasMessage("Hostname already exists");
    }
}
```

### 2. Integration Tests

```java
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class InstanceResourceIT {
    
    @Autowired
    private MockMvc restMockMvc;
    
    @Autowired
    private InstanceRepository instanceRepository;
    
    @Autowired
    private EntityManager em;
    
    @Test
    void shouldCreateInstance() throws Exception {
        int databaseSizeBeforeCreate = instanceRepository.findAll().size();
        
        InstanceDTO instanceDTO = createInstanceDTO();
        
        restMockMvc.perform(post("/api/instances")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(instanceDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value(instanceDTO.getName()))
            .andExpect(jsonPath("$.hostname").value(instanceDTO.getHostname()));
        
        List<Instance> instances = instanceRepository.findAll();
        assertThat(instances).hasSize(databaseSizeBeforeCreate + 1);
    }
    
    @Test
    void shouldGetInstancesByDatacenter() throws Exception {
        // Initialize database
        Instance instance = createInstance();
        instanceRepository.saveAndFlush(instance);
        
        // Get instances by datacenter
        restMockMvc.perform(get("/api/instances")
                .param("datacenterId", instance.getDatacenter().getId().toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0].hostname")
                .value(instance.getHostname()));
    }
}
```

### 3. Performance Tests

```java
@SpringBootTest
@Transactional
class InstanceServicePerformanceTest {
    
    @Autowired
    private InstanceService instanceService;
    
    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void shouldQueryInstancesWithinTimeout() {
        // Given: 1000 instances in database
        createTestInstances(1000);
        
        // When
        InstanceFilter filter = new InstanceFilter();
        filter.setDatacenterId(1L);
        
        Page<InstanceDTO> result = instanceService.findAll(
            filter, 
            PageRequest.of(0, 20)
        );
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
    }
}
```

---

## Frontend Development

### 1. API Client Service

```typescript
// src/services/api/instanceService.ts
import axios from 'axios';
import { Instance, InstanceFilter } from '@/types';

const API_URL = '/api/instances';

export const instanceService = {
  async getAll(filter?: InstanceFilter, page = 0, size = 20) {
    const params = { ...filter, page, size };
    const response = await axios.get(API_URL, { params });
    return response.data;
  },
  
  async getById(id: number) {
    const response = await axios.get(`${API_URL}/${id}`);
    return response.data;
  },
  
  async create(instance: Partial<Instance>) {
    const response = await axios.post(API_URL, instance);
    return response.data;
  },
  
  async update(id: number, instance: Partial<Instance>) {
    const response = await axios.put(`${API_URL}/${id}`, instance);
    return response.data;
  },
  
  async delete(id: number) {
    await axios.delete(`${API_URL}/${id}`);
  },
  
  async restart(id: number) {
    await axios.post(`${API_URL}/${id}/restart`);
  }
};
```

### 2. Redux Store Slice

```typescript
// src/store/slices/instanceSlice.ts
import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { instanceService } from '@/services/api/instanceService';

export const fetchInstances = createAsyncThunk(
  'instances/fetchAll',
  async (filter: InstanceFilter) => {
    const response = await instanceService.getAll(filter);
    return response;
  }
);

const instanceSlice = createSlice({
  name: 'instances',
  initialState: {
    items: [],
    loading: false,
    error: null,
    totalPages: 0,
    currentPage: 0
  },
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchInstances.pending, (state) => {
        state.loading = true;
      })
      .addCase(fetchInstances.fulfilled, (state, action) => {
        state.loading = false;
        state.items = action.payload.content;
        state.totalPages = action.payload.totalPages;
        state.currentPage = action.payload.number;
      })
      .addCase(fetchInstances.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      });
  }
});

export default instanceSlice.reducer;
```

### 3. React Component

```typescript
// src/components/instances/InstanceList.tsx
import React, { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { fetchInstances } from '@/store/slices/instanceSlice';
import { DataGrid } from '@mui/x-data-grid';

export const InstanceList: React.FC = () => {
  const dispatch = useDispatch();
  const { items, loading } = useSelector((state) => state.instances);
  
  useEffect(() => {
    dispatch(fetchInstances({}));
  }, [dispatch]);
  
  const columns = [
    { field: 'name', headerName: 'Name', width: 200 },
    { field: 'hostname', headerName: 'Hostname', width: 200 },
    { field: 'privateIpAddress', headerName: 'IP Address', width: 150 },
    { field: 'status', headerName: 'Status', width: 120 },
    { field: 'healthStatus', headerName: 'Health', width: 120 }
  ];
  
  return (
    <div style={{ height: 600, width: '100%' }}>
      <DataGrid
        rows={items}
        columns={columns}
        loading={loading}
        pageSize={20}
        rowsPerPageOptions={[10, 20, 50]}
      />
    </div>
  );
};
```

---

## Performance Optimization

### 1. Database Indexing

```sql
-- Critical indexes for performance
CREATE INDEX idx_instances_datacenter ON instances(datacenter_id);
CREATE INDEX idx_instances_agent ON instances(agent_id);
CREATE INDEX idx_instances_status ON instances(status);
CREATE INDEX idx_instances_hostname ON instances(hostname);
CREATE INDEX idx_instances_type ON instances(instance_type);

-- Composite indexes for common queries
CREATE INDEX idx_instances_dc_status ON instances(datacenter_id, status);
CREATE INDEX idx_instances_dc_type ON instances(datacenter_id, instance_type);

-- GIN index for JSONB columns
CREATE INDEX idx_instances_tags ON instances USING gin(tags);
CREATE INDEX idx_instances_metadata ON instances USING gin(metadata);
```

### 2. Query Optimization

```java
// Bad: N+1 query problem
List<Instance> instances = instanceRepository.findAll();
for (Instance instance : instances) {
    // This triggers additional queries
    Datacenter dc = instance.getDatacenter();
    Agent agent = instance.getAgent();
}

// Good: Use fetch join
@Query("SELECT i FROM Instance i " +
       "LEFT JOIN FETCH i.datacenter " +
       "LEFT JOIN FETCH i.agent " +
       "WHERE i.status = :status")
List<Instance> findWithDependencies(@Param("status") InstanceStatus status);
```

### 3. Caching Strategy

```java
@Service
public class InstanceService {
    
    @Cacheable(value = "instances", key = "#id")
    public Optional<InstanceDTO> findOne(Long id) {
        return instanceRepository.findById(id)
            .map(instanceMapper::toDto);
    }
    
    @CacheEvict(value = "instances", key = "#id")
    public void delete(Long id) {
        instanceRepository.deleteById(id);
    }
    
    @CachePut(value = "instances", key = "#result.id")
    public InstanceDTO update(Long id, InstanceDTO dto) {
        // update logic
        return result;
    }
}
```

---

## Security Best Practices

### 1. API Key Security

```java
@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        
        String apiKey = request.getHeader("X-API-Key");
        
        if (apiKey != null) {
            try {
                Authentication auth = authenticateApiKey(apiKey);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (BadCredentialsException e) {
                response.sendError(HttpStatus.UNAUTHORIZED.value());
                return;
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    private Authentication authenticateApiKey(String apiKey) {
        // Validate API key
        // Check expiration
        // Check rate limits
        // Return authentication
    }
}
```

### 2. Input Validation

```java
@Data
@Valid
public class InstanceDTO {
    
    private Long id;
    
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must be less than 255 characters")
    private String name;
    
    @NotBlank(message = "Hostname is required")
    @Pattern(regexp = "^[a-zA-Z0-9.-]+$", message = "Invalid hostname format")
    private String hostname;
    
    @Pattern(regexp = "^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$", 
             message = "Invalid IP address")
    private String privateIpAddress;
    
    @Min(value = 1, message = "CPU cores must be at least 1")
    @Max(value = 256, message = "CPU cores cannot exceed 256")
    private Integer cpuCores;
}
```

### 3. Rate Limiting

```java
@Configuration
public class RateLimitConfig {
    
    @Bean
    public RateLimiter apiRateLimiter() {
        return RateLimiter.create(100.0); // 100 requests per second
    }
}

@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    
    private final RateLimiter rateLimiter;
    
    @Override
    public boolean preHandle(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler
    ) {
        if (!rateLimiter.tryAcquire()) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            return false;
        }
        return true;
    }
}
```

---

## Monitoring & Logging

### 1. Structured Logging

```java
@Slf4j
@Service
public class InstanceService {
    
    public InstanceDTO save(InstanceDTO instanceDTO) {
        log.info("Creating instance: name={}, datacenter={}", 
                 instanceDTO.getName(), 
                 instanceDTO.getDatacenterId());
        
        try {
            Instance saved = instanceRepository.save(instance);
            
            log.info("Instance created successfully: id={}, hostname={}", 
                     saved.getId(), 
                     saved.getHostname());
            
            return mapper.toDto(saved);
            
        } catch (Exception e) {
            log.error("Failed to create instance: name={}, error={}", 
                      instanceDTO.getName(), 
                      e.getMessage(), 
                      e);
            throw e;
        }
    }
}
```

### 2. Custom Metrics

```java
@Service
public class InstanceMetricsService {
    
    private final Counter instanceCreatedCounter;
    private final Gauge activeInstancesGauge;
    
    public InstanceMetricsService(MeterRegistry registry) {
        this.instanceCreatedCounter = Counter.builder("instances.created")
            .description("Total instances created")
            .register(registry);
            
        this.activeInstancesGauge = Gauge.builder("instances.active", 
                                                   this::getActiveCount)
            .description("Current active instances")
            .register(registry);
    }
    
    public void recordInstanceCreated() {
        instanceCreatedCounter.increment();
    }
    
    private long getActiveCount() {
        return instanceRepository.countByStatus(InstanceStatus.RUNNING);
    }
}
```

---

## Conclusion

This implementation guide provides:
- Step-by-step setup instructions
- Best practices for all layers
- Code examples and patterns
- Testing strategies
- Performance optimization techniques
- Security guidelines

Follow these practices to build a robust, scalable, and maintainable InfraMirror platform.

**Next Steps:**
1. Set up development environment
2. Generate entities using JDL
3. Implement core services
4. Add comprehensive tests
5. Deploy to staging
6. Gather feedback and iterate
