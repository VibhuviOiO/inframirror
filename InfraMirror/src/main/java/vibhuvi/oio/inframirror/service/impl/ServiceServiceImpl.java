package vibhuvi.oio.inframirror.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.domain.Service;
import vibhuvi.oio.inframirror.repository.ServiceRepository;
import vibhuvi.oio.inframirror.repository.search.ServiceSearchRepository;
import vibhuvi.oio.inframirror.service.ServiceService;
import vibhuvi.oio.inframirror.service.dto.ServiceDTO;
import vibhuvi.oio.inframirror.service.mapper.ServiceMapper;

/**
 * Service Implementation for managing {@link vibhuvi.oio.inframirror.domain.Service}.
 */
@Service
@Transactional
public class ServiceServiceImpl implements ServiceService {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceServiceImpl.class);

    private final ServiceRepository serviceRepository;

    private final ServiceMapper serviceMapper;

    private final ServiceSearchRepository serviceSearchRepository;

    public ServiceServiceImpl(
        ServiceRepository serviceRepository,
        ServiceMapper serviceMapper,
        ServiceSearchRepository serviceSearchRepository
    ) {
        this.serviceRepository = serviceRepository;
        this.serviceMapper = serviceMapper;
        this.serviceSearchRepository = serviceSearchRepository;
    }

    @Override
    public ServiceDTO save(ServiceDTO serviceDTO) {
        LOG.debug("Request to save Service : {}", serviceDTO);
        Service service = serviceMapper.toEntity(serviceDTO);
        service = serviceRepository.save(service);
        serviceSearchRepository.index(service);
        return serviceMapper.toDto(service);
    }

    @Override
    public ServiceDTO update(ServiceDTO serviceDTO) {
        LOG.debug("Request to update Service : {}", serviceDTO);
        Service service = serviceMapper.toEntity(serviceDTO);
        service = serviceRepository.save(service);
        serviceSearchRepository.index(service);
        return serviceMapper.toDto(service);
    }

    @Override
    public Optional<ServiceDTO> partialUpdate(ServiceDTO serviceDTO) {
        LOG.debug("Request to partially update Service : {}", serviceDTO);

        return serviceRepository
            .findById(serviceDTO.getId())
            .map(existingService -> {
                serviceMapper.partialUpdate(existingService, serviceDTO);

                return existingService;
            })
            .map(serviceRepository::save)
            .map(savedService -> {
                serviceSearchRepository.index(savedService);
                return savedService;
            })
            .map(serviceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ServiceDTO> findOne(Long id) {
        LOG.debug("Request to get Service : {}", id);
        return serviceRepository.findById(id).map(serviceMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Service : {}", id);
        serviceRepository.deleteById(id);
        serviceSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Services for query {}", query);
        return serviceSearchRepository.search(query, pageable).map(serviceMapper::toDto);
    }
}
