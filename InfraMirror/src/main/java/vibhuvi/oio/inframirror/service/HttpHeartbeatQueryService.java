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
import vibhuvi.oio.inframirror.domain.HttpHeartbeat;
import vibhuvi.oio.inframirror.repository.HttpHeartbeatRepository;
import vibhuvi.oio.inframirror.repository.search.HttpHeartbeatSearchRepository;
import vibhuvi.oio.inframirror.service.criteria.HttpHeartbeatCriteria;
import vibhuvi.oio.inframirror.service.dto.HttpHeartbeatDTO;
import vibhuvi.oio.inframirror.service.mapper.HttpHeartbeatMapper;

/**
 * Service for executing complex queries for {@link HttpHeartbeat} entities in the database.
 * The main input is a {@link HttpHeartbeatCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link HttpHeartbeatDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class HttpHeartbeatQueryService extends QueryService<HttpHeartbeat> {

    private static final Logger LOG = LoggerFactory.getLogger(HttpHeartbeatQueryService.class);

    private final HttpHeartbeatRepository httpHeartbeatRepository;

    private final HttpHeartbeatMapper httpHeartbeatMapper;

    private final HttpHeartbeatSearchRepository httpHeartbeatSearchRepository;

    public HttpHeartbeatQueryService(
        HttpHeartbeatRepository httpHeartbeatRepository,
        HttpHeartbeatMapper httpHeartbeatMapper,
        HttpHeartbeatSearchRepository httpHeartbeatSearchRepository
    ) {
        this.httpHeartbeatRepository = httpHeartbeatRepository;
        this.httpHeartbeatMapper = httpHeartbeatMapper;
        this.httpHeartbeatSearchRepository = httpHeartbeatSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link HttpHeartbeatDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<HttpHeartbeatDTO> findByCriteria(HttpHeartbeatCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<HttpHeartbeat> specification = createSpecification(criteria);
        return httpHeartbeatRepository.findAll(specification, page).map(httpHeartbeatMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(HttpHeartbeatCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<HttpHeartbeat> specification = createSpecification(criteria);
        return httpHeartbeatRepository.count(specification);
    }

    /**
     * Function to convert {@link HttpHeartbeatCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<HttpHeartbeat> createSpecification(HttpHeartbeatCriteria criteria) {
        Specification<HttpHeartbeat> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), HttpHeartbeat_.id),
                buildRangeSpecification(criteria.getExecutedAt(), HttpHeartbeat_.executedAt),
                buildSpecification(criteria.getSuccess(), HttpHeartbeat_.success),
                buildRangeSpecification(criteria.getResponseTimeMs(), HttpHeartbeat_.responseTimeMs),
                buildRangeSpecification(criteria.getResponseSizeBytes(), HttpHeartbeat_.responseSizeBytes),
                buildRangeSpecification(criteria.getResponseStatusCode(), HttpHeartbeat_.responseStatusCode),
                buildStringSpecification(criteria.getResponseContentType(), HttpHeartbeat_.responseContentType),
                buildStringSpecification(criteria.getResponseServer(), HttpHeartbeat_.responseServer),
                buildStringSpecification(criteria.getResponseCacheStatus(), HttpHeartbeat_.responseCacheStatus),
                buildRangeSpecification(criteria.getDnsLookupMs(), HttpHeartbeat_.dnsLookupMs),
                buildRangeSpecification(criteria.getTcpConnectMs(), HttpHeartbeat_.tcpConnectMs),
                buildRangeSpecification(criteria.getTlsHandshakeMs(), HttpHeartbeat_.tlsHandshakeMs),
                buildRangeSpecification(criteria.getTimeToFirstByteMs(), HttpHeartbeat_.timeToFirstByteMs),
                buildRangeSpecification(criteria.getWarningThresholdMs(), HttpHeartbeat_.warningThresholdMs),
                buildRangeSpecification(criteria.getCriticalThresholdMs(), HttpHeartbeat_.criticalThresholdMs),
                buildStringSpecification(criteria.getErrorType(), HttpHeartbeat_.errorType),
                buildSpecification(criteria.getMonitorId(), root -> root.join(HttpHeartbeat_.monitor, JoinType.LEFT).get(HttpMonitor_.id)),
                buildSpecification(criteria.getAgentId(), root -> root.join(HttpHeartbeat_.agent, JoinType.LEFT).get(Agent_.id))
            );
        }
        return specification;
    }
}
