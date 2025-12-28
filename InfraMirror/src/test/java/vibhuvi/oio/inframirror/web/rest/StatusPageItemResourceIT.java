package vibhuvi.oio.inframirror.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static vibhuvi.oio.inframirror.domain.StatusPageItemAsserts.*;
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
import vibhuvi.oio.inframirror.domain.StatusPageItem;
import vibhuvi.oio.inframirror.repository.StatusPageItemRepository;
import vibhuvi.oio.inframirror.service.dto.StatusPageItemDTO;
import vibhuvi.oio.inframirror.service.mapper.StatusPageItemMapper;

/**
 * Integration tests for the {@link StatusPageItemResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class StatusPageItemResourceIT {

    private static final String DEFAULT_ITEM_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_ITEM_TYPE = "BBBBBBBBBB";

    private static final Long DEFAULT_ITEM_ID = 1L;
    private static final Long UPDATED_ITEM_ID = 2L;

    private static final Integer DEFAULT_DISPLAY_ORDER = 1;
    private static final Integer UPDATED_DISPLAY_ORDER = 2;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/status-page-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/status-page-items/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;
    
    private StatusPageItemRepository statusPageItemRepository;

    @Autowired
    private StatusPageItemMapper statusPageItemMapper;
    

    @Autowired
    private EntityManager em;
    
    private MockMvc restStatusPageItemMockMvc;

    private StatusPageItem statusPageItem;

    private StatusPageItem insertedStatusPageItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StatusPageItem createEntity(EntityManager em) {
        StatusPageItem statusPageItem = new StatusPageItem()
            .itemType(DEFAULT_ITEM_TYPE)
            .itemId(DEFAULT_ITEM_ID)
            .displayOrder(DEFAULT_DISPLAY_ORDER)
            .createdAt(DEFAULT_CREATED_AT);
        // Add required entity
        StatusPage statusPage;
        if (TestUtil.findAll(em, StatusPage.class).isEmpty()) {
            statusPage = StatusPageResourceIT.createEntity();
            em.persist(statusPage);
            em.flush();
        } else {
            statusPage = TestUtil.findAll(em, StatusPage.class).get(0);
        }
        statusPageItem.setStatusPage(statusPage);
        return statusPageItem;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StatusPageItem createUpdatedEntity(EntityManager em) {
        StatusPageItem updatedStatusPageItem = new StatusPageItem()
            .itemType(UPDATED_ITEM_TYPE)
            .itemId(UPDATED_ITEM_ID)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .createdAt(UPDATED_CREATED_AT);
        // Add required entity
        StatusPage statusPage;
        if (TestUtil.findAll(em, StatusPage.class).isEmpty()) {
            statusPage = StatusPageResourceIT.createUpdatedEntity();
            em.persist(statusPage);
            em.flush();
        } else {
            statusPage = TestUtil.findAll(em, StatusPage.class).get(0);
        }
        updatedStatusPageItem.setStatusPage(statusPage);
        return updatedStatusPageItem;
    }

    @BeforeEach
    void initTest() {
        statusPageItem = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedStatusPageItem != null) {
            statusPageItemRepository.delete(insertedStatusPageItem);
            insertedStatusPageItem = null;
        }
    }

    @Test
    @Transactional
    void createStatusPageItem() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the StatusPageItem
        StatusPageItemDTO statusPageItemDTO = statusPageItemMapper.toDto(statusPageItem);
        var returnedStatusPageItemDTO = om.readValue(
            restStatusPageItemMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(statusPageItemDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            StatusPageItemDTO.class
        );

        // Validate the StatusPageItem in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedStatusPageItem = statusPageItemMapper.toEntity(returnedStatusPageItemDTO);
        assertStatusPageItemUpdatableFieldsEquals(returnedStatusPageItem, getPersistedStatusPageItem(returnedStatusPageItem));        insertedStatusPageItem = returnedStatusPageItem;
    }

    @Test
    @Transactional
    void createStatusPageItemWithExistingId() throws Exception {
        // Create the StatusPageItem with an existing ID
        statusPageItem.setId(1L);
        StatusPageItemDTO statusPageItemDTO = statusPageItemMapper.toDto(statusPageItem);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStatusPageItemMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(statusPageItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StatusPageItem in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkItemTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        statusPageItem.setItemType(null);

        // Create the StatusPageItem, which fails.
        StatusPageItemDTO statusPageItemDTO = statusPageItemMapper.toDto(statusPageItem);

        restStatusPageItemMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(statusPageItemDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

    }

    @Test
    @Transactional
    void checkItemIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        statusPageItem.setItemId(null);

        // Create the StatusPageItem, which fails.
        StatusPageItemDTO statusPageItemDTO = statusPageItemMapper.toDto(statusPageItem);

        restStatusPageItemMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(statusPageItemDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

    }

    @Test
    @Transactional
    void getAllStatusPageItems() throws Exception {
        // Initialize the database
        insertedStatusPageItem = statusPageItemRepository.saveAndFlush(statusPageItem);

        // Get all the statusPageItemList
        restStatusPageItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(statusPageItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].itemType").value(hasItem(DEFAULT_ITEM_TYPE)))
            .andExpect(jsonPath("$.[*].itemId").value(hasItem(DEFAULT_ITEM_ID.intValue())))
            .andExpect(jsonPath("$.[*].displayOrder").value(hasItem(DEFAULT_DISPLAY_ORDER)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getStatusPageItem() throws Exception {
        // Initialize the database
        insertedStatusPageItem = statusPageItemRepository.saveAndFlush(statusPageItem);

        // Get the statusPageItem
        restStatusPageItemMockMvc
            .perform(get(ENTITY_API_URL_ID, statusPageItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(statusPageItem.getId().intValue()))
            .andExpect(jsonPath("$.itemType").value(DEFAULT_ITEM_TYPE))
            .andExpect(jsonPath("$.itemId").value(DEFAULT_ITEM_ID.intValue()))
            .andExpect(jsonPath("$.displayOrder").value(DEFAULT_DISPLAY_ORDER))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingStatusPageItem() throws Exception {
        // Get the statusPageItem
        restStatusPageItemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStatusPageItem() throws Exception {
        // Initialize the database
        insertedStatusPageItem = statusPageItemRepository.saveAndFlush(statusPageItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the statusPageItem
        StatusPageItem updatedStatusPageItem = statusPageItemRepository.findById(statusPageItem.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedStatusPageItem are not directly saved in db
        em.detach(updatedStatusPageItem);
        updatedStatusPageItem
            .itemType(UPDATED_ITEM_TYPE)
            .itemId(UPDATED_ITEM_ID)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .createdAt(UPDATED_CREATED_AT);
        StatusPageItemDTO statusPageItemDTO = statusPageItemMapper.toDto(updatedStatusPageItem);

        restStatusPageItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, statusPageItemDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(statusPageItemDTO))
            )
            .andExpect(status().isOk());

        // Validate the StatusPageItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStatusPageItemToMatchAllProperties(updatedStatusPageItem);    }

    @Test
    @Transactional
    void putNonExistingStatusPageItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statusPageItem.setId(longCount.incrementAndGet());

        // Create the StatusPageItem
        StatusPageItemDTO statusPageItemDTO = statusPageItemMapper.toDto(statusPageItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStatusPageItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, statusPageItemDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(statusPageItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StatusPageItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStatusPageItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statusPageItem.setId(longCount.incrementAndGet());

        // Create the StatusPageItem
        StatusPageItemDTO statusPageItemDTO = statusPageItemMapper.toDto(statusPageItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatusPageItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(statusPageItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StatusPageItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStatusPageItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statusPageItem.setId(longCount.incrementAndGet());

        // Create the StatusPageItem
        StatusPageItemDTO statusPageItemDTO = statusPageItemMapper.toDto(statusPageItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatusPageItemMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(statusPageItemDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the StatusPageItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStatusPageItemWithPatch() throws Exception {
        // Initialize the database
        insertedStatusPageItem = statusPageItemRepository.saveAndFlush(statusPageItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the statusPageItem using partial update
        StatusPageItem partialUpdatedStatusPageItem = new StatusPageItem();
        partialUpdatedStatusPageItem.setId(statusPageItem.getId());

        partialUpdatedStatusPageItem
            .itemType(UPDATED_ITEM_TYPE)
            .itemId(UPDATED_ITEM_ID)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .createdAt(UPDATED_CREATED_AT);

        restStatusPageItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStatusPageItem.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStatusPageItem))
            )
            .andExpect(status().isOk());

        // Validate the StatusPageItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStatusPageItemUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedStatusPageItem, statusPageItem),
            getPersistedStatusPageItem(statusPageItem)
        );
    }

    @Test
    @Transactional
    void fullUpdateStatusPageItemWithPatch() throws Exception {
        // Initialize the database
        insertedStatusPageItem = statusPageItemRepository.saveAndFlush(statusPageItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the statusPageItem using partial update
        StatusPageItem partialUpdatedStatusPageItem = new StatusPageItem();
        partialUpdatedStatusPageItem.setId(statusPageItem.getId());

        partialUpdatedStatusPageItem
            .itemType(UPDATED_ITEM_TYPE)
            .itemId(UPDATED_ITEM_ID)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .createdAt(UPDATED_CREATED_AT);

        restStatusPageItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStatusPageItem.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStatusPageItem))
            )
            .andExpect(status().isOk());

        // Validate the StatusPageItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStatusPageItemUpdatableFieldsEquals(partialUpdatedStatusPageItem, getPersistedStatusPageItem(partialUpdatedStatusPageItem));
    }

    @Test
    @Transactional
    void patchNonExistingStatusPageItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statusPageItem.setId(longCount.incrementAndGet());

        // Create the StatusPageItem
        StatusPageItemDTO statusPageItemDTO = statusPageItemMapper.toDto(statusPageItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStatusPageItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, statusPageItemDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(statusPageItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StatusPageItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStatusPageItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statusPageItem.setId(longCount.incrementAndGet());

        // Create the StatusPageItem
        StatusPageItemDTO statusPageItemDTO = statusPageItemMapper.toDto(statusPageItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatusPageItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(statusPageItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StatusPageItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStatusPageItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statusPageItem.setId(longCount.incrementAndGet());

        // Create the StatusPageItem
        StatusPageItemDTO statusPageItemDTO = statusPageItemMapper.toDto(statusPageItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatusPageItemMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(statusPageItemDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the StatusPageItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStatusPageItem() throws Exception {
        // Initialize the database
        insertedStatusPageItem = statusPageItemRepository.saveAndFlush(statusPageItem);
        statusPageItemRepository.save(statusPageItem);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the statusPageItem
        restStatusPageItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, statusPageItem.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    @Test
    @Transactional
    void searchStatusPageItem() throws Exception {
        // Initialize the database
        insertedStatusPageItem = statusPageItemRepository.saveAndFlush(statusPageItem);

        // Search the statusPageItem
        restStatusPageItemMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + statusPageItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(statusPageItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].itemType").value(hasItem(DEFAULT_ITEM_TYPE)))
            .andExpect(jsonPath("$.[*].itemId").value(hasItem(DEFAULT_ITEM_ID.intValue())))
            .andExpect(jsonPath("$.[*].displayOrder").value(hasItem(DEFAULT_DISPLAY_ORDER)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return statusPageItemRepository.count();
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

    protected StatusPageItem getPersistedStatusPageItem(StatusPageItem statusPageItem) {
        return statusPageItemRepository.findById(statusPageItem.getId()).orElseThrow();
    }

    protected void assertPersistedStatusPageItemToMatchAllProperties(StatusPageItem expectedStatusPageItem) {
        assertStatusPageItemAllPropertiesEquals(expectedStatusPageItem, getPersistedStatusPageItem(expectedStatusPageItem));
    }

    protected void assertPersistedStatusPageItemToMatchUpdatableProperties(StatusPageItem expectedStatusPageItem) {
        assertStatusPageItemAllUpdatablePropertiesEquals(expectedStatusPageItem, getPersistedStatusPageItem(expectedStatusPageItem));
    }
}
