package vibhuvi.oio.inframirror.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.domain.Region;
import vibhuvi.oio.inframirror.repository.RegionRepository;
import vibhuvi.oio.inframirror.service.RegionService;
import vibhuvi.oio.inframirror.service.base.AbstractCrudService;
import vibhuvi.oio.inframirror.service.dto.RegionDTO;
import vibhuvi.oio.inframirror.service.mapper.EntityMapper;
import vibhuvi.oio.inframirror.service.mapper.RegionMapper;

/**
 * Service Implementation for managing {@link vibhuvi.oio.inframirror.domain.Region}.
 * Extends AbstractCrudService to eliminate code duplication.
 */
@Service
@Transactional
public class RegionServiceImpl extends AbstractCrudService<RegionDTO, Region> implements RegionService {

    private static final Logger LOG = LoggerFactory.getLogger(RegionServiceImpl.class);

    private final RegionRepository regionRepository;
    private final RegionMapper regionMapper;

    public RegionServiceImpl(RegionRepository regionRepository, RegionMapper regionMapper) {
        this.regionRepository = regionRepository;
        this.regionMapper = regionMapper;
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    protected JpaRepository<Region, Long> getRepository() {
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
    public java.util.Optional<RegionDTO> partialUpdate(RegionDTO regionDTO) {
        return partialUpdate(regionDTO, regionDTO.getId());
    }
}
