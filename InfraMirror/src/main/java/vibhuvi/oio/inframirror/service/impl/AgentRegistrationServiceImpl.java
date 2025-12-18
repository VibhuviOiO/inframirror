package vibhuvi.oio.inframirror.service.impl;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.domain.Agent;
import vibhuvi.oio.inframirror.domain.Datacenter;
import vibhuvi.oio.inframirror.domain.Region;
import vibhuvi.oio.inframirror.repository.AgentRepository;
import vibhuvi.oio.inframirror.repository.DatacenterRepository;
import vibhuvi.oio.inframirror.repository.RegionRepository;
import vibhuvi.oio.inframirror.service.AgentRegistrationService;
import vibhuvi.oio.inframirror.service.dto.AgentRegistrationRequestDTO;
import vibhuvi.oio.inframirror.service.dto.AgentRegistrationResponseDTO;
import vibhuvi.oio.inframirror.service.dto.DatacenterDTO;
import vibhuvi.oio.inframirror.service.dto.RegionDTO;
import vibhuvi.oio.inframirror.service.mapper.DatacenterMapper;
import vibhuvi.oio.inframirror.service.mapper.RegionMapper;

/**
 * Service Implementation for agent self-registration.
 */
@Service
@Transactional
public class AgentRegistrationServiceImpl implements AgentRegistrationService {

    private static final Logger LOG = LoggerFactory.getLogger(AgentRegistrationServiceImpl.class);

    private final AgentRepository agentRepository;
    private final RegionRepository regionRepository;
    private final DatacenterRepository datacenterRepository;
    private final RegionMapper regionMapper;
    private final DatacenterMapper datacenterMapper;

    public AgentRegistrationServiceImpl(
        AgentRepository agentRepository,
        RegionRepository regionRepository,
        DatacenterRepository datacenterRepository,
        RegionMapper regionMapper,
        DatacenterMapper datacenterMapper
    ) {
        this.agentRepository = agentRepository;
        this.regionRepository = regionRepository;
        this.datacenterRepository = datacenterRepository;
        this.regionMapper = regionMapper;
        this.datacenterMapper = datacenterMapper;
    }

    @Override
    public AgentRegistrationResponseDTO registerAgent(AgentRegistrationRequestDTO request) {
        LOG.debug("Request to register agent: {}", request.getName());

        // Extract region and datacenter from tags
        String regionName = request.getTags() != null ? request.getTags().get("region") : null;
        String datacenterName = request.getTags() != null ? request.getTags().get("datacenter") : null;

        if (regionName == null || datacenterName == null) {
            throw new IllegalArgumentException("Region and datacenter tags are required");
        }

        // Find or create Region
        Region region = regionRepository
            .findByName(regionName)
            .orElseGet(() -> {
                LOG.debug("Creating new region: {}", regionName);
                Region newRegion = new Region();
                newRegion.setName(regionName);
                return regionRepository.save(newRegion);
            });

        // Find or create Datacenter
        Datacenter datacenter = datacenterRepository
            .findByNameAndRegionId(datacenterName, region.getId())
            .orElseGet(() -> {
                LOG.debug("Creating new datacenter: {} in region: {}", datacenterName, regionName);
                Datacenter newDatacenter = new Datacenter();
                newDatacenter.setName(datacenterName);
                newDatacenter.setRegion(region);
                return datacenterRepository.save(newDatacenter);
            });

        // Create Agent
        Agent agent = new Agent();
        agent.setName(request.getName());
        agent.setRegion(region);
        agent = agentRepository.save(agent);

        // Generate API key
        String apiKey = "agent-" + UUID.randomUUID().toString();

        // Build response
        AgentRegistrationResponseDTO response = new AgentRegistrationResponseDTO();
        response.setAgentId(agent.getId());
        response.setApiKey(apiKey);
        response.setRegion(regionMapper.toDto(region));
        response.setDatacenter(datacenterMapper.toDto(datacenter));
        response.setStatus("REGISTERED");
        response.setMessage("Agent registered successfully");

        LOG.debug("Agent registered with ID: {}", agent.getId());
        return response;
    }
}
