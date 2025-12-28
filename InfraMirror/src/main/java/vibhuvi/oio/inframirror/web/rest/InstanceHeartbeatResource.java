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
import vibhuvi.oio.inframirror.repository.InstanceHeartbeatRepository;
import vibhuvi.oio.inframirror.service.InstanceHeartbeatService;
import vibhuvi.oio.inframirror.service.dto.InstanceHeartbeatDTO;
import vibhuvi.oio.inframirror.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link vibhuvi.oio.inframirror.domain.InstanceHeartbeat}.
 */
@RestController
@RequestMapping("/api/instance-heartbeats")
public class InstanceHeartbeatResource {

    private static final Logger LOG = LoggerFactory.getLogger(InstanceHeartbeatResource.class);

    private static final String ENTITY_NAME = "instanceHeartbeat";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InstanceHeartbeatService instanceHeartbeatService;

    private final InstanceHeartbeatRepository instanceHeartbeatRepository;

    public InstanceHeartbeatResource(
        InstanceHeartbeatService instanceHeartbeatService,
        InstanceHeartbeatRepository instanceHeartbeatRepository
    ) {
        this.instanceHeartbeatService = instanceHeartbeatService;
        this.instanceHeartbeatRepository = instanceHeartbeatRepository;
    }

    /**
     * {@code POST  /instance-heartbeats} : Create a new instanceHeartbeat.
     *
     * @param instanceHeartbeatDTO the instanceHeartbeatDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new instanceHeartbeatDTO, or with status {@code 400 (Bad Request)} if the instanceHeartbeat has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<InstanceHeartbeatDTO> createInstanceHeartbeat(@Valid @RequestBody InstanceHeartbeatDTO instanceHeartbeatDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save InstanceHeartbeat : {}", instanceHeartbeatDTO);
        if (instanceHeartbeatDTO.getId() != null) {
            throw new BadRequestAlertException("A new instanceHeartbeat cannot already have an ID", ENTITY_NAME, "idexists");
        }
        instanceHeartbeatDTO = instanceHeartbeatService.save(instanceHeartbeatDTO);
        return ResponseEntity.created(new URI("/api/instance-heartbeats/" + instanceHeartbeatDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, instanceHeartbeatDTO.getId().toString()))
            .body(instanceHeartbeatDTO);
    }

    /**
     * {@code PUT  /instance-heartbeats/:id} : Updates an existing instanceHeartbeat.
     *
     * @param id the id of the instanceHeartbeatDTO to save.
     * @param instanceHeartbeatDTO the instanceHeartbeatDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated instanceHeartbeatDTO,
     * or with status {@code 400 (Bad Request)} if the instanceHeartbeatDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the instanceHeartbeatDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<InstanceHeartbeatDTO> updateInstanceHeartbeat(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody InstanceHeartbeatDTO instanceHeartbeatDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update InstanceHeartbeat : {}, {}", id, instanceHeartbeatDTO);
        if (instanceHeartbeatDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, instanceHeartbeatDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!instanceHeartbeatRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        instanceHeartbeatDTO = instanceHeartbeatService.update(instanceHeartbeatDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, instanceHeartbeatDTO.getId().toString()))
            .body(instanceHeartbeatDTO);
    }

    /**
     * {@code PATCH  /instance-heartbeats/:id} : Partial updates given fields of an existing instanceHeartbeat, field will ignore if it is null
     *
     * @param id the id of the instanceHeartbeatDTO to save.
     * @param instanceHeartbeatDTO the instanceHeartbeatDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated instanceHeartbeatDTO,
     * or with status {@code 400 (Bad Request)} if the instanceHeartbeatDTO is not valid,
     * or with status {@code 404 (Not Found)} if the instanceHeartbeatDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the instanceHeartbeatDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<InstanceHeartbeatDTO> partialUpdateInstanceHeartbeat(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody InstanceHeartbeatDTO instanceHeartbeatDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update InstanceHeartbeat partially : {}, {}", id, instanceHeartbeatDTO);
        if (instanceHeartbeatDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, instanceHeartbeatDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!instanceHeartbeatRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<InstanceHeartbeatDTO> result = instanceHeartbeatService.partialUpdate(instanceHeartbeatDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, instanceHeartbeatDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /instance-heartbeats} : get all the instanceHeartbeats.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of instanceHeartbeats in body.
     */
    @GetMapping("")
    public ResponseEntity<List<InstanceHeartbeatDTO>> getAllInstanceHeartbeats(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of InstanceHeartbeats");
        Page<InstanceHeartbeatDTO> page = instanceHeartbeatService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /instance-heartbeats/:id} : get the "id" instanceHeartbeat.
     *
     * @param id the id of the instanceHeartbeatDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the instanceHeartbeatDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<InstanceHeartbeatDTO> getInstanceHeartbeat(@PathVariable("id") Long id) {
        LOG.debug("REST request to get InstanceHeartbeat : {}", id);
        Optional<InstanceHeartbeatDTO> instanceHeartbeatDTO = instanceHeartbeatService.findOne(id);
        return ResponseUtil.wrapOrNotFound(instanceHeartbeatDTO);
    }

    /**
     * {@code DELETE  /instance-heartbeats/:id} : delete the "id" instanceHeartbeat.
     *
     * @param id the id of the instanceHeartbeatDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInstanceHeartbeat(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete InstanceHeartbeat : {}", id);
        instanceHeartbeatService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /instance-heartbeats/_search?query=:query} : search for the instanceHeartbeat corresponding
     * to the query.
     *
     * @param query the query of the instanceHeartbeat search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<InstanceHeartbeatDTO>> searchInstanceHeartbeats(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of InstanceHeartbeats for query {}", query);
            Page<InstanceHeartbeatDTO> page = instanceHeartbeatService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        }
}
