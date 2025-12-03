package vibhuvi.oio.inframirror.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.domain.Datacenter;
import vibhuvi.oio.inframirror.repository.DatacenterRepository;
import vibhuvi.oio.inframirror.repository.search.DatacenterSearchRepository;
import vibhuvi.oio.inframirror.service.DatacenterService;
import vibhuvi.oio.inframirror.service.dto.DatacenterDTO;
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

    private final DatacenterSearchRepository datacenterSearchRepository;

    public DatacenterServiceImpl(
        DatacenterRepository datacenterRepository,
        DatacenterMapper datacenterMapper,
        DatacenterSearchRepository datacenterSearchRepository
    ) {
        this.datacenterRepository = datacenterRepository;
        this.datacenterMapper = datacenterMapper;
        this.datacenterSearchRepository = datacenterSearchRepository;
    }

    @Override
    public DatacenterDTO save(DatacenterDTO datacenterDTO) {
        LOG.debug("Request to save Datacenter : {}", datacenterDTO);
        Datacenter datacenter = datacenterMapper.toEntity(datacenterDTO);
        datacenter = datacenterRepository.save(datacenter);
        datacenterSearchRepository.index(datacenter);
        return datacenterMapper.toDto(datacenter);
    }

    @Override
    public DatacenterDTO update(DatacenterDTO datacenterDTO) {
        LOG.debug("Request to update Datacenter : {}", datacenterDTO);
        Datacenter datacenter = datacenterMapper.toEntity(datacenterDTO);
        datacenter = datacenterRepository.save(datacenter);
        datacenterSearchRepository.index(datacenter);
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
            .map(savedDatacenter -> {
                datacenterSearchRepository.index(savedDatacenter);
                return savedDatacenter;
            })
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
        datacenterSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DatacenterDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Datacenters for query {}", query);
        return datacenterSearchRepository.search(query, pageable).map(datacenterMapper::toDto);
    }
}
