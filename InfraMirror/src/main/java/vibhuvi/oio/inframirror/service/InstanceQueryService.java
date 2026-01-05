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
import vibhuvi.oio.inframirror.domain.Instance;
import vibhuvi.oio.inframirror.repository.InstanceRepository;
import vibhuvi.oio.inframirror.service.criteria.InstanceCriteria;
import vibhuvi.oio.inframirror.service.dto.InstanceDTO;
import vibhuvi.oio.inframirror.service.mapper.InstanceMapper;

/**
 * Service for executing complex queries for {@link Instance} entities in the database.
 * The main input is a {@link InstanceCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link InstanceDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class InstanceQueryService extends QueryService<Instance> {

    private static final Logger LOG = LoggerFactory.getLogger(InstanceQueryService.class);

    private final InstanceRepository instanceRepository;

    private final InstanceMapper instanceMapper;

    public InstanceQueryService(InstanceRepository instanceRepository, InstanceMapper instanceMapper) {
        this.instanceRepository = instanceRepository;
        this.instanceMapper = instanceMapper;
    }

    /**
     * Return a {@link Page} of {@link InstanceDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<InstanceDTO> findByCriteria(InstanceCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Instance> specification = createSpecification(criteria);
        return instanceRepository.findAll(specification, page).map(instanceMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(InstanceCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Instance> specification = createSpecification(criteria);
        return instanceRepository.count(specification);
    }

    /**
     * Function to convert {@link InstanceCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Instance> createSpecification(InstanceCriteria criteria) {
        Specification<Instance> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Instance_.id),
                buildStringSpecification(criteria.getName(), Instance_.name),
                buildStringSpecification(criteria.getHostname(), Instance_.hostname),
                buildStringSpecification(criteria.getDescription(), Instance_.description),
                buildSpecification(criteria.getInstanceType(), Instance_.instanceType),
                buildSpecification(criteria.getOperatingSystem(), Instance_.operatingSystem),
                buildStringSpecification(criteria.getPlatform(), Instance_.platform),
                buildStringSpecification(criteria.getPrivateIpAddress(), Instance_.privateIpAddress),
                buildStringSpecification(criteria.getPublicIpAddress(), Instance_.publicIpAddress),
                buildRangeSpecification(criteria.getCreatedAt(), Instance_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), Instance_.updatedAt),
                buildSpecification(criteria.getHeartbeatId(), root ->
                    root.join(Instance_.heartbeats, JoinType.LEFT).get(InstanceHeartbeat_.id)
                ),
                buildSpecification(criteria.getServiceInstanceId(), root ->
                    root.join(Instance_.serviceInstances, JoinType.LEFT).get(ServiceInstance_.id)
                ),
                buildSpecification(criteria.getDatacenterId(), root -> root.join(Instance_.datacenter, JoinType.LEFT).get(Datacenter_.id)),
                buildSpecification(criteria.getAgentId(), root -> root.join(Instance_.agent, JoinType.LEFT).get(Agent_.id))
            );
        }
        return specification;
    }
}
