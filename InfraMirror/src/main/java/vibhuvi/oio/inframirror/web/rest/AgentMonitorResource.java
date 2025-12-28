package vibhuvi.oio.inframirror.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;
import vibhuvi.oio.inframirror.repository.AgentMonitorRepository;
import vibhuvi.oio.inframirror.service.AgentMonitorQueryService;
import vibhuvi.oio.inframirror.service.AgentMonitorService;
import vibhuvi.oio.inframirror.service.criteria.AgentMonitorCriteria;
import vibhuvi.oio.inframirror.service.dto.AgentMonitorDTO;
import vibhuvi.oio.inframirror.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link vibhuvi.oio.inframirror.domain.AgentMonitor}.
 */
@RestController
@RequestMapping("/api/agent-monitors")
public class AgentMonitorResource {

    private static final Logger LOG = LoggerFactory.getLogger(AgentMonitorResource.class);

    private static final String ENTITY_NAME = "agentMonitor";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AgentMonitorService agentMonitorService;

    private final AgentMonitorRepository agentMonitorRepository;

    private final AgentMonitorQueryService agentMonitorQueryService;

    public AgentMonitorResource(
        AgentMonitorService agentMonitorService,
        AgentMonitorRepository agentMonitorRepository,
        AgentMonitorQueryService agentMonitorQueryService
    ) {
        this.agentMonitorService = agentMonitorService;
        this.agentMonitorRepository = agentMonitorRepository;
        this.agentMonitorQueryService = agentMonitorQueryService;
    }

    @PostMapping("")
    public ResponseEntity<AgentMonitorDTO> createAgentMonitor(@Valid @RequestBody AgentMonitorDTO agentMonitorDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save AgentMonitor");
        if (agentMonitorDTO.getId() != null) {
            throw new BadRequestAlertException("A new agentMonitor cannot already have an ID", ENTITY_NAME, "idexists");
        }
        agentMonitorDTO = agentMonitorService.save(agentMonitorDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, agentMonitorDTO.getId().toString()))
            .body(agentMonitorDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgentMonitorDTO> updateAgentMonitor(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AgentMonitorDTO agentMonitorDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AgentMonitor : {}", String.valueOf(id).replaceAll("[\\r\\n]", ""));
        if (agentMonitorDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, agentMonitorDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!agentMonitorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        agentMonitorDTO = agentMonitorService.update(agentMonitorDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, agentMonitorDTO.getId().toString()))
            .body(agentMonitorDTO);
    }

    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AgentMonitorDTO> partialUpdateAgentMonitor(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AgentMonitorDTO agentMonitorDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update AgentMonitor partially : {}", String.valueOf(id).replaceAll("[\\r\\n]", ""));
        if (agentMonitorDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, agentMonitorDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!agentMonitorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AgentMonitorDTO> result = agentMonitorService.partialUpdate(agentMonitorDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, agentMonitorDTO.getId().toString())
        );
    }

    @GetMapping("")
    public ResponseEntity<List<AgentMonitorDTO>> getAllAgentMonitors(
        AgentMonitorCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get AgentMonitors by criteria: {}", criteria);

        Page<AgentMonitorDTO> page = agentMonitorQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countAgentMonitors(AgentMonitorCriteria criteria) {
        LOG.debug("REST request to count AgentMonitors by criteria: {}", criteria);
        return ResponseEntity.ok().body(agentMonitorQueryService.countByCriteria(criteria));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgentMonitorDTO> getAgentMonitor(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AgentMonitor : {}", String.valueOf(id).replaceAll("[\\r\\n]", ""));
        Optional<AgentMonitorDTO> agentMonitorDTO = agentMonitorService.findOne(id);
        return ResponseUtil.wrapOrNotFound(agentMonitorDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAgentMonitor(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AgentMonitor : {}", String.valueOf(id).replaceAll("[\\r\\n]", ""));
        agentMonitorService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
