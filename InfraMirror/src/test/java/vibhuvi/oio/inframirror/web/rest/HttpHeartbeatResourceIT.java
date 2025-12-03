package vibhuvi.oio.inframirror.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static vibhuvi.oio.inframirror.domain.HttpHeartbeatAsserts.*;

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
import vibhuvi.oio.inframirror.domain.HttpHeartbeat;
import vibhuvi.oio.inframirror.domain.HttpMonitor;
import vibhuvi.oio.inframirror.repository.HttpHeartbeatRepository;
import vibhuvi.oio.inframirror.repository.search.HttpHeartbeatSearchRepository;
import vibhuvi.oio.inframirror.service.mapper.HttpHeartbeatMapper;

/**
 * Integration tests for the {@link HttpHeartbeatResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class HttpHeartbeatResourceIT {

    private static final Instant DEFAULT_EXECUTED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EXECUTED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_SUCCESS = false;
    private static final Boolean UPDATED_SUCCESS = true;

    private static final Integer DEFAULT_RESPONSE_TIME_MS = 1;
    private static final Integer UPDATED_RESPONSE_TIME_MS = 2;
    private static final Integer SMALLER_RESPONSE_TIME_MS = 1 - 1;

    private static final Integer DEFAULT_RESPONSE_SIZE_BYTES = 1;
    private static final Integer UPDATED_RESPONSE_SIZE_BYTES = 2;
    private static final Integer SMALLER_RESPONSE_SIZE_BYTES = 1 - 1;

    private static final Integer DEFAULT_RESPONSE_STATUS_CODE = 1;
    private static final Integer UPDATED_RESPONSE_STATUS_CODE = 2;
    private static final Integer SMALLER_RESPONSE_STATUS_CODE = 1 - 1;

    private static final String DEFAULT_RESPONSE_CONTENT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_RESPONSE_CONTENT_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_RESPONSE_SERVER = "AAAAAAAAAA";
    private static final String UPDATED_RESPONSE_SERVER = "BBBBBBBBBB";

    private static final String DEFAULT_RESPONSE_CACHE_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_RESPONSE_CACHE_STATUS = "BBBBBBBBBB";

    private static final Integer DEFAULT_DNS_LOOKUP_MS = 1;
    private static final Integer UPDATED_DNS_LOOKUP_MS = 2;
    private static final Integer SMALLER_DNS_LOOKUP_MS = 1 - 1;

    private static final Integer DEFAULT_TCP_CONNECT_MS = 1;
    private static final Integer UPDATED_TCP_CONNECT_MS = 2;
    private static final Integer SMALLER_TCP_CONNECT_MS = 1 - 1;

    private static final Integer DEFAULT_TLS_HANDSHAKE_MS = 1;
    private static final Integer UPDATED_TLS_HANDSHAKE_MS = 2;
    private static final Integer SMALLER_TLS_HANDSHAKE_MS = 1 - 1;

    private static final Integer DEFAULT_TIME_TO_FIRST_BYTE_MS = 1;
    private static final Integer UPDATED_TIME_TO_FIRST_BYTE_MS = 2;
    private static final Integer SMALLER_TIME_TO_FIRST_BYTE_MS = 1 - 1;

    private static final Integer DEFAULT_WARNING_THRESHOLD_MS = 1;
    private static final Integer UPDATED_WARNING_THRESHOLD_MS = 2;
    private static final Integer SMALLER_WARNING_THRESHOLD_MS = 1 - 1;

    private static final Integer DEFAULT_CRITICAL_THRESHOLD_MS = 1;
    private static final Integer UPDATED_CRITICAL_THRESHOLD_MS = 2;
    private static final Integer SMALLER_CRITICAL_THRESHOLD_MS = 1 - 1;

    private static final String DEFAULT_ERROR_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_ERROR_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_ERROR_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_ERROR_MESSAGE = "BBBBBBBBBB";

    private static final String DEFAULT_RAW_REQUEST_HEADERS = "AAAAAAAAAA";
    private static final String UPDATED_RAW_REQUEST_HEADERS = "BBBBBBBBBB";

    private static final String DEFAULT_RAW_RESPONSE_HEADERS = "AAAAAAAAAA";
    private static final String UPDATED_RAW_RESPONSE_HEADERS = "BBBBBBBBBB";

    private static final String DEFAULT_RAW_RESPONSE_BODY = "AAAAAAAAAA";
    private static final String UPDATED_RAW_RESPONSE_BODY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/http-heartbeats";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/http-heartbeats/_search";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private HttpHeartbeatRepository httpHeartbeatRepository;

    @Autowired
    private HttpHeartbeatMapper httpHeartbeatMapper;

    @Autowired
    private HttpHeartbeatSearchRepository httpHeartbeatSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restHttpHeartbeatMockMvc;

    private HttpHeartbeat httpHeartbeat;

    private HttpHeartbeat insertedHttpHeartbeat;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HttpHeartbeat createEntity() {
        return new HttpHeartbeat()
            .executedAt(DEFAULT_EXECUTED_AT)
            .success(DEFAULT_SUCCESS)
            .responseTimeMs(DEFAULT_RESPONSE_TIME_MS)
            .responseSizeBytes(DEFAULT_RESPONSE_SIZE_BYTES)
            .responseStatusCode(DEFAULT_RESPONSE_STATUS_CODE)
            .responseContentType(DEFAULT_RESPONSE_CONTENT_TYPE)
            .responseServer(DEFAULT_RESPONSE_SERVER)
            .responseCacheStatus(DEFAULT_RESPONSE_CACHE_STATUS)
            .dnsLookupMs(DEFAULT_DNS_LOOKUP_MS)
            .tcpConnectMs(DEFAULT_TCP_CONNECT_MS)
            .tlsHandshakeMs(DEFAULT_TLS_HANDSHAKE_MS)
            .timeToFirstByteMs(DEFAULT_TIME_TO_FIRST_BYTE_MS)
            .warningThresholdMs(DEFAULT_WARNING_THRESHOLD_MS)
            .criticalThresholdMs(DEFAULT_CRITICAL_THRESHOLD_MS)
            .errorType(DEFAULT_ERROR_TYPE)
            .errorMessage(DEFAULT_ERROR_MESSAGE)
            .rawRequestHeaders(DEFAULT_RAW_REQUEST_HEADERS)
            .rawResponseHeaders(DEFAULT_RAW_RESPONSE_HEADERS)
            .rawResponseBody(DEFAULT_RAW_RESPONSE_BODY);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HttpHeartbeat createUpdatedEntity() {
        return new HttpHeartbeat()
            .executedAt(UPDATED_EXECUTED_AT)
            .success(UPDATED_SUCCESS)
            .responseTimeMs(UPDATED_RESPONSE_TIME_MS)
            .responseSizeBytes(UPDATED_RESPONSE_SIZE_BYTES)
            .responseStatusCode(UPDATED_RESPONSE_STATUS_CODE)
            .responseContentType(UPDATED_RESPONSE_CONTENT_TYPE)
            .responseServer(UPDATED_RESPONSE_SERVER)
            .responseCacheStatus(UPDATED_RESPONSE_CACHE_STATUS)
            .dnsLookupMs(UPDATED_DNS_LOOKUP_MS)
            .tcpConnectMs(UPDATED_TCP_CONNECT_MS)
            .tlsHandshakeMs(UPDATED_TLS_HANDSHAKE_MS)
            .timeToFirstByteMs(UPDATED_TIME_TO_FIRST_BYTE_MS)
            .warningThresholdMs(UPDATED_WARNING_THRESHOLD_MS)
            .criticalThresholdMs(UPDATED_CRITICAL_THRESHOLD_MS)
            .errorType(UPDATED_ERROR_TYPE)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .rawRequestHeaders(UPDATED_RAW_REQUEST_HEADERS)
            .rawResponseHeaders(UPDATED_RAW_RESPONSE_HEADERS)
            .rawResponseBody(UPDATED_RAW_RESPONSE_BODY);
    }

    @BeforeEach
    void initTest() {
        httpHeartbeat = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedHttpHeartbeat != null) {
            httpHeartbeatRepository.delete(insertedHttpHeartbeat);
            httpHeartbeatSearchRepository.delete(insertedHttpHeartbeat);
            insertedHttpHeartbeat = null;
        }
    }

    @Test
    @Transactional
    void getAllHttpHeartbeats() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList
        restHttpHeartbeatMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(httpHeartbeat.getId().intValue())))
            .andExpect(jsonPath("$.[*].executedAt").value(hasItem(DEFAULT_EXECUTED_AT.toString())))
            .andExpect(jsonPath("$.[*].success").value(hasItem(DEFAULT_SUCCESS)))
            .andExpect(jsonPath("$.[*].responseTimeMs").value(hasItem(DEFAULT_RESPONSE_TIME_MS)))
            .andExpect(jsonPath("$.[*].responseSizeBytes").value(hasItem(DEFAULT_RESPONSE_SIZE_BYTES)))
            .andExpect(jsonPath("$.[*].responseStatusCode").value(hasItem(DEFAULT_RESPONSE_STATUS_CODE)))
            .andExpect(jsonPath("$.[*].responseContentType").value(hasItem(DEFAULT_RESPONSE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].responseServer").value(hasItem(DEFAULT_RESPONSE_SERVER)))
            .andExpect(jsonPath("$.[*].responseCacheStatus").value(hasItem(DEFAULT_RESPONSE_CACHE_STATUS)))
            .andExpect(jsonPath("$.[*].dnsLookupMs").value(hasItem(DEFAULT_DNS_LOOKUP_MS)))
            .andExpect(jsonPath("$.[*].tcpConnectMs").value(hasItem(DEFAULT_TCP_CONNECT_MS)))
            .andExpect(jsonPath("$.[*].tlsHandshakeMs").value(hasItem(DEFAULT_TLS_HANDSHAKE_MS)))
            .andExpect(jsonPath("$.[*].timeToFirstByteMs").value(hasItem(DEFAULT_TIME_TO_FIRST_BYTE_MS)))
            .andExpect(jsonPath("$.[*].warningThresholdMs").value(hasItem(DEFAULT_WARNING_THRESHOLD_MS)))
            .andExpect(jsonPath("$.[*].criticalThresholdMs").value(hasItem(DEFAULT_CRITICAL_THRESHOLD_MS)))
            .andExpect(jsonPath("$.[*].errorType").value(hasItem(DEFAULT_ERROR_TYPE)))
            .andExpect(jsonPath("$.[*].errorMessage").value(hasItem(DEFAULT_ERROR_MESSAGE)))
            .andExpect(jsonPath("$.[*].rawRequestHeaders").value(hasItem(DEFAULT_RAW_REQUEST_HEADERS)))
            .andExpect(jsonPath("$.[*].rawResponseHeaders").value(hasItem(DEFAULT_RAW_RESPONSE_HEADERS)))
            .andExpect(jsonPath("$.[*].rawResponseBody").value(hasItem(DEFAULT_RAW_RESPONSE_BODY)));
    }

    @Test
    @Transactional
    void getHttpHeartbeat() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get the httpHeartbeat
        restHttpHeartbeatMockMvc
            .perform(get(ENTITY_API_URL_ID, httpHeartbeat.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(httpHeartbeat.getId().intValue()))
            .andExpect(jsonPath("$.executedAt").value(DEFAULT_EXECUTED_AT.toString()))
            .andExpect(jsonPath("$.success").value(DEFAULT_SUCCESS))
            .andExpect(jsonPath("$.responseTimeMs").value(DEFAULT_RESPONSE_TIME_MS))
            .andExpect(jsonPath("$.responseSizeBytes").value(DEFAULT_RESPONSE_SIZE_BYTES))
            .andExpect(jsonPath("$.responseStatusCode").value(DEFAULT_RESPONSE_STATUS_CODE))
            .andExpect(jsonPath("$.responseContentType").value(DEFAULT_RESPONSE_CONTENT_TYPE))
            .andExpect(jsonPath("$.responseServer").value(DEFAULT_RESPONSE_SERVER))
            .andExpect(jsonPath("$.responseCacheStatus").value(DEFAULT_RESPONSE_CACHE_STATUS))
            .andExpect(jsonPath("$.dnsLookupMs").value(DEFAULT_DNS_LOOKUP_MS))
            .andExpect(jsonPath("$.tcpConnectMs").value(DEFAULT_TCP_CONNECT_MS))
            .andExpect(jsonPath("$.tlsHandshakeMs").value(DEFAULT_TLS_HANDSHAKE_MS))
            .andExpect(jsonPath("$.timeToFirstByteMs").value(DEFAULT_TIME_TO_FIRST_BYTE_MS))
            .andExpect(jsonPath("$.warningThresholdMs").value(DEFAULT_WARNING_THRESHOLD_MS))
            .andExpect(jsonPath("$.criticalThresholdMs").value(DEFAULT_CRITICAL_THRESHOLD_MS))
            .andExpect(jsonPath("$.errorType").value(DEFAULT_ERROR_TYPE))
            .andExpect(jsonPath("$.errorMessage").value(DEFAULT_ERROR_MESSAGE))
            .andExpect(jsonPath("$.rawRequestHeaders").value(DEFAULT_RAW_REQUEST_HEADERS))
            .andExpect(jsonPath("$.rawResponseHeaders").value(DEFAULT_RAW_RESPONSE_HEADERS))
            .andExpect(jsonPath("$.rawResponseBody").value(DEFAULT_RAW_RESPONSE_BODY));
    }

    @Test
    @Transactional
    void getHttpHeartbeatsByIdFiltering() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        Long id = httpHeartbeat.getId();

        defaultHttpHeartbeatFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultHttpHeartbeatFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultHttpHeartbeatFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByExecutedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where executedAt equals to
        defaultHttpHeartbeatFiltering("executedAt.equals=" + DEFAULT_EXECUTED_AT, "executedAt.equals=" + UPDATED_EXECUTED_AT);
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByExecutedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where executedAt in
        defaultHttpHeartbeatFiltering(
            "executedAt.in=" + DEFAULT_EXECUTED_AT + "," + UPDATED_EXECUTED_AT,
            "executedAt.in=" + UPDATED_EXECUTED_AT
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByExecutedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where executedAt is not null
        defaultHttpHeartbeatFiltering("executedAt.specified=true", "executedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsBySuccessIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where success equals to
        defaultHttpHeartbeatFiltering("success.equals=" + DEFAULT_SUCCESS, "success.equals=" + UPDATED_SUCCESS);
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsBySuccessIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where success in
        defaultHttpHeartbeatFiltering("success.in=" + DEFAULT_SUCCESS + "," + UPDATED_SUCCESS, "success.in=" + UPDATED_SUCCESS);
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsBySuccessIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where success is not null
        defaultHttpHeartbeatFiltering("success.specified=true", "success.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByResponseTimeMsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where responseTimeMs equals to
        defaultHttpHeartbeatFiltering(
            "responseTimeMs.equals=" + DEFAULT_RESPONSE_TIME_MS,
            "responseTimeMs.equals=" + UPDATED_RESPONSE_TIME_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByResponseTimeMsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where responseTimeMs in
        defaultHttpHeartbeatFiltering(
            "responseTimeMs.in=" + DEFAULT_RESPONSE_TIME_MS + "," + UPDATED_RESPONSE_TIME_MS,
            "responseTimeMs.in=" + UPDATED_RESPONSE_TIME_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByResponseTimeMsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where responseTimeMs is not null
        defaultHttpHeartbeatFiltering("responseTimeMs.specified=true", "responseTimeMs.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByResponseTimeMsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where responseTimeMs is greater than or equal to
        defaultHttpHeartbeatFiltering(
            "responseTimeMs.greaterThanOrEqual=" + DEFAULT_RESPONSE_TIME_MS,
            "responseTimeMs.greaterThanOrEqual=" + UPDATED_RESPONSE_TIME_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByResponseTimeMsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where responseTimeMs is less than or equal to
        defaultHttpHeartbeatFiltering(
            "responseTimeMs.lessThanOrEqual=" + DEFAULT_RESPONSE_TIME_MS,
            "responseTimeMs.lessThanOrEqual=" + SMALLER_RESPONSE_TIME_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByResponseTimeMsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where responseTimeMs is less than
        defaultHttpHeartbeatFiltering(
            "responseTimeMs.lessThan=" + UPDATED_RESPONSE_TIME_MS,
            "responseTimeMs.lessThan=" + DEFAULT_RESPONSE_TIME_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByResponseTimeMsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where responseTimeMs is greater than
        defaultHttpHeartbeatFiltering(
            "responseTimeMs.greaterThan=" + SMALLER_RESPONSE_TIME_MS,
            "responseTimeMs.greaterThan=" + DEFAULT_RESPONSE_TIME_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByResponseSizeBytesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where responseSizeBytes equals to
        defaultHttpHeartbeatFiltering(
            "responseSizeBytes.equals=" + DEFAULT_RESPONSE_SIZE_BYTES,
            "responseSizeBytes.equals=" + UPDATED_RESPONSE_SIZE_BYTES
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByResponseSizeBytesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where responseSizeBytes in
        defaultHttpHeartbeatFiltering(
            "responseSizeBytes.in=" + DEFAULT_RESPONSE_SIZE_BYTES + "," + UPDATED_RESPONSE_SIZE_BYTES,
            "responseSizeBytes.in=" + UPDATED_RESPONSE_SIZE_BYTES
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByResponseSizeBytesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where responseSizeBytes is not null
        defaultHttpHeartbeatFiltering("responseSizeBytes.specified=true", "responseSizeBytes.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByResponseSizeBytesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where responseSizeBytes is greater than or equal to
        defaultHttpHeartbeatFiltering(
            "responseSizeBytes.greaterThanOrEqual=" + DEFAULT_RESPONSE_SIZE_BYTES,
            "responseSizeBytes.greaterThanOrEqual=" + UPDATED_RESPONSE_SIZE_BYTES
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByResponseSizeBytesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where responseSizeBytes is less than or equal to
        defaultHttpHeartbeatFiltering(
            "responseSizeBytes.lessThanOrEqual=" + DEFAULT_RESPONSE_SIZE_BYTES,
            "responseSizeBytes.lessThanOrEqual=" + SMALLER_RESPONSE_SIZE_BYTES
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByResponseSizeBytesIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where responseSizeBytes is less than
        defaultHttpHeartbeatFiltering(
            "responseSizeBytes.lessThan=" + UPDATED_RESPONSE_SIZE_BYTES,
            "responseSizeBytes.lessThan=" + DEFAULT_RESPONSE_SIZE_BYTES
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByResponseSizeBytesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where responseSizeBytes is greater than
        defaultHttpHeartbeatFiltering(
            "responseSizeBytes.greaterThan=" + SMALLER_RESPONSE_SIZE_BYTES,
            "responseSizeBytes.greaterThan=" + DEFAULT_RESPONSE_SIZE_BYTES
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByResponseStatusCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where responseStatusCode equals to
        defaultHttpHeartbeatFiltering(
            "responseStatusCode.equals=" + DEFAULT_RESPONSE_STATUS_CODE,
            "responseStatusCode.equals=" + UPDATED_RESPONSE_STATUS_CODE
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByResponseStatusCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where responseStatusCode in
        defaultHttpHeartbeatFiltering(
            "responseStatusCode.in=" + DEFAULT_RESPONSE_STATUS_CODE + "," + UPDATED_RESPONSE_STATUS_CODE,
            "responseStatusCode.in=" + UPDATED_RESPONSE_STATUS_CODE
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByResponseStatusCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where responseStatusCode is not null
        defaultHttpHeartbeatFiltering("responseStatusCode.specified=true", "responseStatusCode.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByResponseStatusCodeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where responseStatusCode is greater than or equal to
        defaultHttpHeartbeatFiltering(
            "responseStatusCode.greaterThanOrEqual=" + DEFAULT_RESPONSE_STATUS_CODE,
            "responseStatusCode.greaterThanOrEqual=" + UPDATED_RESPONSE_STATUS_CODE
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByResponseStatusCodeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where responseStatusCode is less than or equal to
        defaultHttpHeartbeatFiltering(
            "responseStatusCode.lessThanOrEqual=" + DEFAULT_RESPONSE_STATUS_CODE,
            "responseStatusCode.lessThanOrEqual=" + SMALLER_RESPONSE_STATUS_CODE
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByResponseStatusCodeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where responseStatusCode is less than
        defaultHttpHeartbeatFiltering(
            "responseStatusCode.lessThan=" + UPDATED_RESPONSE_STATUS_CODE,
            "responseStatusCode.lessThan=" + DEFAULT_RESPONSE_STATUS_CODE
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByResponseStatusCodeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where responseStatusCode is greater than
        defaultHttpHeartbeatFiltering(
            "responseStatusCode.greaterThan=" + SMALLER_RESPONSE_STATUS_CODE,
            "responseStatusCode.greaterThan=" + DEFAULT_RESPONSE_STATUS_CODE
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByResponseContentTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where responseContentType equals to
        defaultHttpHeartbeatFiltering(
            "responseContentType.equals=" + DEFAULT_RESPONSE_CONTENT_TYPE,
            "responseContentType.equals=" + UPDATED_RESPONSE_CONTENT_TYPE
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByResponseContentTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where responseContentType in
        defaultHttpHeartbeatFiltering(
            "responseContentType.in=" + DEFAULT_RESPONSE_CONTENT_TYPE + "," + UPDATED_RESPONSE_CONTENT_TYPE,
            "responseContentType.in=" + UPDATED_RESPONSE_CONTENT_TYPE
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByResponseContentTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where responseContentType is not null
        defaultHttpHeartbeatFiltering("responseContentType.specified=true", "responseContentType.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByResponseContentTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where responseContentType contains
        defaultHttpHeartbeatFiltering(
            "responseContentType.contains=" + DEFAULT_RESPONSE_CONTENT_TYPE,
            "responseContentType.contains=" + UPDATED_RESPONSE_CONTENT_TYPE
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByResponseContentTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where responseContentType does not contain
        defaultHttpHeartbeatFiltering(
            "responseContentType.doesNotContain=" + UPDATED_RESPONSE_CONTENT_TYPE,
            "responseContentType.doesNotContain=" + DEFAULT_RESPONSE_CONTENT_TYPE
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByResponseServerIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where responseServer equals to
        defaultHttpHeartbeatFiltering(
            "responseServer.equals=" + DEFAULT_RESPONSE_SERVER,
            "responseServer.equals=" + UPDATED_RESPONSE_SERVER
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByResponseServerIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where responseServer in
        defaultHttpHeartbeatFiltering(
            "responseServer.in=" + DEFAULT_RESPONSE_SERVER + "," + UPDATED_RESPONSE_SERVER,
            "responseServer.in=" + UPDATED_RESPONSE_SERVER
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByResponseServerIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where responseServer is not null
        defaultHttpHeartbeatFiltering("responseServer.specified=true", "responseServer.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByResponseServerContainsSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where responseServer contains
        defaultHttpHeartbeatFiltering(
            "responseServer.contains=" + DEFAULT_RESPONSE_SERVER,
            "responseServer.contains=" + UPDATED_RESPONSE_SERVER
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByResponseServerNotContainsSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where responseServer does not contain
        defaultHttpHeartbeatFiltering(
            "responseServer.doesNotContain=" + UPDATED_RESPONSE_SERVER,
            "responseServer.doesNotContain=" + DEFAULT_RESPONSE_SERVER
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByResponseCacheStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where responseCacheStatus equals to
        defaultHttpHeartbeatFiltering(
            "responseCacheStatus.equals=" + DEFAULT_RESPONSE_CACHE_STATUS,
            "responseCacheStatus.equals=" + UPDATED_RESPONSE_CACHE_STATUS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByResponseCacheStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where responseCacheStatus in
        defaultHttpHeartbeatFiltering(
            "responseCacheStatus.in=" + DEFAULT_RESPONSE_CACHE_STATUS + "," + UPDATED_RESPONSE_CACHE_STATUS,
            "responseCacheStatus.in=" + UPDATED_RESPONSE_CACHE_STATUS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByResponseCacheStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where responseCacheStatus is not null
        defaultHttpHeartbeatFiltering("responseCacheStatus.specified=true", "responseCacheStatus.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByResponseCacheStatusContainsSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where responseCacheStatus contains
        defaultHttpHeartbeatFiltering(
            "responseCacheStatus.contains=" + DEFAULT_RESPONSE_CACHE_STATUS,
            "responseCacheStatus.contains=" + UPDATED_RESPONSE_CACHE_STATUS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByResponseCacheStatusNotContainsSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where responseCacheStatus does not contain
        defaultHttpHeartbeatFiltering(
            "responseCacheStatus.doesNotContain=" + UPDATED_RESPONSE_CACHE_STATUS,
            "responseCacheStatus.doesNotContain=" + DEFAULT_RESPONSE_CACHE_STATUS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByDnsLookupMsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where dnsLookupMs equals to
        defaultHttpHeartbeatFiltering("dnsLookupMs.equals=" + DEFAULT_DNS_LOOKUP_MS, "dnsLookupMs.equals=" + UPDATED_DNS_LOOKUP_MS);
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByDnsLookupMsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where dnsLookupMs in
        defaultHttpHeartbeatFiltering(
            "dnsLookupMs.in=" + DEFAULT_DNS_LOOKUP_MS + "," + UPDATED_DNS_LOOKUP_MS,
            "dnsLookupMs.in=" + UPDATED_DNS_LOOKUP_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByDnsLookupMsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where dnsLookupMs is not null
        defaultHttpHeartbeatFiltering("dnsLookupMs.specified=true", "dnsLookupMs.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByDnsLookupMsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where dnsLookupMs is greater than or equal to
        defaultHttpHeartbeatFiltering(
            "dnsLookupMs.greaterThanOrEqual=" + DEFAULT_DNS_LOOKUP_MS,
            "dnsLookupMs.greaterThanOrEqual=" + UPDATED_DNS_LOOKUP_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByDnsLookupMsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where dnsLookupMs is less than or equal to
        defaultHttpHeartbeatFiltering(
            "dnsLookupMs.lessThanOrEqual=" + DEFAULT_DNS_LOOKUP_MS,
            "dnsLookupMs.lessThanOrEqual=" + SMALLER_DNS_LOOKUP_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByDnsLookupMsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where dnsLookupMs is less than
        defaultHttpHeartbeatFiltering("dnsLookupMs.lessThan=" + UPDATED_DNS_LOOKUP_MS, "dnsLookupMs.lessThan=" + DEFAULT_DNS_LOOKUP_MS);
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByDnsLookupMsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where dnsLookupMs is greater than
        defaultHttpHeartbeatFiltering(
            "dnsLookupMs.greaterThan=" + SMALLER_DNS_LOOKUP_MS,
            "dnsLookupMs.greaterThan=" + DEFAULT_DNS_LOOKUP_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByTcpConnectMsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where tcpConnectMs equals to
        defaultHttpHeartbeatFiltering("tcpConnectMs.equals=" + DEFAULT_TCP_CONNECT_MS, "tcpConnectMs.equals=" + UPDATED_TCP_CONNECT_MS);
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByTcpConnectMsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where tcpConnectMs in
        defaultHttpHeartbeatFiltering(
            "tcpConnectMs.in=" + DEFAULT_TCP_CONNECT_MS + "," + UPDATED_TCP_CONNECT_MS,
            "tcpConnectMs.in=" + UPDATED_TCP_CONNECT_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByTcpConnectMsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where tcpConnectMs is not null
        defaultHttpHeartbeatFiltering("tcpConnectMs.specified=true", "tcpConnectMs.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByTcpConnectMsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where tcpConnectMs is greater than or equal to
        defaultHttpHeartbeatFiltering(
            "tcpConnectMs.greaterThanOrEqual=" + DEFAULT_TCP_CONNECT_MS,
            "tcpConnectMs.greaterThanOrEqual=" + UPDATED_TCP_CONNECT_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByTcpConnectMsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where tcpConnectMs is less than or equal to
        defaultHttpHeartbeatFiltering(
            "tcpConnectMs.lessThanOrEqual=" + DEFAULT_TCP_CONNECT_MS,
            "tcpConnectMs.lessThanOrEqual=" + SMALLER_TCP_CONNECT_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByTcpConnectMsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where tcpConnectMs is less than
        defaultHttpHeartbeatFiltering("tcpConnectMs.lessThan=" + UPDATED_TCP_CONNECT_MS, "tcpConnectMs.lessThan=" + DEFAULT_TCP_CONNECT_MS);
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByTcpConnectMsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where tcpConnectMs is greater than
        defaultHttpHeartbeatFiltering(
            "tcpConnectMs.greaterThan=" + SMALLER_TCP_CONNECT_MS,
            "tcpConnectMs.greaterThan=" + DEFAULT_TCP_CONNECT_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByTlsHandshakeMsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where tlsHandshakeMs equals to
        defaultHttpHeartbeatFiltering(
            "tlsHandshakeMs.equals=" + DEFAULT_TLS_HANDSHAKE_MS,
            "tlsHandshakeMs.equals=" + UPDATED_TLS_HANDSHAKE_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByTlsHandshakeMsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where tlsHandshakeMs in
        defaultHttpHeartbeatFiltering(
            "tlsHandshakeMs.in=" + DEFAULT_TLS_HANDSHAKE_MS + "," + UPDATED_TLS_HANDSHAKE_MS,
            "tlsHandshakeMs.in=" + UPDATED_TLS_HANDSHAKE_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByTlsHandshakeMsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where tlsHandshakeMs is not null
        defaultHttpHeartbeatFiltering("tlsHandshakeMs.specified=true", "tlsHandshakeMs.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByTlsHandshakeMsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where tlsHandshakeMs is greater than or equal to
        defaultHttpHeartbeatFiltering(
            "tlsHandshakeMs.greaterThanOrEqual=" + DEFAULT_TLS_HANDSHAKE_MS,
            "tlsHandshakeMs.greaterThanOrEqual=" + UPDATED_TLS_HANDSHAKE_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByTlsHandshakeMsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where tlsHandshakeMs is less than or equal to
        defaultHttpHeartbeatFiltering(
            "tlsHandshakeMs.lessThanOrEqual=" + DEFAULT_TLS_HANDSHAKE_MS,
            "tlsHandshakeMs.lessThanOrEqual=" + SMALLER_TLS_HANDSHAKE_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByTlsHandshakeMsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where tlsHandshakeMs is less than
        defaultHttpHeartbeatFiltering(
            "tlsHandshakeMs.lessThan=" + UPDATED_TLS_HANDSHAKE_MS,
            "tlsHandshakeMs.lessThan=" + DEFAULT_TLS_HANDSHAKE_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByTlsHandshakeMsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where tlsHandshakeMs is greater than
        defaultHttpHeartbeatFiltering(
            "tlsHandshakeMs.greaterThan=" + SMALLER_TLS_HANDSHAKE_MS,
            "tlsHandshakeMs.greaterThan=" + DEFAULT_TLS_HANDSHAKE_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByTimeToFirstByteMsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where timeToFirstByteMs equals to
        defaultHttpHeartbeatFiltering(
            "timeToFirstByteMs.equals=" + DEFAULT_TIME_TO_FIRST_BYTE_MS,
            "timeToFirstByteMs.equals=" + UPDATED_TIME_TO_FIRST_BYTE_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByTimeToFirstByteMsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where timeToFirstByteMs in
        defaultHttpHeartbeatFiltering(
            "timeToFirstByteMs.in=" + DEFAULT_TIME_TO_FIRST_BYTE_MS + "," + UPDATED_TIME_TO_FIRST_BYTE_MS,
            "timeToFirstByteMs.in=" + UPDATED_TIME_TO_FIRST_BYTE_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByTimeToFirstByteMsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where timeToFirstByteMs is not null
        defaultHttpHeartbeatFiltering("timeToFirstByteMs.specified=true", "timeToFirstByteMs.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByTimeToFirstByteMsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where timeToFirstByteMs is greater than or equal to
        defaultHttpHeartbeatFiltering(
            "timeToFirstByteMs.greaterThanOrEqual=" + DEFAULT_TIME_TO_FIRST_BYTE_MS,
            "timeToFirstByteMs.greaterThanOrEqual=" + UPDATED_TIME_TO_FIRST_BYTE_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByTimeToFirstByteMsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where timeToFirstByteMs is less than or equal to
        defaultHttpHeartbeatFiltering(
            "timeToFirstByteMs.lessThanOrEqual=" + DEFAULT_TIME_TO_FIRST_BYTE_MS,
            "timeToFirstByteMs.lessThanOrEqual=" + SMALLER_TIME_TO_FIRST_BYTE_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByTimeToFirstByteMsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where timeToFirstByteMs is less than
        defaultHttpHeartbeatFiltering(
            "timeToFirstByteMs.lessThan=" + UPDATED_TIME_TO_FIRST_BYTE_MS,
            "timeToFirstByteMs.lessThan=" + DEFAULT_TIME_TO_FIRST_BYTE_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByTimeToFirstByteMsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where timeToFirstByteMs is greater than
        defaultHttpHeartbeatFiltering(
            "timeToFirstByteMs.greaterThan=" + SMALLER_TIME_TO_FIRST_BYTE_MS,
            "timeToFirstByteMs.greaterThan=" + DEFAULT_TIME_TO_FIRST_BYTE_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByWarningThresholdMsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where warningThresholdMs equals to
        defaultHttpHeartbeatFiltering(
            "warningThresholdMs.equals=" + DEFAULT_WARNING_THRESHOLD_MS,
            "warningThresholdMs.equals=" + UPDATED_WARNING_THRESHOLD_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByWarningThresholdMsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where warningThresholdMs in
        defaultHttpHeartbeatFiltering(
            "warningThresholdMs.in=" + DEFAULT_WARNING_THRESHOLD_MS + "," + UPDATED_WARNING_THRESHOLD_MS,
            "warningThresholdMs.in=" + UPDATED_WARNING_THRESHOLD_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByWarningThresholdMsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where warningThresholdMs is not null
        defaultHttpHeartbeatFiltering("warningThresholdMs.specified=true", "warningThresholdMs.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByWarningThresholdMsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where warningThresholdMs is greater than or equal to
        defaultHttpHeartbeatFiltering(
            "warningThresholdMs.greaterThanOrEqual=" + DEFAULT_WARNING_THRESHOLD_MS,
            "warningThresholdMs.greaterThanOrEqual=" + UPDATED_WARNING_THRESHOLD_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByWarningThresholdMsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where warningThresholdMs is less than or equal to
        defaultHttpHeartbeatFiltering(
            "warningThresholdMs.lessThanOrEqual=" + DEFAULT_WARNING_THRESHOLD_MS,
            "warningThresholdMs.lessThanOrEqual=" + SMALLER_WARNING_THRESHOLD_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByWarningThresholdMsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where warningThresholdMs is less than
        defaultHttpHeartbeatFiltering(
            "warningThresholdMs.lessThan=" + UPDATED_WARNING_THRESHOLD_MS,
            "warningThresholdMs.lessThan=" + DEFAULT_WARNING_THRESHOLD_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByWarningThresholdMsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where warningThresholdMs is greater than
        defaultHttpHeartbeatFiltering(
            "warningThresholdMs.greaterThan=" + SMALLER_WARNING_THRESHOLD_MS,
            "warningThresholdMs.greaterThan=" + DEFAULT_WARNING_THRESHOLD_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByCriticalThresholdMsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where criticalThresholdMs equals to
        defaultHttpHeartbeatFiltering(
            "criticalThresholdMs.equals=" + DEFAULT_CRITICAL_THRESHOLD_MS,
            "criticalThresholdMs.equals=" + UPDATED_CRITICAL_THRESHOLD_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByCriticalThresholdMsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where criticalThresholdMs in
        defaultHttpHeartbeatFiltering(
            "criticalThresholdMs.in=" + DEFAULT_CRITICAL_THRESHOLD_MS + "," + UPDATED_CRITICAL_THRESHOLD_MS,
            "criticalThresholdMs.in=" + UPDATED_CRITICAL_THRESHOLD_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByCriticalThresholdMsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where criticalThresholdMs is not null
        defaultHttpHeartbeatFiltering("criticalThresholdMs.specified=true", "criticalThresholdMs.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByCriticalThresholdMsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where criticalThresholdMs is greater than or equal to
        defaultHttpHeartbeatFiltering(
            "criticalThresholdMs.greaterThanOrEqual=" + DEFAULT_CRITICAL_THRESHOLD_MS,
            "criticalThresholdMs.greaterThanOrEqual=" + UPDATED_CRITICAL_THRESHOLD_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByCriticalThresholdMsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where criticalThresholdMs is less than or equal to
        defaultHttpHeartbeatFiltering(
            "criticalThresholdMs.lessThanOrEqual=" + DEFAULT_CRITICAL_THRESHOLD_MS,
            "criticalThresholdMs.lessThanOrEqual=" + SMALLER_CRITICAL_THRESHOLD_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByCriticalThresholdMsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where criticalThresholdMs is less than
        defaultHttpHeartbeatFiltering(
            "criticalThresholdMs.lessThan=" + UPDATED_CRITICAL_THRESHOLD_MS,
            "criticalThresholdMs.lessThan=" + DEFAULT_CRITICAL_THRESHOLD_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByCriticalThresholdMsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where criticalThresholdMs is greater than
        defaultHttpHeartbeatFiltering(
            "criticalThresholdMs.greaterThan=" + SMALLER_CRITICAL_THRESHOLD_MS,
            "criticalThresholdMs.greaterThan=" + DEFAULT_CRITICAL_THRESHOLD_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByErrorTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where errorType equals to
        defaultHttpHeartbeatFiltering("errorType.equals=" + DEFAULT_ERROR_TYPE, "errorType.equals=" + UPDATED_ERROR_TYPE);
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByErrorTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where errorType in
        defaultHttpHeartbeatFiltering(
            "errorType.in=" + DEFAULT_ERROR_TYPE + "," + UPDATED_ERROR_TYPE,
            "errorType.in=" + UPDATED_ERROR_TYPE
        );
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByErrorTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where errorType is not null
        defaultHttpHeartbeatFiltering("errorType.specified=true", "errorType.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByErrorTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where errorType contains
        defaultHttpHeartbeatFiltering("errorType.contains=" + DEFAULT_ERROR_TYPE, "errorType.contains=" + UPDATED_ERROR_TYPE);
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByErrorTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        // Get all the httpHeartbeatList where errorType does not contain
        defaultHttpHeartbeatFiltering("errorType.doesNotContain=" + UPDATED_ERROR_TYPE, "errorType.doesNotContain=" + DEFAULT_ERROR_TYPE);
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByMonitorIsEqualToSomething() throws Exception {
        HttpMonitor monitor;
        if (TestUtil.findAll(em, HttpMonitor.class).isEmpty()) {
            httpHeartbeatRepository.saveAndFlush(httpHeartbeat);
            monitor = HttpMonitorResourceIT.createEntity();
        } else {
            monitor = TestUtil.findAll(em, HttpMonitor.class).get(0);
        }
        em.persist(monitor);
        em.flush();
        httpHeartbeat.setMonitor(monitor);
        httpHeartbeatRepository.saveAndFlush(httpHeartbeat);
        Long monitorId = monitor.getId();
        // Get all the httpHeartbeatList where monitor equals to monitorId
        defaultHttpHeartbeatShouldBeFound("monitorId.equals=" + monitorId);

        // Get all the httpHeartbeatList where monitor equals to (monitorId + 1)
        defaultHttpHeartbeatShouldNotBeFound("monitorId.equals=" + (monitorId + 1));
    }

    @Test
    @Transactional
    void getAllHttpHeartbeatsByAgentIsEqualToSomething() throws Exception {
        Agent agent;
        if (TestUtil.findAll(em, Agent.class).isEmpty()) {
            httpHeartbeatRepository.saveAndFlush(httpHeartbeat);
            agent = AgentResourceIT.createEntity();
        } else {
            agent = TestUtil.findAll(em, Agent.class).get(0);
        }
        em.persist(agent);
        em.flush();
        httpHeartbeat.setAgent(agent);
        httpHeartbeatRepository.saveAndFlush(httpHeartbeat);
        Long agentId = agent.getId();
        // Get all the httpHeartbeatList where agent equals to agentId
        defaultHttpHeartbeatShouldBeFound("agentId.equals=" + agentId);

        // Get all the httpHeartbeatList where agent equals to (agentId + 1)
        defaultHttpHeartbeatShouldNotBeFound("agentId.equals=" + (agentId + 1));
    }

    private void defaultHttpHeartbeatFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultHttpHeartbeatShouldBeFound(shouldBeFound);
        defaultHttpHeartbeatShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultHttpHeartbeatShouldBeFound(String filter) throws Exception {
        restHttpHeartbeatMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(httpHeartbeat.getId().intValue())))
            .andExpect(jsonPath("$.[*].executedAt").value(hasItem(DEFAULT_EXECUTED_AT.toString())))
            .andExpect(jsonPath("$.[*].success").value(hasItem(DEFAULT_SUCCESS)))
            .andExpect(jsonPath("$.[*].responseTimeMs").value(hasItem(DEFAULT_RESPONSE_TIME_MS)))
            .andExpect(jsonPath("$.[*].responseSizeBytes").value(hasItem(DEFAULT_RESPONSE_SIZE_BYTES)))
            .andExpect(jsonPath("$.[*].responseStatusCode").value(hasItem(DEFAULT_RESPONSE_STATUS_CODE)))
            .andExpect(jsonPath("$.[*].responseContentType").value(hasItem(DEFAULT_RESPONSE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].responseServer").value(hasItem(DEFAULT_RESPONSE_SERVER)))
            .andExpect(jsonPath("$.[*].responseCacheStatus").value(hasItem(DEFAULT_RESPONSE_CACHE_STATUS)))
            .andExpect(jsonPath("$.[*].dnsLookupMs").value(hasItem(DEFAULT_DNS_LOOKUP_MS)))
            .andExpect(jsonPath("$.[*].tcpConnectMs").value(hasItem(DEFAULT_TCP_CONNECT_MS)))
            .andExpect(jsonPath("$.[*].tlsHandshakeMs").value(hasItem(DEFAULT_TLS_HANDSHAKE_MS)))
            .andExpect(jsonPath("$.[*].timeToFirstByteMs").value(hasItem(DEFAULT_TIME_TO_FIRST_BYTE_MS)))
            .andExpect(jsonPath("$.[*].warningThresholdMs").value(hasItem(DEFAULT_WARNING_THRESHOLD_MS)))
            .andExpect(jsonPath("$.[*].criticalThresholdMs").value(hasItem(DEFAULT_CRITICAL_THRESHOLD_MS)))
            .andExpect(jsonPath("$.[*].errorType").value(hasItem(DEFAULT_ERROR_TYPE)))
            .andExpect(jsonPath("$.[*].errorMessage").value(hasItem(DEFAULT_ERROR_MESSAGE)))
            .andExpect(jsonPath("$.[*].rawRequestHeaders").value(hasItem(DEFAULT_RAW_REQUEST_HEADERS)))
            .andExpect(jsonPath("$.[*].rawResponseHeaders").value(hasItem(DEFAULT_RAW_RESPONSE_HEADERS)))
            .andExpect(jsonPath("$.[*].rawResponseBody").value(hasItem(DEFAULT_RAW_RESPONSE_BODY)));

        // Check, that the count call also returns 1
        restHttpHeartbeatMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultHttpHeartbeatShouldNotBeFound(String filter) throws Exception {
        restHttpHeartbeatMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restHttpHeartbeatMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingHttpHeartbeat() throws Exception {
        // Get the httpHeartbeat
        restHttpHeartbeatMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void searchHttpHeartbeat() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);
        httpHeartbeatSearchRepository.save(httpHeartbeat);

        // Search the httpHeartbeat
        restHttpHeartbeatMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + httpHeartbeat.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(httpHeartbeat.getId().intValue())))
            .andExpect(jsonPath("$.[*].executedAt").value(hasItem(DEFAULT_EXECUTED_AT.toString())))
            .andExpect(jsonPath("$.[*].success").value(hasItem(DEFAULT_SUCCESS)))
            .andExpect(jsonPath("$.[*].responseTimeMs").value(hasItem(DEFAULT_RESPONSE_TIME_MS)))
            .andExpect(jsonPath("$.[*].responseSizeBytes").value(hasItem(DEFAULT_RESPONSE_SIZE_BYTES)))
            .andExpect(jsonPath("$.[*].responseStatusCode").value(hasItem(DEFAULT_RESPONSE_STATUS_CODE)))
            .andExpect(jsonPath("$.[*].responseContentType").value(hasItem(DEFAULT_RESPONSE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].responseServer").value(hasItem(DEFAULT_RESPONSE_SERVER)))
            .andExpect(jsonPath("$.[*].responseCacheStatus").value(hasItem(DEFAULT_RESPONSE_CACHE_STATUS)))
            .andExpect(jsonPath("$.[*].dnsLookupMs").value(hasItem(DEFAULT_DNS_LOOKUP_MS)))
            .andExpect(jsonPath("$.[*].tcpConnectMs").value(hasItem(DEFAULT_TCP_CONNECT_MS)))
            .andExpect(jsonPath("$.[*].tlsHandshakeMs").value(hasItem(DEFAULT_TLS_HANDSHAKE_MS)))
            .andExpect(jsonPath("$.[*].timeToFirstByteMs").value(hasItem(DEFAULT_TIME_TO_FIRST_BYTE_MS)))
            .andExpect(jsonPath("$.[*].warningThresholdMs").value(hasItem(DEFAULT_WARNING_THRESHOLD_MS)))
            .andExpect(jsonPath("$.[*].criticalThresholdMs").value(hasItem(DEFAULT_CRITICAL_THRESHOLD_MS)))
            .andExpect(jsonPath("$.[*].errorType").value(hasItem(DEFAULT_ERROR_TYPE)))
            .andExpect(jsonPath("$.[*].errorMessage").value(hasItem(DEFAULT_ERROR_MESSAGE.toString())))
            .andExpect(jsonPath("$.[*].rawRequestHeaders").value(hasItem(DEFAULT_RAW_REQUEST_HEADERS.toString())))
            .andExpect(jsonPath("$.[*].rawResponseHeaders").value(hasItem(DEFAULT_RAW_RESPONSE_HEADERS.toString())))
            .andExpect(jsonPath("$.[*].rawResponseBody").value(hasItem(DEFAULT_RAW_RESPONSE_BODY.toString())));
    }

    protected long getRepositoryCount() {
        return httpHeartbeatRepository.count();
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

    protected HttpHeartbeat getPersistedHttpHeartbeat(HttpHeartbeat httpHeartbeat) {
        return httpHeartbeatRepository.findById(httpHeartbeat.getId()).orElseThrow();
    }

    protected void assertPersistedHttpHeartbeatToMatchAllProperties(HttpHeartbeat expectedHttpHeartbeat) {
        assertHttpHeartbeatAllPropertiesEquals(expectedHttpHeartbeat, getPersistedHttpHeartbeat(expectedHttpHeartbeat));
    }

    protected void assertPersistedHttpHeartbeatToMatchUpdatableProperties(HttpHeartbeat expectedHttpHeartbeat) {
        assertHttpHeartbeatAllUpdatablePropertiesEquals(expectedHttpHeartbeat, getPersistedHttpHeartbeat(expectedHttpHeartbeat));
    }
}
