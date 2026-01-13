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
import vibhuvi.oio.inframirror.repository.IntegrationInstanceRepository;
import vibhuvi.oio.inframirror.service.IntegrationInstanceService;
import vibhuvi.oio.inframirror.service.dto.IntegrationInstanceDTO;
import vibhuvi.oio.inframirror.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link vibhuvi.oio.inframirror.domain.IntegrationInstance}.
 */
@RestController
@RequestMapping("/api/integration-instances")
public class IntegrationInstanceResource {

    private static final Logger LOG = LoggerFactory.getLogger(IntegrationInstanceResource.class);
    private static final String ENTITY_NAME = "integrationInstance";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final IntegrationInstanceService integrationInstanceService;
    private final IntegrationInstanceRepository integrationInstanceRepository;

    public IntegrationInstanceResource(
        IntegrationInstanceService integrationInstanceService,
        IntegrationInstanceRepository integrationInstanceRepository
    ) {
        this.integrationInstanceService = integrationInstanceService;
        this.integrationInstanceRepository = integrationInstanceRepository;
    }

    @PostMapping("")
    public ResponseEntity<IntegrationInstanceDTO> createIntegrationInstance(
        @Valid @RequestBody IntegrationInstanceDTO integrationInstanceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save IntegrationInstance : {}", integrationInstanceDTO);
        if (integrationInstanceDTO.getId() != null) {
            throw new BadRequestAlertException("A new integrationInstance cannot already have an ID", ENTITY_NAME, "idexists");
        }
        integrationInstanceDTO = integrationInstanceService.save(integrationInstanceDTO);
        return ResponseEntity.created(new URI("/api/integration-instances/" + integrationInstanceDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, integrationInstanceDTO.getId().toString()))
            .body(integrationInstanceDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IntegrationInstanceDTO> updateIntegrationInstance(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody IntegrationInstanceDTO integrationInstanceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update IntegrationInstance : {}, {}", id, integrationInstanceDTO);
        if (integrationInstanceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, integrationInstanceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }
        if (!integrationInstanceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        integrationInstanceDTO = integrationInstanceService.update(integrationInstanceDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, integrationInstanceDTO.getId().toString()))
            .body(integrationInstanceDTO);
    }

    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<IntegrationInstanceDTO> partialUpdateIntegrationInstance(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody IntegrationInstanceDTO integrationInstanceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update IntegrationInstance partially : {}, {}", id, integrationInstanceDTO);
        if (integrationInstanceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, integrationInstanceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }
        if (!integrationInstanceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        Optional<IntegrationInstanceDTO> result = integrationInstanceService.partialUpdate(integrationInstanceDTO);
        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, integrationInstanceDTO.getId().toString())
        );
    }

    @GetMapping("")
    public ResponseEntity<List<IntegrationInstanceDTO>> getAllIntegrationInstances(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of IntegrationInstances");
        Page<IntegrationInstanceDTO> page = integrationInstanceService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IntegrationInstanceDTO> getIntegrationInstance(@PathVariable("id") Long id) {
        LOG.debug("REST request to get IntegrationInstance : {}", id);
        Optional<IntegrationInstanceDTO> integrationInstanceDTO = integrationInstanceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(integrationInstanceDTO);
    }

    @GetMapping("/by-integration/{code}")
    public ResponseEntity<List<IntegrationInstanceDTO>> getIntegrationInstancesByCode(@PathVariable("code") String code) {
        LOG.debug("REST request to get IntegrationInstances by integration code : {}", code);
        List<IntegrationInstanceDTO> instances = integrationInstanceService.findByControlIntegrationCode(code);
        return ResponseEntity.ok().body(instances);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIntegrationInstance(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete IntegrationInstance : {}", id);
        integrationInstanceService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
