package vibhuvi.oio.inframirror.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static vibhuvi.oio.inframirror.domain.RegionAsserts.*;
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
import vibhuvi.oio.inframirror.domain.Region;
import vibhuvi.oio.inframirror.repository.RegionRepository;

import vibhuvi.oio.inframirror.service.dto.RegionDTO;
import vibhuvi.oio.inframirror.service.mapper.RegionMapper;

/**
 * Integration tests for the {@link RegionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RegionResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_REGION_CODE = "AAAAAAAAAA";
    private static final String UPDATED_REGION_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_GROUP_NAME = "AAAAAAAAAA";
    private static final String UPDATED_GROUP_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/regions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/regions/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;
    
    private RegionRepository regionRepository;

    @Autowired
    private RegionMapper regionMapper;
    

    private EntityManager em;

    @Autowired
    private MockMvc restRegionMockMvc;

    private Region region;

    private Region insertedRegion;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Region createEntity() {
        return new Region().name(DEFAULT_NAME).regionCode(DEFAULT_REGION_CODE).groupName(DEFAULT_GROUP_NAME);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Region createUpdatedEntity() {
        return new Region().name(UPDATED_NAME).regionCode(UPDATED_REGION_CODE).groupName(UPDATED_GROUP_NAME);
    }

    @BeforeEach
    void initTest() {
        region = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedRegion != null) {
            regionRepository.delete(insertedRegion);
            insertedRegion = null;
        }
    }

    @Test
    @Transactional
    void createRegion() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Region
        RegionDTO regionDTO = regionMapper.toDto(region);
        var returnedRegionDTO = om.readValue(
            restRegionMockMvc
                .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(regionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            RegionDTO.class
        );

        // Validate the Region in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedRegion = regionMapper.toEntity(returnedRegionDTO);
        assertRegionUpdatableFieldsEquals(returnedRegion, getPersistedRegion(returnedRegion));


        insertedRegion = returnedRegion;
    }

    @Test
    @Transactional
    void createRegionWithExistingId() throws Exception {
        // Create the Region with an existing ID
        region.setId(1L);
        RegionDTO regionDTO = regionMapper.toDto(region);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRegionMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(regionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Region in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        region.setName(null);

        // Create the Region, which fails.
        RegionDTO regionDTO = regionMapper.toDto(region);

        restRegionMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(regionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

    }

    @Test
    @Transactional
    void getAllRegions() throws Exception {
        // Initialize the database
        insertedRegion = regionRepository.saveAndFlush(region);

        // Get all the regionList
        restRegionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(region.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].regionCode").value(hasItem(DEFAULT_REGION_CODE)))
            .andExpect(jsonPath("$.[*].groupName").value(hasItem(DEFAULT_GROUP_NAME)));
    }

    @Test
    @Transactional
    void getRegion() throws Exception {
        // Initialize the database
        insertedRegion = regionRepository.saveAndFlush(region);

        // Get the region
        restRegionMockMvc
            .perform(get(ENTITY_API_URL_ID, region.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(region.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.regionCode").value(DEFAULT_REGION_CODE))
            .andExpect(jsonPath("$.groupName").value(DEFAULT_GROUP_NAME));
    }

    @Test
    @Transactional
    void getRegionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedRegion = regionRepository.saveAndFlush(region);

        Long id = region.getId();

        defaultRegionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultRegionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultRegionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllRegionsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRegion = regionRepository.saveAndFlush(region);

        // Get all the regionList where name equals to
        defaultRegionFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllRegionsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRegion = regionRepository.saveAndFlush(region);

        // Get all the regionList where name in
        defaultRegionFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllRegionsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRegion = regionRepository.saveAndFlush(region);

        // Get all the regionList where name is not null
        defaultRegionFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllRegionsByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedRegion = regionRepository.saveAndFlush(region);

        // Get all the regionList where name contains
        defaultRegionFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllRegionsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRegion = regionRepository.saveAndFlush(region);

        // Get all the regionList where name does not contain
        defaultRegionFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllRegionsByRegionCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRegion = regionRepository.saveAndFlush(region);

        // Get all the regionList where regionCode equals to
        defaultRegionFiltering("regionCode.equals=" + DEFAULT_REGION_CODE, "regionCode.equals=" + UPDATED_REGION_CODE);
    }

    @Test
    @Transactional
    void getAllRegionsByRegionCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRegion = regionRepository.saveAndFlush(region);

        // Get all the regionList where regionCode in
        defaultRegionFiltering("regionCode.in=" + DEFAULT_REGION_CODE + "," + UPDATED_REGION_CODE, "regionCode.in=" + UPDATED_REGION_CODE);
    }

    @Test
    @Transactional
    void getAllRegionsByRegionCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRegion = regionRepository.saveAndFlush(region);

        // Get all the regionList where regionCode is not null
        defaultRegionFiltering("regionCode.specified=true", "regionCode.specified=false");
    }

    @Test
    @Transactional
    void getAllRegionsByRegionCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedRegion = regionRepository.saveAndFlush(region);

        // Get all the regionList where regionCode contains
        defaultRegionFiltering("regionCode.contains=" + DEFAULT_REGION_CODE, "regionCode.contains=" + UPDATED_REGION_CODE);
    }

    @Test
    @Transactional
    void getAllRegionsByRegionCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRegion = regionRepository.saveAndFlush(region);

        // Get all the regionList where regionCode does not contain
        defaultRegionFiltering("regionCode.doesNotContain=" + UPDATED_REGION_CODE, "regionCode.doesNotContain=" + DEFAULT_REGION_CODE);
    }

    @Test
    @Transactional
    void getAllRegionsByGroupNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRegion = regionRepository.saveAndFlush(region);

        // Get all the regionList where groupName equals to
        defaultRegionFiltering("groupName.equals=" + DEFAULT_GROUP_NAME, "groupName.equals=" + UPDATED_GROUP_NAME);
    }

    @Test
    @Transactional
    void getAllRegionsByGroupNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRegion = regionRepository.saveAndFlush(region);

        // Get all the regionList where groupName in
        defaultRegionFiltering("groupName.in=" + DEFAULT_GROUP_NAME + "," + UPDATED_GROUP_NAME, "groupName.in=" + UPDATED_GROUP_NAME);
    }

    @Test
    @Transactional
    void getAllRegionsByGroupNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRegion = regionRepository.saveAndFlush(region);

        // Get all the regionList where groupName is not null
        defaultRegionFiltering("groupName.specified=true", "groupName.specified=false");
    }

    @Test
    @Transactional
    void getAllRegionsByGroupNameContainsSomething() throws Exception {
        // Initialize the database
        insertedRegion = regionRepository.saveAndFlush(region);

        // Get all the regionList where groupName contains
        defaultRegionFiltering("groupName.contains=" + DEFAULT_GROUP_NAME, "groupName.contains=" + UPDATED_GROUP_NAME);
    }

    @Test
    @Transactional
    void getAllRegionsByGroupNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRegion = regionRepository.saveAndFlush(region);

        // Get all the regionList where groupName does not contain
        defaultRegionFiltering("groupName.doesNotContain=" + UPDATED_GROUP_NAME, "groupName.doesNotContain=" + DEFAULT_GROUP_NAME);
    }

    private void defaultRegionFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultRegionShouldBeFound(shouldBeFound);
        defaultRegionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRegionShouldBeFound(String filter) throws Exception {
        restRegionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(region.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].regionCode").value(hasItem(DEFAULT_REGION_CODE)))
            .andExpect(jsonPath("$.[*].groupName").value(hasItem(DEFAULT_GROUP_NAME)));

        // Check, that the count call also returns 1
        restRegionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRegionShouldNotBeFound(String filter) throws Exception {
        restRegionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRegionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingRegion() throws Exception {
        // Get the region
        restRegionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRegion() throws Exception {
        // Initialize the database
        insertedRegion = regionRepository.saveAndFlush(region);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the region
        Region updatedRegion = regionRepository.findById(region.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedRegion are not directly saved in db
        em.detach(updatedRegion);
        updatedRegion.name(UPDATED_NAME).regionCode(UPDATED_REGION_CODE).groupName(UPDATED_GROUP_NAME);
        RegionDTO regionDTO = regionMapper.toDto(updatedRegion);

        restRegionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, regionDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(regionDTO))
            )
            .andExpect(status().isOk());

        // Validate the Region in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRegionToMatchAllProperties(updatedRegion);

    }

    @Test
    @Transactional
    void putNonExistingRegion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        region.setId(longCount.incrementAndGet());

        // Create the Region
        RegionDTO regionDTO = regionMapper.toDto(region);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRegionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, regionDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(regionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Region in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRegion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        region.setId(longCount.incrementAndGet());

        // Create the Region
        RegionDTO regionDTO = regionMapper.toDto(region);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRegionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(regionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Region in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRegion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        region.setId(longCount.incrementAndGet());

        // Create the Region
        RegionDTO regionDTO = regionMapper.toDto(region);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRegionMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(regionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Region in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRegionWithPatch() throws Exception {
        // Initialize the database
        insertedRegion = regionRepository.saveAndFlush(region);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the region using partial update
        Region partialUpdatedRegion = new Region();
        partialUpdatedRegion.setId(region.getId());

        partialUpdatedRegion.groupName(UPDATED_GROUP_NAME);

        restRegionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRegion.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRegion))
            )
            .andExpect(status().isOk());

        // Validate the Region in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRegionUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedRegion, region), getPersistedRegion(region));
    }

    @Test
    @Transactional
    void fullUpdateRegionWithPatch() throws Exception {
        // Initialize the database
        insertedRegion = regionRepository.saveAndFlush(region);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the region using partial update
        Region partialUpdatedRegion = new Region();
        partialUpdatedRegion.setId(region.getId());

        partialUpdatedRegion.name(UPDATED_NAME).regionCode(UPDATED_REGION_CODE).groupName(UPDATED_GROUP_NAME);

        restRegionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRegion.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRegion))
            )
            .andExpect(status().isOk());

        // Validate the Region in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRegionUpdatableFieldsEquals(partialUpdatedRegion, getPersistedRegion(partialUpdatedRegion));
    }

    @Test
    @Transactional
    void patchNonExistingRegion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        region.setId(longCount.incrementAndGet());

        // Create the Region
        RegionDTO regionDTO = regionMapper.toDto(region);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRegionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, regionDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(regionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Region in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRegion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        region.setId(longCount.incrementAndGet());

        // Create the Region
        RegionDTO regionDTO = regionMapper.toDto(region);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRegionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(regionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Region in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRegion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        region.setId(longCount.incrementAndGet());

        // Create the Region
        RegionDTO regionDTO = regionMapper.toDto(region);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRegionMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(regionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Region in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRegion() throws Exception {
        // Initialize the database
        insertedRegion = regionRepository.saveAndFlush(region);
        regionRepository.save(region);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the region
        restRegionMockMvc
            .perform(delete(ENTITY_API_URL_ID, region.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    @Test
    @Transactional
    void searchRegion() throws Exception {
        // Initialize the database
        insertedRegion = regionRepository.saveAndFlush(region);
        // Flush to trigger database trigger for search_vector
        em.flush();
        em.clear();

        // Search the region by name (not ID, as ID search doesn't use FTS)
        restRegionMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=" + DEFAULT_NAME.substring(0, 3)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(region.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].regionCode").value(hasItem(DEFAULT_REGION_CODE)))
            .andExpect(jsonPath("$.[*].groupName").value(hasItem(DEFAULT_GROUP_NAME)));
    }

    @Test
    @Transactional
    void searchRegionByName() throws Exception {
        // Initialize the database
        insertedRegion = regionRepository.saveAndFlush(region);
        em.flush();
        em.clear();

        // Search by name prefix
        restRegionMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=" + DEFAULT_NAME.substring(0, 3)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(region.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void searchRegionPrefix() throws Exception {
        // Initialize the database
        insertedRegion = regionRepository.saveAndFlush(region);
        em.flush();
        em.clear();

        // Prefix search for autocomplete
        restRegionMockMvc
            .perform(get("/api/regions/_search/prefix?query=" + DEFAULT_NAME.substring(0, 2)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(region.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void searchRegionFuzzy() throws Exception {
        // Initialize the database
        insertedRegion = regionRepository.saveAndFlush(region);
        em.flush();
        em.clear();

        // Fuzzy search with typo tolerance
        restRegionMockMvc
            .perform(get("/api/regions/_search/fuzzy?query=" + DEFAULT_NAME))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    @Transactional
    void searchRegionWithHighlight() throws Exception {
        // Initialize the database
        insertedRegion = regionRepository.saveAndFlush(region);
        em.flush();
        em.clear();

        // Search with highlighting - just verify endpoint works and returns proper structure
        // Note: May return empty if no matches, but structure should be valid
        restRegionMockMvc
            .perform(get("/api/regions/_search/highlight?query=" + DEFAULT_NAME.substring(0, 3)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    @Transactional
    void searchRegionEmptyQuery() throws Exception {
        // Initialize the database
        insertedRegion = regionRepository.saveAndFlush(region);
        em.flush();
        em.clear();

        // Empty query should return all results
        restRegionMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query="))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(region.getId().intValue())));
    }

    protected long getRepositoryCount() {
        return regionRepository.count();
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

    protected Region getPersistedRegion(Region region) {
        return regionRepository.findById(region.getId()).orElseThrow();
    }

    protected void assertPersistedRegionToMatchAllProperties(Region expectedRegion) {
        assertRegionAllPropertiesEquals(expectedRegion, getPersistedRegion(expectedRegion));
    }

    protected void assertPersistedRegionToMatchUpdatableProperties(Region expectedRegion) {
        assertRegionAllUpdatablePropertiesEquals(expectedRegion, getPersistedRegion(expectedRegion));
    }
}
