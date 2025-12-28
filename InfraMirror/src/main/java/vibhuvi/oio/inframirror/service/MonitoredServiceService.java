package vibhuvi.oio.inframirror.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vibhuvi.oio.inframirror.service.dto.MonitoredServiceDTO;
import vibhuvi.oio.inframirror.service.dto.MonitoredServiceSearchResultDTO;
import vibhuvi.oio.inframirror.service.dto.ServiceInstanceDTO;

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
     * Find a monitoredService by name.
     *
     * @param name the name of the entity.
     * @return the entity.
     */
    Optional<MonitoredServiceDTO> findByName(String name);

    /**
     * Delete the "id" monitoredService.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Get service instances for a monitored service.
     *
     * @param monitoredServiceId the id of the monitored service.
     * @return the list of service instances.
     */
    List<ServiceInstanceDTO> findServiceInstances(Long monitoredServiceId);

    /**
     * Add a service instance to a monitored service.
     *
     * @param monitoredServiceId the id of the monitored service.
     * @param serviceInstanceDTO the service instance to add.
     * @return the persisted service instance.
     */
    ServiceInstanceDTO addServiceInstance(Long monitoredServiceId, ServiceInstanceDTO serviceInstanceDTO);

    /**
     * Search for the monitoredService corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<MonitoredServiceDTO> search(String query, Pageable pageable);

    /**
     * Search with prefix matching.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<MonitoredServiceDTO> searchPrefix(String query, Pageable pageable);

    /**
     * Search with fuzzy matching.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<MonitoredServiceDTO> searchFuzzy(String query, Pageable pageable);

    /**
     * Search with highlighting.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities with highlights.
     */
    Page<MonitoredServiceSearchResultDTO> searchWithHighlight(String query, Pageable pageable);
}
