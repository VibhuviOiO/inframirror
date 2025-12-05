package vibhuvi.oio.inframirror.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.domain.AgentLock;
import vibhuvi.oio.inframirror.repository.AgentLockRepository;
import vibhuvi.oio.inframirror.repository.search.AgentLockSearchRepository;
import vibhuvi.oio.inframirror.service.AgentLockService;
import vibhuvi.oio.inframirror.service.dto.AgentLockDTO;
import vibhuvi.oio.inframirror.service.mapper.AgentLockMapper;

/**
 * Service Implementation for managing {@link vibhuvi.oio.inframirror.domain.AgentLock}.
 */
@Service
@Transactional
public class AgentLockServiceImpl implements AgentLockService {

    private static final Logger LOG = LoggerFactory.getLogger(AgentLockServiceImpl.class);

    private final AgentLockRepository agentLockRepository;

    private final AgentLockMapper agentLockMapper;

    private final AgentLockSearchRepository agentLockSearchRepository;

    public AgentLockServiceImpl(
        AgentLockRepository agentLockRepository,
        AgentLockMapper agentLockMapper,
        AgentLockSearchRepository agentLockSearchRepository
    ) {
        this.agentLockRepository = agentLockRepository;
        this.agentLockMapper = agentLockMapper;
        this.agentLockSearchRepository = agentLockSearchRepository;
    }

    @Override
    public AgentLockDTO save(AgentLockDTO agentLockDTO) {
        LOG.debug("Request to save AgentLock : {}", agentLockDTO);
        AgentLock agentLock = agentLockMapper.toEntity(agentLockDTO);
        agentLock = agentLockRepository.save(agentLock);
        agentLockSearchRepository.index(agentLock);
        return agentLockMapper.toDto(agentLock);
    }

    @Override
    public AgentLockDTO update(AgentLockDTO agentLockDTO) {
        LOG.debug("Request to update AgentLock : {}", agentLockDTO);
        AgentLock agentLock = agentLockMapper.toEntity(agentLockDTO);
        agentLock = agentLockRepository.save(agentLock);
        agentLockSearchRepository.index(agentLock);
        return agentLockMapper.toDto(agentLock);
    }

    @Override
    public Optional<AgentLockDTO> partialUpdate(AgentLockDTO agentLockDTO) {
        LOG.debug("Request to partially update AgentLock : {}", agentLockDTO);

        return agentLockRepository
            .findById(agentLockDTO.getId())
            .map(existingAgentLock -> {
                agentLockMapper.partialUpdate(existingAgentLock, agentLockDTO);

                return existingAgentLock;
            })
            .map(agentLockRepository::save)
            .map(savedAgentLock -> {
                agentLockSearchRepository.index(savedAgentLock);
                return savedAgentLock;
            })
            .map(agentLockMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AgentLockDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all AgentLocks");
        return agentLockRepository.findAll(pageable).map(agentLockMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AgentLockDTO> findOne(Long id) {
        LOG.debug("Request to get AgentLock : {}", id);
        return agentLockRepository.findById(id).map(agentLockMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete AgentLock : {}", id);
        agentLockRepository.deleteById(id);
        agentLockSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AgentLockDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of AgentLocks for query {}", query);
        return agentLockSearchRepository.search(query, pageable).map(agentLockMapper::toDto);
    }
}
