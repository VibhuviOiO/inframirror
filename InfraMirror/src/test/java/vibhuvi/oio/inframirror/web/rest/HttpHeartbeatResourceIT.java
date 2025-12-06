package vibhuvi.oio.inframirror.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static vibhuvi.oio.inframirror.domain.HttpHeartbeatAsserts.*;
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
import vibhuvi.oio.inframirror.domain.HttpHeartbeat;
import vibhuvi.oio.inframirror.repository.HttpHeartbeatRepository;
import vibhuvi.oio.inframirror.repository.search.HttpHeartbeatSearchRepository;
import vibhuvi.oio.inframirror.service.dto.HttpHeartbeatDTO;
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

    private static final Integer DEFAULT_RESPONSE_SIZE_BYTES = 1;
    private static final Integer UPDATED_RESPONSE_SIZE_BYTES = 2;

    private static final Integer DEFAULT_RESPONSE_STATUS_CODE = 1;
    private static final Integer UPDATED_RESPONSE_STATUS_CODE = 2;

    private static final String DEFAULT_RESPONSE_CONTENT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_RESPONSE_CONTENT_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_RESPONSE_SERVER = "AAAAAAAAAA";
    private static final String UPDATED_RESPONSE_SERVER = "BBBBBBBBBB";

    private static final String DEFAULT_RESPONSE_CACHE_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_RESPONSE_CACHE_STATUS = "BBBBBBBBBB";

    private static final Integer DEFAULT_DNS_LOOKUP_MS = 1;
    private static final Integer UPDATED_DNS_LOOKUP_MS = 2;

    private static final String DEFAULT_DNS_RESOLVED_IP = "AAAAAAAAAA";
    private static final String UPDATED_DNS_RESOLVED_IP = "BBBBBBBBBB";

    private static final Integer DEFAULT_TCP_CONNECT_MS = 1;
    private static final Integer UPDATED_TCP_CONNECT_MS = 2;

    private static final Integer DEFAULT_TLS_HANDSHAKE_MS = 1;
    private static final Integer UPDATED_TLS_HANDSHAKE_MS = 2;

    private static final Boolean DEFAULT_SSL_CERTIFICATE_VALID = false;
    private static final Boolean UPDATED_SSL_CERTIFICATE_VALID = true;

    private static final Instant DEFAULT_SSL_CERTIFICATE_EXPIRY = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SSL_CERTIFICATE_EXPIRY = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_SSL_CERTIFICATE_ISSUER = "AAAAAAAAAA";
    private static final String UPDATED_SSL_CERTIFICATE_ISSUER = "BBBBBBBBBB";

    private static final Integer DEFAULT_SSL_DAYS_UNTIL_EXPIRY = 1;
    private static final Integer UPDATED_SSL_DAYS_UNTIL_EXPIRY = 2;

    private static final Integer DEFAULT_TIME_TO_FIRST_BYTE_MS = 1;
    private static final Integer UPDATED_TIME_TO_FIRST_BYTE_MS = 2;

    private static final Integer DEFAULT_WARNING_THRESHOLD_MS = 1;
    private static final Integer UPDATED_WARNING_THRESHOLD_MS = 2;

    private static final Integer DEFAULT_CRITICAL_THRESHOLD_MS = 1;
    private static final Integer UPDATED_CRITICAL_THRESHOLD_MS = 2;

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

    private static final String DEFAULT_DNS_DETAILS = "AAAAAAAAAA";
    private static final String UPDATED_DNS_DETAILS = "BBBBBBBBBB";

    private static final String DEFAULT_TLS_DETAILS = "AAAAAAAAAA";
    private static final String UPDATED_TLS_DETAILS = "BBBBBBBBBB";

    private static final String DEFAULT_HTTP_VERSION = "AAAAAAAAAA";
    private static final String UPDATED_HTTP_VERSION = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT_ENCODING = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT_ENCODING = "BBBBBBBBBB";

    private static final Float DEFAULT_COMPRESSION_RATIO = 1F;
    private static final Float UPDATED_COMPRESSION_RATIO = 2F;

    private static final String DEFAULT_TRANSFER_ENCODING = "AAAAAAAAAA";
    private static final String UPDATED_TRANSFER_ENCODING = "BBBBBBBBBB";

    private static final String DEFAULT_RESPONSE_BODY_HASH = "AAAAAAAAAA";
    private static final String UPDATED_RESPONSE_BODY_HASH = "BBBBBBBBBB";

    private static final String DEFAULT_RESPONSE_BODY_SAMPLE = "AAAAAAAAAA";
    private static final String UPDATED_RESPONSE_BODY_SAMPLE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_RESPONSE_BODY_VALID = false;
    private static final Boolean UPDATED_RESPONSE_BODY_VALID = true;

    private static final Integer DEFAULT_RESPONSE_BODY_UNCOMPRESSED_BYTES = 1;
    private static final Integer UPDATED_RESPONSE_BODY_UNCOMPRESSED_BYTES = 2;

    private static final String DEFAULT_REDIRECT_DETAILS = "AAAAAAAAAA";
    private static final String UPDATED_REDIRECT_DETAILS = "BBBBBBBBBB";

    private static final String DEFAULT_CACHE_CONTROL = "AAAAAAAAAA";
    private static final String UPDATED_CACHE_CONTROL = "BBBBBBBBBB";

    private static final String DEFAULT_ETAG = "AAAAAAAAAA";
    private static final String UPDATED_ETAG = "BBBBBBBBBB";

    private static final Integer DEFAULT_CACHE_AGE = 1;
    private static final Integer UPDATED_CACHE_AGE = 2;

    private static final String DEFAULT_CDN_PROVIDER = "AAAAAAAAAA";
    private static final String UPDATED_CDN_PROVIDER = "BBBBBBBBBB";

    private static final String DEFAULT_CDN_POP = "AAAAAAAAAA";
    private static final String UPDATED_CDN_POP = "BBBBBBBBBB";

    private static final String DEFAULT_RATE_LIMIT_DETAILS = "AAAAAAAAAA";
    private static final String UPDATED_RATE_LIMIT_DETAILS = "BBBBBBBBBB";

    private static final String DEFAULT_NETWORK_PATH = "AAAAAAAAAA";
    private static final String UPDATED_NETWORK_PATH = "BBBBBBBBBB";

    private static final String DEFAULT_AGENT_METRICS = "AAAAAAAAAA";
    private static final String UPDATED_AGENT_METRICS = "BBBBBBBBBB";

    private static final String DEFAULT_PHASE_LATENCIES = "AAAAAAAAAA";
    private static final String UPDATED_PHASE_LATENCIES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/http-heartbeats";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/http-heartbeats/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

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
            .dnsResolvedIp(DEFAULT_DNS_RESOLVED_IP)
            .tcpConnectMs(DEFAULT_TCP_CONNECT_MS)
            .tlsHandshakeMs(DEFAULT_TLS_HANDSHAKE_MS)
            .sslCertificateValid(DEFAULT_SSL_CERTIFICATE_VALID)
            .sslCertificateExpiry(DEFAULT_SSL_CERTIFICATE_EXPIRY)
            .sslCertificateIssuer(DEFAULT_SSL_CERTIFICATE_ISSUER)
            .sslDaysUntilExpiry(DEFAULT_SSL_DAYS_UNTIL_EXPIRY)
            .timeToFirstByteMs(DEFAULT_TIME_TO_FIRST_BYTE_MS)
            .warningThresholdMs(DEFAULT_WARNING_THRESHOLD_MS)
            .criticalThresholdMs(DEFAULT_CRITICAL_THRESHOLD_MS)
            .errorType(DEFAULT_ERROR_TYPE)
            .errorMessage(DEFAULT_ERROR_MESSAGE)
            .rawRequestHeaders(DEFAULT_RAW_REQUEST_HEADERS)
            .rawResponseHeaders(DEFAULT_RAW_RESPONSE_HEADERS)
            .rawResponseBody(DEFAULT_RAW_RESPONSE_BODY)
            .dnsDetails(DEFAULT_DNS_DETAILS)
            .tlsDetails(DEFAULT_TLS_DETAILS)
            .httpVersion(DEFAULT_HTTP_VERSION)
            .contentEncoding(DEFAULT_CONTENT_ENCODING)
            .compressionRatio(DEFAULT_COMPRESSION_RATIO)
            .transferEncoding(DEFAULT_TRANSFER_ENCODING)
            .responseBodyHash(DEFAULT_RESPONSE_BODY_HASH)
            .responseBodySample(DEFAULT_RESPONSE_BODY_SAMPLE)
            .responseBodyValid(DEFAULT_RESPONSE_BODY_VALID)
            .responseBodyUncompressedBytes(DEFAULT_RESPONSE_BODY_UNCOMPRESSED_BYTES)
            .redirectDetails(DEFAULT_REDIRECT_DETAILS)
            .cacheControl(DEFAULT_CACHE_CONTROL)
            .etag(DEFAULT_ETAG)
            .cacheAge(DEFAULT_CACHE_AGE)
            .cdnProvider(DEFAULT_CDN_PROVIDER)
            .cdnPop(DEFAULT_CDN_POP)
            .rateLimitDetails(DEFAULT_RATE_LIMIT_DETAILS)
            .networkPath(DEFAULT_NETWORK_PATH)
            .agentMetrics(DEFAULT_AGENT_METRICS)
            .phaseLatencies(DEFAULT_PHASE_LATENCIES);
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
            .dnsResolvedIp(UPDATED_DNS_RESOLVED_IP)
            .tcpConnectMs(UPDATED_TCP_CONNECT_MS)
            .tlsHandshakeMs(UPDATED_TLS_HANDSHAKE_MS)
            .sslCertificateValid(UPDATED_SSL_CERTIFICATE_VALID)
            .sslCertificateExpiry(UPDATED_SSL_CERTIFICATE_EXPIRY)
            .sslCertificateIssuer(UPDATED_SSL_CERTIFICATE_ISSUER)
            .sslDaysUntilExpiry(UPDATED_SSL_DAYS_UNTIL_EXPIRY)
            .timeToFirstByteMs(UPDATED_TIME_TO_FIRST_BYTE_MS)
            .warningThresholdMs(UPDATED_WARNING_THRESHOLD_MS)
            .criticalThresholdMs(UPDATED_CRITICAL_THRESHOLD_MS)
            .errorType(UPDATED_ERROR_TYPE)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .rawRequestHeaders(UPDATED_RAW_REQUEST_HEADERS)
            .rawResponseHeaders(UPDATED_RAW_RESPONSE_HEADERS)
            .rawResponseBody(UPDATED_RAW_RESPONSE_BODY)
            .dnsDetails(UPDATED_DNS_DETAILS)
            .tlsDetails(UPDATED_TLS_DETAILS)
            .httpVersion(UPDATED_HTTP_VERSION)
            .contentEncoding(UPDATED_CONTENT_ENCODING)
            .compressionRatio(UPDATED_COMPRESSION_RATIO)
            .transferEncoding(UPDATED_TRANSFER_ENCODING)
            .responseBodyHash(UPDATED_RESPONSE_BODY_HASH)
            .responseBodySample(UPDATED_RESPONSE_BODY_SAMPLE)
            .responseBodyValid(UPDATED_RESPONSE_BODY_VALID)
            .responseBodyUncompressedBytes(UPDATED_RESPONSE_BODY_UNCOMPRESSED_BYTES)
            .redirectDetails(UPDATED_REDIRECT_DETAILS)
            .cacheControl(UPDATED_CACHE_CONTROL)
            .etag(UPDATED_ETAG)
            .cacheAge(UPDATED_CACHE_AGE)
            .cdnProvider(UPDATED_CDN_PROVIDER)
            .cdnPop(UPDATED_CDN_POP)
            .rateLimitDetails(UPDATED_RATE_LIMIT_DETAILS)
            .networkPath(UPDATED_NETWORK_PATH)
            .agentMetrics(UPDATED_AGENT_METRICS)
            .phaseLatencies(UPDATED_PHASE_LATENCIES);
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
    void createHttpHeartbeat() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(httpHeartbeatSearchRepository.findAll());
        // Create the HttpHeartbeat
        HttpHeartbeatDTO httpHeartbeatDTO = httpHeartbeatMapper.toDto(httpHeartbeat);
        var returnedHttpHeartbeatDTO = om.readValue(
            restHttpHeartbeatMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(httpHeartbeatDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            HttpHeartbeatDTO.class
        );

        // Validate the HttpHeartbeat in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedHttpHeartbeat = httpHeartbeatMapper.toEntity(returnedHttpHeartbeatDTO);
        assertHttpHeartbeatUpdatableFieldsEquals(returnedHttpHeartbeat, getPersistedHttpHeartbeat(returnedHttpHeartbeat));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(httpHeartbeatSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedHttpHeartbeat = returnedHttpHeartbeat;
    }

    @Test
    @Transactional
    void createHttpHeartbeatWithExistingId() throws Exception {
        // Create the HttpHeartbeat with an existing ID
        httpHeartbeat.setId(1L);
        HttpHeartbeatDTO httpHeartbeatDTO = httpHeartbeatMapper.toDto(httpHeartbeat);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(httpHeartbeatSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restHttpHeartbeatMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(httpHeartbeatDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HttpHeartbeat in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(httpHeartbeatSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkExecutedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(httpHeartbeatSearchRepository.findAll());
        // set the field null
        httpHeartbeat.setExecutedAt(null);

        // Create the HttpHeartbeat, which fails.
        HttpHeartbeatDTO httpHeartbeatDTO = httpHeartbeatMapper.toDto(httpHeartbeat);

        restHttpHeartbeatMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(httpHeartbeatDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(httpHeartbeatSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
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
            .andExpect(jsonPath("$.[*].dnsResolvedIp").value(hasItem(DEFAULT_DNS_RESOLVED_IP)))
            .andExpect(jsonPath("$.[*].tcpConnectMs").value(hasItem(DEFAULT_TCP_CONNECT_MS)))
            .andExpect(jsonPath("$.[*].tlsHandshakeMs").value(hasItem(DEFAULT_TLS_HANDSHAKE_MS)))
            .andExpect(jsonPath("$.[*].sslCertificateValid").value(hasItem(DEFAULT_SSL_CERTIFICATE_VALID)))
            .andExpect(jsonPath("$.[*].sslCertificateExpiry").value(hasItem(DEFAULT_SSL_CERTIFICATE_EXPIRY.toString())))
            .andExpect(jsonPath("$.[*].sslCertificateIssuer").value(hasItem(DEFAULT_SSL_CERTIFICATE_ISSUER)))
            .andExpect(jsonPath("$.[*].sslDaysUntilExpiry").value(hasItem(DEFAULT_SSL_DAYS_UNTIL_EXPIRY)))
            .andExpect(jsonPath("$.[*].timeToFirstByteMs").value(hasItem(DEFAULT_TIME_TO_FIRST_BYTE_MS)))
            .andExpect(jsonPath("$.[*].warningThresholdMs").value(hasItem(DEFAULT_WARNING_THRESHOLD_MS)))
            .andExpect(jsonPath("$.[*].criticalThresholdMs").value(hasItem(DEFAULT_CRITICAL_THRESHOLD_MS)))
            .andExpect(jsonPath("$.[*].errorType").value(hasItem(DEFAULT_ERROR_TYPE)))
            .andExpect(jsonPath("$.[*].errorMessage").value(hasItem(DEFAULT_ERROR_MESSAGE)))
            .andExpect(jsonPath("$.[*].rawRequestHeaders").value(hasItem(DEFAULT_RAW_REQUEST_HEADERS)))
            .andExpect(jsonPath("$.[*].rawResponseHeaders").value(hasItem(DEFAULT_RAW_RESPONSE_HEADERS)))
            .andExpect(jsonPath("$.[*].rawResponseBody").value(hasItem(DEFAULT_RAW_RESPONSE_BODY)))
            .andExpect(jsonPath("$.[*].dnsDetails").value(hasItem(DEFAULT_DNS_DETAILS)))
            .andExpect(jsonPath("$.[*].tlsDetails").value(hasItem(DEFAULT_TLS_DETAILS)))
            .andExpect(jsonPath("$.[*].httpVersion").value(hasItem(DEFAULT_HTTP_VERSION)))
            .andExpect(jsonPath("$.[*].contentEncoding").value(hasItem(DEFAULT_CONTENT_ENCODING)))
            .andExpect(jsonPath("$.[*].compressionRatio").value(hasItem(DEFAULT_COMPRESSION_RATIO.doubleValue())))
            .andExpect(jsonPath("$.[*].transferEncoding").value(hasItem(DEFAULT_TRANSFER_ENCODING)))
            .andExpect(jsonPath("$.[*].responseBodyHash").value(hasItem(DEFAULT_RESPONSE_BODY_HASH)))
            .andExpect(jsonPath("$.[*].responseBodySample").value(hasItem(DEFAULT_RESPONSE_BODY_SAMPLE)))
            .andExpect(jsonPath("$.[*].responseBodyValid").value(hasItem(DEFAULT_RESPONSE_BODY_VALID)))
            .andExpect(jsonPath("$.[*].responseBodyUncompressedBytes").value(hasItem(DEFAULT_RESPONSE_BODY_UNCOMPRESSED_BYTES)))
            .andExpect(jsonPath("$.[*].redirectDetails").value(hasItem(DEFAULT_REDIRECT_DETAILS)))
            .andExpect(jsonPath("$.[*].cacheControl").value(hasItem(DEFAULT_CACHE_CONTROL)))
            .andExpect(jsonPath("$.[*].etag").value(hasItem(DEFAULT_ETAG)))
            .andExpect(jsonPath("$.[*].cacheAge").value(hasItem(DEFAULT_CACHE_AGE)))
            .andExpect(jsonPath("$.[*].cdnProvider").value(hasItem(DEFAULT_CDN_PROVIDER)))
            .andExpect(jsonPath("$.[*].cdnPop").value(hasItem(DEFAULT_CDN_POP)))
            .andExpect(jsonPath("$.[*].rateLimitDetails").value(hasItem(DEFAULT_RATE_LIMIT_DETAILS)))
            .andExpect(jsonPath("$.[*].networkPath").value(hasItem(DEFAULT_NETWORK_PATH)))
            .andExpect(jsonPath("$.[*].agentMetrics").value(hasItem(DEFAULT_AGENT_METRICS)))
            .andExpect(jsonPath("$.[*].phaseLatencies").value(hasItem(DEFAULT_PHASE_LATENCIES)));
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
            .andExpect(jsonPath("$.dnsResolvedIp").value(DEFAULT_DNS_RESOLVED_IP))
            .andExpect(jsonPath("$.tcpConnectMs").value(DEFAULT_TCP_CONNECT_MS))
            .andExpect(jsonPath("$.tlsHandshakeMs").value(DEFAULT_TLS_HANDSHAKE_MS))
            .andExpect(jsonPath("$.sslCertificateValid").value(DEFAULT_SSL_CERTIFICATE_VALID))
            .andExpect(jsonPath("$.sslCertificateExpiry").value(DEFAULT_SSL_CERTIFICATE_EXPIRY.toString()))
            .andExpect(jsonPath("$.sslCertificateIssuer").value(DEFAULT_SSL_CERTIFICATE_ISSUER))
            .andExpect(jsonPath("$.sslDaysUntilExpiry").value(DEFAULT_SSL_DAYS_UNTIL_EXPIRY))
            .andExpect(jsonPath("$.timeToFirstByteMs").value(DEFAULT_TIME_TO_FIRST_BYTE_MS))
            .andExpect(jsonPath("$.warningThresholdMs").value(DEFAULT_WARNING_THRESHOLD_MS))
            .andExpect(jsonPath("$.criticalThresholdMs").value(DEFAULT_CRITICAL_THRESHOLD_MS))
            .andExpect(jsonPath("$.errorType").value(DEFAULT_ERROR_TYPE))
            .andExpect(jsonPath("$.errorMessage").value(DEFAULT_ERROR_MESSAGE))
            .andExpect(jsonPath("$.rawRequestHeaders").value(DEFAULT_RAW_REQUEST_HEADERS))
            .andExpect(jsonPath("$.rawResponseHeaders").value(DEFAULT_RAW_RESPONSE_HEADERS))
            .andExpect(jsonPath("$.rawResponseBody").value(DEFAULT_RAW_RESPONSE_BODY))
            .andExpect(jsonPath("$.dnsDetails").value(DEFAULT_DNS_DETAILS))
            .andExpect(jsonPath("$.tlsDetails").value(DEFAULT_TLS_DETAILS))
            .andExpect(jsonPath("$.httpVersion").value(DEFAULT_HTTP_VERSION))
            .andExpect(jsonPath("$.contentEncoding").value(DEFAULT_CONTENT_ENCODING))
            .andExpect(jsonPath("$.compressionRatio").value(DEFAULT_COMPRESSION_RATIO.doubleValue()))
            .andExpect(jsonPath("$.transferEncoding").value(DEFAULT_TRANSFER_ENCODING))
            .andExpect(jsonPath("$.responseBodyHash").value(DEFAULT_RESPONSE_BODY_HASH))
            .andExpect(jsonPath("$.responseBodySample").value(DEFAULT_RESPONSE_BODY_SAMPLE))
            .andExpect(jsonPath("$.responseBodyValid").value(DEFAULT_RESPONSE_BODY_VALID))
            .andExpect(jsonPath("$.responseBodyUncompressedBytes").value(DEFAULT_RESPONSE_BODY_UNCOMPRESSED_BYTES))
            .andExpect(jsonPath("$.redirectDetails").value(DEFAULT_REDIRECT_DETAILS))
            .andExpect(jsonPath("$.cacheControl").value(DEFAULT_CACHE_CONTROL))
            .andExpect(jsonPath("$.etag").value(DEFAULT_ETAG))
            .andExpect(jsonPath("$.cacheAge").value(DEFAULT_CACHE_AGE))
            .andExpect(jsonPath("$.cdnProvider").value(DEFAULT_CDN_PROVIDER))
            .andExpect(jsonPath("$.cdnPop").value(DEFAULT_CDN_POP))
            .andExpect(jsonPath("$.rateLimitDetails").value(DEFAULT_RATE_LIMIT_DETAILS))
            .andExpect(jsonPath("$.networkPath").value(DEFAULT_NETWORK_PATH))
            .andExpect(jsonPath("$.agentMetrics").value(DEFAULT_AGENT_METRICS))
            .andExpect(jsonPath("$.phaseLatencies").value(DEFAULT_PHASE_LATENCIES));
    }

    @Test
    @Transactional
    void getNonExistingHttpHeartbeat() throws Exception {
        // Get the httpHeartbeat
        restHttpHeartbeatMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingHttpHeartbeat() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        httpHeartbeatSearchRepository.save(httpHeartbeat);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(httpHeartbeatSearchRepository.findAll());

        // Update the httpHeartbeat
        HttpHeartbeat updatedHttpHeartbeat = httpHeartbeatRepository.findById(httpHeartbeat.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedHttpHeartbeat are not directly saved in db
        em.detach(updatedHttpHeartbeat);
        updatedHttpHeartbeat
            .executedAt(UPDATED_EXECUTED_AT)
            .success(UPDATED_SUCCESS)
            .responseTimeMs(UPDATED_RESPONSE_TIME_MS)
            .responseSizeBytes(UPDATED_RESPONSE_SIZE_BYTES)
            .responseStatusCode(UPDATED_RESPONSE_STATUS_CODE)
            .responseContentType(UPDATED_RESPONSE_CONTENT_TYPE)
            .responseServer(UPDATED_RESPONSE_SERVER)
            .responseCacheStatus(UPDATED_RESPONSE_CACHE_STATUS)
            .dnsLookupMs(UPDATED_DNS_LOOKUP_MS)
            .dnsResolvedIp(UPDATED_DNS_RESOLVED_IP)
            .tcpConnectMs(UPDATED_TCP_CONNECT_MS)
            .tlsHandshakeMs(UPDATED_TLS_HANDSHAKE_MS)
            .sslCertificateValid(UPDATED_SSL_CERTIFICATE_VALID)
            .sslCertificateExpiry(UPDATED_SSL_CERTIFICATE_EXPIRY)
            .sslCertificateIssuer(UPDATED_SSL_CERTIFICATE_ISSUER)
            .sslDaysUntilExpiry(UPDATED_SSL_DAYS_UNTIL_EXPIRY)
            .timeToFirstByteMs(UPDATED_TIME_TO_FIRST_BYTE_MS)
            .warningThresholdMs(UPDATED_WARNING_THRESHOLD_MS)
            .criticalThresholdMs(UPDATED_CRITICAL_THRESHOLD_MS)
            .errorType(UPDATED_ERROR_TYPE)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .rawRequestHeaders(UPDATED_RAW_REQUEST_HEADERS)
            .rawResponseHeaders(UPDATED_RAW_RESPONSE_HEADERS)
            .rawResponseBody(UPDATED_RAW_RESPONSE_BODY)
            .dnsDetails(UPDATED_DNS_DETAILS)
            .tlsDetails(UPDATED_TLS_DETAILS)
            .httpVersion(UPDATED_HTTP_VERSION)
            .contentEncoding(UPDATED_CONTENT_ENCODING)
            .compressionRatio(UPDATED_COMPRESSION_RATIO)
            .transferEncoding(UPDATED_TRANSFER_ENCODING)
            .responseBodyHash(UPDATED_RESPONSE_BODY_HASH)
            .responseBodySample(UPDATED_RESPONSE_BODY_SAMPLE)
            .responseBodyValid(UPDATED_RESPONSE_BODY_VALID)
            .responseBodyUncompressedBytes(UPDATED_RESPONSE_BODY_UNCOMPRESSED_BYTES)
            .redirectDetails(UPDATED_REDIRECT_DETAILS)
            .cacheControl(UPDATED_CACHE_CONTROL)
            .etag(UPDATED_ETAG)
            .cacheAge(UPDATED_CACHE_AGE)
            .cdnProvider(UPDATED_CDN_PROVIDER)
            .cdnPop(UPDATED_CDN_POP)
            .rateLimitDetails(UPDATED_RATE_LIMIT_DETAILS)
            .networkPath(UPDATED_NETWORK_PATH)
            .agentMetrics(UPDATED_AGENT_METRICS)
            .phaseLatencies(UPDATED_PHASE_LATENCIES);
        HttpHeartbeatDTO httpHeartbeatDTO = httpHeartbeatMapper.toDto(updatedHttpHeartbeat);

        restHttpHeartbeatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, httpHeartbeatDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(httpHeartbeatDTO))
            )
            .andExpect(status().isOk());

        // Validate the HttpHeartbeat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedHttpHeartbeatToMatchAllProperties(updatedHttpHeartbeat);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(httpHeartbeatSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<HttpHeartbeat> httpHeartbeatSearchList = Streamable.of(httpHeartbeatSearchRepository.findAll()).toList();
                HttpHeartbeat testHttpHeartbeatSearch = httpHeartbeatSearchList.get(searchDatabaseSizeAfter - 1);

                assertHttpHeartbeatAllPropertiesEquals(testHttpHeartbeatSearch, updatedHttpHeartbeat);
            });
    }

    @Test
    @Transactional
    void putNonExistingHttpHeartbeat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(httpHeartbeatSearchRepository.findAll());
        httpHeartbeat.setId(longCount.incrementAndGet());

        // Create the HttpHeartbeat
        HttpHeartbeatDTO httpHeartbeatDTO = httpHeartbeatMapper.toDto(httpHeartbeat);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHttpHeartbeatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, httpHeartbeatDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(httpHeartbeatDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HttpHeartbeat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(httpHeartbeatSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchHttpHeartbeat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(httpHeartbeatSearchRepository.findAll());
        httpHeartbeat.setId(longCount.incrementAndGet());

        // Create the HttpHeartbeat
        HttpHeartbeatDTO httpHeartbeatDTO = httpHeartbeatMapper.toDto(httpHeartbeat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHttpHeartbeatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(httpHeartbeatDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HttpHeartbeat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(httpHeartbeatSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamHttpHeartbeat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(httpHeartbeatSearchRepository.findAll());
        httpHeartbeat.setId(longCount.incrementAndGet());

        // Create the HttpHeartbeat
        HttpHeartbeatDTO httpHeartbeatDTO = httpHeartbeatMapper.toDto(httpHeartbeat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHttpHeartbeatMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(httpHeartbeatDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the HttpHeartbeat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(httpHeartbeatSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateHttpHeartbeatWithPatch() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the httpHeartbeat using partial update
        HttpHeartbeat partialUpdatedHttpHeartbeat = new HttpHeartbeat();
        partialUpdatedHttpHeartbeat.setId(httpHeartbeat.getId());

        partialUpdatedHttpHeartbeat
            .responseSizeBytes(UPDATED_RESPONSE_SIZE_BYTES)
            .responseContentType(UPDATED_RESPONSE_CONTENT_TYPE)
            .responseServer(UPDATED_RESPONSE_SERVER)
            .responseCacheStatus(UPDATED_RESPONSE_CACHE_STATUS)
            .dnsLookupMs(UPDATED_DNS_LOOKUP_MS)
            .sslCertificateValid(UPDATED_SSL_CERTIFICATE_VALID)
            .sslCertificateExpiry(UPDATED_SSL_CERTIFICATE_EXPIRY)
            .sslCertificateIssuer(UPDATED_SSL_CERTIFICATE_ISSUER)
            .criticalThresholdMs(UPDATED_CRITICAL_THRESHOLD_MS)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .dnsDetails(UPDATED_DNS_DETAILS)
            .tlsDetails(UPDATED_TLS_DETAILS)
            .httpVersion(UPDATED_HTTP_VERSION)
            .contentEncoding(UPDATED_CONTENT_ENCODING)
            .responseBodySample(UPDATED_RESPONSE_BODY_SAMPLE)
            .responseBodyValid(UPDATED_RESPONSE_BODY_VALID)
            .responseBodyUncompressedBytes(UPDATED_RESPONSE_BODY_UNCOMPRESSED_BYTES)
            .redirectDetails(UPDATED_REDIRECT_DETAILS)
            .etag(UPDATED_ETAG)
            .cdnProvider(UPDATED_CDN_PROVIDER)
            .rateLimitDetails(UPDATED_RATE_LIMIT_DETAILS)
            .networkPath(UPDATED_NETWORK_PATH)
            .agentMetrics(UPDATED_AGENT_METRICS);

        restHttpHeartbeatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHttpHeartbeat.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedHttpHeartbeat))
            )
            .andExpect(status().isOk());

        // Validate the HttpHeartbeat in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertHttpHeartbeatUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedHttpHeartbeat, httpHeartbeat),
            getPersistedHttpHeartbeat(httpHeartbeat)
        );
    }

    @Test
    @Transactional
    void fullUpdateHttpHeartbeatWithPatch() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the httpHeartbeat using partial update
        HttpHeartbeat partialUpdatedHttpHeartbeat = new HttpHeartbeat();
        partialUpdatedHttpHeartbeat.setId(httpHeartbeat.getId());

        partialUpdatedHttpHeartbeat
            .executedAt(UPDATED_EXECUTED_AT)
            .success(UPDATED_SUCCESS)
            .responseTimeMs(UPDATED_RESPONSE_TIME_MS)
            .responseSizeBytes(UPDATED_RESPONSE_SIZE_BYTES)
            .responseStatusCode(UPDATED_RESPONSE_STATUS_CODE)
            .responseContentType(UPDATED_RESPONSE_CONTENT_TYPE)
            .responseServer(UPDATED_RESPONSE_SERVER)
            .responseCacheStatus(UPDATED_RESPONSE_CACHE_STATUS)
            .dnsLookupMs(UPDATED_DNS_LOOKUP_MS)
            .dnsResolvedIp(UPDATED_DNS_RESOLVED_IP)
            .tcpConnectMs(UPDATED_TCP_CONNECT_MS)
            .tlsHandshakeMs(UPDATED_TLS_HANDSHAKE_MS)
            .sslCertificateValid(UPDATED_SSL_CERTIFICATE_VALID)
            .sslCertificateExpiry(UPDATED_SSL_CERTIFICATE_EXPIRY)
            .sslCertificateIssuer(UPDATED_SSL_CERTIFICATE_ISSUER)
            .sslDaysUntilExpiry(UPDATED_SSL_DAYS_UNTIL_EXPIRY)
            .timeToFirstByteMs(UPDATED_TIME_TO_FIRST_BYTE_MS)
            .warningThresholdMs(UPDATED_WARNING_THRESHOLD_MS)
            .criticalThresholdMs(UPDATED_CRITICAL_THRESHOLD_MS)
            .errorType(UPDATED_ERROR_TYPE)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .rawRequestHeaders(UPDATED_RAW_REQUEST_HEADERS)
            .rawResponseHeaders(UPDATED_RAW_RESPONSE_HEADERS)
            .rawResponseBody(UPDATED_RAW_RESPONSE_BODY)
            .dnsDetails(UPDATED_DNS_DETAILS)
            .tlsDetails(UPDATED_TLS_DETAILS)
            .httpVersion(UPDATED_HTTP_VERSION)
            .contentEncoding(UPDATED_CONTENT_ENCODING)
            .compressionRatio(UPDATED_COMPRESSION_RATIO)
            .transferEncoding(UPDATED_TRANSFER_ENCODING)
            .responseBodyHash(UPDATED_RESPONSE_BODY_HASH)
            .responseBodySample(UPDATED_RESPONSE_BODY_SAMPLE)
            .responseBodyValid(UPDATED_RESPONSE_BODY_VALID)
            .responseBodyUncompressedBytes(UPDATED_RESPONSE_BODY_UNCOMPRESSED_BYTES)
            .redirectDetails(UPDATED_REDIRECT_DETAILS)
            .cacheControl(UPDATED_CACHE_CONTROL)
            .etag(UPDATED_ETAG)
            .cacheAge(UPDATED_CACHE_AGE)
            .cdnProvider(UPDATED_CDN_PROVIDER)
            .cdnPop(UPDATED_CDN_POP)
            .rateLimitDetails(UPDATED_RATE_LIMIT_DETAILS)
            .networkPath(UPDATED_NETWORK_PATH)
            .agentMetrics(UPDATED_AGENT_METRICS)
            .phaseLatencies(UPDATED_PHASE_LATENCIES);

        restHttpHeartbeatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHttpHeartbeat.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedHttpHeartbeat))
            )
            .andExpect(status().isOk());

        // Validate the HttpHeartbeat in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertHttpHeartbeatUpdatableFieldsEquals(partialUpdatedHttpHeartbeat, getPersistedHttpHeartbeat(partialUpdatedHttpHeartbeat));
    }

    @Test
    @Transactional
    void patchNonExistingHttpHeartbeat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(httpHeartbeatSearchRepository.findAll());
        httpHeartbeat.setId(longCount.incrementAndGet());

        // Create the HttpHeartbeat
        HttpHeartbeatDTO httpHeartbeatDTO = httpHeartbeatMapper.toDto(httpHeartbeat);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHttpHeartbeatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, httpHeartbeatDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(httpHeartbeatDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HttpHeartbeat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(httpHeartbeatSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchHttpHeartbeat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(httpHeartbeatSearchRepository.findAll());
        httpHeartbeat.setId(longCount.incrementAndGet());

        // Create the HttpHeartbeat
        HttpHeartbeatDTO httpHeartbeatDTO = httpHeartbeatMapper.toDto(httpHeartbeat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHttpHeartbeatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(httpHeartbeatDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HttpHeartbeat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(httpHeartbeatSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamHttpHeartbeat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(httpHeartbeatSearchRepository.findAll());
        httpHeartbeat.setId(longCount.incrementAndGet());

        // Create the HttpHeartbeat
        HttpHeartbeatDTO httpHeartbeatDTO = httpHeartbeatMapper.toDto(httpHeartbeat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHttpHeartbeatMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(httpHeartbeatDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the HttpHeartbeat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(httpHeartbeatSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteHttpHeartbeat() throws Exception {
        // Initialize the database
        insertedHttpHeartbeat = httpHeartbeatRepository.saveAndFlush(httpHeartbeat);
        httpHeartbeatRepository.save(httpHeartbeat);
        httpHeartbeatSearchRepository.save(httpHeartbeat);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(httpHeartbeatSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the httpHeartbeat
        restHttpHeartbeatMockMvc
            .perform(delete(ENTITY_API_URL_ID, httpHeartbeat.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(httpHeartbeatSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
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
            .andExpect(jsonPath("$.[*].dnsResolvedIp").value(hasItem(DEFAULT_DNS_RESOLVED_IP)))
            .andExpect(jsonPath("$.[*].tcpConnectMs").value(hasItem(DEFAULT_TCP_CONNECT_MS)))
            .andExpect(jsonPath("$.[*].tlsHandshakeMs").value(hasItem(DEFAULT_TLS_HANDSHAKE_MS)))
            .andExpect(jsonPath("$.[*].sslCertificateValid").value(hasItem(DEFAULT_SSL_CERTIFICATE_VALID)))
            .andExpect(jsonPath("$.[*].sslCertificateExpiry").value(hasItem(DEFAULT_SSL_CERTIFICATE_EXPIRY.toString())))
            .andExpect(jsonPath("$.[*].sslCertificateIssuer").value(hasItem(DEFAULT_SSL_CERTIFICATE_ISSUER)))
            .andExpect(jsonPath("$.[*].sslDaysUntilExpiry").value(hasItem(DEFAULT_SSL_DAYS_UNTIL_EXPIRY)))
            .andExpect(jsonPath("$.[*].timeToFirstByteMs").value(hasItem(DEFAULT_TIME_TO_FIRST_BYTE_MS)))
            .andExpect(jsonPath("$.[*].warningThresholdMs").value(hasItem(DEFAULT_WARNING_THRESHOLD_MS)))
            .andExpect(jsonPath("$.[*].criticalThresholdMs").value(hasItem(DEFAULT_CRITICAL_THRESHOLD_MS)))
            .andExpect(jsonPath("$.[*].errorType").value(hasItem(DEFAULT_ERROR_TYPE)))
            .andExpect(jsonPath("$.[*].errorMessage").value(hasItem(DEFAULT_ERROR_MESSAGE.toString())))
            .andExpect(jsonPath("$.[*].rawRequestHeaders").value(hasItem(DEFAULT_RAW_REQUEST_HEADERS.toString())))
            .andExpect(jsonPath("$.[*].rawResponseHeaders").value(hasItem(DEFAULT_RAW_RESPONSE_HEADERS.toString())))
            .andExpect(jsonPath("$.[*].rawResponseBody").value(hasItem(DEFAULT_RAW_RESPONSE_BODY.toString())))
            .andExpect(jsonPath("$.[*].dnsDetails").value(hasItem(DEFAULT_DNS_DETAILS.toString())))
            .andExpect(jsonPath("$.[*].tlsDetails").value(hasItem(DEFAULT_TLS_DETAILS.toString())))
            .andExpect(jsonPath("$.[*].httpVersion").value(hasItem(DEFAULT_HTTP_VERSION)))
            .andExpect(jsonPath("$.[*].contentEncoding").value(hasItem(DEFAULT_CONTENT_ENCODING)))
            .andExpect(jsonPath("$.[*].compressionRatio").value(hasItem(DEFAULT_COMPRESSION_RATIO.doubleValue())))
            .andExpect(jsonPath("$.[*].transferEncoding").value(hasItem(DEFAULT_TRANSFER_ENCODING)))
            .andExpect(jsonPath("$.[*].responseBodyHash").value(hasItem(DEFAULT_RESPONSE_BODY_HASH)))
            .andExpect(jsonPath("$.[*].responseBodySample").value(hasItem(DEFAULT_RESPONSE_BODY_SAMPLE.toString())))
            .andExpect(jsonPath("$.[*].responseBodyValid").value(hasItem(DEFAULT_RESPONSE_BODY_VALID)))
            .andExpect(jsonPath("$.[*].responseBodyUncompressedBytes").value(hasItem(DEFAULT_RESPONSE_BODY_UNCOMPRESSED_BYTES)))
            .andExpect(jsonPath("$.[*].redirectDetails").value(hasItem(DEFAULT_REDIRECT_DETAILS.toString())))
            .andExpect(jsonPath("$.[*].cacheControl").value(hasItem(DEFAULT_CACHE_CONTROL)))
            .andExpect(jsonPath("$.[*].etag").value(hasItem(DEFAULT_ETAG)))
            .andExpect(jsonPath("$.[*].cacheAge").value(hasItem(DEFAULT_CACHE_AGE)))
            .andExpect(jsonPath("$.[*].cdnProvider").value(hasItem(DEFAULT_CDN_PROVIDER)))
            .andExpect(jsonPath("$.[*].cdnPop").value(hasItem(DEFAULT_CDN_POP)))
            .andExpect(jsonPath("$.[*].rateLimitDetails").value(hasItem(DEFAULT_RATE_LIMIT_DETAILS.toString())))
            .andExpect(jsonPath("$.[*].networkPath").value(hasItem(DEFAULT_NETWORK_PATH.toString())))
            .andExpect(jsonPath("$.[*].agentMetrics").value(hasItem(DEFAULT_AGENT_METRICS.toString())))
            .andExpect(jsonPath("$.[*].phaseLatencies").value(hasItem(DEFAULT_PHASE_LATENCIES.toString())));
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
