package vibhuvi.oio.inframirror.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static vibhuvi.oio.inframirror.domain.AuditTrailAsserts.*;
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
import vibhuvi.oio.inframirror.domain.AuditTrail;
import vibhuvi.oio.inframirror.repository.AuditTrailRepository;
import vibhuvi.oio.inframirror.service.dto.AuditTrailDTO;
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

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;
    
    private AuditTrailRepository auditTrailRepository;

    @Autowired
    private AuditTrailMapper auditTrailMapper;
    

    @Autowired
    private EntityManager em;
    
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
            insertedAuditTrail = null;
        }
    }

    @Test
    @Transactional
    void createAuditTrail() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AuditTrail
        AuditTrailDTO auditTrailDTO = auditTrailMapper.toDto(auditTrail);
        var returnedAuditTrailDTO = om.readValue(
            restAuditTrailMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(auditTrailDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AuditTrailDTO.class
        );

        // Validate the AuditTrail in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAuditTrail = auditTrailMapper.toEntity(returnedAuditTrailDTO);
        assertAuditTrailUpdatableFieldsEquals(returnedAuditTrail, getPersistedAuditTrail(returnedAuditTrail));        insertedAuditTrail = returnedAuditTrail;
    }

    @Test
    @Transactional
    void createAuditTrailWithExistingId() throws Exception {
        // Create the AuditTrail with an existing ID
        auditTrail.setId(1L);
        AuditTrailDTO auditTrailDTO = auditTrailMapper.toDto(auditTrail);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAuditTrailMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(auditTrailDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AuditTrail in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkActionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        auditTrail.setAction(null);

        // Create the AuditTrail, which fails.
        AuditTrailDTO auditTrailDTO = auditTrailMapper.toDto(auditTrail);

        restAuditTrailMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(auditTrailDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

    }

    @Test
    @Transactional
    void checkEntityNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        auditTrail.setEntityName(null);

        // Create the AuditTrail, which fails.
        AuditTrailDTO auditTrailDTO = auditTrailMapper.toDto(auditTrail);

        restAuditTrailMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(auditTrailDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

    }

    @Test
    @Transactional
    void checkEntityIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        auditTrail.setEntityId(null);

        // Create the AuditTrail, which fails.
        AuditTrailDTO auditTrailDTO = auditTrailMapper.toDto(auditTrail);

        restAuditTrailMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(auditTrailDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

    }

    @Test
    @Transactional
    void checkTimestampIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        auditTrail.setTimestamp(null);

        // Create the AuditTrail, which fails.
        AuditTrailDTO auditTrailDTO = auditTrailMapper.toDto(auditTrail);

        restAuditTrailMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(auditTrailDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

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
    void getNonExistingAuditTrail() throws Exception {
        // Get the auditTrail
        restAuditTrailMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAuditTrail() throws Exception {
        // Initialize the database
        insertedAuditTrail = auditTrailRepository.saveAndFlush(auditTrail);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the auditTrail
        AuditTrail updatedAuditTrail = auditTrailRepository.findById(auditTrail.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAuditTrail are not directly saved in db
        em.detach(updatedAuditTrail);
        updatedAuditTrail
            .action(UPDATED_ACTION)
            .entityName(UPDATED_ENTITY_NAME)
            .entityId(UPDATED_ENTITY_ID)
            .oldValue(UPDATED_OLD_VALUE)
            .newValue(UPDATED_NEW_VALUE)
            .timestamp(UPDATED_TIMESTAMP)
            .ipAddress(UPDATED_IP_ADDRESS)
            .userAgent(UPDATED_USER_AGENT);
        AuditTrailDTO auditTrailDTO = auditTrailMapper.toDto(updatedAuditTrail);

        restAuditTrailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, auditTrailDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(auditTrailDTO))
            )
            .andExpect(status().isOk());

        // Validate the AuditTrail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAuditTrailToMatchAllProperties(updatedAuditTrail);    }

    @Test
    @Transactional
    void putNonExistingAuditTrail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditTrail.setId(longCount.incrementAndGet());

        // Create the AuditTrail
        AuditTrailDTO auditTrailDTO = auditTrailMapper.toDto(auditTrail);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAuditTrailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, auditTrailDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(auditTrailDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AuditTrail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAuditTrail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditTrail.setId(longCount.incrementAndGet());

        // Create the AuditTrail
        AuditTrailDTO auditTrailDTO = auditTrailMapper.toDto(auditTrail);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuditTrailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(auditTrailDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AuditTrail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAuditTrail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditTrail.setId(longCount.incrementAndGet());

        // Create the AuditTrail
        AuditTrailDTO auditTrailDTO = auditTrailMapper.toDto(auditTrail);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuditTrailMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(auditTrailDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AuditTrail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAuditTrailWithPatch() throws Exception {
        // Initialize the database
        insertedAuditTrail = auditTrailRepository.saveAndFlush(auditTrail);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the auditTrail using partial update
        AuditTrail partialUpdatedAuditTrail = new AuditTrail();
        partialUpdatedAuditTrail.setId(auditTrail.getId());

        partialUpdatedAuditTrail
            .action(UPDATED_ACTION)
            .entityName(UPDATED_ENTITY_NAME)
            .entityId(UPDATED_ENTITY_ID)
            .oldValue(UPDATED_OLD_VALUE)
            .timestamp(UPDATED_TIMESTAMP);

        restAuditTrailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAuditTrail.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAuditTrail))
            )
            .andExpect(status().isOk());

        // Validate the AuditTrail in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAuditTrailUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAuditTrail, auditTrail),
            getPersistedAuditTrail(auditTrail)
        );
    }

    @Test
    @Transactional
    void fullUpdateAuditTrailWithPatch() throws Exception {
        // Initialize the database
        insertedAuditTrail = auditTrailRepository.saveAndFlush(auditTrail);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the auditTrail using partial update
        AuditTrail partialUpdatedAuditTrail = new AuditTrail();
        partialUpdatedAuditTrail.setId(auditTrail.getId());

        partialUpdatedAuditTrail
            .action(UPDATED_ACTION)
            .entityName(UPDATED_ENTITY_NAME)
            .entityId(UPDATED_ENTITY_ID)
            .oldValue(UPDATED_OLD_VALUE)
            .newValue(UPDATED_NEW_VALUE)
            .timestamp(UPDATED_TIMESTAMP)
            .ipAddress(UPDATED_IP_ADDRESS)
            .userAgent(UPDATED_USER_AGENT);

        restAuditTrailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAuditTrail.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAuditTrail))
            )
            .andExpect(status().isOk());

        // Validate the AuditTrail in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAuditTrailUpdatableFieldsEquals(partialUpdatedAuditTrail, getPersistedAuditTrail(partialUpdatedAuditTrail));
    }

    @Test
    @Transactional
    void patchNonExistingAuditTrail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditTrail.setId(longCount.incrementAndGet());

        // Create the AuditTrail
        AuditTrailDTO auditTrailDTO = auditTrailMapper.toDto(auditTrail);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAuditTrailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, auditTrailDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(auditTrailDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AuditTrail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAuditTrail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditTrail.setId(longCount.incrementAndGet());

        // Create the AuditTrail
        AuditTrailDTO auditTrailDTO = auditTrailMapper.toDto(auditTrail);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuditTrailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(auditTrailDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AuditTrail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAuditTrail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditTrail.setId(longCount.incrementAndGet());

        // Create the AuditTrail
        AuditTrailDTO auditTrailDTO = auditTrailMapper.toDto(auditTrail);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuditTrailMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(auditTrailDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AuditTrail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAuditTrail() throws Exception {
        // Initialize the database
        insertedAuditTrail = auditTrailRepository.saveAndFlush(auditTrail);
        auditTrailRepository.save(auditTrail);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the auditTrail
        restAuditTrailMockMvc
            .perform(delete(ENTITY_API_URL_ID, auditTrail.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    @Test
    @Transactional
    void searchAuditTrail() throws Exception {
        // Initialize the database
        insertedAuditTrail = auditTrailRepository.saveAndFlush(auditTrail);

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
