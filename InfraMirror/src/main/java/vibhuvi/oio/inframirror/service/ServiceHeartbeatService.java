package vibhuvi.oio.inframirror.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vibhuvi.oio.inframirror.service.dto.ServiceHeartbeatDTO;

/**
 * Service Interface for managing {@link vibhuvi.oio.inframirror.domain.ServiceHeartbeat}.
 */
public interface ServiceHeartbeatService {
    /**
     * Save a serviceHeartbeat.
     *
     * @param serviceHeartbeatDTO the entity to save.
     * @return the persisted entity.
     */
    ServiceHeartbeatDTO save(ServiceHeartbeatDTO serviceHeartbeatDTO);

    /**
     * Updates a serviceHeartbeat.
     *
     * @param serviceHeartbeatDTO the entity to update.
     * @return the persisted entity.
     */
    ServiceHeartbeatDTO update(ServiceHeartbeatDTO serviceHeartbeatDTO);

    /**
     * Partially updates a serviceHeartbeat.
     *
     * @param serviceHeartbeatDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ServiceHeartbeatDTO> partialUpdate(ServiceHeartbeatDTO serviceHeartbeatDTO);

    /**
     * Get all the serviceHeartbeats.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ServiceHeartbeatDTO> findAll(Pageable pageable);

    /**
     * Get the "id" serviceHeartbeat.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ServiceHeartbeatDTO> findOne(Long id);

    /**
     * Delete the "id" serviceHeartbeat.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the serviceHeartbeat corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ServiceHeartbeatDTO> search(String query, Pageable pageable);
}
