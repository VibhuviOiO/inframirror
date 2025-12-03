package vibhuvi.oio.inframirror.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.domain.ApiKey;
import vibhuvi.oio.inframirror.repository.ApiKeyRepository;
import vibhuvi.oio.inframirror.repository.search.ApiKeySearchRepository;
import vibhuvi.oio.inframirror.service.ApiKeyService;
import vibhuvi.oio.inframirror.service.dto.ApiKeyDTO;
import vibhuvi.oio.inframirror.service.mapper.ApiKeyMapper;

/**
 * Service Implementation for managing {@link vibhuvi.oio.inframirror.domain.ApiKey}.
 */
@Service
@Transactional
public class ApiKeyServiceImpl implements ApiKeyService {

    private static final Logger LOG = LoggerFactory.getLogger(ApiKeyServiceImpl.class);

    private final ApiKeyRepository apiKeyRepository;

    private final ApiKeyMapper apiKeyMapper;

    private final ApiKeySearchRepository apiKeySearchRepository;

    public ApiKeyServiceImpl(ApiKeyRepository apiKeyRepository, ApiKeyMapper apiKeyMapper, ApiKeySearchRepository apiKeySearchRepository) {
        this.apiKeyRepository = apiKeyRepository;
        this.apiKeyMapper = apiKeyMapper;
        this.apiKeySearchRepository = apiKeySearchRepository;
    }

    @Override
    public ApiKeyDTO save(ApiKeyDTO apiKeyDTO) {
        LOG.debug("Request to save ApiKey : {}", apiKeyDTO);
        ApiKey apiKey = apiKeyMapper.toEntity(apiKeyDTO);
        apiKey = apiKeyRepository.save(apiKey);
        apiKeySearchRepository.index(apiKey);
        return apiKeyMapper.toDto(apiKey);
    }

    @Override
    public ApiKeyDTO update(ApiKeyDTO apiKeyDTO) {
        LOG.debug("Request to update ApiKey : {}", apiKeyDTO);
        ApiKey apiKey = apiKeyMapper.toEntity(apiKeyDTO);
        apiKey = apiKeyRepository.save(apiKey);
        apiKeySearchRepository.index(apiKey);
        return apiKeyMapper.toDto(apiKey);
    }

    @Override
    public Optional<ApiKeyDTO> partialUpdate(ApiKeyDTO apiKeyDTO) {
        LOG.debug("Request to partially update ApiKey : {}", apiKeyDTO);

        return apiKeyRepository
            .findById(apiKeyDTO.getId())
            .map(existingApiKey -> {
                apiKeyMapper.partialUpdate(existingApiKey, apiKeyDTO);

                return existingApiKey;
            })
            .map(apiKeyRepository::save)
            .map(savedApiKey -> {
                apiKeySearchRepository.index(savedApiKey);
                return savedApiKey;
            })
            .map(apiKeyMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ApiKeyDTO> findOne(Long id) {
        LOG.debug("Request to get ApiKey : {}", id);
        return apiKeyRepository.findById(id).map(apiKeyMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ApiKey : {}", id);
        apiKeyRepository.deleteById(id);
        apiKeySearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApiKeyDTO> search(String query) {
        LOG.debug("Request to search ApiKeys for query {}", query);
        try {
            return StreamSupport.stream(apiKeySearchRepository.search(query).spliterator(), false).map(apiKeyMapper::toDto).toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
