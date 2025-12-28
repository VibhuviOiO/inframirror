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
import vibhuvi.oio.inframirror.domain.*; // for static metamodels
import vibhuvi.oio.inframirror.domain.MonitoredService;
import vibhuvi.oio.inframirror.repository.MonitoredServiceRepository;
import vibhuvi.oio.inframirror.service.criteria.MonitoredServiceCriteria;
import vibhuvi.oio.inframirror.service.dto.MonitoredServiceDTO;
import vibhuvi.oio.inframirror.service.mapper.MonitoredServiceMapper;

/**
 * Service for executing complex queries for {@link MonitoredService} entities in the database.
 * The main input is a {@link MonitoredServiceCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link MonitoredServiceDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class MonitoredServiceQueryService extends QueryService<MonitoredService> {

    private static final Logger LOG = LoggerFactory.getLogger(MonitoredServiceQueryService.class);

    private final MonitoredServiceRepository monitoredServiceRepository;

    private final MonitoredServiceMapper monitoredServiceMapper;

    public MonitoredServiceQueryService(
        MonitoredServiceRepository monitoredServiceRepository,
        MonitoredServiceMapper monitoredServiceMapper
    ) {
        this.monitoredServiceRepository = monitoredServiceRepository;
        this.monitoredServiceMapper = monitoredServiceMapper;
    }

    /**
     * Return a {@link Page} of {@link MonitoredServiceDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<MonitoredServiceDTO> findByCriteria(MonitoredServiceCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<MonitoredService> specification = createSpecification(criteria);
        return monitoredServiceRepository.findAll(specification, page).map(monitoredServiceMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(MonitoredServiceCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<MonitoredService> specification = createSpecification(criteria);
        return monitoredServiceRepository.count(specification);
    }

    /**
     * Function to convert {@link MonitoredServiceCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<MonitoredService> createSpecification(MonitoredServiceCriteria criteria) {
        Specification<MonitoredService> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), MonitoredService_.id),
                buildStringSpecification(criteria.getName(), MonitoredService_.name),
                buildStringSpecification(criteria.getDescription(), MonitoredService_.description),
                buildStringSpecification(criteria.getServiceType(), MonitoredService_.serviceType),
                buildStringSpecification(criteria.getEnvironment(), MonitoredService_.environment),
                buildSpecification(criteria.getMonitoringEnabled(), MonitoredService_.monitoringEnabled),
                buildSpecification(criteria.getClusterMonitoringEnabled(), MonitoredService_.clusterMonitoringEnabled),
                buildRangeSpecification(criteria.getIntervalSeconds(), MonitoredService_.intervalSeconds),
                buildRangeSpecification(criteria.getTimeoutMs(), MonitoredService_.timeoutMs),
                buildRangeSpecification(criteria.getRetryCount(), MonitoredService_.retryCount),
                buildRangeSpecification(criteria.getLatencyWarningMs(), MonitoredService_.latencyWarningMs),
                buildRangeSpecification(criteria.getLatencyCriticalMs(), MonitoredService_.latencyCriticalMs),
                buildSpecification(criteria.getIsActive(), MonitoredService_.isActive),
                buildRangeSpecification(criteria.getCreatedAt(), MonitoredService_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), MonitoredService_.updatedAt),
                buildSpecification(criteria.getServiceInstanceId(), root ->
                    root.join(MonitoredService_.serviceInstances, JoinType.LEFT).get(ServiceInstance_.id)
                ),
                buildSpecification(criteria.getHeartbeatId(), root ->
                    root.join(MonitoredService_.heartbeats, JoinType.LEFT).get(ServiceHeartbeat_.id)
                ),
                buildSpecification(criteria.getDatacenterId(), root ->
                    root.join(MonitoredService_.datacenter, JoinType.LEFT).get(Datacenter_.id)
                )
            );
        }
        return specification;
    }
}
