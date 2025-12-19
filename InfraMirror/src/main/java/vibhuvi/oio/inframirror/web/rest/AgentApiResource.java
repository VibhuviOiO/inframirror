package vibhuvi.oio.inframirror.web.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;
import vibhuvi.oio.inframirror.web.rest.errors.BadRequestAlertException;
import vibhuvi.oio.inframirror.service.AgentService;
import vibhuvi.oio.inframirror.service.DatacenterService;
import vibhuvi.oio.inframirror.service.RegionService;
import vibhuvi.oio.inframirror.service.InstanceService;
import vibhuvi.oio.inframirror.service.HttpMonitorService;
import vibhuvi.oio.inframirror.service.MonitoredServiceService;
import vibhuvi.oio.inframirror.service.dto.*;

/**
 * REST controller for agent operations.
 * All endpoints require API key authentication.
 */
@RestController
@RequestMapping("/api/agent")
public class AgentApiResource {

    private static final Logger LOG = LoggerFactory.getLogger(AgentApiResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AgentService agentService;
    private final RegionService regionService;
    private final DatacenterService datacenterService;
    private final InstanceService instanceService;
    private final HttpMonitorService httpMonitorService;
    private final MonitoredServiceService monitoredServiceService;

    public AgentApiResource(
        AgentService agentService,
        RegionService regionService,
        DatacenterService datacenterService,
        InstanceService instanceService,
        HttpMonitorService httpMonitorService,
        MonitoredServiceService monitoredServiceService
    ) {
        this.agentService = agentService;
        this.regionService = regionService;
        this.datacenterService = datacenterService;
        this.instanceService = instanceService;
        this.httpMonitorService = httpMonitorService;
        this.monitoredServiceService = monitoredServiceService;
    }

    /**
     * Agent self-registration
     */
    @PostMapping("/register")
    public ResponseEntity<AgentRegistrationResponseDTO> registerAgent(@Valid @RequestBody AgentRegistrationRequestDTO request)
        throws URISyntaxException {
        LOG.debug("REST request to register Agent : {}", request);
        AgentRegistrationResponseDTO response = agentService.registerAgent(request);
        return ResponseEntity.created(new URI("/api/agents/" + response.getAgentId()))
            .headers(HeaderUtil.createAlert(applicationName, "Agent registered successfully", response.getAgentId().toString()))
            .body(response);
    }

    /**
     * Create region
     */
    @PostMapping("/regions")
    public ResponseEntity<RegionDTO> createRegion(@Valid @RequestBody RegionDTO regionDTO) throws URISyntaxException {
        LOG.debug("REST request to save Region : {}", regionDTO);
        RegionDTO result = regionService.save(regionDTO);
        return ResponseEntity.created(new URI("/api/regions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, "region", result.getId().toString()))
            .body(result);
    }

    /**
     * Create datacenter
     */
    @PostMapping("/datacenters")
    public ResponseEntity<DatacenterDTO> createDatacenter(@Valid @RequestBody DatacenterDTO datacenterDTO) throws URISyntaxException {
        LOG.debug("REST request to save Datacenter : {}", datacenterDTO);
        DatacenterDTO result = datacenterService.save(datacenterDTO);
        return ResponseEntity.created(new URI("/api/datacenters/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, "datacenter", result.getId().toString()))
            .body(result);
    }

    /**
     * Create instance
     */
    @PostMapping("/instances")
    public ResponseEntity<InstanceDTO> createInstance(@Valid @RequestBody InstanceDTO instanceDTO) throws URISyntaxException {
        LOG.debug("REST request to save Instance : {}", instanceDTO);
        
        if (instanceDTO.getId() != null) {
            throw new BadRequestAlertException("A new instance cannot already have an ID", "instance", "idexists");
        }
        
        InstanceDTO result = instanceService.save(instanceDTO);
        return ResponseEntity.created(new URI("/api/instances/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, "instance", result.getId().toString()))
            .body(result);
    }

    /**
     * Create HTTP monitor
     */
    @PostMapping("/http-monitors")
    public ResponseEntity<HttpMonitorDTO> createHttpMonitor(@Valid @RequestBody HttpMonitorDTO httpMonitorDTO) throws URISyntaxException {
        LOG.debug("REST request to save HttpMonitor : {}", httpMonitorDTO);
        HttpMonitorDTO result = httpMonitorService.save(httpMonitorDTO);
        return ResponseEntity.created(new URI("/api/http-monitors/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, "httpMonitor", result.getId().toString()))
            .body(result);
    }

    /**
     * Create monitored service
     */
    @PostMapping("/monitored-services")
    public ResponseEntity<MonitoredServiceDTO> createMonitoredService(@Valid @RequestBody MonitoredServiceDTO monitoredServiceDTO) throws URISyntaxException {
        LOG.debug("REST request to save MonitoredService : {}", monitoredServiceDTO);
        MonitoredServiceDTO result = monitoredServiceService.save(monitoredServiceDTO);
        return ResponseEntity.created(new URI("/api/monitored-services/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, "monitoredService", result.getId().toString()))
            .body(result);
    }

    /**
     * Agent heartbeat - updates lastSeenAt timestamp
     */
    @PostMapping("/heartbeat")
    public ResponseEntity<Void> agentHeartbeat(HttpServletRequest request) {
        LOG.debug("Agent heartbeat received");
        
        String apiKey = request.getHeader("X-API-Key");
        if (apiKey != null) {
            agentService.updateLastSeenByApiKey(apiKey);
        }
        
        return ResponseEntity.ok().build();
    }
}