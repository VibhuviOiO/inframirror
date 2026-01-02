package vibhuvi.oio.inframirror.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static vibhuvi.oio.inframirror.domain.StatusPageAsserts.*;
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
import vibhuvi.oio.inframirror.domain.StatusPage;
import vibhuvi.oio.inframirror.repository.StatusPageRepository;
import vibhuvi.oio.inframirror.service.dto.StatusPageDTO;
import vibhuvi.oio.inframirror.service.mapper.StatusPageMapper;

/**
 * Integration tests for the {@link StatusPageResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class StatusPageResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SLUG = "AAAAAAAAAA";
    private static final String UPDATED_SLUG = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_PUBLIC = false;
    private static final Boolean UPDATED_IS_PUBLIC = true;

    private static final String DEFAULT_CUSTOM_DOMAIN = "AAAAAAAAAA";
    private static final String UPDATED_CUSTOM_DOMAIN = "BBBBBBBBBB";

    private static final String DEFAULT_LOGO_URL = "AAAAAAAAAA";
    private static final String UPDATED_LOGO_URL = "BBBBBBBBBB";

    private static final String DEFAULT_THEME_COLOR = "AAAAAAA";
    private static final String UPDATED_THEME_COLOR = "BBBBBBB";

    private static final String DEFAULT_HEADER_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_HEADER_TEXT = "BBBBBBBBBB";

    private static final String DEFAULT_FOOTER_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_FOOTER_TEXT = "BBBBBBBBBB";

    private static final Boolean DEFAULT_SHOW_RESPONSE_TIMES = false;
    private static final Boolean UPDATED_SHOW_RESPONSE_TIMES = true;

    private static final Boolean DEFAULT_SHOW_UPTIME_PERCENTAGE = false;
    private static final Boolean UPDATED_SHOW_UPTIME_PERCENTAGE = true;

    private static final Integer DEFAULT_AUTO_REFRESH_SECONDS = 1;
    private static final Integer UPDATED_AUTO_REFRESH_SECONDS = 2;
    private static final Integer SMALLER_AUTO_REFRESH_SECONDS = 1 - 1;

    private static final String DEFAULT_MONITOR_SELECTION = "AAAAAAAAAA";
    private static final String UPDATED_MONITOR_SELECTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final Boolean DEFAULT_IS_HOME_PAGE = false;
    private static final Boolean UPDATED_IS_HOME_PAGE = true;

    private static final String DEFAULT_ALLOWED_ROLES = "AAAAAAAAAA";
    private static final String UPDATED_ALLOWED_ROLES = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/status-pages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/status-pages/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;
    
    private StatusPageRepository statusPageRepository;

    @Autowired
    private StatusPageMapper statusPageMapper;
    

    @Autowired
    private EntityManager em;
    
    private MockMvc restStatusPageMockMvc;

    private StatusPage statusPage;

    private StatusPage insertedStatusPage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StatusPage createEntity() {
        return new StatusPage()
            .name(DEFAULT_NAME)
            .slug(DEFAULT_SLUG)
            .description(DEFAULT_DESCRIPTION)
            .isPublic(DEFAULT_IS_PUBLIC)
            .isActive(DEFAULT_IS_ACTIVE)
            .isHomePage(DEFAULT_IS_HOME_PAGE)
            .allowedRoles(DEFAULT_ALLOWED_ROLES)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StatusPage createUpdatedEntity() {
        return new StatusPage()
            .name(UPDATED_NAME)
            .slug(UPDATED_SLUG)
            .description(UPDATED_DESCRIPTION)
            .isPublic(UPDATED_IS_PUBLIC)
            .isActive(UPDATED_IS_ACTIVE)
            .isHomePage(UPDATED_IS_HOME_PAGE)
            .allowedRoles(UPDATED_ALLOWED_ROLES)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        statusPage = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedStatusPage != null) {
            statusPageRepository.delete(insertedStatusPage);
            insertedStatusPage = null;
        }
    }

    @Test
    @Transactional
    void createStatusPage() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the StatusPage
        StatusPageDTO statusPageDTO = statusPageMapper.toDto(statusPage);
        var returnedStatusPageDTO = om.readValue(
            restStatusPageMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(statusPageDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            StatusPageDTO.class
        );

        // Validate the StatusPage in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedStatusPage = statusPageMapper.toEntity(returnedStatusPageDTO);
        assertStatusPageUpdatableFieldsEquals(returnedStatusPage, getPersistedStatusPage(returnedStatusPage));        insertedStatusPage = returnedStatusPage;
    }

    @Test
    @Transactional
    void createStatusPageWithExistingId() throws Exception {
        // Create the StatusPage with an existing ID
        statusPage.setId(1L);
        StatusPageDTO statusPageDTO = statusPageMapper.toDto(statusPage);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStatusPageMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(statusPageDTO)))
            .andExpect(status().isBadRequest());

        // Validate the StatusPage in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        statusPage.setName(null);

        // Create the StatusPage, which fails.
        StatusPageDTO statusPageDTO = statusPageMapper.toDto(statusPage);

        restStatusPageMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(statusPageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

    }

    @Test
    @Transactional
    void checkSlugIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        statusPage.setSlug(null);

        // Create the StatusPage, which fails.
        StatusPageDTO statusPageDTO = statusPageMapper.toDto(statusPage);

        restStatusPageMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(statusPageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

    }

    @Test
    @Transactional
    void checkIsPublicIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        statusPage.setIsPublic(null);

        // Create the StatusPage, which fails.
        StatusPageDTO statusPageDTO = statusPageMapper.toDto(statusPage);

        restStatusPageMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(statusPageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        statusPage.setCreatedAt(null);

        // Create the StatusPage, which fails.
        StatusPageDTO statusPageDTO = statusPageMapper.toDto(statusPage);

        restStatusPageMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(statusPageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

    }

    @Test
    @Transactional
    void checkUpdatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        statusPage.setUpdatedAt(null);

        // Create the StatusPage, which fails.
        StatusPageDTO statusPageDTO = statusPageMapper.toDto(statusPage);

        restStatusPageMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(statusPageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

    }

    @Test
    @Transactional
    void getAllStatusPages() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList
        restStatusPageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(statusPage.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].slug").value(hasItem(DEFAULT_SLUG)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].isPublic").value(hasItem(DEFAULT_IS_PUBLIC)))
            .andExpect(jsonPath("$.[*].customDomain").value(hasItem(DEFAULT_CUSTOM_DOMAIN)))
            .andExpect(jsonPath("$.[*].logoUrl").value(hasItem(DEFAULT_LOGO_URL)))
            .andExpect(jsonPath("$.[*].themeColor").value(hasItem(DEFAULT_THEME_COLOR)))
            .andExpect(jsonPath("$.[*].headerText").value(hasItem(DEFAULT_HEADER_TEXT)))
            .andExpect(jsonPath("$.[*].footerText").value(hasItem(DEFAULT_FOOTER_TEXT)))
            .andExpect(jsonPath("$.[*].showResponseTimes").value(hasItem(DEFAULT_SHOW_RESPONSE_TIMES)))
            .andExpect(jsonPath("$.[*].showUptimePercentage").value(hasItem(DEFAULT_SHOW_UPTIME_PERCENTAGE)))
            .andExpect(jsonPath("$.[*].autoRefreshSeconds").value(hasItem(DEFAULT_AUTO_REFRESH_SECONDS)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].isHomePage").value(hasItem(DEFAULT_IS_HOME_PAGE)))
            .andExpect(jsonPath("$.[*].allowedRoles").value(hasItem(DEFAULT_ALLOWED_ROLES)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getStatusPage() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get the statusPage
        restStatusPageMockMvc
            .perform(get(ENTITY_API_URL_ID, statusPage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(statusPage.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.slug").value(DEFAULT_SLUG))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.isPublic").value(DEFAULT_IS_PUBLIC))
            .andExpect(jsonPath("$.customDomain").value(DEFAULT_CUSTOM_DOMAIN))
            .andExpect(jsonPath("$.logoUrl").value(DEFAULT_LOGO_URL))
            .andExpect(jsonPath("$.themeColor").value(DEFAULT_THEME_COLOR))
            .andExpect(jsonPath("$.headerText").value(DEFAULT_HEADER_TEXT))
            .andExpect(jsonPath("$.footerText").value(DEFAULT_FOOTER_TEXT))
            .andExpect(jsonPath("$.showResponseTimes").value(DEFAULT_SHOW_RESPONSE_TIMES))
            .andExpect(jsonPath("$.showUptimePercentage").value(DEFAULT_SHOW_UPTIME_PERCENTAGE))
            .andExpect(jsonPath("$.autoRefreshSeconds").value(DEFAULT_AUTO_REFRESH_SECONDS))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE))
            .andExpect(jsonPath("$.isHomePage").value(DEFAULT_IS_HOME_PAGE))
            .andExpect(jsonPath("$.allowedRoles").value(DEFAULT_ALLOWED_ROLES))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getStatusPagesByIdFiltering() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        Long id = statusPage.getId();

        defaultStatusPageFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultStatusPageFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultStatusPageFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllStatusPagesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where name equals to
        defaultStatusPageFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllStatusPagesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where name in
        defaultStatusPageFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllStatusPagesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where name is not null
        defaultStatusPageFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllStatusPagesByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where name contains
        defaultStatusPageFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllStatusPagesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where name does not contain
        defaultStatusPageFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllStatusPagesBySlugIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where slug equals to
        defaultStatusPageFiltering("slug.equals=" + DEFAULT_SLUG, "slug.equals=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    void getAllStatusPagesBySlugIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where slug in
        defaultStatusPageFiltering("slug.in=" + DEFAULT_SLUG + "," + UPDATED_SLUG, "slug.in=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    void getAllStatusPagesBySlugIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where slug is not null
        defaultStatusPageFiltering("slug.specified=true", "slug.specified=false");
    }

    @Test
    @Transactional
    void getAllStatusPagesBySlugContainsSomething() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where slug contains
        defaultStatusPageFiltering("slug.contains=" + DEFAULT_SLUG, "slug.contains=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    void getAllStatusPagesBySlugNotContainsSomething() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where slug does not contain
        defaultStatusPageFiltering("slug.doesNotContain=" + UPDATED_SLUG, "slug.doesNotContain=" + DEFAULT_SLUG);
    }

    @Test
    @Transactional
    void getAllStatusPagesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where description equals to
        defaultStatusPageFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllStatusPagesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where description in
        defaultStatusPageFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllStatusPagesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where description is not null
        defaultStatusPageFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    @Transactional
    void getAllStatusPagesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where description contains
        defaultStatusPageFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllStatusPagesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where description does not contain
        defaultStatusPageFiltering(
            "description.doesNotContain=" + UPDATED_DESCRIPTION,
            "description.doesNotContain=" + DEFAULT_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllStatusPagesByIsPublicIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where isPublic equals to
        defaultStatusPageFiltering("isPublic.equals=" + DEFAULT_IS_PUBLIC, "isPublic.equals=" + UPDATED_IS_PUBLIC);
    }

    @Test
    @Transactional
    void getAllStatusPagesByIsPublicIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where isPublic in
        defaultStatusPageFiltering("isPublic.in=" + DEFAULT_IS_PUBLIC + "," + UPDATED_IS_PUBLIC, "isPublic.in=" + UPDATED_IS_PUBLIC);
    }

    @Test
    @Transactional
    void getAllStatusPagesByIsPublicIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where isPublic is not null
        defaultStatusPageFiltering("isPublic.specified=true", "isPublic.specified=false");
    }

    @Test
    @Transactional
    void getAllStatusPagesByCustomDomainIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where customDomain equals to
        defaultStatusPageFiltering("customDomain.equals=" + DEFAULT_CUSTOM_DOMAIN, "customDomain.equals=" + UPDATED_CUSTOM_DOMAIN);
    }

    @Test
    @Transactional
    void getAllStatusPagesByCustomDomainIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where customDomain in
        defaultStatusPageFiltering(
            "customDomain.in=" + DEFAULT_CUSTOM_DOMAIN + "," + UPDATED_CUSTOM_DOMAIN,
            "customDomain.in=" + UPDATED_CUSTOM_DOMAIN
        );
    }

    @Test
    @Transactional
    void getAllStatusPagesByCustomDomainIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where customDomain is not null
        defaultStatusPageFiltering("customDomain.specified=true", "customDomain.specified=false");
    }

    @Test
    @Transactional
    void getAllStatusPagesByCustomDomainContainsSomething() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where customDomain contains
        defaultStatusPageFiltering("customDomain.contains=" + DEFAULT_CUSTOM_DOMAIN, "customDomain.contains=" + UPDATED_CUSTOM_DOMAIN);
    }

    @Test
    @Transactional
    void getAllStatusPagesByCustomDomainNotContainsSomething() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where customDomain does not contain
        defaultStatusPageFiltering(
            "customDomain.doesNotContain=" + UPDATED_CUSTOM_DOMAIN,
            "customDomain.doesNotContain=" + DEFAULT_CUSTOM_DOMAIN
        );
    }

    @Test
    @Transactional
    void getAllStatusPagesByLogoUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where logoUrl equals to
        defaultStatusPageFiltering("logoUrl.equals=" + DEFAULT_LOGO_URL, "logoUrl.equals=" + UPDATED_LOGO_URL);
    }

    @Test
    @Transactional
    void getAllStatusPagesByLogoUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where logoUrl in
        defaultStatusPageFiltering("logoUrl.in=" + DEFAULT_LOGO_URL + "," + UPDATED_LOGO_URL, "logoUrl.in=" + UPDATED_LOGO_URL);
    }

    @Test
    @Transactional
    void getAllStatusPagesByLogoUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where logoUrl is not null
        defaultStatusPageFiltering("logoUrl.specified=true", "logoUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllStatusPagesByLogoUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where logoUrl contains
        defaultStatusPageFiltering("logoUrl.contains=" + DEFAULT_LOGO_URL, "logoUrl.contains=" + UPDATED_LOGO_URL);
    }

    @Test
    @Transactional
    void getAllStatusPagesByLogoUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where logoUrl does not contain
        defaultStatusPageFiltering("logoUrl.doesNotContain=" + UPDATED_LOGO_URL, "logoUrl.doesNotContain=" + DEFAULT_LOGO_URL);
    }

    @Test
    @Transactional
    void getAllStatusPagesByThemeColorIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where themeColor equals to
        defaultStatusPageFiltering("themeColor.equals=" + DEFAULT_THEME_COLOR, "themeColor.equals=" + UPDATED_THEME_COLOR);
    }

    @Test
    @Transactional
    void getAllStatusPagesByThemeColorIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where themeColor in
        defaultStatusPageFiltering(
            "themeColor.in=" + DEFAULT_THEME_COLOR + "," + UPDATED_THEME_COLOR,
            "themeColor.in=" + UPDATED_THEME_COLOR
        );
    }

    @Test
    @Transactional
    void getAllStatusPagesByThemeColorIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where themeColor is not null
        defaultStatusPageFiltering("themeColor.specified=true", "themeColor.specified=false");
    }

    @Test
    @Transactional
    void getAllStatusPagesByThemeColorContainsSomething() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where themeColor contains
        defaultStatusPageFiltering("themeColor.contains=" + DEFAULT_THEME_COLOR, "themeColor.contains=" + UPDATED_THEME_COLOR);
    }

    @Test
    @Transactional
    void getAllStatusPagesByThemeColorNotContainsSomething() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where themeColor does not contain
        defaultStatusPageFiltering("themeColor.doesNotContain=" + UPDATED_THEME_COLOR, "themeColor.doesNotContain=" + DEFAULT_THEME_COLOR);
    }

    @Test
    @Transactional
    void getAllStatusPagesByHeaderTextIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where headerText equals to
        defaultStatusPageFiltering("headerText.equals=" + DEFAULT_HEADER_TEXT, "headerText.equals=" + UPDATED_HEADER_TEXT);
    }

    @Test
    @Transactional
    void getAllStatusPagesByHeaderTextIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where headerText in
        defaultStatusPageFiltering(
            "headerText.in=" + DEFAULT_HEADER_TEXT + "," + UPDATED_HEADER_TEXT,
            "headerText.in=" + UPDATED_HEADER_TEXT
        );
    }

    @Test
    @Transactional
    void getAllStatusPagesByHeaderTextIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where headerText is not null
        defaultStatusPageFiltering("headerText.specified=true", "headerText.specified=false");
    }

    @Test
    @Transactional
    void getAllStatusPagesByHeaderTextContainsSomething() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where headerText contains
        defaultStatusPageFiltering("headerText.contains=" + DEFAULT_HEADER_TEXT, "headerText.contains=" + UPDATED_HEADER_TEXT);
    }

    @Test
    @Transactional
    void getAllStatusPagesByHeaderTextNotContainsSomething() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where headerText does not contain
        defaultStatusPageFiltering("headerText.doesNotContain=" + UPDATED_HEADER_TEXT, "headerText.doesNotContain=" + DEFAULT_HEADER_TEXT);
    }

    @Test
    @Transactional
    void getAllStatusPagesByFooterTextIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where footerText equals to
        defaultStatusPageFiltering("footerText.equals=" + DEFAULT_FOOTER_TEXT, "footerText.equals=" + UPDATED_FOOTER_TEXT);
    }

    @Test
    @Transactional
    void getAllStatusPagesByFooterTextIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where footerText in
        defaultStatusPageFiltering(
            "footerText.in=" + DEFAULT_FOOTER_TEXT + "," + UPDATED_FOOTER_TEXT,
            "footerText.in=" + UPDATED_FOOTER_TEXT
        );
    }

    @Test
    @Transactional
    void getAllStatusPagesByFooterTextIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where footerText is not null
        defaultStatusPageFiltering("footerText.specified=true", "footerText.specified=false");
    }

    @Test
    @Transactional
    void getAllStatusPagesByFooterTextContainsSomething() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where footerText contains
        defaultStatusPageFiltering("footerText.contains=" + DEFAULT_FOOTER_TEXT, "footerText.contains=" + UPDATED_FOOTER_TEXT);
    }

    @Test
    @Transactional
    void getAllStatusPagesByFooterTextNotContainsSomething() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where footerText does not contain
        defaultStatusPageFiltering("footerText.doesNotContain=" + UPDATED_FOOTER_TEXT, "footerText.doesNotContain=" + DEFAULT_FOOTER_TEXT);
    }

    @Test
    @Transactional
    void getAllStatusPagesByShowResponseTimesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where showResponseTimes equals to
        defaultStatusPageFiltering(
            "showResponseTimes.equals=" + DEFAULT_SHOW_RESPONSE_TIMES,
            "showResponseTimes.equals=" + UPDATED_SHOW_RESPONSE_TIMES
        );
    }

    @Test
    @Transactional
    void getAllStatusPagesByShowResponseTimesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where showResponseTimes in
        defaultStatusPageFiltering(
            "showResponseTimes.in=" + DEFAULT_SHOW_RESPONSE_TIMES + "," + UPDATED_SHOW_RESPONSE_TIMES,
            "showResponseTimes.in=" + UPDATED_SHOW_RESPONSE_TIMES
        );
    }

    @Test
    @Transactional
    void getAllStatusPagesByShowResponseTimesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where showResponseTimes is not null
        defaultStatusPageFiltering("showResponseTimes.specified=true", "showResponseTimes.specified=false");
    }

    @Test
    @Transactional
    void getAllStatusPagesByShowUptimePercentageIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where showUptimePercentage equals to
        defaultStatusPageFiltering(
            "showUptimePercentage.equals=" + DEFAULT_SHOW_UPTIME_PERCENTAGE,
            "showUptimePercentage.equals=" + UPDATED_SHOW_UPTIME_PERCENTAGE
        );
    }

    @Test
    @Transactional
    void getAllStatusPagesByShowUptimePercentageIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where showUptimePercentage in
        defaultStatusPageFiltering(
            "showUptimePercentage.in=" + DEFAULT_SHOW_UPTIME_PERCENTAGE + "," + UPDATED_SHOW_UPTIME_PERCENTAGE,
            "showUptimePercentage.in=" + UPDATED_SHOW_UPTIME_PERCENTAGE
        );
    }

    @Test
    @Transactional
    void getAllStatusPagesByShowUptimePercentageIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where showUptimePercentage is not null
        defaultStatusPageFiltering("showUptimePercentage.specified=true", "showUptimePercentage.specified=false");
    }

    @Test
    @Transactional
    void getAllStatusPagesByAutoRefreshSecondsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where autoRefreshSeconds equals to
        defaultStatusPageFiltering(
            "autoRefreshSeconds.equals=" + DEFAULT_AUTO_REFRESH_SECONDS,
            "autoRefreshSeconds.equals=" + UPDATED_AUTO_REFRESH_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllStatusPagesByAutoRefreshSecondsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where autoRefreshSeconds in
        defaultStatusPageFiltering(
            "autoRefreshSeconds.in=" + DEFAULT_AUTO_REFRESH_SECONDS + "," + UPDATED_AUTO_REFRESH_SECONDS,
            "autoRefreshSeconds.in=" + UPDATED_AUTO_REFRESH_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllStatusPagesByAutoRefreshSecondsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where autoRefreshSeconds is not null
        defaultStatusPageFiltering("autoRefreshSeconds.specified=true", "autoRefreshSeconds.specified=false");
    }

    @Test
    @Transactional
    void getAllStatusPagesByAutoRefreshSecondsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where autoRefreshSeconds is greater than or equal to
        defaultStatusPageFiltering(
            "autoRefreshSeconds.greaterThanOrEqual=" + DEFAULT_AUTO_REFRESH_SECONDS,
            "autoRefreshSeconds.greaterThanOrEqual=" + UPDATED_AUTO_REFRESH_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllStatusPagesByAutoRefreshSecondsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where autoRefreshSeconds is less than or equal to
        defaultStatusPageFiltering(
            "autoRefreshSeconds.lessThanOrEqual=" + DEFAULT_AUTO_REFRESH_SECONDS,
            "autoRefreshSeconds.lessThanOrEqual=" + SMALLER_AUTO_REFRESH_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllStatusPagesByAutoRefreshSecondsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where autoRefreshSeconds is less than
        defaultStatusPageFiltering(
            "autoRefreshSeconds.lessThan=" + UPDATED_AUTO_REFRESH_SECONDS,
            "autoRefreshSeconds.lessThan=" + DEFAULT_AUTO_REFRESH_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllStatusPagesByAutoRefreshSecondsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where autoRefreshSeconds is greater than
        defaultStatusPageFiltering(
            "autoRefreshSeconds.greaterThan=" + SMALLER_AUTO_REFRESH_SECONDS,
            "autoRefreshSeconds.greaterThan=" + DEFAULT_AUTO_REFRESH_SECONDS
        );
    }

    @Test
    @Transactional
    void getAllStatusPagesByIsActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where isActive equals to
        defaultStatusPageFiltering("isActive.equals=" + DEFAULT_IS_ACTIVE, "isActive.equals=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllStatusPagesByIsActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where isActive in
        defaultStatusPageFiltering("isActive.in=" + DEFAULT_IS_ACTIVE + "," + UPDATED_IS_ACTIVE, "isActive.in=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllStatusPagesByIsActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where isActive is not null
        defaultStatusPageFiltering("isActive.specified=true", "isActive.specified=false");
    }

    @Test
    @Transactional
    void getAllStatusPagesByIsHomePageIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where isHomePage equals to
        defaultStatusPageFiltering("isHomePage.equals=" + DEFAULT_IS_HOME_PAGE, "isHomePage.equals=" + UPDATED_IS_HOME_PAGE);
    }

    @Test
    @Transactional
    void getAllStatusPagesByIsHomePageIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where isHomePage in
        defaultStatusPageFiltering(
            "isHomePage.in=" + DEFAULT_IS_HOME_PAGE + "," + UPDATED_IS_HOME_PAGE,
            "isHomePage.in=" + UPDATED_IS_HOME_PAGE
        );
    }

    @Test
    @Transactional
    void getAllStatusPagesByIsHomePageIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where isHomePage is not null
        defaultStatusPageFiltering("isHomePage.specified=true", "isHomePage.specified=false");
    }

    @Test
    @Transactional
    void getAllStatusPagesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where createdAt equals to
        defaultStatusPageFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllStatusPagesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where createdAt in
        defaultStatusPageFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllStatusPagesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where createdAt is not null
        defaultStatusPageFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllStatusPagesByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where updatedAt equals to
        defaultStatusPageFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllStatusPagesByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where updatedAt in
        defaultStatusPageFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllStatusPagesByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        // Get all the statusPageList where updatedAt is not null
        defaultStatusPageFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    private void defaultStatusPageFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultStatusPageShouldBeFound(shouldBeFound);
        defaultStatusPageShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultStatusPageShouldBeFound(String filter) throws Exception {
        restStatusPageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(statusPage.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].slug").value(hasItem(DEFAULT_SLUG)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].isPublic").value(hasItem(DEFAULT_IS_PUBLIC)))
            .andExpect(jsonPath("$.[*].customDomain").value(hasItem(DEFAULT_CUSTOM_DOMAIN)))
            .andExpect(jsonPath("$.[*].logoUrl").value(hasItem(DEFAULT_LOGO_URL)))
            .andExpect(jsonPath("$.[*].themeColor").value(hasItem(DEFAULT_THEME_COLOR)))
            .andExpect(jsonPath("$.[*].headerText").value(hasItem(DEFAULT_HEADER_TEXT)))
            .andExpect(jsonPath("$.[*].footerText").value(hasItem(DEFAULT_FOOTER_TEXT)))
            .andExpect(jsonPath("$.[*].showResponseTimes").value(hasItem(DEFAULT_SHOW_RESPONSE_TIMES)))
            .andExpect(jsonPath("$.[*].showUptimePercentage").value(hasItem(DEFAULT_SHOW_UPTIME_PERCENTAGE)))
            .andExpect(jsonPath("$.[*].autoRefreshSeconds").value(hasItem(DEFAULT_AUTO_REFRESH_SECONDS)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].isHomePage").value(hasItem(DEFAULT_IS_HOME_PAGE)))
            .andExpect(jsonPath("$.[*].allowedRoles").value(hasItem(DEFAULT_ALLOWED_ROLES)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restStatusPageMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultStatusPageShouldNotBeFound(String filter) throws Exception {
        restStatusPageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restStatusPageMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingStatusPage() throws Exception {
        // Get the statusPage
        restStatusPageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStatusPage() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the statusPage
        StatusPage updatedStatusPage = statusPageRepository.findById(statusPage.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedStatusPage are not directly saved in db
        em.detach(updatedStatusPage);
        updatedStatusPage
            .name(UPDATED_NAME)
            .slug(UPDATED_SLUG)
            .description(UPDATED_DESCRIPTION)
            .isPublic(UPDATED_IS_PUBLIC)
            .isActive(UPDATED_IS_ACTIVE)
            .isHomePage(UPDATED_IS_HOME_PAGE)
            .allowedRoles(UPDATED_ALLOWED_ROLES)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        StatusPageDTO statusPageDTO = statusPageMapper.toDto(updatedStatusPage);

        restStatusPageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, statusPageDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(statusPageDTO))
            )
            .andExpect(status().isOk());

        // Validate the StatusPage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStatusPageToMatchAllProperties(updatedStatusPage);    }

    @Test
    @Transactional
    void putNonExistingStatusPage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statusPage.setId(longCount.incrementAndGet());

        // Create the StatusPage
        StatusPageDTO statusPageDTO = statusPageMapper.toDto(statusPage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStatusPageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, statusPageDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(statusPageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StatusPage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStatusPage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statusPage.setId(longCount.incrementAndGet());

        // Create the StatusPage
        StatusPageDTO statusPageDTO = statusPageMapper.toDto(statusPage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatusPageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(statusPageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StatusPage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStatusPage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statusPage.setId(longCount.incrementAndGet());

        // Create the StatusPage
        StatusPageDTO statusPageDTO = statusPageMapper.toDto(statusPage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatusPageMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(statusPageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StatusPage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStatusPageWithPatch() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the statusPage using partial update
        StatusPage partialUpdatedStatusPage = new StatusPage();
        partialUpdatedStatusPage.setId(statusPage.getId());

        partialUpdatedStatusPage
            .name(UPDATED_NAME)
            .isHomePage(UPDATED_IS_HOME_PAGE)
            .updatedAt(UPDATED_UPDATED_AT);

        restStatusPageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStatusPage.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStatusPage))
            )
            .andExpect(status().isOk());

        // Validate the StatusPage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStatusPageUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedStatusPage, statusPage),
            getPersistedStatusPage(statusPage)
        );
    }

    @Test
    @Transactional
    void fullUpdateStatusPageWithPatch() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the statusPage using partial update
        StatusPage partialUpdatedStatusPage = new StatusPage();
        partialUpdatedStatusPage.setId(statusPage.getId());

        partialUpdatedStatusPage
            .name(UPDATED_NAME)
            .slug(UPDATED_SLUG)
            .description(UPDATED_DESCRIPTION)
            .isPublic(UPDATED_IS_PUBLIC)
            .isActive(UPDATED_IS_ACTIVE)
            .isHomePage(UPDATED_IS_HOME_PAGE)
            .allowedRoles(UPDATED_ALLOWED_ROLES)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restStatusPageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStatusPage.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStatusPage))
            )
            .andExpect(status().isOk());

        // Validate the StatusPage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStatusPageUpdatableFieldsEquals(partialUpdatedStatusPage, getPersistedStatusPage(partialUpdatedStatusPage));
    }

    @Test
    @Transactional
    void patchNonExistingStatusPage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statusPage.setId(longCount.incrementAndGet());

        // Create the StatusPage
        StatusPageDTO statusPageDTO = statusPageMapper.toDto(statusPage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStatusPageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, statusPageDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(statusPageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StatusPage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStatusPage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statusPage.setId(longCount.incrementAndGet());

        // Create the StatusPage
        StatusPageDTO statusPageDTO = statusPageMapper.toDto(statusPage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatusPageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(statusPageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StatusPage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStatusPage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statusPage.setId(longCount.incrementAndGet());

        // Create the StatusPage
        StatusPageDTO statusPageDTO = statusPageMapper.toDto(statusPage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatusPageMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(statusPageDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the StatusPage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStatusPage() throws Exception {
        // Initialize the database
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);
        statusPageRepository.save(statusPage);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the statusPage
        restStatusPageMockMvc
            .perform(delete(ENTITY_API_URL_ID, statusPage.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    @Test
    @Transactional
    void searchStatusPage() throws Exception {
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);
        em.flush();
        em.clear();

        restStatusPageMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=" + DEFAULT_NAME.substring(0, 3)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(statusPage.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void searchStatusPagePrefix() throws Exception {
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);
        em.flush();
        em.clear();

        restStatusPageMockMvc
            .perform(get("/api/status-pages/_search/prefix?query=" + DEFAULT_NAME.substring(0, 2)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(statusPage.getId().intValue())));
    }

    @Test
    @Transactional
    void searchStatusPageFuzzy() throws Exception {
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);
        em.flush();
        em.clear();

        restStatusPageMockMvc
            .perform(get("/api/status-pages/_search/fuzzy?query=" + DEFAULT_NAME))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    @Transactional
    void searchStatusPageWithHighlight() throws Exception {
        insertedStatusPage = statusPageRepository.saveAndFlush(statusPage);
        em.flush();
        em.clear();

        restStatusPageMockMvc
            .perform(get("/api/status-pages/_search/highlight?query=" + DEFAULT_NAME.substring(0, 3)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    protected long getRepositoryCount() {
        return statusPageRepository.count();
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

    protected StatusPage getPersistedStatusPage(StatusPage statusPage) {
        return statusPageRepository.findById(statusPage.getId()).orElseThrow();
    }

    protected void assertPersistedStatusPageToMatchAllProperties(StatusPage expectedStatusPage) {
        assertStatusPageAllPropertiesEquals(expectedStatusPage, getPersistedStatusPage(expectedStatusPage));
    }

    protected void assertPersistedStatusPageToMatchUpdatableProperties(StatusPage expectedStatusPage) {
        assertStatusPageAllUpdatablePropertiesEquals(expectedStatusPage, getPersistedStatusPage(expectedStatusPage));
    }
}
