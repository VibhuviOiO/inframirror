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
import vibhuvi.oio.inframirror.repository.HttpHeartbeatRepository;
import vibhuvi.oio.inframirror.service.HttpHeartbeatService;
import vibhuvi.oio.inframirror.service.dto.HttpHeartbeatDTO;
import vibhuvi.oio.inframirror.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link vibhuvi.oio.inframirror.domain.HttpHeartbeat}.
 */
@RestController
@RequestMapping("/api/http-heartbeats")
public class HttpHeartbeatResource {

    private static final Logger LOG = LoggerFactory.getLogger(HttpHeartbeatResource.class);

    private static final String ENTITY_NAME = "httpHeartbeat";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final HttpHeartbeatService httpHeartbeatService;

    private final HttpHeartbeatRepository httpHeartbeatRepository;

    public HttpHeartbeatResource(HttpHeartbeatService httpHeartbeatService, HttpHeartbeatRepository httpHeartbeatRepository) {
        this.httpHeartbeatService = httpHeartbeatService;
        this.httpHeartbeatRepository = httpHeartbeatRepository;
    }

    /**
     * {@code POST  /http-heartbeats} : Create a new httpHeartbeat.
     *
     * @param httpHeartbeatDTO the httpHeartbeatDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new httpHeartbeatDTO, or with status {@code 400 (Bad Request)} if the httpHeartbeat has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<HttpHeartbeatDTO> createHttpHeartbeat(@Valid @RequestBody HttpHeartbeatDTO httpHeartbeatDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save HttpHeartbeat : {}", httpHeartbeatDTO);
        if (httpHeartbeatDTO.getId() != null) {
            throw new BadRequestAlertException("A new httpHeartbeat cannot already have an ID", ENTITY_NAME, "idexists");
        }
        httpHeartbeatDTO = httpHeartbeatService.save(httpHeartbeatDTO);
        return ResponseEntity.created(new URI("/api/http-heartbeats/" + httpHeartbeatDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, httpHeartbeatDTO.getId().toString()))
            .body(httpHeartbeatDTO);
    }

    /**
     * {@code PUT  /http-heartbeats/:id} : Updates an existing httpHeartbeat.
     *
     * @param id the id of the httpHeartbeatDTO to save.
     * @param httpHeartbeatDTO the httpHeartbeatDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated httpHeartbeatDTO,
     * or with status {@code 400 (Bad Request)} if the httpHeartbeatDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the httpHeartbeatDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<HttpHeartbeatDTO> updateHttpHeartbeat(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody HttpHeartbeatDTO httpHeartbeatDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update HttpHeartbeat : {}, {}", id, httpHeartbeatDTO);
        if (httpHeartbeatDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, httpHeartbeatDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!httpHeartbeatRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        httpHeartbeatDTO = httpHeartbeatService.update(httpHeartbeatDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, httpHeartbeatDTO.getId().toString()))
            .body(httpHeartbeatDTO);
    }

    /**
     * {@code PATCH  /http-heartbeats/:id} : Partial updates given fields of an existing httpHeartbeat, field will ignore if it is null
     *
     * @param id the id of the httpHeartbeatDTO to save.
     * @param httpHeartbeatDTO the httpHeartbeatDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated httpHeartbeatDTO,
     * or with status {@code 400 (Bad Request)} if the httpHeartbeatDTO is not valid,
     * or with status {@code 404 (Not Found)} if the httpHeartbeatDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the httpHeartbeatDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<HttpHeartbeatDTO> partialUpdateHttpHeartbeat(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody HttpHeartbeatDTO httpHeartbeatDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update HttpHeartbeat partially : {}, {}", id, httpHeartbeatDTO);
        if (httpHeartbeatDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, httpHeartbeatDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!httpHeartbeatRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<HttpHeartbeatDTO> result = httpHeartbeatService.partialUpdate(httpHeartbeatDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, httpHeartbeatDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /http-heartbeats} : get all the httpHeartbeats.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of httpHeartbeats in body.
     */
    @GetMapping("")
    public ResponseEntity<List<HttpHeartbeatDTO>> getAllHttpHeartbeats(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of HttpHeartbeats");
        Page<HttpHeartbeatDTO> page = httpHeartbeatService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /http-heartbeats/:id} : get the "id" httpHeartbeat.
     *
     * @param id the id of the httpHeartbeatDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the httpHeartbeatDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<HttpHeartbeatDTO> getHttpHeartbeat(@PathVariable("id") Long id) {
        LOG.debug("REST request to get HttpHeartbeat : {}", id);
        Optional<HttpHeartbeatDTO> httpHeartbeatDTO = httpHeartbeatService.findOne(id);
        return ResponseUtil.wrapOrNotFound(httpHeartbeatDTO);
    }

    /**
     * {@code DELETE  /http-heartbeats/:id} : delete the "id" httpHeartbeat.
     *
     * @param id the id of the httpHeartbeatDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHttpHeartbeat(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete HttpHeartbeat : {}", id);
        httpHeartbeatService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /http-heartbeats/_search?query=:query} : search for the httpHeartbeat corresponding
     * to the query.
     *
     * @param query the query of the httpHeartbeat search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<HttpHeartbeatDTO>> searchHttpHeartbeats(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of HttpHeartbeats for query {}", query);
            Page<HttpHeartbeatDTO> page = httpHeartbeatService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        }
}
