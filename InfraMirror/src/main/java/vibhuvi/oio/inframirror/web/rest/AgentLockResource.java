package vibhuvi.oio.inframirror.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
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
import vibhuvi.oio.inframirror.repository.AgentLockRepository;
import vibhuvi.oio.inframirror.service.AgentLockService;
import vibhuvi.oio.inframirror.service.dto.AgentLockDTO;
import vibhuvi.oio.inframirror.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link vibhuvi.oio.inframirror.domain.AgentLock}.
 * Used for HA coordination between multiple agent nodes.
 */
@RestController
@RequestMapping("/api/agent-locks")
public class AgentLockResource {

    private static final Logger LOG = LoggerFactory.getLogger(AgentLockResource.class);

    private static final String ENTITY_NAME = "agentLock";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AgentLockService agentLockService;
    private final AgentLockRepository agentLockRepository;

    public AgentLockResource(AgentLockService agentLockService, AgentLockRepository agentLockRepository) {
        this.agentLockService = agentLockService;
        this.agentLockRepository = agentLockRepository;
    }

    /**
     * {@code POST  /agent-locks} : Create a new agentLock.
     */
    @PostMapping("")
    public ResponseEntity<AgentLockDTO> createAgentLock(@Valid @RequestBody AgentLockDTO agentLockDTO) throws URISyntaxException {
        LOG.debug("REST request to save AgentLock : {}", agentLockDTO);
        if (agentLockDTO.getId() != null) {
            throw new BadRequestAlertException("A new agentLock cannot already have an ID", ENTITY_NAME, "idexists");
        }
        agentLockDTO = agentLockService.save(agentLockDTO);
        return ResponseEntity.created(new URI("/api/agent-locks/" + agentLockDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, agentLockDTO.getId().toString()))
            .body(agentLockDTO);
    }

    /**
     * {@code PUT  /agent-locks/:id} : Updates an existing agentLock.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AgentLockDTO> updateAgentLock(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AgentLockDTO agentLockDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AgentLock : {}, {}", id, agentLockDTO);
        if (agentLockDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, agentLockDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!agentLockRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        agentLockDTO = agentLockService.update(agentLockDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, agentLockDTO.getId().toString()))
            .body(agentLockDTO);
    }

    /**
     * {@code GET  /agent-locks} : get all the agentLocks.
     */
    @GetMapping("")
    public ResponseEntity<List<AgentLockDTO>> getAllAgentLocks(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of AgentLocks");
        Page<AgentLockDTO> page = agentLockService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /agent-locks/:id} : get the "id" agentLock.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AgentLockDTO> getAgentLock(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AgentLock : {}", id);
        Optional<AgentLockDTO> agentLockDTO = agentLockService.findOne(id);
        return ResponseUtil.wrapOrNotFound(agentLockDTO);
    }

    /**
     * {@code DELETE  /agent-locks/:id} : delete the "id" agentLock.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAgentLock(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AgentLock : {}", id);
        agentLockService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}