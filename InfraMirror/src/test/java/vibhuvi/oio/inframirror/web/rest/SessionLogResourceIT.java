package vibhuvi.oio.inframirror.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static vibhuvi.oio.inframirror.domain.SessionLogAsserts.*;

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
import vibhuvi.oio.inframirror.domain.SessionLog;
import vibhuvi.oio.inframirror.domain.User;
import vibhuvi.oio.inframirror.repository.SessionLogRepository;
import vibhuvi.oio.inframirror.repository.UserRepository;
import vibhuvi.oio.inframirror.repository.search.SessionLogSearchRepository;
import vibhuvi.oio.inframirror.service.mapper.SessionLogMapper;

/**
 * Integration tests for the {@link SessionLogResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SessionLogResourceIT {

    private static final String DEFAULT_SESSION_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_SESSION_TYPE = "BBBBBBBBBB";

    private static final Instant DEFAULT_START_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_DURATION = 1;
    private static final Integer UPDATED_DURATION = 2;
    private static final Integer SMALLER_DURATION = 1 - 1;

    private static final String DEFAULT_SOURCE_IP_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_SOURCE_IP_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_TERMINATION_REASON = "AAAAAAAAAA";
    private static final String UPDATED_TERMINATION_REASON = "BBBBBBBBBB";

    private static final Integer DEFAULT_COMMANDS_EXECUTED = 1;
    private static final Integer UPDATED_COMMANDS_EXECUTED = 2;
    private static final Integer SMALLER_COMMANDS_EXECUTED = 1 - 1;

    private static final Long DEFAULT_BYTES_TRANSFERRED = 1L;
    private static final Long UPDATED_BYTES_TRANSFERRED = 2L;
    private static final Long SMALLER_BYTES_TRANSFERRED = 1L - 1L;

    private static final String DEFAULT_SESSION_ID = "AAAAAAAAAA";
    private static final String UPDATED_SESSION_ID = "BBBBBBBBBB";

    private static final String DEFAULT_METADATA = "AAAAAAAAAA";
    private static final String UPDATED_METADATA = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/session-logs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/session-logs/_search";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SessionLogRepository sessionLogRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionLogMapper sessionLogMapper;

    @Autowired
    private SessionLogSearchRepository sessionLogSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSessionLogMockMvc;

    private SessionLog sessionLog;

    private SessionLog insertedSessionLog;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SessionLog createEntity(EntityManager em) {
        SessionLog sessionLog = new SessionLog()
            .sessionType(DEFAULT_SESSION_TYPE)
            .startTime(DEFAULT_START_TIME)
            .endTime(DEFAULT_END_TIME)
            .duration(DEFAULT_DURATION)
            .sourceIpAddress(DEFAULT_SOURCE_IP_ADDRESS)
            .status(DEFAULT_STATUS)
            .terminationReason(DEFAULT_TERMINATION_REASON)
            .commandsExecuted(DEFAULT_COMMANDS_EXECUTED)
            .bytesTransferred(DEFAULT_BYTES_TRANSFERRED)
            .sessionId(DEFAULT_SESSION_ID)
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
        sessionLog.setInstance(instance);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        sessionLog.setUser(user);
        return sessionLog;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SessionLog createUpdatedEntity(EntityManager em) {
        SessionLog updatedSessionLog = new SessionLog()
            .sessionType(UPDATED_SESSION_TYPE)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .duration(UPDATED_DURATION)
            .sourceIpAddress(UPDATED_SOURCE_IP_ADDRESS)
            .status(UPDATED_STATUS)
            .terminationReason(UPDATED_TERMINATION_REASON)
            .commandsExecuted(UPDATED_COMMANDS_EXECUTED)
            .bytesTransferred(UPDATED_BYTES_TRANSFERRED)
            .sessionId(UPDATED_SESSION_ID)
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
        updatedSessionLog.setInstance(instance);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedSessionLog.setUser(user);
        return updatedSessionLog;
    }

    @BeforeEach
    void initTest() {
        sessionLog = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedSessionLog != null) {
            sessionLogRepository.delete(insertedSessionLog);
            sessionLogSearchRepository.delete(insertedSessionLog);
            insertedSessionLog = null;
        }
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    void getAllSessionLogs() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList
        restSessionLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sessionLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].sessionType").value(hasItem(DEFAULT_SESSION_TYPE)))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())))
            .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION)))
            .andExpect(jsonPath("$.[*].sourceIpAddress").value(hasItem(DEFAULT_SOURCE_IP_ADDRESS)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].terminationReason").value(hasItem(DEFAULT_TERMINATION_REASON)))
            .andExpect(jsonPath("$.[*].commandsExecuted").value(hasItem(DEFAULT_COMMANDS_EXECUTED)))
            .andExpect(jsonPath("$.[*].bytesTransferred").value(hasItem(DEFAULT_BYTES_TRANSFERRED.intValue())))
            .andExpect(jsonPath("$.[*].sessionId").value(hasItem(DEFAULT_SESSION_ID)))
            .andExpect(jsonPath("$.[*].metadata").value(hasItem(DEFAULT_METADATA)));
    }

    @Test
    @Transactional
    void getSessionLog() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get the sessionLog
        restSessionLogMockMvc
            .perform(get(ENTITY_API_URL_ID, sessionLog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sessionLog.getId().intValue()))
            .andExpect(jsonPath("$.sessionType").value(DEFAULT_SESSION_TYPE))
            .andExpect(jsonPath("$.startTime").value(DEFAULT_START_TIME.toString()))
            .andExpect(jsonPath("$.endTime").value(DEFAULT_END_TIME.toString()))
            .andExpect(jsonPath("$.duration").value(DEFAULT_DURATION))
            .andExpect(jsonPath("$.sourceIpAddress").value(DEFAULT_SOURCE_IP_ADDRESS))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.terminationReason").value(DEFAULT_TERMINATION_REASON))
            .andExpect(jsonPath("$.commandsExecuted").value(DEFAULT_COMMANDS_EXECUTED))
            .andExpect(jsonPath("$.bytesTransferred").value(DEFAULT_BYTES_TRANSFERRED.intValue()))
            .andExpect(jsonPath("$.sessionId").value(DEFAULT_SESSION_ID))
            .andExpect(jsonPath("$.metadata").value(DEFAULT_METADATA));
    }

    @Test
    @Transactional
    void getSessionLogsByIdFiltering() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        Long id = sessionLog.getId();

        defaultSessionLogFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultSessionLogFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultSessionLogFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSessionLogsBySessionTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where sessionType equals to
        defaultSessionLogFiltering("sessionType.equals=" + DEFAULT_SESSION_TYPE, "sessionType.equals=" + UPDATED_SESSION_TYPE);
    }

    @Test
    @Transactional
    void getAllSessionLogsBySessionTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where sessionType in
        defaultSessionLogFiltering(
            "sessionType.in=" + DEFAULT_SESSION_TYPE + "," + UPDATED_SESSION_TYPE,
            "sessionType.in=" + UPDATED_SESSION_TYPE
        );
    }

    @Test
    @Transactional
    void getAllSessionLogsBySessionTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where sessionType is not null
        defaultSessionLogFiltering("sessionType.specified=true", "sessionType.specified=false");
    }

    @Test
    @Transactional
    void getAllSessionLogsBySessionTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where sessionType contains
        defaultSessionLogFiltering("sessionType.contains=" + DEFAULT_SESSION_TYPE, "sessionType.contains=" + UPDATED_SESSION_TYPE);
    }

    @Test
    @Transactional
    void getAllSessionLogsBySessionTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where sessionType does not contain
        defaultSessionLogFiltering(
            "sessionType.doesNotContain=" + UPDATED_SESSION_TYPE,
            "sessionType.doesNotContain=" + DEFAULT_SESSION_TYPE
        );
    }

    @Test
    @Transactional
    void getAllSessionLogsByStartTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where startTime equals to
        defaultSessionLogFiltering("startTime.equals=" + DEFAULT_START_TIME, "startTime.equals=" + UPDATED_START_TIME);
    }

    @Test
    @Transactional
    void getAllSessionLogsByStartTimeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where startTime in
        defaultSessionLogFiltering("startTime.in=" + DEFAULT_START_TIME + "," + UPDATED_START_TIME, "startTime.in=" + UPDATED_START_TIME);
    }

    @Test
    @Transactional
    void getAllSessionLogsByStartTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where startTime is not null
        defaultSessionLogFiltering("startTime.specified=true", "startTime.specified=false");
    }

    @Test
    @Transactional
    void getAllSessionLogsByEndTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where endTime equals to
        defaultSessionLogFiltering("endTime.equals=" + DEFAULT_END_TIME, "endTime.equals=" + UPDATED_END_TIME);
    }

    @Test
    @Transactional
    void getAllSessionLogsByEndTimeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where endTime in
        defaultSessionLogFiltering("endTime.in=" + DEFAULT_END_TIME + "," + UPDATED_END_TIME, "endTime.in=" + UPDATED_END_TIME);
    }

    @Test
    @Transactional
    void getAllSessionLogsByEndTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where endTime is not null
        defaultSessionLogFiltering("endTime.specified=true", "endTime.specified=false");
    }

    @Test
    @Transactional
    void getAllSessionLogsByDurationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where duration equals to
        defaultSessionLogFiltering("duration.equals=" + DEFAULT_DURATION, "duration.equals=" + UPDATED_DURATION);
    }

    @Test
    @Transactional
    void getAllSessionLogsByDurationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where duration in
        defaultSessionLogFiltering("duration.in=" + DEFAULT_DURATION + "," + UPDATED_DURATION, "duration.in=" + UPDATED_DURATION);
    }

    @Test
    @Transactional
    void getAllSessionLogsByDurationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where duration is not null
        defaultSessionLogFiltering("duration.specified=true", "duration.specified=false");
    }

    @Test
    @Transactional
    void getAllSessionLogsByDurationIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where duration is greater than or equal to
        defaultSessionLogFiltering("duration.greaterThanOrEqual=" + DEFAULT_DURATION, "duration.greaterThanOrEqual=" + UPDATED_DURATION);
    }

    @Test
    @Transactional
    void getAllSessionLogsByDurationIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where duration is less than or equal to
        defaultSessionLogFiltering("duration.lessThanOrEqual=" + DEFAULT_DURATION, "duration.lessThanOrEqual=" + SMALLER_DURATION);
    }

    @Test
    @Transactional
    void getAllSessionLogsByDurationIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where duration is less than
        defaultSessionLogFiltering("duration.lessThan=" + UPDATED_DURATION, "duration.lessThan=" + DEFAULT_DURATION);
    }

    @Test
    @Transactional
    void getAllSessionLogsByDurationIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where duration is greater than
        defaultSessionLogFiltering("duration.greaterThan=" + SMALLER_DURATION, "duration.greaterThan=" + DEFAULT_DURATION);
    }

    @Test
    @Transactional
    void getAllSessionLogsBySourceIpAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where sourceIpAddress equals to
        defaultSessionLogFiltering(
            "sourceIpAddress.equals=" + DEFAULT_SOURCE_IP_ADDRESS,
            "sourceIpAddress.equals=" + UPDATED_SOURCE_IP_ADDRESS
        );
    }

    @Test
    @Transactional
    void getAllSessionLogsBySourceIpAddressIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where sourceIpAddress in
        defaultSessionLogFiltering(
            "sourceIpAddress.in=" + DEFAULT_SOURCE_IP_ADDRESS + "," + UPDATED_SOURCE_IP_ADDRESS,
            "sourceIpAddress.in=" + UPDATED_SOURCE_IP_ADDRESS
        );
    }

    @Test
    @Transactional
    void getAllSessionLogsBySourceIpAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where sourceIpAddress is not null
        defaultSessionLogFiltering("sourceIpAddress.specified=true", "sourceIpAddress.specified=false");
    }

    @Test
    @Transactional
    void getAllSessionLogsBySourceIpAddressContainsSomething() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where sourceIpAddress contains
        defaultSessionLogFiltering(
            "sourceIpAddress.contains=" + DEFAULT_SOURCE_IP_ADDRESS,
            "sourceIpAddress.contains=" + UPDATED_SOURCE_IP_ADDRESS
        );
    }

    @Test
    @Transactional
    void getAllSessionLogsBySourceIpAddressNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where sourceIpAddress does not contain
        defaultSessionLogFiltering(
            "sourceIpAddress.doesNotContain=" + UPDATED_SOURCE_IP_ADDRESS,
            "sourceIpAddress.doesNotContain=" + DEFAULT_SOURCE_IP_ADDRESS
        );
    }

    @Test
    @Transactional
    void getAllSessionLogsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where status equals to
        defaultSessionLogFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllSessionLogsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where status in
        defaultSessionLogFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllSessionLogsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where status is not null
        defaultSessionLogFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllSessionLogsByStatusContainsSomething() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where status contains
        defaultSessionLogFiltering("status.contains=" + DEFAULT_STATUS, "status.contains=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllSessionLogsByStatusNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where status does not contain
        defaultSessionLogFiltering("status.doesNotContain=" + UPDATED_STATUS, "status.doesNotContain=" + DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void getAllSessionLogsByTerminationReasonIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where terminationReason equals to
        defaultSessionLogFiltering(
            "terminationReason.equals=" + DEFAULT_TERMINATION_REASON,
            "terminationReason.equals=" + UPDATED_TERMINATION_REASON
        );
    }

    @Test
    @Transactional
    void getAllSessionLogsByTerminationReasonIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where terminationReason in
        defaultSessionLogFiltering(
            "terminationReason.in=" + DEFAULT_TERMINATION_REASON + "," + UPDATED_TERMINATION_REASON,
            "terminationReason.in=" + UPDATED_TERMINATION_REASON
        );
    }

    @Test
    @Transactional
    void getAllSessionLogsByTerminationReasonIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where terminationReason is not null
        defaultSessionLogFiltering("terminationReason.specified=true", "terminationReason.specified=false");
    }

    @Test
    @Transactional
    void getAllSessionLogsByTerminationReasonContainsSomething() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where terminationReason contains
        defaultSessionLogFiltering(
            "terminationReason.contains=" + DEFAULT_TERMINATION_REASON,
            "terminationReason.contains=" + UPDATED_TERMINATION_REASON
        );
    }

    @Test
    @Transactional
    void getAllSessionLogsByTerminationReasonNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where terminationReason does not contain
        defaultSessionLogFiltering(
            "terminationReason.doesNotContain=" + UPDATED_TERMINATION_REASON,
            "terminationReason.doesNotContain=" + DEFAULT_TERMINATION_REASON
        );
    }

    @Test
    @Transactional
    void getAllSessionLogsByCommandsExecutedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where commandsExecuted equals to
        defaultSessionLogFiltering(
            "commandsExecuted.equals=" + DEFAULT_COMMANDS_EXECUTED,
            "commandsExecuted.equals=" + UPDATED_COMMANDS_EXECUTED
        );
    }

    @Test
    @Transactional
    void getAllSessionLogsByCommandsExecutedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where commandsExecuted in
        defaultSessionLogFiltering(
            "commandsExecuted.in=" + DEFAULT_COMMANDS_EXECUTED + "," + UPDATED_COMMANDS_EXECUTED,
            "commandsExecuted.in=" + UPDATED_COMMANDS_EXECUTED
        );
    }

    @Test
    @Transactional
    void getAllSessionLogsByCommandsExecutedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where commandsExecuted is not null
        defaultSessionLogFiltering("commandsExecuted.specified=true", "commandsExecuted.specified=false");
    }

    @Test
    @Transactional
    void getAllSessionLogsByCommandsExecutedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where commandsExecuted is greater than or equal to
        defaultSessionLogFiltering(
            "commandsExecuted.greaterThanOrEqual=" + DEFAULT_COMMANDS_EXECUTED,
            "commandsExecuted.greaterThanOrEqual=" + UPDATED_COMMANDS_EXECUTED
        );
    }

    @Test
    @Transactional
    void getAllSessionLogsByCommandsExecutedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where commandsExecuted is less than or equal to
        defaultSessionLogFiltering(
            "commandsExecuted.lessThanOrEqual=" + DEFAULT_COMMANDS_EXECUTED,
            "commandsExecuted.lessThanOrEqual=" + SMALLER_COMMANDS_EXECUTED
        );
    }

    @Test
    @Transactional
    void getAllSessionLogsByCommandsExecutedIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where commandsExecuted is less than
        defaultSessionLogFiltering(
            "commandsExecuted.lessThan=" + UPDATED_COMMANDS_EXECUTED,
            "commandsExecuted.lessThan=" + DEFAULT_COMMANDS_EXECUTED
        );
    }

    @Test
    @Transactional
    void getAllSessionLogsByCommandsExecutedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where commandsExecuted is greater than
        defaultSessionLogFiltering(
            "commandsExecuted.greaterThan=" + SMALLER_COMMANDS_EXECUTED,
            "commandsExecuted.greaterThan=" + DEFAULT_COMMANDS_EXECUTED
        );
    }

    @Test
    @Transactional
    void getAllSessionLogsByBytesTransferredIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where bytesTransferred equals to
        defaultSessionLogFiltering(
            "bytesTransferred.equals=" + DEFAULT_BYTES_TRANSFERRED,
            "bytesTransferred.equals=" + UPDATED_BYTES_TRANSFERRED
        );
    }

    @Test
    @Transactional
    void getAllSessionLogsByBytesTransferredIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where bytesTransferred in
        defaultSessionLogFiltering(
            "bytesTransferred.in=" + DEFAULT_BYTES_TRANSFERRED + "," + UPDATED_BYTES_TRANSFERRED,
            "bytesTransferred.in=" + UPDATED_BYTES_TRANSFERRED
        );
    }

    @Test
    @Transactional
    void getAllSessionLogsByBytesTransferredIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where bytesTransferred is not null
        defaultSessionLogFiltering("bytesTransferred.specified=true", "bytesTransferred.specified=false");
    }

    @Test
    @Transactional
    void getAllSessionLogsByBytesTransferredIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where bytesTransferred is greater than or equal to
        defaultSessionLogFiltering(
            "bytesTransferred.greaterThanOrEqual=" + DEFAULT_BYTES_TRANSFERRED,
            "bytesTransferred.greaterThanOrEqual=" + UPDATED_BYTES_TRANSFERRED
        );
    }

    @Test
    @Transactional
    void getAllSessionLogsByBytesTransferredIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where bytesTransferred is less than or equal to
        defaultSessionLogFiltering(
            "bytesTransferred.lessThanOrEqual=" + DEFAULT_BYTES_TRANSFERRED,
            "bytesTransferred.lessThanOrEqual=" + SMALLER_BYTES_TRANSFERRED
        );
    }

    @Test
    @Transactional
    void getAllSessionLogsByBytesTransferredIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where bytesTransferred is less than
        defaultSessionLogFiltering(
            "bytesTransferred.lessThan=" + UPDATED_BYTES_TRANSFERRED,
            "bytesTransferred.lessThan=" + DEFAULT_BYTES_TRANSFERRED
        );
    }

    @Test
    @Transactional
    void getAllSessionLogsByBytesTransferredIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where bytesTransferred is greater than
        defaultSessionLogFiltering(
            "bytesTransferred.greaterThan=" + SMALLER_BYTES_TRANSFERRED,
            "bytesTransferred.greaterThan=" + DEFAULT_BYTES_TRANSFERRED
        );
    }

    @Test
    @Transactional
    void getAllSessionLogsBySessionIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where sessionId equals to
        defaultSessionLogFiltering("sessionId.equals=" + DEFAULT_SESSION_ID, "sessionId.equals=" + UPDATED_SESSION_ID);
    }

    @Test
    @Transactional
    void getAllSessionLogsBySessionIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where sessionId in
        defaultSessionLogFiltering("sessionId.in=" + DEFAULT_SESSION_ID + "," + UPDATED_SESSION_ID, "sessionId.in=" + UPDATED_SESSION_ID);
    }

    @Test
    @Transactional
    void getAllSessionLogsBySessionIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where sessionId is not null
        defaultSessionLogFiltering("sessionId.specified=true", "sessionId.specified=false");
    }

    @Test
    @Transactional
    void getAllSessionLogsBySessionIdContainsSomething() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where sessionId contains
        defaultSessionLogFiltering("sessionId.contains=" + DEFAULT_SESSION_ID, "sessionId.contains=" + UPDATED_SESSION_ID);
    }

    @Test
    @Transactional
    void getAllSessionLogsBySessionIdNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList where sessionId does not contain
        defaultSessionLogFiltering("sessionId.doesNotContain=" + UPDATED_SESSION_ID, "sessionId.doesNotContain=" + DEFAULT_SESSION_ID);
    }

    @Test
    @Transactional
    void getAllSessionLogsByInstanceIsEqualToSomething() throws Exception {
        Instance instance;
        if (TestUtil.findAll(em, Instance.class).isEmpty()) {
            sessionLogRepository.saveAndFlush(sessionLog);
            instance = InstanceResourceIT.createEntity(em);
        } else {
            instance = TestUtil.findAll(em, Instance.class).get(0);
        }
        em.persist(instance);
        em.flush();
        sessionLog.setInstance(instance);
        sessionLogRepository.saveAndFlush(sessionLog);
        Long instanceId = instance.getId();
        // Get all the sessionLogList where instance equals to instanceId
        defaultSessionLogShouldBeFound("instanceId.equals=" + instanceId);

        // Get all the sessionLogList where instance equals to (instanceId + 1)
        defaultSessionLogShouldNotBeFound("instanceId.equals=" + (instanceId + 1));
    }

    @Test
    @Transactional
    void getAllSessionLogsByAgentIsEqualToSomething() throws Exception {
        Agent agent;
        if (TestUtil.findAll(em, Agent.class).isEmpty()) {
            sessionLogRepository.saveAndFlush(sessionLog);
            agent = AgentResourceIT.createEntity();
        } else {
            agent = TestUtil.findAll(em, Agent.class).get(0);
        }
        em.persist(agent);
        em.flush();
        sessionLog.setAgent(agent);
        sessionLogRepository.saveAndFlush(sessionLog);
        Long agentId = agent.getId();
        // Get all the sessionLogList where agent equals to agentId
        defaultSessionLogShouldBeFound("agentId.equals=" + agentId);

        // Get all the sessionLogList where agent equals to (agentId + 1)
        defaultSessionLogShouldNotBeFound("agentId.equals=" + (agentId + 1));
    }

    @Test
    @Transactional
    void getAllSessionLogsByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            sessionLogRepository.saveAndFlush(sessionLog);
            user = UserResourceIT.createEntity();
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        sessionLog.setUser(user);
        sessionLogRepository.saveAndFlush(sessionLog);
        String userId = user.getId();
        // Get all the sessionLogList where user equals to userId
        defaultSessionLogShouldBeFound("userId.equals=" + userId);

        // Get all the sessionLogList where user equals to "invalid-id"
        defaultSessionLogShouldNotBeFound("userId.equals=" + "invalid-id");
    }

    private void defaultSessionLogFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultSessionLogShouldBeFound(shouldBeFound);
        defaultSessionLogShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSessionLogShouldBeFound(String filter) throws Exception {
        restSessionLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sessionLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].sessionType").value(hasItem(DEFAULT_SESSION_TYPE)))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())))
            .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION)))
            .andExpect(jsonPath("$.[*].sourceIpAddress").value(hasItem(DEFAULT_SOURCE_IP_ADDRESS)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].terminationReason").value(hasItem(DEFAULT_TERMINATION_REASON)))
            .andExpect(jsonPath("$.[*].commandsExecuted").value(hasItem(DEFAULT_COMMANDS_EXECUTED)))
            .andExpect(jsonPath("$.[*].bytesTransferred").value(hasItem(DEFAULT_BYTES_TRANSFERRED.intValue())))
            .andExpect(jsonPath("$.[*].sessionId").value(hasItem(DEFAULT_SESSION_ID)))
            .andExpect(jsonPath("$.[*].metadata").value(hasItem(DEFAULT_METADATA)));

        // Check, that the count call also returns 1
        restSessionLogMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSessionLogShouldNotBeFound(String filter) throws Exception {
        restSessionLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSessionLogMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSessionLog() throws Exception {
        // Get the sessionLog
        restSessionLogMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void searchSessionLog() throws Exception {
        // Initialize the database
        insertedSessionLog = sessionLogRepository.saveAndFlush(sessionLog);
        sessionLogSearchRepository.save(sessionLog);

        // Search the sessionLog
        restSessionLogMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + sessionLog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sessionLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].sessionType").value(hasItem(DEFAULT_SESSION_TYPE)))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())))
            .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION)))
            .andExpect(jsonPath("$.[*].sourceIpAddress").value(hasItem(DEFAULT_SOURCE_IP_ADDRESS)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].terminationReason").value(hasItem(DEFAULT_TERMINATION_REASON)))
            .andExpect(jsonPath("$.[*].commandsExecuted").value(hasItem(DEFAULT_COMMANDS_EXECUTED)))
            .andExpect(jsonPath("$.[*].bytesTransferred").value(hasItem(DEFAULT_BYTES_TRANSFERRED.intValue())))
            .andExpect(jsonPath("$.[*].sessionId").value(hasItem(DEFAULT_SESSION_ID)))
            .andExpect(jsonPath("$.[*].metadata").value(hasItem(DEFAULT_METADATA.toString())));
    }

    protected long getRepositoryCount() {
        return sessionLogRepository.count();
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

    protected SessionLog getPersistedSessionLog(SessionLog sessionLog) {
        return sessionLogRepository.findById(sessionLog.getId()).orElseThrow();
    }

    protected void assertPersistedSessionLogToMatchAllProperties(SessionLog expectedSessionLog) {
        assertSessionLogAllPropertiesEquals(expectedSessionLog, getPersistedSessionLog(expectedSessionLog));
    }

    protected void assertPersistedSessionLogToMatchUpdatableProperties(SessionLog expectedSessionLog) {
        assertSessionLogAllUpdatablePropertiesEquals(expectedSessionLog, getPersistedSessionLog(expectedSessionLog));
    }
}
