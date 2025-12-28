package vibhuvi.oio.inframirror.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static vibhuvi.oio.inframirror.domain.BrandingAsserts.*;
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
import vibhuvi.oio.inframirror.domain.Branding;
import vibhuvi.oio.inframirror.repository.BrandingRepository;
import vibhuvi.oio.inframirror.service.dto.BrandingDTO;
import vibhuvi.oio.inframirror.service.mapper.BrandingMapper;

/**
 * Integration tests for the {@link BrandingResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BrandingResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_KEYWORDS = "AAAAAAAAAA";
    private static final String UPDATED_KEYWORDS = "BBBBBBBBBB";

    private static final String DEFAULT_AUTHOR = "AAAAAAAAAA";
    private static final String UPDATED_AUTHOR = "BBBBBBBBBB";

    private static final String DEFAULT_FAVICON_PATH = "AAAAAAAAAA";
    private static final String UPDATED_FAVICON_PATH = "BBBBBBBBBB";

    private static final String DEFAULT_LOGO_PATH = "AAAAAAAAAA";
    private static final String UPDATED_LOGO_PATH = "BBBBBBBBBB";

    private static final Integer DEFAULT_LOGO_WIDTH = 1;
    private static final Integer UPDATED_LOGO_WIDTH = 2;

    private static final Integer DEFAULT_LOGO_HEIGHT = 1;
    private static final Integer UPDATED_LOGO_HEIGHT = 2;

    private static final String DEFAULT_FOOTER_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_FOOTER_TITLE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/brandings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/brandings/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;
    
    private BrandingRepository brandingRepository;

    @Autowired
    private BrandingMapper brandingMapper;
    

    @Autowired
    private EntityManager em;
    
    private MockMvc restBrandingMockMvc;

    private Branding branding;

    private Branding insertedBranding;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Branding createEntity() {
        return new Branding()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .keywords(DEFAULT_KEYWORDS)
            .author(DEFAULT_AUTHOR)
            .faviconPath(DEFAULT_FAVICON_PATH)
            .logoPath(DEFAULT_LOGO_PATH)
            .logoWidth(DEFAULT_LOGO_WIDTH)
            .logoHeight(DEFAULT_LOGO_HEIGHT)
            .footerTitle(DEFAULT_FOOTER_TITLE)
            .isActive(DEFAULT_IS_ACTIVE)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Branding createUpdatedEntity() {
        return new Branding()
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .keywords(UPDATED_KEYWORDS)
            .author(UPDATED_AUTHOR)
            .faviconPath(UPDATED_FAVICON_PATH)
            .logoPath(UPDATED_LOGO_PATH)
            .logoWidth(UPDATED_LOGO_WIDTH)
            .logoHeight(UPDATED_LOGO_HEIGHT)
            .footerTitle(UPDATED_FOOTER_TITLE)
            .isActive(UPDATED_IS_ACTIVE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        branding = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedBranding != null) {
            brandingRepository.delete(insertedBranding);
            insertedBranding = null;
        }
    }

    @Test
    @Transactional
    void createBranding() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Branding
        BrandingDTO brandingDTO = brandingMapper.toDto(branding);
        var returnedBrandingDTO = om.readValue(
            restBrandingMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(brandingDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            BrandingDTO.class
        );

        // Validate the Branding in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedBranding = brandingMapper.toEntity(returnedBrandingDTO);
        assertBrandingUpdatableFieldsEquals(returnedBranding, getPersistedBranding(returnedBranding));        insertedBranding = returnedBranding;
    }

    @Test
    @Transactional
    void createBrandingWithExistingId() throws Exception {
        // Create the Branding with an existing ID
        branding.setId(1L);
        BrandingDTO brandingDTO = brandingMapper.toDto(branding);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBrandingMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(brandingDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Branding in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        branding.setTitle(null);

        // Create the Branding, which fails.
        BrandingDTO brandingDTO = brandingMapper.toDto(branding);

        restBrandingMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(brandingDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

    }

    @Test
    @Transactional
    void checkIsActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        branding.setIsActive(null);

        // Create the Branding, which fails.
        BrandingDTO brandingDTO = brandingMapper.toDto(branding);

        restBrandingMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(brandingDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

    }

    @Test
    @Transactional
    void getAllBrandings() throws Exception {
        // Initialize the database
        insertedBranding = brandingRepository.saveAndFlush(branding);

        // Get all the brandingList
        restBrandingMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(branding.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].keywords").value(hasItem(DEFAULT_KEYWORDS)))
            .andExpect(jsonPath("$.[*].author").value(hasItem(DEFAULT_AUTHOR)))
            .andExpect(jsonPath("$.[*].faviconPath").value(hasItem(DEFAULT_FAVICON_PATH)))
            .andExpect(jsonPath("$.[*].logoPath").value(hasItem(DEFAULT_LOGO_PATH)))
            .andExpect(jsonPath("$.[*].logoWidth").value(hasItem(DEFAULT_LOGO_WIDTH)))
            .andExpect(jsonPath("$.[*].logoHeight").value(hasItem(DEFAULT_LOGO_HEIGHT)))
            .andExpect(jsonPath("$.[*].footerTitle").value(hasItem(DEFAULT_FOOTER_TITLE)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getBranding() throws Exception {
        // Initialize the database
        insertedBranding = brandingRepository.saveAndFlush(branding);

        // Get the branding
        restBrandingMockMvc
            .perform(get(ENTITY_API_URL_ID, branding.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(branding.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.keywords").value(DEFAULT_KEYWORDS))
            .andExpect(jsonPath("$.author").value(DEFAULT_AUTHOR))
            .andExpect(jsonPath("$.faviconPath").value(DEFAULT_FAVICON_PATH))
            .andExpect(jsonPath("$.logoPath").value(DEFAULT_LOGO_PATH))
            .andExpect(jsonPath("$.logoWidth").value(DEFAULT_LOGO_WIDTH))
            .andExpect(jsonPath("$.logoHeight").value(DEFAULT_LOGO_HEIGHT))
            .andExpect(jsonPath("$.footerTitle").value(DEFAULT_FOOTER_TITLE))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingBranding() throws Exception {
        // Get the branding
        restBrandingMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBranding() throws Exception {
        // Initialize the database
        insertedBranding = brandingRepository.saveAndFlush(branding);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the branding
        Branding updatedBranding = brandingRepository.findById(branding.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBranding are not directly saved in db
        em.detach(updatedBranding);
        updatedBranding
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .keywords(UPDATED_KEYWORDS)
            .author(UPDATED_AUTHOR)
            .faviconPath(UPDATED_FAVICON_PATH)
            .logoPath(UPDATED_LOGO_PATH)
            .logoWidth(UPDATED_LOGO_WIDTH)
            .logoHeight(UPDATED_LOGO_HEIGHT)
            .footerTitle(UPDATED_FOOTER_TITLE)
            .isActive(UPDATED_IS_ACTIVE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        BrandingDTO brandingDTO = brandingMapper.toDto(updatedBranding);

        restBrandingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, brandingDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(brandingDTO))
            )
            .andExpect(status().isOk());

        // Validate the Branding in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBrandingToMatchAllProperties(updatedBranding);    }

    @Test
    @Transactional
    void putNonExistingBranding() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        branding.setId(longCount.incrementAndGet());

        // Create the Branding
        BrandingDTO brandingDTO = brandingMapper.toDto(branding);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBrandingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, brandingDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(brandingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Branding in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBranding() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        branding.setId(longCount.incrementAndGet());

        // Create the Branding
        BrandingDTO brandingDTO = brandingMapper.toDto(branding);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBrandingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(brandingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Branding in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBranding() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        branding.setId(longCount.incrementAndGet());

        // Create the Branding
        BrandingDTO brandingDTO = brandingMapper.toDto(branding);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBrandingMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(brandingDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Branding in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBrandingWithPatch() throws Exception {
        // Initialize the database
        insertedBranding = brandingRepository.saveAndFlush(branding);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the branding using partial update
        Branding partialUpdatedBranding = new Branding();
        partialUpdatedBranding.setId(branding.getId());

        partialUpdatedBranding.author(UPDATED_AUTHOR).logoPath(UPDATED_LOGO_PATH).logoHeight(UPDATED_LOGO_HEIGHT);

        restBrandingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBranding.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBranding))
            )
            .andExpect(status().isOk());

        // Validate the Branding in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBrandingUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedBranding, branding), getPersistedBranding(branding));
    }

    @Test
    @Transactional
    void fullUpdateBrandingWithPatch() throws Exception {
        // Initialize the database
        insertedBranding = brandingRepository.saveAndFlush(branding);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the branding using partial update
        Branding partialUpdatedBranding = new Branding();
        partialUpdatedBranding.setId(branding.getId());

        partialUpdatedBranding
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .keywords(UPDATED_KEYWORDS)
            .author(UPDATED_AUTHOR)
            .faviconPath(UPDATED_FAVICON_PATH)
            .logoPath(UPDATED_LOGO_PATH)
            .logoWidth(UPDATED_LOGO_WIDTH)
            .logoHeight(UPDATED_LOGO_HEIGHT)
            .footerTitle(UPDATED_FOOTER_TITLE)
            .isActive(UPDATED_IS_ACTIVE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restBrandingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBranding.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBranding))
            )
            .andExpect(status().isOk());

        // Validate the Branding in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBrandingUpdatableFieldsEquals(partialUpdatedBranding, getPersistedBranding(partialUpdatedBranding));
    }

    @Test
    @Transactional
    void patchNonExistingBranding() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        branding.setId(longCount.incrementAndGet());

        // Create the Branding
        BrandingDTO brandingDTO = brandingMapper.toDto(branding);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBrandingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, brandingDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(brandingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Branding in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBranding() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        branding.setId(longCount.incrementAndGet());

        // Create the Branding
        BrandingDTO brandingDTO = brandingMapper.toDto(branding);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBrandingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(brandingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Branding in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBranding() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        branding.setId(longCount.incrementAndGet());

        // Create the Branding
        BrandingDTO brandingDTO = brandingMapper.toDto(branding);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBrandingMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(brandingDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Branding in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBranding() throws Exception {
        // Initialize the database
        insertedBranding = brandingRepository.saveAndFlush(branding);
        brandingRepository.save(branding);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the branding
        restBrandingMockMvc
            .perform(delete(ENTITY_API_URL_ID, branding.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    @Test
    @Transactional
    void searchBranding() throws Exception {
        // Initialize the database
        insertedBranding = brandingRepository.saveAndFlush(branding);

        // Search the branding
        restBrandingMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + branding.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(branding.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].keywords").value(hasItem(DEFAULT_KEYWORDS)))
            .andExpect(jsonPath("$.[*].author").value(hasItem(DEFAULT_AUTHOR)))
            .andExpect(jsonPath("$.[*].faviconPath").value(hasItem(DEFAULT_FAVICON_PATH)))
            .andExpect(jsonPath("$.[*].logoPath").value(hasItem(DEFAULT_LOGO_PATH)))
            .andExpect(jsonPath("$.[*].logoWidth").value(hasItem(DEFAULT_LOGO_WIDTH)))
            .andExpect(jsonPath("$.[*].logoHeight").value(hasItem(DEFAULT_LOGO_HEIGHT)))
            .andExpect(jsonPath("$.[*].footerTitle").value(hasItem(DEFAULT_FOOTER_TITLE)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return brandingRepository.count();
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

    protected Branding getPersistedBranding(Branding branding) {
        return brandingRepository.findById(branding.getId()).orElseThrow();
    }

    protected void assertPersistedBrandingToMatchAllProperties(Branding expectedBranding) {
        assertBrandingAllPropertiesEquals(expectedBranding, getPersistedBranding(expectedBranding));
    }

    protected void assertPersistedBrandingToMatchUpdatableProperties(Branding expectedBranding) {
        assertBrandingAllUpdatablePropertiesEquals(expectedBranding, getPersistedBranding(expectedBranding));
    }
}
