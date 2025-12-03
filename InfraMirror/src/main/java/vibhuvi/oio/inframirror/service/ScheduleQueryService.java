package vibhuvi.oio.inframirror.service;

import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;
import vibhuvi.oio.inframirror.domain.*; // for static metamodels
import vibhuvi.oio.inframirror.domain.Schedule;
import vibhuvi.oio.inframirror.repository.ScheduleRepository;
import vibhuvi.oio.inframirror.repository.search.ScheduleSearchRepository;
import vibhuvi.oio.inframirror.service.criteria.ScheduleCriteria;
import vibhuvi.oio.inframirror.service.dto.ScheduleDTO;
import vibhuvi.oio.inframirror.service.mapper.ScheduleMapper;

/**
 * Service for executing complex queries for {@link Schedule} entities in the database.
 * The main input is a {@link ScheduleCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ScheduleDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ScheduleQueryService extends QueryService<Schedule> {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduleQueryService.class);

    private final ScheduleRepository scheduleRepository;

    private final ScheduleMapper scheduleMapper;

    private final ScheduleSearchRepository scheduleSearchRepository;

    public ScheduleQueryService(
        ScheduleRepository scheduleRepository,
        ScheduleMapper scheduleMapper,
        ScheduleSearchRepository scheduleSearchRepository
    ) {
        this.scheduleRepository = scheduleRepository;
        this.scheduleMapper = scheduleMapper;
        this.scheduleSearchRepository = scheduleSearchRepository;
    }

    /**
     * Return a {@link List} of {@link ScheduleDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ScheduleDTO> findByCriteria(ScheduleCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<Schedule> specification = createSpecification(criteria);
        return scheduleMapper.toDto(scheduleRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ScheduleCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Schedule> specification = createSpecification(criteria);
        return scheduleRepository.count(specification);
    }

    /**
     * Function to convert {@link ScheduleCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Schedule> createSpecification(ScheduleCriteria criteria) {
        Specification<Schedule> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Schedule_.id),
                buildStringSpecification(criteria.getName(), Schedule_.name),
                buildRangeSpecification(criteria.getInterval(), Schedule_.interval),
                buildSpecification(criteria.getIncludeResponseBody(), Schedule_.includeResponseBody),
                buildRangeSpecification(criteria.getThresholdsWarning(), Schedule_.thresholdsWarning),
                buildRangeSpecification(criteria.getThresholdsCritical(), Schedule_.thresholdsCritical),
                buildSpecification(criteria.getMonitorsId(), root -> root.join(Schedule_.monitors, JoinType.LEFT).get(HttpMonitor_.id))
            );
        }
        return specification;
    }
}
