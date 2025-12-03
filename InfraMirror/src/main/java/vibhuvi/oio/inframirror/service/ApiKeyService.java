package vibhuvi.oio.inframirror.service;

import java.util.List;
import java.util.Optional;
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
     * @return the list of entities.
     */
    List<ApiKeyDTO> search(String query);
}
