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
import vibhuvi.oio.inframirror.domain.Service;
import vibhuvi.oio.inframirror.repository.ServiceRepository;
import vibhuvi.oio.inframirror.repository.search.ServiceSearchRepository;
import vibhuvi.oio.inframirror.service.criteria.ServiceCriteria;
import vibhuvi.oio.inframirror.service.dto.ServiceDTO;
import vibhuvi.oio.inframirror.service.mapper.ServiceMapper;

/**
 * Service for executing complex queries for {@link Service} entities in the database.
 * The main input is a {@link ServiceCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ServiceDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ServiceQueryService extends QueryService<Service> {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceQueryService.class);

    private final ServiceRepository serviceRepository;

    private final ServiceMapper serviceMapper;

    private final ServiceSearchRepository serviceSearchRepository;

    public ServiceQueryService(
        ServiceRepository serviceRepository,
        ServiceMapper serviceMapper,
        ServiceSearchRepository serviceSearchRepository
    ) {
        this.serviceRepository = serviceRepository;
        this.serviceMapper = serviceMapper;
        this.serviceSearchRepository = serviceSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link ServiceDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ServiceDTO> findByCriteria(ServiceCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Service> specification = createSpecification(criteria);
        return serviceRepository.findAll(specification, page).map(serviceMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ServiceCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Service> specification = createSpecification(criteria);
        return serviceRepository.count(specification);
    }

    /**
     * Function to convert {@link ServiceCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Service> createSpecification(ServiceCriteria criteria) {
        Specification<Service> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Service_.id),
                buildStringSpecification(criteria.getName(), Service_.name),
                buildStringSpecification(criteria.getDescription(), Service_.description),
                buildStringSpecification(criteria.getServiceType(), Service_.serviceType),
                buildStringSpecification(criteria.getEnvironment(), Service_.environment),
                buildSpecification(criteria.getMonitoringEnabled(), Service_.monitoringEnabled),
                buildSpecification(criteria.getClusterMonitoringEnabled(), Service_.clusterMonitoringEnabled),
                buildRangeSpecification(criteria.getIntervalSeconds(), Service_.intervalSeconds),
                buildRangeSpecification(criteria.getTimeoutMs(), Service_.timeoutMs),
                buildRangeSpecification(criteria.getRetryCount(), Service_.retryCount),
                buildRangeSpecification(criteria.getLatencyWarningMs(), Service_.latencyWarningMs),
                buildRangeSpecification(criteria.getLatencyCriticalMs(), Service_.latencyCriticalMs),
                buildSpecification(criteria.getIsActive(), Service_.isActive),
                buildRangeSpecification(criteria.getCreatedAt(), Service_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), Service_.updatedAt),
                buildSpecification(criteria.getServiceInstanceId(), root ->
                    root.join(Service_.serviceInstances, JoinType.LEFT).get(ServiceInstance_.id)
                ),
                buildSpecification(criteria.getHeartbeatId(), root ->
                    root.join(Service_.heartbeats, JoinType.LEFT).get(ServiceHeartbeat_.id)
                ),
                buildSpecification(criteria.getDatacenterId(), root -> root.join(Service_.datacenter, JoinType.LEFT).get(Datacenter_.id))
            );
        }
        return specification;
    }
}
