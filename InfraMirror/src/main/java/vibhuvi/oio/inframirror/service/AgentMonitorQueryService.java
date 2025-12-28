package vibhuvi.oio.inframirror.service;

import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;
import vibhuvi.oio.inframirror.domain.*;
import vibhuvi.oio.inframirror.repository.AgentMonitorRepository;
import vibhuvi.oio.inframirror.repository.HttpMonitorRepository;
import vibhuvi.oio.inframirror.repository.InstanceRepository;
import vibhuvi.oio.inframirror.repository.MonitoredServiceRepository;
import vibhuvi.oio.inframirror.service.criteria.AgentMonitorCriteria;
import vibhuvi.oio.inframirror.service.dto.AgentMonitorDTO;
import vibhuvi.oio.inframirror.service.mapper.AgentMonitorMapper;

@Service
@Transactional(readOnly = true)
public class AgentMonitorQueryService extends QueryService<AgentMonitor> {

    private static final Logger LOG = LoggerFactory.getLogger(AgentMonitorQueryService.class);

    private final AgentMonitorRepository agentMonitorRepository;

    private final AgentMonitorMapper agentMonitorMapper;

    private final HttpMonitorRepository httpMonitorRepository;

    private final InstanceRepository instanceRepository;

    private final MonitoredServiceRepository monitoredServiceRepository;

    public AgentMonitorQueryService(
        AgentMonitorRepository agentMonitorRepository,
        AgentMonitorMapper agentMonitorMapper,
        HttpMonitorRepository httpMonitorRepository,
        InstanceRepository instanceRepository,
        MonitoredServiceRepository monitoredServiceRepository
    ) {
        this.agentMonitorRepository = agentMonitorRepository;
        this.agentMonitorMapper = agentMonitorMapper;
        this.httpMonitorRepository = httpMonitorRepository;
        this.instanceRepository = instanceRepository;
        this.monitoredServiceRepository = monitoredServiceRepository;
    }

    private void enrichWithMonitorName(AgentMonitorDTO dto) {
        if (dto.getMonitorType() != null && dto.getMonitorId() != null) {
            LOG.debug("Enriching monitor name for type: {}, id: {}", dto.getMonitorType(), dto.getMonitorId());
            switch (dto.getMonitorType()) {
                case "HTTP":
                    httpMonitorRepository.findById(dto.getMonitorId()).ifPresent(monitor -> {
                        LOG.debug("Found HTTP monitor name: {}", monitor.getName());
                        dto.setMonitorName(monitor.getName());
                    });
                    break;
                case "INSTANCE":
                    instanceRepository.findById(dto.getMonitorId()).ifPresent(instance -> {
                        LOG.debug("Found Instance name: {}", instance.getName());
                        dto.setMonitorName(instance.getName());
                    });
                    break;
                case "SERVICE":
                    monitoredServiceRepository.findById(dto.getMonitorId()).ifPresent(service -> {
                        LOG.debug("Found Service name: {}", service.getName());
                        dto.setMonitorName(service.getName());
                    });
                    break;
            }
            LOG.debug("After enrichment, monitorName: {}", dto.getMonitorName());
        }
    }

    @Transactional(readOnly = true)
    public Page<AgentMonitorDTO> findByCriteria(AgentMonitorCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<AgentMonitor> specification = createSpecification(criteria);
        return agentMonitorRepository.findAll(specification, page).map(agentMonitorMapper::toDto).map(dto -> {
            enrichWithMonitorName(dto);
            return dto;
        });
    }

    @Transactional(readOnly = true)
    public long countByCriteria(AgentMonitorCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<AgentMonitor> specification = createSpecification(criteria);
        return agentMonitorRepository.count(specification);
    }

    protected Specification<AgentMonitor> createSpecification(AgentMonitorCriteria criteria) {
        Specification<AgentMonitor> specification = Specification.where(null);
        if (criteria != null) {
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), AgentMonitor_.id),
                buildSpecification(criteria.getActive(), AgentMonitor_.active),
                buildStringSpecification(criteria.getMonitorType(), AgentMonitor_.monitorType),
                buildRangeSpecification(criteria.getMonitorId(), AgentMonitor_.monitorId),
                buildSpecification(criteria.getAgentId(), root -> root.join(AgentMonitor_.agent, JoinType.LEFT).get(Agent_.id))
            );
        }
        return specification;
    }
}
