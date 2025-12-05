package vibhuvi.oio.inframirror.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vibhuvi.oio.inframirror.service.dto.ApiKeyDTO;

/**
 * Service Interface for managing {@link vibhuvi.oio.inframirror.domain.ApiKey}.
 */
public interface ApiKeyService {
    /**
     * Save a apiKey.
     *
     * @param apiKeyDTO the entity to save.
     * @return the persisted entity.
     */
    ApiKeyDTO save(ApiKeyDTO apiKeyDTO);

    /**
     * Updates a apiKey.
     *
     * @param apiKeyDTO the entity to update.
     * @return the persisted entity.
     */
    ApiKeyDTO update(ApiKeyDTO apiKeyDTO);

    /**
     * Partially updates a apiKey.
     *
     * @param apiKeyDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ApiKeyDTO> partialUpdate(ApiKeyDTO apiKeyDTO);

    /**
     * Get all the apiKeys.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ApiKeyDTO> findAll(Pageable pageable);

    /**
     * Get the "id" apiKey.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ApiKeyDTO> findOne(Long id);

    /**
     * Delete the "id" apiKey.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the apiKey corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ApiKeyDTO> search(String query, Pageable pageable);
}
