package vibhuvi.oio.inframirror.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static vibhuvi.oio.inframirror.domain.ServiceAsserts.*;
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
import vibhuvi.oio.inframirror.domain.Service;
import vibhuvi.oio.inframirror.repository.ServiceRepository;
import vibhuvi.oio.inframirror.repository.search.ServiceSearchRepository;
import vibhuvi.oio.inframirror.service.dto.ServiceDTO;
import vibhuvi.oio.inframirror.service.mapper.ServiceMapper;

/**
 * Integration tests for the {@link ServiceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ServiceResourceIT {

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

    private static final String ENTITY_API_URL = "/api/services";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/services/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ServiceMapper serviceMapper;

    @Autowired
    private ServiceSearchRepository serviceSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restServiceMockMvc;

    private Service service;

    private Service insertedService;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Service createEntity() {
        return new Service()
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
    public static Service createUpdatedEntity() {
        return new Service()
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
        service = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedService != null) {
            serviceRepository.delete(insertedService);
            serviceSearchRepository.delete(insertedService);
            insertedService = null;
        }
    }

    @Test
    @Transactional
    void createService() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(serviceSearchRepository.findAll());
        // Create the Service
        ServiceDTO serviceDTO = serviceMapper.toDto(service);
        var returnedServiceDTO = om.readValue(
            restServiceMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(serviceDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ServiceDTO.class
        );

        // Validate the Service in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedService = serviceMapper.toEntity(returnedServiceDTO);
        assertServiceUpdatableFieldsEquals(returnedService, getPersistedService(returnedService));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(serviceSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedService = returnedService;
    }

    @Test
    @Transactional
    void createServiceWithExistingId() throws Exception {
        // Create the Service with an existing ID
        service.setId(1L);
        ServiceDTO serviceDTO = serviceMapper.toDto(service);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(serviceSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restServiceMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(serviceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Service in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(serviceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(serviceSearchRepository.findAll());
        // set the field null
        service.setName(null);

        // Create the Service, which fails.
        ServiceDTO serviceDTO = serviceMapper.toDto(service);

        restServiceMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(serviceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(serviceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkServiceTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(serviceSearchRepository.findAll());
        // set the field null
        service.setServiceType(null);

        // Create the Service, which fails.
        ServiceDTO serviceDTO = serviceMapper.toDto(service);

        restServiceMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(serviceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(serviceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkEnvironmentIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(serviceSearchRepository.findAll());
        // set the field null
        service.setEnvironment(null);

        // Create the Service, which fails.
        ServiceDTO serviceDTO = serviceMapper.toDto(service);

        restServiceMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(serviceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(serviceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkIntervalSecondsIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(serviceSearchRepository.findAll());
        // set the field null
        service.setIntervalSeconds(null);

        // Create the Service, which fails.
        ServiceDTO serviceDTO = serviceMapper.toDto(service);

        restServiceMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(serviceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(serviceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTimeoutMsIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(serviceSearchRepository.findAll());
        // set the field null
        service.setTimeoutMs(null);

        // Create the Service, which fails.
        ServiceDTO serviceDTO = serviceMapper.toDto(service);

        restServiceMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(serviceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(serviceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkRetryCountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(serviceSearchRepository.findAll());
        // set the field null
        service.setRetryCount(null);

        // Create the Service, which fails.
        ServiceDTO serviceDTO = serviceMapper.toDto(service);

        restServiceMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(serviceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(serviceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllServices() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList
        restServiceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(service.getId().intValue())))
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
    void getService() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get the service
        restServiceMockMvc
            .perform(get(ENTITY_API_URL_ID, service.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(service.getId().intValue()))
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
    void getServicesByIdFiltering() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        Long id = service.getId();

        defaultServiceFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultServiceFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultServiceFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllServicesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where name equals to
        defaultServiceFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllServicesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where name in
        defaultServiceFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllServicesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where name is not null
        defaultServiceFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllServicesByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where name contains
        defaultServiceFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllServicesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where name does not contain
        defaultServiceFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllServicesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where description equals to
        defaultServiceFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllServicesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where description in
        defaultServiceFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllServicesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where description is not null
        defaultServiceFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    @Transactional
    void getAllServicesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where description contains
        defaultServiceFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllServicesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where description does not contain
        defaultServiceFiltering("description.doesNotContain=" + UPDATED_DESCRIPTION, "description.doesNotContain=" + DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllServicesByServiceTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where serviceType equals to
        defaultServiceFiltering("serviceType.equals=" + DEFAULT_SERVICE_TYPE, "serviceType.equals=" + UPDATED_SERVICE_TYPE);
    }

    @Test
    @Transactional
    void getAllServicesByServiceTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where serviceType in
        defaultServiceFiltering(
            "serviceType.in=" + DEFAULT_SERVICE_TYPE + "," + UPDATED_SERVICE_TYPE,
            "serviceType.in=" + UPDATED_SERVICE_TYPE
        );
    }

    @Test
    @Transactional
    void getAllServicesByServiceTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where serviceType is not null
        defaultServiceFiltering("serviceType.specified=true", "serviceType.specified=false");
    }

    @Test
    @Transactional
    void getAllServicesByServiceTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where serviceType contains
        defaultServiceFiltering("serviceType.contains=" + DEFAULT_SERVICE_TYPE, "serviceType.contains=" + UPDATED_SERVICE_TYPE);
    }

    @Test
    @Transactional
    void getAllServicesByServiceTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where serviceType does not contain
        defaultServiceFiltering("serviceType.doesNotContain=" + UPDATED_SERVICE_TYPE, "serviceType.doesNotContain=" + DEFAULT_SERVICE_TYPE);
    }

    @Test
    @Transactional
    void getAllServicesByEnvironmentIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where environment equals to
        defaultServiceFiltering("environment.equals=" + DEFAULT_ENVIRONMENT, "environment.equals=" + UPDATED_ENVIRONMENT);
    }

    @Test
    @Transactional
    void getAllServicesByEnvironmentIsInShouldWork() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where environment in
        defaultServiceFiltering(
            "environment.in=" + DEFAULT_ENVIRONMENT + "," + UPDATED_ENVIRONMENT,
            "environment.in=" + UPDATED_ENVIRONMENT
        );
    }

    @Test
    @Transactional
    void getAllServicesByEnvironmentIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where environment is not null
        defaultServiceFiltering("environment.specified=true", "environment.specified=false");
    }

    @Test
    @Transactional
    void getAllServicesByEnvironmentContainsSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where environment contains
        defaultServiceFiltering("environment.contains=" + DEFAULT_ENVIRONMENT, "environment.contains=" + UPDATED_ENVIRONMENT);
    }

    @Test
    @Transactional
    void getAllServicesByEnvironmentNotContainsSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where environment does not contain
        defaultServiceFiltering("environment.doesNotContain=" + UPDATED_ENVIRONMENT, "environment.doesNotContain=" + DEFAULT_ENVIRONMENT);
    }

    @Test
    @Transactional
    void getAllServicesByMonitoringEnabledIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where monitoringEnabled equals to
        defaultServiceFiltering(
            "monitoringEnabled.equals=" + DEFAULT_MONITORING_ENABLED,
            "monitoringEnabled.equals=" + UPDATED_MONITORING_ENABLED
        );
    }

    @Test
    @Transactional
    void getAllServicesByMonitoringEnabledIsInShouldWork() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where monitoringEnabled in
        defaultServiceFiltering(
            "monitoringEnabled.in=" + DEFAULT_MONITORING_ENABLED + "," + UPDATED_MONITORING_ENABLED,
            "monitoringEnabled.in=" + UPDATED_MONITORING_ENABLED
        );
    }

    @Test
    @Transactional
    void getAllServicesByMonitoringEnabledIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where monitoringEnabled is not null
        defaultServiceFiltering("monitoringEnabled.specified=true", "monitoringEnabled.specified=false");
    }

    @Test
    @Transactional
    void getAllServicesByClusterMonitoringEnabledIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where clusterMonitoringEnabled equals to
        defaultServiceFiltering(
            "clusterMonitoringEnabled.equals=" + DEFAULT_CLUSTER_MONITORING_ENABLED,
            "clusterMonitoringEnabled.equals=" + UPDATED_CLUSTER_MONITORING_ENABLED
        );
    }

    @Test
    @Transactional
    void getAllServicesByClusterMonitoringEnabledIsInShouldWork() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where clusterMonitoringEnabled in
        defaultServiceFiltering(
            "clusterMonitoringEnabled.in=" + DEFAULT_CLUSTER_MONITORING_ENABLED + "," + UPDATED_CLUSTER_MONITORING_ENABLED,
            "clusterMonitoringEnabled.in=" + UPDATED_CLUSTER_MONITORING_ENABLED
        );
    }

    @Test
    @Transactional
    void getAllServicesByClusterMonitoringEnabledIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where clusterMonitoringEnabled is not null
        defaultServiceFiltering("clusterMonitoringEnabled.specified=true", "clusterMonitoringEnabled.specified=false");
    }

    @Test
    @Transactional
    void getAllServicesByIntervalSecondsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where intervalSeconds equals to
        defaultServiceFiltering("intervalSeconds.equals=" + DEFAULT_INTERVAL_SECONDS, "intervalSeconds.equals=" + UPDATED_INTERVAL_SECONDS);
    }

    @Test
    @Transactional
    void getAllServicesByIntervalSecondsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where intervalSeconds in
        defaultServiceFiltering(
            "intervalSeconds.in=" + DEFAULT_INTERVAL_SECONDS + "," + UPDATED_INTERVAL_SECONDS,
            "intervalSeconds.in=" + UPDATED_INTERVAL_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllServicesByIntervalSecondsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where intervalSeconds is not null
        defaultServiceFiltering("intervalSeconds.specified=true", "intervalSeconds.specified=false");
    }

    @Test
    @Transactional
    void getAllServicesByIntervalSecondsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where intervalSeconds is greater than or equal to
        defaultServiceFiltering(
            "intervalSeconds.greaterThanOrEqual=" + DEFAULT_INTERVAL_SECONDS,
            "intervalSeconds.greaterThanOrEqual=" + UPDATED_INTERVAL_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllServicesByIntervalSecondsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where intervalSeconds is less than or equal to
        defaultServiceFiltering(
            "intervalSeconds.lessThanOrEqual=" + DEFAULT_INTERVAL_SECONDS,
            "intervalSeconds.lessThanOrEqual=" + SMALLER_INTERVAL_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllServicesByIntervalSecondsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where intervalSeconds is less than
        defaultServiceFiltering(
            "intervalSeconds.lessThan=" + UPDATED_INTERVAL_SECONDS,
            "intervalSeconds.lessThan=" + DEFAULT_INTERVAL_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllServicesByIntervalSecondsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where intervalSeconds is greater than
        defaultServiceFiltering(
            "intervalSeconds.greaterThan=" + SMALLER_INTERVAL_SECONDS,
            "intervalSeconds.greaterThan=" + DEFAULT_INTERVAL_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllServicesByTimeoutMsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where timeoutMs equals to
        defaultServiceFiltering("timeoutMs.equals=" + DEFAULT_TIMEOUT_MS, "timeoutMs.equals=" + UPDATED_TIMEOUT_MS);
    }

    @Test
    @Transactional
    void getAllServicesByTimeoutMsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where timeoutMs in
        defaultServiceFiltering("timeoutMs.in=" + DEFAULT_TIMEOUT_MS + "," + UPDATED_TIMEOUT_MS, "timeoutMs.in=" + UPDATED_TIMEOUT_MS);
    }

    @Test
    @Transactional
    void getAllServicesByTimeoutMsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where timeoutMs is not null
        defaultServiceFiltering("timeoutMs.specified=true", "timeoutMs.specified=false");
    }

    @Test
    @Transactional
    void getAllServicesByTimeoutMsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where timeoutMs is greater than or equal to
        defaultServiceFiltering("timeoutMs.greaterThanOrEqual=" + DEFAULT_TIMEOUT_MS, "timeoutMs.greaterThanOrEqual=" + UPDATED_TIMEOUT_MS);
    }

    @Test
    @Transactional
    void getAllServicesByTimeoutMsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where timeoutMs is less than or equal to
        defaultServiceFiltering("timeoutMs.lessThanOrEqual=" + DEFAULT_TIMEOUT_MS, "timeoutMs.lessThanOrEqual=" + SMALLER_TIMEOUT_MS);
    }

    @Test
    @Transactional
    void getAllServicesByTimeoutMsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where timeoutMs is less than
        defaultServiceFiltering("timeoutMs.lessThan=" + UPDATED_TIMEOUT_MS, "timeoutMs.lessThan=" + DEFAULT_TIMEOUT_MS);
    }

    @Test
    @Transactional
    void getAllServicesByTimeoutMsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where timeoutMs is greater than
        defaultServiceFiltering("timeoutMs.greaterThan=" + SMALLER_TIMEOUT_MS, "timeoutMs.greaterThan=" + DEFAULT_TIMEOUT_MS);
    }

    @Test
    @Transactional
    void getAllServicesByRetryCountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where retryCount equals to
        defaultServiceFiltering("retryCount.equals=" + DEFAULT_RETRY_COUNT, "retryCount.equals=" + UPDATED_RETRY_COUNT);
    }

    @Test
    @Transactional
    void getAllServicesByRetryCountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where retryCount in
        defaultServiceFiltering("retryCount.in=" + DEFAULT_RETRY_COUNT + "," + UPDATED_RETRY_COUNT, "retryCount.in=" + UPDATED_RETRY_COUNT);
    }

    @Test
    @Transactional
    void getAllServicesByRetryCountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where retryCount is not null
        defaultServiceFiltering("retryCount.specified=true", "retryCount.specified=false");
    }

    @Test
    @Transactional
    void getAllServicesByRetryCountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where retryCount is greater than or equal to
        defaultServiceFiltering(
            "retryCount.greaterThanOrEqual=" + DEFAULT_RETRY_COUNT,
            "retryCount.greaterThanOrEqual=" + UPDATED_RETRY_COUNT
        );
    }

    @Test
    @Transactional
    void getAllServicesByRetryCountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where retryCount is less than or equal to
        defaultServiceFiltering("retryCount.lessThanOrEqual=" + DEFAULT_RETRY_COUNT, "retryCount.lessThanOrEqual=" + SMALLER_RETRY_COUNT);
    }

    @Test
    @Transactional
    void getAllServicesByRetryCountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where retryCount is less than
        defaultServiceFiltering("retryCount.lessThan=" + UPDATED_RETRY_COUNT, "retryCount.lessThan=" + DEFAULT_RETRY_COUNT);
    }

    @Test
    @Transactional
    void getAllServicesByRetryCountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where retryCount is greater than
        defaultServiceFiltering("retryCount.greaterThan=" + SMALLER_RETRY_COUNT, "retryCount.greaterThan=" + DEFAULT_RETRY_COUNT);
    }

    @Test
    @Transactional
    void getAllServicesByLatencyWarningMsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where latencyWarningMs equals to
        defaultServiceFiltering(
            "latencyWarningMs.equals=" + DEFAULT_LATENCY_WARNING_MS,
            "latencyWarningMs.equals=" + UPDATED_LATENCY_WARNING_MS
        );
    }

    @Test
    @Transactional
    void getAllServicesByLatencyWarningMsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where latencyWarningMs in
        defaultServiceFiltering(
            "latencyWarningMs.in=" + DEFAULT_LATENCY_WARNING_MS + "," + UPDATED_LATENCY_WARNING_MS,
            "latencyWarningMs.in=" + UPDATED_LATENCY_WARNING_MS
        );
    }

    @Test
    @Transactional
    void getAllServicesByLatencyWarningMsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where latencyWarningMs is not null
        defaultServiceFiltering("latencyWarningMs.specified=true", "latencyWarningMs.specified=false");
    }

    @Test
    @Transactional
    void getAllServicesByLatencyWarningMsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where latencyWarningMs is greater than or equal to
        defaultServiceFiltering(
            "latencyWarningMs.greaterThanOrEqual=" + DEFAULT_LATENCY_WARNING_MS,
            "latencyWarningMs.greaterThanOrEqual=" + UPDATED_LATENCY_WARNING_MS
        );
    }

    @Test
    @Transactional
    void getAllServicesByLatencyWarningMsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where latencyWarningMs is less than or equal to
        defaultServiceFiltering(
            "latencyWarningMs.lessThanOrEqual=" + DEFAULT_LATENCY_WARNING_MS,
            "latencyWarningMs.lessThanOrEqual=" + SMALLER_LATENCY_WARNING_MS
        );
    }

    @Test
    @Transactional
    void getAllServicesByLatencyWarningMsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where latencyWarningMs is less than
        defaultServiceFiltering(
            "latencyWarningMs.lessThan=" + UPDATED_LATENCY_WARNING_MS,
            "latencyWarningMs.lessThan=" + DEFAULT_LATENCY_WARNING_MS
        );
    }

    @Test
    @Transactional
    void getAllServicesByLatencyWarningMsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where latencyWarningMs is greater than
        defaultServiceFiltering(
            "latencyWarningMs.greaterThan=" + SMALLER_LATENCY_WARNING_MS,
            "latencyWarningMs.greaterThan=" + DEFAULT_LATENCY_WARNING_MS
        );
    }

    @Test
    @Transactional
    void getAllServicesByLatencyCriticalMsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where latencyCriticalMs equals to
        defaultServiceFiltering(
            "latencyCriticalMs.equals=" + DEFAULT_LATENCY_CRITICAL_MS,
            "latencyCriticalMs.equals=" + UPDATED_LATENCY_CRITICAL_MS
        );
    }

    @Test
    @Transactional
    void getAllServicesByLatencyCriticalMsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where latencyCriticalMs in
        defaultServiceFiltering(
            "latencyCriticalMs.in=" + DEFAULT_LATENCY_CRITICAL_MS + "," + UPDATED_LATENCY_CRITICAL_MS,
            "latencyCriticalMs.in=" + UPDATED_LATENCY_CRITICAL_MS
        );
    }

    @Test
    @Transactional
    void getAllServicesByLatencyCriticalMsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where latencyCriticalMs is not null
        defaultServiceFiltering("latencyCriticalMs.specified=true", "latencyCriticalMs.specified=false");
    }

    @Test
    @Transactional
    void getAllServicesByLatencyCriticalMsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where latencyCriticalMs is greater than or equal to
        defaultServiceFiltering(
            "latencyCriticalMs.greaterThanOrEqual=" + DEFAULT_LATENCY_CRITICAL_MS,
            "latencyCriticalMs.greaterThanOrEqual=" + UPDATED_LATENCY_CRITICAL_MS
        );
    }

    @Test
    @Transactional
    void getAllServicesByLatencyCriticalMsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where latencyCriticalMs is less than or equal to
        defaultServiceFiltering(
            "latencyCriticalMs.lessThanOrEqual=" + DEFAULT_LATENCY_CRITICAL_MS,
            "latencyCriticalMs.lessThanOrEqual=" + SMALLER_LATENCY_CRITICAL_MS
        );
    }

    @Test
    @Transactional
    void getAllServicesByLatencyCriticalMsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where latencyCriticalMs is less than
        defaultServiceFiltering(
            "latencyCriticalMs.lessThan=" + UPDATED_LATENCY_CRITICAL_MS,
            "latencyCriticalMs.lessThan=" + DEFAULT_LATENCY_CRITICAL_MS
        );
    }

    @Test
    @Transactional
    void getAllServicesByLatencyCriticalMsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where latencyCriticalMs is greater than
        defaultServiceFiltering(
            "latencyCriticalMs.greaterThan=" + SMALLER_LATENCY_CRITICAL_MS,
            "latencyCriticalMs.greaterThan=" + DEFAULT_LATENCY_CRITICAL_MS
        );
    }

    @Test
    @Transactional
    void getAllServicesByIsActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where isActive equals to
        defaultServiceFiltering("isActive.equals=" + DEFAULT_IS_ACTIVE, "isActive.equals=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllServicesByIsActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where isActive in
        defaultServiceFiltering("isActive.in=" + DEFAULT_IS_ACTIVE + "," + UPDATED_IS_ACTIVE, "isActive.in=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllServicesByIsActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where isActive is not null
        defaultServiceFiltering("isActive.specified=true", "isActive.specified=false");
    }

    @Test
    @Transactional
    void getAllServicesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where createdAt equals to
        defaultServiceFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllServicesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where createdAt in
        defaultServiceFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllServicesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where createdAt is not null
        defaultServiceFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllServicesByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where updatedAt equals to
        defaultServiceFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllServicesByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where updatedAt in
        defaultServiceFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllServicesByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList where updatedAt is not null
        defaultServiceFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllServicesByDatacenterIsEqualToSomething() throws Exception {
        Datacenter datacenter;
        if (TestUtil.findAll(em, Datacenter.class).isEmpty()) {
            serviceRepository.saveAndFlush(service);
            datacenter = DatacenterResourceIT.createEntity();
        } else {
            datacenter = TestUtil.findAll(em, Datacenter.class).get(0);
        }
        em.persist(datacenter);
        em.flush();
        service.setDatacenter(datacenter);
        serviceRepository.saveAndFlush(service);
        Long datacenterId = datacenter.getId();
        // Get all the serviceList where datacenter equals to datacenterId
        defaultServiceShouldBeFound("datacenterId.equals=" + datacenterId);

        // Get all the serviceList where datacenter equals to (datacenterId + 1)
        defaultServiceShouldNotBeFound("datacenterId.equals=" + (datacenterId + 1));
    }

    private void defaultServiceFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultServiceShouldBeFound(shouldBeFound);
        defaultServiceShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultServiceShouldBeFound(String filter) throws Exception {
        restServiceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(service.getId().intValue())))
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
        restServiceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultServiceShouldNotBeFound(String filter) throws Exception {
        restServiceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restServiceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingService() throws Exception {
        // Get the service
        restServiceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingService() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        serviceSearchRepository.save(service);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(serviceSearchRepository.findAll());

        // Update the service
        Service updatedService = serviceRepository.findById(service.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedService are not directly saved in db
        em.detach(updatedService);
        updatedService
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
        ServiceDTO serviceDTO = serviceMapper.toDto(updatedService);

        restServiceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, serviceDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(serviceDTO))
            )
            .andExpect(status().isOk());

        // Validate the Service in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedServiceToMatchAllProperties(updatedService);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(serviceSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Service> serviceSearchList = Streamable.of(serviceSearchRepository.findAll()).toList();
                Service testServiceSearch = serviceSearchList.get(searchDatabaseSizeAfter - 1);

                assertServiceAllPropertiesEquals(testServiceSearch, updatedService);
            });
    }

    @Test
    @Transactional
    void putNonExistingService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(serviceSearchRepository.findAll());
        service.setId(longCount.incrementAndGet());

        // Create the Service
        ServiceDTO serviceDTO = serviceMapper.toDto(service);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restServiceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, serviceDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(serviceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Service in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(serviceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(serviceSearchRepository.findAll());
        service.setId(longCount.incrementAndGet());

        // Create the Service
        ServiceDTO serviceDTO = serviceMapper.toDto(service);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServiceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(serviceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Service in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(serviceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(serviceSearchRepository.findAll());
        service.setId(longCount.incrementAndGet());

        // Create the Service
        ServiceDTO serviceDTO = serviceMapper.toDto(service);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServiceMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(serviceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Service in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(serviceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateServiceWithPatch() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the service using partial update
        Service partialUpdatedService = new Service();
        partialUpdatedService.setId(service.getId());

        partialUpdatedService
            .description(UPDATED_DESCRIPTION)
            .environment(UPDATED_ENVIRONMENT)
            .intervalSeconds(UPDATED_INTERVAL_SECONDS)
            .isActive(UPDATED_IS_ACTIVE)
            .updatedAt(UPDATED_UPDATED_AT);

        restServiceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedService.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedService))
            )
            .andExpect(status().isOk());

        // Validate the Service in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertServiceUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedService, service), getPersistedService(service));
    }

    @Test
    @Transactional
    void fullUpdateServiceWithPatch() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the service using partial update
        Service partialUpdatedService = new Service();
        partialUpdatedService.setId(service.getId());

        partialUpdatedService
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

        restServiceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedService.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedService))
            )
            .andExpect(status().isOk());

        // Validate the Service in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertServiceUpdatableFieldsEquals(partialUpdatedService, getPersistedService(partialUpdatedService));
    }

    @Test
    @Transactional
    void patchNonExistingService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(serviceSearchRepository.findAll());
        service.setId(longCount.incrementAndGet());

        // Create the Service
        ServiceDTO serviceDTO = serviceMapper.toDto(service);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restServiceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, serviceDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(serviceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Service in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(serviceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(serviceSearchRepository.findAll());
        service.setId(longCount.incrementAndGet());

        // Create the Service
        ServiceDTO serviceDTO = serviceMapper.toDto(service);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServiceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(serviceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Service in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(serviceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(serviceSearchRepository.findAll());
        service.setId(longCount.incrementAndGet());

        // Create the Service
        ServiceDTO serviceDTO = serviceMapper.toDto(service);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServiceMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(serviceDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Service in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(serviceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteService() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);
        serviceRepository.save(service);
        serviceSearchRepository.save(service);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(serviceSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the service
        restServiceMockMvc
            .perform(delete(ENTITY_API_URL_ID, service.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(serviceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchService() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);
        serviceSearchRepository.save(service);

        // Search the service
        restServiceMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + service.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(service.getId().intValue())))
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
        return serviceRepository.count();
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

    protected Service getPersistedService(Service service) {
        return serviceRepository.findById(service.getId()).orElseThrow();
    }

    protected void assertPersistedServiceToMatchAllProperties(Service expectedService) {
        assertServiceAllPropertiesEquals(expectedService, getPersistedService(expectedService));
    }

    protected void assertPersistedServiceToMatchUpdatableProperties(Service expectedService) {
        assertServiceAllUpdatablePropertiesEquals(expectedService, getPersistedService(expectedService));
    }
}
