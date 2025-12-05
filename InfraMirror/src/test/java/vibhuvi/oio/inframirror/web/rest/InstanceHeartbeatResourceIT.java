package vibhuvi.oio.inframirror.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static vibhuvi.oio.inframirror.domain.InstanceHeartbeatAsserts.*;
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
import vibhuvi.oio.inframirror.domain.Instance;
import vibhuvi.oio.inframirror.domain.InstanceHeartbeat;
import vibhuvi.oio.inframirror.repository.InstanceHeartbeatRepository;
import vibhuvi.oio.inframirror.repository.search.InstanceHeartbeatSearchRepository;
import vibhuvi.oio.inframirror.service.dto.InstanceHeartbeatDTO;
import vibhuvi.oio.inframirror.service.mapper.InstanceHeartbeatMapper;

/**
 * Integration tests for the {@link InstanceHeartbeatResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class InstanceHeartbeatResourceIT {

    private static final Instant DEFAULT_EXECUTED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EXECUTED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_HEARTBEAT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_HEARTBEAT_TYPE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_SUCCESS = false;
    private static final Boolean UPDATED_SUCCESS = true;

    private static final Integer DEFAULT_RESPONSE_TIME_MS = 1;
    private static final Integer UPDATED_RESPONSE_TIME_MS = 2;

    private static final Float DEFAULT_PACKET_LOSS = 1F;
    private static final Float UPDATED_PACKET_LOSS = 2F;

    private static final Integer DEFAULT_JITTER_MS = 1;
    private static final Integer UPDATED_JITTER_MS = 2;

    private static final Float DEFAULT_CPU_USAGE = 1F;
    private static final Float UPDATED_CPU_USAGE = 2F;

    private static final Float DEFAULT_MEMORY_USAGE = 1F;
    private static final Float UPDATED_MEMORY_USAGE = 2F;

    private static final Float DEFAULT_DISK_USAGE = 1F;
    private static final Float UPDATED_DISK_USAGE = 2F;

    private static final Float DEFAULT_LOAD_AVERAGE = 1F;
    private static final Float UPDATED_LOAD_AVERAGE = 2F;

    private static final Integer DEFAULT_PROCESS_COUNT = 1;
    private static final Integer UPDATED_PROCESS_COUNT = 2;

    private static final Long DEFAULT_NETWORK_RX_BYTES = 1L;
    private static final Long UPDATED_NETWORK_RX_BYTES = 2L;

    private static final Long DEFAULT_NETWORK_TX_BYTES = 1L;
    private static final Long UPDATED_NETWORK_TX_BYTES = 2L;

    private static final Long DEFAULT_UPTIME_SECONDS = 1L;
    private static final Long UPDATED_UPTIME_SECONDS = 2L;

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_ERROR_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_ERROR_MESSAGE = "BBBBBBBBBB";

    private static final String DEFAULT_ERROR_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_ERROR_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_METADATA = "AAAAAAAAAA";
    private static final String UPDATED_METADATA = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/instance-heartbeats";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/instance-heartbeats/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InstanceHeartbeatRepository instanceHeartbeatRepository;

    @Autowired
    private InstanceHeartbeatMapper instanceHeartbeatMapper;

    @Autowired
    private InstanceHeartbeatSearchRepository instanceHeartbeatSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInstanceHeartbeatMockMvc;

    private InstanceHeartbeat instanceHeartbeat;

    private InstanceHeartbeat insertedInstanceHeartbeat;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InstanceHeartbeat createEntity(EntityManager em) {
        InstanceHeartbeat instanceHeartbeat = new InstanceHeartbeat()
            .executedAt(DEFAULT_EXECUTED_AT)
            .heartbeatType(DEFAULT_HEARTBEAT_TYPE)
            .success(DEFAULT_SUCCESS)
            .responseTimeMs(DEFAULT_RESPONSE_TIME_MS)
            .packetLoss(DEFAULT_PACKET_LOSS)
            .jitterMs(DEFAULT_JITTER_MS)
            .cpuUsage(DEFAULT_CPU_USAGE)
            .memoryUsage(DEFAULT_MEMORY_USAGE)
            .diskUsage(DEFAULT_DISK_USAGE)
            .loadAverage(DEFAULT_LOAD_AVERAGE)
            .processCount(DEFAULT_PROCESS_COUNT)
            .networkRxBytes(DEFAULT_NETWORK_RX_BYTES)
            .networkTxBytes(DEFAULT_NETWORK_TX_BYTES)
            .uptimeSeconds(DEFAULT_UPTIME_SECONDS)
            .status(DEFAULT_STATUS)
            .errorMessage(DEFAULT_ERROR_MESSAGE)
            .errorType(DEFAULT_ERROR_TYPE)
            .metadata(DEFAULT_METADATA);
        // Add required entity
        Instance instance;
        if (TestUtil.findAll(em, Instance.class).isEmpty()) {
            instance = InstanceResourceIT.createEntity(em);
            em.persist(instance);
            em.flush();
        } else {
            instance = TestUtil.findAll(em, Instance.class).get(0);
        }
        instanceHeartbeat.setInstance(instance);
        return instanceHeartbeat;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InstanceHeartbeat createUpdatedEntity(EntityManager em) {
        InstanceHeartbeat updatedInstanceHeartbeat = new InstanceHeartbeat()
            .executedAt(UPDATED_EXECUTED_AT)
            .heartbeatType(UPDATED_HEARTBEAT_TYPE)
            .success(UPDATED_SUCCESS)
            .responseTimeMs(UPDATED_RESPONSE_TIME_MS)
            .packetLoss(UPDATED_PACKET_LOSS)
            .jitterMs(UPDATED_JITTER_MS)
            .cpuUsage(UPDATED_CPU_USAGE)
            .memoryUsage(UPDATED_MEMORY_USAGE)
            .diskUsage(UPDATED_DISK_USAGE)
            .loadAverage(UPDATED_LOAD_AVERAGE)
            .processCount(UPDATED_PROCESS_COUNT)
            .networkRxBytes(UPDATED_NETWORK_RX_BYTES)
            .networkTxBytes(UPDATED_NETWORK_TX_BYTES)
            .uptimeSeconds(UPDATED_UPTIME_SECONDS)
            .status(UPDATED_STATUS)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .errorType(UPDATED_ERROR_TYPE)
            .metadata(UPDATED_METADATA);
        // Add required entity
        Instance instance;
        if (TestUtil.findAll(em, Instance.class).isEmpty()) {
            instance = InstanceResourceIT.createUpdatedEntity(em);
            em.persist(instance);
            em.flush();
        } else {
            instance = TestUtil.findAll(em, Instance.class).get(0);
        }
        updatedInstanceHeartbeat.setInstance(instance);
        return updatedInstanceHeartbeat;
    }

    @BeforeEach
    void initTest() {
        instanceHeartbeat = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedInstanceHeartbeat != null) {
            instanceHeartbeatRepository.delete(insertedInstanceHeartbeat);
            instanceHeartbeatSearchRepository.delete(insertedInstanceHeartbeat);
            insertedInstanceHeartbeat = null;
        }
    }

    @Test
    @Transactional
    void createInstanceHeartbeat() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(instanceHeartbeatSearchRepository.findAll());
        // Create the InstanceHeartbeat
        InstanceHeartbeatDTO instanceHeartbeatDTO = instanceHeartbeatMapper.toDto(instanceHeartbeat);
        var returnedInstanceHeartbeatDTO = om.readValue(
            restInstanceHeartbeatMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(instanceHeartbeatDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            InstanceHeartbeatDTO.class
        );

        // Validate the InstanceHeartbeat in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedInstanceHeartbeat = instanceHeartbeatMapper.toEntity(returnedInstanceHeartbeatDTO);
        assertInstanceHeartbeatUpdatableFieldsEquals(returnedInstanceHeartbeat, getPersistedInstanceHeartbeat(returnedInstanceHeartbeat));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(instanceHeartbeatSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedInstanceHeartbeat = returnedInstanceHeartbeat;
    }

    @Test
    @Transactional
    void createInstanceHeartbeatWithExistingId() throws Exception {
        // Create the InstanceHeartbeat with an existing ID
        instanceHeartbeat.setId(1L);
        InstanceHeartbeatDTO instanceHeartbeatDTO = instanceHeartbeatMapper.toDto(instanceHeartbeat);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(instanceHeartbeatSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restInstanceHeartbeatMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(instanceHeartbeatDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InstanceHeartbeat in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(instanceHeartbeatSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkExecutedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(instanceHeartbeatSearchRepository.findAll());
        // set the field null
        instanceHeartbeat.setExecutedAt(null);

        // Create the InstanceHeartbeat, which fails.
        InstanceHeartbeatDTO instanceHeartbeatDTO = instanceHeartbeatMapper.toDto(instanceHeartbeat);

        restInstanceHeartbeatMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(instanceHeartbeatDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(instanceHeartbeatSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkHeartbeatTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(instanceHeartbeatSearchRepository.findAll());
        // set the field null
        instanceHeartbeat.setHeartbeatType(null);

        // Create the InstanceHeartbeat, which fails.
        InstanceHeartbeatDTO instanceHeartbeatDTO = instanceHeartbeatMapper.toDto(instanceHeartbeat);

        restInstanceHeartbeatMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(instanceHeartbeatDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(instanceHeartbeatSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkSuccessIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(instanceHeartbeatSearchRepository.findAll());
        // set the field null
        instanceHeartbeat.setSuccess(null);

        // Create the InstanceHeartbeat, which fails.
        InstanceHeartbeatDTO instanceHeartbeatDTO = instanceHeartbeatMapper.toDto(instanceHeartbeat);

        restInstanceHeartbeatMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(instanceHeartbeatDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(instanceHeartbeatSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(instanceHeartbeatSearchRepository.findAll());
        // set the field null
        instanceHeartbeat.setStatus(null);

        // Create the InstanceHeartbeat, which fails.
        InstanceHeartbeatDTO instanceHeartbeatDTO = instanceHeartbeatMapper.toDto(instanceHeartbeat);

        restInstanceHeartbeatMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(instanceHeartbeatDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(instanceHeartbeatSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllInstanceHeartbeats() throws Exception {
        // Initialize the database
        insertedInstanceHeartbeat = instanceHeartbeatRepository.saveAndFlush(instanceHeartbeat);

        // Get all the instanceHeartbeatList
        restInstanceHeartbeatMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(instanceHeartbeat.getId().intValue())))
            .andExpect(jsonPath("$.[*].executedAt").value(hasItem(DEFAULT_EXECUTED_AT.toString())))
            .andExpect(jsonPath("$.[*].heartbeatType").value(hasItem(DEFAULT_HEARTBEAT_TYPE)))
            .andExpect(jsonPath("$.[*].success").value(hasItem(DEFAULT_SUCCESS)))
            .andExpect(jsonPath("$.[*].responseTimeMs").value(hasItem(DEFAULT_RESPONSE_TIME_MS)))
            .andExpect(jsonPath("$.[*].packetLoss").value(hasItem(DEFAULT_PACKET_LOSS.doubleValue())))
            .andExpect(jsonPath("$.[*].jitterMs").value(hasItem(DEFAULT_JITTER_MS)))
            .andExpect(jsonPath("$.[*].cpuUsage").value(hasItem(DEFAULT_CPU_USAGE.doubleValue())))
            .andExpect(jsonPath("$.[*].memoryUsage").value(hasItem(DEFAULT_MEMORY_USAGE.doubleValue())))
            .andExpect(jsonPath("$.[*].diskUsage").value(hasItem(DEFAULT_DISK_USAGE.doubleValue())))
            .andExpect(jsonPath("$.[*].loadAverage").value(hasItem(DEFAULT_LOAD_AVERAGE.doubleValue())))
            .andExpect(jsonPath("$.[*].processCount").value(hasItem(DEFAULT_PROCESS_COUNT)))
            .andExpect(jsonPath("$.[*].networkRxBytes").value(hasItem(DEFAULT_NETWORK_RX_BYTES.intValue())))
            .andExpect(jsonPath("$.[*].networkTxBytes").value(hasItem(DEFAULT_NETWORK_TX_BYTES.intValue())))
            .andExpect(jsonPath("$.[*].uptimeSeconds").value(hasItem(DEFAULT_UPTIME_SECONDS.intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].errorMessage").value(hasItem(DEFAULT_ERROR_MESSAGE)))
            .andExpect(jsonPath("$.[*].errorType").value(hasItem(DEFAULT_ERROR_TYPE)))
            .andExpect(jsonPath("$.[*].metadata").value(hasItem(DEFAULT_METADATA)));
    }

    @Test
    @Transactional
    void getInstanceHeartbeat() throws Exception {
        // Initialize the database
        insertedInstanceHeartbeat = instanceHeartbeatRepository.saveAndFlush(instanceHeartbeat);

        // Get the instanceHeartbeat
        restInstanceHeartbeatMockMvc
            .perform(get(ENTITY_API_URL_ID, instanceHeartbeat.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(instanceHeartbeat.getId().intValue()))
            .andExpect(jsonPath("$.executedAt").value(DEFAULT_EXECUTED_AT.toString()))
            .andExpect(jsonPath("$.heartbeatType").value(DEFAULT_HEARTBEAT_TYPE))
            .andExpect(jsonPath("$.success").value(DEFAULT_SUCCESS))
            .andExpect(jsonPath("$.responseTimeMs").value(DEFAULT_RESPONSE_TIME_MS))
            .andExpect(jsonPath("$.packetLoss").value(DEFAULT_PACKET_LOSS.doubleValue()))
            .andExpect(jsonPath("$.jitterMs").value(DEFAULT_JITTER_MS))
            .andExpect(jsonPath("$.cpuUsage").value(DEFAULT_CPU_USAGE.doubleValue()))
            .andExpect(jsonPath("$.memoryUsage").value(DEFAULT_MEMORY_USAGE.doubleValue()))
            .andExpect(jsonPath("$.diskUsage").value(DEFAULT_DISK_USAGE.doubleValue()))
            .andExpect(jsonPath("$.loadAverage").value(DEFAULT_LOAD_AVERAGE.doubleValue()))
            .andExpect(jsonPath("$.processCount").value(DEFAULT_PROCESS_COUNT))
            .andExpect(jsonPath("$.networkRxBytes").value(DEFAULT_NETWORK_RX_BYTES.intValue()))
            .andExpect(jsonPath("$.networkTxBytes").value(DEFAULT_NETWORK_TX_BYTES.intValue()))
            .andExpect(jsonPath("$.uptimeSeconds").value(DEFAULT_UPTIME_SECONDS.intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.errorMessage").value(DEFAULT_ERROR_MESSAGE))
            .andExpect(jsonPath("$.errorType").value(DEFAULT_ERROR_TYPE))
            .andExpect(jsonPath("$.metadata").value(DEFAULT_METADATA));
    }

    @Test
    @Transactional
    void getNonExistingInstanceHeartbeat() throws Exception {
        // Get the instanceHeartbeat
        restInstanceHeartbeatMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInstanceHeartbeat() throws Exception {
        // Initialize the database
        insertedInstanceHeartbeat = instanceHeartbeatRepository.saveAndFlush(instanceHeartbeat);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        instanceHeartbeatSearchRepository.save(instanceHeartbeat);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(instanceHeartbeatSearchRepository.findAll());

        // Update the instanceHeartbeat
        InstanceHeartbeat updatedInstanceHeartbeat = instanceHeartbeatRepository.findById(instanceHeartbeat.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedInstanceHeartbeat are not directly saved in db
        em.detach(updatedInstanceHeartbeat);
        updatedInstanceHeartbeat
            .executedAt(UPDATED_EXECUTED_AT)
            .heartbeatType(UPDATED_HEARTBEAT_TYPE)
            .success(UPDATED_SUCCESS)
            .responseTimeMs(UPDATED_RESPONSE_TIME_MS)
            .packetLoss(UPDATED_PACKET_LOSS)
            .jitterMs(UPDATED_JITTER_MS)
            .cpuUsage(UPDATED_CPU_USAGE)
            .memoryUsage(UPDATED_MEMORY_USAGE)
            .diskUsage(UPDATED_DISK_USAGE)
            .loadAverage(UPDATED_LOAD_AVERAGE)
            .processCount(UPDATED_PROCESS_COUNT)
            .networkRxBytes(UPDATED_NETWORK_RX_BYTES)
            .networkTxBytes(UPDATED_NETWORK_TX_BYTES)
            .uptimeSeconds(UPDATED_UPTIME_SECONDS)
            .status(UPDATED_STATUS)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .errorType(UPDATED_ERROR_TYPE)
            .metadata(UPDATED_METADATA);
        InstanceHeartbeatDTO instanceHeartbeatDTO = instanceHeartbeatMapper.toDto(updatedInstanceHeartbeat);

        restInstanceHeartbeatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, instanceHeartbeatDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(instanceHeartbeatDTO))
            )
            .andExpect(status().isOk());

        // Validate the InstanceHeartbeat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInstanceHeartbeatToMatchAllProperties(updatedInstanceHeartbeat);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(instanceHeartbeatSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<InstanceHeartbeat> instanceHeartbeatSearchList = Streamable.of(instanceHeartbeatSearchRepository.findAll()).toList();
                InstanceHeartbeat testInstanceHeartbeatSearch = instanceHeartbeatSearchList.get(searchDatabaseSizeAfter - 1);

                assertInstanceHeartbeatAllPropertiesEquals(testInstanceHeartbeatSearch, updatedInstanceHeartbeat);
            });
    }

    @Test
    @Transactional
    void putNonExistingInstanceHeartbeat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(instanceHeartbeatSearchRepository.findAll());
        instanceHeartbeat.setId(longCount.incrementAndGet());

        // Create the InstanceHeartbeat
        InstanceHeartbeatDTO instanceHeartbeatDTO = instanceHeartbeatMapper.toDto(instanceHeartbeat);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInstanceHeartbeatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, instanceHeartbeatDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(instanceHeartbeatDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InstanceHeartbeat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(instanceHeartbeatSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchInstanceHeartbeat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(instanceHeartbeatSearchRepository.findAll());
        instanceHeartbeat.setId(longCount.incrementAndGet());

        // Create the InstanceHeartbeat
        InstanceHeartbeatDTO instanceHeartbeatDTO = instanceHeartbeatMapper.toDto(instanceHeartbeat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInstanceHeartbeatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(instanceHeartbeatDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InstanceHeartbeat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(instanceHeartbeatSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInstanceHeartbeat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(instanceHeartbeatSearchRepository.findAll());
        instanceHeartbeat.setId(longCount.incrementAndGet());

        // Create the InstanceHeartbeat
        InstanceHeartbeatDTO instanceHeartbeatDTO = instanceHeartbeatMapper.toDto(instanceHeartbeat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInstanceHeartbeatMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(instanceHeartbeatDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the InstanceHeartbeat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(instanceHeartbeatSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateInstanceHeartbeatWithPatch() throws Exception {
        // Initialize the database
        insertedInstanceHeartbeat = instanceHeartbeatRepository.saveAndFlush(instanceHeartbeat);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the instanceHeartbeat using partial update
        InstanceHeartbeat partialUpdatedInstanceHeartbeat = new InstanceHeartbeat();
        partialUpdatedInstanceHeartbeat.setId(instanceHeartbeat.getId());

        partialUpdatedInstanceHeartbeat
            .executedAt(UPDATED_EXECUTED_AT)
            .heartbeatType(UPDATED_HEARTBEAT_TYPE)
            .success(UPDATED_SUCCESS)
            .responseTimeMs(UPDATED_RESPONSE_TIME_MS)
            .jitterMs(UPDATED_JITTER_MS)
            .cpuUsage(UPDATED_CPU_USAGE)
            .diskUsage(UPDATED_DISK_USAGE)
            .loadAverage(UPDATED_LOAD_AVERAGE)
            .uptimeSeconds(UPDATED_UPTIME_SECONDS)
            .status(UPDATED_STATUS)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .metadata(UPDATED_METADATA);

        restInstanceHeartbeatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInstanceHeartbeat.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInstanceHeartbeat))
            )
            .andExpect(status().isOk());

        // Validate the InstanceHeartbeat in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInstanceHeartbeatUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedInstanceHeartbeat, instanceHeartbeat),
            getPersistedInstanceHeartbeat(instanceHeartbeat)
        );
    }

    @Test
    @Transactional
    void fullUpdateInstanceHeartbeatWithPatch() throws Exception {
        // Initialize the database
        insertedInstanceHeartbeat = instanceHeartbeatRepository.saveAndFlush(instanceHeartbeat);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the instanceHeartbeat using partial update
        InstanceHeartbeat partialUpdatedInstanceHeartbeat = new InstanceHeartbeat();
        partialUpdatedInstanceHeartbeat.setId(instanceHeartbeat.getId());

        partialUpdatedInstanceHeartbeat
            .executedAt(UPDATED_EXECUTED_AT)
            .heartbeatType(UPDATED_HEARTBEAT_TYPE)
            .success(UPDATED_SUCCESS)
            .responseTimeMs(UPDATED_RESPONSE_TIME_MS)
            .packetLoss(UPDATED_PACKET_LOSS)
            .jitterMs(UPDATED_JITTER_MS)
            .cpuUsage(UPDATED_CPU_USAGE)
            .memoryUsage(UPDATED_MEMORY_USAGE)
            .diskUsage(UPDATED_DISK_USAGE)
            .loadAverage(UPDATED_LOAD_AVERAGE)
            .processCount(UPDATED_PROCESS_COUNT)
            .networkRxBytes(UPDATED_NETWORK_RX_BYTES)
            .networkTxBytes(UPDATED_NETWORK_TX_BYTES)
            .uptimeSeconds(UPDATED_UPTIME_SECONDS)
            .status(UPDATED_STATUS)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .errorType(UPDATED_ERROR_TYPE)
            .metadata(UPDATED_METADATA);

        restInstanceHeartbeatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInstanceHeartbeat.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInstanceHeartbeat))
            )
            .andExpect(status().isOk());

        // Validate the InstanceHeartbeat in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInstanceHeartbeatUpdatableFieldsEquals(
            partialUpdatedInstanceHeartbeat,
            getPersistedInstanceHeartbeat(partialUpdatedInstanceHeartbeat)
        );
    }

    @Test
    @Transactional
    void patchNonExistingInstanceHeartbeat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(instanceHeartbeatSearchRepository.findAll());
        instanceHeartbeat.setId(longCount.incrementAndGet());

        // Create the InstanceHeartbeat
        InstanceHeartbeatDTO instanceHeartbeatDTO = instanceHeartbeatMapper.toDto(instanceHeartbeat);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInstanceHeartbeatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, instanceHeartbeatDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(instanceHeartbeatDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InstanceHeartbeat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(instanceHeartbeatSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInstanceHeartbeat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(instanceHeartbeatSearchRepository.findAll());
        instanceHeartbeat.setId(longCount.incrementAndGet());

        // Create the InstanceHeartbeat
        InstanceHeartbeatDTO instanceHeartbeatDTO = instanceHeartbeatMapper.toDto(instanceHeartbeat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInstanceHeartbeatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(instanceHeartbeatDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InstanceHeartbeat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(instanceHeartbeatSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInstanceHeartbeat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(instanceHeartbeatSearchRepository.findAll());
        instanceHeartbeat.setId(longCount.incrementAndGet());

        // Create the InstanceHeartbeat
        InstanceHeartbeatDTO instanceHeartbeatDTO = instanceHeartbeatMapper.toDto(instanceHeartbeat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInstanceHeartbeatMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(instanceHeartbeatDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the InstanceHeartbeat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(instanceHeartbeatSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteInstanceHeartbeat() throws Exception {
        // Initialize the database
        insertedInstanceHeartbeat = instanceHeartbeatRepository.saveAndFlush(instanceHeartbeat);
        instanceHeartbeatRepository.save(instanceHeartbeat);
        instanceHeartbeatSearchRepository.save(instanceHeartbeat);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(instanceHeartbeatSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the instanceHeartbeat
        restInstanceHeartbeatMockMvc
            .perform(delete(ENTITY_API_URL_ID, instanceHeartbeat.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(instanceHeartbeatSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchInstanceHeartbeat() throws Exception {
        // Initialize the database
        insertedInstanceHeartbeat = instanceHeartbeatRepository.saveAndFlush(instanceHeartbeat);
        instanceHeartbeatSearchRepository.save(instanceHeartbeat);

        // Search the instanceHeartbeat
        restInstanceHeartbeatMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + instanceHeartbeat.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(instanceHeartbeat.getId().intValue())))
            .andExpect(jsonPath("$.[*].executedAt").value(hasItem(DEFAULT_EXECUTED_AT.toString())))
            .andExpect(jsonPath("$.[*].heartbeatType").value(hasItem(DEFAULT_HEARTBEAT_TYPE)))
            .andExpect(jsonPath("$.[*].success").value(hasItem(DEFAULT_SUCCESS)))
            .andExpect(jsonPath("$.[*].responseTimeMs").value(hasItem(DEFAULT_RESPONSE_TIME_MS)))
            .andExpect(jsonPath("$.[*].packetLoss").value(hasItem(DEFAULT_PACKET_LOSS.doubleValue())))
            .andExpect(jsonPath("$.[*].jitterMs").value(hasItem(DEFAULT_JITTER_MS)))
            .andExpect(jsonPath("$.[*].cpuUsage").value(hasItem(DEFAULT_CPU_USAGE.doubleValue())))
            .andExpect(jsonPath("$.[*].memoryUsage").value(hasItem(DEFAULT_MEMORY_USAGE.doubleValue())))
            .andExpect(jsonPath("$.[*].diskUsage").value(hasItem(DEFAULT_DISK_USAGE.doubleValue())))
            .andExpect(jsonPath("$.[*].loadAverage").value(hasItem(DEFAULT_LOAD_AVERAGE.doubleValue())))
            .andExpect(jsonPath("$.[*].processCount").value(hasItem(DEFAULT_PROCESS_COUNT)))
            .andExpect(jsonPath("$.[*].networkRxBytes").value(hasItem(DEFAULT_NETWORK_RX_BYTES.intValue())))
            .andExpect(jsonPath("$.[*].networkTxBytes").value(hasItem(DEFAULT_NETWORK_TX_BYTES.intValue())))
            .andExpect(jsonPath("$.[*].uptimeSeconds").value(hasItem(DEFAULT_UPTIME_SECONDS.intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].errorMessage").value(hasItem(DEFAULT_ERROR_MESSAGE.toString())))
            .andExpect(jsonPath("$.[*].errorType").value(hasItem(DEFAULT_ERROR_TYPE)))
            .andExpect(jsonPath("$.[*].metadata").value(hasItem(DEFAULT_METADATA.toString())));
    }

    protected long getRepositoryCount() {
        return instanceHeartbeatRepository.count();
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

    protected InstanceHeartbeat getPersistedInstanceHeartbeat(InstanceHeartbeat instanceHeartbeat) {
        return instanceHeartbeatRepository.findById(instanceHeartbeat.getId()).orElseThrow();
    }

    protected void assertPersistedInstanceHeartbeatToMatchAllProperties(InstanceHeartbeat expectedInstanceHeartbeat) {
        assertInstanceHeartbeatAllPropertiesEquals(expectedInstanceHeartbeat, getPersistedInstanceHeartbeat(expectedInstanceHeartbeat));
    }

    protected void assertPersistedInstanceHeartbeatToMatchUpdatableProperties(InstanceHeartbeat expectedInstanceHeartbeat) {
        assertInstanceHeartbeatAllUpdatablePropertiesEquals(
            expectedInstanceHeartbeat,
            getPersistedInstanceHeartbeat(expectedInstanceHeartbeat)
        );
    }
}
