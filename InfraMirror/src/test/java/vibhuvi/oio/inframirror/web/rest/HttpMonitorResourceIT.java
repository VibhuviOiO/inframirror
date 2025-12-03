package vibhuvi.oio.inframirror.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static vibhuvi.oio.inframirror.domain.HttpMonitorAsserts.*;
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
import vibhuvi.oio.inframirror.domain.HttpMonitor;
import vibhuvi.oio.inframirror.domain.Schedule;
import vibhuvi.oio.inframirror.repository.HttpMonitorRepository;
import vibhuvi.oio.inframirror.repository.search.HttpMonitorSearchRepository;
import vibhuvi.oio.inframirror.service.dto.HttpMonitorDTO;
import vibhuvi.oio.inframirror.service.mapper.HttpMonitorMapper;

/**
 * Integration tests for the {@link HttpMonitorResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class HttpMonitorResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_METHOD = "AAAAAAAAAA";
    private static final String UPDATED_METHOD = "BBBBBBBBBB";

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final String DEFAULT_HEADERS = "AAAAAAAAAA";
    private static final String UPDATED_HEADERS = "BBBBBBBBBB";

    private static final String DEFAULT_BODY = "AAAAAAAAAA";
    private static final String UPDATED_BODY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/http-monitors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/http-monitors/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private HttpMonitorRepository httpMonitorRepository;

    @Autowired
    private HttpMonitorMapper httpMonitorMapper;

    @Autowired
    private HttpMonitorSearchRepository httpMonitorSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restHttpMonitorMockMvc;

    private HttpMonitor httpMonitor;

    private HttpMonitor insertedHttpMonitor;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HttpMonitor createEntity() {
        return new HttpMonitor()
            .name(DEFAULT_NAME)
            .method(DEFAULT_METHOD)
            .type(DEFAULT_TYPE)
            .url(DEFAULT_URL)
            .headers(DEFAULT_HEADERS)
            .body(DEFAULT_BODY);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HttpMonitor createUpdatedEntity() {
        return new HttpMonitor()
            .name(UPDATED_NAME)
            .method(UPDATED_METHOD)
            .type(UPDATED_TYPE)
            .url(UPDATED_URL)
            .headers(UPDATED_HEADERS)
            .body(UPDATED_BODY);
    }

    @BeforeEach
    void initTest() {
        httpMonitor = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedHttpMonitor != null) {
            httpMonitorRepository.delete(insertedHttpMonitor);
            httpMonitorSearchRepository.delete(insertedHttpMonitor);
            insertedHttpMonitor = null;
        }
    }

    @Test
    @Transactional
    void createHttpMonitor() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(httpMonitorSearchRepository.findAll());
        // Create the HttpMonitor
        HttpMonitorDTO httpMonitorDTO = httpMonitorMapper.toDto(httpMonitor);
        var returnedHttpMonitorDTO = om.readValue(
            restHttpMonitorMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(httpMonitorDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            HttpMonitorDTO.class
        );

        // Validate the HttpMonitor in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedHttpMonitor = httpMonitorMapper.toEntity(returnedHttpMonitorDTO);
        assertHttpMonitorUpdatableFieldsEquals(returnedHttpMonitor, getPersistedHttpMonitor(returnedHttpMonitor));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(httpMonitorSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedHttpMonitor = returnedHttpMonitor;
    }

    @Test
    @Transactional
    void createHttpMonitorWithExistingId() throws Exception {
        // Create the HttpMonitor with an existing ID
        httpMonitor.setId(1L);
        HttpMonitorDTO httpMonitorDTO = httpMonitorMapper.toDto(httpMonitor);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(httpMonitorSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restHttpMonitorMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(httpMonitorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HttpMonitor in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(httpMonitorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(httpMonitorSearchRepository.findAll());
        // set the field null
        httpMonitor.setName(null);

        // Create the HttpMonitor, which fails.
        HttpMonitorDTO httpMonitorDTO = httpMonitorMapper.toDto(httpMonitor);

        restHttpMonitorMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(httpMonitorDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(httpMonitorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkMethodIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(httpMonitorSearchRepository.findAll());
        // set the field null
        httpMonitor.setMethod(null);

        // Create the HttpMonitor, which fails.
        HttpMonitorDTO httpMonitorDTO = httpMonitorMapper.toDto(httpMonitor);

        restHttpMonitorMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(httpMonitorDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(httpMonitorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(httpMonitorSearchRepository.findAll());
        // set the field null
        httpMonitor.setType(null);

        // Create the HttpMonitor, which fails.
        HttpMonitorDTO httpMonitorDTO = httpMonitorMapper.toDto(httpMonitor);

        restHttpMonitorMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(httpMonitorDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(httpMonitorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllHttpMonitors() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList
        restHttpMonitorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(httpMonitor.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].method").value(hasItem(DEFAULT_METHOD)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].headers").value(hasItem(DEFAULT_HEADERS)))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY)));
    }

    @Test
    @Transactional
    void getHttpMonitor() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get the httpMonitor
        restHttpMonitorMockMvc
            .perform(get(ENTITY_API_URL_ID, httpMonitor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(httpMonitor.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.method").value(DEFAULT_METHOD))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL))
            .andExpect(jsonPath("$.headers").value(DEFAULT_HEADERS))
            .andExpect(jsonPath("$.body").value(DEFAULT_BODY));
    }

    @Test
    @Transactional
    void getHttpMonitorsByIdFiltering() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        Long id = httpMonitor.getId();

        defaultHttpMonitorFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultHttpMonitorFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultHttpMonitorFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where name equals to
        defaultHttpMonitorFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where name in
        defaultHttpMonitorFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where name is not null
        defaultHttpMonitorFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where name contains
        defaultHttpMonitorFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where name does not contain
        defaultHttpMonitorFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByMethodIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where method equals to
        defaultHttpMonitorFiltering("method.equals=" + DEFAULT_METHOD, "method.equals=" + UPDATED_METHOD);
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByMethodIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where method in
        defaultHttpMonitorFiltering("method.in=" + DEFAULT_METHOD + "," + UPDATED_METHOD, "method.in=" + UPDATED_METHOD);
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByMethodIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where method is not null
        defaultHttpMonitorFiltering("method.specified=true", "method.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByMethodContainsSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where method contains
        defaultHttpMonitorFiltering("method.contains=" + DEFAULT_METHOD, "method.contains=" + UPDATED_METHOD);
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByMethodNotContainsSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where method does not contain
        defaultHttpMonitorFiltering("method.doesNotContain=" + UPDATED_METHOD, "method.doesNotContain=" + DEFAULT_METHOD);
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where type equals to
        defaultHttpMonitorFiltering("type.equals=" + DEFAULT_TYPE, "type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where type in
        defaultHttpMonitorFiltering("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE, "type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where type is not null
        defaultHttpMonitorFiltering("type.specified=true", "type.specified=false");
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where type contains
        defaultHttpMonitorFiltering("type.contains=" + DEFAULT_TYPE, "type.contains=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        // Get all the httpMonitorList where type does not contain
        defaultHttpMonitorFiltering("type.doesNotContain=" + UPDATED_TYPE, "type.doesNotContain=" + DEFAULT_TYPE);
    }

    @Test
    @Transactional
    void getAllHttpMonitorsByScheduleIsEqualToSomething() throws Exception {
        Schedule schedule;
        if (TestUtil.findAll(em, Schedule.class).isEmpty()) {
            httpMonitorRepository.saveAndFlush(httpMonitor);
            schedule = ScheduleResourceIT.createEntity();
        } else {
            schedule = TestUtil.findAll(em, Schedule.class).get(0);
        }
        em.persist(schedule);
        em.flush();
        httpMonitor.setSchedule(schedule);
        httpMonitorRepository.saveAndFlush(httpMonitor);
        Long scheduleId = schedule.getId();
        // Get all the httpMonitorList where schedule equals to scheduleId
        defaultHttpMonitorShouldBeFound("scheduleId.equals=" + scheduleId);

        // Get all the httpMonitorList where schedule equals to (scheduleId + 1)
        defaultHttpMonitorShouldNotBeFound("scheduleId.equals=" + (scheduleId + 1));
    }

    private void defaultHttpMonitorFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultHttpMonitorShouldBeFound(shouldBeFound);
        defaultHttpMonitorShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultHttpMonitorShouldBeFound(String filter) throws Exception {
        restHttpMonitorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(httpMonitor.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].method").value(hasItem(DEFAULT_METHOD)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].headers").value(hasItem(DEFAULT_HEADERS)))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY)));

        // Check, that the count call also returns 1
        restHttpMonitorMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultHttpMonitorShouldNotBeFound(String filter) throws Exception {
        restHttpMonitorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restHttpMonitorMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingHttpMonitor() throws Exception {
        // Get the httpMonitor
        restHttpMonitorMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingHttpMonitor() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        httpMonitorSearchRepository.save(httpMonitor);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(httpMonitorSearchRepository.findAll());

        // Update the httpMonitor
        HttpMonitor updatedHttpMonitor = httpMonitorRepository.findById(httpMonitor.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedHttpMonitor are not directly saved in db
        em.detach(updatedHttpMonitor);
        updatedHttpMonitor
            .name(UPDATED_NAME)
            .method(UPDATED_METHOD)
            .type(UPDATED_TYPE)
            .url(UPDATED_URL)
            .headers(UPDATED_HEADERS)
            .body(UPDATED_BODY);
        HttpMonitorDTO httpMonitorDTO = httpMonitorMapper.toDto(updatedHttpMonitor);

        restHttpMonitorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, httpMonitorDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(httpMonitorDTO))
            )
            .andExpect(status().isOk());

        // Validate the HttpMonitor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedHttpMonitorToMatchAllProperties(updatedHttpMonitor);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(httpMonitorSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<HttpMonitor> httpMonitorSearchList = Streamable.of(httpMonitorSearchRepository.findAll()).toList();
                HttpMonitor testHttpMonitorSearch = httpMonitorSearchList.get(searchDatabaseSizeAfter - 1);

                assertHttpMonitorAllPropertiesEquals(testHttpMonitorSearch, updatedHttpMonitor);
            });
    }

    @Test
    @Transactional
    void putNonExistingHttpMonitor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(httpMonitorSearchRepository.findAll());
        httpMonitor.setId(longCount.incrementAndGet());

        // Create the HttpMonitor
        HttpMonitorDTO httpMonitorDTO = httpMonitorMapper.toDto(httpMonitor);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHttpMonitorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, httpMonitorDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(httpMonitorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HttpMonitor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(httpMonitorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchHttpMonitor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(httpMonitorSearchRepository.findAll());
        httpMonitor.setId(longCount.incrementAndGet());

        // Create the HttpMonitor
        HttpMonitorDTO httpMonitorDTO = httpMonitorMapper.toDto(httpMonitor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHttpMonitorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(httpMonitorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HttpMonitor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(httpMonitorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamHttpMonitor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(httpMonitorSearchRepository.findAll());
        httpMonitor.setId(longCount.incrementAndGet());

        // Create the HttpMonitor
        HttpMonitorDTO httpMonitorDTO = httpMonitorMapper.toDto(httpMonitor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHttpMonitorMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(httpMonitorDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the HttpMonitor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(httpMonitorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateHttpMonitorWithPatch() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the httpMonitor using partial update
        HttpMonitor partialUpdatedHttpMonitor = new HttpMonitor();
        partialUpdatedHttpMonitor.setId(httpMonitor.getId());

        partialUpdatedHttpMonitor.type(UPDATED_TYPE).headers(UPDATED_HEADERS).body(UPDATED_BODY);

        restHttpMonitorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHttpMonitor.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedHttpMonitor))
            )
            .andExpect(status().isOk());

        // Validate the HttpMonitor in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertHttpMonitorUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedHttpMonitor, httpMonitor),
            getPersistedHttpMonitor(httpMonitor)
        );
    }

    @Test
    @Transactional
    void fullUpdateHttpMonitorWithPatch() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the httpMonitor using partial update
        HttpMonitor partialUpdatedHttpMonitor = new HttpMonitor();
        partialUpdatedHttpMonitor.setId(httpMonitor.getId());

        partialUpdatedHttpMonitor
            .name(UPDATED_NAME)
            .method(UPDATED_METHOD)
            .type(UPDATED_TYPE)
            .url(UPDATED_URL)
            .headers(UPDATED_HEADERS)
            .body(UPDATED_BODY);

        restHttpMonitorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHttpMonitor.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedHttpMonitor))
            )
            .andExpect(status().isOk());

        // Validate the HttpMonitor in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertHttpMonitorUpdatableFieldsEquals(partialUpdatedHttpMonitor, getPersistedHttpMonitor(partialUpdatedHttpMonitor));
    }

    @Test
    @Transactional
    void patchNonExistingHttpMonitor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(httpMonitorSearchRepository.findAll());
        httpMonitor.setId(longCount.incrementAndGet());

        // Create the HttpMonitor
        HttpMonitorDTO httpMonitorDTO = httpMonitorMapper.toDto(httpMonitor);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHttpMonitorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, httpMonitorDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(httpMonitorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HttpMonitor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(httpMonitorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchHttpMonitor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(httpMonitorSearchRepository.findAll());
        httpMonitor.setId(longCount.incrementAndGet());

        // Create the HttpMonitor
        HttpMonitorDTO httpMonitorDTO = httpMonitorMapper.toDto(httpMonitor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHttpMonitorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(httpMonitorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HttpMonitor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(httpMonitorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamHttpMonitor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(httpMonitorSearchRepository.findAll());
        httpMonitor.setId(longCount.incrementAndGet());

        // Create the HttpMonitor
        HttpMonitorDTO httpMonitorDTO = httpMonitorMapper.toDto(httpMonitor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHttpMonitorMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(httpMonitorDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the HttpMonitor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(httpMonitorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteHttpMonitor() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);
        httpMonitorRepository.save(httpMonitor);
        httpMonitorSearchRepository.save(httpMonitor);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(httpMonitorSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the httpMonitor
        restHttpMonitorMockMvc
            .perform(delete(ENTITY_API_URL_ID, httpMonitor.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(httpMonitorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchHttpMonitor() throws Exception {
        // Initialize the database
        insertedHttpMonitor = httpMonitorRepository.saveAndFlush(httpMonitor);
        httpMonitorSearchRepository.save(httpMonitor);

        // Search the httpMonitor
        restHttpMonitorMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + httpMonitor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(httpMonitor.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].method").value(hasItem(DEFAULT_METHOD)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL.toString())))
            .andExpect(jsonPath("$.[*].headers").value(hasItem(DEFAULT_HEADERS.toString())))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY.toString())));
    }

    protected long getRepositoryCount() {
        return httpMonitorRepository.count();
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

    protected HttpMonitor getPersistedHttpMonitor(HttpMonitor httpMonitor) {
        return httpMonitorRepository.findById(httpMonitor.getId()).orElseThrow();
    }

    protected void assertPersistedHttpMonitorToMatchAllProperties(HttpMonitor expectedHttpMonitor) {
        assertHttpMonitorAllPropertiesEquals(expectedHttpMonitor, getPersistedHttpMonitor(expectedHttpMonitor));
    }

    protected void assertPersistedHttpMonitorToMatchUpdatableProperties(HttpMonitor expectedHttpMonitor) {
        assertHttpMonitorAllUpdatablePropertiesEquals(expectedHttpMonitor, getPersistedHttpMonitor(expectedHttpMonitor));
    }
}
