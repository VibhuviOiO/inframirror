package vibhuvi.oio.inframirror.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.domain.ControlIntegration;
import vibhuvi.oio.inframirror.repository.ControlIntegrationRepository;
import vibhuvi.oio.inframirror.service.dto.ControlIntegrationDTO;
import vibhuvi.oio.inframirror.service.mapper.ControlIntegrationMapper;

import java.util.Optional;

/**
 * Service for managing ControlIntegration.
 */
@Service
@Transactional
public class ControlIntegrationService {

    private static final Logger LOG = LoggerFactory.getLogger(ControlIntegrationService.class);

    private final ControlIntegrationRepository controlIntegrationRepository;
    private final ControlIntegrationMapper controlIntegrationMapper;

    public ControlIntegrationService(
        ControlIntegrationRepository controlIntegrationRepository,
        ControlIntegrationMapper controlIntegrationMapper
    ) {
        this.controlIntegrationRepository = controlIntegrationRepository;
        this.controlIntegrationMapper = controlIntegrationMapper;
    }

    public ControlIntegrationDTO save(ControlIntegrationDTO controlIntegrationDTO) {
        LOG.debug("Request to save ControlIntegration : {}", controlIntegrationDTO);
        ControlIntegration controlIntegration = controlIntegrationMapper.toEntity(controlIntegrationDTO);
        controlIntegration = controlIntegrationRepository.save(controlIntegration);
        return controlIntegrationMapper.toDto(controlIntegration);
    }

    public ControlIntegrationDTO update(ControlIntegrationDTO controlIntegrationDTO) {
        LOG.debug("Request to update ControlIntegration : {}", controlIntegrationDTO);
        ControlIntegration controlIntegration = controlIntegrationMapper.toEntity(controlIntegrationDTO);
        controlIntegration = controlIntegrationRepository.save(controlIntegration);
        return controlIntegrationMapper.toDto(controlIntegration);
    }

    public Optional<ControlIntegrationDTO> partialUpdate(ControlIntegrationDTO controlIntegrationDTO) {
        LOG.debug("Request to partially update ControlIntegration : {}", controlIntegrationDTO);
        return controlIntegrationRepository
            .findById(controlIntegrationDTO.getId())
            .map(existingEntity -> {
                controlIntegrationMapper.partialUpdate(existingEntity, controlIntegrationDTO);
                return existingEntity;
            })
            .map(controlIntegrationRepository::save)
            .map(controlIntegrationMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ControlIntegrationDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ControlIntegrations");
        return controlIntegrationRepository.findAll(pageable).map(controlIntegrationMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<ControlIntegrationDTO> findOne(Long id) {
        LOG.debug("Request to get ControlIntegration : {}", id);
        return controlIntegrationRepository.findById(id).map(controlIntegrationMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<ControlIntegrationDTO> findByCode(String code) {
        LOG.debug("Request to get ControlIntegration by code : {}", code);
        return controlIntegrationRepository.findByCode(code).map(controlIntegrationMapper::toDto);
    }

    public void delete(Long id) {
        LOG.debug("Request to delete ControlIntegration : {}", id);
        controlIntegrationRepository.deleteById(id);
    }
}
