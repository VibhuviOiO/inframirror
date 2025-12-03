package vibhuvi.oio.inframirror.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;
import vibhuvi.oio.inframirror.domain.*; // for static metamodels
import vibhuvi.oio.inframirror.domain.ApiKey;
import vibhuvi.oio.inframirror.repository.ApiKeyRepository;
import vibhuvi.oio.inframirror.repository.search.ApiKeySearchRepository;
import vibhuvi.oio.inframirror.service.criteria.ApiKeyCriteria;
import vibhuvi.oio.inframirror.service.dto.ApiKeyDTO;
import vibhuvi.oio.inframirror.service.mapper.ApiKeyMapper;

/**
 * Service for executing complex queries for {@link ApiKey} entities in the database.
 * The main input is a {@link ApiKeyCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ApiKeyDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ApiKeyQueryService extends QueryService<ApiKey> {

    private static final Logger LOG = LoggerFactory.getLogger(ApiKeyQueryService.class);

    private final ApiKeyRepository apiKeyRepository;

    private final ApiKeyMapper apiKeyMapper;

    private final ApiKeySearchRepository apiKeySearchRepository;

    public ApiKeyQueryService(ApiKeyRepository apiKeyRepository, ApiKeyMapper apiKeyMapper, ApiKeySearchRepository apiKeySearchRepository) {
        this.apiKeyRepository = apiKeyRepository;
        this.apiKeyMapper = apiKeyMapper;
        this.apiKeySearchRepository = apiKeySearchRepository;
    }

    /**
     * Return a {@link List} of {@link ApiKeyDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ApiKeyDTO> findByCriteria(ApiKeyCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<ApiKey> specification = createSpecification(criteria);
        return apiKeyMapper.toDto(apiKeyRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ApiKeyCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ApiKey> specification = createSpecification(criteria);
        return apiKeyRepository.count(specification);
    }

    /**
     * Function to convert {@link ApiKeyCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ApiKey> createSpecification(ApiKeyCriteria criteria) {
        Specification<ApiKey> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), ApiKey_.id),
                buildStringSpecification(criteria.getName(), ApiKey_.name),
                buildStringSpecification(criteria.getDescription(), ApiKey_.description),
                buildStringSpecification(criteria.getKeyHash(), ApiKey_.keyHash),
                buildSpecification(criteria.getActive(), ApiKey_.active),
                buildRangeSpecification(criteria.getLastUsedDate(), ApiKey_.lastUsedDate),
                buildRangeSpecification(criteria.getExpiresAt(), ApiKey_.expiresAt),
                buildStringSpecification(criteria.getCreatedBy(), ApiKey_.createdBy),
                buildRangeSpecification(criteria.getCreatedDate(), ApiKey_.createdDate),
                buildStringSpecification(criteria.getLastModifiedBy(), ApiKey_.lastModifiedBy),
                buildRangeSpecification(criteria.getLastModifiedDate(), ApiKey_.lastModifiedDate)
            );
        }
        return specification;
    }
}
