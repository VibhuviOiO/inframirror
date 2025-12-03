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
import vibhuvi.oio.inframirror.domain.AuditTrail;
import vibhuvi.oio.inframirror.repository.AuditTrailRepository;
import vibhuvi.oio.inframirror.repository.search.AuditTrailSearchRepository;
import vibhuvi.oio.inframirror.service.criteria.AuditTrailCriteria;
import vibhuvi.oio.inframirror.service.dto.AuditTrailDTO;
import vibhuvi.oio.inframirror.service.mapper.AuditTrailMapper;

/**
 * Service for executing complex queries for {@link AuditTrail} entities in the database.
 * The main input is a {@link AuditTrailCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link AuditTrailDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AuditTrailQueryService extends QueryService<AuditTrail> {

    private static final Logger LOG = LoggerFactory.getLogger(AuditTrailQueryService.class);

    private final AuditTrailRepository auditTrailRepository;

    private final AuditTrailMapper auditTrailMapper;

    private final AuditTrailSearchRepository auditTrailSearchRepository;

    public AuditTrailQueryService(
        AuditTrailRepository auditTrailRepository,
        AuditTrailMapper auditTrailMapper,
        AuditTrailSearchRepository auditTrailSearchRepository
    ) {
        this.auditTrailRepository = auditTrailRepository;
        this.auditTrailMapper = auditTrailMapper;
        this.auditTrailSearchRepository = auditTrailSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link AuditTrailDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<AuditTrailDTO> findByCriteria(AuditTrailCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<AuditTrail> specification = createSpecification(criteria);
        return auditTrailRepository.findAll(specification, page).map(auditTrailMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AuditTrailCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<AuditTrail> specification = createSpecification(criteria);
        return auditTrailRepository.count(specification);
    }

    /**
     * Function to convert {@link AuditTrailCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<AuditTrail> createSpecification(AuditTrailCriteria criteria) {
        Specification<AuditTrail> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), AuditTrail_.id),
                buildStringSpecification(criteria.getAction(), AuditTrail_.action),
                buildStringSpecification(criteria.getEntityName(), AuditTrail_.entityName),
                buildRangeSpecification(criteria.getEntityId(), AuditTrail_.entityId),
                buildRangeSpecification(criteria.getTimestamp(), AuditTrail_.timestamp),
                buildStringSpecification(criteria.getIpAddress(), AuditTrail_.ipAddress),
                buildSpecification(criteria.getUserId(), root -> root.join(AuditTrail_.user, JoinType.LEFT).get(User_.id))
            );
        }
        return specification;
    }
}
