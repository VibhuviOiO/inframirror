package vibhuvi.oio.inframirror.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.domain.ServiceHeartbeat;
import vibhuvi.oio.inframirror.repository.ServiceHeartbeatRepository;
import vibhuvi.oio.inframirror.repository.search.ServiceHeartbeatSearchRepository;
import vibhuvi.oio.inframirror.service.ServiceHeartbeatService;
import vibhuvi.oio.inframirror.service.dto.ServiceHeartbeatDTO;
import vibhuvi.oio.inframirror.service.mapper.ServiceHeartbeatMapper;

/**
 * Service Implementation for managing {@link vibhuvi.oio.inframirror.domain.ServiceHeartbeat}.
 */
@Service
@Transactional
public class ServiceHeartbeatServiceImpl implements ServiceHeartbeatService {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceHeartbeatServiceImpl.class);

    private final ServiceHeartbeatRepository serviceHeartbeatRepository;

    private final ServiceHeartbeatMapper serviceHeartbeatMapper;

    private final ServiceHeartbeatSearchRepository serviceHeartbeatSearchRepository;

    public ServiceHeartbeatServiceImpl(
        ServiceHeartbeatRepository serviceHeartbeatRepository,
        ServiceHeartbeatMapper serviceHeartbeatMapper,
        ServiceHeartbeatSearchRepository serviceHeartbeatSearchRepository
    ) {
        this.serviceHeartbeatRepository = serviceHeartbeatRepository;
        this.serviceHeartbeatMapper = serviceHeartbeatMapper;
        this.serviceHeartbeatSearchRepository = serviceHeartbeatSearchRepository;
    }

    @Override
    public ServiceHeartbeatDTO save(ServiceHeartbeatDTO serviceHeartbeatDTO) {
        LOG.debug("Request to save ServiceHeartbeat : {}", serviceHeartbeatDTO);
        ServiceHeartbeat serviceHeartbeat = serviceHeartbeatMapper.toEntity(serviceHeartbeatDTO);
        serviceHeartbeat = serviceHeartbeatRepository.save(serviceHeartbeat);
        serviceHeartbeatSearchRepository.index(serviceHeartbeat);
        return serviceHeartbeatMapper.toDto(serviceHeartbeat);
    }

    @Override
    public ServiceHeartbeatDTO update(ServiceHeartbeatDTO serviceHeartbeatDTO) {
        LOG.debug("Request to update ServiceHeartbeat : {}", serviceHeartbeatDTO);
        ServiceHeartbeat serviceHeartbeat = serviceHeartbeatMapper.toEntity(serviceHeartbeatDTO);
        serviceHeartbeat = serviceHeartbeatRepository.save(serviceHeartbeat);
        serviceHeartbeatSearchRepository.index(serviceHeartbeat);
        return serviceHeartbeatMapper.toDto(serviceHeartbeat);
    }

    @Override
    public Optional<ServiceHeartbeatDTO> partialUpdate(ServiceHeartbeatDTO serviceHeartbeatDTO) {
        LOG.debug("Request to partially update ServiceHeartbeat : {}", serviceHeartbeatDTO);

        return serviceHeartbeatRepository
            .findById(serviceHeartbeatDTO.getId())
            .map(existingServiceHeartbeat -> {
                serviceHeartbeatMapper.partialUpdate(existingServiceHeartbeat, serviceHeartbeatDTO);

                return existingServiceHeartbeat;
            })
            .map(serviceHeartbeatRepository::save)
            .map(savedServiceHeartbeat -> {
                serviceHeartbeatSearchRepository.index(savedServiceHeartbeat);
                return savedServiceHeartbeat;
            })
            .map(serviceHeartbeatMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceHeartbeatDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ServiceHeartbeats");
        return serviceHeartbeatRepository.findAll(pageable).map(serviceHeartbeatMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ServiceHeartbeatDTO> findOne(Long id) {
        LOG.debug("Request to get ServiceHeartbeat : {}", id);
        return serviceHeartbeatRepository.findById(id).map(serviceHeartbeatMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ServiceHeartbeat : {}", id);
        serviceHeartbeatRepository.deleteById(id);
        serviceHeartbeatSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceHeartbeatDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of ServiceHeartbeats for query {}", query);
        return serviceHeartbeatSearchRepository.search(query, pageable).map(serviceHeartbeatMapper::toDto);
    }
}
