package vibhuvi.oio.inframirror.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vibhuvi.oio.inframirror.service.dto.HttpHeartbeatDTO;

/**
 * Service Interface for managing {@link vibhuvi.oio.inframirror.domain.HttpHeartbeat}.
 */
public interface HttpHeartbeatService {
    /**
     * Save a httpHeartbeat.
     *
     * @param httpHeartbeatDTO the entity to save.
     * @return the persisted entity.
     */
    HttpHeartbeatDTO save(HttpHeartbeatDTO httpHeartbeatDTO);

    /**
     * Updates a httpHeartbeat.
     *
     * @param httpHeartbeatDTO the entity to update.
     * @return the persisted entity.
     */
    HttpHeartbeatDTO update(HttpHeartbeatDTO httpHeartbeatDTO);

    /**
     * Partially updates a httpHeartbeat.
     *
     * @param httpHeartbeatDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<HttpHeartbeatDTO> partialUpdate(HttpHeartbeatDTO httpHeartbeatDTO);

    /**
     * Get all the httpHeartbeats.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<HttpHeartbeatDTO> findAll(Pageable pageable);

    /**
     * Get the "id" httpHeartbeat.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<HttpHeartbeatDTO> findOne(Long id);

    /**
     * Delete the "id" httpHeartbeat.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the httpHeartbeat corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<HttpHeartbeatDTO> search(String query, Pageable pageable);
}
