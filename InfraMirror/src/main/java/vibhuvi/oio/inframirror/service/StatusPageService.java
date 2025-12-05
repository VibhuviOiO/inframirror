package vibhuvi.oio.inframirror.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vibhuvi.oio.inframirror.service.dto.StatusPageDTO;

/**
 * Service Interface for managing {@link vibhuvi.oio.inframirror.domain.StatusPage}.
 */
public interface StatusPageService {
    /**
     * Save a statusPage.
     *
     * @param statusPageDTO the entity to save.
     * @return the persisted entity.
     */
    StatusPageDTO save(StatusPageDTO statusPageDTO);

    /**
     * Updates a statusPage.
     *
     * @param statusPageDTO the entity to update.
     * @return the persisted entity.
     */
    StatusPageDTO update(StatusPageDTO statusPageDTO);

    /**
     * Partially updates a statusPage.
     *
     * @param statusPageDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<StatusPageDTO> partialUpdate(StatusPageDTO statusPageDTO);

    /**
     * Get the "id" statusPage.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<StatusPageDTO> findOne(Long id);

    /**
     * Delete the "id" statusPage.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the statusPage corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<StatusPageDTO> search(String query, Pageable pageable);
}
