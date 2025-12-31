package vibhuvi.oio.inframirror.service;

import java.util.Optional;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vibhuvi.oio.inframirror.service.dto.StatusDependencyDTO;
import vibhuvi.oio.inframirror.service.dto.DependencyTreeDTO;

/**
 * Service Interface for managing {@link vibhuvi.oio.inframirror.domain.StatusDependency}.
 */
public interface StatusDependencyService {
    /**
     * Save a statusDependency.
     *
     * @param statusDependencyDTO the entity to save.
     * @return the persisted entity.
     */
    StatusDependencyDTO save(StatusDependencyDTO statusDependencyDTO);

    /**
     * Updates a statusDependency.
     *
     * @param statusDependencyDTO the entity to update.
     * @return the persisted entity.
     */
    StatusDependencyDTO update(StatusDependencyDTO statusDependencyDTO);

    /**
     * Partially updates a statusDependency.
     *
     * @param statusDependencyDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<StatusDependencyDTO> partialUpdate(StatusDependencyDTO statusDependencyDTO);

    /**
     * Get all the statusDependencies.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<StatusDependencyDTO> findAll(Pageable pageable);

    /**
     * Get the "id" statusDependency.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<StatusDependencyDTO> findOne(Long id);

    /**
     * Delete the "id" statusDependency.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the statusDependency corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<StatusDependencyDTO> search(String query, Pageable pageable);

    /**
     * Get dependency tree for a status page.
     *
     * @param statusPageId the status page id.
     * @return the dependency tree.
     */
    List<DependencyTreeDTO> getDependencyTree(Long statusPageId);
}
