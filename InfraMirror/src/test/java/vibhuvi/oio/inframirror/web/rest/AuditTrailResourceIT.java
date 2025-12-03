package vibhuvi.oio.inframirror.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static vibhuvi.oio.inframirror.domain.AuditTrailAsserts.*;

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
import vibhuvi.oio.inframirror.domain.AuditTrail;
import vibhuvi.oio.inframirror.domain.User;
import vibhuvi.oio.inframirror.repository.AuditTrailRepository;
import vibhuvi.oio.inframirror.repository.UserRepository;
import vibhuvi.oio.inframirror.repository.search.AuditTrailSearchRepository;
import vibhuvi.oio.inframirror.service.mapper.AuditTrailMapper;

/**
 * Integration tests for the {@link AuditTrailResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AuditTrailResourceIT {

    private static final String DEFAULT_ACTION = "AAAAAAAAAA";
    private static final String UPDATED_ACTION = "BBBBBBBBBB";

    private static final String DEFAULT_ENTITY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ENTITY_NAME = "BBBBBBBBBB";

    private static final Long DEFAULT_ENTITY_ID = 1L;
    private static final Long UPDATED_ENTITY_ID = 2L;
    private static final Long SMALLER_ENTITY_ID = 1L - 1L;

    private static final String DEFAULT_OLD_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_OLD_VALUE = "BBBBBBBBBB";

    private static final String DEFAULT_NEW_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_NEW_VALUE = "BBBBBBBBBB";

    private static final Instant DEFAULT_TIMESTAMP = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TIMESTAMP = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_IP_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_IP_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_USER_AGENT = "AAAAAAAAAA";
    private static final String UPDATED_USER_AGENT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/audit-trails";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/audit-trails/_search";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AuditTrailRepository auditTrailRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditTrailMapper auditTrailMapper;

    @Autowired
    private AuditTrailSearchRepository auditTrailSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAuditTrailMockMvc;

    private AuditTrail auditTrail;

    private AuditTrail insertedAuditTrail;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AuditTrail createEntity() {
        return new AuditTrail()
            .action(DEFAULT_ACTION)
            .entityName(DEFAULT_ENTITY_NAME)
            .entityId(DEFAULT_ENTITY_ID)
            .oldValue(DEFAULT_OLD_VALUE)
            .newValue(DEFAULT_NEW_VALUE)
            .timestamp(DEFAULT_TIMESTAMP)
            .ipAddress(DEFAULT_IP_ADDRESS)
            .userAgent(DEFAULT_USER_AGENT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AuditTrail createUpdatedEntity() {
        return new AuditTrail()
            .action(UPDATED_ACTION)
            .entityName(UPDATED_ENTITY_NAME)
            .entityId(UPDATED_ENTITY_ID)
            .oldValue(UPDATED_OLD_VALUE)
            .newValue(UPDATED_NEW_VALUE)
            .timestamp(UPDATED_TIMESTAMP)
            .ipAddress(UPDATED_IP_ADDRESS)
            .userAgent(UPDATED_USER_AGENT);
    }

    @BeforeEach
    void initTest() {
        auditTrail = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAuditTrail != null) {
            auditTrailRepository.delete(insertedAuditTrail);
            auditTrailSearchRepository.delete(insertedAuditTrail);
            insertedAuditTrail = null;
        }
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    void getAllAuditTrails() throws Exception {
        // Initialize the database
        insertedAuditTrail = auditTrailRepository.saveAndFlush(auditTrail);

        // Get all the auditTrailList
        restAuditTrailMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(auditTrail.getId().intValue())))
            .andExpect(jsonPath("$.[*].action").value(hasItem(DEFAULT_ACTION)))
            .andExpect(jsonPath("$.[*].entityName").value(hasItem(DEFAULT_ENTITY_NAME)))
            .andExpect(jsonPath("$.[*].entityId").value(hasItem(DEFAULT_ENTITY_ID.intValue())))
            .andExpect(jsonPath("$.[*].oldValue").value(hasItem(DEFAULT_OLD_VALUE)))
            .andExpect(jsonPath("$.[*].newValue").value(hasItem(DEFAULT_NEW_VALUE)))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP.toString())))
            .andExpect(jsonPath("$.[*].ipAddress").value(hasItem(DEFAULT_IP_ADDRESS)))
            .andExpect(jsonPath("$.[*].userAgent").value(hasItem(DEFAULT_USER_AGENT)));
    }

    @Test
    @Transactional
    void getAuditTrail() throws Exception {
        // Initialize the database
        insertedAuditTrail = auditTrailRepository.saveAndFlush(auditTrail);

        // Get the auditTrail
        restAuditTrailMockMvc
            .perform(get(ENTITY_API_URL_ID, auditTrail.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(auditTrail.getId().intValue()))
            .andExpect(jsonPath("$.action").value(DEFAULT_ACTION))
            .andExpect(jsonPath("$.entityName").value(DEFAULT_ENTITY_NAME))
            .andExpect(jsonPath("$.entityId").value(DEFAULT_ENTITY_ID.intValue()))
            .andExpect(jsonPath("$.oldValue").value(DEFAULT_OLD_VALUE))
            .andExpect(jsonPath("$.newValue").value(DEFAULT_NEW_VALUE))
            .andExpect(jsonPath("$.timestamp").value(DEFAULT_TIMESTAMP.toString()))
            .andExpect(jsonPath("$.ipAddress").value(DEFAULT_IP_ADDRESS))
            .andExpect(jsonPath("$.userAgent").value(DEFAULT_USER_AGENT));
    }

    @Test
    @Transactional
    void getAuditTrailsByIdFiltering() throws Exception {
        // Initialize the database
        insertedAuditTrail = auditTrailRepository.saveAndFlush(auditTrail);

        Long id = auditTrail.getId();

        defaultAuditTrailFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAuditTrailFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAuditTrailFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAuditTrailsByActionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuditTrail = auditTrailRepository.saveAndFlush(auditTrail);

        // Get all the auditTrailList where action equals to
        defaultAuditTrailFiltering("action.equals=" + DEFAULT_ACTION, "action.equals=" + UPDATED_ACTION);
    }

    @Test
    @Transactional
    void getAllAuditTrailsByActionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuditTrail = auditTrailRepository.saveAndFlush(auditTrail);

        // Get all the auditTrailList where action in
        defaultAuditTrailFiltering("action.in=" + DEFAULT_ACTION + "," + UPDATED_ACTION, "action.in=" + UPDATED_ACTION);
    }

    @Test
    @Transactional
    void getAllAuditTrailsByActionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuditTrail = auditTrailRepository.saveAndFlush(auditTrail);

        // Get all the auditTrailList where action is not null
        defaultAuditTrailFiltering("action.specified=true", "action.specified=false");
    }

    @Test
    @Transactional
    void getAllAuditTrailsByActionContainsSomething() throws Exception {
        // Initialize the database
        insertedAuditTrail = auditTrailRepository.saveAndFlush(auditTrail);

        // Get all the auditTrailList where action contains
        defaultAuditTrailFiltering("action.contains=" + DEFAULT_ACTION, "action.contains=" + UPDATED_ACTION);
    }

    @Test
    @Transactional
    void getAllAuditTrailsByActionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAuditTrail = auditTrailRepository.saveAndFlush(auditTrail);

        // Get all the auditTrailList where action does not contain
        defaultAuditTrailFiltering("action.doesNotContain=" + UPDATED_ACTION, "action.doesNotContain=" + DEFAULT_ACTION);
    }

    @Test
    @Transactional
    void getAllAuditTrailsByEntityNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuditTrail = auditTrailRepository.saveAndFlush(auditTrail);

        // Get all the auditTrailList where entityName equals to
        defaultAuditTrailFiltering("entityName.equals=" + DEFAULT_ENTITY_NAME, "entityName.equals=" + UPDATED_ENTITY_NAME);
    }

    @Test
    @Transactional
    void getAllAuditTrailsByEntityNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuditTrail = auditTrailRepository.saveAndFlush(auditTrail);

        // Get all the auditTrailList where entityName in
        defaultAuditTrailFiltering(
            "entityName.in=" + DEFAULT_ENTITY_NAME + "," + UPDATED_ENTITY_NAME,
            "entityName.in=" + UPDATED_ENTITY_NAME
        );
    }

    @Test
    @Transactional
    void getAllAuditTrailsByEntityNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuditTrail = auditTrailRepository.saveAndFlush(auditTrail);

        // Get all the auditTrailList where entityName is not null
        defaultAuditTrailFiltering("entityName.specified=true", "entityName.specified=false");
    }

    @Test
    @Transactional
    void getAllAuditTrailsByEntityNameContainsSomething() throws Exception {
        // Initialize the database
        insertedAuditTrail = auditTrailRepository.saveAndFlush(auditTrail);

        // Get all the auditTrailList where entityName contains
        defaultAuditTrailFiltering("entityName.contains=" + DEFAULT_ENTITY_NAME, "entityName.contains=" + UPDATED_ENTITY_NAME);
    }

    @Test
    @Transactional
    void getAllAuditTrailsByEntityNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAuditTrail = auditTrailRepository.saveAndFlush(auditTrail);

        // Get all the auditTrailList where entityName does not contain
        defaultAuditTrailFiltering("entityName.doesNotContain=" + UPDATED_ENTITY_NAME, "entityName.doesNotContain=" + DEFAULT_ENTITY_NAME);
    }

    @Test
    @Transactional
    void getAllAuditTrailsByEntityIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuditTrail = auditTrailRepository.saveAndFlush(auditTrail);

        // Get all the auditTrailList where entityId equals to
        defaultAuditTrailFiltering("entityId.equals=" + DEFAULT_ENTITY_ID, "entityId.equals=" + UPDATED_ENTITY_ID);
    }

    @Test
    @Transactional
    void getAllAuditTrailsByEntityIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuditTrail = auditTrailRepository.saveAndFlush(auditTrail);

        // Get all the auditTrailList where entityId in
        defaultAuditTrailFiltering("entityId.in=" + DEFAULT_ENTITY_ID + "," + UPDATED_ENTITY_ID, "entityId.in=" + UPDATED_ENTITY_ID);
    }

    @Test
    @Transactional
    void getAllAuditTrailsByEntityIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuditTrail = auditTrailRepository.saveAndFlush(auditTrail);

        // Get all the auditTrailList where entityId is not null
        defaultAuditTrailFiltering("entityId.specified=true", "entityId.specified=false");
    }

    @Test
    @Transactional
    void getAllAuditTrailsByEntityIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuditTrail = auditTrailRepository.saveAndFlush(auditTrail);

        // Get all the auditTrailList where entityId is greater than or equal to
        defaultAuditTrailFiltering("entityId.greaterThanOrEqual=" + DEFAULT_ENTITY_ID, "entityId.greaterThanOrEqual=" + UPDATED_ENTITY_ID);
    }

    @Test
    @Transactional
    void getAllAuditTrailsByEntityIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuditTrail = auditTrailRepository.saveAndFlush(auditTrail);

        // Get all the auditTrailList where entityId is less than or equal to
        defaultAuditTrailFiltering("entityId.lessThanOrEqual=" + DEFAULT_ENTITY_ID, "entityId.lessThanOrEqual=" + SMALLER_ENTITY_ID);
    }

    @Test
    @Transactional
    void getAllAuditTrailsByEntityIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedAuditTrail = auditTrailRepository.saveAndFlush(auditTrail);

        // Get all the auditTrailList where entityId is less than
        defaultAuditTrailFiltering("entityId.lessThan=" + UPDATED_ENTITY_ID, "entityId.lessThan=" + DEFAULT_ENTITY_ID);
    }

    @Test
    @Transactional
    void getAllAuditTrailsByEntityIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedAuditTrail = auditTrailRepository.saveAndFlush(auditTrail);

        // Get all the auditTrailList where entityId is greater than
        defaultAuditTrailFiltering("entityId.greaterThan=" + SMALLER_ENTITY_ID, "entityId.greaterThan=" + DEFAULT_ENTITY_ID);
    }

    @Test
    @Transactional
    void getAllAuditTrailsByTimestampIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuditTrail = auditTrailRepository.saveAndFlush(auditTrail);

        // Get all the auditTrailList where timestamp equals to
        defaultAuditTrailFiltering("timestamp.equals=" + DEFAULT_TIMESTAMP, "timestamp.equals=" + UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllAuditTrailsByTimestampIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuditTrail = auditTrailRepository.saveAndFlush(auditTrail);

        // Get all the auditTrailList where timestamp in
        defaultAuditTrailFiltering("timestamp.in=" + DEFAULT_TIMESTAMP + "," + UPDATED_TIMESTAMP, "timestamp.in=" + UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllAuditTrailsByTimestampIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuditTrail = auditTrailRepository.saveAndFlush(auditTrail);

        // Get all the auditTrailList where timestamp is not null
        defaultAuditTrailFiltering("timestamp.specified=true", "timestamp.specified=false");
    }

    @Test
    @Transactional
    void getAllAuditTrailsByIpAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuditTrail = auditTrailRepository.saveAndFlush(auditTrail);

        // Get all the auditTrailList where ipAddress equals to
        defaultAuditTrailFiltering("ipAddress.equals=" + DEFAULT_IP_ADDRESS, "ipAddress.equals=" + UPDATED_IP_ADDRESS);
    }

    @Test
    @Transactional
    void getAllAuditTrailsByIpAddressIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuditTrail = auditTrailRepository.saveAndFlush(auditTrail);

        // Get all the auditTrailList where ipAddress in
        defaultAuditTrailFiltering("ipAddress.in=" + DEFAULT_IP_ADDRESS + "," + UPDATED_IP_ADDRESS, "ipAddress.in=" + UPDATED_IP_ADDRESS);
    }

    @Test
    @Transactional
    void getAllAuditTrailsByIpAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuditTrail = auditTrailRepository.saveAndFlush(auditTrail);

        // Get all the auditTrailList where ipAddress is not null
        defaultAuditTrailFiltering("ipAddress.specified=true", "ipAddress.specified=false");
    }

    @Test
    @Transactional
    void getAllAuditTrailsByIpAddressContainsSomething() throws Exception {
        // Initialize the database
        insertedAuditTrail = auditTrailRepository.saveAndFlush(auditTrail);

        // Get all the auditTrailList where ipAddress contains
        defaultAuditTrailFiltering("ipAddress.contains=" + DEFAULT_IP_ADDRESS, "ipAddress.contains=" + UPDATED_IP_ADDRESS);
    }

    @Test
    @Transactional
    void getAllAuditTrailsByIpAddressNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAuditTrail = auditTrailRepository.saveAndFlush(auditTrail);

        // Get all the auditTrailList where ipAddress does not contain
        defaultAuditTrailFiltering("ipAddress.doesNotContain=" + UPDATED_IP_ADDRESS, "ipAddress.doesNotContain=" + DEFAULT_IP_ADDRESS);
    }

    @Test
    @Transactional
    void getAllAuditTrailsByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            auditTrailRepository.saveAndFlush(auditTrail);
            user = UserResourceIT.createEntity();
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        auditTrail.setUser(user);
        auditTrailRepository.saveAndFlush(auditTrail);
        String userId = user.getId();
        // Get all the auditTrailList where user equals to userId
        defaultAuditTrailShouldBeFound("userId.equals=" + userId);

        // Get all the auditTrailList where user equals to "invalid-id"
        defaultAuditTrailShouldNotBeFound("userId.equals=" + "invalid-id");
    }

    private void defaultAuditTrailFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultAuditTrailShouldBeFound(shouldBeFound);
        defaultAuditTrailShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAuditTrailShouldBeFound(String filter) throws Exception {
        restAuditTrailMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(auditTrail.getId().intValue())))
            .andExpect(jsonPath("$.[*].action").value(hasItem(DEFAULT_ACTION)))
            .andExpect(jsonPath("$.[*].entityName").value(hasItem(DEFAULT_ENTITY_NAME)))
            .andExpect(jsonPath("$.[*].entityId").value(hasItem(DEFAULT_ENTITY_ID.intValue())))
            .andExpect(jsonPath("$.[*].oldValue").value(hasItem(DEFAULT_OLD_VALUE)))
            .andExpect(jsonPath("$.[*].newValue").value(hasItem(DEFAULT_NEW_VALUE)))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP.toString())))
            .andExpect(jsonPath("$.[*].ipAddress").value(hasItem(DEFAULT_IP_ADDRESS)))
            .andExpect(jsonPath("$.[*].userAgent").value(hasItem(DEFAULT_USER_AGENT)));

        // Check, that the count call also returns 1
        restAuditTrailMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAuditTrailShouldNotBeFound(String filter) throws Exception {
        restAuditTrailMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAuditTrailMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAuditTrail() throws Exception {
        // Get the auditTrail
        restAuditTrailMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void searchAuditTrail() throws Exception {
        // Initialize the database
        insertedAuditTrail = auditTrailRepository.saveAndFlush(auditTrail);
        auditTrailSearchRepository.save(auditTrail);

        // Search the auditTrail
        restAuditTrailMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + auditTrail.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(auditTrail.getId().intValue())))
            .andExpect(jsonPath("$.[*].action").value(hasItem(DEFAULT_ACTION)))
            .andExpect(jsonPath("$.[*].entityName").value(hasItem(DEFAULT_ENTITY_NAME)))
            .andExpect(jsonPath("$.[*].entityId").value(hasItem(DEFAULT_ENTITY_ID.intValue())))
            .andExpect(jsonPath("$.[*].oldValue").value(hasItem(DEFAULT_OLD_VALUE.toString())))
            .andExpect(jsonPath("$.[*].newValue").value(hasItem(DEFAULT_NEW_VALUE.toString())))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP.toString())))
            .andExpect(jsonPath("$.[*].ipAddress").value(hasItem(DEFAULT_IP_ADDRESS)))
            .andExpect(jsonPath("$.[*].userAgent").value(hasItem(DEFAULT_USER_AGENT.toString())));
    }

    protected long getRepositoryCount() {
        return auditTrailRepository.count();
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

    protected AuditTrail getPersistedAuditTrail(AuditTrail auditTrail) {
        return auditTrailRepository.findById(auditTrail.getId()).orElseThrow();
    }

    protected void assertPersistedAuditTrailToMatchAllProperties(AuditTrail expectedAuditTrail) {
        assertAuditTrailAllPropertiesEquals(expectedAuditTrail, getPersistedAuditTrail(expectedAuditTrail));
    }

    protected void assertPersistedAuditTrailToMatchUpdatableProperties(AuditTrail expectedAuditTrail) {
        assertAuditTrailAllUpdatablePropertiesEquals(expectedAuditTrail, getPersistedAuditTrail(expectedAuditTrail));
    }
}
