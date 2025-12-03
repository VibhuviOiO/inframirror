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
import vibhuvi.oio.inframirror.domain.HttpMonitor;
import vibhuvi.oio.inframirror.repository.HttpMonitorRepository;
import vibhuvi.oio.inframirror.repository.search.HttpMonitorSearchRepository;
import vibhuvi.oio.inframirror.service.criteria.HttpMonitorCriteria;
import vibhuvi.oio.inframirror.service.dto.HttpMonitorDTO;
import vibhuvi.oio.inframirror.service.mapper.HttpMonitorMapper;

/**
 * Service for executing complex queries for {@link HttpMonitor} entities in the database.
 * The main input is a {@link HttpMonitorCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link HttpMonitorDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class HttpMonitorQueryService extends QueryService<HttpMonitor> {

    private static final Logger LOG = LoggerFactory.getLogger(HttpMonitorQueryService.class);

    private final HttpMonitorRepository httpMonitorRepository;

    private final HttpMonitorMapper httpMonitorMapper;

    private final HttpMonitorSearchRepository httpMonitorSearchRepository;

    public HttpMonitorQueryService(
        HttpMonitorRepository httpMonitorRepository,
        HttpMonitorMapper httpMonitorMapper,
        HttpMonitorSearchRepository httpMonitorSearchRepository
    ) {
        this.httpMonitorRepository = httpMonitorRepository;
        this.httpMonitorMapper = httpMonitorMapper;
        this.httpMonitorSearchRepository = httpMonitorSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link HttpMonitorDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<HttpMonitorDTO> findByCriteria(HttpMonitorCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<HttpMonitor> specification = createSpecification(criteria);
        return httpMonitorRepository.findAll(specification, page).map(httpMonitorMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(HttpMonitorCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<HttpMonitor> specification = createSpecification(criteria);
        return httpMonitorRepository.count(specification);
    }

    /**
     * Function to convert {@link HttpMonitorCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<HttpMonitor> createSpecification(HttpMonitorCriteria criteria) {
        Specification<HttpMonitor> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), HttpMonitor_.id),
                buildStringSpecification(criteria.getName(), HttpMonitor_.name),
                buildStringSpecification(criteria.getMethod(), HttpMonitor_.method),
                buildStringSpecification(criteria.getType(), HttpMonitor_.type),
                buildSpecification(criteria.getHeartbeatsId(), root ->
                    root.join(HttpMonitor_.heartbeats, JoinType.LEFT).get(HttpHeartbeat_.id)
                ),
                buildSpecification(criteria.getScheduleId(), root -> root.join(HttpMonitor_.schedule, JoinType.LEFT).get(Schedule_.id))
            );
        }
        return specification;
    }
}
