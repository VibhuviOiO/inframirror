package vibhuvi.oio.inframirror.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static vibhuvi.oio.inframirror.domain.DatacenterAsserts.*;
import static vibhuvi.oio.inframirror.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
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
import vibhuvi.oio.inframirror.domain.Datacenter;
import vibhuvi.oio.inframirror.domain.Region;
import vibhuvi.oio.inframirror.repository.DatacenterRepository;
import vibhuvi.oio.inframirror.repository.search.DatacenterSearchRepository;
import vibhuvi.oio.inframirror.service.dto.DatacenterDTO;
import vibhuvi.oio.inframirror.service.mapper.DatacenterMapper;

/**
 * Integration tests for the {@link DatacenterResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DatacenterResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/datacenters";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/datacenters/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DatacenterRepository datacenterRepository;

    @Autowired
    private DatacenterMapper datacenterMapper;

    @Autowired
    private DatacenterSearchRepository datacenterSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDatacenterMockMvc;

    private Datacenter datacenter;

    private Datacenter insertedDatacenter;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Datacenter createEntity() {
        return new Datacenter().code(DEFAULT_CODE).name(DEFAULT_NAME);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Datacenter createUpdatedEntity() {
        return new Datacenter().code(UPDATED_CODE).name(UPDATED_NAME);
    }

    @BeforeEach
    void initTest() {
        datacenter = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedDatacenter != null) {
            datacenterRepository.delete(insertedDatacenter);
            datacenterSearchRepository.delete(insertedDatacenter);
            insertedDatacenter = null;
        }
    }

    @Test
    @Transactional
    void createDatacenter() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(datacenterSearchRepository.findAll());
        // Create the Datacenter
        DatacenterDTO datacenterDTO = datacenterMapper.toDto(datacenter);
        var returnedDatacenterDTO = om.readValue(
            restDatacenterMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(datacenterDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DatacenterDTO.class
        );

        // Validate the Datacenter in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDatacenter = datacenterMapper.toEntity(returnedDatacenterDTO);
        assertDatacenterUpdatableFieldsEquals(returnedDatacenter, getPersistedDatacenter(returnedDatacenter));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(datacenterSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedDatacenter = returnedDatacenter;
    }

    @Test
    @Transactional
    void createDatacenterWithExistingId() throws Exception {
        // Create the Datacenter with an existing ID
        datacenter.setId(1L);
        DatacenterDTO datacenterDTO = datacenterMapper.toDto(datacenter);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(datacenterSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restDatacenterMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(datacenterDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Datacenter in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(datacenterSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(datacenterSearchRepository.findAll());
        // set the field null
        datacenter.setCode(null);

        // Create the Datacenter, which fails.
        DatacenterDTO datacenterDTO = datacenterMapper.toDto(datacenter);

        restDatacenterMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(datacenterDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(datacenterSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(datacenterSearchRepository.findAll());
        // set the field null
        datacenter.setName(null);

        // Create the Datacenter, which fails.
        DatacenterDTO datacenterDTO = datacenterMapper.toDto(datacenter);

        restDatacenterMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(datacenterDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(datacenterSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllDatacenters() throws Exception {
        // Initialize the database
        insertedDatacenter = datacenterRepository.saveAndFlush(datacenter);

        // Get all the datacenterList
        restDatacenterMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(datacenter.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getDatacenter() throws Exception {
        // Initialize the database
        insertedDatacenter = datacenterRepository.saveAndFlush(datacenter);

        // Get the datacenter
        restDatacenterMockMvc
            .perform(get(ENTITY_API_URL_ID, datacenter.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(datacenter.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getDatacentersByIdFiltering() throws Exception {
        // Initialize the database
        insertedDatacenter = datacenterRepository.saveAndFlush(datacenter);

        Long id = datacenter.getId();

        defaultDatacenterFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultDatacenterFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultDatacenterFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDatacentersByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDatacenter = datacenterRepository.saveAndFlush(datacenter);

        // Get all the datacenterList where code equals to
        defaultDatacenterFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllDatacentersByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDatacenter = datacenterRepository.saveAndFlush(datacenter);

        // Get all the datacenterList where code in
        defaultDatacenterFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllDatacentersByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDatacenter = datacenterRepository.saveAndFlush(datacenter);

        // Get all the datacenterList where code is not null
        defaultDatacenterFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllDatacentersByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedDatacenter = datacenterRepository.saveAndFlush(datacenter);

        // Get all the datacenterList where code contains
        defaultDatacenterFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllDatacentersByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDatacenter = datacenterRepository.saveAndFlush(datacenter);

        // Get all the datacenterList where code does not contain
        defaultDatacenterFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllDatacentersByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDatacenter = datacenterRepository.saveAndFlush(datacenter);

        // Get all the datacenterList where name equals to
        defaultDatacenterFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllDatacentersByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDatacenter = datacenterRepository.saveAndFlush(datacenter);

        // Get all the datacenterList where name in
        defaultDatacenterFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllDatacentersByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDatacenter = datacenterRepository.saveAndFlush(datacenter);

        // Get all the datacenterList where name is not null
        defaultDatacenterFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllDatacentersByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedDatacenter = datacenterRepository.saveAndFlush(datacenter);

        // Get all the datacenterList where name contains
        defaultDatacenterFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllDatacentersByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDatacenter = datacenterRepository.saveAndFlush(datacenter);

        // Get all the datacenterList where name does not contain
        defaultDatacenterFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllDatacentersByRegionIsEqualToSomething() throws Exception {
        Region region;
        if (TestUtil.findAll(em, Region.class).isEmpty()) {
            datacenterRepository.saveAndFlush(datacenter);
            region = RegionResourceIT.createEntity();
        } else {
            region = TestUtil.findAll(em, Region.class).get(0);
        }
        em.persist(region);
        em.flush();
        datacenter.setRegion(region);
        datacenterRepository.saveAndFlush(datacenter);
        Long regionId = region.getId();
        // Get all the datacenterList where region equals to regionId
        defaultDatacenterShouldBeFound("regionId.equals=" + regionId);

        // Get all the datacenterList where region equals to (regionId + 1)
        defaultDatacenterShouldNotBeFound("regionId.equals=" + (regionId + 1));
    }

    private void defaultDatacenterFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultDatacenterShouldBeFound(shouldBeFound);
        defaultDatacenterShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDatacenterShouldBeFound(String filter) throws Exception {
        restDatacenterMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(datacenter.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));

        // Check, that the count call also returns 1
        restDatacenterMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDatacenterShouldNotBeFound(String filter) throws Exception {
        restDatacenterMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDatacenterMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDatacenter() throws Exception {
        // Get the datacenter
        restDatacenterMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDatacenter() throws Exception {
        // Initialize the database
        insertedDatacenter = datacenterRepository.saveAndFlush(datacenter);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        datacenterSearchRepository.save(datacenter);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(datacenterSearchRepository.findAll());

        // Update the datacenter
        Datacenter updatedDatacenter = datacenterRepository.findById(datacenter.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDatacenter are not directly saved in db
        em.detach(updatedDatacenter);
        updatedDatacenter.code(UPDATED_CODE).name(UPDATED_NAME);
        DatacenterDTO datacenterDTO = datacenterMapper.toDto(updatedDatacenter);

        restDatacenterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, datacenterDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(datacenterDTO))
            )
            .andExpect(status().isOk());

        // Validate the Datacenter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDatacenterToMatchAllProperties(updatedDatacenter);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(datacenterSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Datacenter> datacenterSearchList = Streamable.of(datacenterSearchRepository.findAll()).toList();
                Datacenter testDatacenterSearch = datacenterSearchList.get(searchDatabaseSizeAfter - 1);

                assertDatacenterAllPropertiesEquals(testDatacenterSearch, updatedDatacenter);
            });
    }

    @Test
    @Transactional
    void putNonExistingDatacenter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(datacenterSearchRepository.findAll());
        datacenter.setId(longCount.incrementAndGet());

        // Create the Datacenter
        DatacenterDTO datacenterDTO = datacenterMapper.toDto(datacenter);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDatacenterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, datacenterDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(datacenterDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Datacenter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(datacenterSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchDatacenter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(datacenterSearchRepository.findAll());
        datacenter.setId(longCount.incrementAndGet());

        // Create the Datacenter
        DatacenterDTO datacenterDTO = datacenterMapper.toDto(datacenter);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDatacenterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(datacenterDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Datacenter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(datacenterSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDatacenter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(datacenterSearchRepository.findAll());
        datacenter.setId(longCount.incrementAndGet());

        // Create the Datacenter
        DatacenterDTO datacenterDTO = datacenterMapper.toDto(datacenter);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDatacenterMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(datacenterDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Datacenter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(datacenterSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateDatacenterWithPatch() throws Exception {
        // Initialize the database
        insertedDatacenter = datacenterRepository.saveAndFlush(datacenter);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the datacenter using partial update
        Datacenter partialUpdatedDatacenter = new Datacenter();
        partialUpdatedDatacenter.setId(datacenter.getId());

        partialUpdatedDatacenter.code(UPDATED_CODE).name(UPDATED_NAME);

        restDatacenterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDatacenter.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDatacenter))
            )
            .andExpect(status().isOk());

        // Validate the Datacenter in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDatacenterUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDatacenter, datacenter),
            getPersistedDatacenter(datacenter)
        );
    }

    @Test
    @Transactional
    void fullUpdateDatacenterWithPatch() throws Exception {
        // Initialize the database
        insertedDatacenter = datacenterRepository.saveAndFlush(datacenter);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the datacenter using partial update
        Datacenter partialUpdatedDatacenter = new Datacenter();
        partialUpdatedDatacenter.setId(datacenter.getId());

        partialUpdatedDatacenter.code(UPDATED_CODE).name(UPDATED_NAME);

        restDatacenterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDatacenter.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDatacenter))
            )
            .andExpect(status().isOk());

        // Validate the Datacenter in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDatacenterUpdatableFieldsEquals(partialUpdatedDatacenter, getPersistedDatacenter(partialUpdatedDatacenter));
    }

    @Test
    @Transactional
    void patchNonExistingDatacenter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(datacenterSearchRepository.findAll());
        datacenter.setId(longCount.incrementAndGet());

        // Create the Datacenter
        DatacenterDTO datacenterDTO = datacenterMapper.toDto(datacenter);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDatacenterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, datacenterDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(datacenterDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Datacenter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(datacenterSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDatacenter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(datacenterSearchRepository.findAll());
        datacenter.setId(longCount.incrementAndGet());

        // Create the Datacenter
        DatacenterDTO datacenterDTO = datacenterMapper.toDto(datacenter);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDatacenterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(datacenterDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Datacenter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(datacenterSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDatacenter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(datacenterSearchRepository.findAll());
        datacenter.setId(longCount.incrementAndGet());

        // Create the Datacenter
        DatacenterDTO datacenterDTO = datacenterMapper.toDto(datacenter);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDatacenterMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(datacenterDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Datacenter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(datacenterSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteDatacenter() throws Exception {
        // Initialize the database
        insertedDatacenter = datacenterRepository.saveAndFlush(datacenter);
        datacenterRepository.save(datacenter);
        datacenterSearchRepository.save(datacenter);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(datacenterSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the datacenter
        restDatacenterMockMvc
            .perform(delete(ENTITY_API_URL_ID, datacenter.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(datacenterSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchDatacenter() throws Exception {
        // Initialize the database
        insertedDatacenter = datacenterRepository.saveAndFlush(datacenter);
        datacenterSearchRepository.save(datacenter);

        // Search the datacenter
        restDatacenterMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + datacenter.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(datacenter.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    protected long getRepositoryCount() {
        return datacenterRepository.count();
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

    protected Datacenter getPersistedDatacenter(Datacenter datacenter) {
        return datacenterRepository.findById(datacenter.getId()).orElseThrow();
    }

    protected void assertPersistedDatacenterToMatchAllProperties(Datacenter expectedDatacenter) {
        assertDatacenterAllPropertiesEquals(expectedDatacenter, getPersistedDatacenter(expectedDatacenter));
    }

    protected void assertPersistedDatacenterToMatchUpdatableProperties(Datacenter expectedDatacenter) {
        assertDatacenterAllUpdatablePropertiesEquals(expectedDatacenter, getPersistedDatacenter(expectedDatacenter));
    }
}
