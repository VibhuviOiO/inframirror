package vibhuvi.oio.inframirror.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static vibhuvi.oio.inframirror.domain.ApiKeyAsserts.*;
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
import vibhuvi.oio.inframirror.domain.ApiKey;
import vibhuvi.oio.inframirror.repository.ApiKeyRepository;
import vibhuvi.oio.inframirror.repository.search.ApiKeySearchRepository;
import vibhuvi.oio.inframirror.service.dto.ApiKeyDTO;
import vibhuvi.oio.inframirror.service.mapper.ApiKeyMapper;

/**
 * Integration tests for the {@link ApiKeyResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ApiKeyResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_KEY_HASH = "AAAAAAAAAA";
    private static final String UPDATED_KEY_HASH = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final Instant DEFAULT_LAST_USED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_USED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_EXPIRES_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EXPIRES_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/api-keys";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/api-keys/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ApiKeyRepository apiKeyRepository;

    @Autowired
    private ApiKeyMapper apiKeyMapper;

    @Autowired
    private ApiKeySearchRepository apiKeySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restApiKeyMockMvc;

    private ApiKey apiKey;

    private ApiKey insertedApiKey;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ApiKey createEntity() {
        return new ApiKey()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .keyHash(DEFAULT_KEY_HASH)
            .active(DEFAULT_ACTIVE)
            .lastUsedDate(DEFAULT_LAST_USED_DATE)
            .expiresAt(DEFAULT_EXPIRES_AT)
            .createdBy(DEFAULT_CREATED_BY)
            .createdDate(DEFAULT_CREATED_DATE)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY)
            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ApiKey createUpdatedEntity() {
        return new ApiKey()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .keyHash(UPDATED_KEY_HASH)
            .active(UPDATED_ACTIVE)
            .lastUsedDate(UPDATED_LAST_USED_DATE)
            .expiresAt(UPDATED_EXPIRES_AT)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
    }

    @BeforeEach
    void initTest() {
        apiKey = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedApiKey != null) {
            apiKeyRepository.delete(insertedApiKey);
            apiKeySearchRepository.delete(insertedApiKey);
            insertedApiKey = null;
        }
    }

    @Test
    @Transactional
    void createApiKey() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(apiKeySearchRepository.findAll());
        // Create the ApiKey
        ApiKeyDTO apiKeyDTO = apiKeyMapper.toDto(apiKey);
        var returnedApiKeyDTO = om.readValue(
            restApiKeyMockMvc
                .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(apiKeyDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ApiKeyDTO.class
        );

        // Validate the ApiKey in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedApiKey = apiKeyMapper.toEntity(returnedApiKeyDTO);
        assertApiKeyUpdatableFieldsEquals(returnedApiKey, getPersistedApiKey(returnedApiKey));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(apiKeySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedApiKey = returnedApiKey;
    }

    @Test
    @Transactional
    void createApiKeyWithExistingId() throws Exception {
        // Create the ApiKey with an existing ID
        apiKey.setId(1L);
        ApiKeyDTO apiKeyDTO = apiKeyMapper.toDto(apiKey);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(apiKeySearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restApiKeyMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(apiKeyDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ApiKey in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(apiKeySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(apiKeySearchRepository.findAll());
        // set the field null
        apiKey.setName(null);

        // Create the ApiKey, which fails.
        ApiKeyDTO apiKeyDTO = apiKeyMapper.toDto(apiKey);

        restApiKeyMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(apiKeyDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(apiKeySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkKeyHashIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(apiKeySearchRepository.findAll());
        // set the field null
        apiKey.setKeyHash(null);

        // Create the ApiKey, which fails.
        ApiKeyDTO apiKeyDTO = apiKeyMapper.toDto(apiKey);

        restApiKeyMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(apiKeyDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(apiKeySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(apiKeySearchRepository.findAll());
        // set the field null
        apiKey.setActive(null);

        // Create the ApiKey, which fails.
        ApiKeyDTO apiKeyDTO = apiKeyMapper.toDto(apiKey);

        restApiKeyMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(apiKeyDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(apiKeySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCreatedByIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(apiKeySearchRepository.findAll());
        // set the field null
        apiKey.setCreatedBy(null);

        // Create the ApiKey, which fails.
        ApiKeyDTO apiKeyDTO = apiKeyMapper.toDto(apiKey);

        restApiKeyMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(apiKeyDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(apiKeySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllApiKeys() throws Exception {
        // Initialize the database
        insertedApiKey = apiKeyRepository.saveAndFlush(apiKey);

        // Get all the apiKeyList
        restApiKeyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(apiKey.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].keyHash").value(hasItem(DEFAULT_KEY_HASH)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].lastUsedDate").value(hasItem(DEFAULT_LAST_USED_DATE.toString())))
            .andExpect(jsonPath("$.[*].expiresAt").value(hasItem(DEFAULT_EXPIRES_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));
    }

    @Test
    @Transactional
    void getApiKey() throws Exception {
        // Initialize the database
        insertedApiKey = apiKeyRepository.saveAndFlush(apiKey);

        // Get the apiKey
        restApiKeyMockMvc
            .perform(get(ENTITY_API_URL_ID, apiKey.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(apiKey.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.keyHash").value(DEFAULT_KEY_HASH))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE))
            .andExpect(jsonPath("$.lastUsedDate").value(DEFAULT_LAST_USED_DATE.toString()))
            .andExpect(jsonPath("$.expiresAt").value(DEFAULT_EXPIRES_AT.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingApiKey() throws Exception {
        // Get the apiKey
        restApiKeyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingApiKey() throws Exception {
        // Initialize the database
        insertedApiKey = apiKeyRepository.saveAndFlush(apiKey);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        apiKeySearchRepository.save(apiKey);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(apiKeySearchRepository.findAll());

        // Update the apiKey
        ApiKey updatedApiKey = apiKeyRepository.findById(apiKey.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedApiKey are not directly saved in db
        em.detach(updatedApiKey);
        updatedApiKey
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .keyHash(UPDATED_KEY_HASH)
            .active(UPDATED_ACTIVE)
            .lastUsedDate(UPDATED_LAST_USED_DATE)
            .expiresAt(UPDATED_EXPIRES_AT)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        ApiKeyDTO apiKeyDTO = apiKeyMapper.toDto(updatedApiKey);

        restApiKeyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, apiKeyDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(apiKeyDTO))
            )
            .andExpect(status().isOk());

        // Validate the ApiKey in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedApiKeyToMatchAllProperties(updatedApiKey);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(apiKeySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ApiKey> apiKeySearchList = Streamable.of(apiKeySearchRepository.findAll()).toList();
                ApiKey testApiKeySearch = apiKeySearchList.get(searchDatabaseSizeAfter - 1);

                assertApiKeyAllPropertiesEquals(testApiKeySearch, updatedApiKey);
            });
    }

    @Test
    @Transactional
    void putNonExistingApiKey() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(apiKeySearchRepository.findAll());
        apiKey.setId(longCount.incrementAndGet());

        // Create the ApiKey
        ApiKeyDTO apiKeyDTO = apiKeyMapper.toDto(apiKey);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restApiKeyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, apiKeyDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(apiKeyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApiKey in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(apiKeySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchApiKey() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(apiKeySearchRepository.findAll());
        apiKey.setId(longCount.incrementAndGet());

        // Create the ApiKey
        ApiKeyDTO apiKeyDTO = apiKeyMapper.toDto(apiKey);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApiKeyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(apiKeyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApiKey in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(apiKeySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamApiKey() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(apiKeySearchRepository.findAll());
        apiKey.setId(longCount.incrementAndGet());

        // Create the ApiKey
        ApiKeyDTO apiKeyDTO = apiKeyMapper.toDto(apiKey);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApiKeyMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(apiKeyDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ApiKey in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(apiKeySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateApiKeyWithPatch() throws Exception {
        // Initialize the database
        insertedApiKey = apiKeyRepository.saveAndFlush(apiKey);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the apiKey using partial update
        ApiKey partialUpdatedApiKey = new ApiKey();
        partialUpdatedApiKey.setId(apiKey.getId());

        partialUpdatedApiKey
            .keyHash(UPDATED_KEY_HASH)
            .lastUsedDate(UPDATED_LAST_USED_DATE)
            .expiresAt(UPDATED_EXPIRES_AT)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE);

        restApiKeyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedApiKey.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedApiKey))
            )
            .andExpect(status().isOk());

        // Validate the ApiKey in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertApiKeyUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedApiKey, apiKey), getPersistedApiKey(apiKey));
    }

    @Test
    @Transactional
    void fullUpdateApiKeyWithPatch() throws Exception {
        // Initialize the database
        insertedApiKey = apiKeyRepository.saveAndFlush(apiKey);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the apiKey using partial update
        ApiKey partialUpdatedApiKey = new ApiKey();
        partialUpdatedApiKey.setId(apiKey.getId());

        partialUpdatedApiKey
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .keyHash(UPDATED_KEY_HASH)
            .active(UPDATED_ACTIVE)
            .lastUsedDate(UPDATED_LAST_USED_DATE)
            .expiresAt(UPDATED_EXPIRES_AT)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);

        restApiKeyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedApiKey.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedApiKey))
            )
            .andExpect(status().isOk());

        // Validate the ApiKey in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertApiKeyUpdatableFieldsEquals(partialUpdatedApiKey, getPersistedApiKey(partialUpdatedApiKey));
    }

    @Test
    @Transactional
    void patchNonExistingApiKey() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(apiKeySearchRepository.findAll());
        apiKey.setId(longCount.incrementAndGet());

        // Create the ApiKey
        ApiKeyDTO apiKeyDTO = apiKeyMapper.toDto(apiKey);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restApiKeyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, apiKeyDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(apiKeyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApiKey in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(apiKeySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchApiKey() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(apiKeySearchRepository.findAll());
        apiKey.setId(longCount.incrementAndGet());

        // Create the ApiKey
        ApiKeyDTO apiKeyDTO = apiKeyMapper.toDto(apiKey);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApiKeyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(apiKeyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApiKey in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(apiKeySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamApiKey() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(apiKeySearchRepository.findAll());
        apiKey.setId(longCount.incrementAndGet());

        // Create the ApiKey
        ApiKeyDTO apiKeyDTO = apiKeyMapper.toDto(apiKey);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApiKeyMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(apiKeyDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ApiKey in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(apiKeySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteApiKey() throws Exception {
        // Initialize the database
        insertedApiKey = apiKeyRepository.saveAndFlush(apiKey);
        apiKeyRepository.save(apiKey);
        apiKeySearchRepository.save(apiKey);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(apiKeySearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the apiKey
        restApiKeyMockMvc
            .perform(delete(ENTITY_API_URL_ID, apiKey.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(apiKeySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchApiKey() throws Exception {
        // Initialize the database
        insertedApiKey = apiKeyRepository.saveAndFlush(apiKey);
        apiKeySearchRepository.save(apiKey);

        // Search the apiKey
        restApiKeyMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + apiKey.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(apiKey.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].keyHash").value(hasItem(DEFAULT_KEY_HASH)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].lastUsedDate").value(hasItem(DEFAULT_LAST_USED_DATE.toString())))
            .andExpect(jsonPath("$.[*].expiresAt").value(hasItem(DEFAULT_EXPIRES_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));
    }

    protected long getRepositoryCount() {
        return apiKeyRepository.count();
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

    protected ApiKey getPersistedApiKey(ApiKey apiKey) {
        return apiKeyRepository.findById(apiKey.getId()).orElseThrow();
    }

    protected void assertPersistedApiKeyToMatchAllProperties(ApiKey expectedApiKey) {
        assertApiKeyAllPropertiesEquals(expectedApiKey, getPersistedApiKey(expectedApiKey));
    }

    protected void assertPersistedApiKeyToMatchUpdatableProperties(ApiKey expectedApiKey) {
        assertApiKeyAllUpdatablePropertiesEquals(expectedApiKey, getPersistedApiKey(expectedApiKey));
    }
}
