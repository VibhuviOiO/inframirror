package vibhuvi.oio.inframirror.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.domain.MonitoredService;
import vibhuvi.oio.inframirror.repository.MonitoredServiceRepository;
import vibhuvi.oio.inframirror.repository.search.MonitoredServiceSearchRepository;
import vibhuvi.oio.inframirror.service.MonitoredServiceService;
import vibhuvi.oio.inframirror.service.dto.MonitoredServiceDTO;
import vibhuvi.oio.inframirror.service.mapper.MonitoredServiceMapper;

/**
 * Service Implementation for managing {@link vibhuvi.oio.inframirror.domain.MonitoredService}.
 */
@Service
@Transactional
public class MonitoredServiceServiceImpl implements MonitoredServiceService {

    private static final Logger LOG = LoggerFactory.getLogger(MonitoredServiceServiceImpl.class);

    private final MonitoredServiceRepository monitoredServiceRepository;

    private final MonitoredServiceMapper monitoredServiceMapper;

    private final MonitoredServiceSearchRepository monitoredServiceSearchRepository;

    public MonitoredServiceServiceImpl(
        MonitoredServiceRepository monitoredServiceRepository,
        MonitoredServiceMapper monitoredServiceMapper,
        MonitoredServiceSearchRepository monitoredServiceSearchRepository
    ) {
        this.monitoredServiceRepository = monitoredServiceRepository;
        this.monitoredServiceMapper = monitoredServiceMapper;
        this.monitoredServiceSearchRepository = monitoredServiceSearchRepository;
    }

    @Override
    public MonitoredServiceDTO save(MonitoredServiceDTO monitoredServiceDTO) {
        LOG.debug("Request to save MonitoredService : {}", monitoredServiceDTO);
        MonitoredService monitoredService = monitoredServiceMapper.toEntity(monitoredServiceDTO);
        monitoredService = monitoredServiceRepository.save(monitoredService);
        monitoredServiceSearchRepository.index(monitoredService);
        return monitoredServiceMapper.toDto(monitoredService);
    }

    @Override
    public MonitoredServiceDTO update(MonitoredServiceDTO monitoredServiceDTO) {
        LOG.debug("Request to update MonitoredService : {}", monitoredServiceDTO);
        MonitoredService monitoredService = monitoredServiceMapper.toEntity(monitoredServiceDTO);
        monitoredService = monitoredServiceRepository.save(monitoredService);
        monitoredServiceSearchRepository.index(monitoredService);
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
            .map(savedMonitoredService -> {
                monitoredServiceSearchRepository.index(savedMonitoredService);
                return savedMonitoredService;
            })
            .map(monitoredServiceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MonitoredServiceDTO> findOne(Long id) {
        LOG.debug("Request to get MonitoredService : {}", id);
        return monitoredServiceRepository.findById(id).map(monitoredServiceMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete MonitoredService : {}", id);
        monitoredServiceRepository.deleteById(id);
        monitoredServiceSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MonitoredServiceDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of MonitoredServices for query {}", query);
        return monitoredServiceSearchRepository.search(query, pageable).map(monitoredServiceMapper::toDto);
    }
}
