package vibhuvi.oio.inframirror.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.domain.Datacenter;
import vibhuvi.oio.inframirror.repository.DatacenterRepository;
import vibhuvi.oio.inframirror.repository.base.SearchableRepository;
import vibhuvi.oio.inframirror.service.DatacenterSearchService;
import vibhuvi.oio.inframirror.service.base.AbstractSearchService;
import vibhuvi.oio.inframirror.service.dto.DatacenterDTO;
import vibhuvi.oio.inframirror.service.dto.DatacenterSearchResultDTO;
import vibhuvi.oio.inframirror.service.mapper.DatacenterMapper;

/**
 * Service Implementation for searching {@link Datacenter}.
 */
/**
 * Service for searching datacenters.
 * Provides full-text, prefix, and fuzzy search capabilities.
 */
@Service
@Transactional(readOnly = true)
public class DatacenterSearchServiceImpl extends AbstractSearchService<DatacenterDTO, Datacenter> implements DatacenterSearchService {

    private static final Logger LOG = LoggerFactory.getLogger(DatacenterSearchServiceImpl.class);

    private final DatacenterRepository datacenterRepository;
    private final DatacenterMapper datacenterMapper;

    public DatacenterSearchServiceImpl(DatacenterRepository datacenterRepository, DatacenterMapper datacenterMapper) {
        this.datacenterRepository = datacenterRepository;
        this.datacenterMapper = datacenterMapper;
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    protected SearchableRepository<Datacenter> getRepository() {
        return datacenterRepository;
    }

    @Override
    protected DatacenterMapper getMapper() {
        return datacenterMapper;
    }

    @Override
    protected String getEntityName() {
        return "datacenter";
    }

    @Override
    public Page<DatacenterSearchResultDTO> searchWithHighlight(String query, Pageable pageable) {
        LOG.debug("Request to search Datacenters with highlight for query {}", query);
        return datacenterRepository
            .searchWithHighlight(query, pageable)
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
