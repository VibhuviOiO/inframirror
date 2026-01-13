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
import vibhuvi.oio.inframirror.repository.IntegrationViewRepository;
import vibhuvi.oio.inframirror.service.IntegrationViewService;
import vibhuvi.oio.inframirror.service.dto.IntegrationViewDTO;
import vibhuvi.oio.inframirror.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link vibhuvi.oio.inframirror.domain.IntegrationView}.
 */
@RestController
@RequestMapping("/api/integration-views")
public class IntegrationViewResource {

    private static final Logger LOG = LoggerFactory.getLogger(IntegrationViewResource.class);
    private static final String ENTITY_NAME = "integrationView";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final IntegrationViewService integrationViewService;
    private final IntegrationViewRepository integrationViewRepository;

    public IntegrationViewResource(
        IntegrationViewService integrationViewService,
        IntegrationViewRepository integrationViewRepository
    ) {
        this.integrationViewService = integrationViewService;
        this.integrationViewRepository = integrationViewRepository;
    }

    @PostMapping("")
    public ResponseEntity<IntegrationViewDTO> createIntegrationView(@Valid @RequestBody IntegrationViewDTO integrationViewDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save IntegrationView : {}", integrationViewDTO);
        if (integrationViewDTO.getId() != null) {
            throw new BadRequestAlertException("A new integrationView cannot already have an ID", ENTITY_NAME, "idexists");
        }
        integrationViewDTO = integrationViewService.save(integrationViewDTO);
        return ResponseEntity.created(new URI("/api/integration-views/" + integrationViewDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, integrationViewDTO.getId().toString()))
            .body(integrationViewDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IntegrationViewDTO> updateIntegrationView(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody IntegrationViewDTO integrationViewDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update IntegrationView : {}, {}", id, integrationViewDTO);
        if (integrationViewDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, integrationViewDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }
        if (!integrationViewRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        integrationViewDTO = integrationViewService.update(integrationViewDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, integrationViewDTO.getId().toString()))
            .body(integrationViewDTO);
    }

    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<IntegrationViewDTO> partialUpdateIntegrationView(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody IntegrationViewDTO integrationViewDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update IntegrationView partially : {}, {}", id, integrationViewDTO);
        if (integrationViewDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, integrationViewDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }
        if (!integrationViewRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        Optional<IntegrationViewDTO> result = integrationViewService.partialUpdate(integrationViewDTO);
        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, integrationViewDTO.getId().toString())
        );
    }

    @GetMapping("")
    public ResponseEntity<List<IntegrationViewDTO>> getAllIntegrationViews(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of IntegrationViews");
        Page<IntegrationViewDTO> page = integrationViewService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IntegrationViewDTO> getIntegrationView(@PathVariable("id") Long id) {
        LOG.debug("REST request to get IntegrationView : {}", id);
        Optional<IntegrationViewDTO> integrationViewDTO = integrationViewService.findOne(id);
        return ResponseUtil.wrapOrNotFound(integrationViewDTO);
    }

    @GetMapping("/by-resource/{resourceId}")
    public ResponseEntity<List<IntegrationViewDTO>> getIntegrationViewsByResource(@PathVariable("resourceId") Long resourceId) {
        LOG.debug("REST request to get IntegrationViews by resource id : {}", resourceId);
        List<IntegrationViewDTO> views = integrationViewService.findByResourceId(resourceId);
        return ResponseEntity.ok().body(views);
    }

    @GetMapping("/by-resource/{resourceId}/default")
    public ResponseEntity<IntegrationViewDTO> getDefaultIntegrationViewByResource(@PathVariable("resourceId") Long resourceId) {
        LOG.debug("REST request to get default IntegrationView by resource id : {}", resourceId);
        Optional<IntegrationViewDTO> view = integrationViewService.findDefaultByResourceId(resourceId);
        return ResponseUtil.wrapOrNotFound(view);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIntegrationView(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete IntegrationView : {}", id);
        integrationViewService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
