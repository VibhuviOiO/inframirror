package vibhuvi.oio.inframirror.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.domain.Agent;
import vibhuvi.oio.inframirror.domain.Datacenter;
import vibhuvi.oio.inframirror.domain.Region;
import vibhuvi.oio.inframirror.repository.AgentRepository;
import vibhuvi.oio.inframirror.repository.DatacenterRepository;
import vibhuvi.oio.inframirror.repository.RegionRepository;
import vibhuvi.oio.inframirror.repository.search.AgentSearchRepository;
import vibhuvi.oio.inframirror.service.AgentService;
import vibhuvi.oio.inframirror.service.dto.AgentDTO;
import vibhuvi.oio.inframirror.service.dto.AgentRegistrationRequestDTO;
import vibhuvi.oio.inframirror.service.dto.AgentRegistrationResponseDTO;
import vibhuvi.oio.inframirror.service.mapper.AgentMapper;
import vibhuvi.oio.inframirror.service.mapper.DatacenterMapper;
import vibhuvi.oio.inframirror.service.mapper.RegionMapper;

/**
 * Service Implementation for managing {@link vibhuvi.oio.inframirror.domain.Agent}.
 */
@Service
@Transactional
public class AgentServiceImpl implements AgentService {

    private static final Logger LOG = LoggerFactory.getLogger(AgentServiceImpl.class);

    private final AgentRepository agentRepository;
    private final AgentMapper agentMapper;
    private final AgentSearchRepository agentSearchRepository;
    private final RegionRepository regionRepository;
    private final DatacenterRepository datacenterRepository;
    private final RegionMapper regionMapper;
    private final DatacenterMapper datacenterMapper;
    private final ObjectMapper objectMapper;

    public AgentServiceImpl(
        AgentRepository agentRepository,
        AgentMapper agentMapper,
        AgentSearchRepository agentSearchRepository,
        RegionRepository regionRepository,
        DatacenterRepository datacenterRepository,
        RegionMapper regionMapper,
        DatacenterMapper datacenterMapper,
        ObjectMapper objectMapper
    ) {
        this.agentRepository = agentRepository;
        this.agentMapper = agentMapper;
        this.agentSearchRepository = agentSearchRepository;
        this.regionRepository = regionRepository;
        this.datacenterRepository = datacenterRepository;
        this.regionMapper = regionMapper;
        this.datacenterMapper = datacenterMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public AgentDTO save(AgentDTO agentDTO) {
        LOG.debug("Request to save Agent : {}", agentDTO);
        Agent agent = agentMapper.toEntity(agentDTO);
        agent = agentRepository.save(agent);
        agentSearchRepository.index(agent);
        return agentMapper.toDto(agent);
    }

    @Override
    public AgentDTO update(AgentDTO agentDTO) {
        LOG.debug("Request to update Agent : {}", agentDTO);
        Agent agent = agentMapper.toEntity(agentDTO);
        agent = agentRepository.save(agent);
        agentSearchRepository.index(agent);
        return agentMapper.toDto(agent);
    }

    @Override
    public Optional<AgentDTO> partialUpdate(AgentDTO agentDTO) {
        LOG.debug("Request to partially update Agent : {}", agentDTO);

        return agentRepository
            .findById(agentDTO.getId())
            .map(existingAgent -> {
                agentMapper.partialUpdate(existingAgent, agentDTO);

                return existingAgent;
            })
            .map(agentRepository::save)
            .map(savedAgent -> {
                agentSearchRepository.index(savedAgent);
                return savedAgent;
            })
            .map(agentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AgentDTO> findOne(Long id) {
        LOG.debug("Request to get Agent : {}", id);
        return agentRepository.findById(id).map(agentMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Agent : {}", id);
        agentRepository.deleteById(id);
        agentSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AgentDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Agents for query {}", query);
        return agentSearchRepository.search(query, pageable).map(agentMapper::toDto);
    }

    @Override
    public AgentRegistrationResponseDTO registerAgent(AgentRegistrationRequestDTO request) {
        LOG.debug("Request to register agent: {}", request.getName());

        String regionName = request.getTags() != null ? request.getTags().get("region") : null;
        String datacenterName = request.getTags() != null ? request.getTags().get("datacenter") : null;

        if (regionName == null || datacenterName == null) {
            throw new IllegalArgumentException("Region and datacenter tags are required");
        }

        Region region = regionRepository
            .findByName(regionName)
            .orElseGet(() -> {
                Region newRegion = new Region();
                newRegion.setName(regionName);
                return regionRepository.save(newRegion);
            });

        Datacenter datacenter = datacenterRepository
            .findByNameAndRegionId(datacenterName, region.getId())
            .orElseGet(() -> {
                Datacenter newDatacenter = new Datacenter();
                newDatacenter.setName(datacenterName);
                newDatacenter.setCode(datacenterName.toLowerCase().replaceAll("\\s+", "-"));
                newDatacenter.setRegion(region);
                return datacenterRepository.save(newDatacenter);
            });

        Agent agent = new Agent();
        agent.setName(request.getName());
        agent.setHostname(request.getHostname());
        agent.setIpAddress(request.getIpAddress());
        agent.setOsType(request.getOsType());
        agent.setOsVersion(request.getOsVersion());
        agent.setAgentVersion(request.getAgentVersion());
        agent.setStatus("ACTIVE");
        agent.setTags(objectMapper.valueToTree(request.getTags()));
        agent.setDatacenter(datacenter);
        agent = agentRepository.save(agent);
        agentSearchRepository.index(agent);

        AgentRegistrationResponseDTO response = new AgentRegistrationResponseDTO();
        response.setAgentId(agent.getId());
        response.setApiKey("agent-" + UUID.randomUUID().toString());
        response.setRegion(regionMapper.toDto(region));
        response.setDatacenter(datacenterMapper.toDto(datacenter));
        response.setStatus("REGISTERED");
        response.setMessage("Agent registered successfully");

        return response;
    }
}
