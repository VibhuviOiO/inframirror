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
import vibhuvi.oio.inframirror.domain.PingHeartbeat;
import vibhuvi.oio.inframirror.repository.PingHeartbeatRepository;
import vibhuvi.oio.inframirror.repository.search.PingHeartbeatSearchRepository;
import vibhuvi.oio.inframirror.service.criteria.PingHeartbeatCriteria;
import vibhuvi.oio.inframirror.service.dto.PingHeartbeatDTO;
import vibhuvi.oio.inframirror.service.mapper.PingHeartbeatMapper;

/**
 * Service for executing complex queries for {@link PingHeartbeat} entities in the database.
 * The main input is a {@link PingHeartbeatCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link PingHeartbeatDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PingHeartbeatQueryService extends QueryService<PingHeartbeat> {

    private static final Logger LOG = LoggerFactory.getLogger(PingHeartbeatQueryService.class);

    private final PingHeartbeatRepository pingHeartbeatRepository;

    private final PingHeartbeatMapper pingHeartbeatMapper;

    private final PingHeartbeatSearchRepository pingHeartbeatSearchRepository;

    public PingHeartbeatQueryService(
        PingHeartbeatRepository pingHeartbeatRepository,
        PingHeartbeatMapper pingHeartbeatMapper,
        PingHeartbeatSearchRepository pingHeartbeatSearchRepository
    ) {
        this.pingHeartbeatRepository = pingHeartbeatRepository;
        this.pingHeartbeatMapper = pingHeartbeatMapper;
        this.pingHeartbeatSearchRepository = pingHeartbeatSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link PingHeartbeatDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PingHeartbeatDTO> findByCriteria(PingHeartbeatCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<PingHeartbeat> specification = createSpecification(criteria);
        return pingHeartbeatRepository.findAll(specification, page).map(pingHeartbeatMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PingHeartbeatCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<PingHeartbeat> specification = createSpecification(criteria);
        return pingHeartbeatRepository.count(specification);
    }

    /**
     * Function to convert {@link PingHeartbeatCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<PingHeartbeat> createSpecification(PingHeartbeatCriteria criteria) {
        Specification<PingHeartbeat> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), PingHeartbeat_.id),
                buildRangeSpecification(criteria.getExecutedAt(), PingHeartbeat_.executedAt),
                buildStringSpecification(criteria.getHeartbeatType(), PingHeartbeat_.heartbeatType),
                buildSpecification(criteria.getSuccess(), PingHeartbeat_.success),
                buildRangeSpecification(criteria.getResponseTimeMs(), PingHeartbeat_.responseTimeMs),
                buildRangeSpecification(criteria.getPacketLoss(), PingHeartbeat_.packetLoss),
                buildRangeSpecification(criteria.getJitterMs(), PingHeartbeat_.jitterMs),
                buildRangeSpecification(criteria.getCpuUsage(), PingHeartbeat_.cpuUsage),
                buildRangeSpecification(criteria.getMemoryUsage(), PingHeartbeat_.memoryUsage),
                buildRangeSpecification(criteria.getDiskUsage(), PingHeartbeat_.diskUsage),
                buildRangeSpecification(criteria.getLoadAverage(), PingHeartbeat_.loadAverage),
                buildRangeSpecification(criteria.getProcessCount(), PingHeartbeat_.processCount),
                buildRangeSpecification(criteria.getNetworkRxBytes(), PingHeartbeat_.networkRxBytes),
                buildRangeSpecification(criteria.getNetworkTxBytes(), PingHeartbeat_.networkTxBytes),
                buildRangeSpecification(criteria.getUptimeSeconds(), PingHeartbeat_.uptimeSeconds),
                buildStringSpecification(criteria.getStatus(), PingHeartbeat_.status),
                buildStringSpecification(criteria.getErrorType(), PingHeartbeat_.errorType),
                buildSpecification(criteria.getInstanceId(), root -> root.join(PingHeartbeat_.instance, JoinType.LEFT).get(Instance_.id)),
                buildSpecification(criteria.getAgentId(), root -> root.join(PingHeartbeat_.agent, JoinType.LEFT).get(Agent_.id))
            );
        }
        return specification;
    }
}
