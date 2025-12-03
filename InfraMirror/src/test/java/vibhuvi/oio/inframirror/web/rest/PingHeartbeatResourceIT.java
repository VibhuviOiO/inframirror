package vibhuvi.oio.inframirror.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static vibhuvi.oio.inframirror.domain.PingHeartbeatAsserts.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
import vibhuvi.oio.inframirror.domain.Instance;
import vibhuvi.oio.inframirror.domain.PingHeartbeat;
import vibhuvi.oio.inframirror.repository.PingHeartbeatRepository;
import vibhuvi.oio.inframirror.repository.search.PingHeartbeatSearchRepository;
import vibhuvi.oio.inframirror.service.mapper.PingHeartbeatMapper;

/**
 * Integration tests for the {@link PingHeartbeatResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PingHeartbeatResourceIT {

    private static final Instant DEFAULT_EXECUTED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EXECUTED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_HEARTBEAT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_HEARTBEAT_TYPE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_SUCCESS = false;
    private static final Boolean UPDATED_SUCCESS = true;

    private static final Integer DEFAULT_RESPONSE_TIME_MS = 1;
    private static final Integer UPDATED_RESPONSE_TIME_MS = 2;
    private static final Integer SMALLER_RESPONSE_TIME_MS = 1 - 1;

    private static final Float DEFAULT_PACKET_LOSS = 1F;
    private static final Float UPDATED_PACKET_LOSS = 2F;
    private static final Float SMALLER_PACKET_LOSS = 1F - 1F;

    private static final Integer DEFAULT_JITTER_MS = 1;
    private static final Integer UPDATED_JITTER_MS = 2;
    private static final Integer SMALLER_JITTER_MS = 1 - 1;

    private static final Float DEFAULT_CPU_USAGE = 1F;
    private static final Float UPDATED_CPU_USAGE = 2F;
    private static final Float SMALLER_CPU_USAGE = 1F - 1F;

    private static final Float DEFAULT_MEMORY_USAGE = 1F;
    private static final Float UPDATED_MEMORY_USAGE = 2F;
    private static final Float SMALLER_MEMORY_USAGE = 1F - 1F;

    private static final Float DEFAULT_DISK_USAGE = 1F;
    private static final Float UPDATED_DISK_USAGE = 2F;
    private static final Float SMALLER_DISK_USAGE = 1F - 1F;

    private static final Float DEFAULT_LOAD_AVERAGE = 1F;
    private static final Float UPDATED_LOAD_AVERAGE = 2F;
    private static final Float SMALLER_LOAD_AVERAGE = 1F - 1F;

    private static final Integer DEFAULT_PROCESS_COUNT = 1;
    private static final Integer UPDATED_PROCESS_COUNT = 2;
    private static final Integer SMALLER_PROCESS_COUNT = 1 - 1;

    private static final Long DEFAULT_NETWORK_RX_BYTES = 1L;
    private static final Long UPDATED_NETWORK_RX_BYTES = 2L;
    private static final Long SMALLER_NETWORK_RX_BYTES = 1L - 1L;

    private static final Long DEFAULT_NETWORK_TX_BYTES = 1L;
    private static final Long UPDATED_NETWORK_TX_BYTES = 2L;
    private static final Long SMALLER_NETWORK_TX_BYTES = 1L - 1L;

    private static final Long DEFAULT_UPTIME_SECONDS = 1L;
    private static final Long UPDATED_UPTIME_SECONDS = 2L;
    private static final Long SMALLER_UPTIME_SECONDS = 1L - 1L;

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_ERROR_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_ERROR_MESSAGE = "BBBBBBBBBB";

    private static final String DEFAULT_ERROR_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_ERROR_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_METADATA = "AAAAAAAAAA";
    private static final String UPDATED_METADATA = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/ping-heartbeats";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/ping-heartbeats/_search";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PingHeartbeatRepository pingHeartbeatRepository;

    @Autowired
    private PingHeartbeatMapper pingHeartbeatMapper;

    @Autowired
    private PingHeartbeatSearchRepository pingHeartbeatSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPingHeartbeatMockMvc;

    private PingHeartbeat pingHeartbeat;

    private PingHeartbeat insertedPingHeartbeat;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PingHeartbeat createEntity(EntityManager em) {
        PingHeartbeat pingHeartbeat = new PingHeartbeat()
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
        pingHeartbeat.setInstance(instance);
        return pingHeartbeat;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PingHeartbeat createUpdatedEntity(EntityManager em) {
        PingHeartbeat updatedPingHeartbeat = new PingHeartbeat()
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
        updatedPingHeartbeat.setInstance(instance);
        return updatedPingHeartbeat;
    }

    @BeforeEach
    void initTest() {
        pingHeartbeat = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedPingHeartbeat != null) {
            pingHeartbeatRepository.delete(insertedPingHeartbeat);
            pingHeartbeatSearchRepository.delete(insertedPingHeartbeat);
            insertedPingHeartbeat = null;
        }
    }

    @Test
    @Transactional
    void getAllPingHeartbeats() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList
        restPingHeartbeatMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pingHeartbeat.getId().intValue())))
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
    void getPingHeartbeat() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get the pingHeartbeat
        restPingHeartbeatMockMvc
            .perform(get(ENTITY_API_URL_ID, pingHeartbeat.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(pingHeartbeat.getId().intValue()))
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
    void getPingHeartbeatsByIdFiltering() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        Long id = pingHeartbeat.getId();

        defaultPingHeartbeatFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultPingHeartbeatFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultPingHeartbeatFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByExecutedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where executedAt equals to
        defaultPingHeartbeatFiltering("executedAt.equals=" + DEFAULT_EXECUTED_AT, "executedAt.equals=" + UPDATED_EXECUTED_AT);
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByExecutedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where executedAt in
        defaultPingHeartbeatFiltering(
            "executedAt.in=" + DEFAULT_EXECUTED_AT + "," + UPDATED_EXECUTED_AT,
            "executedAt.in=" + UPDATED_EXECUTED_AT
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByExecutedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where executedAt is not null
        defaultPingHeartbeatFiltering("executedAt.specified=true", "executedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByHeartbeatTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where heartbeatType equals to
        defaultPingHeartbeatFiltering("heartbeatType.equals=" + DEFAULT_HEARTBEAT_TYPE, "heartbeatType.equals=" + UPDATED_HEARTBEAT_TYPE);
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByHeartbeatTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where heartbeatType in
        defaultPingHeartbeatFiltering(
            "heartbeatType.in=" + DEFAULT_HEARTBEAT_TYPE + "," + UPDATED_HEARTBEAT_TYPE,
            "heartbeatType.in=" + UPDATED_HEARTBEAT_TYPE
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByHeartbeatTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where heartbeatType is not null
        defaultPingHeartbeatFiltering("heartbeatType.specified=true", "heartbeatType.specified=false");
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByHeartbeatTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where heartbeatType contains
        defaultPingHeartbeatFiltering(
            "heartbeatType.contains=" + DEFAULT_HEARTBEAT_TYPE,
            "heartbeatType.contains=" + UPDATED_HEARTBEAT_TYPE
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByHeartbeatTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where heartbeatType does not contain
        defaultPingHeartbeatFiltering(
            "heartbeatType.doesNotContain=" + UPDATED_HEARTBEAT_TYPE,
            "heartbeatType.doesNotContain=" + DEFAULT_HEARTBEAT_TYPE
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsBySuccessIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where success equals to
        defaultPingHeartbeatFiltering("success.equals=" + DEFAULT_SUCCESS, "success.equals=" + UPDATED_SUCCESS);
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsBySuccessIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where success in
        defaultPingHeartbeatFiltering("success.in=" + DEFAULT_SUCCESS + "," + UPDATED_SUCCESS, "success.in=" + UPDATED_SUCCESS);
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsBySuccessIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where success is not null
        defaultPingHeartbeatFiltering("success.specified=true", "success.specified=false");
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByResponseTimeMsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where responseTimeMs equals to
        defaultPingHeartbeatFiltering(
            "responseTimeMs.equals=" + DEFAULT_RESPONSE_TIME_MS,
            "responseTimeMs.equals=" + UPDATED_RESPONSE_TIME_MS
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByResponseTimeMsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where responseTimeMs in
        defaultPingHeartbeatFiltering(
            "responseTimeMs.in=" + DEFAULT_RESPONSE_TIME_MS + "," + UPDATED_RESPONSE_TIME_MS,
            "responseTimeMs.in=" + UPDATED_RESPONSE_TIME_MS
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByResponseTimeMsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where responseTimeMs is not null
        defaultPingHeartbeatFiltering("responseTimeMs.specified=true", "responseTimeMs.specified=false");
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByResponseTimeMsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where responseTimeMs is greater than or equal to
        defaultPingHeartbeatFiltering(
            "responseTimeMs.greaterThanOrEqual=" + DEFAULT_RESPONSE_TIME_MS,
            "responseTimeMs.greaterThanOrEqual=" + UPDATED_RESPONSE_TIME_MS
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByResponseTimeMsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where responseTimeMs is less than or equal to
        defaultPingHeartbeatFiltering(
            "responseTimeMs.lessThanOrEqual=" + DEFAULT_RESPONSE_TIME_MS,
            "responseTimeMs.lessThanOrEqual=" + SMALLER_RESPONSE_TIME_MS
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByResponseTimeMsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where responseTimeMs is less than
        defaultPingHeartbeatFiltering(
            "responseTimeMs.lessThan=" + UPDATED_RESPONSE_TIME_MS,
            "responseTimeMs.lessThan=" + DEFAULT_RESPONSE_TIME_MS
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByResponseTimeMsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where responseTimeMs is greater than
        defaultPingHeartbeatFiltering(
            "responseTimeMs.greaterThan=" + SMALLER_RESPONSE_TIME_MS,
            "responseTimeMs.greaterThan=" + DEFAULT_RESPONSE_TIME_MS
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByPacketLossIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where packetLoss equals to
        defaultPingHeartbeatFiltering("packetLoss.equals=" + DEFAULT_PACKET_LOSS, "packetLoss.equals=" + UPDATED_PACKET_LOSS);
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByPacketLossIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where packetLoss in
        defaultPingHeartbeatFiltering(
            "packetLoss.in=" + DEFAULT_PACKET_LOSS + "," + UPDATED_PACKET_LOSS,
            "packetLoss.in=" + UPDATED_PACKET_LOSS
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByPacketLossIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where packetLoss is not null
        defaultPingHeartbeatFiltering("packetLoss.specified=true", "packetLoss.specified=false");
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByPacketLossIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where packetLoss is greater than or equal to
        defaultPingHeartbeatFiltering(
            "packetLoss.greaterThanOrEqual=" + DEFAULT_PACKET_LOSS,
            "packetLoss.greaterThanOrEqual=" + UPDATED_PACKET_LOSS
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByPacketLossIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where packetLoss is less than or equal to
        defaultPingHeartbeatFiltering(
            "packetLoss.lessThanOrEqual=" + DEFAULT_PACKET_LOSS,
            "packetLoss.lessThanOrEqual=" + SMALLER_PACKET_LOSS
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByPacketLossIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where packetLoss is less than
        defaultPingHeartbeatFiltering("packetLoss.lessThan=" + UPDATED_PACKET_LOSS, "packetLoss.lessThan=" + DEFAULT_PACKET_LOSS);
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByPacketLossIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where packetLoss is greater than
        defaultPingHeartbeatFiltering("packetLoss.greaterThan=" + SMALLER_PACKET_LOSS, "packetLoss.greaterThan=" + DEFAULT_PACKET_LOSS);
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByJitterMsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where jitterMs equals to
        defaultPingHeartbeatFiltering("jitterMs.equals=" + DEFAULT_JITTER_MS, "jitterMs.equals=" + UPDATED_JITTER_MS);
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByJitterMsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where jitterMs in
        defaultPingHeartbeatFiltering("jitterMs.in=" + DEFAULT_JITTER_MS + "," + UPDATED_JITTER_MS, "jitterMs.in=" + UPDATED_JITTER_MS);
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByJitterMsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where jitterMs is not null
        defaultPingHeartbeatFiltering("jitterMs.specified=true", "jitterMs.specified=false");
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByJitterMsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where jitterMs is greater than or equal to
        defaultPingHeartbeatFiltering(
            "jitterMs.greaterThanOrEqual=" + DEFAULT_JITTER_MS,
            "jitterMs.greaterThanOrEqual=" + UPDATED_JITTER_MS
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByJitterMsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where jitterMs is less than or equal to
        defaultPingHeartbeatFiltering("jitterMs.lessThanOrEqual=" + DEFAULT_JITTER_MS, "jitterMs.lessThanOrEqual=" + SMALLER_JITTER_MS);
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByJitterMsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where jitterMs is less than
        defaultPingHeartbeatFiltering("jitterMs.lessThan=" + UPDATED_JITTER_MS, "jitterMs.lessThan=" + DEFAULT_JITTER_MS);
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByJitterMsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where jitterMs is greater than
        defaultPingHeartbeatFiltering("jitterMs.greaterThan=" + SMALLER_JITTER_MS, "jitterMs.greaterThan=" + DEFAULT_JITTER_MS);
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByCpuUsageIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where cpuUsage equals to
        defaultPingHeartbeatFiltering("cpuUsage.equals=" + DEFAULT_CPU_USAGE, "cpuUsage.equals=" + UPDATED_CPU_USAGE);
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByCpuUsageIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where cpuUsage in
        defaultPingHeartbeatFiltering("cpuUsage.in=" + DEFAULT_CPU_USAGE + "," + UPDATED_CPU_USAGE, "cpuUsage.in=" + UPDATED_CPU_USAGE);
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByCpuUsageIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where cpuUsage is not null
        defaultPingHeartbeatFiltering("cpuUsage.specified=true", "cpuUsage.specified=false");
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByCpuUsageIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where cpuUsage is greater than or equal to
        defaultPingHeartbeatFiltering(
            "cpuUsage.greaterThanOrEqual=" + DEFAULT_CPU_USAGE,
            "cpuUsage.greaterThanOrEqual=" + UPDATED_CPU_USAGE
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByCpuUsageIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where cpuUsage is less than or equal to
        defaultPingHeartbeatFiltering("cpuUsage.lessThanOrEqual=" + DEFAULT_CPU_USAGE, "cpuUsage.lessThanOrEqual=" + SMALLER_CPU_USAGE);
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByCpuUsageIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where cpuUsage is less than
        defaultPingHeartbeatFiltering("cpuUsage.lessThan=" + UPDATED_CPU_USAGE, "cpuUsage.lessThan=" + DEFAULT_CPU_USAGE);
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByCpuUsageIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where cpuUsage is greater than
        defaultPingHeartbeatFiltering("cpuUsage.greaterThan=" + SMALLER_CPU_USAGE, "cpuUsage.greaterThan=" + DEFAULT_CPU_USAGE);
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByMemoryUsageIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where memoryUsage equals to
        defaultPingHeartbeatFiltering("memoryUsage.equals=" + DEFAULT_MEMORY_USAGE, "memoryUsage.equals=" + UPDATED_MEMORY_USAGE);
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByMemoryUsageIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where memoryUsage in
        defaultPingHeartbeatFiltering(
            "memoryUsage.in=" + DEFAULT_MEMORY_USAGE + "," + UPDATED_MEMORY_USAGE,
            "memoryUsage.in=" + UPDATED_MEMORY_USAGE
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByMemoryUsageIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where memoryUsage is not null
        defaultPingHeartbeatFiltering("memoryUsage.specified=true", "memoryUsage.specified=false");
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByMemoryUsageIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where memoryUsage is greater than or equal to
        defaultPingHeartbeatFiltering(
            "memoryUsage.greaterThanOrEqual=" + DEFAULT_MEMORY_USAGE,
            "memoryUsage.greaterThanOrEqual=" + UPDATED_MEMORY_USAGE
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByMemoryUsageIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where memoryUsage is less than or equal to
        defaultPingHeartbeatFiltering(
            "memoryUsage.lessThanOrEqual=" + DEFAULT_MEMORY_USAGE,
            "memoryUsage.lessThanOrEqual=" + SMALLER_MEMORY_USAGE
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByMemoryUsageIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where memoryUsage is less than
        defaultPingHeartbeatFiltering("memoryUsage.lessThan=" + UPDATED_MEMORY_USAGE, "memoryUsage.lessThan=" + DEFAULT_MEMORY_USAGE);
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByMemoryUsageIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where memoryUsage is greater than
        defaultPingHeartbeatFiltering("memoryUsage.greaterThan=" + SMALLER_MEMORY_USAGE, "memoryUsage.greaterThan=" + DEFAULT_MEMORY_USAGE);
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByDiskUsageIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where diskUsage equals to
        defaultPingHeartbeatFiltering("diskUsage.equals=" + DEFAULT_DISK_USAGE, "diskUsage.equals=" + UPDATED_DISK_USAGE);
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByDiskUsageIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where diskUsage in
        defaultPingHeartbeatFiltering(
            "diskUsage.in=" + DEFAULT_DISK_USAGE + "," + UPDATED_DISK_USAGE,
            "diskUsage.in=" + UPDATED_DISK_USAGE
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByDiskUsageIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where diskUsage is not null
        defaultPingHeartbeatFiltering("diskUsage.specified=true", "diskUsage.specified=false");
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByDiskUsageIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where diskUsage is greater than or equal to
        defaultPingHeartbeatFiltering(
            "diskUsage.greaterThanOrEqual=" + DEFAULT_DISK_USAGE,
            "diskUsage.greaterThanOrEqual=" + UPDATED_DISK_USAGE
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByDiskUsageIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where diskUsage is less than or equal to
        defaultPingHeartbeatFiltering("diskUsage.lessThanOrEqual=" + DEFAULT_DISK_USAGE, "diskUsage.lessThanOrEqual=" + SMALLER_DISK_USAGE);
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByDiskUsageIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where diskUsage is less than
        defaultPingHeartbeatFiltering("diskUsage.lessThan=" + UPDATED_DISK_USAGE, "diskUsage.lessThan=" + DEFAULT_DISK_USAGE);
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByDiskUsageIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where diskUsage is greater than
        defaultPingHeartbeatFiltering("diskUsage.greaterThan=" + SMALLER_DISK_USAGE, "diskUsage.greaterThan=" + DEFAULT_DISK_USAGE);
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByLoadAverageIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where loadAverage equals to
        defaultPingHeartbeatFiltering("loadAverage.equals=" + DEFAULT_LOAD_AVERAGE, "loadAverage.equals=" + UPDATED_LOAD_AVERAGE);
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByLoadAverageIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where loadAverage in
        defaultPingHeartbeatFiltering(
            "loadAverage.in=" + DEFAULT_LOAD_AVERAGE + "," + UPDATED_LOAD_AVERAGE,
            "loadAverage.in=" + UPDATED_LOAD_AVERAGE
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByLoadAverageIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where loadAverage is not null
        defaultPingHeartbeatFiltering("loadAverage.specified=true", "loadAverage.specified=false");
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByLoadAverageIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where loadAverage is greater than or equal to
        defaultPingHeartbeatFiltering(
            "loadAverage.greaterThanOrEqual=" + DEFAULT_LOAD_AVERAGE,
            "loadAverage.greaterThanOrEqual=" + UPDATED_LOAD_AVERAGE
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByLoadAverageIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where loadAverage is less than or equal to
        defaultPingHeartbeatFiltering(
            "loadAverage.lessThanOrEqual=" + DEFAULT_LOAD_AVERAGE,
            "loadAverage.lessThanOrEqual=" + SMALLER_LOAD_AVERAGE
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByLoadAverageIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where loadAverage is less than
        defaultPingHeartbeatFiltering("loadAverage.lessThan=" + UPDATED_LOAD_AVERAGE, "loadAverage.lessThan=" + DEFAULT_LOAD_AVERAGE);
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByLoadAverageIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where loadAverage is greater than
        defaultPingHeartbeatFiltering("loadAverage.greaterThan=" + SMALLER_LOAD_AVERAGE, "loadAverage.greaterThan=" + DEFAULT_LOAD_AVERAGE);
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByProcessCountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where processCount equals to
        defaultPingHeartbeatFiltering("processCount.equals=" + DEFAULT_PROCESS_COUNT, "processCount.equals=" + UPDATED_PROCESS_COUNT);
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByProcessCountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where processCount in
        defaultPingHeartbeatFiltering(
            "processCount.in=" + DEFAULT_PROCESS_COUNT + "," + UPDATED_PROCESS_COUNT,
            "processCount.in=" + UPDATED_PROCESS_COUNT
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByProcessCountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where processCount is not null
        defaultPingHeartbeatFiltering("processCount.specified=true", "processCount.specified=false");
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByProcessCountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where processCount is greater than or equal to
        defaultPingHeartbeatFiltering(
            "processCount.greaterThanOrEqual=" + DEFAULT_PROCESS_COUNT,
            "processCount.greaterThanOrEqual=" + UPDATED_PROCESS_COUNT
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByProcessCountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where processCount is less than or equal to
        defaultPingHeartbeatFiltering(
            "processCount.lessThanOrEqual=" + DEFAULT_PROCESS_COUNT,
            "processCount.lessThanOrEqual=" + SMALLER_PROCESS_COUNT
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByProcessCountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where processCount is less than
        defaultPingHeartbeatFiltering("processCount.lessThan=" + UPDATED_PROCESS_COUNT, "processCount.lessThan=" + DEFAULT_PROCESS_COUNT);
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByProcessCountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where processCount is greater than
        defaultPingHeartbeatFiltering(
            "processCount.greaterThan=" + SMALLER_PROCESS_COUNT,
            "processCount.greaterThan=" + DEFAULT_PROCESS_COUNT
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByNetworkRxBytesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where networkRxBytes equals to
        defaultPingHeartbeatFiltering(
            "networkRxBytes.equals=" + DEFAULT_NETWORK_RX_BYTES,
            "networkRxBytes.equals=" + UPDATED_NETWORK_RX_BYTES
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByNetworkRxBytesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where networkRxBytes in
        defaultPingHeartbeatFiltering(
            "networkRxBytes.in=" + DEFAULT_NETWORK_RX_BYTES + "," + UPDATED_NETWORK_RX_BYTES,
            "networkRxBytes.in=" + UPDATED_NETWORK_RX_BYTES
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByNetworkRxBytesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where networkRxBytes is not null
        defaultPingHeartbeatFiltering("networkRxBytes.specified=true", "networkRxBytes.specified=false");
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByNetworkRxBytesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where networkRxBytes is greater than or equal to
        defaultPingHeartbeatFiltering(
            "networkRxBytes.greaterThanOrEqual=" + DEFAULT_NETWORK_RX_BYTES,
            "networkRxBytes.greaterThanOrEqual=" + UPDATED_NETWORK_RX_BYTES
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByNetworkRxBytesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where networkRxBytes is less than or equal to
        defaultPingHeartbeatFiltering(
            "networkRxBytes.lessThanOrEqual=" + DEFAULT_NETWORK_RX_BYTES,
            "networkRxBytes.lessThanOrEqual=" + SMALLER_NETWORK_RX_BYTES
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByNetworkRxBytesIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where networkRxBytes is less than
        defaultPingHeartbeatFiltering(
            "networkRxBytes.lessThan=" + UPDATED_NETWORK_RX_BYTES,
            "networkRxBytes.lessThan=" + DEFAULT_NETWORK_RX_BYTES
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByNetworkRxBytesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where networkRxBytes is greater than
        defaultPingHeartbeatFiltering(
            "networkRxBytes.greaterThan=" + SMALLER_NETWORK_RX_BYTES,
            "networkRxBytes.greaterThan=" + DEFAULT_NETWORK_RX_BYTES
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByNetworkTxBytesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where networkTxBytes equals to
        defaultPingHeartbeatFiltering(
            "networkTxBytes.equals=" + DEFAULT_NETWORK_TX_BYTES,
            "networkTxBytes.equals=" + UPDATED_NETWORK_TX_BYTES
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByNetworkTxBytesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where networkTxBytes in
        defaultPingHeartbeatFiltering(
            "networkTxBytes.in=" + DEFAULT_NETWORK_TX_BYTES + "," + UPDATED_NETWORK_TX_BYTES,
            "networkTxBytes.in=" + UPDATED_NETWORK_TX_BYTES
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByNetworkTxBytesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where networkTxBytes is not null
        defaultPingHeartbeatFiltering("networkTxBytes.specified=true", "networkTxBytes.specified=false");
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByNetworkTxBytesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where networkTxBytes is greater than or equal to
        defaultPingHeartbeatFiltering(
            "networkTxBytes.greaterThanOrEqual=" + DEFAULT_NETWORK_TX_BYTES,
            "networkTxBytes.greaterThanOrEqual=" + UPDATED_NETWORK_TX_BYTES
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByNetworkTxBytesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where networkTxBytes is less than or equal to
        defaultPingHeartbeatFiltering(
            "networkTxBytes.lessThanOrEqual=" + DEFAULT_NETWORK_TX_BYTES,
            "networkTxBytes.lessThanOrEqual=" + SMALLER_NETWORK_TX_BYTES
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByNetworkTxBytesIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where networkTxBytes is less than
        defaultPingHeartbeatFiltering(
            "networkTxBytes.lessThan=" + UPDATED_NETWORK_TX_BYTES,
            "networkTxBytes.lessThan=" + DEFAULT_NETWORK_TX_BYTES
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByNetworkTxBytesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where networkTxBytes is greater than
        defaultPingHeartbeatFiltering(
            "networkTxBytes.greaterThan=" + SMALLER_NETWORK_TX_BYTES,
            "networkTxBytes.greaterThan=" + DEFAULT_NETWORK_TX_BYTES
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByUptimeSecondsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where uptimeSeconds equals to
        defaultPingHeartbeatFiltering("uptimeSeconds.equals=" + DEFAULT_UPTIME_SECONDS, "uptimeSeconds.equals=" + UPDATED_UPTIME_SECONDS);
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByUptimeSecondsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where uptimeSeconds in
        defaultPingHeartbeatFiltering(
            "uptimeSeconds.in=" + DEFAULT_UPTIME_SECONDS + "," + UPDATED_UPTIME_SECONDS,
            "uptimeSeconds.in=" + UPDATED_UPTIME_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByUptimeSecondsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where uptimeSeconds is not null
        defaultPingHeartbeatFiltering("uptimeSeconds.specified=true", "uptimeSeconds.specified=false");
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByUptimeSecondsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where uptimeSeconds is greater than or equal to
        defaultPingHeartbeatFiltering(
            "uptimeSeconds.greaterThanOrEqual=" + DEFAULT_UPTIME_SECONDS,
            "uptimeSeconds.greaterThanOrEqual=" + UPDATED_UPTIME_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByUptimeSecondsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where uptimeSeconds is less than or equal to
        defaultPingHeartbeatFiltering(
            "uptimeSeconds.lessThanOrEqual=" + DEFAULT_UPTIME_SECONDS,
            "uptimeSeconds.lessThanOrEqual=" + SMALLER_UPTIME_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByUptimeSecondsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where uptimeSeconds is less than
        defaultPingHeartbeatFiltering(
            "uptimeSeconds.lessThan=" + UPDATED_UPTIME_SECONDS,
            "uptimeSeconds.lessThan=" + DEFAULT_UPTIME_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByUptimeSecondsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where uptimeSeconds is greater than
        defaultPingHeartbeatFiltering(
            "uptimeSeconds.greaterThan=" + SMALLER_UPTIME_SECONDS,
            "uptimeSeconds.greaterThan=" + DEFAULT_UPTIME_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where status equals to
        defaultPingHeartbeatFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where status in
        defaultPingHeartbeatFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where status is not null
        defaultPingHeartbeatFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByStatusContainsSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where status contains
        defaultPingHeartbeatFiltering("status.contains=" + DEFAULT_STATUS, "status.contains=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByStatusNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where status does not contain
        defaultPingHeartbeatFiltering("status.doesNotContain=" + UPDATED_STATUS, "status.doesNotContain=" + DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByErrorTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where errorType equals to
        defaultPingHeartbeatFiltering("errorType.equals=" + DEFAULT_ERROR_TYPE, "errorType.equals=" + UPDATED_ERROR_TYPE);
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByErrorTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where errorType in
        defaultPingHeartbeatFiltering(
            "errorType.in=" + DEFAULT_ERROR_TYPE + "," + UPDATED_ERROR_TYPE,
            "errorType.in=" + UPDATED_ERROR_TYPE
        );
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByErrorTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where errorType is not null
        defaultPingHeartbeatFiltering("errorType.specified=true", "errorType.specified=false");
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByErrorTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where errorType contains
        defaultPingHeartbeatFiltering("errorType.contains=" + DEFAULT_ERROR_TYPE, "errorType.contains=" + UPDATED_ERROR_TYPE);
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByErrorTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);

        // Get all the pingHeartbeatList where errorType does not contain
        defaultPingHeartbeatFiltering("errorType.doesNotContain=" + UPDATED_ERROR_TYPE, "errorType.doesNotContain=" + DEFAULT_ERROR_TYPE);
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByInstanceIsEqualToSomething() throws Exception {
        Instance instance;
        if (TestUtil.findAll(em, Instance.class).isEmpty()) {
            pingHeartbeatRepository.saveAndFlush(pingHeartbeat);
            instance = InstanceResourceIT.createEntity(em);
        } else {
            instance = TestUtil.findAll(em, Instance.class).get(0);
        }
        em.persist(instance);
        em.flush();
        pingHeartbeat.setInstance(instance);
        pingHeartbeatRepository.saveAndFlush(pingHeartbeat);
        Long instanceId = instance.getId();
        // Get all the pingHeartbeatList where instance equals to instanceId
        defaultPingHeartbeatShouldBeFound("instanceId.equals=" + instanceId);

        // Get all the pingHeartbeatList where instance equals to (instanceId + 1)
        defaultPingHeartbeatShouldNotBeFound("instanceId.equals=" + (instanceId + 1));
    }

    @Test
    @Transactional
    void getAllPingHeartbeatsByAgentIsEqualToSomething() throws Exception {
        Agent agent;
        if (TestUtil.findAll(em, Agent.class).isEmpty()) {
            pingHeartbeatRepository.saveAndFlush(pingHeartbeat);
            agent = AgentResourceIT.createEntity();
        } else {
            agent = TestUtil.findAll(em, Agent.class).get(0);
        }
        em.persist(agent);
        em.flush();
        pingHeartbeat.setAgent(agent);
        pingHeartbeatRepository.saveAndFlush(pingHeartbeat);
        Long agentId = agent.getId();
        // Get all the pingHeartbeatList where agent equals to agentId
        defaultPingHeartbeatShouldBeFound("agentId.equals=" + agentId);

        // Get all the pingHeartbeatList where agent equals to (agentId + 1)
        defaultPingHeartbeatShouldNotBeFound("agentId.equals=" + (agentId + 1));
    }

    private void defaultPingHeartbeatFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultPingHeartbeatShouldBeFound(shouldBeFound);
        defaultPingHeartbeatShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPingHeartbeatShouldBeFound(String filter) throws Exception {
        restPingHeartbeatMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pingHeartbeat.getId().intValue())))
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

        // Check, that the count call also returns 1
        restPingHeartbeatMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPingHeartbeatShouldNotBeFound(String filter) throws Exception {
        restPingHeartbeatMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPingHeartbeatMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPingHeartbeat() throws Exception {
        // Get the pingHeartbeat
        restPingHeartbeatMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void searchPingHeartbeat() throws Exception {
        // Initialize the database
        insertedPingHeartbeat = pingHeartbeatRepository.saveAndFlush(pingHeartbeat);
        pingHeartbeatSearchRepository.save(pingHeartbeat);

        // Search the pingHeartbeat
        restPingHeartbeatMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + pingHeartbeat.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pingHeartbeat.getId().intValue())))
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
        return pingHeartbeatRepository.count();
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

    protected PingHeartbeat getPersistedPingHeartbeat(PingHeartbeat pingHeartbeat) {
        return pingHeartbeatRepository.findById(pingHeartbeat.getId()).orElseThrow();
    }

    protected void assertPersistedPingHeartbeatToMatchAllProperties(PingHeartbeat expectedPingHeartbeat) {
        assertPingHeartbeatAllPropertiesEquals(expectedPingHeartbeat, getPersistedPingHeartbeat(expectedPingHeartbeat));
    }

    protected void assertPersistedPingHeartbeatToMatchUpdatableProperties(PingHeartbeat expectedPingHeartbeat) {
        assertPingHeartbeatAllUpdatablePropertiesEquals(expectedPingHeartbeat, getPersistedPingHeartbeat(expectedPingHeartbeat));
    }
}
