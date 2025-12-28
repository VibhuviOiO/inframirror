package vibhuvi.oio.inframirror.service.impl;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.domain.InstanceHeartbeat;
import vibhuvi.oio.inframirror.repository.InstanceHeartbeatRepository;
import vibhuvi.oio.inframirror.service.InstanceHeartbeatService;
import vibhuvi.oio.inframirror.service.dto.InstanceHeartbeatDTO;
import vibhuvi.oio.inframirror.service.mapper.InstanceHeartbeatMapper;
/**
 * Service Implementation for managing {@link vibhuvi.oio.inframirror.domain.InstanceHeartbeat}.
 */
@Service
@Transactional
public class InstanceHeartbeatServiceImpl implements InstanceHeartbeatService {
    private static final Logger LOG = LoggerFactory.getLogger(InstanceHeartbeatServiceImpl.class);
    private final InstanceHeartbeatRepository instanceHeartbeatRepository;
    private final InstanceHeartbeatMapper instanceHeartbeatMapper;
    public InstanceHeartbeatServiceImpl(
        InstanceHeartbeatRepository instanceHeartbeatRepository,
        InstanceHeartbeatMapper instanceHeartbeatMapper
    ) {
        this.instanceHeartbeatRepository = instanceHeartbeatRepository;
        this.instanceHeartbeatMapper = instanceHeartbeatMapper;
    }
    @Override
    public InstanceHeartbeatDTO save(InstanceHeartbeatDTO instanceHeartbeatDTO) {
        LOG.debug("Request to save InstanceHeartbeat : {}", instanceHeartbeatDTO);
        InstanceHeartbeat instanceHeartbeat = instanceHeartbeatMapper.toEntity(instanceHeartbeatDTO);
        instanceHeartbeat = instanceHeartbeatRepository.save(instanceHeartbeat);
        return instanceHeartbeatMapper.toDto(instanceHeartbeat);
    }
    @Override
    public InstanceHeartbeatDTO update(InstanceHeartbeatDTO instanceHeartbeatDTO) {
        LOG.debug("Request to update InstanceHeartbeat : {}", instanceHeartbeatDTO);
        InstanceHeartbeat instanceHeartbeat = instanceHeartbeatMapper.toEntity(instanceHeartbeatDTO);
        instanceHeartbeat = instanceHeartbeatRepository.save(instanceHeartbeat);
        return instanceHeartbeatMapper.toDto(instanceHeartbeat);
    }
    @Override
    public Optional<InstanceHeartbeatDTO> partialUpdate(InstanceHeartbeatDTO instanceHeartbeatDTO) {
        LOG.debug("Request to partially update InstanceHeartbeat : {}", instanceHeartbeatDTO);
        return instanceHeartbeatRepository

            .findById(instanceHeartbeatDTO.getId())
            .map(existingInstanceHeartbeat -> {
                instanceHeartbeatMapper.partialUpdate(existingInstanceHeartbeat, instanceHeartbeatDTO);
                return existingInstanceHeartbeat;
            })
            .map(instanceHeartbeatRepository::save)
            .map(savedInstanceHeartbeat -> {
                return savedInstanceHeartbeat;
            })
            .map(instanceHeartbeatMapper::toDto);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<InstanceHeartbeatDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all InstanceHeartbeats");
        return instanceHeartbeatRepository.findAll(pageable).map(instanceHeartbeatMapper::toDto);
    }
    @Override
    @Transactional(readOnly = true)
    public Optional<InstanceHeartbeatDTO> findOne(Long id) {
        LOG.debug("Request to get InstanceHeartbeat : {}", id);
        return instanceHeartbeatRepository.findById(id).map(instanceHeartbeatMapper::toDto);
    }
    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete InstanceHeartbeat : {}", id);
        instanceHeartbeatRepository.deleteById(id);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<InstanceHeartbeatDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of InstanceHeartbeats for query {}", query);
        return instanceHeartbeatRepository.findAll(pageable).map(instanceHeartbeatMapper::toDto);
    }
}
