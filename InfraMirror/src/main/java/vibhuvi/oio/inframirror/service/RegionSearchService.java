package vibhuvi.oio.inframirror.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vibhuvi.oio.inframirror.service.dto.RegionDTO;
import vibhuvi.oio.inframirror.service.dto.RegionSearchResultDTO;

/**
 * Service Interface for searching {@link vibhuvi.oio.inframirror.domain.Region}.
 * Separated from RegionService to follow Interface Segregation Principle.
 */
public interface RegionSearchService {
    /**
     * Full-text search for regions.
     *
     * @param query the query of the search.
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
