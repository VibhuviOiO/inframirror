package vibhuvi.oio.inframirror.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vibhuvi.oio.inframirror.service.dto.ServiceDTO;

/**
 * Service Interface for managing {@link vibhuvi.oio.inframirror.domain.Service}.
 */
public interface ServiceService {
    /**
     * Save a service.
     *
     * @param serviceDTO the entity to save.
     * @return the persisted entity.
     */
    ServiceDTO save(ServiceDTO serviceDTO);

    /**
     * Updates a service.
     *
     * @param serviceDTO the entity to update.
     * @return the persisted entity.
     */
    ServiceDTO update(ServiceDTO serviceDTO);

    /**
     * Partially updates a service.
     *
     * @param serviceDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ServiceDTO> partialUpdate(ServiceDTO serviceDTO);

    /**
     * Get the "id" service.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ServiceDTO> findOne(Long id);

    /**
     * Delete the "id" service.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the service corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ServiceDTO> search(String query, Pageable pageable);
}
