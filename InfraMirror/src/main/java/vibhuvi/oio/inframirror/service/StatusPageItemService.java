package vibhuvi.oio.inframirror.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vibhuvi.oio.inframirror.service.dto.StatusPageItemDTO;

/**
 * Service Interface for managing {@link vibhuvi.oio.inframirror.domain.StatusPageItem}.
 */
public interface StatusPageItemService {
    /**
     * Save a statusPageItem.
     *
     * @param statusPageItemDTO the entity to save.
     * @return the persisted entity.
     */
    StatusPageItemDTO save(StatusPageItemDTO statusPageItemDTO);

    /**
     * Updates a statusPageItem.
     *
     * @param statusPageItemDTO the entity to update.
     * @return the persisted entity.
     */
    StatusPageItemDTO update(StatusPageItemDTO statusPageItemDTO);

    /**
     * Partially updates a statusPageItem.
     *
     * @param statusPageItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<StatusPageItemDTO> partialUpdate(StatusPageItemDTO statusPageItemDTO);

    /**
     * Get all the statusPageItems.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<StatusPageItemDTO> findAll(Pageable pageable);

    /**
     * Get the "id" statusPageItem.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<StatusPageItemDTO> findOne(Long id);

    /**
     * Delete the "id" statusPageItem.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the statusPageItem corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<StatusPageItemDTO> search(String query, Pageable pageable);
}
