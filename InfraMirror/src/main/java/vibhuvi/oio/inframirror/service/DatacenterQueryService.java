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
import vibhuvi.oio.inframirror.domain.Datacenter;
import vibhuvi.oio.inframirror.repository.DatacenterRepository;
import vibhuvi.oio.inframirror.repository.search.DatacenterSearchRepository;
import vibhuvi.oio.inframirror.service.criteria.DatacenterCriteria;
import vibhuvi.oio.inframirror.service.dto.DatacenterDTO;
import vibhuvi.oio.inframirror.service.mapper.DatacenterMapper;

/**
 * Service for executing complex queries for {@link Datacenter} entities in the database.
 * The main input is a {@link DatacenterCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link DatacenterDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DatacenterQueryService extends QueryService<Datacenter> {

    private static final Logger LOG = LoggerFactory.getLogger(DatacenterQueryService.class);

    private final DatacenterRepository datacenterRepository;

    private final DatacenterMapper datacenterMapper;

    private final DatacenterSearchRepository datacenterSearchRepository;

    public DatacenterQueryService(
        DatacenterRepository datacenterRepository,
        DatacenterMapper datacenterMapper,
        DatacenterSearchRepository datacenterSearchRepository
    ) {
        this.datacenterRepository = datacenterRepository;
        this.datacenterMapper = datacenterMapper;
        this.datacenterSearchRepository = datacenterSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link DatacenterDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DatacenterDTO> findByCriteria(DatacenterCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Datacenter> specification = createSpecification(criteria);
        return datacenterRepository.findAll(specification, page).map(datacenterMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DatacenterCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Datacenter> specification = createSpecification(criteria);
        return datacenterRepository.count(specification);
    }

    /**
     * Function to convert {@link DatacenterCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Datacenter> createSpecification(DatacenterCriteria criteria) {
        Specification<Datacenter> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Datacenter_.id),
                buildStringSpecification(criteria.getCode(), Datacenter_.code),
                buildStringSpecification(criteria.getName(), Datacenter_.name),
                buildSpecification(criteria.getAgentsId(), root -> root.join(Datacenter_.agents, JoinType.LEFT).get(Agent_.id)),
                buildSpecification(criteria.getInstancesId(), root -> root.join(Datacenter_.instances, JoinType.LEFT).get(Instance_.id)),
                buildSpecification(criteria.getRegionId(), root -> root.join(Datacenter_.region, JoinType.LEFT).get(Region_.id))
            );
        }
        return specification;
    }
}
