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
import vibhuvi.oio.inframirror.repository.ControlIntegrationRepository;
import vibhuvi.oio.inframirror.service.ControlIntegrationService;
import vibhuvi.oio.inframirror.service.dto.ControlIntegrationDTO;
import vibhuvi.oio.inframirror.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link vibhuvi.oio.inframirror.domain.ControlIntegration}.
 */
@RestController
@RequestMapping("/api/control-integrations")
public class ControlIntegrationResource {

    private static final Logger LOG = LoggerFactory.getLogger(ControlIntegrationResource.class);
    private static final String ENTITY_NAME = "controlIntegration";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ControlIntegrationService controlIntegrationService;
    private final ControlIntegrationRepository controlIntegrationRepository;

    public ControlIntegrationResource(
        ControlIntegrationService controlIntegrationService,
        ControlIntegrationRepository controlIntegrationRepository
    ) {
        this.controlIntegrationService = controlIntegrationService;
        this.controlIntegrationRepository = controlIntegrationRepository;
    }

    @PostMapping("")
    public ResponseEntity<ControlIntegrationDTO> createControlIntegration(@Valid @RequestBody ControlIntegrationDTO controlIntegrationDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ControlIntegration : {}", controlIntegrationDTO);
        if (controlIntegrationDTO.getId() != null) {
            throw new BadRequestAlertException("A new controlIntegration cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (controlIntegrationRepository.existsByCode(controlIntegrationDTO.getCode())) {
            throw new BadRequestAlertException("Integration code already exists", ENTITY_NAME, "codeexists");
        }
        controlIntegrationDTO = controlIntegrationService.save(controlIntegrationDTO);
        return ResponseEntity.created(new URI("/api/control-integrations/" + controlIntegrationDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, controlIntegrationDTO.getId().toString()))
            .body(controlIntegrationDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ControlIntegrationDTO> updateControlIntegration(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ControlIntegrationDTO controlIntegrationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ControlIntegration : {}, {}", id, controlIntegrationDTO);
        if (controlIntegrationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, controlIntegrationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }
        if (!controlIntegrationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        controlIntegrationDTO = controlIntegrationService.update(controlIntegrationDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, controlIntegrationDTO.getId().toString()))
            .body(controlIntegrationDTO);
    }

    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ControlIntegrationDTO> partialUpdateControlIntegration(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ControlIntegrationDTO controlIntegrationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ControlIntegration partially : {}, {}", id, controlIntegrationDTO);
        if (controlIntegrationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, controlIntegrationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }
        if (!controlIntegrationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        Optional<ControlIntegrationDTO> result = controlIntegrationService.partialUpdate(controlIntegrationDTO);
        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, controlIntegrationDTO.getId().toString())
        );
    }

    @GetMapping("")
    public ResponseEntity<List<ControlIntegrationDTO>> getAllControlIntegrations(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of ControlIntegrations");
        Page<ControlIntegrationDTO> page = controlIntegrationService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ControlIntegrationDTO> getControlIntegration(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ControlIntegration : {}", id);
        Optional<ControlIntegrationDTO> controlIntegrationDTO = controlIntegrationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(controlIntegrationDTO);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ControlIntegrationDTO> getControlIntegrationByCode(@PathVariable("code") String code) {
        LOG.debug("REST request to get ControlIntegration by code : {}", code);
        Optional<ControlIntegrationDTO> controlIntegrationDTO = controlIntegrationService.findByCode(code);
        return ResponseUtil.wrapOrNotFound(controlIntegrationDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteControlIntegration(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ControlIntegration : {}", id);
        controlIntegrationService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
