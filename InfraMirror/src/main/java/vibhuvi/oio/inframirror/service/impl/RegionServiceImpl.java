package vibhuvi.oio.inframirror.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.domain.Region;
import vibhuvi.oio.inframirror.repository.RegionRepository;
import vibhuvi.oio.inframirror.service.FullTextSearchUtil;
import vibhuvi.oio.inframirror.service.RegionService;
import vibhuvi.oio.inframirror.service.dto.RegionDTO;
import vibhuvi.oio.inframirror.service.dto.RegionSearchResultDTO;
import vibhuvi.oio.inframirror.service.mapper.RegionMapper;

/**
 * Service Implementation for managing {@link vibhuvi.oio.inframirror.domain.Region}.
 */
@Service
@Transactional
public class RegionServiceImpl implements RegionService {

    private static final Logger LOG = LoggerFactory.getLogger(RegionServiceImpl.class);

    private final RegionRepository regionRepository;
    private final RegionMapper regionMapper;

    public RegionServiceImpl(RegionRepository regionRepository, RegionMapper regionMapper) {
        this.regionRepository = regionRepository;
        this.regionMapper = regionMapper;
    }

    @Override
    public RegionDTO save(RegionDTO regionDTO) {
        LOG.debug("Request to save Region : {}", regionDTO);
        Region region = regionMapper.toEntity(regionDTO);
        region = regionRepository.save(region);
        return regionMapper.toDto(region);
    }

    @Override
    public RegionDTO update(RegionDTO regionDTO) {
        LOG.debug("Request to update Region : {}", regionDTO);
        Region region = regionMapper.toEntity(regionDTO);
        region = regionRepository.save(region);
        return regionMapper.toDto(region);
    }

    @Override
    public Optional<RegionDTO> partialUpdate(RegionDTO regionDTO) {
        LOG.debug("Request to partially update Region : {}", regionDTO);

        return regionRepository
            .findById(regionDTO.getId())
            .map(existingRegion -> {
                regionMapper.partialUpdate(existingRegion, regionDTO);
                return existingRegion;
            })
            .map(regionRepository::save)
            .map(regionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RegionDTO> findOne(Long id) {
        LOG.debug("Request to get Region : {}", id);
        return regionRepository.findById(id).map(regionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Region : {}", id);
        regionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RegionDTO> search(String query, Pageable pageable) {
        if (query != null && query.length() > 100) {
            throw new IllegalArgumentException("Search query too long (max 100 characters)");
        }
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
        LOG.debug("Request to search Regions : {}", query);

        if (FullTextSearchUtil.isEmptyQuery(query)) {
            return regionRepository.findAll(pageable).map(regionMapper::toDto);
        }

        String searchTerm = FullTextSearchUtil.sanitizeQuery(query);
        Pageable limitedPageable = FullTextSearchUtil.createLimitedPageable(pageable);

        return regionRepository.searchFullText(searchTerm, limitedPageable).map(regionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RegionDTO> searchPrefix(String query, Pageable pageable) {
        if (query != null && query.length() > 100) {
            throw new IllegalArgumentException("Search query too long (max 100 characters)");
        }
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
        LOG.debug("Request to prefix search Regions : {}", query);

        if (FullTextSearchUtil.isEmptyQuery(query)) {
            return Page.empty(pageable);
        }

        String normalizedQuery = FullTextSearchUtil.normalizeQuery(query);
        Pageable limitedPageable = FullTextSearchUtil.createLimitedPageable(pageable);

        return regionRepository.searchPrefix(normalizedQuery, limitedPageable).map(regionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RegionDTO> searchFuzzy(String query, Pageable pageable) {
        if (query != null && query.length() > 100) {
            throw new IllegalArgumentException("Search query too long (max 100 characters)");
        }
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
        LOG.debug("Request to fuzzy search Regions : {}", query);

        if (FullTextSearchUtil.isEmptyQuery(query)) {
            return Page.empty(pageable);
        }

        String normalizedQuery = FullTextSearchUtil.normalizeQuery(query);
        Pageable limitedPageable = FullTextSearchUtil.createLimitedPageable(pageable);

        return regionRepository.searchFuzzy(normalizedQuery, limitedPageable).map(regionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RegionSearchResultDTO> searchWithHighlight(String query, Pageable pageable) {
        if (query != null && query.length() > 100) {
            throw new IllegalArgumentException("Search query too long (max 100 characters)");
        }
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
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
