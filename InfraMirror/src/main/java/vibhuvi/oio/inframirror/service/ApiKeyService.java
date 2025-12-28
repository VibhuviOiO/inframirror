package vibhuvi.oio.inframirror.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vibhuvi.oio.inframirror.service.dto.ApiKeyDTO;
import vibhuvi.oio.inframirror.service.dto.ApiKeySearchResultDTO;

public interface ApiKeyService {
    ApiKeyDTO save(ApiKeyDTO apiKeyDTO);
    ApiKeyDTO update(ApiKeyDTO apiKeyDTO);
    Optional<ApiKeyDTO> partialUpdate(ApiKeyDTO apiKeyDTO);
    Page<ApiKeyDTO> findAll(Pageable pageable);
    Optional<ApiKeyDTO> findOne(Long id);
    void delete(Long id);
    Page<ApiKeyDTO> search(String query, Pageable pageable);
    Page<ApiKeyDTO> searchPrefix(String query, Pageable pageable);
    Page<ApiKeyDTO> searchFuzzy(String query, Pageable pageable);
    Page<ApiKeySearchResultDTO> searchWithHighlight(String query, Pageable pageable);
}
