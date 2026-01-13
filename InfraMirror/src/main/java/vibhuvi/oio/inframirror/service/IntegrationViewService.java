package vibhuvi.oio.inframirror.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.domain.IntegrationView;
import vibhuvi.oio.inframirror.repository.IntegrationViewRepository;
import vibhuvi.oio.inframirror.service.dto.IntegrationViewDTO;
import vibhuvi.oio.inframirror.service.mapper.IntegrationViewMapper;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing IntegrationView.
 */
@Service
@Transactional
public class IntegrationViewService {

    private static final Logger LOG = LoggerFactory.getLogger(IntegrationViewService.class);

    private final IntegrationViewRepository integrationViewRepository;
    private final IntegrationViewMapper integrationViewMapper;

    public IntegrationViewService(
        IntegrationViewRepository integrationViewRepository,
        IntegrationViewMapper integrationViewMapper
    ) {
        this.integrationViewRepository = integrationViewRepository;
        this.integrationViewMapper = integrationViewMapper;
    }

    public IntegrationViewDTO save(IntegrationViewDTO integrationViewDTO) {
        LOG.debug("Request to save IntegrationView : {}", integrationViewDTO);
        IntegrationView integrationView = integrationViewMapper.toEntity(integrationViewDTO);
        integrationView = integrationViewRepository.save(integrationView);
        return integrationViewMapper.toDto(integrationView);
    }

    public IntegrationViewDTO update(IntegrationViewDTO integrationViewDTO) {
        LOG.debug("Request to update IntegrationView : {}", integrationViewDTO);
        IntegrationView integrationView = integrationViewMapper.toEntity(integrationViewDTO);
        integrationView = integrationViewRepository.save(integrationView);
        return integrationViewMapper.toDto(integrationView);
    }

    public Optional<IntegrationViewDTO> partialUpdate(IntegrationViewDTO integrationViewDTO) {
        LOG.debug("Request to partially update IntegrationView : {}", integrationViewDTO);
        return integrationViewRepository
            .findById(integrationViewDTO.getId())
            .map(existingEntity -> {
                integrationViewMapper.partialUpdate(existingEntity, integrationViewDTO);
                return existingEntity;
            })
            .map(integrationViewRepository::save)
            .map(integrationViewMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<IntegrationViewDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all IntegrationViews");
        return integrationViewRepository.findAll(pageable).map(integrationViewMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<IntegrationViewDTO> findOne(Long id) {
        LOG.debug("Request to get IntegrationView : {}", id);
        return integrationViewRepository.findById(id).map(integrationViewMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<IntegrationViewDTO> findByResourceId(Long resourceId) {
        LOG.debug("Request to get IntegrationViews by resource id : {}", resourceId);
        return integrationViewRepository.findByIntegrationResourceId(resourceId).stream()
            .map(integrationViewMapper::toDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public Optional<IntegrationViewDTO> findDefaultByResourceId(Long resourceId) {
        LOG.debug("Request to get default IntegrationView by resource id : {}", resourceId);
        return integrationViewRepository.findByIntegrationResourceIdAndIsDefaultTrue(resourceId)
            .map(integrationViewMapper::toDto);
    }

    public void delete(Long id) {
        LOG.debug("Request to delete IntegrationView : {}", id);
        integrationViewRepository.deleteById(id);
    }
}
