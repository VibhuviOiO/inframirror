package vibhuvi.oio.inframirror.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vibhuvi.oio.inframirror.service.dto.SessionLogDTO;

/**
 * Service Interface for managing {@link vibhuvi.oio.inframirror.domain.SessionLog}.
 */
public interface SessionLogService {
    /**
     * Save a sessionLog.
     *
     * @param sessionLogDTO the entity to save.
     * @return the persisted entity.
     */
    SessionLogDTO save(SessionLogDTO sessionLogDTO);

    /**
     * Updates a sessionLog.
     *
     * @param sessionLogDTO the entity to update.
     * @return the persisted entity.
     */
    SessionLogDTO update(SessionLogDTO sessionLogDTO);

    /**
     * Partially updates a sessionLog.
     *
     * @param sessionLogDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SessionLogDTO> partialUpdate(SessionLogDTO sessionLogDTO);

    /**
     * Get the "id" sessionLog.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SessionLogDTO> findOne(Long id);

    /**
     * Delete the "id" sessionLog.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the sessionLog corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SessionLogDTO> search(String query, Pageable pageable);
}
