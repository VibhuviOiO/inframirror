package vibhuvi.oio.inframirror.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.domain.Region;
import vibhuvi.oio.inframirror.repository.RegionRepository;
import vibhuvi.oio.inframirror.repository.base.SearchableRepository;
import vibhuvi.oio.inframirror.service.FullTextSearchUtil;
import vibhuvi.oio.inframirror.service.RegionSearchService;
import vibhuvi.oio.inframirror.service.base.AbstractSearchService;
import vibhuvi.oio.inframirror.service.dto.RegionDTO;
import vibhuvi.oio.inframirror.service.dto.RegionSearchResultDTO;
import vibhuvi.oio.inframirror.service.mapper.EntityMapper;
import vibhuvi.oio.inframirror.service.mapper.RegionMapper;

/**
 * Service Implementation for searching {@link vibhuvi.oio.inframirror.domain.Region}.
 * Extends AbstractSearchService to eliminate code duplication.
 */
@Service
@Transactional(readOnly = true)
public class RegionSearchServiceImpl extends AbstractSearchService<RegionDTO, Region> implements RegionSearchService {

    private static final Logger LOG = LoggerFactory.getLogger(RegionSearchServiceImpl.class);

    private final RegionRepository regionRepository;
    private final RegionMapper regionMapper;

    public RegionSearchServiceImpl(RegionRepository regionRepository, RegionMapper regionMapper) {
        this.regionRepository = regionRepository;
        this.regionMapper = regionMapper;
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    protected SearchableRepository<Region> getRepository() {
        return regionRepository;
    }

    @Override
    protected EntityMapper<RegionDTO, Region> getMapper() {
        return regionMapper;
    }

    @Override
    protected String getEntityName() {
        return "Region";
    }

    @Override
    public Page<RegionSearchResultDTO> searchWithHighlight(String query, Pageable pageable) {
        validateSearchParams(query, pageable);
        LOG.debug("Request to search Regions with highlight : {}", query);

        if (FullTextSearchUtil.isEmptyQuery(query)) {
            return Page.empty(pageable);
        }

        String normalizedQuery = FullTextSearchUtil.normalizeQuery(query);
        Pageable limitedPageable = FullTextSearchUtil.createLimitedPageable(pageable);

        return regionRepository.searchWithHighlight(normalizedQuery, limitedPageable).map(row -> {
            Long id = ((Number) row[0]).longValue();
            String name = (String) row[1];
            String regionCode = (String) row[2];
            String groupName = (String) row[3];
            Float rank = ((Number) row[4]).floatValue();
            String highlight = (String) row[5];
            return new RegionSearchResultDTO(id, name, regionCode, groupName, rank, highlight);
        });
    }
}
