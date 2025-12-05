package vibhuvi.oio.inframirror.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vibhuvi.oio.inframirror.service.dto.ServiceInstanceDTO;

/**
 * Service Interface for managing {@link vibhuvi.oio.inframirror.domain.ServiceInstance}.
 */
public interface ServiceInstanceService {
    /**
     * Save a serviceInstance.
     *
     * @param serviceInstanceDTO the entity to save.
     * @return the persisted entity.
     */
    ServiceInstanceDTO save(ServiceInstanceDTO serviceInstanceDTO);

    /**
     * Updates a serviceInstance.
     *
     * @param serviceInstanceDTO the entity to update.
     * @return the persisted entity.
     */
    ServiceInstanceDTO update(ServiceInstanceDTO serviceInstanceDTO);

    /**
     * Partially updates a serviceInstance.
     *
     * @param serviceInstanceDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ServiceInstanceDTO> partialUpdate(ServiceInstanceDTO serviceInstanceDTO);

    /**
     * Get all the serviceInstances.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ServiceInstanceDTO> findAll(Pageable pageable);

    /**
     * Get the "id" serviceInstance.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ServiceInstanceDTO> findOne(Long id);

    /**
     * Delete the "id" serviceInstance.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the serviceInstance corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ServiceInstanceDTO> search(String query, Pageable pageable);
}
