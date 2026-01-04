package vibhuvi.oio.inframirror.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vibhuvi.oio.inframirror.service.dto.MonitoredServiceDTO;
import vibhuvi.oio.inframirror.service.dto.MonitoredServiceSearchResultDTO;

/**
 * Service Interface for searching {@link vibhuvi.oio.inframirror.domain.MonitoredService}.
 */
public interface MonitoredServiceSearchService {
    /**
     * Search for monitored services.
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
