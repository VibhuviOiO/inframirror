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
import vibhuvi.oio.inframirror.repository.HttpMonitorRepository;
import vibhuvi.oio.inframirror.service.HttpMonitorQueryService;
import vibhuvi.oio.inframirror.service.HttpMonitorService;
import vibhuvi.oio.inframirror.service.criteria.HttpMonitorCriteria;
import vibhuvi.oio.inframirror.service.dto.HttpMonitorDTO;
import vibhuvi.oio.inframirror.web.rest.errors.BadRequestAlertException;
import vibhuvi.oio.inframirror.web.rest.errors.ElasticsearchExceptionMapper;

/**
 * REST controller for managing {@link vibhuvi.oio.inframirror.domain.HttpMonitor}.
 */
@RestController
@RequestMapping("/api/http-monitors")
public class HttpMonitorResource {

    private static final Logger LOG = LoggerFactory.getLogger(HttpMonitorResource.class);

    private static final String ENTITY_NAME = "httpMonitor";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final HttpMonitorService httpMonitorService;

    private final HttpMonitorRepository httpMonitorRepository;

    private final HttpMonitorQueryService httpMonitorQueryService;

    public HttpMonitorResource(
        HttpMonitorService httpMonitorService,
        HttpMonitorRepository httpMonitorRepository,
        HttpMonitorQueryService httpMonitorQueryService
    ) {
        this.httpMonitorService = httpMonitorService;
        this.httpMonitorRepository = httpMonitorRepository;
        this.httpMonitorQueryService = httpMonitorQueryService;
    }

    /**
     * {@code POST  /http-monitors} : Create a new httpMonitor.
     *
     * @param httpMonitorDTO the httpMonitorDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new httpMonitorDTO, or with status {@code 400 (Bad Request)} if the httpMonitor has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<HttpMonitorDTO> createHttpMonitor(@Valid @RequestBody HttpMonitorDTO httpMonitorDTO) throws URISyntaxException {
        LOG.debug("REST request to save HttpMonitor : {}", httpMonitorDTO);
        if (httpMonitorDTO.getId() != null) {
            throw new BadRequestAlertException("A new httpMonitor cannot already have an ID", ENTITY_NAME, "idexists");
        }
        httpMonitorDTO = httpMonitorService.save(httpMonitorDTO);
        return ResponseEntity.created(new URI("/api/http-monitors/" + httpMonitorDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, httpMonitorDTO.getId().toString()))
            .body(httpMonitorDTO);
    }

    /**
     * {@code PUT  /http-monitors/:id} : Updates an existing httpMonitor.
     *
     * @param id the id of the httpMonitorDTO to save.
     * @param httpMonitorDTO the httpMonitorDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated httpMonitorDTO,
     * or with status {@code 400 (Bad Request)} if the httpMonitorDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the httpMonitorDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<HttpMonitorDTO> updateHttpMonitor(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody HttpMonitorDTO httpMonitorDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update HttpMonitor : {}, {}", id, httpMonitorDTO);
        if (httpMonitorDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, httpMonitorDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!httpMonitorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        httpMonitorDTO = httpMonitorService.update(httpMonitorDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, httpMonitorDTO.getId().toString()))
            .body(httpMonitorDTO);
    }

    /**
     * {@code PATCH  /http-monitors/:id} : Partial updates given fields of an existing httpMonitor, field will ignore if it is null
     *
     * @param id the id of the httpMonitorDTO to save.
     * @param httpMonitorDTO the httpMonitorDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated httpMonitorDTO,
     * or with status {@code 400 (Bad Request)} if the httpMonitorDTO is not valid,
     * or with status {@code 404 (Not Found)} if the httpMonitorDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the httpMonitorDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<HttpMonitorDTO> partialUpdateHttpMonitor(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody HttpMonitorDTO httpMonitorDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update HttpMonitor partially : {}, {}", id, httpMonitorDTO);
        if (httpMonitorDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, httpMonitorDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!httpMonitorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<HttpMonitorDTO> result = httpMonitorService.partialUpdate(httpMonitorDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, httpMonitorDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /http-monitors} : get all the httpMonitors.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of httpMonitors in body.
     */
    @GetMapping("")
    public ResponseEntity<List<HttpMonitorDTO>> getAllHttpMonitors(
        HttpMonitorCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get HttpMonitors by criteria: {}", criteria);

        Page<HttpMonitorDTO> page = httpMonitorQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /http-monitors/count} : count all the httpMonitors.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countHttpMonitors(HttpMonitorCriteria criteria) {
        LOG.debug("REST request to count HttpMonitors by criteria: {}", criteria);
        return ResponseEntity.ok().body(httpMonitorQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /http-monitors/:id} : get the "id" httpMonitor.
     *
     * @param id the id of the httpMonitorDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the httpMonitorDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<HttpMonitorDTO> getHttpMonitor(@PathVariable("id") Long id) {
        LOG.debug("REST request to get HttpMonitor : {}", id);
        Optional<HttpMonitorDTO> httpMonitorDTO = httpMonitorService.findOne(id);
        return ResponseUtil.wrapOrNotFound(httpMonitorDTO);
    }

    /**
     * {@code DELETE  /http-monitors/:id} : delete the "id" httpMonitor.
     *
     * @param id the id of the httpMonitorDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHttpMonitor(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete HttpMonitor : {}", id);
        httpMonitorService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /http-monitors/_search?query=:query} : search for the httpMonitor corresponding
     * to the query.
     *
     * @param query the query of the httpMonitor search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<HttpMonitorDTO>> searchHttpMonitors(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of HttpMonitors for query {}", query);
        try {
            Page<HttpMonitorDTO> page = httpMonitorService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
