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
import vibhuvi.oio.inframirror.repository.StatusDependencyRepository;
import vibhuvi.oio.inframirror.service.StatusDependencyService;
import vibhuvi.oio.inframirror.service.dto.StatusDependencyDTO;
import vibhuvi.oio.inframirror.web.rest.errors.BadRequestAlertException;
import vibhuvi.oio.inframirror.web.rest.errors.ElasticsearchExceptionMapper;

/**
 * REST controller for managing {@link vibhuvi.oio.inframirror.domain.StatusDependency}.
 */
@RestController
@RequestMapping("/api/status-dependencies")
public class StatusDependencyResource {

    private static final Logger LOG = LoggerFactory.getLogger(StatusDependencyResource.class);

    private static final String ENTITY_NAME = "statusDependency";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StatusDependencyService statusDependencyService;

    private final StatusDependencyRepository statusDependencyRepository;

    public StatusDependencyResource(
        StatusDependencyService statusDependencyService,
        StatusDependencyRepository statusDependencyRepository
    ) {
        this.statusDependencyService = statusDependencyService;
        this.statusDependencyRepository = statusDependencyRepository;
    }

    /**
     * {@code POST  /status-dependencies} : Create a new statusDependency.
     *
     * @param statusDependencyDTO the statusDependencyDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new statusDependencyDTO, or with status {@code 400 (Bad Request)} if the statusDependency has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<StatusDependencyDTO> createStatusDependency(@Valid @RequestBody StatusDependencyDTO statusDependencyDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save StatusDependency : {}", statusDependencyDTO);
        if (statusDependencyDTO.getId() != null) {
            throw new BadRequestAlertException("A new statusDependency cannot already have an ID", ENTITY_NAME, "idexists");
        }
        statusDependencyDTO = statusDependencyService.save(statusDependencyDTO);
        return ResponseEntity.created(new URI("/api/status-dependencies/" + statusDependencyDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, statusDependencyDTO.getId().toString()))
            .body(statusDependencyDTO);
    }

    /**
     * {@code PUT  /status-dependencies/:id} : Updates an existing statusDependency.
     *
     * @param id the id of the statusDependencyDTO to save.
     * @param statusDependencyDTO the statusDependencyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated statusDependencyDTO,
     * or with status {@code 400 (Bad Request)} if the statusDependencyDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the statusDependencyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StatusDependencyDTO> updateStatusDependency(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StatusDependencyDTO statusDependencyDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update StatusDependency : {}, {}", id, statusDependencyDTO);
        if (statusDependencyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, statusDependencyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!statusDependencyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        statusDependencyDTO = statusDependencyService.update(statusDependencyDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, statusDependencyDTO.getId().toString()))
            .body(statusDependencyDTO);
    }

    /**
     * {@code PATCH  /status-dependencies/:id} : Partial updates given fields of an existing statusDependency, field will ignore if it is null
     *
     * @param id the id of the statusDependencyDTO to save.
     * @param statusDependencyDTO the statusDependencyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated statusDependencyDTO,
     * or with status {@code 400 (Bad Request)} if the statusDependencyDTO is not valid,
     * or with status {@code 404 (Not Found)} if the statusDependencyDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the statusDependencyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StatusDependencyDTO> partialUpdateStatusDependency(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StatusDependencyDTO statusDependencyDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update StatusDependency partially : {}, {}", id, statusDependencyDTO);
        if (statusDependencyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, statusDependencyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!statusDependencyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StatusDependencyDTO> result = statusDependencyService.partialUpdate(statusDependencyDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, statusDependencyDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /status-dependencies} : get all the statusDependencies.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of statusDependencies in body.
     */
    @GetMapping("")
    public ResponseEntity<List<StatusDependencyDTO>> getAllStatusDependencies(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of StatusDependencies");
        Page<StatusDependencyDTO> page = statusDependencyService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /status-dependencies/:id} : get the "id" statusDependency.
     *
     * @param id the id of the statusDependencyDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the statusDependencyDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StatusDependencyDTO> getStatusDependency(@PathVariable("id") Long id) {
        LOG.debug("REST request to get StatusDependency : {}", id);
        Optional<StatusDependencyDTO> statusDependencyDTO = statusDependencyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(statusDependencyDTO);
    }

    /**
     * {@code DELETE  /status-dependencies/:id} : delete the "id" statusDependency.
     *
     * @param id the id of the statusDependencyDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStatusDependency(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete StatusDependency : {}", id);
        statusDependencyService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /status-dependencies/_search?query=:query} : search for the statusDependency corresponding
     * to the query.
     *
     * @param query the query of the statusDependency search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<StatusDependencyDTO>> searchStatusDependencies(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of StatusDependencies for query {}", query);
        try {
            Page<StatusDependencyDTO> page = statusDependencyService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
