package vibhuvi.oio.inframirror.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vibhuvi.oio.inframirror.service.dto.InstanceHeartbeatDTO;

/**
 * Service Interface for managing {@link vibhuvi.oio.inframirror.domain.InstanceHeartbeat}.
 */
public interface InstanceHeartbeatService {
    /**
     * Save a instanceHeartbeat.
     *
     * @param instanceHeartbeatDTO the entity to save.
     * @return the persisted entity.
     */
    InstanceHeartbeatDTO save(InstanceHeartbeatDTO instanceHeartbeatDTO);

    /**
     * Updates a instanceHeartbeat.
     *
     * @param instanceHeartbeatDTO the entity to update.
     * @return the persisted entity.
     */
    InstanceHeartbeatDTO update(InstanceHeartbeatDTO instanceHeartbeatDTO);

    /**
     * Partially updates a instanceHeartbeat.
     *
     * @param instanceHeartbeatDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<InstanceHeartbeatDTO> partialUpdate(InstanceHeartbeatDTO instanceHeartbeatDTO);

    /**
     * Get all the instanceHeartbeats.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<InstanceHeartbeatDTO> findAll(Pageable pageable);

    /**
     * Get the "id" instanceHeartbeat.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<InstanceHeartbeatDTO> findOne(Long id);

    /**
     * Delete the "id" instanceHeartbeat.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the instanceHeartbeat corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<InstanceHeartbeatDTO> search(String query, Pageable pageable);
}
