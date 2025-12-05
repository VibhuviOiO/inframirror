package vibhuvi.oio.inframirror.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static vibhuvi.oio.inframirror.domain.AgentLockAsserts.*;
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
import vibhuvi.oio.inframirror.domain.AgentLock;
import vibhuvi.oio.inframirror.repository.AgentLockRepository;
import vibhuvi.oio.inframirror.repository.search.AgentLockSearchRepository;
import vibhuvi.oio.inframirror.service.dto.AgentLockDTO;
import vibhuvi.oio.inframirror.service.mapper.AgentLockMapper;

/**
 * Integration tests for the {@link AgentLockResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AgentLockResourceIT {

    private static final Long DEFAULT_AGENT_ID = 1L;
    private static final Long UPDATED_AGENT_ID = 2L;

    private static final Instant DEFAULT_ACQUIRED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ACQUIRED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_EXPIRES_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EXPIRES_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/agent-locks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/agent-locks/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AgentLockRepository agentLockRepository;

    @Autowired
    private AgentLockMapper agentLockMapper;

    @Autowired
    private AgentLockSearchRepository agentLockSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAgentLockMockMvc;

    private AgentLock agentLock;

    private AgentLock insertedAgentLock;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AgentLock createEntity() {
        return new AgentLock().agentId(DEFAULT_AGENT_ID).acquiredAt(DEFAULT_ACQUIRED_AT).expiresAt(DEFAULT_EXPIRES_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AgentLock createUpdatedEntity() {
        return new AgentLock().agentId(UPDATED_AGENT_ID).acquiredAt(UPDATED_ACQUIRED_AT).expiresAt(UPDATED_EXPIRES_AT);
    }

    @BeforeEach
    void initTest() {
        agentLock = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAgentLock != null) {
            agentLockRepository.delete(insertedAgentLock);
            agentLockSearchRepository.delete(insertedAgentLock);
            insertedAgentLock = null;
        }
    }

    @Test
    @Transactional
    void createAgentLock() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(agentLockSearchRepository.findAll());
        // Create the AgentLock
        AgentLockDTO agentLockDTO = agentLockMapper.toDto(agentLock);
        var returnedAgentLockDTO = om.readValue(
            restAgentLockMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(agentLockDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AgentLockDTO.class
        );

        // Validate the AgentLock in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAgentLock = agentLockMapper.toEntity(returnedAgentLockDTO);
        assertAgentLockUpdatableFieldsEquals(returnedAgentLock, getPersistedAgentLock(returnedAgentLock));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(agentLockSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedAgentLock = returnedAgentLock;
    }

    @Test
    @Transactional
    void createAgentLockWithExistingId() throws Exception {
        // Create the AgentLock with an existing ID
        agentLock.setId(1L);
        AgentLockDTO agentLockDTO = agentLockMapper.toDto(agentLock);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(agentLockSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restAgentLockMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(agentLockDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AgentLock in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(agentLockSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkAgentIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(agentLockSearchRepository.findAll());
        // set the field null
        agentLock.setAgentId(null);

        // Create the AgentLock, which fails.
        AgentLockDTO agentLockDTO = agentLockMapper.toDto(agentLock);

        restAgentLockMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(agentLockDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(agentLockSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkAcquiredAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(agentLockSearchRepository.findAll());
        // set the field null
        agentLock.setAcquiredAt(null);

        // Create the AgentLock, which fails.
        AgentLockDTO agentLockDTO = agentLockMapper.toDto(agentLock);

        restAgentLockMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(agentLockDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(agentLockSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkExpiresAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(agentLockSearchRepository.findAll());
        // set the field null
        agentLock.setExpiresAt(null);

        // Create the AgentLock, which fails.
        AgentLockDTO agentLockDTO = agentLockMapper.toDto(agentLock);

        restAgentLockMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(agentLockDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(agentLockSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllAgentLocks() throws Exception {
        // Initialize the database
        insertedAgentLock = agentLockRepository.saveAndFlush(agentLock);

        // Get all the agentLockList
        restAgentLockMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(agentLock.getId().intValue())))
            .andExpect(jsonPath("$.[*].agentId").value(hasItem(DEFAULT_AGENT_ID.intValue())))
            .andExpect(jsonPath("$.[*].acquiredAt").value(hasItem(DEFAULT_ACQUIRED_AT.toString())))
            .andExpect(jsonPath("$.[*].expiresAt").value(hasItem(DEFAULT_EXPIRES_AT.toString())));
    }

    @Test
    @Transactional
    void getAgentLock() throws Exception {
        // Initialize the database
        insertedAgentLock = agentLockRepository.saveAndFlush(agentLock);

        // Get the agentLock
        restAgentLockMockMvc
            .perform(get(ENTITY_API_URL_ID, agentLock.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(agentLock.getId().intValue()))
            .andExpect(jsonPath("$.agentId").value(DEFAULT_AGENT_ID.intValue()))
            .andExpect(jsonPath("$.acquiredAt").value(DEFAULT_ACQUIRED_AT.toString()))
            .andExpect(jsonPath("$.expiresAt").value(DEFAULT_EXPIRES_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingAgentLock() throws Exception {
        // Get the agentLock
        restAgentLockMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAgentLock() throws Exception {
        // Initialize the database
        insertedAgentLock = agentLockRepository.saveAndFlush(agentLock);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        agentLockSearchRepository.save(agentLock);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(agentLockSearchRepository.findAll());

        // Update the agentLock
        AgentLock updatedAgentLock = agentLockRepository.findById(agentLock.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAgentLock are not directly saved in db
        em.detach(updatedAgentLock);
        updatedAgentLock.agentId(UPDATED_AGENT_ID).acquiredAt(UPDATED_ACQUIRED_AT).expiresAt(UPDATED_EXPIRES_AT);
        AgentLockDTO agentLockDTO = agentLockMapper.toDto(updatedAgentLock);

        restAgentLockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, agentLockDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(agentLockDTO))
            )
            .andExpect(status().isOk());

        // Validate the AgentLock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAgentLockToMatchAllProperties(updatedAgentLock);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(agentLockSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<AgentLock> agentLockSearchList = Streamable.of(agentLockSearchRepository.findAll()).toList();
                AgentLock testAgentLockSearch = agentLockSearchList.get(searchDatabaseSizeAfter - 1);

                assertAgentLockAllPropertiesEquals(testAgentLockSearch, updatedAgentLock);
            });
    }

    @Test
    @Transactional
    void putNonExistingAgentLock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(agentLockSearchRepository.findAll());
        agentLock.setId(longCount.incrementAndGet());

        // Create the AgentLock
        AgentLockDTO agentLockDTO = agentLockMapper.toDto(agentLock);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAgentLockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, agentLockDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(agentLockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AgentLock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(agentLockSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchAgentLock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(agentLockSearchRepository.findAll());
        agentLock.setId(longCount.incrementAndGet());

        // Create the AgentLock
        AgentLockDTO agentLockDTO = agentLockMapper.toDto(agentLock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAgentLockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(agentLockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AgentLock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(agentLockSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAgentLock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(agentLockSearchRepository.findAll());
        agentLock.setId(longCount.incrementAndGet());

        // Create the AgentLock
        AgentLockDTO agentLockDTO = agentLockMapper.toDto(agentLock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAgentLockMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(agentLockDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AgentLock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(agentLockSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateAgentLockWithPatch() throws Exception {
        // Initialize the database
        insertedAgentLock = agentLockRepository.saveAndFlush(agentLock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the agentLock using partial update
        AgentLock partialUpdatedAgentLock = new AgentLock();
        partialUpdatedAgentLock.setId(agentLock.getId());

        restAgentLockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAgentLock.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAgentLock))
            )
            .andExpect(status().isOk());

        // Validate the AgentLock in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAgentLockUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAgentLock, agentLock),
            getPersistedAgentLock(agentLock)
        );
    }

    @Test
    @Transactional
    void fullUpdateAgentLockWithPatch() throws Exception {
        // Initialize the database
        insertedAgentLock = agentLockRepository.saveAndFlush(agentLock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the agentLock using partial update
        AgentLock partialUpdatedAgentLock = new AgentLock();
        partialUpdatedAgentLock.setId(agentLock.getId());

        partialUpdatedAgentLock.agentId(UPDATED_AGENT_ID).acquiredAt(UPDATED_ACQUIRED_AT).expiresAt(UPDATED_EXPIRES_AT);

        restAgentLockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAgentLock.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAgentLock))
            )
            .andExpect(status().isOk());

        // Validate the AgentLock in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAgentLockUpdatableFieldsEquals(partialUpdatedAgentLock, getPersistedAgentLock(partialUpdatedAgentLock));
    }

    @Test
    @Transactional
    void patchNonExistingAgentLock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(agentLockSearchRepository.findAll());
        agentLock.setId(longCount.incrementAndGet());

        // Create the AgentLock
        AgentLockDTO agentLockDTO = agentLockMapper.toDto(agentLock);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAgentLockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, agentLockDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(agentLockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AgentLock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(agentLockSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAgentLock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(agentLockSearchRepository.findAll());
        agentLock.setId(longCount.incrementAndGet());

        // Create the AgentLock
        AgentLockDTO agentLockDTO = agentLockMapper.toDto(agentLock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAgentLockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(agentLockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AgentLock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(agentLockSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAgentLock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(agentLockSearchRepository.findAll());
        agentLock.setId(longCount.incrementAndGet());

        // Create the AgentLock
        AgentLockDTO agentLockDTO = agentLockMapper.toDto(agentLock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAgentLockMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(agentLockDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AgentLock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(agentLockSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteAgentLock() throws Exception {
        // Initialize the database
        insertedAgentLock = agentLockRepository.saveAndFlush(agentLock);
        agentLockRepository.save(agentLock);
        agentLockSearchRepository.save(agentLock);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(agentLockSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the agentLock
        restAgentLockMockMvc
            .perform(delete(ENTITY_API_URL_ID, agentLock.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(agentLockSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchAgentLock() throws Exception {
        // Initialize the database
        insertedAgentLock = agentLockRepository.saveAndFlush(agentLock);
        agentLockSearchRepository.save(agentLock);

        // Search the agentLock
        restAgentLockMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + agentLock.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(agentLock.getId().intValue())))
            .andExpect(jsonPath("$.[*].agentId").value(hasItem(DEFAULT_AGENT_ID.intValue())))
            .andExpect(jsonPath("$.[*].acquiredAt").value(hasItem(DEFAULT_ACQUIRED_AT.toString())))
            .andExpect(jsonPath("$.[*].expiresAt").value(hasItem(DEFAULT_EXPIRES_AT.toString())));
    }

    protected long getRepositoryCount() {
        return agentLockRepository.count();
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

    protected AgentLock getPersistedAgentLock(AgentLock agentLock) {
        return agentLockRepository.findById(agentLock.getId()).orElseThrow();
    }

    protected void assertPersistedAgentLockToMatchAllProperties(AgentLock expectedAgentLock) {
        assertAgentLockAllPropertiesEquals(expectedAgentLock, getPersistedAgentLock(expectedAgentLock));
    }

    protected void assertPersistedAgentLockToMatchUpdatableProperties(AgentLock expectedAgentLock) {
        assertAgentLockAllUpdatablePropertiesEquals(expectedAgentLock, getPersistedAgentLock(expectedAgentLock));
    }
}
