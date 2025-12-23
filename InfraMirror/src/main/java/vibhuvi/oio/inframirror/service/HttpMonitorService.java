package vibhuvi.oio.inframirror.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vibhuvi.oio.inframirror.service.dto.HttpMonitorDTO;

/**
 * Service Interface for managing {@link vibhuvi.oio.inframirror.domain.HttpMonitor}.
 */
public interface HttpMonitorService {
    /**
     * Save a httpMonitor.
     *
     * @param httpMonitorDTO the entity to save.
     * @return the persisted entity.
     */
    HttpMonitorDTO save(HttpMonitorDTO httpMonitorDTO);

    /**
     * Updates a httpMonitor.
     *
     * @param httpMonitorDTO the entity to update.
     * @return the persisted entity.
     */
    HttpMonitorDTO update(HttpMonitorDTO httpMonitorDTO);

    /**
     * Partially updates a httpMonitor.
     *
     * @param httpMonitorDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<HttpMonitorDTO> partialUpdate(HttpMonitorDTO httpMonitorDTO);

    /**
     * Get the "id" httpMonitor.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<HttpMonitorDTO> findOne(Long id);

    /**
     * Find a httpMonitor by name.
     *
     * @param name the name of the entity.
     * @return the entity.
     */
    Optional<HttpMonitorDTO> findByName(String name);

    /**
     * Delete the "id" httpMonitor.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the httpMonitor corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<HttpMonitorDTO> search(String query, Pageable pageable);
}
