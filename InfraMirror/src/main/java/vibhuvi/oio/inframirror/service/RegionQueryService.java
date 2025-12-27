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
import vibhuvi.oio.inframirror.domain.Region;
import vibhuvi.oio.inframirror.repository.RegionRepository;
import vibhuvi.oio.inframirror.service.criteria.RegionCriteria;
import vibhuvi.oio.inframirror.service.dto.RegionDTO;
import vibhuvi.oio.inframirror.service.mapper.RegionMapper;

/**
 * Service for executing complex queries for {@link Region} entities in the database.
 * The main input is a {@link RegionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link RegionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class RegionQueryService extends QueryService<Region> {

    private static final Logger LOG = LoggerFactory.getLogger(RegionQueryService.class);

    private final RegionRepository regionRepository;

    private final RegionMapper regionMapper;

    public RegionQueryService(RegionRepository regionRepository, RegionMapper regionMapper) {
        this.regionRepository = regionRepository;
        this.regionMapper = regionMapper;
    }

    /**
     * Return a {@link Page} of {@link RegionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<RegionDTO> findByCriteria(RegionCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Region> specification = createSpecification(criteria);
        return regionRepository.findAll(specification, page).map(regionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(RegionCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Region> specification = createSpecification(criteria);
        return regionRepository.count(specification);
    }

    /**
     * Function to convert {@link RegionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Region> createSpecification(RegionCriteria criteria) {
        Specification<Region> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Region_.id),
                buildStringSpecification(criteria.getName(), Region_.name),
                buildStringSpecification(criteria.getRegionCode(), Region_.regionCode),
                buildStringSpecification(criteria.getGroupName(), Region_.groupName),
                buildSpecification(criteria.getDatacenterId(), root -> root.join(Region_.datacenters, JoinType.LEFT).get(Datacenter_.id)),
                buildSpecification(criteria.getAgentId(), root -> root.join(Region_.agents, JoinType.LEFT).get(Agent_.id))
            );
        }
        return specification;
    }
}
