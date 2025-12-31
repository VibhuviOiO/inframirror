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
import vibhuvi.oio.inframirror.domain.StatusPage;
import vibhuvi.oio.inframirror.repository.StatusPageRepository;
import vibhuvi.oio.inframirror.service.criteria.StatusPageCriteria;
import vibhuvi.oio.inframirror.service.dto.StatusPageDTO;
import vibhuvi.oio.inframirror.service.mapper.StatusPageMapper;

/**
 * Service for executing complex queries for {@link StatusPage} entities in the database.
 * The main input is a {@link StatusPageCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link StatusPageDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class StatusPageQueryService extends QueryService<StatusPage> {

    private static final Logger LOG = LoggerFactory.getLogger(StatusPageQueryService.class);

    private final StatusPageRepository statusPageRepository;

    private final StatusPageMapper statusPageMapper;

    public StatusPageQueryService(
        StatusPageRepository statusPageRepository,
        StatusPageMapper statusPageMapper
    ) {
        this.statusPageRepository = statusPageRepository;
        this.statusPageMapper = statusPageMapper;
    }

    /**
     * Return a {@link Page} of {@link StatusPageDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<StatusPageDTO> findByCriteria(StatusPageCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<StatusPage> specification = createSpecification(criteria);
        Page<StatusPage> result = statusPageRepository.findAll(specification, page);
        
        // Fetch item counts in a single query
        if (!result.isEmpty()) {
            java.util.List<Long> ids = result.getContent().stream().map(StatusPage::getId).toList();
            java.util.Map<Long, Long> itemCounts = statusPageRepository.findItemCountsByStatusPageIds(ids)
                .stream()
                .collect(java.util.stream.Collectors.toMap(
                    arr -> ((Number) arr[0]).longValue(),
                    arr -> ((Number) arr[1]).longValue()
                ));
            
            return result.map(entity -> {
                StatusPageDTO dto = statusPageMapper.toDto(entity);
                dto.setItemCount(itemCounts.getOrDefault(entity.getId(), 0L).intValue());
                return dto;
            });
        }
        
        return result.map(statusPageMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(StatusPageCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<StatusPage> specification = createSpecification(criteria);
        return statusPageRepository.count(specification);
    }

    /**
     * Function to convert {@link StatusPageCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<StatusPage> createSpecification(StatusPageCriteria criteria) {
        Specification<StatusPage> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), StatusPage_.id),
                buildStringSpecification(criteria.getName(), StatusPage_.name),
                buildStringSpecification(criteria.getSlug(), StatusPage_.slug),
                buildStringSpecification(criteria.getDescription(), StatusPage_.description),
                buildSpecification(criteria.getIsPublic(), StatusPage_.isPublic),
                buildSpecification(criteria.getIsActive(), StatusPage_.isActive),
                buildSpecification(criteria.getIsHomePage(), StatusPage_.isHomePage),
                buildRangeSpecification(criteria.getCreatedAt(), StatusPage_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), StatusPage_.updatedAt),
                buildSpecification(criteria.getItemId(), root -> root.join(StatusPage_.items, JoinType.LEFT).get(StatusPageItem_.id)),
                buildSpecification(criteria.getStatusDependencyId(), root ->
                    root.join(StatusPage_.statusDependencies, JoinType.LEFT).get(StatusDependency_.id)
                )
            );
        }
        return specification;
    }
}
