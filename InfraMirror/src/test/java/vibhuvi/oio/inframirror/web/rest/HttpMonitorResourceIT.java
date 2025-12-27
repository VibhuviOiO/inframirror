package vibhuvi.oio.inframirror.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static vibhuvi.oio.inframirror.domain.HttpMonitorAsserts.*;
import static vibhuvi.oio.inframirror.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
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
import vibhuvi.oio.inframirror.domain.HttpMonitor;
import vibhuvi.oio.inframirror.repository.HttpMonitorRepository;
import vibhuvi.oio.inframirror.service.dto.HttpMonitorDTO;
import vibhuvi.oio.inframirror.service.mapper.HttpMonitorMapper;

/**
 * Integration tests for the {@link HttpMonitorResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class HttpMonitorResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_METHOD = "AAAAAAAAAA";
    private static final String UPDATED_METHOD = "BBBBBBBBBB";

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static JsonNode DEFAULT_HEADERS;
    private static JsonNode UPDATED_HEADERS;

    private static JsonNode DEFAULT_BODY;
    private static JsonNode UPDATED_BODY;

    private static final Integer DEFAULT_INTERVAL_SECONDS = 1;
    private static final Integer UPDATED_INTERVAL_SECONDS = 2;
    private static final Integer SMALLER_INTERVAL_SECONDS = 1 - 1;

    private static final Integer DEFAULT_TIMEOUT_SECONDS = 1;
    private static final Integer UPDATED_TIMEOUT_SECONDS = 2;
    private static final Integer SMALLER_TIMEOUT_SECONDS = 1 - 1;

    private static final Integer DEFAULT_RETRY_COUNT = 1;
    private static final Integer UPDATED_RETRY_COUNT = 2;
    private static final Integer SMALLER_RETRY_COUNT = 1 - 1;

    private static final Integer DEFAULT_RETRY_DELAY_SECONDS = 1;
    private static final Integer UPDATED_RETRY_DELAY_SECONDS = 2;
    private static final Integer SMALLER_RETRY_DELAY_SECONDS = 1 - 1;

    private static final Integer DEFAULT_RESPONSE_TIME_WARNING_MS = 1;
    private static final Integer UPDATED_RESPONSE_TIME_WARNING_MS = 2;
    private static final Integer SMALLER_RESPONSE_TIME_WARNING_MS = 1 - 1;

    private static final Integer DEFAULT_RESPONSE_TIME_CRITICAL_MS = 1;
    private static final Integer UPDATED_RESPONSE_TIME_CRITICAL_MS = 2;
    private static final Integer SMALLER_RESPONSE_TIME_CRITICAL_MS = 1 - 1;

    private static final Float DEFAULT_UPTIME_WARNING_PERCENT = 1F;
    private static final Float UPDATED_UPTIME_WARNING_PERCENT = 2F;
    private static final Float SMALLER_UPTIME_WARNING_PERCENT = 1F - 1F;

    private static final Float DEFAULT_UPTIME_CRITICAL_PERCENT = 1F;
    private static final Float UPDATED_UPTIME_CRITICAL_PERCENT = 2F;
    private static final Float SMALLER_UPTIME_CRITICAL_PERCENT = 1F - 1F;

    private static final Boolean DEFAULT_INCLUDE_RESPONSE_BODY = false;
    private static final Boolean UPDATED_INCLUDE_RESPONSE_BODY = true;

    private static final Integer DEFAULT_RESEND_NOTIFICATION_COUNT = 1;
    private static final Integer UPDATED_RESEND_NOTIFICATION_COUNT = 2;
    private static final Integer SMALLER_RESEND_NOTIFICATION_COUNT = 1 - 1;

    private static final Integer DEFAULT_CERTIFICATE_EXPIRY_DAYS = 1;
    private static final Integer UPDATED_CERTIFICATE_EXPIRY_DAYS = 2;
    private static final Integer SMALLER_CERTIFICATE_EXPIRY_DAYS = 1 - 1;

    private static final Boolean DEFAULT_IGNORE_TLS_ERROR = false;
    private static final Boolean UPDATED_IGNORE_TLS_ERROR = true;

    private static final Boolean DEFAULT_CHECK_SSL_CERTIFICATE = false;
    private static final Boolean UPDATED_CHECK_SSL_CERTIFICATE = true;

    private static final Boolean DEFAULT_CHECK_DNS_RESOLUTION = false;
    private static final Boolean UPDATED_CHECK_DNS_RESOLUTION = true;

    private static final Boolean DEFAULT_UPSIDE_DOWN_MODE = false;
    private static final Boolean UPDATED_UPSIDE_DOWN_MODE = true;

    private static final Integer DEFAULT_MAX_REDIRECTS = 1;
    private static final Integer UPDATED_MAX_REDIRECTS = 2;
    private static final Integer SMALLER_MAX_REDIRECTS = 1 - 1;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_TAGS = "AAAAAAAAAA";
    private static final String UPDATED_TAGS = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ENABLED = false;
    private static final Boolean UPDATED_ENABLED = true;

    private static final String DEFAULT_EXPECTED_STATUS_CODES = "AAAAAAAAAA";
    private static final String UPDATED_EXPECTED_STATUS_CODES = "BBBBBBBBBB";

    private static final Integer DEFAULT_PERFORMANCE_BUDGET_MS = 1;
    private static final Integer UPDATED_PERFORMANCE_BUDGET_MS = 2;
    private static final Integer SMALLER_PERFORMANCE_BUDGET_MS = 1 - 1;

    private static final Integer DEFAULT_SIZE_BUDGET_KB = 1;
    private static final Integer UPDATED_SIZE_BUDGET_KB = 2;
    private static final Integer SMALLER_SIZE_BUDGET_KB = 1 - 1;

    private static final String ENTITY_API_URL = "/api/http-monitors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/http-monitors/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    static {
        try {
            ObjectMapper mapper = new ObjectMapper();
            DEFAULT_HEADERS = mapper.readTree("{\"key\":\"value\"}");
            UPDATED_HEADERS = mapper.readTree("{\"key\":\"updated\"}");
            DEFAULT_BODY = mapper.readTree("{\"data\":\"test\"}");
            UPDATED_BODY = mapper.readTree("{\"data\":\"updated\"}");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Autowired
    private HttpMonitorRepository httpMonitorRepository;

    @Autowired
    private HttpMonitorMapper httpMonitorMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restHttpMonitorMockMvc;

    private HttpMonitor httpMonitor;

    private HttpMonitor insertedHttpMonitor;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HttpMonitor createEntity() {
        return new HttpMonitor()
            .name(DEFAULT_NAME)
            .method(DEFAULT_METHOD)
            .type(DEFAULT_TYPE)
            .url(DEFAULT_URL)
            .headers(DEFAULT_HEADERS)
            .body(DEFAULT_BODY)
            .intervalSeconds(DEFAULT_INTERVAL_SECONDS)
            .timeoutSeconds(DEFAULT_TIMEOUT_SECONDS)
            .retryCount(DEFAULT_RETRY_COUNT)
            .retryDelaySeconds(DEFAULT_RETRY_DELAY_SECONDS)
            .responseTimeWarningMs(DEFAULT_RESPONSE_TIME_WARNING_MS)
            .responseTimeCriticalMs(DEFAULT_RESPONSE_TIME_CRITICAL_MS)
            .uptimeWarningPercent(DEFAULT_UPTIME_WARNING_PERCENT)
            .uptimeCriticalPercent(DEFAULT_UPTIME_CRITICAL_PERCENT)
            .includeResponseBody(DEFAULT_INCLUDE_RESPONSE_BODY)
            .resendNotificationCount(DEFAULT_RESEND_NOTIFICATION_COUNT)
            .certificateExpiryDays(DEFAULT_CERTIFICATE_EXPIRY_DAYS)
            .ignoreTlsError(DEFAULT_IGNORE_TLS_ERROR)
            .checkSslCertificate(DEFAULT_CHECK_SSL_CERTIFICATE)
            .checkDnsResolution(DEFAULT_CHECK_DNS_RESOLUTION)
            .upsideDownMode(DEFAULT_UPSIDE_DOWN_MODE)
            .maxRedirects(DEFAULT_MAX_REDIRECTS)
            .description(DEFAULT_DESCRIPTION)
            .tags(DEFAULT_TAGS)
            .enabled(DEFAULT_ENABLED)
            .expectedStatusCodes(DEFAULT_EXPECTED_STATUS_CODES)
            .performanceBudgetMs(DEFAULT_PERFORMANCE_BUDGET_MS)
            .sizeBudgetKb(DEFAULT_SIZE_BUDGET_KB);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HttpMonitor createUpdatedEntity() {
        return new HttpMonitor()
            .name(UPDATED_NAME)
            .method(UPDATED_METHOD)
            .type(UPDATED_TYPE)
            .url(UPDATED_URL)
            .headers(UPDATED_HEADERS)
            .body(UPDATED_BODY)
            .intervalSeconds(UPDATED_INTERVAL_SECONDS)
            .timeoutSeconds(UPDATED_TIMEOUT_SECONDS)
            .retryCount(UPDATED_RETRY_COUNT)
            .retryDelaySeconds(UPDATED_RETRY_DELAY_SECONDS)
            .responseTimeWarningMs(UPDATED_RESPONSE_TIME_WARNING_MS)
            .responseTimeCriticalMs(UPDATED_RESPONSE_TIME_CRITICAL_MS)
            .uptimeWarningPercent(UPDATED_UPTIME_WARNING_PERCENT)
            .uptimeCriticalPercent(UPDATED_UPTIME_CRITICAL_PERCENT)
            .includeResponseBody(UPDATED_INCLUDE_RESPONSE_BODY)
            .resendNotificationCount(UPDATED_RESEND_NOTIFICATION_COUNT)
            .certificateExpiryDays(UPDATED_CERTIFICATE_EXPIRY_DAYS)
            .ignoreTlsError(UPDATED_IGNORE_TLS_ERROR)
            .checkSslCertificate(UPDATED_CHECK_SSL_CERTIFICATE)
            .checkDnsResolution(UPDATED_CHECK_DNS_RESOLUTION)
            .upsideDownMode(UPDATED_UPSIDE_DOWN_MODE)
            .maxRedirects(UPDATED_MAX_REDIRECTS)
            .description(UPDATED_DESCRIPTION)
            .tags(UPDATED_TAGS)
            .enabled(UPDATED_ENABLED)
            .expectedStatusCodes(UPDATED_EXPECTED_STATUS_CODES)
            .performanceBudgetMs(UPDATED_PERFORMANCE_BUDGET_MS)
            .sizeBudgetKb(UPDATED_SIZE_BUDGET_KB);
    }

    @BeforeEach
    void initTest() {
        httpMonitor = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedHttpMonitor != null) {
            httpMonitorRepository.delete(insertedHttpMonitor);
            insertedHttpMonitor = null;
        }
    }

    @Test
    @Transactional
    void createHttpMonitor() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the HttpMonitor
        HttpMonitorDTO httpMonitorDTO = httpMonitorMapper.toDto(httpMonitor);
        var returnedHttpMonitorDTO = om.readValue(
            restHttpMonitorMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(httpMonitorDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            HttpMonitorDTO.class
        );

        // Validate the HttpMonitor in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedHttpMonitor = httpMonitorMapper.toEntity(returnedHttpMonitorDTO);
        assertHttpMonitorUpdatableFieldsEquals(returnedHttpMonitor, getPersistedHttpMonitor(returnedHttpMonitor));

        insertedHttpMonitor = returnedHttpMonitor;
    }

    @Test
    @Transactional
    void createHttpMonitorWithExistingId() throws Exception {
        // Create the HttpMonitor with an existing ID
        httpMonitor.setId(1L);
        HttpMonitorDTO httpMonitorDTO = httpMonitorMapper.toDto(httpMonitor);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restHttpMonitorMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(httpMonitorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HttpMonitor in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        httpMonitor.setName(null);

        // Create the HttpMonitor, which fails.
        HttpMonitorDTO httpMonitorDTO = httpMonitorMapper.toDto(httpMonitor);

        restHttpMonitorMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(httpMonitorDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMethodIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        httpMonitor.setMethod(null);

        // Create the HttpMonitor, which fails.
        HttpMonitorDTO httpMonitorDTO = httpMonitorMapper.toDto(httpMonitor);

        restHttpMonitorMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(httpMonitorDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        httpMonitor.setType(null);

        // Create the HttpMonitor, which fails.
        HttpMonitorDTO httpMonitorDTO = httpMonitorMapper.toDto(httpMonitor);

        restHttpMonitorMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(httpMonitorDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIntervalSecondsIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        httpMonitor.setIntervalSeconds(null);

        // Create the HttpMonitor, which fails.
        HttpMonitorDTO httpMonitorDTO = httpMonitorMapper.toDto(httpMonitor);

        restHttpMonitorMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(httpMonitorDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTimeoutSecondsIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        httpMonitor.setTimeoutSeconds(null);

        // Create the HttpMonitor, which fails.
        HttpMonitorDTO httpMonitorDTO = httpMonitorMapper.toDto(httpMonitor);

        restHttpMonitorMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(httpMonitorDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRetryCountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        httpMonitor.setRetryCount(null);

        // Create the HttpMonitor, which fails.
        HttpMonitorDTO httpMonitorDTO = httpMonitorMapper.toDto(httpMonitor);

        restHttpMonitorMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(httpMonitorDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRetryDelaySecondsIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        httpMonitor.setRetryDelaySeconds(null);

        // Create the HttpMonitor, which fails.
        HttpMonitorDTO httpMonitorDTO = httpMonitorMapper.toDto(httpMonitor);

        restHttpMonitorMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(httpMonitorDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllHttpMonitors() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList
        restHttpMonitorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(httpMonitor.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].method").value(hasItem(DEFAULT_METHOD)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].headers").value(hasItem(DEFAULT_HEADERS)))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY)))
            .andExpect(jsonPath("$.[*].intervalSeconds").value(hasItem(DEFAULT_INTERVAL_SECONDS)))
            .andExpect(jsonPath("$.[*].timeoutSeconds").value(hasItem(DEFAULT_TIMEOUT_SECONDS)))
            .andExpect(jsonPath("$.[*].retryCount").value(hasItem(DEFAULT_RETRY_COUNT)))
            .andExpect(jsonPath("$.[*].retryDelaySeconds").value(hasItem(DEFAULT_RETRY_DELAY_SECONDS)))
            .andExpect(jsonPath("$.[*].responseTimeWarningMs").value(hasItem(DEFAULT_RESPONSE_TIME_WARNING_MS)))
            .andExpect(jsonPath("$.[*].responseTimeCriticalMs").value(hasItem(DEFAULT_RESPONSE_TIME_CRITICAL_MS)))
            .andExpect(jsonPath("$.[*].uptimeWarningPercent").value(hasItem(DEFAULT_UPTIME_WARNING_PERCENT.doubleValue())))
            .andExpect(jsonPath("$.[*].uptimeCriticalPercent").value(hasItem(DEFAULT_UPTIME_CRITICAL_PERCENT.doubleValue())))
            .andExpect(jsonPath("$.[*].includeResponseBody").value(hasItem(DEFAULT_INCLUDE_RESPONSE_BODY)))
            .andExpect(jsonPath("$.[*].resendNotificationCount").value(hasItem(DEFAULT_RESEND_NOTIFICATION_COUNT)))
            .andExpect(jsonPath("$.[*].certificateExpiryDays").value(hasItem(DEFAULT_CERTIFICATE_EXPIRY_DAYS)))
            .andExpect(jsonPath("$.[*].ignoreTlsError").value(hasItem(DEFAULT_IGNORE_TLS_ERROR)))
            .andExpect(jsonPath("$.[*].checkSslCertificate").value(hasItem(DEFAULT_CHECK_SSL_CERTIFICATE)))
            .andExpect(jsonPath("$.[*].checkDnsResolution").value(hasItem(DEFAULT_CHECK_DNS_RESOLUTION)))
            .andExpect(jsonPath("$.[*].upsideDownMode").value(hasItem(DEFAULT_UPSIDE_DOWN_MODE)))
            .andExpect(jsonPath("$.[*].maxRedirects").value(hasItem(DEFAULT_MAX_REDIRECTS)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].tags").value(hasItem(DEFAULT_TAGS)))
            .andExpect(jsonPath("$.[*].enabled").value(hasItem(DEFAULT_ENABLED)))
            .andExpect(jsonPath("$.[*].expectedStatusCodes").value(hasItem(DEFAULT_EXPECTED_STATUS_CODES)))
            .andExpect(jsonPath("$.[*].performanceBudgetMs").value(hasItem(DEFAULT_PERFORMANCE_BUDGET_MS)))
            .andExpect(jsonPath("$.[*].sizeBudgetKb").value(hasItem(DEFAULT_SIZE_BUDGET_KB)));
    }

    @Test
    @Transactional
    void getHttpMonitor() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get the httpMonitor
        restHttpMonitorMockMvc
            .perform(get(ENTITY_API_URL_ID, httpMonitor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(httpMonitor.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.method").value(DEFAULT_METHOD))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL))
            .andExpect(jsonPath("$.headers").value(DEFAULT_HEADERS))
            .andExpect(jsonPath("$.body").value(DEFAULT_BODY))
            .andExpect(jsonPath("$.intervalSeconds").value(DEFAULT_INTERVAL_SECONDS))
            .andExpect(jsonPath("$.timeoutSeconds").value(DEFAULT_TIMEOUT_SECONDS))
            .andExpect(jsonPath("$.retryCount").value(DEFAULT_RETRY_COUNT))
            .andExpect(jsonPath("$.retryDelaySeconds").value(DEFAULT_RETRY_DELAY_SECONDS))
            .andExpect(jsonPath("$.responseTimeWarningMs").value(DEFAULT_RESPONSE_TIME_WARNING_MS))
            .andExpect(jsonPath("$.responseTimeCriticalMs").value(DEFAULT_RESPONSE_TIME_CRITICAL_MS))
            .andExpect(jsonPath("$.uptimeWarningPercent").value(DEFAULT_UPTIME_WARNING_PERCENT.doubleValue()))
            .andExpect(jsonPath("$.uptimeCriticalPercent").value(DEFAULT_UPTIME_CRITICAL_PERCENT.doubleValue()))
            .andExpect(jsonPath("$.includeResponseBody").value(DEFAULT_INCLUDE_RESPONSE_BODY))
            .andExpect(jsonPath("$.resendNotificationCount").value(DEFAULT_RESEND_NOTIFICATION_COUNT))
            .andExpect(jsonPath("$.certificateExpiryDays").value(DEFAULT_CERTIFICATE_EXPIRY_DAYS))
            .andExpect(jsonPath("$.ignoreTlsError").value(DEFAULT_IGNORE_TLS_ERROR))
            .andExpect(jsonPath("$.checkSslCertificate").value(DEFAULT_CHECK_SSL_CERTIFICATE))
            .andExpect(jsonPath("$.checkDnsResolution").value(DEFAULT_CHECK_DNS_RESOLUTION))
            .andExpect(jsonPath("$.upsideDownMode").value(DEFAULT_UPSIDE_DOWN_MODE))
            .andExpect(jsonPath("$.maxRedirects").value(DEFAULT_MAX_REDIRECTS))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.tags").value(DEFAULT_TAGS))
            .andExpect(jsonPath("$.enabled").value(DEFAULT_ENABLED))
            .andExpect(jsonPath("$.expectedStatusCodes").value(DEFAULT_EXPECTED_STATUS_CODES))
            .andExpect(jsonPath("$.performanceBudgetMs").value(DEFAULT_PERFORMANCE_BUDGET_MS))
            .andExpect(jsonPath("$.sizeBudgetKb").value(DEFAULT_SIZE_BUDGET_KB));
    }

    @Test
    @Transactional
    void getHttpMonitorsByIdFiltering() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        Long id = httpMonitor.getId();

        defaultHttpMonitorFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultHttpMonitorFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultHttpMonitorFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where name equals to
        defaultHttpMonitorFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where name in
        defaultHttpMonitorFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where name is not null
        defaultHttpMonitorFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where name contains
        defaultHttpMonitorFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where name does not contain
        defaultHttpMonitorFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByMethodIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where method equals to
        defaultHttpMonitorFiltering("method.equals=" + DEFAULT_METHOD, "method.equals=" + UPDATED_METHOD);
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByMethodIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where method in
        defaultHttpMonitorFiltering("method.in=" + DEFAULT_METHOD + "," + UPDATED_METHOD, "method.in=" + UPDATED_METHOD);
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByMethodIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where method is not null
        defaultHttpMonitorFiltering("method.specified=true", "method.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByMethodContainsSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where method contains
        defaultHttpMonitorFiltering("method.contains=" + DEFAULT_METHOD, "method.contains=" + UPDATED_METHOD);
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByMethodNotContainsSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where method does not contain
        defaultHttpMonitorFiltering("method.doesNotContain=" + UPDATED_METHOD, "method.doesNotContain=" + DEFAULT_METHOD);
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where type equals to
        defaultHttpMonitorFiltering("type.equals=" + DEFAULT_TYPE, "type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where type in
        defaultHttpMonitorFiltering("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE, "type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where type is not null
        defaultHttpMonitorFiltering("type.specified=true", "type.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where type contains
        defaultHttpMonitorFiltering("type.contains=" + DEFAULT_TYPE, "type.contains=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where type does not contain
        defaultHttpMonitorFiltering("type.doesNotContain=" + UPDATED_TYPE, "type.doesNotContain=" + DEFAULT_TYPE);
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByIntervalSecondsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where intervalSeconds equals to
        defaultHttpMonitorFiltering(
            "intervalSeconds.equals=" + DEFAULT_INTERVAL_SECONDS,
            "intervalSeconds.equals=" + UPDATED_INTERVAL_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByIntervalSecondsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where intervalSeconds in
        defaultHttpMonitorFiltering(
            "intervalSeconds.in=" + DEFAULT_INTERVAL_SECONDS + "," + UPDATED_INTERVAL_SECONDS,
            "intervalSeconds.in=" + UPDATED_INTERVAL_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByIntervalSecondsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where intervalSeconds is not null
        defaultHttpMonitorFiltering("intervalSeconds.specified=true", "intervalSeconds.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByIntervalSecondsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where intervalSeconds is greater than or equal to
        defaultHttpMonitorFiltering(
            "intervalSeconds.greaterThanOrEqual=" + DEFAULT_INTERVAL_SECONDS,
            "intervalSeconds.greaterThanOrEqual=" + UPDATED_INTERVAL_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByIntervalSecondsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where intervalSeconds is less than or equal to
        defaultHttpMonitorFiltering(
            "intervalSeconds.lessThanOrEqual=" + DEFAULT_INTERVAL_SECONDS,
            "intervalSeconds.lessThanOrEqual=" + SMALLER_INTERVAL_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByIntervalSecondsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where intervalSeconds is less than
        defaultHttpMonitorFiltering(
            "intervalSeconds.lessThan=" + UPDATED_INTERVAL_SECONDS,
            "intervalSeconds.lessThan=" + DEFAULT_INTERVAL_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByIntervalSecondsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where intervalSeconds is greater than
        defaultHttpMonitorFiltering(
            "intervalSeconds.greaterThan=" + SMALLER_INTERVAL_SECONDS,
            "intervalSeconds.greaterThan=" + DEFAULT_INTERVAL_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByTimeoutSecondsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where timeoutSeconds equals to
        defaultHttpMonitorFiltering("timeoutSeconds.equals=" + DEFAULT_TIMEOUT_SECONDS, "timeoutSeconds.equals=" + UPDATED_TIMEOUT_SECONDS);
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByTimeoutSecondsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where timeoutSeconds in
        defaultHttpMonitorFiltering(
            "timeoutSeconds.in=" + DEFAULT_TIMEOUT_SECONDS + "," + UPDATED_TIMEOUT_SECONDS,
            "timeoutSeconds.in=" + UPDATED_TIMEOUT_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByTimeoutSecondsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where timeoutSeconds is not null
        defaultHttpMonitorFiltering("timeoutSeconds.specified=true", "timeoutSeconds.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByTimeoutSecondsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where timeoutSeconds is greater than or equal to
        defaultHttpMonitorFiltering(
            "timeoutSeconds.greaterThanOrEqual=" + DEFAULT_TIMEOUT_SECONDS,
            "timeoutSeconds.greaterThanOrEqual=" + UPDATED_TIMEOUT_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByTimeoutSecondsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where timeoutSeconds is less than or equal to
        defaultHttpMonitorFiltering(
            "timeoutSeconds.lessThanOrEqual=" + DEFAULT_TIMEOUT_SECONDS,
            "timeoutSeconds.lessThanOrEqual=" + SMALLER_TIMEOUT_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByTimeoutSecondsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where timeoutSeconds is less than
        defaultHttpMonitorFiltering(
            "timeoutSeconds.lessThan=" + UPDATED_TIMEOUT_SECONDS,
            "timeoutSeconds.lessThan=" + DEFAULT_TIMEOUT_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByTimeoutSecondsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where timeoutSeconds is greater than
        defaultHttpMonitorFiltering(
            "timeoutSeconds.greaterThan=" + SMALLER_TIMEOUT_SECONDS,
            "timeoutSeconds.greaterThan=" + DEFAULT_TIMEOUT_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByRetryCountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where retryCount equals to
        defaultHttpMonitorFiltering("retryCount.equals=" + DEFAULT_RETRY_COUNT, "retryCount.equals=" + UPDATED_RETRY_COUNT);
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByRetryCountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where retryCount in
        defaultHttpMonitorFiltering(
            "retryCount.in=" + DEFAULT_RETRY_COUNT + "," + UPDATED_RETRY_COUNT,
            "retryCount.in=" + UPDATED_RETRY_COUNT
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByRetryCountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where retryCount is not null
        defaultHttpMonitorFiltering("retryCount.specified=true", "retryCount.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByRetryCountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where retryCount is greater than or equal to
        defaultHttpMonitorFiltering(
            "retryCount.greaterThanOrEqual=" + DEFAULT_RETRY_COUNT,
            "retryCount.greaterThanOrEqual=" + UPDATED_RETRY_COUNT
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByRetryCountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where retryCount is less than or equal to
        defaultHttpMonitorFiltering(
            "retryCount.lessThanOrEqual=" + DEFAULT_RETRY_COUNT,
            "retryCount.lessThanOrEqual=" + SMALLER_RETRY_COUNT
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByRetryCountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where retryCount is less than
        defaultHttpMonitorFiltering("retryCount.lessThan=" + UPDATED_RETRY_COUNT, "retryCount.lessThan=" + DEFAULT_RETRY_COUNT);
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByRetryCountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where retryCount is greater than
        defaultHttpMonitorFiltering("retryCount.greaterThan=" + SMALLER_RETRY_COUNT, "retryCount.greaterThan=" + DEFAULT_RETRY_COUNT);
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByRetryDelaySecondsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where retryDelaySeconds equals to
        defaultHttpMonitorFiltering(
            "retryDelaySeconds.equals=" + DEFAULT_RETRY_DELAY_SECONDS,
            "retryDelaySeconds.equals=" + UPDATED_RETRY_DELAY_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByRetryDelaySecondsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where retryDelaySeconds in
        defaultHttpMonitorFiltering(
            "retryDelaySeconds.in=" + DEFAULT_RETRY_DELAY_SECONDS + "," + UPDATED_RETRY_DELAY_SECONDS,
            "retryDelaySeconds.in=" + UPDATED_RETRY_DELAY_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByRetryDelaySecondsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where retryDelaySeconds is not null
        defaultHttpMonitorFiltering("retryDelaySeconds.specified=true", "retryDelaySeconds.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByRetryDelaySecondsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where retryDelaySeconds is greater than or equal to
        defaultHttpMonitorFiltering(
            "retryDelaySeconds.greaterThanOrEqual=" + DEFAULT_RETRY_DELAY_SECONDS,
            "retryDelaySeconds.greaterThanOrEqual=" + UPDATED_RETRY_DELAY_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByRetryDelaySecondsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where retryDelaySeconds is less than or equal to
        defaultHttpMonitorFiltering(
            "retryDelaySeconds.lessThanOrEqual=" + DEFAULT_RETRY_DELAY_SECONDS,
            "retryDelaySeconds.lessThanOrEqual=" + SMALLER_RETRY_DELAY_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByRetryDelaySecondsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where retryDelaySeconds is less than
        defaultHttpMonitorFiltering(
            "retryDelaySeconds.lessThan=" + UPDATED_RETRY_DELAY_SECONDS,
            "retryDelaySeconds.lessThan=" + DEFAULT_RETRY_DELAY_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByRetryDelaySecondsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where retryDelaySeconds is greater than
        defaultHttpMonitorFiltering(
            "retryDelaySeconds.greaterThan=" + SMALLER_RETRY_DELAY_SECONDS,
            "retryDelaySeconds.greaterThan=" + DEFAULT_RETRY_DELAY_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByResponseTimeWarningMsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where responseTimeWarningMs equals to
        defaultHttpMonitorFiltering(
            "responseTimeWarningMs.equals=" + DEFAULT_RESPONSE_TIME_WARNING_MS,
            "responseTimeWarningMs.equals=" + UPDATED_RESPONSE_TIME_WARNING_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByResponseTimeWarningMsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where responseTimeWarningMs in
        defaultHttpMonitorFiltering(
            "responseTimeWarningMs.in=" + DEFAULT_RESPONSE_TIME_WARNING_MS + "," + UPDATED_RESPONSE_TIME_WARNING_MS,
            "responseTimeWarningMs.in=" + UPDATED_RESPONSE_TIME_WARNING_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByResponseTimeWarningMsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where responseTimeWarningMs is not null
        defaultHttpMonitorFiltering("responseTimeWarningMs.specified=true", "responseTimeWarningMs.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByResponseTimeWarningMsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where responseTimeWarningMs is greater than or equal to
        defaultHttpMonitorFiltering(
            "responseTimeWarningMs.greaterThanOrEqual=" + DEFAULT_RESPONSE_TIME_WARNING_MS,
            "responseTimeWarningMs.greaterThanOrEqual=" + UPDATED_RESPONSE_TIME_WARNING_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByResponseTimeWarningMsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where responseTimeWarningMs is less than or equal to
        defaultHttpMonitorFiltering(
            "responseTimeWarningMs.lessThanOrEqual=" + DEFAULT_RESPONSE_TIME_WARNING_MS,
            "responseTimeWarningMs.lessThanOrEqual=" + SMALLER_RESPONSE_TIME_WARNING_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByResponseTimeWarningMsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where responseTimeWarningMs is less than
        defaultHttpMonitorFiltering(
            "responseTimeWarningMs.lessThan=" + UPDATED_RESPONSE_TIME_WARNING_MS,
            "responseTimeWarningMs.lessThan=" + DEFAULT_RESPONSE_TIME_WARNING_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByResponseTimeWarningMsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where responseTimeWarningMs is greater than
        defaultHttpMonitorFiltering(
            "responseTimeWarningMs.greaterThan=" + SMALLER_RESPONSE_TIME_WARNING_MS,
            "responseTimeWarningMs.greaterThan=" + DEFAULT_RESPONSE_TIME_WARNING_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByResponseTimeCriticalMsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where responseTimeCriticalMs equals to
        defaultHttpMonitorFiltering(
            "responseTimeCriticalMs.equals=" + DEFAULT_RESPONSE_TIME_CRITICAL_MS,
            "responseTimeCriticalMs.equals=" + UPDATED_RESPONSE_TIME_CRITICAL_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByResponseTimeCriticalMsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where responseTimeCriticalMs in
        defaultHttpMonitorFiltering(
            "responseTimeCriticalMs.in=" + DEFAULT_RESPONSE_TIME_CRITICAL_MS + "," + UPDATED_RESPONSE_TIME_CRITICAL_MS,
            "responseTimeCriticalMs.in=" + UPDATED_RESPONSE_TIME_CRITICAL_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByResponseTimeCriticalMsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where responseTimeCriticalMs is not null
        defaultHttpMonitorFiltering("responseTimeCriticalMs.specified=true", "responseTimeCriticalMs.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByResponseTimeCriticalMsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where responseTimeCriticalMs is greater than or equal to
        defaultHttpMonitorFiltering(
            "responseTimeCriticalMs.greaterThanOrEqual=" + DEFAULT_RESPONSE_TIME_CRITICAL_MS,
            "responseTimeCriticalMs.greaterThanOrEqual=" + UPDATED_RESPONSE_TIME_CRITICAL_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByResponseTimeCriticalMsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where responseTimeCriticalMs is less than or equal to
        defaultHttpMonitorFiltering(
            "responseTimeCriticalMs.lessThanOrEqual=" + DEFAULT_RESPONSE_TIME_CRITICAL_MS,
            "responseTimeCriticalMs.lessThanOrEqual=" + SMALLER_RESPONSE_TIME_CRITICAL_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByResponseTimeCriticalMsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where responseTimeCriticalMs is less than
        defaultHttpMonitorFiltering(
            "responseTimeCriticalMs.lessThan=" + UPDATED_RESPONSE_TIME_CRITICAL_MS,
            "responseTimeCriticalMs.lessThan=" + DEFAULT_RESPONSE_TIME_CRITICAL_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByResponseTimeCriticalMsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where responseTimeCriticalMs is greater than
        defaultHttpMonitorFiltering(
            "responseTimeCriticalMs.greaterThan=" + SMALLER_RESPONSE_TIME_CRITICAL_MS,
            "responseTimeCriticalMs.greaterThan=" + DEFAULT_RESPONSE_TIME_CRITICAL_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByUptimeWarningPercentIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where uptimeWarningPercent equals to
        defaultHttpMonitorFiltering(
            "uptimeWarningPercent.equals=" + DEFAULT_UPTIME_WARNING_PERCENT,
            "uptimeWarningPercent.equals=" + UPDATED_UPTIME_WARNING_PERCENT
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByUptimeWarningPercentIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where uptimeWarningPercent in
        defaultHttpMonitorFiltering(
            "uptimeWarningPercent.in=" + DEFAULT_UPTIME_WARNING_PERCENT + "," + UPDATED_UPTIME_WARNING_PERCENT,
            "uptimeWarningPercent.in=" + UPDATED_UPTIME_WARNING_PERCENT
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByUptimeWarningPercentIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where uptimeWarningPercent is not null
        defaultHttpMonitorFiltering("uptimeWarningPercent.specified=true", "uptimeWarningPercent.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByUptimeWarningPercentIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where uptimeWarningPercent is greater than or equal to
        defaultHttpMonitorFiltering(
            "uptimeWarningPercent.greaterThanOrEqual=" + DEFAULT_UPTIME_WARNING_PERCENT,
            "uptimeWarningPercent.greaterThanOrEqual=" + UPDATED_UPTIME_WARNING_PERCENT
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByUptimeWarningPercentIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where uptimeWarningPercent is less than or equal to
        defaultHttpMonitorFiltering(
            "uptimeWarningPercent.lessThanOrEqual=" + DEFAULT_UPTIME_WARNING_PERCENT,
            "uptimeWarningPercent.lessThanOrEqual=" + SMALLER_UPTIME_WARNING_PERCENT
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByUptimeWarningPercentIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where uptimeWarningPercent is less than
        defaultHttpMonitorFiltering(
            "uptimeWarningPercent.lessThan=" + UPDATED_UPTIME_WARNING_PERCENT,
            "uptimeWarningPercent.lessThan=" + DEFAULT_UPTIME_WARNING_PERCENT
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByUptimeWarningPercentIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where uptimeWarningPercent is greater than
        defaultHttpMonitorFiltering(
            "uptimeWarningPercent.greaterThan=" + SMALLER_UPTIME_WARNING_PERCENT,
            "uptimeWarningPercent.greaterThan=" + DEFAULT_UPTIME_WARNING_PERCENT
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByUptimeCriticalPercentIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where uptimeCriticalPercent equals to
        defaultHttpMonitorFiltering(
            "uptimeCriticalPercent.equals=" + DEFAULT_UPTIME_CRITICAL_PERCENT,
            "uptimeCriticalPercent.equals=" + UPDATED_UPTIME_CRITICAL_PERCENT
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByUptimeCriticalPercentIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where uptimeCriticalPercent in
        defaultHttpMonitorFiltering(
            "uptimeCriticalPercent.in=" + DEFAULT_UPTIME_CRITICAL_PERCENT + "," + UPDATED_UPTIME_CRITICAL_PERCENT,
            "uptimeCriticalPercent.in=" + UPDATED_UPTIME_CRITICAL_PERCENT
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByUptimeCriticalPercentIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where uptimeCriticalPercent is not null
        defaultHttpMonitorFiltering("uptimeCriticalPercent.specified=true", "uptimeCriticalPercent.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByUptimeCriticalPercentIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where uptimeCriticalPercent is greater than or equal to
        defaultHttpMonitorFiltering(
            "uptimeCriticalPercent.greaterThanOrEqual=" + DEFAULT_UPTIME_CRITICAL_PERCENT,
            "uptimeCriticalPercent.greaterThanOrEqual=" + UPDATED_UPTIME_CRITICAL_PERCENT
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByUptimeCriticalPercentIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where uptimeCriticalPercent is less than or equal to
        defaultHttpMonitorFiltering(
            "uptimeCriticalPercent.lessThanOrEqual=" + DEFAULT_UPTIME_CRITICAL_PERCENT,
            "uptimeCriticalPercent.lessThanOrEqual=" + SMALLER_UPTIME_CRITICAL_PERCENT
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByUptimeCriticalPercentIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where uptimeCriticalPercent is less than
        defaultHttpMonitorFiltering(
            "uptimeCriticalPercent.lessThan=" + UPDATED_UPTIME_CRITICAL_PERCENT,
            "uptimeCriticalPercent.lessThan=" + DEFAULT_UPTIME_CRITICAL_PERCENT
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByUptimeCriticalPercentIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where uptimeCriticalPercent is greater than
        defaultHttpMonitorFiltering(
            "uptimeCriticalPercent.greaterThan=" + SMALLER_UPTIME_CRITICAL_PERCENT,
            "uptimeCriticalPercent.greaterThan=" + DEFAULT_UPTIME_CRITICAL_PERCENT
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByIncludeResponseBodyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where includeResponseBody equals to
        defaultHttpMonitorFiltering(
            "includeResponseBody.equals=" + DEFAULT_INCLUDE_RESPONSE_BODY,
            "includeResponseBody.equals=" + UPDATED_INCLUDE_RESPONSE_BODY
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByIncludeResponseBodyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where includeResponseBody in
        defaultHttpMonitorFiltering(
            "includeResponseBody.in=" + DEFAULT_INCLUDE_RESPONSE_BODY + "," + UPDATED_INCLUDE_RESPONSE_BODY,
            "includeResponseBody.in=" + UPDATED_INCLUDE_RESPONSE_BODY
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByIncludeResponseBodyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where includeResponseBody is not null
        defaultHttpMonitorFiltering("includeResponseBody.specified=true", "includeResponseBody.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByResendNotificationCountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where resendNotificationCount equals to
        defaultHttpMonitorFiltering(
            "resendNotificationCount.equals=" + DEFAULT_RESEND_NOTIFICATION_COUNT,
            "resendNotificationCount.equals=" + UPDATED_RESEND_NOTIFICATION_COUNT
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByResendNotificationCountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where resendNotificationCount in
        defaultHttpMonitorFiltering(
            "resendNotificationCount.in=" + DEFAULT_RESEND_NOTIFICATION_COUNT + "," + UPDATED_RESEND_NOTIFICATION_COUNT,
            "resendNotificationCount.in=" + UPDATED_RESEND_NOTIFICATION_COUNT
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByResendNotificationCountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where resendNotificationCount is not null
        defaultHttpMonitorFiltering("resendNotificationCount.specified=true", "resendNotificationCount.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByResendNotificationCountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where resendNotificationCount is greater than or equal to
        defaultHttpMonitorFiltering(
            "resendNotificationCount.greaterThanOrEqual=" + DEFAULT_RESEND_NOTIFICATION_COUNT,
            "resendNotificationCount.greaterThanOrEqual=" + UPDATED_RESEND_NOTIFICATION_COUNT
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByResendNotificationCountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where resendNotificationCount is less than or equal to
        defaultHttpMonitorFiltering(
            "resendNotificationCount.lessThanOrEqual=" + DEFAULT_RESEND_NOTIFICATION_COUNT,
            "resendNotificationCount.lessThanOrEqual=" + SMALLER_RESEND_NOTIFICATION_COUNT
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByResendNotificationCountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where resendNotificationCount is less than
        defaultHttpMonitorFiltering(
            "resendNotificationCount.lessThan=" + UPDATED_RESEND_NOTIFICATION_COUNT,
            "resendNotificationCount.lessThan=" + DEFAULT_RESEND_NOTIFICATION_COUNT
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByResendNotificationCountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where resendNotificationCount is greater than
        defaultHttpMonitorFiltering(
            "resendNotificationCount.greaterThan=" + SMALLER_RESEND_NOTIFICATION_COUNT,
            "resendNotificationCount.greaterThan=" + DEFAULT_RESEND_NOTIFICATION_COUNT
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByCertificateExpiryDaysIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where certificateExpiryDays equals to
        defaultHttpMonitorFiltering(
            "certificateExpiryDays.equals=" + DEFAULT_CERTIFICATE_EXPIRY_DAYS,
            "certificateExpiryDays.equals=" + UPDATED_CERTIFICATE_EXPIRY_DAYS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByCertificateExpiryDaysIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where certificateExpiryDays in
        defaultHttpMonitorFiltering(
            "certificateExpiryDays.in=" + DEFAULT_CERTIFICATE_EXPIRY_DAYS + "," + UPDATED_CERTIFICATE_EXPIRY_DAYS,
            "certificateExpiryDays.in=" + UPDATED_CERTIFICATE_EXPIRY_DAYS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByCertificateExpiryDaysIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where certificateExpiryDays is not null
        defaultHttpMonitorFiltering("certificateExpiryDays.specified=true", "certificateExpiryDays.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByCertificateExpiryDaysIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where certificateExpiryDays is greater than or equal to
        defaultHttpMonitorFiltering(
            "certificateExpiryDays.greaterThanOrEqual=" + DEFAULT_CERTIFICATE_EXPIRY_DAYS,
            "certificateExpiryDays.greaterThanOrEqual=" + UPDATED_CERTIFICATE_EXPIRY_DAYS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByCertificateExpiryDaysIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where certificateExpiryDays is less than or equal to
        defaultHttpMonitorFiltering(
            "certificateExpiryDays.lessThanOrEqual=" + DEFAULT_CERTIFICATE_EXPIRY_DAYS,
            "certificateExpiryDays.lessThanOrEqual=" + SMALLER_CERTIFICATE_EXPIRY_DAYS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByCertificateExpiryDaysIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where certificateExpiryDays is less than
        defaultHttpMonitorFiltering(
            "certificateExpiryDays.lessThan=" + UPDATED_CERTIFICATE_EXPIRY_DAYS,
            "certificateExpiryDays.lessThan=" + DEFAULT_CERTIFICATE_EXPIRY_DAYS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByCertificateExpiryDaysIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where certificateExpiryDays is greater than
        defaultHttpMonitorFiltering(
            "certificateExpiryDays.greaterThan=" + SMALLER_CERTIFICATE_EXPIRY_DAYS,
            "certificateExpiryDays.greaterThan=" + DEFAULT_CERTIFICATE_EXPIRY_DAYS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByIgnoreTlsErrorIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where ignoreTlsError equals to
        defaultHttpMonitorFiltering(
            "ignoreTlsError.equals=" + DEFAULT_IGNORE_TLS_ERROR,
            "ignoreTlsError.equals=" + UPDATED_IGNORE_TLS_ERROR
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByIgnoreTlsErrorIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where ignoreTlsError in
        defaultHttpMonitorFiltering(
            "ignoreTlsError.in=" + DEFAULT_IGNORE_TLS_ERROR + "," + UPDATED_IGNORE_TLS_ERROR,
            "ignoreTlsError.in=" + UPDATED_IGNORE_TLS_ERROR
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByIgnoreTlsErrorIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where ignoreTlsError is not null
        defaultHttpMonitorFiltering("ignoreTlsError.specified=true", "ignoreTlsError.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByCheckSslCertificateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where checkSslCertificate equals to
        defaultHttpMonitorFiltering(
            "checkSslCertificate.equals=" + DEFAULT_CHECK_SSL_CERTIFICATE,
            "checkSslCertificate.equals=" + UPDATED_CHECK_SSL_CERTIFICATE
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByCheckSslCertificateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where checkSslCertificate in
        defaultHttpMonitorFiltering(
            "checkSslCertificate.in=" + DEFAULT_CHECK_SSL_CERTIFICATE + "," + UPDATED_CHECK_SSL_CERTIFICATE,
            "checkSslCertificate.in=" + UPDATED_CHECK_SSL_CERTIFICATE
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByCheckSslCertificateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where checkSslCertificate is not null
        defaultHttpMonitorFiltering("checkSslCertificate.specified=true", "checkSslCertificate.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByCheckDnsResolutionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where checkDnsResolution equals to
        defaultHttpMonitorFiltering(
            "checkDnsResolution.equals=" + DEFAULT_CHECK_DNS_RESOLUTION,
            "checkDnsResolution.equals=" + UPDATED_CHECK_DNS_RESOLUTION
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByCheckDnsResolutionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where checkDnsResolution in
        defaultHttpMonitorFiltering(
            "checkDnsResolution.in=" + DEFAULT_CHECK_DNS_RESOLUTION + "," + UPDATED_CHECK_DNS_RESOLUTION,
            "checkDnsResolution.in=" + UPDATED_CHECK_DNS_RESOLUTION
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByCheckDnsResolutionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where checkDnsResolution is not null
        defaultHttpMonitorFiltering("checkDnsResolution.specified=true", "checkDnsResolution.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByUpsideDownModeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where upsideDownMode equals to
        defaultHttpMonitorFiltering(
            "upsideDownMode.equals=" + DEFAULT_UPSIDE_DOWN_MODE,
            "upsideDownMode.equals=" + UPDATED_UPSIDE_DOWN_MODE
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByUpsideDownModeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where upsideDownMode in
        defaultHttpMonitorFiltering(
            "upsideDownMode.in=" + DEFAULT_UPSIDE_DOWN_MODE + "," + UPDATED_UPSIDE_DOWN_MODE,
            "upsideDownMode.in=" + UPDATED_UPSIDE_DOWN_MODE
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByUpsideDownModeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where upsideDownMode is not null
        defaultHttpMonitorFiltering("upsideDownMode.specified=true", "upsideDownMode.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByMaxRedirectsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where maxRedirects equals to
        defaultHttpMonitorFiltering("maxRedirects.equals=" + DEFAULT_MAX_REDIRECTS, "maxRedirects.equals=" + UPDATED_MAX_REDIRECTS);
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByMaxRedirectsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where maxRedirects in
        defaultHttpMonitorFiltering(
            "maxRedirects.in=" + DEFAULT_MAX_REDIRECTS + "," + UPDATED_MAX_REDIRECTS,
            "maxRedirects.in=" + UPDATED_MAX_REDIRECTS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByMaxRedirectsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where maxRedirects is not null
        defaultHttpMonitorFiltering("maxRedirects.specified=true", "maxRedirects.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByMaxRedirectsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where maxRedirects is greater than or equal to
        defaultHttpMonitorFiltering(
            "maxRedirects.greaterThanOrEqual=" + DEFAULT_MAX_REDIRECTS,
            "maxRedirects.greaterThanOrEqual=" + UPDATED_MAX_REDIRECTS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByMaxRedirectsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where maxRedirects is less than or equal to
        defaultHttpMonitorFiltering(
            "maxRedirects.lessThanOrEqual=" + DEFAULT_MAX_REDIRECTS,
            "maxRedirects.lessThanOrEqual=" + SMALLER_MAX_REDIRECTS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByMaxRedirectsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where maxRedirects is less than
        defaultHttpMonitorFiltering("maxRedirects.lessThan=" + UPDATED_MAX_REDIRECTS, "maxRedirects.lessThan=" + DEFAULT_MAX_REDIRECTS);
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByMaxRedirectsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where maxRedirects is greater than
        defaultHttpMonitorFiltering(
            "maxRedirects.greaterThan=" + SMALLER_MAX_REDIRECTS,
            "maxRedirects.greaterThan=" + DEFAULT_MAX_REDIRECTS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByTagsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where tags equals to
        defaultHttpMonitorFiltering("tags.equals=" + DEFAULT_TAGS, "tags.equals=" + UPDATED_TAGS);
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByTagsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where tags in
        defaultHttpMonitorFiltering("tags.in=" + DEFAULT_TAGS + "," + UPDATED_TAGS, "tags.in=" + UPDATED_TAGS);
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByTagsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where tags is not null
        defaultHttpMonitorFiltering("tags.specified=true", "tags.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByTagsContainsSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where tags contains
        defaultHttpMonitorFiltering("tags.contains=" + DEFAULT_TAGS, "tags.contains=" + UPDATED_TAGS);
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByTagsNotContainsSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where tags does not contain
        defaultHttpMonitorFiltering("tags.doesNotContain=" + UPDATED_TAGS, "tags.doesNotContain=" + DEFAULT_TAGS);
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByEnabledIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where enabled equals to
        defaultHttpMonitorFiltering("enabled.equals=" + DEFAULT_ENABLED, "enabled.equals=" + UPDATED_ENABLED);
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByEnabledIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where enabled in
        defaultHttpMonitorFiltering("enabled.in=" + DEFAULT_ENABLED + "," + UPDATED_ENABLED, "enabled.in=" + UPDATED_ENABLED);
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByEnabledIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where enabled is not null
        defaultHttpMonitorFiltering("enabled.specified=true", "enabled.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByExpectedStatusCodesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where expectedStatusCodes equals to
        defaultHttpMonitorFiltering(
            "expectedStatusCodes.equals=" + DEFAULT_EXPECTED_STATUS_CODES,
            "expectedStatusCodes.equals=" + UPDATED_EXPECTED_STATUS_CODES
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByExpectedStatusCodesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where expectedStatusCodes in
        defaultHttpMonitorFiltering(
            "expectedStatusCodes.in=" + DEFAULT_EXPECTED_STATUS_CODES + "," + UPDATED_EXPECTED_STATUS_CODES,
            "expectedStatusCodes.in=" + UPDATED_EXPECTED_STATUS_CODES
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByExpectedStatusCodesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where expectedStatusCodes is not null
        defaultHttpMonitorFiltering("expectedStatusCodes.specified=true", "expectedStatusCodes.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByExpectedStatusCodesContainsSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where expectedStatusCodes contains
        defaultHttpMonitorFiltering(
            "expectedStatusCodes.contains=" + DEFAULT_EXPECTED_STATUS_CODES,
            "expectedStatusCodes.contains=" + UPDATED_EXPECTED_STATUS_CODES
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByExpectedStatusCodesNotContainsSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where expectedStatusCodes does not contain
        defaultHttpMonitorFiltering(
            "expectedStatusCodes.doesNotContain=" + UPDATED_EXPECTED_STATUS_CODES,
            "expectedStatusCodes.doesNotContain=" + DEFAULT_EXPECTED_STATUS_CODES
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByPerformanceBudgetMsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where performanceBudgetMs equals to
        defaultHttpMonitorFiltering(
            "performanceBudgetMs.equals=" + DEFAULT_PERFORMANCE_BUDGET_MS,
            "performanceBudgetMs.equals=" + UPDATED_PERFORMANCE_BUDGET_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByPerformanceBudgetMsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where performanceBudgetMs in
        defaultHttpMonitorFiltering(
            "performanceBudgetMs.in=" + DEFAULT_PERFORMANCE_BUDGET_MS + "," + UPDATED_PERFORMANCE_BUDGET_MS,
            "performanceBudgetMs.in=" + UPDATED_PERFORMANCE_BUDGET_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByPerformanceBudgetMsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where performanceBudgetMs is not null
        defaultHttpMonitorFiltering("performanceBudgetMs.specified=true", "performanceBudgetMs.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByPerformanceBudgetMsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where performanceBudgetMs is greater than or equal to
        defaultHttpMonitorFiltering(
            "performanceBudgetMs.greaterThanOrEqual=" + DEFAULT_PERFORMANCE_BUDGET_MS,
            "performanceBudgetMs.greaterThanOrEqual=" + UPDATED_PERFORMANCE_BUDGET_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByPerformanceBudgetMsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where performanceBudgetMs is less than or equal to
        defaultHttpMonitorFiltering(
            "performanceBudgetMs.lessThanOrEqual=" + DEFAULT_PERFORMANCE_BUDGET_MS,
            "performanceBudgetMs.lessThanOrEqual=" + SMALLER_PERFORMANCE_BUDGET_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByPerformanceBudgetMsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where performanceBudgetMs is less than
        defaultHttpMonitorFiltering(
            "performanceBudgetMs.lessThan=" + UPDATED_PERFORMANCE_BUDGET_MS,
            "performanceBudgetMs.lessThan=" + DEFAULT_PERFORMANCE_BUDGET_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByPerformanceBudgetMsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where performanceBudgetMs is greater than
        defaultHttpMonitorFiltering(
            "performanceBudgetMs.greaterThan=" + SMALLER_PERFORMANCE_BUDGET_MS,
            "performanceBudgetMs.greaterThan=" + DEFAULT_PERFORMANCE_BUDGET_MS
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsBySizeBudgetKbIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where sizeBudgetKb equals to
        defaultHttpMonitorFiltering("sizeBudgetKb.equals=" + DEFAULT_SIZE_BUDGET_KB, "sizeBudgetKb.equals=" + UPDATED_SIZE_BUDGET_KB);
    }

    @Test
    @Transactional
    void getAllHttpMonitorsBySizeBudgetKbIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where sizeBudgetKb in
        defaultHttpMonitorFiltering(
            "sizeBudgetKb.in=" + DEFAULT_SIZE_BUDGET_KB + "," + UPDATED_SIZE_BUDGET_KB,
            "sizeBudgetKb.in=" + UPDATED_SIZE_BUDGET_KB
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsBySizeBudgetKbIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where sizeBudgetKb is not null
        defaultHttpMonitorFiltering("sizeBudgetKb.specified=true", "sizeBudgetKb.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpMonitorsBySizeBudgetKbIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where sizeBudgetKb is greater than or equal to
        defaultHttpMonitorFiltering(
            "sizeBudgetKb.greaterThanOrEqual=" + DEFAULT_SIZE_BUDGET_KB,
            "sizeBudgetKb.greaterThanOrEqual=" + UPDATED_SIZE_BUDGET_KB
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsBySizeBudgetKbIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where sizeBudgetKb is less than or equal to
        defaultHttpMonitorFiltering(
            "sizeBudgetKb.lessThanOrEqual=" + DEFAULT_SIZE_BUDGET_KB,
            "sizeBudgetKb.lessThanOrEqual=" + SMALLER_SIZE_BUDGET_KB
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsBySizeBudgetKbIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where sizeBudgetKb is less than
        defaultHttpMonitorFiltering("sizeBudgetKb.lessThan=" + UPDATED_SIZE_BUDGET_KB, "sizeBudgetKb.lessThan=" + DEFAULT_SIZE_BUDGET_KB);
    }

    @Test
    @Transactional
    void getAllHttpMonitorsBySizeBudgetKbIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where sizeBudgetKb is greater than
        defaultHttpMonitorFiltering(
            "sizeBudgetKb.greaterThan=" + SMALLER_SIZE_BUDGET_KB,
            "sizeBudgetKb.greaterThan=" + DEFAULT_SIZE_BUDGET_KB
        );
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByParentIsEqualToSomething() throws Exception {
        HttpMonitor parent;
        if (TestUtil.findAll(em, HttpMonitor.class).isEmpty()) {
            httpMonitorRepository.saveAndFlush(httpMonitor);
            parent = HttpMonitorResourceIT.createEntity();
        } else {
            parent = TestUtil.findAll(em, HttpMonitor.class).get(0);
        }
        em.persist(parent);
        em.flush();
        httpMonitor.setParent(parent);
        httpMonitorRepository.saveAndFlush(httpMonitor);
        Long parentId = parent.getId();
        // Get all the httpMonitorList where parent equals to parentId
        defaultHttpMonitorShouldBeFound("parentId.equals=" + parentId);

        // Get all the httpMonitorList where parent equals to (parentId + 1)
        defaultHttpMonitorShouldNotBeFound("parentId.equals=" + (parentId + 1));
    }

    private void defaultHttpMonitorFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultHttpMonitorShouldBeFound(shouldBeFound);
        defaultHttpMonitorShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultHttpMonitorShouldBeFound(String filter) throws Exception {
        restHttpMonitorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(httpMonitor.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].method").value(hasItem(DEFAULT_METHOD)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].headers").value(hasItem(DEFAULT_HEADERS)))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY)))
            .andExpect(jsonPath("$.[*].intervalSeconds").value(hasItem(DEFAULT_INTERVAL_SECONDS)))
            .andExpect(jsonPath("$.[*].timeoutSeconds").value(hasItem(DEFAULT_TIMEOUT_SECONDS)))
            .andExpect(jsonPath("$.[*].retryCount").value(hasItem(DEFAULT_RETRY_COUNT)))
            .andExpect(jsonPath("$.[*].retryDelaySeconds").value(hasItem(DEFAULT_RETRY_DELAY_SECONDS)))
            .andExpect(jsonPath("$.[*].responseTimeWarningMs").value(hasItem(DEFAULT_RESPONSE_TIME_WARNING_MS)))
            .andExpect(jsonPath("$.[*].responseTimeCriticalMs").value(hasItem(DEFAULT_RESPONSE_TIME_CRITICAL_MS)))
            .andExpect(jsonPath("$.[*].uptimeWarningPercent").value(hasItem(DEFAULT_UPTIME_WARNING_PERCENT.doubleValue())))
            .andExpect(jsonPath("$.[*].uptimeCriticalPercent").value(hasItem(DEFAULT_UPTIME_CRITICAL_PERCENT.doubleValue())))
            .andExpect(jsonPath("$.[*].includeResponseBody").value(hasItem(DEFAULT_INCLUDE_RESPONSE_BODY)))
            .andExpect(jsonPath("$.[*].resendNotificationCount").value(hasItem(DEFAULT_RESEND_NOTIFICATION_COUNT)))
            .andExpect(jsonPath("$.[*].certificateExpiryDays").value(hasItem(DEFAULT_CERTIFICATE_EXPIRY_DAYS)))
            .andExpect(jsonPath("$.[*].ignoreTlsError").value(hasItem(DEFAULT_IGNORE_TLS_ERROR)))
            .andExpect(jsonPath("$.[*].checkSslCertificate").value(hasItem(DEFAULT_CHECK_SSL_CERTIFICATE)))
            .andExpect(jsonPath("$.[*].checkDnsResolution").value(hasItem(DEFAULT_CHECK_DNS_RESOLUTION)))
            .andExpect(jsonPath("$.[*].upsideDownMode").value(hasItem(DEFAULT_UPSIDE_DOWN_MODE)))
            .andExpect(jsonPath("$.[*].maxRedirects").value(hasItem(DEFAULT_MAX_REDIRECTS)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].tags").value(hasItem(DEFAULT_TAGS)))
            .andExpect(jsonPath("$.[*].enabled").value(hasItem(DEFAULT_ENABLED)))
            .andExpect(jsonPath("$.[*].expectedStatusCodes").value(hasItem(DEFAULT_EXPECTED_STATUS_CODES)))
            .andExpect(jsonPath("$.[*].performanceBudgetMs").value(hasItem(DEFAULT_PERFORMANCE_BUDGET_MS)))
            .andExpect(jsonPath("$.[*].sizeBudgetKb").value(hasItem(DEFAULT_SIZE_BUDGET_KB)));

        // Check, that the count call also returns 1
        restHttpMonitorMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultHttpMonitorShouldNotBeFound(String filter) throws Exception {
        restHttpMonitorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restHttpMonitorMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingHttpMonitor() throws Exception {
        // Get the httpMonitor
        restHttpMonitorMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingHttpMonitor() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the httpMonitor
        HttpMonitor updatedHttpMonitor = httpMonitorRepository.findById(httpMonitor.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedHttpMonitor are not directly saved in db
        em.detach(updatedHttpMonitor);
        updatedHttpMonitor
            .name(UPDATED_NAME)
            .method(UPDATED_METHOD)
            .type(UPDATED_TYPE)
            .url(UPDATED_URL)
            .headers(UPDATED_HEADERS)
            .body(UPDATED_BODY)
            .intervalSeconds(UPDATED_INTERVAL_SECONDS)
            .timeoutSeconds(UPDATED_TIMEOUT_SECONDS)
            .retryCount(UPDATED_RETRY_COUNT)
            .retryDelaySeconds(UPDATED_RETRY_DELAY_SECONDS)
            .responseTimeWarningMs(UPDATED_RESPONSE_TIME_WARNING_MS)
            .responseTimeCriticalMs(UPDATED_RESPONSE_TIME_CRITICAL_MS)
            .uptimeWarningPercent(UPDATED_UPTIME_WARNING_PERCENT)
            .uptimeCriticalPercent(UPDATED_UPTIME_CRITICAL_PERCENT)
            .includeResponseBody(UPDATED_INCLUDE_RESPONSE_BODY)
            .resendNotificationCount(UPDATED_RESEND_NOTIFICATION_COUNT)
            .certificateExpiryDays(UPDATED_CERTIFICATE_EXPIRY_DAYS)
            .ignoreTlsError(UPDATED_IGNORE_TLS_ERROR)
            .checkSslCertificate(UPDATED_CHECK_SSL_CERTIFICATE)
            .checkDnsResolution(UPDATED_CHECK_DNS_RESOLUTION)
            .upsideDownMode(UPDATED_UPSIDE_DOWN_MODE)
            .maxRedirects(UPDATED_MAX_REDIRECTS)
            .description(UPDATED_DESCRIPTION)
            .tags(UPDATED_TAGS)
            .enabled(UPDATED_ENABLED)
            .expectedStatusCodes(UPDATED_EXPECTED_STATUS_CODES)
            .performanceBudgetMs(UPDATED_PERFORMANCE_BUDGET_MS)
            .sizeBudgetKb(UPDATED_SIZE_BUDGET_KB);
        HttpMonitorDTO httpMonitorDTO = httpMonitorMapper.toDto(updatedHttpMonitor);

        restHttpMonitorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, httpMonitorDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(httpMonitorDTO))
            )
            .andExpect(status().isOk());

        // Validate the HttpMonitor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedHttpMonitorToMatchAllProperties(updatedHttpMonitor);
    }

    @Test
    @Transactional
    void putNonExistingHttpMonitor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        httpMonitor.setId(longCount.incrementAndGet());

        // Create the HttpMonitor
        HttpMonitorDTO httpMonitorDTO = httpMonitorMapper.toDto(httpMonitor);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHttpMonitorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, httpMonitorDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(httpMonitorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HttpMonitor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchHttpMonitor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        httpMonitor.setId(longCount.incrementAndGet());

        // Create the HttpMonitor
        HttpMonitorDTO httpMonitorDTO = httpMonitorMapper.toDto(httpMonitor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHttpMonitorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(httpMonitorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HttpMonitor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamHttpMonitor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        httpMonitor.setId(longCount.incrementAndGet());

        // Create the HttpMonitor
        HttpMonitorDTO httpMonitorDTO = httpMonitorMapper.toDto(httpMonitor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHttpMonitorMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(httpMonitorDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the HttpMonitor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateHttpMonitorWithPatch() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the httpMonitor using partial update
        HttpMonitor partialUpdatedHttpMonitor = new HttpMonitor();
        partialUpdatedHttpMonitor.setId(httpMonitor.getId());

        partialUpdatedHttpMonitor
            .name(UPDATED_NAME)
            .intervalSeconds(UPDATED_INTERVAL_SECONDS)
            .retryDelaySeconds(UPDATED_RETRY_DELAY_SECONDS)
            .responseTimeCriticalMs(UPDATED_RESPONSE_TIME_CRITICAL_MS)
            .uptimeWarningPercent(UPDATED_UPTIME_WARNING_PERCENT)
            .resendNotificationCount(UPDATED_RESEND_NOTIFICATION_COUNT)
            .certificateExpiryDays(UPDATED_CERTIFICATE_EXPIRY_DAYS)
            .ignoreTlsError(UPDATED_IGNORE_TLS_ERROR)
            .description(UPDATED_DESCRIPTION)
            .enabled(UPDATED_ENABLED)
            .performanceBudgetMs(UPDATED_PERFORMANCE_BUDGET_MS)
            .sizeBudgetKb(UPDATED_SIZE_BUDGET_KB);

        restHttpMonitorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHttpMonitor.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedHttpMonitor))
            )
            .andExpect(status().isOk());

        // Validate the HttpMonitor in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertHttpMonitorUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedHttpMonitor, httpMonitor),
            getPersistedHttpMonitor(httpMonitor)
        );
    }

    @Test
    @Transactional
    void fullUpdateHttpMonitorWithPatch() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the httpMonitor using partial update
        HttpMonitor partialUpdatedHttpMonitor = new HttpMonitor();
        partialUpdatedHttpMonitor.setId(httpMonitor.getId());

        partialUpdatedHttpMonitor
            .name(UPDATED_NAME)
            .method(UPDATED_METHOD)
            .type(UPDATED_TYPE)
            .url(UPDATED_URL)
            .headers(UPDATED_HEADERS)
            .body(UPDATED_BODY)
            .intervalSeconds(UPDATED_INTERVAL_SECONDS)
            .timeoutSeconds(UPDATED_TIMEOUT_SECONDS)
            .retryCount(UPDATED_RETRY_COUNT)
            .retryDelaySeconds(UPDATED_RETRY_DELAY_SECONDS)
            .responseTimeWarningMs(UPDATED_RESPONSE_TIME_WARNING_MS)
            .responseTimeCriticalMs(UPDATED_RESPONSE_TIME_CRITICAL_MS)
            .uptimeWarningPercent(UPDATED_UPTIME_WARNING_PERCENT)
            .uptimeCriticalPercent(UPDATED_UPTIME_CRITICAL_PERCENT)
            .includeResponseBody(UPDATED_INCLUDE_RESPONSE_BODY)
            .resendNotificationCount(UPDATED_RESEND_NOTIFICATION_COUNT)
            .certificateExpiryDays(UPDATED_CERTIFICATE_EXPIRY_DAYS)
            .ignoreTlsError(UPDATED_IGNORE_TLS_ERROR)
            .checkSslCertificate(UPDATED_CHECK_SSL_CERTIFICATE)
            .checkDnsResolution(UPDATED_CHECK_DNS_RESOLUTION)
            .upsideDownMode(UPDATED_UPSIDE_DOWN_MODE)
            .maxRedirects(UPDATED_MAX_REDIRECTS)
            .description(UPDATED_DESCRIPTION)
            .tags(UPDATED_TAGS)
            .enabled(UPDATED_ENABLED)
            .expectedStatusCodes(UPDATED_EXPECTED_STATUS_CODES)
            .performanceBudgetMs(UPDATED_PERFORMANCE_BUDGET_MS)
            .sizeBudgetKb(UPDATED_SIZE_BUDGET_KB);

        restHttpMonitorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHttpMonitor.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedHttpMonitor))
            )
            .andExpect(status().isOk());

        // Validate the HttpMonitor in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertHttpMonitorUpdatableFieldsEquals(partialUpdatedHttpMonitor, getPersistedHttpMonitor(partialUpdatedHttpMonitor));
    }

    @Test
    @Transactional
    void patchNonExistingHttpMonitor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        httpMonitor.setId(longCount.incrementAndGet());

        // Create the HttpMonitor
        HttpMonitorDTO httpMonitorDTO = httpMonitorMapper.toDto(httpMonitor);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHttpMonitorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, httpMonitorDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(httpMonitorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HttpMonitor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchHttpMonitor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        httpMonitor.setId(longCount.incrementAndGet());

        // Create the HttpMonitor
        HttpMonitorDTO httpMonitorDTO = httpMonitorMapper.toDto(httpMonitor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHttpMonitorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(httpMonitorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HttpMonitor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamHttpMonitor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        httpMonitor.setId(longCount.incrementAndGet());

        // Create the HttpMonitor
        HttpMonitorDTO httpMonitorDTO = httpMonitorMapper.toDto(httpMonitor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHttpMonitorMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(httpMonitorDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the HttpMonitor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteHttpMonitor() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the httpMonitor
        restHttpMonitorMockMvc
            .perform(delete(ENTITY_API_URL_ID, httpMonitor.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    @Test
    @Transactional
    void searchHttpMonitor() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);
        em.flush();
        em.clear();

        // Search the httpMonitor
        restHttpMonitorMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + httpMonitor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(httpMonitor.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].method").value(hasItem(DEFAULT_METHOD)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].headers").value(hasItem(DEFAULT_HEADERS)))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY)))
            .andExpect(jsonPath("$.[*].intervalSeconds").value(hasItem(DEFAULT_INTERVAL_SECONDS)))
            .andExpect(jsonPath("$.[*].timeoutSeconds").value(hasItem(DEFAULT_TIMEOUT_SECONDS)))
            .andExpect(jsonPath("$.[*].retryCount").value(hasItem(DEFAULT_RETRY_COUNT)))
            .andExpect(jsonPath("$.[*].retryDelaySeconds").value(hasItem(DEFAULT_RETRY_DELAY_SECONDS)))
            .andExpect(jsonPath("$.[*].responseTimeWarningMs").value(hasItem(DEFAULT_RESPONSE_TIME_WARNING_MS)))
            .andExpect(jsonPath("$.[*].responseTimeCriticalMs").value(hasItem(DEFAULT_RESPONSE_TIME_CRITICAL_MS)))
            .andExpect(jsonPath("$.[*].uptimeWarningPercent").value(hasItem(DEFAULT_UPTIME_WARNING_PERCENT.doubleValue())))
            .andExpect(jsonPath("$.[*].uptimeCriticalPercent").value(hasItem(DEFAULT_UPTIME_CRITICAL_PERCENT.doubleValue())))
            .andExpect(jsonPath("$.[*].includeResponseBody").value(hasItem(DEFAULT_INCLUDE_RESPONSE_BODY)))
            .andExpect(jsonPath("$.[*].resendNotificationCount").value(hasItem(DEFAULT_RESEND_NOTIFICATION_COUNT)))
            .andExpect(jsonPath("$.[*].certificateExpiryDays").value(hasItem(DEFAULT_CERTIFICATE_EXPIRY_DAYS)))
            .andExpect(jsonPath("$.[*].ignoreTlsError").value(hasItem(DEFAULT_IGNORE_TLS_ERROR)))
            .andExpect(jsonPath("$.[*].checkSslCertificate").value(hasItem(DEFAULT_CHECK_SSL_CERTIFICATE)))
            .andExpect(jsonPath("$.[*].checkDnsResolution").value(hasItem(DEFAULT_CHECK_DNS_RESOLUTION)))
            .andExpect(jsonPath("$.[*].upsideDownMode").value(hasItem(DEFAULT_UPSIDE_DOWN_MODE)))
            .andExpect(jsonPath("$.[*].maxRedirects").value(hasItem(DEFAULT_MAX_REDIRECTS)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].tags").value(hasItem(DEFAULT_TAGS)))
            .andExpect(jsonPath("$.[*].enabled").value(hasItem(DEFAULT_ENABLED)))
            .andExpect(jsonPath("$.[*].expectedStatusCodes").value(hasItem(DEFAULT_EXPECTED_STATUS_CODES)))
            .andExpect(jsonPath("$.[*].performanceBudgetMs").value(hasItem(DEFAULT_PERFORMANCE_BUDGET_MS)))
            .andExpect(jsonPath("$.[*].sizeBudgetKb").value(hasItem(DEFAULT_SIZE_BUDGET_KB)));
    }

    protected long getRepositoryCount() {
        return httpMonitorRepository.count();
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

    protected HttpMonitor getPersistedHttpMonitor(HttpMonitor httpMonitor) {
        return httpMonitorRepository.findById(httpMonitor.getId()).orElseThrow();
    }

    protected void assertPersistedHttpMonitorToMatchAllProperties(HttpMonitor expectedHttpMonitor) {
        assertHttpMonitorAllPropertiesEquals(expectedHttpMonitor, getPersistedHttpMonitor(expectedHttpMonitor));
    }

    protected void assertPersistedHttpMonitorToMatchUpdatableProperties(HttpMonitor expectedHttpMonitor) {
        assertHttpMonitorAllUpdatablePropertiesEquals(expectedHttpMonitor, getPersistedHttpMonitor(expectedHttpMonitor));
    }
}
