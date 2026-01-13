package vibhuvi.oio.inframirror.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.domain.IntegrationResource;
import vibhuvi.oio.inframirror.repository.IntegrationResourceRepository;
import vibhuvi.oio.inframirror.service.dto.IntegrationResourceDTO;
import vibhuvi.oio.inframirror.service.mapper.IntegrationResourceMapper;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing IntegrationResource.
 */
@Service
@Transactional
public class IntegrationResourceService {

    private static final Logger LOG = LoggerFactory.getLogger(IntegrationResourceService.class);

    private final IntegrationResourceRepository integrationResourceRepository;
    private final IntegrationResourceMapper integrationResourceMapper;

    public IntegrationResourceService(
        IntegrationResourceRepository integrationResourceRepository,
        IntegrationResourceMapper integrationResourceMapper
    ) {
        this.integrationResourceRepository = integrationResourceRepository;
        this.integrationResourceMapper = integrationResourceMapper;
    }

    public IntegrationResourceDTO save(IntegrationResourceDTO integrationResourceDTO) {
        LOG.debug("Request to save IntegrationResource : {}", integrationResourceDTO);
        IntegrationResource integrationResource = integrationResourceMapper.toEntity(integrationResourceDTO);
        integrationResource = integrationResourceRepository.save(integrationResource);
        return integrationResourceMapper.toDto(integrationResource);
    }

    public IntegrationResourceDTO update(IntegrationResourceDTO integrationResourceDTO) {
        LOG.debug("Request to update IntegrationResource : {}", integrationResourceDTO);
        IntegrationResource integrationResource = integrationResourceMapper.toEntity(integrationResourceDTO);
        integrationResource = integrationResourceRepository.save(integrationResource);
        return integrationResourceMapper.toDto(integrationResource);
    }

    public Optional<IntegrationResourceDTO> partialUpdate(IntegrationResourceDTO integrationResourceDTO) {
        LOG.debug("Request to partially update IntegrationResource : {}", integrationResourceDTO);
        return integrationResourceRepository
            .findById(integrationResourceDTO.getId())
            .map(existingEntity -> {
                integrationResourceMapper.partialUpdate(existingEntity, integrationResourceDTO);
                return existingEntity;
            })
            .map(integrationResourceRepository::save)
            .map(integrationResourceMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<IntegrationResourceDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all IntegrationResources");
        return integrationResourceRepository.findAll(pageable).map(integrationResourceMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<IntegrationResourceDTO> findOne(Long id) {
        LOG.debug("Request to get IntegrationResource : {}", id);
        return integrationResourceRepository.findById(id).map(integrationResourceMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<IntegrationResourceDTO> findByControlIntegrationId(Long controlIntegrationId) {
        LOG.debug("Request to get IntegrationResources by integration id : {}", controlIntegrationId);
        return integrationResourceRepository.findByControlIntegrationId(controlIntegrationId).stream()
            .map(integrationResourceMapper::toDto)
            .toList();
    }

    public void delete(Long id) {
        LOG.debug("Request to delete IntegrationResource : {}", id);
        integrationResourceRepository.deleteById(id);
    }
}
