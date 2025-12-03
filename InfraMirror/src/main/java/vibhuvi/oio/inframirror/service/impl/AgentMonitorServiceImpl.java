package vibhuvi.oio.inframirror.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.domain.AgentMonitor;
import vibhuvi.oio.inframirror.repository.AgentMonitorRepository;
import vibhuvi.oio.inframirror.repository.search.AgentMonitorSearchRepository;
import vibhuvi.oio.inframirror.service.AgentMonitorService;
import vibhuvi.oio.inframirror.service.dto.AgentMonitorDTO;
import vibhuvi.oio.inframirror.service.mapper.AgentMonitorMapper;

/**
 * Service Implementation for managing {@link vibhuvi.oio.inframirror.domain.AgentMonitor}.
 */
@Service
@Transactional
public class AgentMonitorServiceImpl implements AgentMonitorService {

    private static final Logger LOG = LoggerFactory.getLogger(AgentMonitorServiceImpl.class);

    private final AgentMonitorRepository agentMonitorRepository;

    private final AgentMonitorMapper agentMonitorMapper;

    private final AgentMonitorSearchRepository agentMonitorSearchRepository;

    public AgentMonitorServiceImpl(
        AgentMonitorRepository agentMonitorRepository,
        AgentMonitorMapper agentMonitorMapper,
        AgentMonitorSearchRepository agentMonitorSearchRepository
    ) {
        this.agentMonitorRepository = agentMonitorRepository;
        this.agentMonitorMapper = agentMonitorMapper;
        this.agentMonitorSearchRepository = agentMonitorSearchRepository;
    }

    @Override
    public AgentMonitorDTO save(AgentMonitorDTO agentMonitorDTO) {
        LOG.debug("Request to save AgentMonitor : {}", agentMonitorDTO);
        AgentMonitor agentMonitor = agentMonitorMapper.toEntity(agentMonitorDTO);
        agentMonitor = agentMonitorRepository.save(agentMonitor);
        agentMonitorSearchRepository.index(agentMonitor);
        return agentMonitorMapper.toDto(agentMonitor);
    }

    @Override
    public AgentMonitorDTO update(AgentMonitorDTO agentMonitorDTO) {
        LOG.debug("Request to update AgentMonitor : {}", agentMonitorDTO);
        AgentMonitor agentMonitor = agentMonitorMapper.toEntity(agentMonitorDTO);
        agentMonitor = agentMonitorRepository.save(agentMonitor);
        agentMonitorSearchRepository.index(agentMonitor);
        return agentMonitorMapper.toDto(agentMonitor);
    }

    @Override
    public Optional<AgentMonitorDTO> partialUpdate(AgentMonitorDTO agentMonitorDTO) {
        LOG.debug("Request to partially update AgentMonitor : {}", agentMonitorDTO);

        return agentMonitorRepository
            .findById(agentMonitorDTO.getId())
            .map(existingAgentMonitor -> {
                agentMonitorMapper.partialUpdate(existingAgentMonitor, agentMonitorDTO);

                return existingAgentMonitor;
            })
            .map(agentMonitorRepository::save)
            .map(savedAgentMonitor -> {
                agentMonitorSearchRepository.index(savedAgentMonitor);
                return savedAgentMonitor;
            })
            .map(agentMonitorMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgentMonitorDTO> findAll() {
        LOG.debug("Request to get all AgentMonitors");
        return agentMonitorRepository.findAll().stream().map(agentMonitorMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AgentMonitorDTO> findOne(Long id) {
        LOG.debug("Request to get AgentMonitor : {}", id);
        return agentMonitorRepository.findById(id).map(agentMonitorMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete AgentMonitor : {}", id);
        agentMonitorRepository.deleteById(id);
        agentMonitorSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgentMonitorDTO> search(String query) {
        LOG.debug("Request to search AgentMonitors for query {}", query);
        try {
            return StreamSupport.stream(agentMonitorSearchRepository.search(query).spliterator(), false)
                .map(agentMonitorMapper::toDto)
                .toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
