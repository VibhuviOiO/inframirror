package vibhuvi.oio.inframirror.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vibhuvi.oio.inframirror.service.dto.PingHeartbeatDTO;

/**
 * Service Interface for managing {@link vibhuvi.oio.inframirror.domain.PingHeartbeat}.
 */
public interface PingHeartbeatService {
    /**
     * Save a pingHeartbeat.
     *
     * @param pingHeartbeatDTO the entity to save.
     * @return the persisted entity.
     */
    PingHeartbeatDTO save(PingHeartbeatDTO pingHeartbeatDTO);

    /**
     * Updates a pingHeartbeat.
     *
     * @param pingHeartbeatDTO the entity to update.
     * @return the persisted entity.
     */
    PingHeartbeatDTO update(PingHeartbeatDTO pingHeartbeatDTO);

    /**
     * Partially updates a pingHeartbeat.
     *
     * @param pingHeartbeatDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PingHeartbeatDTO> partialUpdate(PingHeartbeatDTO pingHeartbeatDTO);

    /**
     * Get the "id" pingHeartbeat.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PingHeartbeatDTO> findOne(Long id);

    /**
     * Delete the "id" pingHeartbeat.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the pingHeartbeat corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PingHeartbeatDTO> search(String query, Pageable pageable);
}
