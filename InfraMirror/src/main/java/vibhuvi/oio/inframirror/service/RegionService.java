package vibhuvi.oio.inframirror.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vibhuvi.oio.inframirror.service.dto.RegionDTO;
import vibhuvi.oio.inframirror.service.dto.RegionSearchResultDTO;

/**
 * Service Interface for managing {@link vibhuvi.oio.inframirror.domain.Region}.
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

    /**
     * Search for the region corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<RegionDTO> search(String query, Pageable pageable);

    /**
     * Prefix search for autocomplete.
     *
     * @param query the prefix to search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<RegionDTO> searchPrefix(String query, Pageable pageable);

    /**
     * Fuzzy search with typo tolerance.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<RegionDTO> searchFuzzy(String query, Pageable pageable);

    /**
     * Search with highlighted results.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of search results with highlights.
     */
    Page<RegionSearchResultDTO> searchWithHighlight(String query, Pageable pageable);
}
