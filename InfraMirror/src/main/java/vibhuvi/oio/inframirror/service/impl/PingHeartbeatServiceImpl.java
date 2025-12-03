package vibhuvi.oio.inframirror.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.domain.PingHeartbeat;
import vibhuvi.oio.inframirror.repository.PingHeartbeatRepository;
import vibhuvi.oio.inframirror.repository.search.PingHeartbeatSearchRepository;
import vibhuvi.oio.inframirror.service.PingHeartbeatService;
import vibhuvi.oio.inframirror.service.dto.PingHeartbeatDTO;
import vibhuvi.oio.inframirror.service.mapper.PingHeartbeatMapper;

/**
 * Service Implementation for managing {@link vibhuvi.oio.inframirror.domain.PingHeartbeat}.
 */
@Service
@Transactional
public class PingHeartbeatServiceImpl implements PingHeartbeatService {

    private static final Logger LOG = LoggerFactory.getLogger(PingHeartbeatServiceImpl.class);

    private final PingHeartbeatRepository pingHeartbeatRepository;

    private final PingHeartbeatMapper pingHeartbeatMapper;

    private final PingHeartbeatSearchRepository pingHeartbeatSearchRepository;

    public PingHeartbeatServiceImpl(
        PingHeartbeatRepository pingHeartbeatRepository,
        PingHeartbeatMapper pingHeartbeatMapper,
        PingHeartbeatSearchRepository pingHeartbeatSearchRepository
    ) {
        this.pingHeartbeatRepository = pingHeartbeatRepository;
        this.pingHeartbeatMapper = pingHeartbeatMapper;
        this.pingHeartbeatSearchRepository = pingHeartbeatSearchRepository;
    }

    @Override
    public PingHeartbeatDTO save(PingHeartbeatDTO pingHeartbeatDTO) {
        LOG.debug("Request to save PingHeartbeat : {}", pingHeartbeatDTO);
        PingHeartbeat pingHeartbeat = pingHeartbeatMapper.toEntity(pingHeartbeatDTO);
        pingHeartbeat = pingHeartbeatRepository.save(pingHeartbeat);
        pingHeartbeatSearchRepository.index(pingHeartbeat);
        return pingHeartbeatMapper.toDto(pingHeartbeat);
    }

    @Override
    public PingHeartbeatDTO update(PingHeartbeatDTO pingHeartbeatDTO) {
        LOG.debug("Request to update PingHeartbeat : {}", pingHeartbeatDTO);
        PingHeartbeat pingHeartbeat = pingHeartbeatMapper.toEntity(pingHeartbeatDTO);
        pingHeartbeat = pingHeartbeatRepository.save(pingHeartbeat);
        pingHeartbeatSearchRepository.index(pingHeartbeat);
        return pingHeartbeatMapper.toDto(pingHeartbeat);
    }

    @Override
    public Optional<PingHeartbeatDTO> partialUpdate(PingHeartbeatDTO pingHeartbeatDTO) {
        LOG.debug("Request to partially update PingHeartbeat : {}", pingHeartbeatDTO);

        return pingHeartbeatRepository
            .findById(pingHeartbeatDTO.getId())
            .map(existingPingHeartbeat -> {
                pingHeartbeatMapper.partialUpdate(existingPingHeartbeat, pingHeartbeatDTO);

                return existingPingHeartbeat;
            })
            .map(pingHeartbeatRepository::save)
            .map(savedPingHeartbeat -> {
                pingHeartbeatSearchRepository.index(savedPingHeartbeat);
                return savedPingHeartbeat;
            })
            .map(pingHeartbeatMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PingHeartbeatDTO> findOne(Long id) {
        LOG.debug("Request to get PingHeartbeat : {}", id);
        return pingHeartbeatRepository.findById(id).map(pingHeartbeatMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete PingHeartbeat : {}", id);
        pingHeartbeatRepository.deleteById(id);
        pingHeartbeatSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PingHeartbeatDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of PingHeartbeats for query {}", query);
        return pingHeartbeatSearchRepository.search(query, pageable).map(pingHeartbeatMapper::toDto);
    }
}
