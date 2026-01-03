package vibhuvi.oio.inframirror.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static vibhuvi.oio.inframirror.domain.InstanceAsserts.*;
import static vibhuvi.oio.inframirror.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.IntegrationTest;
import vibhuvi.oio.inframirror.domain.Agent;
import vibhuvi.oio.inframirror.domain.Datacenter;
import vibhuvi.oio.inframirror.domain.Instance;
import vibhuvi.oio.inframirror.repository.InstanceRepository;
import vibhuvi.oio.inframirror.service.dto.InstanceDTO;
import vibhuvi.oio.inframirror.service.mapper.InstanceMapper;
import vibhuvi.oio.inframirror.domain.enumeration.InstanceType;
import vibhuvi.oio.inframirror.domain.enumeration.MonitoringType;
import vibhuvi.oio.inframirror.domain.enumeration.OperatingSystem;

/**
 * Integration tests for the {@link InstanceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class InstanceResourceIT extends AbstractEntityResourceIT<Instance, InstanceRepository> {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_HOSTNAME = "AAAAAAAAAA";
    private static final String UPDATED_HOSTNAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final InstanceType DEFAULT_INSTANCE_TYPE = InstanceType.VM;
    private static final InstanceType UPDATED_INSTANCE_TYPE = InstanceType.BARE_METAL;

    private static final MonitoringType DEFAULT_MONITORING_TYPE = MonitoringType.SELF_HOSTED;
    private static final MonitoringType UPDATED_MONITORING_TYPE = MonitoringType.AGENT_MONITORED;

    private static final OperatingSystem DEFAULT_OPERATING_SYSTEM = OperatingSystem.LINUX;
    private static final OperatingSystem UPDATED_OPERATING_SYSTEM = OperatingSystem.WINDOWS;

    private static final String DEFAULT_PLATFORM = "AAAAAAAAAA";
    private static final String UPDATED_PLATFORM = "BBBBBBBBBB";

    private static final String DEFAULT_PRIVATE_IP_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_PRIVATE_IP_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_PUBLIC_IP_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_PUBLIC_IP_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_TAGS = "AAAAAAAAAA";
    private static final String UPDATED_TAGS = "BBBBBBBBBB";

    private static final Boolean DEFAULT_PING_ENABLED = false;
    private static final Boolean UPDATED_PING_ENABLED = true;

    private static final Integer DEFAULT_PING_INTERVAL = 10;
    private static final Integer UPDATED_PING_INTERVAL = 20;
    private static final Integer SMALLER_PING_INTERVAL = 9;

    private static final Integer DEFAULT_PING_TIMEOUT_MS = 500;
    private static final Integer UPDATED_PING_TIMEOUT_MS = 600;
    private static final Integer SMALLER_PING_TIMEOUT_MS = 499;

    private static final Integer DEFAULT_PING_RETRY_COUNT = 1;
    private static final Integer UPDATED_PING_RETRY_COUNT = 2;
    private static final Integer SMALLER_PING_RETRY_COUNT = 1 - 1;

    private static final Boolean DEFAULT_HARDWARE_MONITORING_ENABLED = false;
    private static final Boolean UPDATED_HARDWARE_MONITORING_ENABLED = true;

    private static final Integer DEFAULT_HARDWARE_MONITORING_INTERVAL = 60;
    private static final Integer UPDATED_HARDWARE_MONITORING_INTERVAL = 120;
    private static final Integer SMALLER_HARDWARE_MONITORING_INTERVAL = 59;

    private static final Integer DEFAULT_CPU_WARNING_THRESHOLD = 1;
    private static final Integer UPDATED_CPU_WARNING_THRESHOLD = 2;
    private static final Integer SMALLER_CPU_WARNING_THRESHOLD = 1 - 1;

    private static final Integer DEFAULT_CPU_DANGER_THRESHOLD = 1;
    private static final Integer UPDATED_CPU_DANGER_THRESHOLD = 2;
    private static final Integer SMALLER_CPU_DANGER_THRESHOLD = 1 - 1;

    private static final Integer DEFAULT_MEMORY_WARNING_THRESHOLD = 1;
    private static final Integer UPDATED_MEMORY_WARNING_THRESHOLD = 2;
    private static final Integer SMALLER_MEMORY_WARNING_THRESHOLD = 1 - 1;

    private static final Integer DEFAULT_MEMORY_DANGER_THRESHOLD = 1;
    private static final Integer UPDATED_MEMORY_DANGER_THRESHOLD = 2;
    private static final Integer SMALLER_MEMORY_DANGER_THRESHOLD = 1 - 1;

    private static final Integer DEFAULT_DISK_WARNING_THRESHOLD = 1;
    private static final Integer UPDATED_DISK_WARNING_THRESHOLD = 2;
    private static final Integer SMALLER_DISK_WARNING_THRESHOLD = 1 - 1;

    private static final Integer DEFAULT_DISK_DANGER_THRESHOLD = 1;
    private static final Integer UPDATED_DISK_DANGER_THRESHOLD = 2;
    private static final Integer SMALLER_DISK_DANGER_THRESHOLD = 1 - 1;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_LAST_PING_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_PING_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_LAST_HARDWARE_CHECK_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_HARDWARE_CHECK_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/instances";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/instances/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private InstanceRepository instanceRepository;

    @Autowired
    private InstanceMapper instanceMapper;

    private Instance instance;
    private Instance insertedInstance;

    @Override
    protected InstanceRepository getRepository() {
        return instanceRepository;
    }

    @Override
    protected String getEntityApiUrl() {
        return ENTITY_API_URL;
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Instance createEntity(EntityManager em) {
        Instance instance = new Instance()
            .name(DEFAULT_NAME)
            .hostname(DEFAULT_HOSTNAME)
            .description(DEFAULT_DESCRIPTION)
            .instanceType(DEFAULT_INSTANCE_TYPE)
            .monitoringType(DEFAULT_MONITORING_TYPE)
            .operatingSystem(DEFAULT_OPERATING_SYSTEM)
            .platform(DEFAULT_PLATFORM)
            .privateIpAddress(DEFAULT_PRIVATE_IP_ADDRESS)
            .publicIpAddress(DEFAULT_PUBLIC_IP_ADDRESS)
            .tags(DEFAULT_TAGS)
            .pingEnabled(DEFAULT_PING_ENABLED)
            .pingInterval(DEFAULT_PING_INTERVAL)
            .pingTimeoutMs(DEFAULT_PING_TIMEOUT_MS)
            .pingRetryCount(DEFAULT_PING_RETRY_COUNT)
            .hardwareMonitoringEnabled(DEFAULT_HARDWARE_MONITORING_ENABLED)
            .hardwareMonitoringInterval(DEFAULT_HARDWARE_MONITORING_INTERVAL)
            .cpuWarningThreshold(DEFAULT_CPU_WARNING_THRESHOLD)
            .cpuDangerThreshold(DEFAULT_CPU_DANGER_THRESHOLD)
            .memoryWarningThreshold(DEFAULT_MEMORY_WARNING_THRESHOLD)
            .memoryDangerThreshold(DEFAULT_MEMORY_DANGER_THRESHOLD)
            .diskWarningThreshold(DEFAULT_DISK_WARNING_THRESHOLD)
            .diskDangerThreshold(DEFAULT_DISK_DANGER_THRESHOLD)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT)
            .lastPingAt(DEFAULT_LAST_PING_AT)
            .lastHardwareCheckAt(DEFAULT_LAST_HARDWARE_CHECK_AT);
        // Add required entity
        Datacenter datacenter;
        if (TestUtil.findAll(em, Datacenter.class).isEmpty()) {
            datacenter = DatacenterResourceIT.createEntity();
            em.persist(datacenter);
            em.flush();
        } else {
            datacenter = TestUtil.findAll(em, Datacenter.class).get(0);
        }
        instance.setDatacenter(datacenter);
        return instance;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Instance createUpdatedEntity(EntityManager em) {
        Instance updatedInstance = new Instance()
            .name(UPDATED_NAME)
            .hostname(UPDATED_HOSTNAME)
            .description(UPDATED_DESCRIPTION)
            .instanceType(UPDATED_INSTANCE_TYPE)
            .monitoringType(UPDATED_MONITORING_TYPE)
            .operatingSystem(UPDATED_OPERATING_SYSTEM)
            .platform(UPDATED_PLATFORM)
            .privateIpAddress(UPDATED_PRIVATE_IP_ADDRESS)
            .publicIpAddress(UPDATED_PUBLIC_IP_ADDRESS)
            .tags(UPDATED_TAGS)
            .pingEnabled(UPDATED_PING_ENABLED)
            .pingInterval(UPDATED_PING_INTERVAL)
            .pingTimeoutMs(UPDATED_PING_TIMEOUT_MS)
            .pingRetryCount(UPDATED_PING_RETRY_COUNT)
            .hardwareMonitoringEnabled(UPDATED_HARDWARE_MONITORING_ENABLED)
            .hardwareMonitoringInterval(UPDATED_HARDWARE_MONITORING_INTERVAL)
            .cpuWarningThreshold(UPDATED_CPU_WARNING_THRESHOLD)
            .cpuDangerThreshold(UPDATED_CPU_DANGER_THRESHOLD)
            .memoryWarningThreshold(UPDATED_MEMORY_WARNING_THRESHOLD)
            .memoryDangerThreshold(UPDATED_MEMORY_DANGER_THRESHOLD)
            .diskWarningThreshold(UPDATED_DISK_WARNING_THRESHOLD)
            .diskDangerThreshold(UPDATED_DISK_DANGER_THRESHOLD)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .lastPingAt(UPDATED_LAST_PING_AT)
            .lastHardwareCheckAt(UPDATED_LAST_HARDWARE_CHECK_AT);
        // Add required entity
        Datacenter datacenter;
        if (TestUtil.findAll(em, Datacenter.class).isEmpty()) {
            datacenter = DatacenterResourceIT.createUpdatedEntity();
            em.persist(datacenter);
            em.flush();
        } else {
            datacenter = TestUtil.findAll(em, Datacenter.class).get(0);
        }
        updatedInstance.setDatacenter(datacenter);
        return updatedInstance;
    }

    @BeforeEach
    void initTest() {
        instance = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedInstance != null) {
            instanceRepository.delete(insertedInstance);
            insertedInstance = null;
        }
    }

    @Test
    @Transactional
    void createInstance() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Instance
        InstanceDTO instanceDTO = instanceMapper.toDto(instance);
        var returnedInstanceDTO = om.readValue(
            restMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(instanceDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            InstanceDTO.class
        );

        // Validate the Instance in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedInstance = instanceMapper.toEntity(returnedInstanceDTO);
        assertInstanceUpdatableFieldsEquals(returnedInstance, getPersistedEntity(returnedInstance.getId()));


        insertedInstance = returnedInstance;
    }

    @Test
    @Transactional
    void createInstanceWithExistingId() throws Exception {
        // Create the Instance with an existing ID
        instance.setId(1L);
        InstanceDTO instanceDTO = instanceMapper.toDto(instance);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(instanceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Instance in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        instance.setName(null);

        // Create the Instance, which fails.
        InstanceDTO instanceDTO = instanceMapper.toDto(instance);

        restMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(instanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

    }

    @Test
    @Transactional
    void checkHostnameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        instance.setHostname(null);

        // Create the Instance, which fails.
        InstanceDTO instanceDTO = instanceMapper.toDto(instance);

        restMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(instanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

    }

    @Test
    @Transactional
    void checkInstanceTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        instance.setInstanceType(null);

        // Create the Instance, which fails.
        InstanceDTO instanceDTO = instanceMapper.toDto(instance);

        restMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(instanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

    }

    @Test
    @Transactional
    void checkMonitoringTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        instance.setMonitoringType(null);

        // Create the Instance, which fails.
        InstanceDTO instanceDTO = instanceMapper.toDto(instance);

        restMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(instanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

    }

    @Test
    @Transactional
    void checkPingEnabledIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        instance.setPingEnabled(null);

        // Create the Instance, which fails.
        InstanceDTO instanceDTO = instanceMapper.toDto(instance);

        restMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(instanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

    }

    @Test
    @Transactional
    void checkPingIntervalIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        instance.setPingInterval(null);

        // Create the Instance, which fails.
        InstanceDTO instanceDTO = instanceMapper.toDto(instance);

        restMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(instanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

    }

    @Test
    @Transactional
    void checkPingTimeoutMsIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        instance.setPingTimeoutMs(null);

        // Create the Instance, which fails.
        InstanceDTO instanceDTO = instanceMapper.toDto(instance);

        restMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(instanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

    }

    @Test
    @Transactional
    void checkPingRetryCountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        instance.setPingRetryCount(null);

        // Create the Instance, which fails.
        InstanceDTO instanceDTO = instanceMapper.toDto(instance);

        restMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(instanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

    }

    @Test
    @Transactional
    void checkHardwareMonitoringEnabledIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        instance.setHardwareMonitoringEnabled(null);

        // Create the Instance, which fails.
        InstanceDTO instanceDTO = instanceMapper.toDto(instance);

        restMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(instanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

    }

    @Test
    @Transactional
    void checkHardwareMonitoringIntervalIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        instance.setHardwareMonitoringInterval(null);

        // Create the Instance, which fails.
        InstanceDTO instanceDTO = instanceMapper.toDto(instance);

        restMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(instanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

    }

    @Test
    @Transactional
    void checkCpuWarningThresholdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        instance.setCpuWarningThreshold(null);

        // Create the Instance, which fails.
        InstanceDTO instanceDTO = instanceMapper.toDto(instance);

        restMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(instanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

    }

    @Test
    @Transactional
    void checkCpuDangerThresholdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        instance.setCpuDangerThreshold(null);

        // Create the Instance, which fails.
        InstanceDTO instanceDTO = instanceMapper.toDto(instance);

        restMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(instanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

    }

    @Test
    @Transactional
    void checkMemoryWarningThresholdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        instance.setMemoryWarningThreshold(null);

        // Create the Instance, which fails.
        InstanceDTO instanceDTO = instanceMapper.toDto(instance);

        restMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(instanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

    }

    @Test
    @Transactional
    void checkMemoryDangerThresholdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        instance.setMemoryDangerThreshold(null);

        // Create the Instance, which fails.
        InstanceDTO instanceDTO = instanceMapper.toDto(instance);

        restMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(instanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

    }

    @Test
    @Transactional
    void checkDiskWarningThresholdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        instance.setDiskWarningThreshold(null);

        // Create the Instance, which fails.
        InstanceDTO instanceDTO = instanceMapper.toDto(instance);

        restMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(instanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

    }

    @Test
    @Transactional
    void checkDiskDangerThresholdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        instance.setDiskDangerThreshold(null);

        // Create the Instance, which fails.
        InstanceDTO instanceDTO = instanceMapper.toDto(instance);

        restMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(instanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

    }

    @Test
    @Transactional
    void getAllInstances() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList
        restMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(instance.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].hostname").value(hasItem(DEFAULT_HOSTNAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].instanceType").value(hasItem(DEFAULT_INSTANCE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].monitoringType").value(hasItem(DEFAULT_MONITORING_TYPE.toString())))
            .andExpect(jsonPath("$.[*].operatingSystem").value(hasItem(DEFAULT_OPERATING_SYSTEM.toString())))
            .andExpect(jsonPath("$.[*].platform").value(hasItem(DEFAULT_PLATFORM)))
            .andExpect(jsonPath("$.[*].privateIpAddress").value(hasItem(DEFAULT_PRIVATE_IP_ADDRESS)))
            .andExpect(jsonPath("$.[*].publicIpAddress").value(hasItem(DEFAULT_PUBLIC_IP_ADDRESS)))
            .andExpect(jsonPath("$.[*].tags").value(hasItem(DEFAULT_TAGS)))
            .andExpect(jsonPath("$.[*].pingEnabled").value(hasItem(DEFAULT_PING_ENABLED)))
            .andExpect(jsonPath("$.[*].pingInterval").value(hasItem(DEFAULT_PING_INTERVAL)))
            .andExpect(jsonPath("$.[*].pingTimeoutMs").value(hasItem(DEFAULT_PING_TIMEOUT_MS)))
            .andExpect(jsonPath("$.[*].pingRetryCount").value(hasItem(DEFAULT_PING_RETRY_COUNT)))
            .andExpect(jsonPath("$.[*].hardwareMonitoringEnabled").value(hasItem(DEFAULT_HARDWARE_MONITORING_ENABLED)))
            .andExpect(jsonPath("$.[*].hardwareMonitoringInterval").value(hasItem(DEFAULT_HARDWARE_MONITORING_INTERVAL)))
            .andExpect(jsonPath("$.[*].cpuWarningThreshold").value(hasItem(DEFAULT_CPU_WARNING_THRESHOLD)))
            .andExpect(jsonPath("$.[*].cpuDangerThreshold").value(hasItem(DEFAULT_CPU_DANGER_THRESHOLD)))
            .andExpect(jsonPath("$.[*].memoryWarningThreshold").value(hasItem(DEFAULT_MEMORY_WARNING_THRESHOLD)))
            .andExpect(jsonPath("$.[*].memoryDangerThreshold").value(hasItem(DEFAULT_MEMORY_DANGER_THRESHOLD)))
            .andExpect(jsonPath("$.[*].diskWarningThreshold").value(hasItem(DEFAULT_DISK_WARNING_THRESHOLD)))
            .andExpect(jsonPath("$.[*].diskDangerThreshold").value(hasItem(DEFAULT_DISK_DANGER_THRESHOLD)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].lastPingAt").value(hasItem(DEFAULT_LAST_PING_AT.toString())))
            .andExpect(jsonPath("$.[*].lastHardwareCheckAt").value(hasItem(DEFAULT_LAST_HARDWARE_CHECK_AT.toString())));
    }

    @Test
    @Transactional
    void getInstance() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get the instance
        restMockMvc
            .perform(get(ENTITY_API_URL_ID, instance.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(instance.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.hostname").value(DEFAULT_HOSTNAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.instanceType").value(DEFAULT_INSTANCE_TYPE.toString()))
            .andExpect(jsonPath("$.monitoringType").value(DEFAULT_MONITORING_TYPE.toString()))
            .andExpect(jsonPath("$.operatingSystem").value(DEFAULT_OPERATING_SYSTEM.toString()))
            .andExpect(jsonPath("$.platform").value(DEFAULT_PLATFORM))
            .andExpect(jsonPath("$.privateIpAddress").value(DEFAULT_PRIVATE_IP_ADDRESS))
            .andExpect(jsonPath("$.publicIpAddress").value(DEFAULT_PUBLIC_IP_ADDRESS))
            .andExpect(jsonPath("$.tags").value(DEFAULT_TAGS))
            .andExpect(jsonPath("$.pingEnabled").value(DEFAULT_PING_ENABLED))
            .andExpect(jsonPath("$.pingInterval").value(DEFAULT_PING_INTERVAL))
            .andExpect(jsonPath("$.pingTimeoutMs").value(DEFAULT_PING_TIMEOUT_MS))
            .andExpect(jsonPath("$.pingRetryCount").value(DEFAULT_PING_RETRY_COUNT))
            .andExpect(jsonPath("$.hardwareMonitoringEnabled").value(DEFAULT_HARDWARE_MONITORING_ENABLED))
            .andExpect(jsonPath("$.hardwareMonitoringInterval").value(DEFAULT_HARDWARE_MONITORING_INTERVAL))
            .andExpect(jsonPath("$.cpuWarningThreshold").value(DEFAULT_CPU_WARNING_THRESHOLD))
            .andExpect(jsonPath("$.cpuDangerThreshold").value(DEFAULT_CPU_DANGER_THRESHOLD))
            .andExpect(jsonPath("$.memoryWarningThreshold").value(DEFAULT_MEMORY_WARNING_THRESHOLD))
            .andExpect(jsonPath("$.memoryDangerThreshold").value(DEFAULT_MEMORY_DANGER_THRESHOLD))
            .andExpect(jsonPath("$.diskWarningThreshold").value(DEFAULT_DISK_WARNING_THRESHOLD))
            .andExpect(jsonPath("$.diskDangerThreshold").value(DEFAULT_DISK_DANGER_THRESHOLD))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.lastPingAt").value(DEFAULT_LAST_PING_AT.toString()))
            .andExpect(jsonPath("$.lastHardwareCheckAt").value(DEFAULT_LAST_HARDWARE_CHECK_AT.toString()));
    }

    @Test
    @Transactional
    void getInstancesByIdFiltering() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        Long id = instance.getId();

        defaultInstanceFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultInstanceFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultInstanceFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllInstancesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where name equals to
        defaultInstanceFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllInstancesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where name in
        defaultInstanceFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllInstancesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where name is not null
        defaultInstanceFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllInstancesByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where name contains
        defaultInstanceFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllInstancesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where name does not contain
        defaultInstanceFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllInstancesByHostnameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where hostname equals to
        defaultInstanceFiltering("hostname.equals=" + DEFAULT_HOSTNAME, "hostname.equals=" + UPDATED_HOSTNAME);
    }

    @Test
    @Transactional
    void getAllInstancesByHostnameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where hostname in
        defaultInstanceFiltering("hostname.in=" + DEFAULT_HOSTNAME + "," + UPDATED_HOSTNAME, "hostname.in=" + UPDATED_HOSTNAME);
    }

    @Test
    @Transactional
    void getAllInstancesByHostnameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where hostname is not null
        defaultInstanceFiltering("hostname.specified=true", "hostname.specified=false");
    }

    @Test
    @Transactional
    void getAllInstancesByHostnameContainsSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where hostname contains
        defaultInstanceFiltering("hostname.contains=" + DEFAULT_HOSTNAME, "hostname.contains=" + UPDATED_HOSTNAME);
    }

    @Test
    @Transactional
    void getAllInstancesByHostnameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where hostname does not contain
        defaultInstanceFiltering("hostname.doesNotContain=" + UPDATED_HOSTNAME, "hostname.doesNotContain=" + DEFAULT_HOSTNAME);
    }

    @Test
    @Transactional
    void getAllInstancesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where description equals to
        defaultInstanceFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllInstancesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where description in
        defaultInstanceFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllInstancesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where description is not null
        defaultInstanceFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    @Transactional
    void getAllInstancesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where description contains
        defaultInstanceFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllInstancesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where description does not contain
        defaultInstanceFiltering("description.doesNotContain=" + UPDATED_DESCRIPTION, "description.doesNotContain=" + DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllInstancesByInstanceTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where instanceType equals to
        defaultInstanceFiltering("instanceType.equals=" + DEFAULT_INSTANCE_TYPE, "instanceType.equals=" + UPDATED_INSTANCE_TYPE);
    }

    @Test
    @Transactional
    void getAllInstancesByInstanceTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where instanceType in
        defaultInstanceFiltering(
            "instanceType.in=" + DEFAULT_INSTANCE_TYPE + "," + UPDATED_INSTANCE_TYPE,
            "instanceType.in=" + UPDATED_INSTANCE_TYPE
        );
    }

    @Test
    @Transactional
    void getAllInstancesByInstanceTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where instanceType is not null
        defaultInstanceFiltering("instanceType.specified=true", "instanceType.specified=false");
    }

    @Test
    @Transactional
    void getAllInstancesByInstanceTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Enum fields don't support contains operation, use equals instead
        defaultInstanceFiltering("instanceType.equals=" + DEFAULT_INSTANCE_TYPE, "instanceType.equals=" + UPDATED_INSTANCE_TYPE);
    }

    @Test
    @Transactional
    void getAllInstancesByInstanceTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Enum fields don't support doesNotContain operation, use notEquals instead
        defaultInstanceFiltering(
            "instanceType.notEquals=" + UPDATED_INSTANCE_TYPE,
            "instanceType.notEquals=" + DEFAULT_INSTANCE_TYPE
        );
    }

    @Test
    @Transactional
    void getAllInstancesByMonitoringTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where monitoringType equals to
        defaultInstanceFiltering("monitoringType.equals=" + DEFAULT_MONITORING_TYPE, "monitoringType.equals=" + UPDATED_MONITORING_TYPE);
    }

    @Test
    @Transactional
    void getAllInstancesByMonitoringTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where monitoringType in
        defaultInstanceFiltering(
            "monitoringType.in=" + DEFAULT_MONITORING_TYPE + "," + UPDATED_MONITORING_TYPE,
            "monitoringType.in=" + UPDATED_MONITORING_TYPE
        );
    }

    @Test
    @Transactional
    void getAllInstancesByMonitoringTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where monitoringType is not null
        defaultInstanceFiltering("monitoringType.specified=true", "monitoringType.specified=false");
    }

    @Test
    @Transactional
    void getAllInstancesByMonitoringTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Enum fields don't support contains operation, use equals instead
        defaultInstanceFiltering(
            "monitoringType.equals=" + DEFAULT_MONITORING_TYPE,
            "monitoringType.equals=" + UPDATED_MONITORING_TYPE
        );
    }

    @Test
    @Transactional
    void getAllInstancesByMonitoringTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Enum fields don't support doesNotContain operation, use notEquals instead
        defaultInstanceFiltering(
            "monitoringType.notEquals=" + UPDATED_MONITORING_TYPE,
            "monitoringType.notEquals=" + DEFAULT_MONITORING_TYPE
        );
    }

    @Test
    @Transactional
    void getAllInstancesByOperatingSystemIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where operatingSystem equals to
        defaultInstanceFiltering(
            "operatingSystem.equals=" + DEFAULT_OPERATING_SYSTEM,
            "operatingSystem.equals=" + UPDATED_OPERATING_SYSTEM
        );
    }

    @Test
    @Transactional
    void getAllInstancesByOperatingSystemIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where operatingSystem in
        defaultInstanceFiltering(
            "operatingSystem.in=" + DEFAULT_OPERATING_SYSTEM + "," + UPDATED_OPERATING_SYSTEM,
            "operatingSystem.in=" + UPDATED_OPERATING_SYSTEM
        );
    }

    @Test
    @Transactional
    void getAllInstancesByOperatingSystemIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where operatingSystem is not null
        defaultInstanceFiltering("operatingSystem.specified=true", "operatingSystem.specified=false");
    }

    @Test
    @Transactional
    void getAllInstancesByOperatingSystemContainsSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Enum fields don't support contains operation, use equals instead
        defaultInstanceFiltering(
            "operatingSystem.equals=" + DEFAULT_OPERATING_SYSTEM,
            "operatingSystem.equals=" + UPDATED_OPERATING_SYSTEM
        );
    }

    @Test
    @Transactional
    void getAllInstancesByOperatingSystemNotContainsSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Enum fields don't support doesNotContain operation, use notEquals instead
        defaultInstanceFiltering(
            "operatingSystem.notEquals=" + UPDATED_OPERATING_SYSTEM,
            "operatingSystem.notEquals=" + DEFAULT_OPERATING_SYSTEM
        );
    }

    @Test
    @Transactional
    void getAllInstancesByPlatformIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where platform equals to
        defaultInstanceFiltering("platform.equals=" + DEFAULT_PLATFORM, "platform.equals=" + UPDATED_PLATFORM);
    }

    @Test
    @Transactional
    void getAllInstancesByPlatformIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where platform in
        defaultInstanceFiltering("platform.in=" + DEFAULT_PLATFORM + "," + UPDATED_PLATFORM, "platform.in=" + UPDATED_PLATFORM);
    }

    @Test
    @Transactional
    void getAllInstancesByPlatformIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where platform is not null
        defaultInstanceFiltering("platform.specified=true", "platform.specified=false");
    }

    @Test
    @Transactional
    void getAllInstancesByPlatformContainsSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where platform contains
        defaultInstanceFiltering("platform.contains=" + DEFAULT_PLATFORM, "platform.contains=" + UPDATED_PLATFORM);
    }

    @Test
    @Transactional
    void getAllInstancesByPlatformNotContainsSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where platform does not contain
        defaultInstanceFiltering("platform.doesNotContain=" + UPDATED_PLATFORM, "platform.doesNotContain=" + DEFAULT_PLATFORM);
    }

    @Test
    @Transactional
    void getAllInstancesByPrivateIpAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where privateIpAddress equals to
        defaultInstanceFiltering(
            "privateIpAddress.equals=" + DEFAULT_PRIVATE_IP_ADDRESS,
            "privateIpAddress.equals=" + UPDATED_PRIVATE_IP_ADDRESS
        );
    }

    @Test
    @Transactional
    void getAllInstancesByPrivateIpAddressIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where privateIpAddress in
        defaultInstanceFiltering(
            "privateIpAddress.in=" + DEFAULT_PRIVATE_IP_ADDRESS + "," + UPDATED_PRIVATE_IP_ADDRESS,
            "privateIpAddress.in=" + UPDATED_PRIVATE_IP_ADDRESS
        );
    }

    @Test
    @Transactional
    void getAllInstancesByPrivateIpAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where privateIpAddress is not null
        defaultInstanceFiltering("privateIpAddress.specified=true", "privateIpAddress.specified=false");
    }

    @Test
    @Transactional
    void getAllInstancesByPrivateIpAddressContainsSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where privateIpAddress contains
        defaultInstanceFiltering(
            "privateIpAddress.contains=" + DEFAULT_PRIVATE_IP_ADDRESS,
            "privateIpAddress.contains=" + UPDATED_PRIVATE_IP_ADDRESS
        );
    }

    @Test
    @Transactional
    void getAllInstancesByPrivateIpAddressNotContainsSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where privateIpAddress does not contain
        defaultInstanceFiltering(
            "privateIpAddress.doesNotContain=" + UPDATED_PRIVATE_IP_ADDRESS,
            "privateIpAddress.doesNotContain=" + DEFAULT_PRIVATE_IP_ADDRESS
        );
    }

    @Test
    @Transactional
    void getAllInstancesByPublicIpAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where publicIpAddress equals to
        defaultInstanceFiltering(
            "publicIpAddress.equals=" + DEFAULT_PUBLIC_IP_ADDRESS,
            "publicIpAddress.equals=" + UPDATED_PUBLIC_IP_ADDRESS
        );
    }

    @Test
    @Transactional
    void getAllInstancesByPublicIpAddressIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where publicIpAddress in
        defaultInstanceFiltering(
            "publicIpAddress.in=" + DEFAULT_PUBLIC_IP_ADDRESS + "," + UPDATED_PUBLIC_IP_ADDRESS,
            "publicIpAddress.in=" + UPDATED_PUBLIC_IP_ADDRESS
        );
    }

    @Test
    @Transactional
    void getAllInstancesByPublicIpAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where publicIpAddress is not null
        defaultInstanceFiltering("publicIpAddress.specified=true", "publicIpAddress.specified=false");
    }

    @Test
    @Transactional
    void getAllInstancesByPublicIpAddressContainsSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where publicIpAddress contains
        defaultInstanceFiltering(
            "publicIpAddress.contains=" + DEFAULT_PUBLIC_IP_ADDRESS,
            "publicIpAddress.contains=" + UPDATED_PUBLIC_IP_ADDRESS
        );
    }

    @Test
    @Transactional
    void getAllInstancesByPublicIpAddressNotContainsSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where publicIpAddress does not contain
        defaultInstanceFiltering(
            "publicIpAddress.doesNotContain=" + UPDATED_PUBLIC_IP_ADDRESS,
            "publicIpAddress.doesNotContain=" + DEFAULT_PUBLIC_IP_ADDRESS
        );
    }

    @Test
    @Transactional
    void getAllInstancesByPingEnabledIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where pingEnabled equals to
        defaultInstanceFiltering("pingEnabled.equals=" + DEFAULT_PING_ENABLED, "pingEnabled.equals=" + UPDATED_PING_ENABLED);
    }

    @Test
    @Transactional
    void getAllInstancesByPingEnabledIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where pingEnabled in
        defaultInstanceFiltering(
            "pingEnabled.in=" + DEFAULT_PING_ENABLED + "," + UPDATED_PING_ENABLED,
            "pingEnabled.in=" + UPDATED_PING_ENABLED
        );
    }

    @Test
    @Transactional
    void getAllInstancesByPingEnabledIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where pingEnabled is not null
        defaultInstanceFiltering("pingEnabled.specified=true", "pingEnabled.specified=false");
    }

    @Test
    @Transactional
    void getAllInstancesByPingIntervalIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where pingInterval equals to
        defaultInstanceFiltering("pingInterval.equals=" + DEFAULT_PING_INTERVAL, "pingInterval.equals=" + UPDATED_PING_INTERVAL);
    }

    @Test
    @Transactional
    void getAllInstancesByPingIntervalIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where pingInterval in
        defaultInstanceFiltering(
            "pingInterval.in=" + DEFAULT_PING_INTERVAL + "," + UPDATED_PING_INTERVAL,
            "pingInterval.in=" + UPDATED_PING_INTERVAL
        );
    }

    @Test
    @Transactional
    void getAllInstancesByPingIntervalIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where pingInterval is not null
        defaultInstanceFiltering("pingInterval.specified=true", "pingInterval.specified=false");
    }

    @Test
    @Transactional
    void getAllInstancesByPingIntervalIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where pingInterval is greater than or equal to
        defaultInstanceFiltering(
            "pingInterval.greaterThanOrEqual=" + DEFAULT_PING_INTERVAL,
            "pingInterval.greaterThanOrEqual=" + UPDATED_PING_INTERVAL
        );
    }

    @Test
    @Transactional
    void getAllInstancesByPingIntervalIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where pingInterval is less than or equal to
        defaultInstanceFiltering(
            "pingInterval.lessThanOrEqual=" + DEFAULT_PING_INTERVAL,
            "pingInterval.lessThanOrEqual=" + SMALLER_PING_INTERVAL
        );
    }

    @Test
    @Transactional
    void getAllInstancesByPingIntervalIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where pingInterval is less than
        defaultInstanceFiltering("pingInterval.lessThan=" + UPDATED_PING_INTERVAL, "pingInterval.lessThan=" + DEFAULT_PING_INTERVAL);
    }

    @Test
    @Transactional
    void getAllInstancesByPingIntervalIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where pingInterval is greater than
        defaultInstanceFiltering("pingInterval.greaterThan=" + SMALLER_PING_INTERVAL, "pingInterval.greaterThan=" + DEFAULT_PING_INTERVAL);
    }

    @Test
    @Transactional
    void getAllInstancesByPingTimeoutMsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where pingTimeoutMs equals to
        defaultInstanceFiltering("pingTimeoutMs.equals=" + DEFAULT_PING_TIMEOUT_MS, "pingTimeoutMs.equals=" + UPDATED_PING_TIMEOUT_MS);
    }

    @Test
    @Transactional
    void getAllInstancesByPingTimeoutMsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where pingTimeoutMs in
        defaultInstanceFiltering(
            "pingTimeoutMs.in=" + DEFAULT_PING_TIMEOUT_MS + "," + UPDATED_PING_TIMEOUT_MS,
            "pingTimeoutMs.in=" + UPDATED_PING_TIMEOUT_MS
        );
    }

    @Test
    @Transactional
    void getAllInstancesByPingTimeoutMsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where pingTimeoutMs is not null
        defaultInstanceFiltering("pingTimeoutMs.specified=true", "pingTimeoutMs.specified=false");
    }

    @Test
    @Transactional
    void getAllInstancesByPingTimeoutMsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where pingTimeoutMs is greater than or equal to
        defaultInstanceFiltering(
            "pingTimeoutMs.greaterThanOrEqual=" + DEFAULT_PING_TIMEOUT_MS,
            "pingTimeoutMs.greaterThanOrEqual=" + UPDATED_PING_TIMEOUT_MS
        );
    }

    @Test
    @Transactional
    void getAllInstancesByPingTimeoutMsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where pingTimeoutMs is less than or equal to
        defaultInstanceFiltering(
            "pingTimeoutMs.lessThanOrEqual=" + DEFAULT_PING_TIMEOUT_MS,
            "pingTimeoutMs.lessThanOrEqual=" + SMALLER_PING_TIMEOUT_MS
        );
    }

    @Test
    @Transactional
    void getAllInstancesByPingTimeoutMsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where pingTimeoutMs is less than
        defaultInstanceFiltering("pingTimeoutMs.lessThan=" + UPDATED_PING_TIMEOUT_MS, "pingTimeoutMs.lessThan=" + DEFAULT_PING_TIMEOUT_MS);
    }

    @Test
    @Transactional
    void getAllInstancesByPingTimeoutMsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where pingTimeoutMs is greater than
        defaultInstanceFiltering(
            "pingTimeoutMs.greaterThan=" + SMALLER_PING_TIMEOUT_MS,
            "pingTimeoutMs.greaterThan=" + DEFAULT_PING_TIMEOUT_MS
        );
    }

    @Test
    @Transactional
    void getAllInstancesByPingRetryCountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where pingRetryCount equals to
        defaultInstanceFiltering("pingRetryCount.equals=" + DEFAULT_PING_RETRY_COUNT, "pingRetryCount.equals=" + UPDATED_PING_RETRY_COUNT);
    }

    @Test
    @Transactional
    void getAllInstancesByPingRetryCountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where pingRetryCount in
        defaultInstanceFiltering(
            "pingRetryCount.in=" + DEFAULT_PING_RETRY_COUNT + "," + UPDATED_PING_RETRY_COUNT,
            "pingRetryCount.in=" + UPDATED_PING_RETRY_COUNT
        );
    }

    @Test
    @Transactional
    void getAllInstancesByPingRetryCountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where pingRetryCount is not null
        defaultInstanceFiltering("pingRetryCount.specified=true", "pingRetryCount.specified=false");
    }

    @Test
    @Transactional
    void getAllInstancesByPingRetryCountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where pingRetryCount is greater than or equal to
        defaultInstanceFiltering(
            "pingRetryCount.greaterThanOrEqual=" + DEFAULT_PING_RETRY_COUNT,
            "pingRetryCount.greaterThanOrEqual=" + UPDATED_PING_RETRY_COUNT
        );
    }

    @Test
    @Transactional
    void getAllInstancesByPingRetryCountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where pingRetryCount is less than or equal to
        defaultInstanceFiltering(
            "pingRetryCount.lessThanOrEqual=" + DEFAULT_PING_RETRY_COUNT,
            "pingRetryCount.lessThanOrEqual=" + SMALLER_PING_RETRY_COUNT
        );
    }

    @Test
    @Transactional
    void getAllInstancesByPingRetryCountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where pingRetryCount is less than
        defaultInstanceFiltering(
            "pingRetryCount.lessThan=" + UPDATED_PING_RETRY_COUNT,
            "pingRetryCount.lessThan=" + DEFAULT_PING_RETRY_COUNT
        );
    }

    @Test
    @Transactional
    void getAllInstancesByPingRetryCountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where pingRetryCount is greater than
        defaultInstanceFiltering(
            "pingRetryCount.greaterThan=" + SMALLER_PING_RETRY_COUNT,
            "pingRetryCount.greaterThan=" + DEFAULT_PING_RETRY_COUNT
        );
    }

    @Test
    @Transactional
    void getAllInstancesByHardwareMonitoringEnabledIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where hardwareMonitoringEnabled equals to
        defaultInstanceFiltering(
            "hardwareMonitoringEnabled.equals=" + DEFAULT_HARDWARE_MONITORING_ENABLED,
            "hardwareMonitoringEnabled.equals=" + UPDATED_HARDWARE_MONITORING_ENABLED
        );
    }

    @Test
    @Transactional
    void getAllInstancesByHardwareMonitoringEnabledIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where hardwareMonitoringEnabled in
        defaultInstanceFiltering(
            "hardwareMonitoringEnabled.in=" + DEFAULT_HARDWARE_MONITORING_ENABLED + "," + UPDATED_HARDWARE_MONITORING_ENABLED,
            "hardwareMonitoringEnabled.in=" + UPDATED_HARDWARE_MONITORING_ENABLED
        );
    }

    @Test
    @Transactional
    void getAllInstancesByHardwareMonitoringEnabledIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where hardwareMonitoringEnabled is not null
        defaultInstanceFiltering("hardwareMonitoringEnabled.specified=true", "hardwareMonitoringEnabled.specified=false");
    }

    @Test
    @Transactional
    void getAllInstancesByHardwareMonitoringIntervalIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where hardwareMonitoringInterval equals to
        defaultInstanceFiltering(
            "hardwareMonitoringInterval.equals=" + DEFAULT_HARDWARE_MONITORING_INTERVAL,
            "hardwareMonitoringInterval.equals=" + UPDATED_HARDWARE_MONITORING_INTERVAL
        );
    }

    @Test
    @Transactional
    void getAllInstancesByHardwareMonitoringIntervalIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where hardwareMonitoringInterval in
        defaultInstanceFiltering(
            "hardwareMonitoringInterval.in=" + DEFAULT_HARDWARE_MONITORING_INTERVAL + "," + UPDATED_HARDWARE_MONITORING_INTERVAL,
            "hardwareMonitoringInterval.in=" + UPDATED_HARDWARE_MONITORING_INTERVAL
        );
    }

    @Test
    @Transactional
    void getAllInstancesByHardwareMonitoringIntervalIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where hardwareMonitoringInterval is not null
        defaultInstanceFiltering("hardwareMonitoringInterval.specified=true", "hardwareMonitoringInterval.specified=false");
    }

    @Test
    @Transactional
    void getAllInstancesByHardwareMonitoringIntervalIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where hardwareMonitoringInterval is greater than or equal to
        defaultInstanceFiltering(
            "hardwareMonitoringInterval.greaterThanOrEqual=" + DEFAULT_HARDWARE_MONITORING_INTERVAL,
            "hardwareMonitoringInterval.greaterThanOrEqual=" + UPDATED_HARDWARE_MONITORING_INTERVAL
        );
    }

    @Test
    @Transactional
    void getAllInstancesByHardwareMonitoringIntervalIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where hardwareMonitoringInterval is less than or equal to
        defaultInstanceFiltering(
            "hardwareMonitoringInterval.lessThanOrEqual=" + DEFAULT_HARDWARE_MONITORING_INTERVAL,
            "hardwareMonitoringInterval.lessThanOrEqual=" + SMALLER_HARDWARE_MONITORING_INTERVAL
        );
    }

    @Test
    @Transactional
    void getAllInstancesByHardwareMonitoringIntervalIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where hardwareMonitoringInterval is less than
        defaultInstanceFiltering(
            "hardwareMonitoringInterval.lessThan=" + UPDATED_HARDWARE_MONITORING_INTERVAL,
            "hardwareMonitoringInterval.lessThan=" + DEFAULT_HARDWARE_MONITORING_INTERVAL
        );
    }

    @Test
    @Transactional
    void getAllInstancesByHardwareMonitoringIntervalIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where hardwareMonitoringInterval is greater than
        defaultInstanceFiltering(
            "hardwareMonitoringInterval.greaterThan=" + SMALLER_HARDWARE_MONITORING_INTERVAL,
            "hardwareMonitoringInterval.greaterThan=" + DEFAULT_HARDWARE_MONITORING_INTERVAL
        );
    }

    @Test
    @Transactional
    void getAllInstancesByCpuWarningThresholdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where cpuWarningThreshold equals to
        defaultInstanceFiltering(
            "cpuWarningThreshold.equals=" + DEFAULT_CPU_WARNING_THRESHOLD,
            "cpuWarningThreshold.equals=" + UPDATED_CPU_WARNING_THRESHOLD
        );
    }

    @Test
    @Transactional
    void getAllInstancesByCpuWarningThresholdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where cpuWarningThreshold in
        defaultInstanceFiltering(
            "cpuWarningThreshold.in=" + DEFAULT_CPU_WARNING_THRESHOLD + "," + UPDATED_CPU_WARNING_THRESHOLD,
            "cpuWarningThreshold.in=" + UPDATED_CPU_WARNING_THRESHOLD
        );
    }

    @Test
    @Transactional
    void getAllInstancesByCpuWarningThresholdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where cpuWarningThreshold is not null
        defaultInstanceFiltering("cpuWarningThreshold.specified=true", "cpuWarningThreshold.specified=false");
    }

    @Test
    @Transactional
    void getAllInstancesByCpuWarningThresholdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where cpuWarningThreshold is greater than or equal to
        defaultInstanceFiltering(
            "cpuWarningThreshold.greaterThanOrEqual=" + DEFAULT_CPU_WARNING_THRESHOLD,
            "cpuWarningThreshold.greaterThanOrEqual=" + UPDATED_CPU_WARNING_THRESHOLD
        );
    }

    @Test
    @Transactional
    void getAllInstancesByCpuWarningThresholdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where cpuWarningThreshold is less than or equal to
        defaultInstanceFiltering(
            "cpuWarningThreshold.lessThanOrEqual=" + DEFAULT_CPU_WARNING_THRESHOLD,
            "cpuWarningThreshold.lessThanOrEqual=" + SMALLER_CPU_WARNING_THRESHOLD
        );
    }

    @Test
    @Transactional
    void getAllInstancesByCpuWarningThresholdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where cpuWarningThreshold is less than
        defaultInstanceFiltering(
            "cpuWarningThreshold.lessThan=" + UPDATED_CPU_WARNING_THRESHOLD,
            "cpuWarningThreshold.lessThan=" + DEFAULT_CPU_WARNING_THRESHOLD
        );
    }

    @Test
    @Transactional
    void getAllInstancesByCpuWarningThresholdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where cpuWarningThreshold is greater than
        defaultInstanceFiltering(
            "cpuWarningThreshold.greaterThan=" + SMALLER_CPU_WARNING_THRESHOLD,
            "cpuWarningThreshold.greaterThan=" + DEFAULT_CPU_WARNING_THRESHOLD
        );
    }

    @Test
    @Transactional
    void getAllInstancesByCpuDangerThresholdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where cpuDangerThreshold equals to
        defaultInstanceFiltering(
            "cpuDangerThreshold.equals=" + DEFAULT_CPU_DANGER_THRESHOLD,
            "cpuDangerThreshold.equals=" + UPDATED_CPU_DANGER_THRESHOLD
        );
    }

    @Test
    @Transactional
    void getAllInstancesByCpuDangerThresholdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where cpuDangerThreshold in
        defaultInstanceFiltering(
            "cpuDangerThreshold.in=" + DEFAULT_CPU_DANGER_THRESHOLD + "," + UPDATED_CPU_DANGER_THRESHOLD,
            "cpuDangerThreshold.in=" + UPDATED_CPU_DANGER_THRESHOLD
        );
    }

    @Test
    @Transactional
    void getAllInstancesByCpuDangerThresholdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where cpuDangerThreshold is not null
        defaultInstanceFiltering("cpuDangerThreshold.specified=true", "cpuDangerThreshold.specified=false");
    }

    @Test
    @Transactional
    void getAllInstancesByCpuDangerThresholdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where cpuDangerThreshold is greater than or equal to
        defaultInstanceFiltering(
            "cpuDangerThreshold.greaterThanOrEqual=" + DEFAULT_CPU_DANGER_THRESHOLD,
            "cpuDangerThreshold.greaterThanOrEqual=" + UPDATED_CPU_DANGER_THRESHOLD
        );
    }

    @Test
    @Transactional
    void getAllInstancesByCpuDangerThresholdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where cpuDangerThreshold is less than or equal to
        defaultInstanceFiltering(
            "cpuDangerThreshold.lessThanOrEqual=" + DEFAULT_CPU_DANGER_THRESHOLD,
            "cpuDangerThreshold.lessThanOrEqual=" + SMALLER_CPU_DANGER_THRESHOLD
        );
    }

    @Test
    @Transactional
    void getAllInstancesByCpuDangerThresholdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where cpuDangerThreshold is less than
        defaultInstanceFiltering(
            "cpuDangerThreshold.lessThan=" + UPDATED_CPU_DANGER_THRESHOLD,
            "cpuDangerThreshold.lessThan=" + DEFAULT_CPU_DANGER_THRESHOLD
        );
    }

    @Test
    @Transactional
    void getAllInstancesByCpuDangerThresholdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where cpuDangerThreshold is greater than
        defaultInstanceFiltering(
            "cpuDangerThreshold.greaterThan=" + SMALLER_CPU_DANGER_THRESHOLD,
            "cpuDangerThreshold.greaterThan=" + DEFAULT_CPU_DANGER_THRESHOLD
        );
    }

    @Test
    @Transactional
    void getAllInstancesByMemoryWarningThresholdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where memoryWarningThreshold equals to
        defaultInstanceFiltering(
            "memoryWarningThreshold.equals=" + DEFAULT_MEMORY_WARNING_THRESHOLD,
            "memoryWarningThreshold.equals=" + UPDATED_MEMORY_WARNING_THRESHOLD
        );
    }

    @Test
    @Transactional
    void getAllInstancesByMemoryWarningThresholdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where memoryWarningThreshold in
        defaultInstanceFiltering(
            "memoryWarningThreshold.in=" + DEFAULT_MEMORY_WARNING_THRESHOLD + "," + UPDATED_MEMORY_WARNING_THRESHOLD,
            "memoryWarningThreshold.in=" + UPDATED_MEMORY_WARNING_THRESHOLD
        );
    }

    @Test
    @Transactional
    void getAllInstancesByMemoryWarningThresholdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where memoryWarningThreshold is not null
        defaultInstanceFiltering("memoryWarningThreshold.specified=true", "memoryWarningThreshold.specified=false");
    }

    @Test
    @Transactional
    void getAllInstancesByMemoryWarningThresholdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where memoryWarningThreshold is greater than or equal to
        defaultInstanceFiltering(
            "memoryWarningThreshold.greaterThanOrEqual=" + DEFAULT_MEMORY_WARNING_THRESHOLD,
            "memoryWarningThreshold.greaterThanOrEqual=" + UPDATED_MEMORY_WARNING_THRESHOLD
        );
    }

    @Test
    @Transactional
    void getAllInstancesByMemoryWarningThresholdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where memoryWarningThreshold is less than or equal to
        defaultInstanceFiltering(
            "memoryWarningThreshold.lessThanOrEqual=" + DEFAULT_MEMORY_WARNING_THRESHOLD,
            "memoryWarningThreshold.lessThanOrEqual=" + SMALLER_MEMORY_WARNING_THRESHOLD
        );
    }

    @Test
    @Transactional
    void getAllInstancesByMemoryWarningThresholdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where memoryWarningThreshold is less than
        defaultInstanceFiltering(
            "memoryWarningThreshold.lessThan=" + UPDATED_MEMORY_WARNING_THRESHOLD,
            "memoryWarningThreshold.lessThan=" + DEFAULT_MEMORY_WARNING_THRESHOLD
        );
    }

    @Test
    @Transactional
    void getAllInstancesByMemoryWarningThresholdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where memoryWarningThreshold is greater than
        defaultInstanceFiltering(
            "memoryWarningThreshold.greaterThan=" + SMALLER_MEMORY_WARNING_THRESHOLD,
            "memoryWarningThreshold.greaterThan=" + DEFAULT_MEMORY_WARNING_THRESHOLD
        );
    }

    @Test
    @Transactional
    void getAllInstancesByMemoryDangerThresholdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where memoryDangerThreshold equals to
        defaultInstanceFiltering(
            "memoryDangerThreshold.equals=" + DEFAULT_MEMORY_DANGER_THRESHOLD,
            "memoryDangerThreshold.equals=" + UPDATED_MEMORY_DANGER_THRESHOLD
        );
    }

    @Test
    @Transactional
    void getAllInstancesByMemoryDangerThresholdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where memoryDangerThreshold in
        defaultInstanceFiltering(
            "memoryDangerThreshold.in=" + DEFAULT_MEMORY_DANGER_THRESHOLD + "," + UPDATED_MEMORY_DANGER_THRESHOLD,
            "memoryDangerThreshold.in=" + UPDATED_MEMORY_DANGER_THRESHOLD
        );
    }

    @Test
    @Transactional
    void getAllInstancesByMemoryDangerThresholdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where memoryDangerThreshold is not null
        defaultInstanceFiltering("memoryDangerThreshold.specified=true", "memoryDangerThreshold.specified=false");
    }

    @Test
    @Transactional
    void getAllInstancesByMemoryDangerThresholdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where memoryDangerThreshold is greater than or equal to
        defaultInstanceFiltering(
            "memoryDangerThreshold.greaterThanOrEqual=" + DEFAULT_MEMORY_DANGER_THRESHOLD,
            "memoryDangerThreshold.greaterThanOrEqual=" + UPDATED_MEMORY_DANGER_THRESHOLD
        );
    }

    @Test
    @Transactional
    void getAllInstancesByMemoryDangerThresholdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where memoryDangerThreshold is less than or equal to
        defaultInstanceFiltering(
            "memoryDangerThreshold.lessThanOrEqual=" + DEFAULT_MEMORY_DANGER_THRESHOLD,
            "memoryDangerThreshold.lessThanOrEqual=" + SMALLER_MEMORY_DANGER_THRESHOLD
        );
    }

    @Test
    @Transactional
    void getAllInstancesByMemoryDangerThresholdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where memoryDangerThreshold is less than
        defaultInstanceFiltering(
            "memoryDangerThreshold.lessThan=" + UPDATED_MEMORY_DANGER_THRESHOLD,
            "memoryDangerThreshold.lessThan=" + DEFAULT_MEMORY_DANGER_THRESHOLD
        );
    }

    @Test
    @Transactional
    void getAllInstancesByMemoryDangerThresholdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where memoryDangerThreshold is greater than
        defaultInstanceFiltering(
            "memoryDangerThreshold.greaterThan=" + SMALLER_MEMORY_DANGER_THRESHOLD,
            "memoryDangerThreshold.greaterThan=" + DEFAULT_MEMORY_DANGER_THRESHOLD
        );
    }

    @Test
    @Transactional
    void getAllInstancesByDiskWarningThresholdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where diskWarningThreshold equals to
        defaultInstanceFiltering(
            "diskWarningThreshold.equals=" + DEFAULT_DISK_WARNING_THRESHOLD,
            "diskWarningThreshold.equals=" + UPDATED_DISK_WARNING_THRESHOLD
        );
    }

    @Test
    @Transactional
    void getAllInstancesByDiskWarningThresholdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where diskWarningThreshold in
        defaultInstanceFiltering(
            "diskWarningThreshold.in=" + DEFAULT_DISK_WARNING_THRESHOLD + "," + UPDATED_DISK_WARNING_THRESHOLD,
            "diskWarningThreshold.in=" + UPDATED_DISK_WARNING_THRESHOLD
        );
    }

    @Test
    @Transactional
    void getAllInstancesByDiskWarningThresholdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where diskWarningThreshold is not null
        defaultInstanceFiltering("diskWarningThreshold.specified=true", "diskWarningThreshold.specified=false");
    }

    @Test
    @Transactional
    void getAllInstancesByDiskWarningThresholdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where diskWarningThreshold is greater than or equal to
        defaultInstanceFiltering(
            "diskWarningThreshold.greaterThanOrEqual=" + DEFAULT_DISK_WARNING_THRESHOLD,
            "diskWarningThreshold.greaterThanOrEqual=" + UPDATED_DISK_WARNING_THRESHOLD
        );
    }

    @Test
    @Transactional
    void getAllInstancesByDiskWarningThresholdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where diskWarningThreshold is less than or equal to
        defaultInstanceFiltering(
            "diskWarningThreshold.lessThanOrEqual=" + DEFAULT_DISK_WARNING_THRESHOLD,
            "diskWarningThreshold.lessThanOrEqual=" + SMALLER_DISK_WARNING_THRESHOLD
        );
    }

    @Test
    @Transactional
    void getAllInstancesByDiskWarningThresholdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where diskWarningThreshold is less than
        defaultInstanceFiltering(
            "diskWarningThreshold.lessThan=" + UPDATED_DISK_WARNING_THRESHOLD,
            "diskWarningThreshold.lessThan=" + DEFAULT_DISK_WARNING_THRESHOLD
        );
    }

    @Test
    @Transactional
    void getAllInstancesByDiskWarningThresholdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where diskWarningThreshold is greater than
        defaultInstanceFiltering(
            "diskWarningThreshold.greaterThan=" + SMALLER_DISK_WARNING_THRESHOLD,
            "diskWarningThreshold.greaterThan=" + DEFAULT_DISK_WARNING_THRESHOLD
        );
    }

    @Test
    @Transactional
    void getAllInstancesByDiskDangerThresholdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where diskDangerThreshold equals to
        defaultInstanceFiltering(
            "diskDangerThreshold.equals=" + DEFAULT_DISK_DANGER_THRESHOLD,
            "diskDangerThreshold.equals=" + UPDATED_DISK_DANGER_THRESHOLD
        );
    }

    @Test
    @Transactional
    void getAllInstancesByDiskDangerThresholdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where diskDangerThreshold in
        defaultInstanceFiltering(
            "diskDangerThreshold.in=" + DEFAULT_DISK_DANGER_THRESHOLD + "," + UPDATED_DISK_DANGER_THRESHOLD,
            "diskDangerThreshold.in=" + UPDATED_DISK_DANGER_THRESHOLD
        );
    }

    @Test
    @Transactional
    void getAllInstancesByDiskDangerThresholdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where diskDangerThreshold is not null
        defaultInstanceFiltering("diskDangerThreshold.specified=true", "diskDangerThreshold.specified=false");
    }

    @Test
    @Transactional
    void getAllInstancesByDiskDangerThresholdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where diskDangerThreshold is greater than or equal to
        defaultInstanceFiltering(
            "diskDangerThreshold.greaterThanOrEqual=" + DEFAULT_DISK_DANGER_THRESHOLD,
            "diskDangerThreshold.greaterThanOrEqual=" + UPDATED_DISK_DANGER_THRESHOLD
        );
    }

    @Test
    @Transactional
    void getAllInstancesByDiskDangerThresholdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where diskDangerThreshold is less than or equal to
        defaultInstanceFiltering(
            "diskDangerThreshold.lessThanOrEqual=" + DEFAULT_DISK_DANGER_THRESHOLD,
            "diskDangerThreshold.lessThanOrEqual=" + SMALLER_DISK_DANGER_THRESHOLD
        );
    }

    @Test
    @Transactional
    void getAllInstancesByDiskDangerThresholdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where diskDangerThreshold is less than
        defaultInstanceFiltering(
            "diskDangerThreshold.lessThan=" + UPDATED_DISK_DANGER_THRESHOLD,
            "diskDangerThreshold.lessThan=" + DEFAULT_DISK_DANGER_THRESHOLD
        );
    }

    @Test
    @Transactional
    void getAllInstancesByDiskDangerThresholdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where diskDangerThreshold is greater than
        defaultInstanceFiltering(
            "diskDangerThreshold.greaterThan=" + SMALLER_DISK_DANGER_THRESHOLD,
            "diskDangerThreshold.greaterThan=" + DEFAULT_DISK_DANGER_THRESHOLD
        );
    }

    @Test
    @Transactional
    void getAllInstancesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where createdAt equals to
        defaultInstanceFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllInstancesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where createdAt in
        defaultInstanceFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllInstancesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where createdAt is not null
        defaultInstanceFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllInstancesByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where updatedAt equals to
        defaultInstanceFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllInstancesByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where updatedAt in
        defaultInstanceFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllInstancesByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where updatedAt is not null
        defaultInstanceFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllInstancesByLastPingAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where lastPingAt equals to
        defaultInstanceFiltering("lastPingAt.equals=" + DEFAULT_LAST_PING_AT, "lastPingAt.equals=" + UPDATED_LAST_PING_AT);
    }

    @Test
    @Transactional
    void getAllInstancesByLastPingAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where lastPingAt in
        defaultInstanceFiltering(
            "lastPingAt.in=" + DEFAULT_LAST_PING_AT + "," + UPDATED_LAST_PING_AT,
            "lastPingAt.in=" + UPDATED_LAST_PING_AT
        );
    }

    @Test
    @Transactional
    void getAllInstancesByLastPingAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where lastPingAt is not null
        defaultInstanceFiltering("lastPingAt.specified=true", "lastPingAt.specified=false");
    }

    @Test
    @Transactional
    void getAllInstancesByLastHardwareCheckAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where lastHardwareCheckAt equals to
        defaultInstanceFiltering(
            "lastHardwareCheckAt.equals=" + DEFAULT_LAST_HARDWARE_CHECK_AT,
            "lastHardwareCheckAt.equals=" + UPDATED_LAST_HARDWARE_CHECK_AT
        );
    }

    @Test
    @Transactional
    void getAllInstancesByLastHardwareCheckAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where lastHardwareCheckAt in
        defaultInstanceFiltering(
            "lastHardwareCheckAt.in=" + DEFAULT_LAST_HARDWARE_CHECK_AT + "," + UPDATED_LAST_HARDWARE_CHECK_AT,
            "lastHardwareCheckAt.in=" + UPDATED_LAST_HARDWARE_CHECK_AT
        );
    }

    @Test
    @Transactional
    void getAllInstancesByLastHardwareCheckAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where lastHardwareCheckAt is not null
        defaultInstanceFiltering("lastHardwareCheckAt.specified=true", "lastHardwareCheckAt.specified=false");
    }

    @Test
    @Transactional
    void getAllInstancesByDatacenterIsEqualToSomething() throws Exception {
        Datacenter datacenter;
        if (TestUtil.findAll(em, Datacenter.class).isEmpty()) {
            instanceRepository.saveAndFlush(instance);
            datacenter = DatacenterResourceIT.createEntity();
        } else {
            datacenter = TestUtil.findAll(em, Datacenter.class).get(0);
        }
        em.persist(datacenter);
        em.flush();
        instance.setDatacenter(datacenter);
        instanceRepository.saveAndFlush(instance);
        Long datacenterId = datacenter.getId();
        // Get all the instanceList where datacenter equals to datacenterId
        defaultInstanceShouldBeFound("datacenterId.equals=" + datacenterId);

        // Get all the instanceList where datacenter equals to (datacenterId + 1)
        defaultInstanceShouldNotBeFound("datacenterId.equals=" + (datacenterId + 1));
    }

    @Test
    @Transactional
    void getAllInstancesByAgentIsEqualToSomething() throws Exception {
        Agent agent;
        if (TestUtil.findAll(em, Agent.class).isEmpty()) {
            instanceRepository.saveAndFlush(instance);
            agent = AgentResourceIT.createEntity();
        } else {
            agent = TestUtil.findAll(em, Agent.class).get(0);
        }
        em.persist(agent);
        em.flush();
        instance.setAgent(agent);
        instanceRepository.saveAndFlush(instance);
        Long agentId = agent.getId();
        // Get all the instanceList where agent equals to agentId
        defaultInstanceShouldBeFound("agentId.equals=" + agentId);

        // Get all the instanceList where agent equals to (agentId + 1)
        defaultInstanceShouldNotBeFound("agentId.equals=" + (agentId + 1));
    }

    private void defaultInstanceFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultInstanceShouldBeFound(shouldBeFound);
        defaultInstanceShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultInstanceShouldBeFound(String filter) throws Exception {
        restMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(instance.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].hostname").value(hasItem(DEFAULT_HOSTNAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].instanceType").value(hasItem(DEFAULT_INSTANCE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].monitoringType").value(hasItem(DEFAULT_MONITORING_TYPE.toString())))
            .andExpect(jsonPath("$.[*].operatingSystem").value(hasItem(DEFAULT_OPERATING_SYSTEM.toString())))
            .andExpect(jsonPath("$.[*].platform").value(hasItem(DEFAULT_PLATFORM)))
            .andExpect(jsonPath("$.[*].privateIpAddress").value(hasItem(DEFAULT_PRIVATE_IP_ADDRESS)))
            .andExpect(jsonPath("$.[*].publicIpAddress").value(hasItem(DEFAULT_PUBLIC_IP_ADDRESS)))
            .andExpect(jsonPath("$.[*].tags").value(hasItem(DEFAULT_TAGS)))
            .andExpect(jsonPath("$.[*].pingEnabled").value(hasItem(DEFAULT_PING_ENABLED)))
            .andExpect(jsonPath("$.[*].pingInterval").value(hasItem(DEFAULT_PING_INTERVAL)))
            .andExpect(jsonPath("$.[*].pingTimeoutMs").value(hasItem(DEFAULT_PING_TIMEOUT_MS)))
            .andExpect(jsonPath("$.[*].pingRetryCount").value(hasItem(DEFAULT_PING_RETRY_COUNT)))
            .andExpect(jsonPath("$.[*].hardwareMonitoringEnabled").value(hasItem(DEFAULT_HARDWARE_MONITORING_ENABLED)))
            .andExpect(jsonPath("$.[*].hardwareMonitoringInterval").value(hasItem(DEFAULT_HARDWARE_MONITORING_INTERVAL)))
            .andExpect(jsonPath("$.[*].cpuWarningThreshold").value(hasItem(DEFAULT_CPU_WARNING_THRESHOLD)))
            .andExpect(jsonPath("$.[*].cpuDangerThreshold").value(hasItem(DEFAULT_CPU_DANGER_THRESHOLD)))
            .andExpect(jsonPath("$.[*].memoryWarningThreshold").value(hasItem(DEFAULT_MEMORY_WARNING_THRESHOLD)))
            .andExpect(jsonPath("$.[*].memoryDangerThreshold").value(hasItem(DEFAULT_MEMORY_DANGER_THRESHOLD)))
            .andExpect(jsonPath("$.[*].diskWarningThreshold").value(hasItem(DEFAULT_DISK_WARNING_THRESHOLD)))
            .andExpect(jsonPath("$.[*].diskDangerThreshold").value(hasItem(DEFAULT_DISK_DANGER_THRESHOLD)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].lastPingAt").value(hasItem(DEFAULT_LAST_PING_AT.toString())))
            .andExpect(jsonPath("$.[*].lastHardwareCheckAt").value(hasItem(DEFAULT_LAST_HARDWARE_CHECK_AT.toString())));

        // Check, that the count call also returns 1
        restMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultInstanceShouldNotBeFound(String filter) throws Exception {
        restMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingInstance() throws Exception {
        // Get the instance
        restMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInstance() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the instance
        Instance updatedInstance = instanceRepository.findById(instance.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedInstance are not directly saved in db
        em.detach(updatedInstance);
        updatedInstance
            .name(UPDATED_NAME)
            .hostname(UPDATED_HOSTNAME)
            .description(UPDATED_DESCRIPTION)
            .instanceType(UPDATED_INSTANCE_TYPE)
            .monitoringType(UPDATED_MONITORING_TYPE)
            .operatingSystem(UPDATED_OPERATING_SYSTEM)
            .platform(UPDATED_PLATFORM)
            .privateIpAddress(UPDATED_PRIVATE_IP_ADDRESS)
            .publicIpAddress(UPDATED_PUBLIC_IP_ADDRESS)
            .tags(UPDATED_TAGS)
            .pingEnabled(UPDATED_PING_ENABLED)
            .pingInterval(UPDATED_PING_INTERVAL)
            .pingTimeoutMs(UPDATED_PING_TIMEOUT_MS)
            .pingRetryCount(UPDATED_PING_RETRY_COUNT)
            .hardwareMonitoringEnabled(UPDATED_HARDWARE_MONITORING_ENABLED)
            .hardwareMonitoringInterval(UPDATED_HARDWARE_MONITORING_INTERVAL)
            .cpuWarningThreshold(UPDATED_CPU_WARNING_THRESHOLD)
            .cpuDangerThreshold(UPDATED_CPU_DANGER_THRESHOLD)
            .memoryWarningThreshold(UPDATED_MEMORY_WARNING_THRESHOLD)
            .memoryDangerThreshold(UPDATED_MEMORY_DANGER_THRESHOLD)
            .diskWarningThreshold(UPDATED_DISK_WARNING_THRESHOLD)
            .diskDangerThreshold(UPDATED_DISK_DANGER_THRESHOLD)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .lastPingAt(UPDATED_LAST_PING_AT)
            .lastHardwareCheckAt(UPDATED_LAST_HARDWARE_CHECK_AT);
        InstanceDTO instanceDTO = instanceMapper.toDto(updatedInstance);

        restMockMvc
            .perform(
                put(ENTITY_API_URL_ID, instanceDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(instanceDTO))
            )
            .andExpect(status().isOk());

        // Validate the Instance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInstanceAllPropertiesEquals(updatedInstance, getPersistedEntity(updatedInstance.getId()));

    }

    @Test
    @Transactional
    void putNonExistingInstance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        instance.setId(longCount.incrementAndGet());

        // Create the Instance
        InstanceDTO instanceDTO = instanceMapper.toDto(instance);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMockMvc
            .perform(
                put(ENTITY_API_URL_ID, instanceDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(instanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Instance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchInstance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        instance.setId(longCount.incrementAndGet());

        // Create the Instance
        InstanceDTO instanceDTO = instanceMapper.toDto(instance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(instanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Instance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInstance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        instance.setId(longCount.incrementAndGet());

        // Create the Instance
        InstanceDTO instanceDTO = instanceMapper.toDto(instance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(instanceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Instance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateInstanceWithPatch() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the instance using partial update
        Instance partialUpdatedInstance = new Instance();
        partialUpdatedInstance.setId(instance.getId());

        partialUpdatedInstance
            .name(UPDATED_NAME)
            .hostname(UPDATED_HOSTNAME)
            .monitoringType(UPDATED_MONITORING_TYPE)
            .operatingSystem(UPDATED_OPERATING_SYSTEM)
            .pingRetryCount(UPDATED_PING_RETRY_COUNT)
            .hardwareMonitoringEnabled(UPDATED_HARDWARE_MONITORING_ENABLED)
            .hardwareMonitoringInterval(UPDATED_HARDWARE_MONITORING_INTERVAL)
            .cpuDangerThreshold(UPDATED_CPU_DANGER_THRESHOLD)
            .memoryWarningThreshold(UPDATED_MEMORY_WARNING_THRESHOLD)
            .memoryDangerThreshold(UPDATED_MEMORY_DANGER_THRESHOLD)
            .diskWarningThreshold(UPDATED_DISK_WARNING_THRESHOLD)
            .diskDangerThreshold(UPDATED_DISK_DANGER_THRESHOLD)
            .createdAt(UPDATED_CREATED_AT)
            .lastPingAt(UPDATED_LAST_PING_AT);

        restMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInstance.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInstance))
            )
            .andExpect(status().isOk());

        // Validate the Instance in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInstanceUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedInstance, instance), getPersistedEntity(instance.getId()));
    }

    @Test
    @Transactional
    void fullUpdateInstanceWithPatch() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the instance using partial update
        Instance partialUpdatedInstance = new Instance();
        partialUpdatedInstance.setId(instance.getId());

        partialUpdatedInstance
            .name(UPDATED_NAME)
            .hostname(UPDATED_HOSTNAME)
            .description(UPDATED_DESCRIPTION)
            .instanceType(UPDATED_INSTANCE_TYPE)
            .monitoringType(UPDATED_MONITORING_TYPE)
            .operatingSystem(UPDATED_OPERATING_SYSTEM)
            .platform(UPDATED_PLATFORM)
            .privateIpAddress(UPDATED_PRIVATE_IP_ADDRESS)
            .publicIpAddress(UPDATED_PUBLIC_IP_ADDRESS)
            .tags(UPDATED_TAGS)
            .pingEnabled(UPDATED_PING_ENABLED)
            .pingInterval(UPDATED_PING_INTERVAL)
            .pingTimeoutMs(UPDATED_PING_TIMEOUT_MS)
            .pingRetryCount(UPDATED_PING_RETRY_COUNT)
            .hardwareMonitoringEnabled(UPDATED_HARDWARE_MONITORING_ENABLED)
            .hardwareMonitoringInterval(UPDATED_HARDWARE_MONITORING_INTERVAL)
            .cpuWarningThreshold(UPDATED_CPU_WARNING_THRESHOLD)
            .cpuDangerThreshold(UPDATED_CPU_DANGER_THRESHOLD)
            .memoryWarningThreshold(UPDATED_MEMORY_WARNING_THRESHOLD)
            .memoryDangerThreshold(UPDATED_MEMORY_DANGER_THRESHOLD)
            .diskWarningThreshold(UPDATED_DISK_WARNING_THRESHOLD)
            .diskDangerThreshold(UPDATED_DISK_DANGER_THRESHOLD)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .lastPingAt(UPDATED_LAST_PING_AT)
            .lastHardwareCheckAt(UPDATED_LAST_HARDWARE_CHECK_AT);

        restMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInstance.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInstance))
            )
            .andExpect(status().isOk());

        // Validate the Instance in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInstanceUpdatableFieldsEquals(partialUpdatedInstance, getPersistedEntity(partialUpdatedInstance.getId()));
    }

    @Test
    @Transactional
    void patchNonExistingInstance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        instance.setId(longCount.incrementAndGet());

        // Create the Instance
        InstanceDTO instanceDTO = instanceMapper.toDto(instance);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, instanceDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(instanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Instance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInstance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        instance.setId(longCount.incrementAndGet());

        // Create the Instance
        InstanceDTO instanceDTO = instanceMapper.toDto(instance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(instanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Instance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInstance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        instance.setId(longCount.incrementAndGet());

        // Create the Instance
        InstanceDTO instanceDTO = instanceMapper.toDto(instance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(instanceDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Instance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteInstance() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);
        instanceRepository.save(instance);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the instance
        restMockMvc
            .perform(delete(ENTITY_API_URL_ID, instance.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    @Test
    @Transactional
    void searchInstance() throws Exception {
        insertedInstance = instanceRepository.saveAndFlush(instance);
        flushAndClear();
        SearchTestHelper.testFullTextSearch(restMockMvc, ENTITY_SEARCH_API_URL, DEFAULT_NAME.substring(0, 3));
    }

    @Test
    @Transactional
    void searchInstancePrefix() throws Exception {
        insertedInstance = instanceRepository.saveAndFlush(instance);
        flushAndClear();
        SearchTestHelper.testPrefixSearch(restMockMvc, ENTITY_SEARCH_API_URL, DEFAULT_NAME.substring(0, 2));
    }

    @Test
    @Transactional
    void searchInstanceFuzzy() throws Exception {
        insertedInstance = instanceRepository.saveAndFlush(instance);
        flushAndClear();
        SearchTestHelper.testFuzzySearch(restMockMvc, ENTITY_SEARCH_API_URL, DEFAULT_NAME);
    }

    @Test
    @Transactional
    void searchInstanceWithHighlight() throws Exception {
        insertedInstance = instanceRepository.saveAndFlush(instance);
        flushAndClear();
        SearchTestHelper.testHighlightSearch(restMockMvc, ENTITY_SEARCH_API_URL, DEFAULT_NAME.substring(0, 3));
    }

    @Test
    @Transactional
    void searchInstanceEmptyQuery() throws Exception {
        insertedInstance = instanceRepository.saveAndFlush(instance);
        flushAndClear();
        SearchTestHelper.testEmptyQuerySearch(restMockMvc, ENTITY_SEARCH_API_URL);
    }

}
