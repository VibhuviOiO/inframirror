package vibhuvi.oio.inframirror.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static vibhuvi.oio.inframirror.domain.ScheduleAsserts.*;
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
import vibhuvi.oio.inframirror.domain.Schedule;
import vibhuvi.oio.inframirror.repository.ScheduleRepository;
import vibhuvi.oio.inframirror.repository.search.ScheduleSearchRepository;
import vibhuvi.oio.inframirror.service.dto.ScheduleDTO;
import vibhuvi.oio.inframirror.service.mapper.ScheduleMapper;

/**
 * Integration tests for the {@link ScheduleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ScheduleResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_INTERVAL = 1;
    private static final Integer UPDATED_INTERVAL = 2;
    private static final Integer SMALLER_INTERVAL = 1 - 1;

    private static final Boolean DEFAULT_INCLUDE_RESPONSE_BODY = false;
    private static final Boolean UPDATED_INCLUDE_RESPONSE_BODY = true;

    private static final Integer DEFAULT_THRESHOLDS_WARNING = 1;
    private static final Integer UPDATED_THRESHOLDS_WARNING = 2;
    private static final Integer SMALLER_THRESHOLDS_WARNING = 1 - 1;

    private static final Integer DEFAULT_THRESHOLDS_CRITICAL = 1;
    private static final Integer UPDATED_THRESHOLDS_CRITICAL = 2;
    private static final Integer SMALLER_THRESHOLDS_CRITICAL = 1 - 1;

    private static final String ENTITY_API_URL = "/api/schedules";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/schedules/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    private ScheduleSearchRepository scheduleSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restScheduleMockMvc;

    private Schedule schedule;

    private Schedule insertedSchedule;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Schedule createEntity() {
        return new Schedule()
            .name(DEFAULT_NAME)
            .interval(DEFAULT_INTERVAL)
            .includeResponseBody(DEFAULT_INCLUDE_RESPONSE_BODY)
            .thresholdsWarning(DEFAULT_THRESHOLDS_WARNING)
            .thresholdsCritical(DEFAULT_THRESHOLDS_CRITICAL);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Schedule createUpdatedEntity() {
        return new Schedule()
            .name(UPDATED_NAME)
            .interval(UPDATED_INTERVAL)
            .includeResponseBody(UPDATED_INCLUDE_RESPONSE_BODY)
            .thresholdsWarning(UPDATED_THRESHOLDS_WARNING)
            .thresholdsCritical(UPDATED_THRESHOLDS_CRITICAL);
    }

    @BeforeEach
    void initTest() {
        schedule = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSchedule != null) {
            scheduleRepository.delete(insertedSchedule);
            scheduleSearchRepository.delete(insertedSchedule);
            insertedSchedule = null;
        }
    }

    @Test
    @Transactional
    void createSchedule() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(scheduleSearchRepository.findAll());
        // Create the Schedule
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);
        var returnedScheduleDTO = om.readValue(
            restScheduleMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scheduleDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ScheduleDTO.class
        );

        // Validate the Schedule in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSchedule = scheduleMapper.toEntity(returnedScheduleDTO);
        assertScheduleUpdatableFieldsEquals(returnedSchedule, getPersistedSchedule(returnedSchedule));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(scheduleSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedSchedule = returnedSchedule;
    }

    @Test
    @Transactional
    void createScheduleWithExistingId() throws Exception {
        // Create the Schedule with an existing ID
        schedule.setId(1L);
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(scheduleSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restScheduleMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scheduleDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Schedule in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(scheduleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(scheduleSearchRepository.findAll());
        // set the field null
        schedule.setName(null);

        // Create the Schedule, which fails.
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        restScheduleMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scheduleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(scheduleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkIntervalIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(scheduleSearchRepository.findAll());
        // set the field null
        schedule.setInterval(null);

        // Create the Schedule, which fails.
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        restScheduleMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scheduleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(scheduleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllSchedules() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList
        restScheduleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(schedule.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].interval").value(hasItem(DEFAULT_INTERVAL)))
            .andExpect(jsonPath("$.[*].includeResponseBody").value(hasItem(DEFAULT_INCLUDE_RESPONSE_BODY)))
            .andExpect(jsonPath("$.[*].thresholdsWarning").value(hasItem(DEFAULT_THRESHOLDS_WARNING)))
            .andExpect(jsonPath("$.[*].thresholdsCritical").value(hasItem(DEFAULT_THRESHOLDS_CRITICAL)));
    }

    @Test
    @Transactional
    void getSchedule() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get the schedule
        restScheduleMockMvc
            .perform(get(ENTITY_API_URL_ID, schedule.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(schedule.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.interval").value(DEFAULT_INTERVAL))
            .andExpect(jsonPath("$.includeResponseBody").value(DEFAULT_INCLUDE_RESPONSE_BODY))
            .andExpect(jsonPath("$.thresholdsWarning").value(DEFAULT_THRESHOLDS_WARNING))
            .andExpect(jsonPath("$.thresholdsCritical").value(DEFAULT_THRESHOLDS_CRITICAL));
    }

    @Test
    @Transactional
    void getSchedulesByIdFiltering() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        Long id = schedule.getId();

        defaultScheduleFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultScheduleFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultScheduleFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSchedulesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where name equals to
        defaultScheduleFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSchedulesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where name in
        defaultScheduleFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSchedulesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where name is not null
        defaultScheduleFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllSchedulesByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where name contains
        defaultScheduleFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSchedulesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where name does not contain
        defaultScheduleFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllSchedulesByIntervalIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where interval equals to
        defaultScheduleFiltering("interval.equals=" + DEFAULT_INTERVAL, "interval.equals=" + UPDATED_INTERVAL);
    }

    @Test
    @Transactional
    void getAllSchedulesByIntervalIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where interval in
        defaultScheduleFiltering("interval.in=" + DEFAULT_INTERVAL + "," + UPDATED_INTERVAL, "interval.in=" + UPDATED_INTERVAL);
    }

    @Test
    @Transactional
    void getAllSchedulesByIntervalIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where interval is not null
        defaultScheduleFiltering("interval.specified=true", "interval.specified=false");
    }

    @Test
    @Transactional
    void getAllSchedulesByIntervalIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where interval is greater than or equal to
        defaultScheduleFiltering("interval.greaterThanOrEqual=" + DEFAULT_INTERVAL, "interval.greaterThanOrEqual=" + UPDATED_INTERVAL);
    }

    @Test
    @Transactional
    void getAllSchedulesByIntervalIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where interval is less than or equal to
        defaultScheduleFiltering("interval.lessThanOrEqual=" + DEFAULT_INTERVAL, "interval.lessThanOrEqual=" + SMALLER_INTERVAL);
    }

    @Test
    @Transactional
    void getAllSchedulesByIntervalIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where interval is less than
        defaultScheduleFiltering("interval.lessThan=" + UPDATED_INTERVAL, "interval.lessThan=" + DEFAULT_INTERVAL);
    }

    @Test
    @Transactional
    void getAllSchedulesByIntervalIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where interval is greater than
        defaultScheduleFiltering("interval.greaterThan=" + SMALLER_INTERVAL, "interval.greaterThan=" + DEFAULT_INTERVAL);
    }

    @Test
    @Transactional
    void getAllSchedulesByIncludeResponseBodyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where includeResponseBody equals to
        defaultScheduleFiltering(
            "includeResponseBody.equals=" + DEFAULT_INCLUDE_RESPONSE_BODY,
            "includeResponseBody.equals=" + UPDATED_INCLUDE_RESPONSE_BODY
        );
    }

    @Test
    @Transactional
    void getAllSchedulesByIncludeResponseBodyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where includeResponseBody in
        defaultScheduleFiltering(
            "includeResponseBody.in=" + DEFAULT_INCLUDE_RESPONSE_BODY + "," + UPDATED_INCLUDE_RESPONSE_BODY,
            "includeResponseBody.in=" + UPDATED_INCLUDE_RESPONSE_BODY
        );
    }

    @Test
    @Transactional
    void getAllSchedulesByIncludeResponseBodyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where includeResponseBody is not null
        defaultScheduleFiltering("includeResponseBody.specified=true", "includeResponseBody.specified=false");
    }

    @Test
    @Transactional
    void getAllSchedulesByThresholdsWarningIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where thresholdsWarning equals to
        defaultScheduleFiltering(
            "thresholdsWarning.equals=" + DEFAULT_THRESHOLDS_WARNING,
            "thresholdsWarning.equals=" + UPDATED_THRESHOLDS_WARNING
        );
    }

    @Test
    @Transactional
    void getAllSchedulesByThresholdsWarningIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where thresholdsWarning in
        defaultScheduleFiltering(
            "thresholdsWarning.in=" + DEFAULT_THRESHOLDS_WARNING + "," + UPDATED_THRESHOLDS_WARNING,
            "thresholdsWarning.in=" + UPDATED_THRESHOLDS_WARNING
        );
    }

    @Test
    @Transactional
    void getAllSchedulesByThresholdsWarningIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where thresholdsWarning is not null
        defaultScheduleFiltering("thresholdsWarning.specified=true", "thresholdsWarning.specified=false");
    }

    @Test
    @Transactional
    void getAllSchedulesByThresholdsWarningIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where thresholdsWarning is greater than or equal to
        defaultScheduleFiltering(
            "thresholdsWarning.greaterThanOrEqual=" + DEFAULT_THRESHOLDS_WARNING,
            "thresholdsWarning.greaterThanOrEqual=" + UPDATED_THRESHOLDS_WARNING
        );
    }

    @Test
    @Transactional
    void getAllSchedulesByThresholdsWarningIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where thresholdsWarning is less than or equal to
        defaultScheduleFiltering(
            "thresholdsWarning.lessThanOrEqual=" + DEFAULT_THRESHOLDS_WARNING,
            "thresholdsWarning.lessThanOrEqual=" + SMALLER_THRESHOLDS_WARNING
        );
    }

    @Test
    @Transactional
    void getAllSchedulesByThresholdsWarningIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where thresholdsWarning is less than
        defaultScheduleFiltering(
            "thresholdsWarning.lessThan=" + UPDATED_THRESHOLDS_WARNING,
            "thresholdsWarning.lessThan=" + DEFAULT_THRESHOLDS_WARNING
        );
    }

    @Test
    @Transactional
    void getAllSchedulesByThresholdsWarningIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where thresholdsWarning is greater than
        defaultScheduleFiltering(
            "thresholdsWarning.greaterThan=" + SMALLER_THRESHOLDS_WARNING,
            "thresholdsWarning.greaterThan=" + DEFAULT_THRESHOLDS_WARNING
        );
    }

    @Test
    @Transactional
    void getAllSchedulesByThresholdsCriticalIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where thresholdsCritical equals to
        defaultScheduleFiltering(
            "thresholdsCritical.equals=" + DEFAULT_THRESHOLDS_CRITICAL,
            "thresholdsCritical.equals=" + UPDATED_THRESHOLDS_CRITICAL
        );
    }

    @Test
    @Transactional
    void getAllSchedulesByThresholdsCriticalIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where thresholdsCritical in
        defaultScheduleFiltering(
            "thresholdsCritical.in=" + DEFAULT_THRESHOLDS_CRITICAL + "," + UPDATED_THRESHOLDS_CRITICAL,
            "thresholdsCritical.in=" + UPDATED_THRESHOLDS_CRITICAL
        );
    }

    @Test
    @Transactional
    void getAllSchedulesByThresholdsCriticalIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where thresholdsCritical is not null
        defaultScheduleFiltering("thresholdsCritical.specified=true", "thresholdsCritical.specified=false");
    }

    @Test
    @Transactional
    void getAllSchedulesByThresholdsCriticalIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where thresholdsCritical is greater than or equal to
        defaultScheduleFiltering(
            "thresholdsCritical.greaterThanOrEqual=" + DEFAULT_THRESHOLDS_CRITICAL,
            "thresholdsCritical.greaterThanOrEqual=" + UPDATED_THRESHOLDS_CRITICAL
        );
    }

    @Test
    @Transactional
    void getAllSchedulesByThresholdsCriticalIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where thresholdsCritical is less than or equal to
        defaultScheduleFiltering(
            "thresholdsCritical.lessThanOrEqual=" + DEFAULT_THRESHOLDS_CRITICAL,
            "thresholdsCritical.lessThanOrEqual=" + SMALLER_THRESHOLDS_CRITICAL
        );
    }

    @Test
    @Transactional
    void getAllSchedulesByThresholdsCriticalIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where thresholdsCritical is less than
        defaultScheduleFiltering(
            "thresholdsCritical.lessThan=" + UPDATED_THRESHOLDS_CRITICAL,
            "thresholdsCritical.lessThan=" + DEFAULT_THRESHOLDS_CRITICAL
        );
    }

    @Test
    @Transactional
    void getAllSchedulesByThresholdsCriticalIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where thresholdsCritical is greater than
        defaultScheduleFiltering(
            "thresholdsCritical.greaterThan=" + SMALLER_THRESHOLDS_CRITICAL,
            "thresholdsCritical.greaterThan=" + DEFAULT_THRESHOLDS_CRITICAL
        );
    }

    private void defaultScheduleFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultScheduleShouldBeFound(shouldBeFound);
        defaultScheduleShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultScheduleShouldBeFound(String filter) throws Exception {
        restScheduleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(schedule.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].interval").value(hasItem(DEFAULT_INTERVAL)))
            .andExpect(jsonPath("$.[*].includeResponseBody").value(hasItem(DEFAULT_INCLUDE_RESPONSE_BODY)))
            .andExpect(jsonPath("$.[*].thresholdsWarning").value(hasItem(DEFAULT_THRESHOLDS_WARNING)))
            .andExpect(jsonPath("$.[*].thresholdsCritical").value(hasItem(DEFAULT_THRESHOLDS_CRITICAL)));

        // Check, that the count call also returns 1
        restScheduleMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultScheduleShouldNotBeFound(String filter) throws Exception {
        restScheduleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restScheduleMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSchedule() throws Exception {
        // Get the schedule
        restScheduleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSchedule() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        scheduleSearchRepository.save(schedule);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(scheduleSearchRepository.findAll());

        // Update the schedule
        Schedule updatedSchedule = scheduleRepository.findById(schedule.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSchedule are not directly saved in db
        em.detach(updatedSchedule);
        updatedSchedule
            .name(UPDATED_NAME)
            .interval(UPDATED_INTERVAL)
            .includeResponseBody(UPDATED_INCLUDE_RESPONSE_BODY)
            .thresholdsWarning(UPDATED_THRESHOLDS_WARNING)
            .thresholdsCritical(UPDATED_THRESHOLDS_CRITICAL);
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(updatedSchedule);

        restScheduleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, scheduleDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(scheduleDTO))
            )
            .andExpect(status().isOk());

        // Validate the Schedule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedScheduleToMatchAllProperties(updatedSchedule);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(scheduleSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Schedule> scheduleSearchList = Streamable.of(scheduleSearchRepository.findAll()).toList();
                Schedule testScheduleSearch = scheduleSearchList.get(searchDatabaseSizeAfter - 1);

                assertScheduleAllPropertiesEquals(testScheduleSearch, updatedSchedule);
            });
    }

    @Test
    @Transactional
    void putNonExistingSchedule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(scheduleSearchRepository.findAll());
        schedule.setId(longCount.incrementAndGet());

        // Create the Schedule
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restScheduleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, scheduleDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(scheduleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Schedule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(scheduleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchSchedule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(scheduleSearchRepository.findAll());
        schedule.setId(longCount.incrementAndGet());

        // Create the Schedule
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScheduleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(scheduleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Schedule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(scheduleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSchedule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(scheduleSearchRepository.findAll());
        schedule.setId(longCount.incrementAndGet());

        // Create the Schedule
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScheduleMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scheduleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Schedule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(scheduleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateScheduleWithPatch() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the schedule using partial update
        Schedule partialUpdatedSchedule = new Schedule();
        partialUpdatedSchedule.setId(schedule.getId());

        partialUpdatedSchedule.includeResponseBody(UPDATED_INCLUDE_RESPONSE_BODY).thresholdsCritical(UPDATED_THRESHOLDS_CRITICAL);

        restScheduleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSchedule.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSchedule))
            )
            .andExpect(status().isOk());

        // Validate the Schedule in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertScheduleUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedSchedule, schedule), getPersistedSchedule(schedule));
    }

    @Test
    @Transactional
    void fullUpdateScheduleWithPatch() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the schedule using partial update
        Schedule partialUpdatedSchedule = new Schedule();
        partialUpdatedSchedule.setId(schedule.getId());

        partialUpdatedSchedule
            .name(UPDATED_NAME)
            .interval(UPDATED_INTERVAL)
            .includeResponseBody(UPDATED_INCLUDE_RESPONSE_BODY)
            .thresholdsWarning(UPDATED_THRESHOLDS_WARNING)
            .thresholdsCritical(UPDATED_THRESHOLDS_CRITICAL);

        restScheduleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSchedule.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSchedule))
            )
            .andExpect(status().isOk());

        // Validate the Schedule in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertScheduleUpdatableFieldsEquals(partialUpdatedSchedule, getPersistedSchedule(partialUpdatedSchedule));
    }

    @Test
    @Transactional
    void patchNonExistingSchedule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(scheduleSearchRepository.findAll());
        schedule.setId(longCount.incrementAndGet());

        // Create the Schedule
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restScheduleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, scheduleDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(scheduleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Schedule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(scheduleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSchedule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(scheduleSearchRepository.findAll());
        schedule.setId(longCount.incrementAndGet());

        // Create the Schedule
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScheduleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(scheduleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Schedule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(scheduleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSchedule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(scheduleSearchRepository.findAll());
        schedule.setId(longCount.incrementAndGet());

        // Create the Schedule
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScheduleMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(scheduleDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Schedule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(scheduleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteSchedule() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);
        scheduleRepository.save(schedule);
        scheduleSearchRepository.save(schedule);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(scheduleSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the schedule
        restScheduleMockMvc
            .perform(delete(ENTITY_API_URL_ID, schedule.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(scheduleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchSchedule() throws Exception {
        // Initialize the database
        insertedSchedule = scheduleRepository.saveAndFlush(schedule);
        scheduleSearchRepository.save(schedule);

        // Search the schedule
        restScheduleMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + schedule.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(schedule.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].interval").value(hasItem(DEFAULT_INTERVAL)))
            .andExpect(jsonPath("$.[*].includeResponseBody").value(hasItem(DEFAULT_INCLUDE_RESPONSE_BODY)))
            .andExpect(jsonPath("$.[*].thresholdsWarning").value(hasItem(DEFAULT_THRESHOLDS_WARNING)))
            .andExpect(jsonPath("$.[*].thresholdsCritical").value(hasItem(DEFAULT_THRESHOLDS_CRITICAL)));
    }

    protected long getRepositoryCount() {
        return scheduleRepository.count();
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

    protected Schedule getPersistedSchedule(Schedule schedule) {
        return scheduleRepository.findById(schedule.getId()).orElseThrow();
    }

    protected void assertPersistedScheduleToMatchAllProperties(Schedule expectedSchedule) {
        assertScheduleAllPropertiesEquals(expectedSchedule, getPersistedSchedule(expectedSchedule));
    }

    protected void assertPersistedScheduleToMatchUpdatableProperties(Schedule expectedSchedule) {
        assertScheduleAllUpdatablePropertiesEquals(expectedSchedule, getPersistedSchedule(expectedSchedule));
    }
}
