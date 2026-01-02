package vibhuvi.oio.inframirror.service;

import java.util.Optional;
import vibhuvi.oio.inframirror.service.dto.RegionDTO;

/**
 * Service Interface for managing {@link vibhuvi.oio.inframirror.domain.Region}.
 * Focused on CRUD operations only (Interface Segregation Principle).
 * For search operations, see {@link RegionSearchService}.
 */
public interface RegionService {
    /**
     * Save a region.
     *
     * @param regionDTO the entity to save.
     * @return the persisted entity.
     */
    RegionDTO save(RegionDTO regionDTO);

    /**
     * Updates a region.
     *
     * @param regionDTO the entity to update.
     * @return the persisted entity.
     */
    RegionDTO update(RegionDTO regionDTO);

    /**
     * Partially updates a region.
     *
     * @param regionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<RegionDTO> partialUpdate(RegionDTO regionDTO);

    /**
     * Get the "id" region.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<RegionDTO> findOne(Long id);

    /**
     * Delete the "id" region.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
