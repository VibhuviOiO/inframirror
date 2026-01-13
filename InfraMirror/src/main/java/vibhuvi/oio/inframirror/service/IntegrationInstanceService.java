package vibhuvi.oio.inframirror.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.domain.IntegrationInstance;
import vibhuvi.oio.inframirror.repository.*;
import vibhuvi.oio.inframirror.service.dto.IntegrationInstanceDTO;
import vibhuvi.oio.inframirror.service.mapper.IntegrationInstanceMapper;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing IntegrationInstance.
 */
@Service
@Transactional
public class IntegrationInstanceService {

    private static final Logger LOG = LoggerFactory.getLogger(IntegrationInstanceService.class);

    private final IntegrationInstanceRepository integrationInstanceRepository;
    private final IntegrationInstanceMapper integrationInstanceMapper;
    private final MonitoredServiceRepository monitoredServiceRepository;
    private final HttpMonitorRepository httpMonitorRepository;
    private final DatacenterRepository datacenterRepository;

    public IntegrationInstanceService(
        IntegrationInstanceRepository integrationInstanceRepository,
        IntegrationInstanceMapper integrationInstanceMapper,
        MonitoredServiceRepository monitoredServiceRepository,
        HttpMonitorRepository httpMonitorRepository,
        DatacenterRepository datacenterRepository
    ) {
        this.integrationInstanceRepository = integrationInstanceRepository;
        this.integrationInstanceMapper = integrationInstanceMapper;
        this.monitoredServiceRepository = monitoredServiceRepository;
        this.httpMonitorRepository = httpMonitorRepository;
        this.datacenterRepository = datacenterRepository;
    }

    public IntegrationInstanceDTO save(IntegrationInstanceDTO integrationInstanceDTO) {
        LOG.debug("Request to save IntegrationInstance : {}", integrationInstanceDTO);
        IntegrationInstance integrationInstance = integrationInstanceMapper.toEntity(integrationInstanceDTO);
        integrationInstance = integrationInstanceRepository.save(integrationInstance);
        return enrichDTO(integrationInstanceMapper.toDto(integrationInstance));
    }

    public IntegrationInstanceDTO update(IntegrationInstanceDTO integrationInstanceDTO) {
        LOG.debug("Request to update IntegrationInstance : {}", integrationInstanceDTO);
        IntegrationInstance integrationInstance = integrationInstanceMapper.toEntity(integrationInstanceDTO);
        integrationInstance = integrationInstanceRepository.save(integrationInstance);
        return enrichDTO(integrationInstanceMapper.toDto(integrationInstance));
    }

    public Optional<IntegrationInstanceDTO> partialUpdate(IntegrationInstanceDTO integrationInstanceDTO) {
        LOG.debug("Request to partially update IntegrationInstance : {}", integrationInstanceDTO);
        return integrationInstanceRepository
            .findById(integrationInstanceDTO.getId())
            .map(existingEntity -> {
                integrationInstanceMapper.partialUpdate(existingEntity, integrationInstanceDTO);
                return existingEntity;
            })
            .map(integrationInstanceRepository::save)
            .map(integrationInstanceMapper::toDto)
            .map(this::enrichDTO);
    }

    @Transactional(readOnly = true)
    public Page<IntegrationInstanceDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all IntegrationInstances");
        return integrationInstanceRepository.findAll(pageable).map(integrationInstanceMapper::toDto).map(this::enrichDTO);
    }

    @Transactional(readOnly = true)
    public Optional<IntegrationInstanceDTO> findOne(Long id) {
        LOG.debug("Request to get IntegrationInstance : {}", id);
        return integrationInstanceRepository.findById(id).map(integrationInstanceMapper::toDto).map(this::enrichDTO);
    }

    @Transactional(readOnly = true)
    public List<IntegrationInstanceDTO> findByControlIntegrationCode(String code) {
        LOG.debug("Request to get IntegrationInstances by integration code : {}", code);
        return integrationInstanceRepository.findByControlIntegrationCode(code).stream()
            .map(integrationInstanceMapper::toDto)
            .map(this::enrichDTO)
            .toList();
    }

    public void delete(Long id) {
        LOG.debug("Request to delete IntegrationInstance : {}", id);
        integrationInstanceRepository.deleteById(id);
    }

    private IntegrationInstanceDTO enrichDTO(IntegrationInstanceDTO dto) {
        if (dto.getMonitoredServiceId() != null) {
            monitoredServiceRepository.findById(dto.getMonitoredServiceId())
                .ifPresent(service -> dto.setMonitoredServiceName(service.getName()));
        }
        if (dto.getHttpMonitorId() != null) {
            httpMonitorRepository.findById(dto.getHttpMonitorId())
                .ifPresent(monitor -> dto.setHttpMonitorName(monitor.getName()));
        }
        if (dto.getDatacenterId() != null) {
            datacenterRepository.findById(dto.getDatacenterId())
                .ifPresent(datacenter -> dto.setDatacenterName(datacenter.getName()));
        }
        return dto;
    }
}
