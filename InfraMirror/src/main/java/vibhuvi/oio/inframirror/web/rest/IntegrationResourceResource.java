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
import vibhuvi.oio.inframirror.repository.IntegrationResourceRepository;
import vibhuvi.oio.inframirror.service.IntegrationResourceService;
import vibhuvi.oio.inframirror.service.dto.IntegrationResourceDTO;
import vibhuvi.oio.inframirror.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link vibhuvi.oio.inframirror.domain.IntegrationResource}.
 */
@RestController
@RequestMapping("/api/integration-resources")
public class IntegrationResourceResource {

    private static final Logger LOG = LoggerFactory.getLogger(IntegrationResourceResource.class);
    private static final String ENTITY_NAME = "integrationResource";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final IntegrationResourceService integrationResourceService;
    private final IntegrationResourceRepository integrationResourceRepository;

    public IntegrationResourceResource(
        IntegrationResourceService integrationResourceService,
        IntegrationResourceRepository integrationResourceRepository
    ) {
        this.integrationResourceService = integrationResourceService;
        this.integrationResourceRepository = integrationResourceRepository;
    }

    @PostMapping("")
    public ResponseEntity<IntegrationResourceDTO> createIntegrationResource(
        @Valid @RequestBody IntegrationResourceDTO integrationResourceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save IntegrationResource : {}", integrationResourceDTO);
        if (integrationResourceDTO.getId() != null) {
            throw new BadRequestAlertException("A new integrationResource cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (integrationResourceRepository.existsByControlIntegrationIdAndName(
            integrationResourceDTO.getControlIntegrationId(),
            integrationResourceDTO.getName()
        )) {
            throw new BadRequestAlertException("Resource name already exists for this integration", ENTITY_NAME, "nameexists");
        }
        integrationResourceDTO = integrationResourceService.save(integrationResourceDTO);
        return ResponseEntity.created(new URI("/api/integration-resources/" + integrationResourceDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, integrationResourceDTO.getId().toString()))
            .body(integrationResourceDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IntegrationResourceDTO> updateIntegrationResource(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody IntegrationResourceDTO integrationResourceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update IntegrationResource : {}, {}", id, integrationResourceDTO);
        if (integrationResourceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, integrationResourceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }
        if (!integrationResourceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        integrationResourceDTO = integrationResourceService.update(integrationResourceDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, integrationResourceDTO.getId().toString()))
            .body(integrationResourceDTO);
    }

    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<IntegrationResourceDTO> partialUpdateIntegrationResource(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody IntegrationResourceDTO integrationResourceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update IntegrationResource partially : {}, {}", id, integrationResourceDTO);
        if (integrationResourceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, integrationResourceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }
        if (!integrationResourceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        Optional<IntegrationResourceDTO> result = integrationResourceService.partialUpdate(integrationResourceDTO);
        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, integrationResourceDTO.getId().toString())
        );
    }

    @GetMapping("")
    public ResponseEntity<List<IntegrationResourceDTO>> getAllIntegrationResources(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of IntegrationResources");
        Page<IntegrationResourceDTO> page = integrationResourceService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IntegrationResourceDTO> getIntegrationResource(@PathVariable("id") Long id) {
        LOG.debug("REST request to get IntegrationResource : {}", id);
        Optional<IntegrationResourceDTO> integrationResourceDTO = integrationResourceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(integrationResourceDTO);
    }

    @GetMapping("/by-integration/{integrationId}")
    public ResponseEntity<List<IntegrationResourceDTO>> getIntegrationResourcesByIntegration(
        @PathVariable("integrationId") Long integrationId
    ) {
        LOG.debug("REST request to get IntegrationResources by integration id : {}", integrationId);
        List<IntegrationResourceDTO> resources = integrationResourceService.findByControlIntegrationId(integrationId);
        return ResponseEntity.ok().body(resources);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIntegrationResource(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete IntegrationResource : {}", id);
        integrationResourceService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
