package vibhuvi.oio.inframirror.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vibhuvi.oio.inframirror.service.dto.MonitoredServiceDTO;

/**
 * Service Interface for managing {@link vibhuvi.oio.inframirror.domain.MonitoredService}.
 */
public interface MonitoredServiceService {
    /**
     * Save a monitoredService.
     *
     * @param monitoredServiceDTO the entity to save.
     * @return the persisted entity.
     */
    MonitoredServiceDTO save(MonitoredServiceDTO monitoredServiceDTO);

    /**
     * Updates a monitoredService.
     *
     * @param monitoredServiceDTO the entity to update.
     * @return the persisted entity.
     */
    MonitoredServiceDTO update(MonitoredServiceDTO monitoredServiceDTO);

    /**
     * Partially updates a monitoredService.
     *
     * @param monitoredServiceDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<MonitoredServiceDTO> partialUpdate(MonitoredServiceDTO monitoredServiceDTO);

    /**
     * Get the "id" monitoredService.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<MonitoredServiceDTO> findOne(Long id);

    /**
     * Delete the "id" monitoredService.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the monitoredService corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<MonitoredServiceDTO> search(String query, Pageable pageable);
}
