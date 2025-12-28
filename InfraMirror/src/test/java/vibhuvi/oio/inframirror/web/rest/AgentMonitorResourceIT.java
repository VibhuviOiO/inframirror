package vibhuvi.oio.inframirror.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static vibhuvi.oio.inframirror.domain.AgentMonitorAsserts.*;
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
import vibhuvi.oio.inframirror.domain.Agent;
import vibhuvi.oio.inframirror.domain.AgentMonitor;
import vibhuvi.oio.inframirror.domain.HttpMonitor;
import vibhuvi.oio.inframirror.repository.AgentMonitorRepository;
import vibhuvi.oio.inframirror.service.dto.AgentMonitorDTO;
import vibhuvi.oio.inframirror.service.mapper.AgentMonitorMapper;

/**
 * Integration tests for the {@link AgentMonitorResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AgentMonitorResourceIT {

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/agent-monitors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/agent-monitors/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;
    
    private AgentMonitorRepository agentMonitorRepository;

    @Autowired
    private AgentMonitorMapper agentMonitorMapper;
    

    @Autowired
    private EntityManager em;
    
    private MockMvc restAgentMonitorMockMvc;

    private AgentMonitor agentMonitor;

    private AgentMonitor insertedAgentMonitor;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AgentMonitor createEntity(EntityManager em) {
        AgentMonitor agentMonitor = new AgentMonitor()
            .active(DEFAULT_ACTIVE)
            .createdBy(DEFAULT_CREATED_BY)
            .createdDate(DEFAULT_CREATED_DATE)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY)
            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE);
        // Add required entity
        Agent agent;
        if (TestUtil.findAll(em, Agent.class).isEmpty()) {
            agent = AgentResourceIT.createEntity();
            em.persist(agent);
            em.flush();
        } else {
            agent = TestUtil.findAll(em, Agent.class).get(0);
        }
        agentMonitor.setAgent(agent);
        // Add required entity
        HttpMonitor httpMonitor;
        if (TestUtil.findAll(em, HttpMonitor.class).isEmpty()) {
            httpMonitor = HttpMonitorResourceIT.createEntity();
            em.persist(httpMonitor);
            em.flush();
        } else {
            httpMonitor = TestUtil.findAll(em, HttpMonitor.class).get(0);
        }
        agentMonitor.setMonitor(httpMonitor);
        return agentMonitor;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AgentMonitor createUpdatedEntity(EntityManager em) {
        AgentMonitor updatedAgentMonitor = new AgentMonitor()
            .active(UPDATED_ACTIVE)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        // Add required entity
        Agent agent;
        if (TestUtil.findAll(em, Agent.class).isEmpty()) {
            agent = AgentResourceIT.createUpdatedEntity();
            em.persist(agent);
            em.flush();
        } else {
            agent = TestUtil.findAll(em, Agent.class).get(0);
        }
        updatedAgentMonitor.setAgent(agent);
        // Add required entity
        HttpMonitor httpMonitor;
        if (TestUtil.findAll(em, HttpMonitor.class).isEmpty()) {
            httpMonitor = HttpMonitorResourceIT.createUpdatedEntity();
            em.persist(httpMonitor);
            em.flush();
        } else {
            httpMonitor = TestUtil.findAll(em, HttpMonitor.class).get(0);
        }
        updatedAgentMonitor.setMonitor(httpMonitor);
        return updatedAgentMonitor;
    }

    @BeforeEach
    void initTest() {
        agentMonitor = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedAgentMonitor != null) {
            agentMonitorRepository.delete(insertedAgentMonitor);
            insertedAgentMonitor = null;
        }
    }

    @Test
    @Transactional
    void createAgentMonitor() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AgentMonitor
        AgentMonitorDTO agentMonitorDTO = agentMonitorMapper.toDto(agentMonitor);
        var returnedAgentMonitorDTO = om.readValue(
            restAgentMonitorMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(agentMonitorDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AgentMonitorDTO.class
        );

        // Validate the AgentMonitor in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAgentMonitor = agentMonitorMapper.toEntity(returnedAgentMonitorDTO);
        assertAgentMonitorUpdatableFieldsEquals(returnedAgentMonitor, getPersistedAgentMonitor(returnedAgentMonitor));        insertedAgentMonitor = returnedAgentMonitor;
    }

    @Test
    @Transactional
    void createAgentMonitorWithExistingId() throws Exception {
        // Create the AgentMonitor with an existing ID
        agentMonitor.setId(1L);
        AgentMonitorDTO agentMonitorDTO = agentMonitorMapper.toDto(agentMonitor);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAgentMonitorMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(agentMonitorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AgentMonitor in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        agentMonitor.setActive(null);

        // Create the AgentMonitor, which fails.
        AgentMonitorDTO agentMonitorDTO = agentMonitorMapper.toDto(agentMonitor);

        restAgentMonitorMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(agentMonitorDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

    }

    @Test
    @Transactional
    void checkCreatedByIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        agentMonitor.setCreatedBy(null);

        // Create the AgentMonitor, which fails.
        AgentMonitorDTO agentMonitorDTO = agentMonitorMapper.toDto(agentMonitor);

        restAgentMonitorMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(agentMonitorDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

    }

    @Test
    @Transactional
    void getAllAgentMonitors() throws Exception {
        // Initialize the database
        insertedAgentMonitor = agentMonitorRepository.saveAndFlush(agentMonitor);

        // Get all the agentMonitorList
        restAgentMonitorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(agentMonitor.getId().intValue())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));
    }

    @Test
    @Transactional
    void getAgentMonitor() throws Exception {
        // Initialize the database
        insertedAgentMonitor = agentMonitorRepository.saveAndFlush(agentMonitor);

        // Get the agentMonitor
        restAgentMonitorMockMvc
            .perform(get(ENTITY_API_URL_ID, agentMonitor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(agentMonitor.getId().intValue()))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingAgentMonitor() throws Exception {
        // Get the agentMonitor
        restAgentMonitorMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAgentMonitor() throws Exception {
        // Initialize the database
        insertedAgentMonitor = agentMonitorRepository.saveAndFlush(agentMonitor);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the agentMonitor
        AgentMonitor updatedAgentMonitor = agentMonitorRepository.findById(agentMonitor.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAgentMonitor are not directly saved in db
        em.detach(updatedAgentMonitor);
        updatedAgentMonitor
            .active(UPDATED_ACTIVE)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        AgentMonitorDTO agentMonitorDTO = agentMonitorMapper.toDto(updatedAgentMonitor);

        restAgentMonitorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, agentMonitorDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(agentMonitorDTO))
            )
            .andExpect(status().isOk());

        // Validate the AgentMonitor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAgentMonitorToMatchAllProperties(updatedAgentMonitor);    }

    @Test
    @Transactional
    void putNonExistingAgentMonitor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        agentMonitor.setId(longCount.incrementAndGet());

        // Create the AgentMonitor
        AgentMonitorDTO agentMonitorDTO = agentMonitorMapper.toDto(agentMonitor);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAgentMonitorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, agentMonitorDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(agentMonitorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AgentMonitor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAgentMonitor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        agentMonitor.setId(longCount.incrementAndGet());

        // Create the AgentMonitor
        AgentMonitorDTO agentMonitorDTO = agentMonitorMapper.toDto(agentMonitor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAgentMonitorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(agentMonitorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AgentMonitor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAgentMonitor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        agentMonitor.setId(longCount.incrementAndGet());

        // Create the AgentMonitor
        AgentMonitorDTO agentMonitorDTO = agentMonitorMapper.toDto(agentMonitor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAgentMonitorMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(agentMonitorDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AgentMonitor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAgentMonitorWithPatch() throws Exception {
        // Initialize the database
        insertedAgentMonitor = agentMonitorRepository.saveAndFlush(agentMonitor);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the agentMonitor using partial update
        AgentMonitor partialUpdatedAgentMonitor = new AgentMonitor();
        partialUpdatedAgentMonitor.setId(agentMonitor.getId());

        partialUpdatedAgentMonitor.lastModifiedBy(UPDATED_LAST_MODIFIED_BY).lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);

        restAgentMonitorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAgentMonitor.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAgentMonitor))
            )
            .andExpect(status().isOk());

        // Validate the AgentMonitor in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAgentMonitorUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAgentMonitor, agentMonitor),
            getPersistedAgentMonitor(agentMonitor)
        );
    }

    @Test
    @Transactional
    void fullUpdateAgentMonitorWithPatch() throws Exception {
        // Initialize the database
        insertedAgentMonitor = agentMonitorRepository.saveAndFlush(agentMonitor);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the agentMonitor using partial update
        AgentMonitor partialUpdatedAgentMonitor = new AgentMonitor();
        partialUpdatedAgentMonitor.setId(agentMonitor.getId());

        partialUpdatedAgentMonitor
            .active(UPDATED_ACTIVE)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);

        restAgentMonitorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAgentMonitor.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAgentMonitor))
            )
            .andExpect(status().isOk());

        // Validate the AgentMonitor in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAgentMonitorUpdatableFieldsEquals(partialUpdatedAgentMonitor, getPersistedAgentMonitor(partialUpdatedAgentMonitor));
    }

    @Test
    @Transactional
    void patchNonExistingAgentMonitor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        agentMonitor.setId(longCount.incrementAndGet());

        // Create the AgentMonitor
        AgentMonitorDTO agentMonitorDTO = agentMonitorMapper.toDto(agentMonitor);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAgentMonitorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, agentMonitorDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(agentMonitorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AgentMonitor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAgentMonitor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        agentMonitor.setId(longCount.incrementAndGet());

        // Create the AgentMonitor
        AgentMonitorDTO agentMonitorDTO = agentMonitorMapper.toDto(agentMonitor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAgentMonitorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(agentMonitorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AgentMonitor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAgentMonitor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        agentMonitor.setId(longCount.incrementAndGet());

        // Create the AgentMonitor
        AgentMonitorDTO agentMonitorDTO = agentMonitorMapper.toDto(agentMonitor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAgentMonitorMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(agentMonitorDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AgentMonitor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAgentMonitor() throws Exception {
        // Initialize the database
        insertedAgentMonitor = agentMonitorRepository.saveAndFlush(agentMonitor);
        agentMonitorRepository.save(agentMonitor);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the agentMonitor
        restAgentMonitorMockMvc
            .perform(delete(ENTITY_API_URL_ID, agentMonitor.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    @Test
    @Transactional
    void searchAgentMonitor() throws Exception {
        // Initialize the database
        insertedAgentMonitor = agentMonitorRepository.saveAndFlush(agentMonitor);

        // Search the agentMonitor
        restAgentMonitorMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + agentMonitor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(agentMonitor.getId().intValue())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));
    }

    protected long getRepositoryCount() {
        return agentMonitorRepository.count();
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

    protected AgentMonitor getPersistedAgentMonitor(AgentMonitor agentMonitor) {
        return agentMonitorRepository.findById(agentMonitor.getId()).orElseThrow();
    }

    protected void assertPersistedAgentMonitorToMatchAllProperties(AgentMonitor expectedAgentMonitor) {
        assertAgentMonitorAllPropertiesEquals(expectedAgentMonitor, getPersistedAgentMonitor(expectedAgentMonitor));
    }

    protected void assertPersistedAgentMonitorToMatchUpdatableProperties(AgentMonitor expectedAgentMonitor) {
        assertAgentMonitorAllUpdatablePropertiesEquals(expectedAgentMonitor, getPersistedAgentMonitor(expectedAgentMonitor));
    }
}
