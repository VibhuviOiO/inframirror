package vibhuvi.oio.inframirror.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vibhuvi.oio.inframirror.service.dto.InstanceDTO;
import vibhuvi.oio.inframirror.service.dto.InstanceSearchResultDTO;

/**
 * Service Interface for searching {@link vibhuvi.oio.inframirror.domain.Instance}.
 */
public interface InstanceSearchService {
    Page<InstanceDTO> search(String query, Pageable pageable);

    Page<InstanceDTO> searchPrefix(String query, Pageable pageable);

    Page<InstanceDTO> searchFuzzy(String query, Pageable pageable);

    Page<InstanceSearchResultDTO> searchWithHighlight(String query, Pageable pageable);
}
