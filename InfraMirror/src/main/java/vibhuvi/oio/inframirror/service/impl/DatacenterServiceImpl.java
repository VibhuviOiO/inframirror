package vibhuvi.oio.inframirror.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.domain.Datacenter;
import vibhuvi.oio.inframirror.repository.DatacenterRepository;
import vibhuvi.oio.inframirror.service.DatacenterService;
import vibhuvi.oio.inframirror.service.base.AbstractCrudService;
import vibhuvi.oio.inframirror.service.dto.DatacenterDTO;
import vibhuvi.oio.inframirror.service.mapper.DatacenterMapper;

/**
 * Service Implementation for managing {@link Datacenter}.
 */
/**
 * Service for managing datacenters.
 * Provides CRUD operations following clean architecture principles.
 */
@Service
@Transactional
public class DatacenterServiceImpl extends AbstractCrudService<DatacenterDTO, Datacenter> implements DatacenterService {

    private static final Logger LOG = LoggerFactory.getLogger(DatacenterServiceImpl.class);

    private final DatacenterRepository datacenterRepository;
    private final DatacenterMapper datacenterMapper;

    public DatacenterServiceImpl(DatacenterRepository datacenterRepository, DatacenterMapper datacenterMapper) {
        this.datacenterRepository = datacenterRepository;
        this.datacenterMapper = datacenterMapper;
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    protected JpaRepository<Datacenter, Long> getRepository() {
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
    public Optional<DatacenterDTO> partialUpdate(DatacenterDTO datacenterDTO) {
        return partialUpdate(datacenterDTO, datacenterDTO.getId());
    }
}
