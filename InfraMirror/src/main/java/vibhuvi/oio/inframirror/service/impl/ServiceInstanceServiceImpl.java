package vibhuvi.oio.inframirror.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.domain.ServiceInstance;
import vibhuvi.oio.inframirror.repository.ServiceInstanceRepository;
import vibhuvi.oio.inframirror.service.ServiceInstanceService;
import vibhuvi.oio.inframirror.service.dto.ServiceInstanceDTO;
import vibhuvi.oio.inframirror.service.mapper.ServiceInstanceMapper;

/**
 * Service Implementation for managing {@link vibhuvi.oio.inframirror.domain.ServiceInstance}.
 */
@Service
@Transactional
public class ServiceInstanceServiceImpl implements ServiceInstanceService {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceInstanceServiceImpl.class);

    private final ServiceInstanceRepository serviceInstanceRepository;

    private final ServiceInstanceMapper serviceInstanceMapper;

    public ServiceInstanceServiceImpl(
        ServiceInstanceRepository serviceInstanceRepository,
        ServiceInstanceMapper serviceInstanceMapper
    ) {
        this.serviceInstanceRepository = serviceInstanceRepository;
        this.serviceInstanceMapper = serviceInstanceMapper;
    }

    @Override
    public ServiceInstanceDTO save(ServiceInstanceDTO serviceInstanceDTO) {
        LOG.debug("Request to save ServiceInstance : {}", serviceInstanceDTO);
        ServiceInstance serviceInstance = serviceInstanceMapper.toEntity(serviceInstanceDTO);
        serviceInstance = serviceInstanceRepository.save(serviceInstance);
        return serviceInstanceMapper.toDto(serviceInstance);
    }

    @Override
    public ServiceInstanceDTO update(ServiceInstanceDTO serviceInstanceDTO) {
        LOG.debug("Request to update ServiceInstance : {}", serviceInstanceDTO);
        ServiceInstance serviceInstance = serviceInstanceMapper.toEntity(serviceInstanceDTO);
        serviceInstance = serviceInstanceRepository.save(serviceInstance);
        return serviceInstanceMapper.toDto(serviceInstance);
    }

    @Override
    public Optional<ServiceInstanceDTO> partialUpdate(ServiceInstanceDTO serviceInstanceDTO) {
        LOG.debug("Request to partially update ServiceInstance : {}", serviceInstanceDTO);

        return serviceInstanceRepository
            .findById(serviceInstanceDTO.getId())
            .map(existingServiceInstance -> {
                serviceInstanceMapper.partialUpdate(existingServiceInstance, serviceInstanceDTO);

                return existingServiceInstance;
            })
            .map(serviceInstanceRepository::save)
            .map(serviceInstanceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceInstanceDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ServiceInstances");
        return serviceInstanceRepository.findAll(pageable).map(serviceInstanceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ServiceInstanceDTO> findOne(Long id) {
        LOG.debug("Request to get ServiceInstance : {}", id);
        return serviceInstanceRepository.findById(id).map(serviceInstanceMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ServiceInstance : {}", id);
        serviceInstanceRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceInstanceDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of ServiceInstances for query {}", query);
        return Page.empty(pageable);
    }
}
