package vibhuvi.oio.inframirror.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static vibhuvi.oio.inframirror.domain.MonitoredServiceAsserts.*;
import static vibhuvi.oio.inframirror.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.IntegrationTest;
import vibhuvi.oio.inframirror.domain.Datacenter;
import vibhuvi.oio.inframirror.domain.MonitoredService;
import vibhuvi.oio.inframirror.repository.MonitoredServiceRepository;
import vibhuvi.oio.inframirror.repository.search.MonitoredServiceSearchRepository;
import vibhuvi.oio.inframirror.service.dto.MonitoredServiceDTO;
import vibhuvi.oio.inframirror.service.mapper.MonitoredServiceMapper;

/**
 * Integration tests for the {@link MonitoredServiceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MonitoredServiceResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_SERVICE_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_SERVICE_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_ENVIRONMENT = "AAAAAAAAAA";
    private static final String UPDATED_ENVIRONMENT = "BBBBBBBBBB";

    private static final Boolean DEFAULT_MONITORING_ENABLED = false;
    private static final Boolean UPDATED_MONITORING_ENABLED = true;

    private static final Boolean DEFAULT_CLUSTER_MONITORING_ENABLED = false;
    private static final Boolean UPDATED_CLUSTER_MONITORING_ENABLED = true;

    private static final Integer DEFAULT_INTERVAL_SECONDS = 1;
    private static final Integer UPDATED_INTERVAL_SECONDS = 2;
    private static final Integer SMALLER_INTERVAL_SECONDS = 1 - 1;

    private static final Integer DEFAULT_TIMEOUT_MS = 1;
    private static final Integer UPDATED_TIMEOUT_MS = 2;
    private static final Integer SMALLER_TIMEOUT_MS = 1 - 1;

    private static final Integer DEFAULT_RETRY_COUNT = 1;
    private static final Integer UPDATED_RETRY_COUNT = 2;
    private static final Integer SMALLER_RETRY_COUNT = 1 - 1;

    private static final Integer DEFAULT_LATENCY_WARNING_MS = 1;
    private static final Integer UPDATED_LATENCY_WARNING_MS = 2;
    private static final Integer SMALLER_LATENCY_WARNING_MS = 1 - 1;

    private static final Integer DEFAULT_LATENCY_CRITICAL_MS = 1;
    private static final Integer UPDATED_LATENCY_CRITICAL_MS = 2;
    private static final Integer SMALLER_LATENCY_CRITICAL_MS = 1 - 1;

    private static final String DEFAULT_ADVANCED_CONFIG = "AAAAAAAAAA";
    private static final String UPDATED_ADVANCED_CONFIG = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/monitored-services";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/monitored-services/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MonitoredServiceRepository monitoredServiceRepository;

    @Autowired
    private MonitoredServiceMapper monitoredServiceMapper;

    @Autowired
    private MonitoredServiceSearchRepository monitoredServiceSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMonitoredServiceMockMvc;

    private MonitoredService monitoredService;

    private MonitoredService insertedMonitoredService;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MonitoredService createEntity() {
        return new MonitoredService()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .serviceType(DEFAULT_SERVICE_TYPE)
            .environment(DEFAULT_ENVIRONMENT)
            .monitoringEnabled(DEFAULT_MONITORING_ENABLED)
            .clusterMonitoringEnabled(DEFAULT_CLUSTER_MONITORING_ENABLED)
            .intervalSeconds(DEFAULT_INTERVAL_SECONDS)
            .timeoutMs(DEFAULT_TIMEOUT_MS)
            .retryCount(DEFAULT_RETRY_COUNT)
            .latencyWarningMs(DEFAULT_LATENCY_WARNING_MS)
            .latencyCriticalMs(DEFAULT_LATENCY_CRITICAL_MS)
            .advancedConfig(DEFAULT_ADVANCED_CONFIG)
            .isActive(DEFAULT_IS_ACTIVE)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MonitoredService createUpdatedEntity() {
        return new MonitoredService()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .serviceType(UPDATED_SERVICE_TYPE)
            .environment(UPDATED_ENVIRONMENT)
            .monitoringEnabled(UPDATED_MONITORING_ENABLED)
            .clusterMonitoringEnabled(UPDATED_CLUSTER_MONITORING_ENABLED)
            .intervalSeconds(UPDATED_INTERVAL_SECONDS)
            .timeoutMs(UPDATED_TIMEOUT_MS)
            .retryCount(UPDATED_RETRY_COUNT)
            .latencyWarningMs(UPDATED_LATENCY_WARNING_MS)
            .latencyCriticalMs(UPDATED_LATENCY_CRITICAL_MS)
            .advancedConfig(UPDATED_ADVANCED_CONFIG)
            .isActive(UPDATED_IS_ACTIVE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        monitoredService = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedMonitoredService != null) {
            monitoredServiceRepository.delete(insertedMonitoredService);
            monitoredServiceSearchRepository.delete(insertedMonitoredService);
            insertedMonitoredService = null;
        }
    }

    @Test
    @Transactional
    void createMonitoredService() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(monitoredServiceSearchRepository.findAll());
        // Create the MonitoredService
        MonitoredServiceDTO monitoredServiceDTO = monitoredServiceMapper.toDto(monitoredService);
        var returnedMonitoredServiceDTO = om.readValue(
            restMonitoredServiceMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(monitoredServiceDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MonitoredServiceDTO.class
        );

        // Validate the MonitoredService in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMonitoredService = monitoredServiceMapper.toEntity(returnedMonitoredServiceDTO);
        assertMonitoredServiceUpdatableFieldsEquals(returnedMonitoredService, getPersistedMonitoredService(returnedMonitoredService));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(monitoredServiceSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedMonitoredService = returnedMonitoredService;
    }

    @Test
    @Transactional
    void createMonitoredServiceWithExistingId() throws Exception {
        // Create the MonitoredService with an existing ID
        monitoredService.setId(1L);
        MonitoredServiceDTO monitoredServiceDTO = monitoredServiceMapper.toDto(monitoredService);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(monitoredServiceSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restMonitoredServiceMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(monitoredServiceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MonitoredService in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(monitoredServiceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(monitoredServiceSearchRepository.findAll());
        // set the field null
        monitoredService.setName(null);

        // Create the MonitoredService, which fails.
        MonitoredServiceDTO monitoredServiceDTO = monitoredServiceMapper.toDto(monitoredService);

        restMonitoredServiceMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(monitoredServiceDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(monitoredServiceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkServiceTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(monitoredServiceSearchRepository.findAll());
        // set the field null
        monitoredService.setServiceType(null);

        // Create the MonitoredService, which fails.
        MonitoredServiceDTO monitoredServiceDTO = monitoredServiceMapper.toDto(monitoredService);

        restMonitoredServiceMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(monitoredServiceDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(monitoredServiceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkEnvironmentIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(monitoredServiceSearchRepository.findAll());
        // set the field null
        monitoredService.setEnvironment(null);

        // Create the MonitoredService, which fails.
        MonitoredServiceDTO monitoredServiceDTO = monitoredServiceMapper.toDto(monitoredService);

        restMonitoredServiceMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(monitoredServiceDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(monitoredServiceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkIntervalSecondsIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(monitoredServiceSearchRepository.findAll());
        // set the field null
        monitoredService.setIntervalSeconds(null);

        // Create the MonitoredService, which fails.
        MonitoredServiceDTO monitoredServiceDTO = monitoredServiceMapper.toDto(monitoredService);

        restMonitoredServiceMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(monitoredServiceDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(monitoredServiceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTimeoutMsIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(monitoredServiceSearchRepository.findAll());
        // set the field null
        monitoredService.setTimeoutMs(null);

        // Create the MonitoredService, which fails.
        MonitoredServiceDTO monitoredServiceDTO = monitoredServiceMapper.toDto(monitoredService);

        restMonitoredServiceMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(monitoredServiceDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(monitoredServiceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkRetryCountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(monitoredServiceSearchRepository.findAll());
        // set the field null
        monitoredService.setRetryCount(null);

        // Create the MonitoredService, which fails.
        MonitoredServiceDTO monitoredServiceDTO = monitoredServiceMapper.toDto(monitoredService);

        restMonitoredServiceMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(monitoredServiceDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(monitoredServiceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllMonitoredServices() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList
        restMonitoredServiceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(monitoredService.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].serviceType").value(hasItem(DEFAULT_SERVICE_TYPE)))
            .andExpect(jsonPath("$.[*].environment").value(hasItem(DEFAULT_ENVIRONMENT)))
            .andExpect(jsonPath("$.[*].monitoringEnabled").value(hasItem(DEFAULT_MONITORING_ENABLED)))
            .andExpect(jsonPath("$.[*].clusterMonitoringEnabled").value(hasItem(DEFAULT_CLUSTER_MONITORING_ENABLED)))
            .andExpect(jsonPath("$.[*].intervalSeconds").value(hasItem(DEFAULT_INTERVAL_SECONDS)))
            .andExpect(jsonPath("$.[*].timeoutMs").value(hasItem(DEFAULT_TIMEOUT_MS)))
            .andExpect(jsonPath("$.[*].retryCount").value(hasItem(DEFAULT_RETRY_COUNT)))
            .andExpect(jsonPath("$.[*].latencyWarningMs").value(hasItem(DEFAULT_LATENCY_WARNING_MS)))
            .andExpect(jsonPath("$.[*].latencyCriticalMs").value(hasItem(DEFAULT_LATENCY_CRITICAL_MS)))
            .andExpect(jsonPath("$.[*].advancedConfig").value(hasItem(DEFAULT_ADVANCED_CONFIG)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getMonitoredService() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get the monitoredService
        restMonitoredServiceMockMvc
            .perform(get(ENTITY_API_URL_ID, monitoredService.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(monitoredService.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.serviceType").value(DEFAULT_SERVICE_TYPE))
            .andExpect(jsonPath("$.environment").value(DEFAULT_ENVIRONMENT))
            .andExpect(jsonPath("$.monitoringEnabled").value(DEFAULT_MONITORING_ENABLED))
            .andExpect(jsonPath("$.clusterMonitoringEnabled").value(DEFAULT_CLUSTER_MONITORING_ENABLED))
            .andExpect(jsonPath("$.intervalSeconds").value(DEFAULT_INTERVAL_SECONDS))
            .andExpect(jsonPath("$.timeoutMs").value(DEFAULT_TIMEOUT_MS))
            .andExpect(jsonPath("$.retryCount").value(DEFAULT_RETRY_COUNT))
            .andExpect(jsonPath("$.latencyWarningMs").value(DEFAULT_LATENCY_WARNING_MS))
            .andExpect(jsonPath("$.latencyCriticalMs").value(DEFAULT_LATENCY_CRITICAL_MS))
            .andExpect(jsonPath("$.advancedConfig").value(DEFAULT_ADVANCED_CONFIG))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getMonitoredServicesByIdFiltering() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        Long id = monitoredService.getId();

        defaultMonitoredServiceFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultMonitoredServiceFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultMonitoredServiceFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where name equals to
        defaultMonitoredServiceFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where name in
        defaultMonitoredServiceFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where name is not null
        defaultMonitoredServiceFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where name contains
        defaultMonitoredServiceFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where name does not contain
        defaultMonitoredServiceFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where description equals to
        defaultMonitoredServiceFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where description in
        defaultMonitoredServiceFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where description is not null
        defaultMonitoredServiceFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where description contains
        defaultMonitoredServiceFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where description does not contain
        defaultMonitoredServiceFiltering(
            "description.doesNotContain=" + UPDATED_DESCRIPTION,
            "description.doesNotContain=" + DEFAULT_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByServiceTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where serviceType equals to
        defaultMonitoredServiceFiltering("serviceType.equals=" + DEFAULT_SERVICE_TYPE, "serviceType.equals=" + UPDATED_SERVICE_TYPE);
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByServiceTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where serviceType in
        defaultMonitoredServiceFiltering(
            "serviceType.in=" + DEFAULT_SERVICE_TYPE + "," + UPDATED_SERVICE_TYPE,
            "serviceType.in=" + UPDATED_SERVICE_TYPE
        );
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByServiceTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where serviceType is not null
        defaultMonitoredServiceFiltering("serviceType.specified=true", "serviceType.specified=false");
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByServiceTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where serviceType contains
        defaultMonitoredServiceFiltering("serviceType.contains=" + DEFAULT_SERVICE_TYPE, "serviceType.contains=" + UPDATED_SERVICE_TYPE);
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByServiceTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where serviceType does not contain
        defaultMonitoredServiceFiltering(
            "serviceType.doesNotContain=" + UPDATED_SERVICE_TYPE,
            "serviceType.doesNotContain=" + DEFAULT_SERVICE_TYPE
        );
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByEnvironmentIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where environment equals to
        defaultMonitoredServiceFiltering("environment.equals=" + DEFAULT_ENVIRONMENT, "environment.equals=" + UPDATED_ENVIRONMENT);
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByEnvironmentIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where environment in
        defaultMonitoredServiceFiltering(
            "environment.in=" + DEFAULT_ENVIRONMENT + "," + UPDATED_ENVIRONMENT,
            "environment.in=" + UPDATED_ENVIRONMENT
        );
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByEnvironmentIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where environment is not null
        defaultMonitoredServiceFiltering("environment.specified=true", "environment.specified=false");
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByEnvironmentContainsSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where environment contains
        defaultMonitoredServiceFiltering("environment.contains=" + DEFAULT_ENVIRONMENT, "environment.contains=" + UPDATED_ENVIRONMENT);
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByEnvironmentNotContainsSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where environment does not contain
        defaultMonitoredServiceFiltering(
            "environment.doesNotContain=" + UPDATED_ENVIRONMENT,
            "environment.doesNotContain=" + DEFAULT_ENVIRONMENT
        );
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByMonitoringEnabledIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where monitoringEnabled equals to
        defaultMonitoredServiceFiltering(
            "monitoringEnabled.equals=" + DEFAULT_MONITORING_ENABLED,
            "monitoringEnabled.equals=" + UPDATED_MONITORING_ENABLED
        );
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByMonitoringEnabledIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where monitoringEnabled in
        defaultMonitoredServiceFiltering(
            "monitoringEnabled.in=" + DEFAULT_MONITORING_ENABLED + "," + UPDATED_MONITORING_ENABLED,
            "monitoringEnabled.in=" + UPDATED_MONITORING_ENABLED
        );
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByMonitoringEnabledIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where monitoringEnabled is not null
        defaultMonitoredServiceFiltering("monitoringEnabled.specified=true", "monitoringEnabled.specified=false");
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByClusterMonitoringEnabledIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where clusterMonitoringEnabled equals to
        defaultMonitoredServiceFiltering(
            "clusterMonitoringEnabled.equals=" + DEFAULT_CLUSTER_MONITORING_ENABLED,
            "clusterMonitoringEnabled.equals=" + UPDATED_CLUSTER_MONITORING_ENABLED
        );
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByClusterMonitoringEnabledIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where clusterMonitoringEnabled in
        defaultMonitoredServiceFiltering(
            "clusterMonitoringEnabled.in=" + DEFAULT_CLUSTER_MONITORING_ENABLED + "," + UPDATED_CLUSTER_MONITORING_ENABLED,
            "clusterMonitoringEnabled.in=" + UPDATED_CLUSTER_MONITORING_ENABLED
        );
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByClusterMonitoringEnabledIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where clusterMonitoringEnabled is not null
        defaultMonitoredServiceFiltering("clusterMonitoringEnabled.specified=true", "clusterMonitoringEnabled.specified=false");
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByIntervalSecondsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where intervalSeconds equals to
        defaultMonitoredServiceFiltering(
            "intervalSeconds.equals=" + DEFAULT_INTERVAL_SECONDS,
            "intervalSeconds.equals=" + UPDATED_INTERVAL_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByIntervalSecondsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where intervalSeconds in
        defaultMonitoredServiceFiltering(
            "intervalSeconds.in=" + DEFAULT_INTERVAL_SECONDS + "," + UPDATED_INTERVAL_SECONDS,
            "intervalSeconds.in=" + UPDATED_INTERVAL_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByIntervalSecondsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where intervalSeconds is not null
        defaultMonitoredServiceFiltering("intervalSeconds.specified=true", "intervalSeconds.specified=false");
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByIntervalSecondsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where intervalSeconds is greater than or equal to
        defaultMonitoredServiceFiltering(
            "intervalSeconds.greaterThanOrEqual=" + DEFAULT_INTERVAL_SECONDS,
            "intervalSeconds.greaterThanOrEqual=" + UPDATED_INTERVAL_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByIntervalSecondsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where intervalSeconds is less than or equal to
        defaultMonitoredServiceFiltering(
            "intervalSeconds.lessThanOrEqual=" + DEFAULT_INTERVAL_SECONDS,
            "intervalSeconds.lessThanOrEqual=" + SMALLER_INTERVAL_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByIntervalSecondsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where intervalSeconds is less than
        defaultMonitoredServiceFiltering(
            "intervalSeconds.lessThan=" + UPDATED_INTERVAL_SECONDS,
            "intervalSeconds.lessThan=" + DEFAULT_INTERVAL_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByIntervalSecondsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where intervalSeconds is greater than
        defaultMonitoredServiceFiltering(
            "intervalSeconds.greaterThan=" + SMALLER_INTERVAL_SECONDS,
            "intervalSeconds.greaterThan=" + DEFAULT_INTERVAL_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByTimeoutMsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where timeoutMs equals to
        defaultMonitoredServiceFiltering("timeoutMs.equals=" + DEFAULT_TIMEOUT_MS, "timeoutMs.equals=" + UPDATED_TIMEOUT_MS);
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByTimeoutMsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where timeoutMs in
        defaultMonitoredServiceFiltering(
            "timeoutMs.in=" + DEFAULT_TIMEOUT_MS + "," + UPDATED_TIMEOUT_MS,
            "timeoutMs.in=" + UPDATED_TIMEOUT_MS
        );
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByTimeoutMsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where timeoutMs is not null
        defaultMonitoredServiceFiltering("timeoutMs.specified=true", "timeoutMs.specified=false");
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByTimeoutMsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where timeoutMs is greater than or equal to
        defaultMonitoredServiceFiltering(
            "timeoutMs.greaterThanOrEqual=" + DEFAULT_TIMEOUT_MS,
            "timeoutMs.greaterThanOrEqual=" + UPDATED_TIMEOUT_MS
        );
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByTimeoutMsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where timeoutMs is less than or equal to
        defaultMonitoredServiceFiltering(
            "timeoutMs.lessThanOrEqual=" + DEFAULT_TIMEOUT_MS,
            "timeoutMs.lessThanOrEqual=" + SMALLER_TIMEOUT_MS
        );
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByTimeoutMsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where timeoutMs is less than
        defaultMonitoredServiceFiltering("timeoutMs.lessThan=" + UPDATED_TIMEOUT_MS, "timeoutMs.lessThan=" + DEFAULT_TIMEOUT_MS);
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByTimeoutMsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where timeoutMs is greater than
        defaultMonitoredServiceFiltering("timeoutMs.greaterThan=" + SMALLER_TIMEOUT_MS, "timeoutMs.greaterThan=" + DEFAULT_TIMEOUT_MS);
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByRetryCountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where retryCount equals to
        defaultMonitoredServiceFiltering("retryCount.equals=" + DEFAULT_RETRY_COUNT, "retryCount.equals=" + UPDATED_RETRY_COUNT);
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByRetryCountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where retryCount in
        defaultMonitoredServiceFiltering(
            "retryCount.in=" + DEFAULT_RETRY_COUNT + "," + UPDATED_RETRY_COUNT,
            "retryCount.in=" + UPDATED_RETRY_COUNT
        );
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByRetryCountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where retryCount is not null
        defaultMonitoredServiceFiltering("retryCount.specified=true", "retryCount.specified=false");
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByRetryCountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where retryCount is greater than or equal to
        defaultMonitoredServiceFiltering(
            "retryCount.greaterThanOrEqual=" + DEFAULT_RETRY_COUNT,
            "retryCount.greaterThanOrEqual=" + UPDATED_RETRY_COUNT
        );
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByRetryCountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where retryCount is less than or equal to
        defaultMonitoredServiceFiltering(
            "retryCount.lessThanOrEqual=" + DEFAULT_RETRY_COUNT,
            "retryCount.lessThanOrEqual=" + SMALLER_RETRY_COUNT
        );
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByRetryCountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where retryCount is less than
        defaultMonitoredServiceFiltering("retryCount.lessThan=" + UPDATED_RETRY_COUNT, "retryCount.lessThan=" + DEFAULT_RETRY_COUNT);
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByRetryCountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where retryCount is greater than
        defaultMonitoredServiceFiltering("retryCount.greaterThan=" + SMALLER_RETRY_COUNT, "retryCount.greaterThan=" + DEFAULT_RETRY_COUNT);
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByLatencyWarningMsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where latencyWarningMs equals to
        defaultMonitoredServiceFiltering(
            "latencyWarningMs.equals=" + DEFAULT_LATENCY_WARNING_MS,
            "latencyWarningMs.equals=" + UPDATED_LATENCY_WARNING_MS
        );
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByLatencyWarningMsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where latencyWarningMs in
        defaultMonitoredServiceFiltering(
            "latencyWarningMs.in=" + DEFAULT_LATENCY_WARNING_MS + "," + UPDATED_LATENCY_WARNING_MS,
            "latencyWarningMs.in=" + UPDATED_LATENCY_WARNING_MS
        );
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByLatencyWarningMsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where latencyWarningMs is not null
        defaultMonitoredServiceFiltering("latencyWarningMs.specified=true", "latencyWarningMs.specified=false");
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByLatencyWarningMsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where latencyWarningMs is greater than or equal to
        defaultMonitoredServiceFiltering(
            "latencyWarningMs.greaterThanOrEqual=" + DEFAULT_LATENCY_WARNING_MS,
            "latencyWarningMs.greaterThanOrEqual=" + UPDATED_LATENCY_WARNING_MS
        );
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByLatencyWarningMsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where latencyWarningMs is less than or equal to
        defaultMonitoredServiceFiltering(
            "latencyWarningMs.lessThanOrEqual=" + DEFAULT_LATENCY_WARNING_MS,
            "latencyWarningMs.lessThanOrEqual=" + SMALLER_LATENCY_WARNING_MS
        );
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByLatencyWarningMsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where latencyWarningMs is less than
        defaultMonitoredServiceFiltering(
            "latencyWarningMs.lessThan=" + UPDATED_LATENCY_WARNING_MS,
            "latencyWarningMs.lessThan=" + DEFAULT_LATENCY_WARNING_MS
        );
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByLatencyWarningMsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where latencyWarningMs is greater than
        defaultMonitoredServiceFiltering(
            "latencyWarningMs.greaterThan=" + SMALLER_LATENCY_WARNING_MS,
            "latencyWarningMs.greaterThan=" + DEFAULT_LATENCY_WARNING_MS
        );
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByLatencyCriticalMsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where latencyCriticalMs equals to
        defaultMonitoredServiceFiltering(
            "latencyCriticalMs.equals=" + DEFAULT_LATENCY_CRITICAL_MS,
            "latencyCriticalMs.equals=" + UPDATED_LATENCY_CRITICAL_MS
        );
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByLatencyCriticalMsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where latencyCriticalMs in
        defaultMonitoredServiceFiltering(
            "latencyCriticalMs.in=" + DEFAULT_LATENCY_CRITICAL_MS + "," + UPDATED_LATENCY_CRITICAL_MS,
            "latencyCriticalMs.in=" + UPDATED_LATENCY_CRITICAL_MS
        );
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByLatencyCriticalMsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where latencyCriticalMs is not null
        defaultMonitoredServiceFiltering("latencyCriticalMs.specified=true", "latencyCriticalMs.specified=false");
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByLatencyCriticalMsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where latencyCriticalMs is greater than or equal to
        defaultMonitoredServiceFiltering(
            "latencyCriticalMs.greaterThanOrEqual=" + DEFAULT_LATENCY_CRITICAL_MS,
            "latencyCriticalMs.greaterThanOrEqual=" + UPDATED_LATENCY_CRITICAL_MS
        );
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByLatencyCriticalMsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where latencyCriticalMs is less than or equal to
        defaultMonitoredServiceFiltering(
            "latencyCriticalMs.lessThanOrEqual=" + DEFAULT_LATENCY_CRITICAL_MS,
            "latencyCriticalMs.lessThanOrEqual=" + SMALLER_LATENCY_CRITICAL_MS
        );
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByLatencyCriticalMsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where latencyCriticalMs is less than
        defaultMonitoredServiceFiltering(
            "latencyCriticalMs.lessThan=" + UPDATED_LATENCY_CRITICAL_MS,
            "latencyCriticalMs.lessThan=" + DEFAULT_LATENCY_CRITICAL_MS
        );
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByLatencyCriticalMsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where latencyCriticalMs is greater than
        defaultMonitoredServiceFiltering(
            "latencyCriticalMs.greaterThan=" + SMALLER_LATENCY_CRITICAL_MS,
            "latencyCriticalMs.greaterThan=" + DEFAULT_LATENCY_CRITICAL_MS
        );
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByIsActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where isActive equals to
        defaultMonitoredServiceFiltering("isActive.equals=" + DEFAULT_IS_ACTIVE, "isActive.equals=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByIsActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where isActive in
        defaultMonitoredServiceFiltering("isActive.in=" + DEFAULT_IS_ACTIVE + "," + UPDATED_IS_ACTIVE, "isActive.in=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByIsActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where isActive is not null
        defaultMonitoredServiceFiltering("isActive.specified=true", "isActive.specified=false");
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where createdAt equals to
        defaultMonitoredServiceFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where createdAt in
        defaultMonitoredServiceFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where createdAt is not null
        defaultMonitoredServiceFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where updatedAt equals to
        defaultMonitoredServiceFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where updatedAt in
        defaultMonitoredServiceFiltering(
            "updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT,
            "updatedAt.in=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        // Get all the monitoredServiceList where updatedAt is not null
        defaultMonitoredServiceFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllMonitoredServicesByDatacenterIsEqualToSomething() throws Exception {
        Datacenter datacenter;
        if (TestUtil.findAll(em, Datacenter.class).isEmpty()) {
            monitoredServiceRepository.saveAndFlush(monitoredService);
            datacenter = DatacenterResourceIT.createEntity();
        } else {
            datacenter = TestUtil.findAll(em, Datacenter.class).get(0);
        }
        em.persist(datacenter);
        em.flush();
        monitoredService.setDatacenter(datacenter);
        monitoredServiceRepository.saveAndFlush(monitoredService);
        Long datacenterId = datacenter.getId();
        // Get all the monitoredServiceList where datacenter equals to datacenterId
        defaultMonitoredServiceShouldBeFound("datacenterId.equals=" + datacenterId);

        // Get all the monitoredServiceList where datacenter equals to (datacenterId + 1)
        defaultMonitoredServiceShouldNotBeFound("datacenterId.equals=" + (datacenterId + 1));
    }

    private void defaultMonitoredServiceFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultMonitoredServiceShouldBeFound(shouldBeFound);
        defaultMonitoredServiceShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMonitoredServiceShouldBeFound(String filter) throws Exception {
        restMonitoredServiceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(monitoredService.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].serviceType").value(hasItem(DEFAULT_SERVICE_TYPE)))
            .andExpect(jsonPath("$.[*].environment").value(hasItem(DEFAULT_ENVIRONMENT)))
            .andExpect(jsonPath("$.[*].monitoringEnabled").value(hasItem(DEFAULT_MONITORING_ENABLED)))
            .andExpect(jsonPath("$.[*].clusterMonitoringEnabled").value(hasItem(DEFAULT_CLUSTER_MONITORING_ENABLED)))
            .andExpect(jsonPath("$.[*].intervalSeconds").value(hasItem(DEFAULT_INTERVAL_SECONDS)))
            .andExpect(jsonPath("$.[*].timeoutMs").value(hasItem(DEFAULT_TIMEOUT_MS)))
            .andExpect(jsonPath("$.[*].retryCount").value(hasItem(DEFAULT_RETRY_COUNT)))
            .andExpect(jsonPath("$.[*].latencyWarningMs").value(hasItem(DEFAULT_LATENCY_WARNING_MS)))
            .andExpect(jsonPath("$.[*].latencyCriticalMs").value(hasItem(DEFAULT_LATENCY_CRITICAL_MS)))
            .andExpect(jsonPath("$.[*].advancedConfig").value(hasItem(DEFAULT_ADVANCED_CONFIG)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restMonitoredServiceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultMonitoredServiceShouldNotBeFound(String filter) throws Exception {
        restMonitoredServiceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restMonitoredServiceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingMonitoredService() throws Exception {
        // Get the monitoredService
        restMonitoredServiceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMonitoredService() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        monitoredServiceSearchRepository.save(monitoredService);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(monitoredServiceSearchRepository.findAll());

        // Update the monitoredService
        MonitoredService updatedMonitoredService = monitoredServiceRepository.findById(monitoredService.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMonitoredService are not directly saved in db
        em.detach(updatedMonitoredService);
        updatedMonitoredService
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .serviceType(UPDATED_SERVICE_TYPE)
            .environment(UPDATED_ENVIRONMENT)
            .monitoringEnabled(UPDATED_MONITORING_ENABLED)
            .clusterMonitoringEnabled(UPDATED_CLUSTER_MONITORING_ENABLED)
            .intervalSeconds(UPDATED_INTERVAL_SECONDS)
            .timeoutMs(UPDATED_TIMEOUT_MS)
            .retryCount(UPDATED_RETRY_COUNT)
            .latencyWarningMs(UPDATED_LATENCY_WARNING_MS)
            .latencyCriticalMs(UPDATED_LATENCY_CRITICAL_MS)
            .advancedConfig(UPDATED_ADVANCED_CONFIG)
            .isActive(UPDATED_IS_ACTIVE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        MonitoredServiceDTO monitoredServiceDTO = monitoredServiceMapper.toDto(updatedMonitoredService);

        restMonitoredServiceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, monitoredServiceDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(monitoredServiceDTO))
            )
            .andExpect(status().isOk());

        // Validate the MonitoredService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMonitoredServiceToMatchAllProperties(updatedMonitoredService);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(monitoredServiceSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<MonitoredService> monitoredServiceSearchList = Streamable.of(monitoredServiceSearchRepository.findAll()).toList();
                MonitoredService testMonitoredServiceSearch = monitoredServiceSearchList.get(searchDatabaseSizeAfter - 1);

                assertMonitoredServiceAllPropertiesEquals(testMonitoredServiceSearch, updatedMonitoredService);
            });
    }

    @Test
    @Transactional
    void putNonExistingMonitoredService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(monitoredServiceSearchRepository.findAll());
        monitoredService.setId(longCount.incrementAndGet());

        // Create the MonitoredService
        MonitoredServiceDTO monitoredServiceDTO = monitoredServiceMapper.toDto(monitoredService);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMonitoredServiceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, monitoredServiceDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(monitoredServiceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MonitoredService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(monitoredServiceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchMonitoredService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(monitoredServiceSearchRepository.findAll());
        monitoredService.setId(longCount.incrementAndGet());

        // Create the MonitoredService
        MonitoredServiceDTO monitoredServiceDTO = monitoredServiceMapper.toDto(monitoredService);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMonitoredServiceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(monitoredServiceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MonitoredService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(monitoredServiceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMonitoredService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(monitoredServiceSearchRepository.findAll());
        monitoredService.setId(longCount.incrementAndGet());

        // Create the MonitoredService
        MonitoredServiceDTO monitoredServiceDTO = monitoredServiceMapper.toDto(monitoredService);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMonitoredServiceMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(monitoredServiceDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the MonitoredService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(monitoredServiceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateMonitoredServiceWithPatch() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the monitoredService using partial update
        MonitoredService partialUpdatedMonitoredService = new MonitoredService();
        partialUpdatedMonitoredService.setId(monitoredService.getId());

        partialUpdatedMonitoredService
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .environment(UPDATED_ENVIRONMENT)
            .monitoringEnabled(UPDATED_MONITORING_ENABLED)
            .clusterMonitoringEnabled(UPDATED_CLUSTER_MONITORING_ENABLED)
            .intervalSeconds(UPDATED_INTERVAL_SECONDS)
            .timeoutMs(UPDATED_TIMEOUT_MS)
            .latencyWarningMs(UPDATED_LATENCY_WARNING_MS)
            .advancedConfig(UPDATED_ADVANCED_CONFIG);

        restMonitoredServiceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMonitoredService.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMonitoredService))
            )
            .andExpect(status().isOk());

        // Validate the MonitoredService in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMonitoredServiceUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMonitoredService, monitoredService),
            getPersistedMonitoredService(monitoredService)
        );
    }

    @Test
    @Transactional
    void fullUpdateMonitoredServiceWithPatch() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the monitoredService using partial update
        MonitoredService partialUpdatedMonitoredService = new MonitoredService();
        partialUpdatedMonitoredService.setId(monitoredService.getId());

        partialUpdatedMonitoredService
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .serviceType(UPDATED_SERVICE_TYPE)
            .environment(UPDATED_ENVIRONMENT)
            .monitoringEnabled(UPDATED_MONITORING_ENABLED)
            .clusterMonitoringEnabled(UPDATED_CLUSTER_MONITORING_ENABLED)
            .intervalSeconds(UPDATED_INTERVAL_SECONDS)
            .timeoutMs(UPDATED_TIMEOUT_MS)
            .retryCount(UPDATED_RETRY_COUNT)
            .latencyWarningMs(UPDATED_LATENCY_WARNING_MS)
            .latencyCriticalMs(UPDATED_LATENCY_CRITICAL_MS)
            .advancedConfig(UPDATED_ADVANCED_CONFIG)
            .isActive(UPDATED_IS_ACTIVE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restMonitoredServiceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMonitoredService.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMonitoredService))
            )
            .andExpect(status().isOk());

        // Validate the MonitoredService in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMonitoredServiceUpdatableFieldsEquals(
            partialUpdatedMonitoredService,
            getPersistedMonitoredService(partialUpdatedMonitoredService)
        );
    }

    @Test
    @Transactional
    void patchNonExistingMonitoredService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(monitoredServiceSearchRepository.findAll());
        monitoredService.setId(longCount.incrementAndGet());

        // Create the MonitoredService
        MonitoredServiceDTO monitoredServiceDTO = monitoredServiceMapper.toDto(monitoredService);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMonitoredServiceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, monitoredServiceDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(monitoredServiceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MonitoredService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(monitoredServiceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMonitoredService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(monitoredServiceSearchRepository.findAll());
        monitoredService.setId(longCount.incrementAndGet());

        // Create the MonitoredService
        MonitoredServiceDTO monitoredServiceDTO = monitoredServiceMapper.toDto(monitoredService);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMonitoredServiceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(monitoredServiceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MonitoredService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(monitoredServiceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMonitoredService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(monitoredServiceSearchRepository.findAll());
        monitoredService.setId(longCount.incrementAndGet());

        // Create the MonitoredService
        MonitoredServiceDTO monitoredServiceDTO = monitoredServiceMapper.toDto(monitoredService);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMonitoredServiceMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(monitoredServiceDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the MonitoredService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(monitoredServiceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteMonitoredService() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);
        monitoredServiceRepository.save(monitoredService);
        monitoredServiceSearchRepository.save(monitoredService);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(monitoredServiceSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the monitoredService
        restMonitoredServiceMockMvc
            .perform(delete(ENTITY_API_URL_ID, monitoredService.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(monitoredServiceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchMonitoredService() throws Exception {
        // Initialize the database
        insertedMonitoredService = monitoredServiceRepository.saveAndFlush(monitoredService);
        monitoredServiceSearchRepository.save(monitoredService);

        // Search the monitoredService
        restMonitoredServiceMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + monitoredService.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(monitoredService.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].serviceType").value(hasItem(DEFAULT_SERVICE_TYPE)))
            .andExpect(jsonPath("$.[*].environment").value(hasItem(DEFAULT_ENVIRONMENT)))
            .andExpect(jsonPath("$.[*].monitoringEnabled").value(hasItem(DEFAULT_MONITORING_ENABLED)))
            .andExpect(jsonPath("$.[*].clusterMonitoringEnabled").value(hasItem(DEFAULT_CLUSTER_MONITORING_ENABLED)))
            .andExpect(jsonPath("$.[*].intervalSeconds").value(hasItem(DEFAULT_INTERVAL_SECONDS)))
            .andExpect(jsonPath("$.[*].timeoutMs").value(hasItem(DEFAULT_TIMEOUT_MS)))
            .andExpect(jsonPath("$.[*].retryCount").value(hasItem(DEFAULT_RETRY_COUNT)))
            .andExpect(jsonPath("$.[*].latencyWarningMs").value(hasItem(DEFAULT_LATENCY_WARNING_MS)))
            .andExpect(jsonPath("$.[*].latencyCriticalMs").value(hasItem(DEFAULT_LATENCY_CRITICAL_MS)))
            .andExpect(jsonPath("$.[*].advancedConfig").value(hasItem(DEFAULT_ADVANCED_CONFIG.toString())))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return monitoredServiceRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected MonitoredService getPersistedMonitoredService(MonitoredService monitoredService) {
        return monitoredServiceRepository.findById(monitoredService.getId()).orElseThrow();
    }

    protected void assertPersistedMonitoredServiceToMatchAllProperties(MonitoredService expectedMonitoredService) {
        assertMonitoredServiceAllPropertiesEquals(expectedMonitoredService, getPersistedMonitoredService(expectedMonitoredService));
    }

    protected void assertPersistedMonitoredServiceToMatchUpdatableProperties(MonitoredService expectedMonitoredService) {
        assertMonitoredServiceAllUpdatablePropertiesEquals(
            expectedMonitoredService,
            getPersistedMonitoredService(expectedMonitoredService)
        );
    }
}
