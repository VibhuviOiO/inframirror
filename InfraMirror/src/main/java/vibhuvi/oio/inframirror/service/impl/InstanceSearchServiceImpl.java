package vibhuvi.oio.inframirror.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.domain.Instance;
import vibhuvi.oio.inframirror.repository.InstanceRepository;
import vibhuvi.oio.inframirror.repository.base.SearchableRepository;
import vibhuvi.oio.inframirror.service.InstanceSearchService;
import vibhuvi.oio.inframirror.service.base.AbstractSearchService;
import vibhuvi.oio.inframirror.service.dto.InstanceDTO;
import vibhuvi.oio.inframirror.service.dto.InstanceSearchResultDTO;
import vibhuvi.oio.inframirror.service.mapper.InstanceMapper;

/**
 * Service for searching instances.
 * Provides full-text, prefix, and fuzzy search capabilities.
 */
@Service
@Transactional(readOnly = true)
public class InstanceSearchServiceImpl extends AbstractSearchService<InstanceDTO, Instance> implements InstanceSearchService {

    private static final Logger LOG = LoggerFactory.getLogger(InstanceSearchServiceImpl.class);
    private final InstanceRepository instanceRepository;
    private final InstanceMapper instanceMapper;

    public InstanceSearchServiceImpl(InstanceRepository instanceRepository, InstanceMapper instanceMapper) {
        this.instanceRepository = instanceRepository;
        this.instanceMapper = instanceMapper;
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    protected SearchableRepository<Instance> getRepository() {
        return instanceRepository;
    }

    @Override
    protected InstanceMapper getMapper() {
        return instanceMapper;
    }

    @Override
    protected String getEntityName() {
        return "instance";
    }

    @Override
    public Page<InstanceSearchResultDTO> searchWithHighlight(String query, Pageable pageable) {
        validateSearchParams(query, pageable);
        LOG.debug("Request to search Instances with highlight for query {}", query);
        
        return instanceRepository
            .searchWithHighlight(query, pageable)
            .map(row -> {
                Long id = ((Number) row[0]).longValue();
                String name = (String) row[1];
                String hostname = (String) row[2];
                String description = (String) row[3];
                String privateIpAddress = (String) row[4];
                String publicIpAddress = (String) row[5];
                Float rank = ((Number) row[6]).floatValue();
                String highlight = (String) row[7];
                return new InstanceSearchResultDTO(id, name, hostname, description, privateIpAddress, publicIpAddress, rank, highlight);
            });
    }
}
