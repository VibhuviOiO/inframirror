package vibhuvi.oio.inframirror.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static vibhuvi.oio.inframirror.domain.ServiceHeartbeatAsserts.*;
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
import vibhuvi.oio.inframirror.domain.Service;
import vibhuvi.oio.inframirror.domain.ServiceHeartbeat;
import vibhuvi.oio.inframirror.repository.ServiceHeartbeatRepository;
import vibhuvi.oio.inframirror.repository.search.ServiceHeartbeatSearchRepository;
import vibhuvi.oio.inframirror.service.dto.ServiceHeartbeatDTO;
import vibhuvi.oio.inframirror.service.mapper.ServiceHeartbeatMapper;

/**
 * Integration tests for the {@link ServiceHeartbeatResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ServiceHeartbeatResourceIT {

    private static final Instant DEFAULT_EXECUTED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EXECUTED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_SUCCESS = false;
    private static final Boolean UPDATED_SUCCESS = true;

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final Integer DEFAULT_RESPONSE_TIME_MS = 1;
    private static final Integer UPDATED_RESPONSE_TIME_MS = 2;

    private static final String DEFAULT_ERROR_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_ERROR_MESSAGE = "BBBBBBBBBB";

    private static final String DEFAULT_METADATA = "AAAAAAAAAA";
    private static final String UPDATED_METADATA = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/service-heartbeats";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/service-heartbeats/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ServiceHeartbeatRepository serviceHeartbeatRepository;

    @Autowired
    private ServiceHeartbeatMapper serviceHeartbeatMapper;

    @Autowired
    private ServiceHeartbeatSearchRepository serviceHeartbeatSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restServiceHeartbeatMockMvc;

    private ServiceHeartbeat serviceHeartbeat;

    private ServiceHeartbeat insertedServiceHeartbeat;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ServiceHeartbeat createEntity(EntityManager em) {
        ServiceHeartbeat serviceHeartbeat = new ServiceHeartbeat()
            .executedAt(DEFAULT_EXECUTED_AT)
            .success(DEFAULT_SUCCESS)
            .status(DEFAULT_STATUS)
            .responseTimeMs(DEFAULT_RESPONSE_TIME_MS)
            .errorMessage(DEFAULT_ERROR_MESSAGE)
            .metadata(DEFAULT_METADATA);
        // Add required entity
        Service service;
        if (TestUtil.findAll(em, Service.class).isEmpty()) {
            service = ServiceResourceIT.createEntity();
            em.persist(service);
            em.flush();
        } else {
            service = TestUtil.findAll(em, Service.class).get(0);
        }
        serviceHeartbeat.setService(service);
        return serviceHeartbeat;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ServiceHeartbeat createUpdatedEntity(EntityManager em) {
        ServiceHeartbeat updatedServiceHeartbeat = new ServiceHeartbeat()
            .executedAt(UPDATED_EXECUTED_AT)
            .success(UPDATED_SUCCESS)
            .status(UPDATED_STATUS)
            .responseTimeMs(UPDATED_RESPONSE_TIME_MS)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .metadata(UPDATED_METADATA);
        // Add required entity
        Service service;
        if (TestUtil.findAll(em, Service.class).isEmpty()) {
            service = ServiceResourceIT.createUpdatedEntity();
            em.persist(service);
            em.flush();
        } else {
            service = TestUtil.findAll(em, Service.class).get(0);
        }
        updatedServiceHeartbeat.setService(service);
        return updatedServiceHeartbeat;
    }

    @BeforeEach
    void initTest() {
        serviceHeartbeat = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedServiceHeartbeat != null) {
            serviceHeartbeatRepository.delete(insertedServiceHeartbeat);
            serviceHeartbeatSearchRepository.delete(insertedServiceHeartbeat);
            insertedServiceHeartbeat = null;
        }
    }

    @Test
    @Transactional
    void createServiceHeartbeat() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(serviceHeartbeatSearchRepository.findAll());
        // Create the ServiceHeartbeat
        ServiceHeartbeatDTO serviceHeartbeatDTO = serviceHeartbeatMapper.toDto(serviceHeartbeat);
        var returnedServiceHeartbeatDTO = om.readValue(
            restServiceHeartbeatMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(serviceHeartbeatDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ServiceHeartbeatDTO.class
        );

        // Validate the ServiceHeartbeat in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedServiceHeartbeat = serviceHeartbeatMapper.toEntity(returnedServiceHeartbeatDTO);
        assertServiceHeartbeatUpdatableFieldsEquals(returnedServiceHeartbeat, getPersistedServiceHeartbeat(returnedServiceHeartbeat));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(serviceHeartbeatSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedServiceHeartbeat = returnedServiceHeartbeat;
    }

    @Test
    @Transactional
    void createServiceHeartbeatWithExistingId() throws Exception {
        // Create the ServiceHeartbeat with an existing ID
        serviceHeartbeat.setId(1L);
        ServiceHeartbeatDTO serviceHeartbeatDTO = serviceHeartbeatMapper.toDto(serviceHeartbeat);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(serviceHeartbeatSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restServiceHeartbeatMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(serviceHeartbeatDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ServiceHeartbeat in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(serviceHeartbeatSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkExecutedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(serviceHeartbeatSearchRepository.findAll());
        // set the field null
        serviceHeartbeat.setExecutedAt(null);

        // Create the ServiceHeartbeat, which fails.
        ServiceHeartbeatDTO serviceHeartbeatDTO = serviceHeartbeatMapper.toDto(serviceHeartbeat);

        restServiceHeartbeatMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(serviceHeartbeatDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(serviceHeartbeatSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkSuccessIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(serviceHeartbeatSearchRepository.findAll());
        // set the field null
        serviceHeartbeat.setSuccess(null);

        // Create the ServiceHeartbeat, which fails.
        ServiceHeartbeatDTO serviceHeartbeatDTO = serviceHeartbeatMapper.toDto(serviceHeartbeat);

        restServiceHeartbeatMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(serviceHeartbeatDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(serviceHeartbeatSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(serviceHeartbeatSearchRepository.findAll());
        // set the field null
        serviceHeartbeat.setStatus(null);

        // Create the ServiceHeartbeat, which fails.
        ServiceHeartbeatDTO serviceHeartbeatDTO = serviceHeartbeatMapper.toDto(serviceHeartbeat);

        restServiceHeartbeatMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(serviceHeartbeatDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(serviceHeartbeatSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllServiceHeartbeats() throws Exception {
        // Initialize the database
        insertedServiceHeartbeat = serviceHeartbeatRepository.saveAndFlush(serviceHeartbeat);

        // Get all the serviceHeartbeatList
        restServiceHeartbeatMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(serviceHeartbeat.getId().intValue())))
            .andExpect(jsonPath("$.[*].executedAt").value(hasItem(DEFAULT_EXECUTED_AT.toString())))
            .andExpect(jsonPath("$.[*].success").value(hasItem(DEFAULT_SUCCESS)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].responseTimeMs").value(hasItem(DEFAULT_RESPONSE_TIME_MS)))
            .andExpect(jsonPath("$.[*].errorMessage").value(hasItem(DEFAULT_ERROR_MESSAGE)))
            .andExpect(jsonPath("$.[*].metadata").value(hasItem(DEFAULT_METADATA)));
    }

    @Test
    @Transactional
    void getServiceHeartbeat() throws Exception {
        // Initialize the database
        insertedServiceHeartbeat = serviceHeartbeatRepository.saveAndFlush(serviceHeartbeat);

        // Get the serviceHeartbeat
        restServiceHeartbeatMockMvc
            .perform(get(ENTITY_API_URL_ID, serviceHeartbeat.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(serviceHeartbeat.getId().intValue()))
            .andExpect(jsonPath("$.executedAt").value(DEFAULT_EXECUTED_AT.toString()))
            .andExpect(jsonPath("$.success").value(DEFAULT_SUCCESS))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.responseTimeMs").value(DEFAULT_RESPONSE_TIME_MS))
            .andExpect(jsonPath("$.errorMessage").value(DEFAULT_ERROR_MESSAGE))
            .andExpect(jsonPath("$.metadata").value(DEFAULT_METADATA));
    }

    @Test
    @Transactional
    void getNonExistingServiceHeartbeat() throws Exception {
        // Get the serviceHeartbeat
        restServiceHeartbeatMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingServiceHeartbeat() throws Exception {
        // Initialize the database
        insertedServiceHeartbeat = serviceHeartbeatRepository.saveAndFlush(serviceHeartbeat);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        serviceHeartbeatSearchRepository.save(serviceHeartbeat);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(serviceHeartbeatSearchRepository.findAll());

        // Update the serviceHeartbeat
        ServiceHeartbeat updatedServiceHeartbeat = serviceHeartbeatRepository.findById(serviceHeartbeat.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedServiceHeartbeat are not directly saved in db
        em.detach(updatedServiceHeartbeat);
        updatedServiceHeartbeat
            .executedAt(UPDATED_EXECUTED_AT)
            .success(UPDATED_SUCCESS)
            .status(UPDATED_STATUS)
            .responseTimeMs(UPDATED_RESPONSE_TIME_MS)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .metadata(UPDATED_METADATA);
        ServiceHeartbeatDTO serviceHeartbeatDTO = serviceHeartbeatMapper.toDto(updatedServiceHeartbeat);

        restServiceHeartbeatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, serviceHeartbeatDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(serviceHeartbeatDTO))
            )
            .andExpect(status().isOk());

        // Validate the ServiceHeartbeat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedServiceHeartbeatToMatchAllProperties(updatedServiceHeartbeat);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(serviceHeartbeatSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ServiceHeartbeat> serviceHeartbeatSearchList = Streamable.of(serviceHeartbeatSearchRepository.findAll()).toList();
                ServiceHeartbeat testServiceHeartbeatSearch = serviceHeartbeatSearchList.get(searchDatabaseSizeAfter - 1);

                assertServiceHeartbeatAllPropertiesEquals(testServiceHeartbeatSearch, updatedServiceHeartbeat);
            });
    }

    @Test
    @Transactional
    void putNonExistingServiceHeartbeat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(serviceHeartbeatSearchRepository.findAll());
        serviceHeartbeat.setId(longCount.incrementAndGet());

        // Create the ServiceHeartbeat
        ServiceHeartbeatDTO serviceHeartbeatDTO = serviceHeartbeatMapper.toDto(serviceHeartbeat);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restServiceHeartbeatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, serviceHeartbeatDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(serviceHeartbeatDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ServiceHeartbeat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(serviceHeartbeatSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchServiceHeartbeat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(serviceHeartbeatSearchRepository.findAll());
        serviceHeartbeat.setId(longCount.incrementAndGet());

        // Create the ServiceHeartbeat
        ServiceHeartbeatDTO serviceHeartbeatDTO = serviceHeartbeatMapper.toDto(serviceHeartbeat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServiceHeartbeatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(serviceHeartbeatDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ServiceHeartbeat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(serviceHeartbeatSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamServiceHeartbeat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(serviceHeartbeatSearchRepository.findAll());
        serviceHeartbeat.setId(longCount.incrementAndGet());

        // Create the ServiceHeartbeat
        ServiceHeartbeatDTO serviceHeartbeatDTO = serviceHeartbeatMapper.toDto(serviceHeartbeat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServiceHeartbeatMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(serviceHeartbeatDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ServiceHeartbeat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(serviceHeartbeatSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateServiceHeartbeatWithPatch() throws Exception {
        // Initialize the database
        insertedServiceHeartbeat = serviceHeartbeatRepository.saveAndFlush(serviceHeartbeat);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the serviceHeartbeat using partial update
        ServiceHeartbeat partialUpdatedServiceHeartbeat = new ServiceHeartbeat();
        partialUpdatedServiceHeartbeat.setId(serviceHeartbeat.getId());

        partialUpdatedServiceHeartbeat.executedAt(UPDATED_EXECUTED_AT).status(UPDATED_STATUS).errorMessage(UPDATED_ERROR_MESSAGE);

        restServiceHeartbeatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedServiceHeartbeat.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedServiceHeartbeat))
            )
            .andExpect(status().isOk());

        // Validate the ServiceHeartbeat in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertServiceHeartbeatUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedServiceHeartbeat, serviceHeartbeat),
            getPersistedServiceHeartbeat(serviceHeartbeat)
        );
    }

    @Test
    @Transactional
    void fullUpdateServiceHeartbeatWithPatch() throws Exception {
        // Initialize the database
        insertedServiceHeartbeat = serviceHeartbeatRepository.saveAndFlush(serviceHeartbeat);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the serviceHeartbeat using partial update
        ServiceHeartbeat partialUpdatedServiceHeartbeat = new ServiceHeartbeat();
        partialUpdatedServiceHeartbeat.setId(serviceHeartbeat.getId());

        partialUpdatedServiceHeartbeat
            .executedAt(UPDATED_EXECUTED_AT)
            .success(UPDATED_SUCCESS)
            .status(UPDATED_STATUS)
            .responseTimeMs(UPDATED_RESPONSE_TIME_MS)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .metadata(UPDATED_METADATA);

        restServiceHeartbeatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedServiceHeartbeat.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedServiceHeartbeat))
            )
            .andExpect(status().isOk());

        // Validate the ServiceHeartbeat in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertServiceHeartbeatUpdatableFieldsEquals(
            partialUpdatedServiceHeartbeat,
            getPersistedServiceHeartbeat(partialUpdatedServiceHeartbeat)
        );
    }

    @Test
    @Transactional
    void patchNonExistingServiceHeartbeat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(serviceHeartbeatSearchRepository.findAll());
        serviceHeartbeat.setId(longCount.incrementAndGet());

        // Create the ServiceHeartbeat
        ServiceHeartbeatDTO serviceHeartbeatDTO = serviceHeartbeatMapper.toDto(serviceHeartbeat);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restServiceHeartbeatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, serviceHeartbeatDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(serviceHeartbeatDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ServiceHeartbeat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(serviceHeartbeatSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchServiceHeartbeat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(serviceHeartbeatSearchRepository.findAll());
        serviceHeartbeat.setId(longCount.incrementAndGet());

        // Create the ServiceHeartbeat
        ServiceHeartbeatDTO serviceHeartbeatDTO = serviceHeartbeatMapper.toDto(serviceHeartbeat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServiceHeartbeatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(serviceHeartbeatDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ServiceHeartbeat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(serviceHeartbeatSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamServiceHeartbeat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(serviceHeartbeatSearchRepository.findAll());
        serviceHeartbeat.setId(longCount.incrementAndGet());

        // Create the ServiceHeartbeat
        ServiceHeartbeatDTO serviceHeartbeatDTO = serviceHeartbeatMapper.toDto(serviceHeartbeat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServiceHeartbeatMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(serviceHeartbeatDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ServiceHeartbeat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(serviceHeartbeatSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteServiceHeartbeat() throws Exception {
        // Initialize the database
        insertedServiceHeartbeat = serviceHeartbeatRepository.saveAndFlush(serviceHeartbeat);
        serviceHeartbeatRepository.save(serviceHeartbeat);
        serviceHeartbeatSearchRepository.save(serviceHeartbeat);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(serviceHeartbeatSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the serviceHeartbeat
        restServiceHeartbeatMockMvc
            .perform(delete(ENTITY_API_URL_ID, serviceHeartbeat.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(serviceHeartbeatSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchServiceHeartbeat() throws Exception {
        // Initialize the database
        insertedServiceHeartbeat = serviceHeartbeatRepository.saveAndFlush(serviceHeartbeat);
        serviceHeartbeatSearchRepository.save(serviceHeartbeat);

        // Search the serviceHeartbeat
        restServiceHeartbeatMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + serviceHeartbeat.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(serviceHeartbeat.getId().intValue())))
            .andExpect(jsonPath("$.[*].executedAt").value(hasItem(DEFAULT_EXECUTED_AT.toString())))
            .andExpect(jsonPath("$.[*].success").value(hasItem(DEFAULT_SUCCESS)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].responseTimeMs").value(hasItem(DEFAULT_RESPONSE_TIME_MS)))
            .andExpect(jsonPath("$.[*].errorMessage").value(hasItem(DEFAULT_ERROR_MESSAGE.toString())))
            .andExpect(jsonPath("$.[*].metadata").value(hasItem(DEFAULT_METADATA.toString())));
    }

    protected long getRepositoryCount() {
        return serviceHeartbeatRepository.count();
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

    protected ServiceHeartbeat getPersistedServiceHeartbeat(ServiceHeartbeat serviceHeartbeat) {
        return serviceHeartbeatRepository.findById(serviceHeartbeat.getId()).orElseThrow();
    }

    protected void assertPersistedServiceHeartbeatToMatchAllProperties(ServiceHeartbeat expectedServiceHeartbeat) {
        assertServiceHeartbeatAllPropertiesEquals(expectedServiceHeartbeat, getPersistedServiceHeartbeat(expectedServiceHeartbeat));
    }

    protected void assertPersistedServiceHeartbeatToMatchUpdatableProperties(ServiceHeartbeat expectedServiceHeartbeat) {
        assertServiceHeartbeatAllUpdatablePropertiesEquals(
            expectedServiceHeartbeat,
            getPersistedServiceHeartbeat(expectedServiceHeartbeat)
        );
    }
}
