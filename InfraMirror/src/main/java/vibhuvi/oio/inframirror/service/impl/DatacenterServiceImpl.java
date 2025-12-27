package vibhuvi.oio.inframirror.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.domain.Datacenter;
import vibhuvi.oio.inframirror.repository.DatacenterRepository;
import vibhuvi.oio.inframirror.service.DatacenterService;
import vibhuvi.oio.inframirror.service.FullTextSearchUtil;
import vibhuvi.oio.inframirror.service.dto.DatacenterDTO;
import vibhuvi.oio.inframirror.service.dto.DatacenterSearchResultDTO;
import vibhuvi.oio.inframirror.service.mapper.DatacenterMapper;

/**
 * Service Implementation for managing {@link vibhuvi.oio.inframirror.domain.Datacenter}.
 */
@Service
@Transactional
public class DatacenterServiceImpl implements DatacenterService {

    private static final Logger LOG = LoggerFactory.getLogger(DatacenterServiceImpl.class);

    private final DatacenterRepository datacenterRepository;

    private final DatacenterMapper datacenterMapper;

    public DatacenterServiceImpl(DatacenterRepository datacenterRepository, DatacenterMapper datacenterMapper) {
        this.datacenterRepository = datacenterRepository;
        this.datacenterMapper = datacenterMapper;
    }

    @Override
    public DatacenterDTO save(DatacenterDTO datacenterDTO) {
        LOG.debug("Request to save Datacenter : {}", datacenterDTO);
        Datacenter datacenter = datacenterMapper.toEntity(datacenterDTO);
        datacenter = datacenterRepository.save(datacenter);
        return datacenterMapper.toDto(datacenter);
    }

    @Override
    public DatacenterDTO update(DatacenterDTO datacenterDTO) {
        LOG.debug("Request to update Datacenter : {}", datacenterDTO);
        Datacenter datacenter = datacenterMapper.toEntity(datacenterDTO);
        datacenter = datacenterRepository.save(datacenter);
        return datacenterMapper.toDto(datacenter);
    }

    @Override
    public Optional<DatacenterDTO> partialUpdate(DatacenterDTO datacenterDTO) {
        LOG.debug("Request to partially update Datacenter : {}", datacenterDTO);

        return datacenterRepository
            .findById(datacenterDTO.getId())
            .map(existingDatacenter -> {
                datacenterMapper.partialUpdate(existingDatacenter, datacenterDTO);

                return existingDatacenter;
            })
            .map(datacenterRepository::save)
            .map(datacenterMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DatacenterDTO> findOne(Long id) {
        LOG.debug("Request to get Datacenter : {}", id);
        return datacenterRepository.findById(id).map(datacenterMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Datacenter : {}", id);
        datacenterRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DatacenterDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search Datacenters for query {}", query);
        if (FullTextSearchUtil.isEmptyQuery(query)) {
            return datacenterRepository.findAll(pageable).map(datacenterMapper::toDto);
        }

        String searchTerm = FullTextSearchUtil.sanitizeQuery(query);
        Pageable limitedPageable = FullTextSearchUtil.createLimitedPageable(pageable);

        return datacenterRepository.searchFullText(searchTerm, limitedPageable).map(datacenterMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DatacenterDTO> searchPrefix(String query, Pageable pageable) {
        LOG.debug("Request to prefix search Datacenters for query {}", query);
        if (FullTextSearchUtil.isEmptyQuery(query)) {
            return Page.empty(pageable);
        }

        String normalizedQuery = FullTextSearchUtil.normalizeQuery(query);
        Pageable limitedPageable = FullTextSearchUtil.createLimitedPageable(pageable);

        return datacenterRepository.searchPrefix(normalizedQuery, limitedPageable).map(datacenterMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DatacenterDTO> searchFuzzy(String query, Pageable pageable) {
        LOG.debug("Request to fuzzy search Datacenters for query {}", query);
        if (FullTextSearchUtil.isEmptyQuery(query)) {
            return Page.empty(pageable);
        }

        String normalizedQuery = FullTextSearchUtil.normalizeQuery(query);
        Pageable limitedPageable = FullTextSearchUtil.createLimitedPageable(pageable);

        return datacenterRepository.searchFuzzy(normalizedQuery, limitedPageable).map(datacenterMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DatacenterSearchResultDTO> searchWithHighlight(String query, Pageable pageable) {
        LOG.debug("Request to search Datacenters with highlight for query {}", query);
        if (FullTextSearchUtil.isEmptyQuery(query)) {
            return Page.empty(pageable);
        }

        String normalizedQuery = FullTextSearchUtil.normalizeQuery(query);
        Pageable limitedPageable = FullTextSearchUtil.createLimitedPageable(pageable);

        return datacenterRepository
            .searchWithHighlight(normalizedQuery, limitedPageable)
            .map(row -> {
                Long id = ((Number) row[0]).longValue();
                String name = (String) row[1];
                String code = (String) row[2];
                Float rank = ((Number) row[3]).floatValue();
                String highlight = (String) row[4];
                return new DatacenterSearchResultDTO(id, name, code, rank, highlight);
            });
    }
}
