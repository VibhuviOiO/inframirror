package vibhuvi.oio.inframirror.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vibhuvi.oio.inframirror.service.dto.StatusPageDTO;
import vibhuvi.oio.inframirror.service.dto.StatusPageSearchResultDTO;

/**
 * Service Interface for managing {@link vibhuvi.oio.inframirror.domain.StatusPage}.
 */
public interface StatusPageService {
    StatusPageDTO save(StatusPageDTO statusPageDTO);
    StatusPageDTO update(StatusPageDTO statusPageDTO);
    Optional<StatusPageDTO> partialUpdate(StatusPageDTO statusPageDTO);
    Optional<StatusPageDTO> findOne(Long id);
    void delete(Long id);
    Page<StatusPageDTO> search(String query, Pageable pageable);
    Page<StatusPageDTO> searchPrefix(String query, Pageable pageable);
    Page<StatusPageDTO> searchFuzzy(String query, Pageable pageable);
    Page<StatusPageSearchResultDTO> searchWithHighlight(String query, Pageable pageable);
}
