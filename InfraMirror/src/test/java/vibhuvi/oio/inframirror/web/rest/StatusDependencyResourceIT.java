package vibhuvi.oio.inframirror.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static vibhuvi.oio.inframirror.domain.StatusDependencyAsserts.*;
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
import vibhuvi.oio.inframirror.domain.StatusDependency;
import vibhuvi.oio.inframirror.repository.StatusDependencyRepository;
import vibhuvi.oio.inframirror.service.dto.StatusDependencyDTO;
import vibhuvi.oio.inframirror.service.mapper.StatusDependencyMapper;

/**
 * Integration tests for the {@link StatusDependencyResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class StatusDependencyResourceIT {

    private static final String DEFAULT_PARENT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_PARENT_TYPE = "BBBBBBBBBB";

    private static final Long DEFAULT_PARENT_ID = 1L;
    private static final Long UPDATED_PARENT_ID = 2L;

    private static final String DEFAULT_CHILD_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_CHILD_TYPE = "BBBBBBBBBB";

    private static final Long DEFAULT_CHILD_ID = 1L;
    private static final Long UPDATED_CHILD_ID = 2L;

    private static final String DEFAULT_METADATA = "AAAAAAAAAA";
    private static final String UPDATED_METADATA = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/status-dependencies";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/status-dependencies/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;
    
    private StatusDependencyRepository statusDependencyRepository;

    @Autowired
    private StatusDependencyMapper statusDependencyMapper;
    

    @Autowired
    private EntityManager em;
    
    private MockMvc restStatusDependencyMockMvc;

    private StatusDependency statusDependency;

    private StatusDependency insertedStatusDependency;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StatusDependency createEntity() {
        return new StatusDependency()
            .parentType(DEFAULT_PARENT_TYPE)
            .parentId(DEFAULT_PARENT_ID)
            .childType(DEFAULT_CHILD_TYPE)
            .childId(DEFAULT_CHILD_ID)
            .metadata(DEFAULT_METADATA)
            .createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StatusDependency createUpdatedEntity() {
        return new StatusDependency()
            .parentType(UPDATED_PARENT_TYPE)
            .parentId(UPDATED_PARENT_ID)
            .childType(UPDATED_CHILD_TYPE)
            .childId(UPDATED_CHILD_ID)
            .metadata(UPDATED_METADATA)
            .createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        statusDependency = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedStatusDependency != null) {
            statusDependencyRepository.delete(insertedStatusDependency);
            insertedStatusDependency = null;
        }
    }

    @Test
    @Transactional
    void createStatusDependency() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the StatusDependency
        StatusDependencyDTO statusDependencyDTO = statusDependencyMapper.toDto(statusDependency);
        var returnedStatusDependencyDTO = om.readValue(
            restStatusDependencyMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(statusDependencyDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            StatusDependencyDTO.class
        );

        // Validate the StatusDependency in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedStatusDependency = statusDependencyMapper.toEntity(returnedStatusDependencyDTO);
        assertStatusDependencyUpdatableFieldsEquals(returnedStatusDependency, getPersistedStatusDependency(returnedStatusDependency));        insertedStatusDependency = returnedStatusDependency;
    }

    @Test
    @Transactional
    void createStatusDependencyWithExistingId() throws Exception {
        // Create the StatusDependency with an existing ID
        statusDependency.setId(1L);
        StatusDependencyDTO statusDependencyDTO = statusDependencyMapper.toDto(statusDependency);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStatusDependencyMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(statusDependencyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StatusDependency in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkParentTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        statusDependency.setParentType(null);

        // Create the StatusDependency, which fails.
        StatusDependencyDTO statusDependencyDTO = statusDependencyMapper.toDto(statusDependency);

        restStatusDependencyMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(statusDependencyDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

    }

    @Test
    @Transactional
    void checkParentIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        statusDependency.setParentId(null);

        // Create the StatusDependency, which fails.
        StatusDependencyDTO statusDependencyDTO = statusDependencyMapper.toDto(statusDependency);

        restStatusDependencyMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(statusDependencyDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

    }

    @Test
    @Transactional
    void checkChildTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        statusDependency.setChildType(null);

        // Create the StatusDependency, which fails.
        StatusDependencyDTO statusDependencyDTO = statusDependencyMapper.toDto(statusDependency);

        restStatusDependencyMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(statusDependencyDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

    }

    @Test
    @Transactional
    void checkChildIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        statusDependency.setChildId(null);

        // Create the StatusDependency, which fails.
        StatusDependencyDTO statusDependencyDTO = statusDependencyMapper.toDto(statusDependency);

        restStatusDependencyMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(statusDependencyDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        statusDependency.setCreatedAt(null);

        // Create the StatusDependency, which fails.
        StatusDependencyDTO statusDependencyDTO = statusDependencyMapper.toDto(statusDependency);

        restStatusDependencyMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(statusDependencyDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

    }

    @Test
    @Transactional
    void getAllStatusDependencies() throws Exception {
        // Initialize the database
        insertedStatusDependency = statusDependencyRepository.saveAndFlush(statusDependency);

        // Get all the statusDependencyList
        restStatusDependencyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(statusDependency.getId().intValue())))
            .andExpect(jsonPath("$.[*].parentType").value(hasItem(DEFAULT_PARENT_TYPE)))
            .andExpect(jsonPath("$.[*].parentId").value(hasItem(DEFAULT_PARENT_ID.intValue())))
            .andExpect(jsonPath("$.[*].childType").value(hasItem(DEFAULT_CHILD_TYPE)))
            .andExpect(jsonPath("$.[*].childId").value(hasItem(DEFAULT_CHILD_ID.intValue())))
            .andExpect(jsonPath("$.[*].metadata").value(hasItem(DEFAULT_METADATA)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getStatusDependency() throws Exception {
        // Initialize the database
        insertedStatusDependency = statusDependencyRepository.saveAndFlush(statusDependency);

        // Get the statusDependency
        restStatusDependencyMockMvc
            .perform(get(ENTITY_API_URL_ID, statusDependency.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(statusDependency.getId().intValue()))
            .andExpect(jsonPath("$.parentType").value(DEFAULT_PARENT_TYPE))
            .andExpect(jsonPath("$.parentId").value(DEFAULT_PARENT_ID.intValue()))
            .andExpect(jsonPath("$.childType").value(DEFAULT_CHILD_TYPE))
            .andExpect(jsonPath("$.childId").value(DEFAULT_CHILD_ID.intValue()))
            .andExpect(jsonPath("$.metadata").value(DEFAULT_METADATA))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingStatusDependency() throws Exception {
        // Get the statusDependency
        restStatusDependencyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStatusDependency() throws Exception {
        // Initialize the database
        insertedStatusDependency = statusDependencyRepository.saveAndFlush(statusDependency);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the statusDependency
        StatusDependency updatedStatusDependency = statusDependencyRepository.findById(statusDependency.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedStatusDependency are not directly saved in db
        em.detach(updatedStatusDependency);
        updatedStatusDependency
            .parentType(UPDATED_PARENT_TYPE)
            .parentId(UPDATED_PARENT_ID)
            .childType(UPDATED_CHILD_TYPE)
            .childId(UPDATED_CHILD_ID)
            .metadata(UPDATED_METADATA)
            .createdAt(UPDATED_CREATED_AT);
        StatusDependencyDTO statusDependencyDTO = statusDependencyMapper.toDto(updatedStatusDependency);

        restStatusDependencyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, statusDependencyDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(statusDependencyDTO))
            )
            .andExpect(status().isOk());

        // Validate the StatusDependency in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStatusDependencyToMatchAllProperties(updatedStatusDependency);    }

    @Test
    @Transactional
    void putNonExistingStatusDependency() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statusDependency.setId(longCount.incrementAndGet());

        // Create the StatusDependency
        StatusDependencyDTO statusDependencyDTO = statusDependencyMapper.toDto(statusDependency);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStatusDependencyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, statusDependencyDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(statusDependencyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StatusDependency in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStatusDependency() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statusDependency.setId(longCount.incrementAndGet());

        // Create the StatusDependency
        StatusDependencyDTO statusDependencyDTO = statusDependencyMapper.toDto(statusDependency);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatusDependencyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(statusDependencyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StatusDependency in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStatusDependency() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statusDependency.setId(longCount.incrementAndGet());

        // Create the StatusDependency
        StatusDependencyDTO statusDependencyDTO = statusDependencyMapper.toDto(statusDependency);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatusDependencyMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(statusDependencyDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the StatusDependency in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStatusDependencyWithPatch() throws Exception {
        // Initialize the database
        insertedStatusDependency = statusDependencyRepository.saveAndFlush(statusDependency);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the statusDependency using partial update
        StatusDependency partialUpdatedStatusDependency = new StatusDependency();
        partialUpdatedStatusDependency.setId(statusDependency.getId());

        partialUpdatedStatusDependency.parentType(UPDATED_PARENT_TYPE).parentId(UPDATED_PARENT_ID).childType(UPDATED_CHILD_TYPE);

        restStatusDependencyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStatusDependency.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStatusDependency))
            )
            .andExpect(status().isOk());

        // Validate the StatusDependency in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStatusDependencyUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedStatusDependency, statusDependency),
            getPersistedStatusDependency(statusDependency)
        );
    }

    @Test
    @Transactional
    void fullUpdateStatusDependencyWithPatch() throws Exception {
        // Initialize the database
        insertedStatusDependency = statusDependencyRepository.saveAndFlush(statusDependency);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the statusDependency using partial update
        StatusDependency partialUpdatedStatusDependency = new StatusDependency();
        partialUpdatedStatusDependency.setId(statusDependency.getId());

        partialUpdatedStatusDependency
            .parentType(UPDATED_PARENT_TYPE)
            .parentId(UPDATED_PARENT_ID)
            .childType(UPDATED_CHILD_TYPE)
            .childId(UPDATED_CHILD_ID)
            .metadata(UPDATED_METADATA)
            .createdAt(UPDATED_CREATED_AT);

        restStatusDependencyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStatusDependency.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStatusDependency))
            )
            .andExpect(status().isOk());

        // Validate the StatusDependency in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStatusDependencyUpdatableFieldsEquals(
            partialUpdatedStatusDependency,
            getPersistedStatusDependency(partialUpdatedStatusDependency)
        );
    }

    @Test
    @Transactional
    void patchNonExistingStatusDependency() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statusDependency.setId(longCount.incrementAndGet());

        // Create the StatusDependency
        StatusDependencyDTO statusDependencyDTO = statusDependencyMapper.toDto(statusDependency);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStatusDependencyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, statusDependencyDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(statusDependencyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StatusDependency in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStatusDependency() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statusDependency.setId(longCount.incrementAndGet());

        // Create the StatusDependency
        StatusDependencyDTO statusDependencyDTO = statusDependencyMapper.toDto(statusDependency);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatusDependencyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(statusDependencyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StatusDependency in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStatusDependency() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statusDependency.setId(longCount.incrementAndGet());

        // Create the StatusDependency
        StatusDependencyDTO statusDependencyDTO = statusDependencyMapper.toDto(statusDependency);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatusDependencyMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(statusDependencyDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the StatusDependency in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStatusDependency() throws Exception {
        // Initialize the database
        insertedStatusDependency = statusDependencyRepository.saveAndFlush(statusDependency);
        statusDependencyRepository.save(statusDependency);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the statusDependency
        restStatusDependencyMockMvc
            .perform(delete(ENTITY_API_URL_ID, statusDependency.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    @Test
    @Transactional
    void searchStatusDependency() throws Exception {
        // Initialize the database
        insertedStatusDependency = statusDependencyRepository.saveAndFlush(statusDependency);

        // Search the statusDependency
        restStatusDependencyMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + statusDependency.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(statusDependency.getId().intValue())))
            .andExpect(jsonPath("$.[*].parentType").value(hasItem(DEFAULT_PARENT_TYPE)))
            .andExpect(jsonPath("$.[*].parentId").value(hasItem(DEFAULT_PARENT_ID.intValue())))
            .andExpect(jsonPath("$.[*].childType").value(hasItem(DEFAULT_CHILD_TYPE)))
            .andExpect(jsonPath("$.[*].childId").value(hasItem(DEFAULT_CHILD_ID.intValue())))
            .andExpect(jsonPath("$.[*].metadata").value(hasItem(DEFAULT_METADATA.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return statusDependencyRepository.count();
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

    protected StatusDependency getPersistedStatusDependency(StatusDependency statusDependency) {
        return statusDependencyRepository.findById(statusDependency.getId()).orElseThrow();
    }

    protected void assertPersistedStatusDependencyToMatchAllProperties(StatusDependency expectedStatusDependency) {
        assertStatusDependencyAllPropertiesEquals(expectedStatusDependency, getPersistedStatusDependency(expectedStatusDependency));
    }

    protected void assertPersistedStatusDependencyToMatchUpdatableProperties(StatusDependency expectedStatusDependency) {
        assertStatusDependencyAllUpdatablePropertiesEquals(
            expectedStatusDependency,
            getPersistedStatusDependency(expectedStatusDependency)
        );
    }
}
