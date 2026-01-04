package vibhuvi.oio.inframirror.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.domain.MonitoredService;
import vibhuvi.oio.inframirror.repository.MonitoredServiceRepository;
import vibhuvi.oio.inframirror.repository.base.SearchableRepository;
import vibhuvi.oio.inframirror.service.MonitoredServiceSearchService;
import vibhuvi.oio.inframirror.service.base.AbstractSearchService;
import vibhuvi.oio.inframirror.service.dto.MonitoredServiceDTO;
import vibhuvi.oio.inframirror.service.dto.MonitoredServiceSearchResultDTO;
import vibhuvi.oio.inframirror.service.mapper.MonitoredServiceMapper;

/**
 * Service for searching monitored services.
 * Provides full-text, prefix, and fuzzy search capabilities.
 */
@Service
@Transactional(readOnly = true)
public class MonitoredServiceSearchServiceImpl extends AbstractSearchService<MonitoredServiceDTO, MonitoredService> implements MonitoredServiceSearchService {

    private static final Logger LOG = LoggerFactory.getLogger(MonitoredServiceSearchServiceImpl.class);
    private final MonitoredServiceRepository monitoredServiceRepository;
    private final MonitoredServiceMapper monitoredServiceMapper;

    public MonitoredServiceSearchServiceImpl(MonitoredServiceRepository monitoredServiceRepository, MonitoredServiceMapper monitoredServiceMapper) {
        this.monitoredServiceRepository = monitoredServiceRepository;
        this.monitoredServiceMapper = monitoredServiceMapper;
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    protected SearchableRepository<MonitoredService> getRepository() {
        return monitoredServiceRepository;
    }

    @Override
    protected MonitoredServiceMapper getMapper() {
        return monitoredServiceMapper;
    }

    @Override
    protected String getEntityName() {
        return "monitoredService";
    }

    @Override
    public Page<MonitoredServiceSearchResultDTO> searchWithHighlight(String query, Pageable pageable) {
        validateSearchParams(query, pageable);
        LOG.debug("Request to search MonitoredServices with highlight for query {}", query);
        
        return monitoredServiceRepository
            .searchWithHighlight(query, pageable)
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
