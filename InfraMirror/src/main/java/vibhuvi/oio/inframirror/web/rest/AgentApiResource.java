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
import vibhuvi.oio.inframirror.service.InstanceHeartbeatService;
import vibhuvi.oio.inframirror.service.dto.*;
import java.time.Instant;
import java.util.Map;

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
    private final InstanceHeartbeatService instanceHeartbeatService;

    public AgentApiResource(
        AgentService agentService,
        RegionService regionService,
        DatacenterService datacenterService,
        InstanceService instanceService,
        HttpMonitorService httpMonitorService,
        MonitoredServiceService monitoredServiceService,
        InstanceHeartbeatService instanceHeartbeatService
    ) {
        this.agentService = agentService;
        this.regionService = regionService;
        this.datacenterService = datacenterService;
        this.instanceService = instanceService;
        this.httpMonitorService = httpMonitorService;
        this.monitoredServiceService = monitoredServiceService;
        this.instanceHeartbeatService = instanceHeartbeatService;
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

    /**
     * Submit instance heartbeat
     */
    @PostMapping("/instance-heartbeats")
    public ResponseEntity<Void> submitInstanceHeartbeat(@RequestBody Map<String, Object> heartbeatData, HttpServletRequest request) {
        LOG.debug("Instance heartbeat received: {}", heartbeatData);
        
        try {
            // Create InstanceHeartbeatDTO from the received data
            InstanceHeartbeatDTO heartbeat = new InstanceHeartbeatDTO();
            
            // Set required fields
            heartbeat.setExecutedAt(Instant.now());
            heartbeat.setHeartbeatType((String) heartbeatData.get("heartbeatType"));
            heartbeat.setSuccess((Boolean) heartbeatData.get("success"));
            heartbeat.setStatus((String) heartbeatData.get("status"));
            
            // Set instance reference
            InstanceDTO instance = new InstanceDTO();
            Object instanceIdObj = heartbeatData.get("instanceId");
            if (instanceIdObj instanceof Number) {
                instance.setId(((Number) instanceIdObj).longValue());
            }
            heartbeat.setInstance(instance);
            
            // Set optional ping metrics
            if (heartbeatData.containsKey("responseTimeMs")) {
                Object responseTime = heartbeatData.get("responseTimeMs");
                if (responseTime instanceof Number) {
                    heartbeat.setResponseTimeMs(((Number) responseTime).intValue());
                }
            }
            if (heartbeatData.containsKey("packetLoss")) {
                Object packetLoss = heartbeatData.get("packetLoss");
                if (packetLoss instanceof Number) {
                    heartbeat.setPacketLoss(((Number) packetLoss).floatValue());
                }
            }
            if (heartbeatData.containsKey("jitterMs")) {
                Object jitter = heartbeatData.get("jitterMs");
                if (jitter instanceof Number) {
                    heartbeat.setJitterMs(((Number) jitter).intValue());
                }
            }
            
            // Set optional hardware metrics
            if (heartbeatData.containsKey("cpuUsage")) {
                Object cpuUsage = heartbeatData.get("cpuUsage");
                if (cpuUsage instanceof Number) {
                    heartbeat.setCpuUsage(((Number) cpuUsage).floatValue());
                }
            }
            if (heartbeatData.containsKey("memoryUsage")) {
                Object memoryUsage = heartbeatData.get("memoryUsage");
                if (memoryUsage instanceof Number) {
                    heartbeat.setMemoryUsage(((Number) memoryUsage).floatValue());
                }
            }
            if (heartbeatData.containsKey("diskUsage")) {
                Object diskUsage = heartbeatData.get("diskUsage");
                if (diskUsage instanceof Number) {
                    heartbeat.setDiskUsage(((Number) diskUsage).floatValue());
                }
            }
            if (heartbeatData.containsKey("loadAverage")) {
                Object loadAverage = heartbeatData.get("loadAverage");
                if (loadAverage instanceof Number) {
                    heartbeat.setLoadAverage(((Number) loadAverage).floatValue());
                }
            }
            if (heartbeatData.containsKey("processCount")) {
                Object processCount = heartbeatData.get("processCount");
                if (processCount instanceof Number) {
                    heartbeat.setProcessCount(((Number) processCount).intValue());
                }
            }
            if (heartbeatData.containsKey("networkRxBytes")) {
                Object networkRx = heartbeatData.get("networkRxBytes");
                if (networkRx instanceof Number) {
                    heartbeat.setNetworkRxBytes(((Number) networkRx).longValue());
                }
            }
            if (heartbeatData.containsKey("networkTxBytes")) {
                Object networkTx = heartbeatData.get("networkTxBytes");
                if (networkTx instanceof Number) {
                    heartbeat.setNetworkTxBytes(((Number) networkTx).longValue());
                }
            }
            if (heartbeatData.containsKey("uptimeSeconds")) {
                Object uptime = heartbeatData.get("uptimeSeconds");
                if (uptime instanceof Number) {
                    heartbeat.setUptimeSeconds(((Number) uptime).longValue());
                }
            }
            
            // Set error information if present
            if (heartbeatData.containsKey("errorMessage")) {
                heartbeat.setErrorMessage((String) heartbeatData.get("errorMessage"));
            }
            if (heartbeatData.containsKey("errorType")) {
                heartbeat.setErrorType((String) heartbeatData.get("errorType"));
            }
            
            // Save the heartbeat
            instanceHeartbeatService.save(heartbeat);
            
            LOG.debug("Instance heartbeat saved successfully for instance {}", instance.getId());
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            LOG.error("Failed to save instance heartbeat: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
}