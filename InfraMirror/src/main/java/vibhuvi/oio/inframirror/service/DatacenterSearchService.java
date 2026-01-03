package vibhuvi.oio.inframirror.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vibhuvi.oio.inframirror.service.dto.DatacenterDTO;
import vibhuvi.oio.inframirror.service.dto.DatacenterSearchResultDTO;

/**
 * Service Interface for searching {@link vibhuvi.oio.inframirror.domain.Datacenter}.
 */
public interface DatacenterSearchService {
    /**
     * Search for the datacenter corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DatacenterDTO> search(String query, Pageable pageable);

    /**
     * Prefix search for datacenters.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DatacenterDTO> searchPrefix(String query, Pageable pageable);

    /**
     * Fuzzy search for datacenters.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DatacenterDTO> searchFuzzy(String query, Pageable pageable);

    /**
     * Search for datacenters with highlighting.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities with highlights.
     */
    Page<DatacenterSearchResultDTO> searchWithHighlight(String query, Pageable pageable);
}
