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
import vibhuvi.oio.inframirror.domain.SessionLog;
import vibhuvi.oio.inframirror.repository.SessionLogRepository;
import vibhuvi.oio.inframirror.repository.search.SessionLogSearchRepository;
import vibhuvi.oio.inframirror.service.criteria.SessionLogCriteria;
import vibhuvi.oio.inframirror.service.dto.SessionLogDTO;
import vibhuvi.oio.inframirror.service.mapper.SessionLogMapper;

/**
 * Service for executing complex queries for {@link SessionLog} entities in the database.
 * The main input is a {@link SessionLogCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link SessionLogDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SessionLogQueryService extends QueryService<SessionLog> {

    private static final Logger LOG = LoggerFactory.getLogger(SessionLogQueryService.class);

    private final SessionLogRepository sessionLogRepository;

    private final SessionLogMapper sessionLogMapper;

    private final SessionLogSearchRepository sessionLogSearchRepository;

    public SessionLogQueryService(
        SessionLogRepository sessionLogRepository,
        SessionLogMapper sessionLogMapper,
        SessionLogSearchRepository sessionLogSearchRepository
    ) {
        this.sessionLogRepository = sessionLogRepository;
        this.sessionLogMapper = sessionLogMapper;
        this.sessionLogSearchRepository = sessionLogSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link SessionLogDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SessionLogDTO> findByCriteria(SessionLogCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<SessionLog> specification = createSpecification(criteria);
        return sessionLogRepository.findAll(specification, page).map(sessionLogMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SessionLogCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<SessionLog> specification = createSpecification(criteria);
        return sessionLogRepository.count(specification);
    }

    /**
     * Function to convert {@link SessionLogCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<SessionLog> createSpecification(SessionLogCriteria criteria) {
        Specification<SessionLog> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), SessionLog_.id),
                buildStringSpecification(criteria.getSessionType(), SessionLog_.sessionType),
                buildRangeSpecification(criteria.getStartTime(), SessionLog_.startTime),
                buildRangeSpecification(criteria.getEndTime(), SessionLog_.endTime),
                buildRangeSpecification(criteria.getDuration(), SessionLog_.duration),
                buildStringSpecification(criteria.getSourceIpAddress(), SessionLog_.sourceIpAddress),
                buildStringSpecification(criteria.getStatus(), SessionLog_.status),
                buildStringSpecification(criteria.getTerminationReason(), SessionLog_.terminationReason),
                buildRangeSpecification(criteria.getCommandsExecuted(), SessionLog_.commandsExecuted),
                buildRangeSpecification(criteria.getBytesTransferred(), SessionLog_.bytesTransferred),
                buildStringSpecification(criteria.getSessionId(), SessionLog_.sessionId),
                buildSpecification(criteria.getInstanceId(), root -> root.join(SessionLog_.instance, JoinType.LEFT).get(Instance_.id)),
                buildSpecification(criteria.getAgentId(), root -> root.join(SessionLog_.agent, JoinType.LEFT).get(Agent_.id)),
                buildSpecification(criteria.getUserId(), root -> root.join(SessionLog_.user, JoinType.LEFT).get(User_.id))
            );
        }
        return specification;
    }
}
