package vibhuvi.oio.inframirror.service.impl;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.domain.MonitoredService;
import vibhuvi.oio.inframirror.repository.MonitoredServiceRepository;
import vibhuvi.oio.inframirror.repository.ServiceInstanceRepository;
import vibhuvi.oio.inframirror.service.FullTextSearchUtil;
import vibhuvi.oio.inframirror.service.MonitoredServiceService;
import vibhuvi.oio.inframirror.service.dto.MonitoredServiceDTO;
import vibhuvi.oio.inframirror.service.dto.MonitoredServiceSearchResultDTO;
import vibhuvi.oio.inframirror.service.dto.ServiceInstanceDTO;
import vibhuvi.oio.inframirror.service.mapper.MonitoredServiceMapper;
import vibhuvi.oio.inframirror.service.mapper.ServiceInstanceMapper;

/**
 * Service Implementation for managing {@link vibhuvi.oio.inframirror.domain.MonitoredService}.
 */
@Service
@Transactional
public class MonitoredServiceServiceImpl implements MonitoredServiceService {

    private static final Logger LOG = LoggerFactory.getLogger(MonitoredServiceServiceImpl.class);

    private final MonitoredServiceRepository monitoredServiceRepository;

    private final MonitoredServiceMapper monitoredServiceMapper;

    private final ServiceInstanceRepository serviceInstanceRepository;

    private final ServiceInstanceMapper serviceInstanceMapper;

    public MonitoredServiceServiceImpl(
        MonitoredServiceRepository monitoredServiceRepository,
        MonitoredServiceMapper monitoredServiceMapper,
        ServiceInstanceRepository serviceInstanceRepository,
        ServiceInstanceMapper serviceInstanceMapper
    ) {
        this.monitoredServiceRepository = monitoredServiceRepository;
        this.monitoredServiceMapper = monitoredServiceMapper;
        this.serviceInstanceRepository = serviceInstanceRepository;
        this.serviceInstanceMapper = serviceInstanceMapper;
    }

    @Override
    public MonitoredServiceDTO save(MonitoredServiceDTO monitoredServiceDTO) {
        LOG.debug("Request to save MonitoredService : {}", monitoredServiceDTO);
        MonitoredService monitoredService = monitoredServiceMapper.toEntity(monitoredServiceDTO);
        monitoredService = monitoredServiceRepository.save(monitoredService);
        return monitoredServiceMapper.toDto(monitoredService);
    }

    @Override
    public MonitoredServiceDTO update(MonitoredServiceDTO monitoredServiceDTO) {
        LOG.debug("Request to update MonitoredService : {}", monitoredServiceDTO);
        MonitoredService monitoredService = monitoredServiceMapper.toEntity(monitoredServiceDTO);
        monitoredService = monitoredServiceRepository.save(monitoredService);
        return monitoredServiceMapper.toDto(monitoredService);
    }

    @Override
    public Optional<MonitoredServiceDTO> partialUpdate(MonitoredServiceDTO monitoredServiceDTO) {
        LOG.debug("Request to partially update MonitoredService : {}", monitoredServiceDTO);

        return monitoredServiceRepository
            .findById(monitoredServiceDTO.getId())
            .map(existingMonitoredService -> {
                monitoredServiceMapper.partialUpdate(existingMonitoredService, monitoredServiceDTO);

                return existingMonitoredService;
            })
            .map(monitoredServiceRepository::save)
            .map(monitoredServiceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MonitoredServiceDTO> findOne(Long id) {
        LOG.debug("Request to get MonitoredService : {}", id);
        return monitoredServiceRepository.findById(id).map(monitoredServiceMapper::toDto);
    }

    @Override
    public Optional<MonitoredServiceDTO> findByName(String name) {
        LOG.debug("Request to get MonitoredService by name : {}", name);
        return monitoredServiceRepository.findFirstByName(name).map(monitoredServiceMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete MonitoredService : {}", id);
        monitoredServiceRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceInstanceDTO> findServiceInstances(Long monitoredServiceId) {
        LOG.debug("Request to get ServiceInstances for MonitoredService : {}", monitoredServiceId);
        return serviceInstanceRepository.findByMonitoredServiceIdWithInstance(monitoredServiceId)
            .stream()
            .map(serviceInstanceMapper::toDtoWithFullInstance)
            .toList();
    }

    @Override
    public ServiceInstanceDTO addServiceInstance(Long monitoredServiceId, ServiceInstanceDTO serviceInstanceDTO) {
        LOG.debug("Request to add ServiceInstance to MonitoredService : {}, {}", monitoredServiceId, serviceInstanceDTO);
        
        MonitoredService monitoredService = monitoredServiceRepository.findById(monitoredServiceId)
            .orElseThrow(() -> new RuntimeException("MonitoredService not found with id: " + monitoredServiceId));
        
        serviceInstanceDTO.setMonitoredService(monitoredServiceMapper.toDto(monitoredService));
        
        var serviceInstance = serviceInstanceMapper.toEntity(serviceInstanceDTO);
        serviceInstance = serviceInstanceRepository.save(serviceInstance);
        
        return serviceInstanceMapper.toDtoWithFullInstance(serviceInstance);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MonitoredServiceDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of MonitoredServices for query {}", query);
        if (FullTextSearchUtil.isEmptyQuery(query)) {
            return monitoredServiceRepository.findAll(pageable).map(monitoredServiceMapper::toDto);
        }

        String searchTerm = FullTextSearchUtil.sanitizeQuery(query);
        Pageable limitedPageable = FullTextSearchUtil.createLimitedPageable(pageable);

        return monitoredServiceRepository.searchFullText(searchTerm, limitedPageable)
            .map(monitoredServiceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MonitoredServiceDTO> searchPrefix(String query, Pageable pageable) {
        if (FullTextSearchUtil.isEmptyQuery(query)) {
            return Page.empty(pageable);
        }

        String normalizedQuery = FullTextSearchUtil.normalizeQuery(query);
        Pageable limitedPageable = FullTextSearchUtil.createLimitedPageable(pageable);

        return monitoredServiceRepository.searchPrefix(normalizedQuery, limitedPageable)
            .map(monitoredServiceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MonitoredServiceDTO> searchFuzzy(String query, Pageable pageable) {
        if (FullTextSearchUtil.isEmptyQuery(query)) {
            return Page.empty(pageable);
        }

        String normalizedQuery = FullTextSearchUtil.normalizeQuery(query);
        Pageable limitedPageable = FullTextSearchUtil.createLimitedPageable(pageable);

        return monitoredServiceRepository.searchFuzzy(normalizedQuery, limitedPageable)
            .map(monitoredServiceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MonitoredServiceSearchResultDTO> searchWithHighlight(String query, Pageable pageable) {
        if (FullTextSearchUtil.isEmptyQuery(query)) {
            return Page.empty(pageable);
        }

        String normalizedQuery = FullTextSearchUtil.normalizeQuery(query);
        Pageable limitedPageable = FullTextSearchUtil.createLimitedPageable(pageable);

        return monitoredServiceRepository.searchWithHighlight(normalizedQuery, limitedPageable)
            .map(row -> {
                Long id = ((Number) row[0]).longValue();
                String name = (String) row[1];
                String description = (String) row[2];
                String serviceType = (String) row[3];
                String environment = (String) row[4];
                Float rank = ((Number) row[5]).floatValue();
                String highlight = (String) row[6];
                return new MonitoredServiceSearchResultDTO(id, name, description, serviceType, environment, rank, highlight);
            });
    }
}
