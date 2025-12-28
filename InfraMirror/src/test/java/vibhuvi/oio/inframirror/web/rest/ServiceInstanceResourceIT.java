package vibhuvi.oio.inframirror.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static vibhuvi.oio.inframirror.domain.ServiceInstanceAsserts.*;
import static vibhuvi.oio.inframirror.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
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
import vibhuvi.oio.inframirror.domain.Instance;
import vibhuvi.oio.inframirror.domain.MonitoredService;
import vibhuvi.oio.inframirror.domain.ServiceInstance;
import vibhuvi.oio.inframirror.repository.ServiceInstanceRepository;
import vibhuvi.oio.inframirror.service.dto.ServiceInstanceDTO;
import vibhuvi.oio.inframirror.service.mapper.ServiceInstanceMapper;

/**
 * Integration tests for the {@link ServiceInstanceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ServiceInstanceResourceIT {

    private static final Integer DEFAULT_PORT = 1;
    private static final Integer UPDATED_PORT = 2;

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/service-instances";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/service-instances/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;
    
    private ServiceInstanceRepository serviceInstanceRepository;

    @Autowired
    private ServiceInstanceMapper serviceInstanceMapper;
    
    private EntityManager em;

    @Autowired
    private MockMvc restServiceInstanceMockMvc;

    private ServiceInstance serviceInstance;

    private ServiceInstance insertedServiceInstance;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ServiceInstance createEntity(EntityManager em) {
        ServiceInstance serviceInstance = new ServiceInstance()
            .port(DEFAULT_PORT)
            .isActive(DEFAULT_IS_ACTIVE)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
        // Add required entity
        Instance instance;
        if (TestUtil.findAll(em, Instance.class).isEmpty()) {
            instance = InstanceResourceIT.createEntity(em);
            em.persist(instance);
            em.flush();
        } else {
            instance = TestUtil.findAll(em, Instance.class).get(0);
        }
        serviceInstance.setInstance(instance);
        // Add required entity
        MonitoredService monitoredService;
        if (TestUtil.findAll(em, MonitoredService.class).isEmpty()) {
            monitoredService = MonitoredServiceResourceIT.createEntity();
            em.persist(monitoredService);
            em.flush();
        } else {
            monitoredService = TestUtil.findAll(em, MonitoredService.class).get(0);
        }
        serviceInstance.setMonitoredService(monitoredService);
        return serviceInstance;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ServiceInstance createUpdatedEntity(EntityManager em) {
        ServiceInstance updatedServiceInstance = new ServiceInstance()
            .port(UPDATED_PORT)
            .isActive(UPDATED_IS_ACTIVE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        // Add required entity
        Instance instance;
        if (TestUtil.findAll(em, Instance.class).isEmpty()) {
            instance = InstanceResourceIT.createUpdatedEntity(em);
            em.persist(instance);
            em.flush();
        } else {
            instance = TestUtil.findAll(em, Instance.class).get(0);
        }
        updatedServiceInstance.setInstance(instance);
        // Add required entity
        MonitoredService monitoredService;
        if (TestUtil.findAll(em, MonitoredService.class).isEmpty()) {
            monitoredService = MonitoredServiceResourceIT.createUpdatedEntity();
            em.persist(monitoredService);
            em.flush();
        } else {
            monitoredService = TestUtil.findAll(em, MonitoredService.class).get(0);
        }
        updatedServiceInstance.setMonitoredService(monitoredService);
        return updatedServiceInstance;
    }

    @BeforeEach
    void initTest() {
        serviceInstance = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedServiceInstance != null) {
            serviceInstanceRepository.delete(insertedServiceInstance);
            insertedServiceInstance = null;
        }
    }

    @Test
    @Transactional
    void createServiceInstance() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ServiceInstance
        ServiceInstanceDTO serviceInstanceDTO = serviceInstanceMapper.toDto(serviceInstance);
        var returnedServiceInstanceDTO = om.readValue(
            restServiceInstanceMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(serviceInstanceDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ServiceInstanceDTO.class
        );

        // Validate the ServiceInstance in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedServiceInstance = serviceInstanceMapper.toEntity(returnedServiceInstanceDTO);
        assertServiceInstanceUpdatableFieldsEquals(returnedServiceInstance, getPersistedServiceInstance(returnedServiceInstance));

        insertedServiceInstance = returnedServiceInstance;
    }

    @Test
    @Transactional
    void createServiceInstanceWithExistingId() throws Exception {
        // Create the ServiceInstance with an existing ID
        serviceInstance.setId(1L);
        ServiceInstanceDTO serviceInstanceDTO = serviceInstanceMapper.toDto(serviceInstance);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restServiceInstanceMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(serviceInstanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ServiceInstance in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPortIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        serviceInstance.setPort(null);

        // Create the ServiceInstance, which fails.
        ServiceInstanceDTO serviceInstanceDTO = serviceInstanceMapper.toDto(serviceInstance);

        restServiceInstanceMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(serviceInstanceDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllServiceInstances() throws Exception {
        // Initialize the database
        insertedServiceInstance = serviceInstanceRepository.saveAndFlush(serviceInstance);

        // Get all the serviceInstanceList
        restServiceInstanceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(serviceInstance.getId().intValue())))
            .andExpect(jsonPath("$.[*].port").value(hasItem(DEFAULT_PORT)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getServiceInstance() throws Exception {
        // Initialize the database
        insertedServiceInstance = serviceInstanceRepository.saveAndFlush(serviceInstance);

        // Get the serviceInstance
        restServiceInstanceMockMvc
            .perform(get(ENTITY_API_URL_ID, serviceInstance.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(serviceInstance.getId().intValue()))
            .andExpect(jsonPath("$.port").value(DEFAULT_PORT))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingServiceInstance() throws Exception {
        // Get the serviceInstance
        restServiceInstanceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingServiceInstance() throws Exception {
        // Initialize the database
        insertedServiceInstance = serviceInstanceRepository.saveAndFlush(serviceInstance);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the serviceInstance
        ServiceInstance updatedServiceInstance = serviceInstanceRepository.findById(serviceInstance.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedServiceInstance are not directly saved in db
        em.detach(updatedServiceInstance);
        updatedServiceInstance.port(UPDATED_PORT).isActive(UPDATED_IS_ACTIVE).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);
        ServiceInstanceDTO serviceInstanceDTO = serviceInstanceMapper.toDto(updatedServiceInstance);

        restServiceInstanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, serviceInstanceDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(serviceInstanceDTO))
            )
            .andExpect(status().isOk());

        // Validate the ServiceInstance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedServiceInstanceToMatchAllProperties(updatedServiceInstance);
    }

    @Test
    @Transactional
    void putNonExistingServiceInstance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serviceInstance.setId(longCount.incrementAndGet());

        // Create the ServiceInstance
        ServiceInstanceDTO serviceInstanceDTO = serviceInstanceMapper.toDto(serviceInstance);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restServiceInstanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, serviceInstanceDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(serviceInstanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ServiceInstance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchServiceInstance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serviceInstance.setId(longCount.incrementAndGet());

        // Create the ServiceInstance
        ServiceInstanceDTO serviceInstanceDTO = serviceInstanceMapper.toDto(serviceInstance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServiceInstanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(serviceInstanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ServiceInstance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamServiceInstance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serviceInstance.setId(longCount.incrementAndGet());

        // Create the ServiceInstance
        ServiceInstanceDTO serviceInstanceDTO = serviceInstanceMapper.toDto(serviceInstance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServiceInstanceMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(serviceInstanceDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ServiceInstance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateServiceInstanceWithPatch() throws Exception {
        // Initialize the database
        insertedServiceInstance = serviceInstanceRepository.saveAndFlush(serviceInstance);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the serviceInstance using partial update
        ServiceInstance partialUpdatedServiceInstance = new ServiceInstance();
        partialUpdatedServiceInstance.setId(serviceInstance.getId());

        partialUpdatedServiceInstance.updatedAt(UPDATED_UPDATED_AT);

        restServiceInstanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedServiceInstance.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedServiceInstance))
            )
            .andExpect(status().isOk());

        // Validate the ServiceInstance in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertServiceInstanceUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedServiceInstance, serviceInstance),
            getPersistedServiceInstance(serviceInstance)
        );
    }

    @Test
    @Transactional
    void fullUpdateServiceInstanceWithPatch() throws Exception {
        // Initialize the database
        insertedServiceInstance = serviceInstanceRepository.saveAndFlush(serviceInstance);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the serviceInstance using partial update
        ServiceInstance partialUpdatedServiceInstance = new ServiceInstance();
        partialUpdatedServiceInstance.setId(serviceInstance.getId());

        partialUpdatedServiceInstance
            .port(UPDATED_PORT)
            .isActive(UPDATED_IS_ACTIVE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restServiceInstanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedServiceInstance.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedServiceInstance))
            )
            .andExpect(status().isOk());

        // Validate the ServiceInstance in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertServiceInstanceUpdatableFieldsEquals(
            partialUpdatedServiceInstance,
            getPersistedServiceInstance(partialUpdatedServiceInstance)
        );
    }

    @Test
    @Transactional
    void patchNonExistingServiceInstance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serviceInstance.setId(longCount.incrementAndGet());

        // Create the ServiceInstance
        ServiceInstanceDTO serviceInstanceDTO = serviceInstanceMapper.toDto(serviceInstance);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restServiceInstanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, serviceInstanceDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(serviceInstanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ServiceInstance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchServiceInstance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serviceInstance.setId(longCount.incrementAndGet());

        // Create the ServiceInstance
        ServiceInstanceDTO serviceInstanceDTO = serviceInstanceMapper.toDto(serviceInstance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServiceInstanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(serviceInstanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ServiceInstance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamServiceInstance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serviceInstance.setId(longCount.incrementAndGet());

        // Create the ServiceInstance
        ServiceInstanceDTO serviceInstanceDTO = serviceInstanceMapper.toDto(serviceInstance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServiceInstanceMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(serviceInstanceDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ServiceInstance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteServiceInstance() throws Exception {
        // Initialize the database
        insertedServiceInstance = serviceInstanceRepository.saveAndFlush(serviceInstance);
        serviceInstanceRepository.save(serviceInstance);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the serviceInstance
        restServiceInstanceMockMvc
            .perform(delete(ENTITY_API_URL_ID, serviceInstance.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    @Test
    @Transactional
    void searchServiceInstance() throws Exception {
        // Initialize the database
        insertedServiceInstance = serviceInstanceRepository.saveAndFlush(serviceInstance);

        // Search returns empty as ServiceInstance has no FTS implementation
        restServiceInstanceMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + serviceInstance.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    protected long getRepositoryCount() {
        return serviceInstanceRepository.count();
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

    protected ServiceInstance getPersistedServiceInstance(ServiceInstance serviceInstance) {
        return serviceInstanceRepository.findById(serviceInstance.getId()).orElseThrow();
    }

    protected void assertPersistedServiceInstanceToMatchAllProperties(ServiceInstance expectedServiceInstance) {
        assertServiceInstanceAllPropertiesEquals(expectedServiceInstance, getPersistedServiceInstance(expectedServiceInstance));
    }

    protected void assertPersistedServiceInstanceToMatchUpdatableProperties(ServiceInstance expectedServiceInstance) {
        assertServiceInstanceAllUpdatablePropertiesEquals(expectedServiceInstance, getPersistedServiceInstance(expectedServiceInstance));
    }
}
