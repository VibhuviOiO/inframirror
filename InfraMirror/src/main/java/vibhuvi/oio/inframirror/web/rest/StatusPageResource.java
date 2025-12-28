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
import vibhuvi.oio.inframirror.repository.StatusPageRepository;
import vibhuvi.oio.inframirror.service.StatusPageQueryService;
import vibhuvi.oio.inframirror.service.StatusPageService;
import vibhuvi.oio.inframirror.service.criteria.StatusPageCriteria;
import vibhuvi.oio.inframirror.service.dto.StatusPageDTO;
import vibhuvi.oio.inframirror.service.dto.StatusPageSearchResultDTO;
import vibhuvi.oio.inframirror.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link vibhuvi.oio.inframirror.domain.StatusPage}.
 */
@RestController
@RequestMapping("/api/status-pages")
public class StatusPageResource {

    private static final Logger LOG = LoggerFactory.getLogger(StatusPageResource.class);

    private static final String ENTITY_NAME = "statusPage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StatusPageService statusPageService;

    private final StatusPageRepository statusPageRepository;

    private final StatusPageQueryService statusPageQueryService;

    public StatusPageResource(
        StatusPageService statusPageService,
        StatusPageRepository statusPageRepository,
        StatusPageQueryService statusPageQueryService
    ) {
        this.statusPageService = statusPageService;
        this.statusPageRepository = statusPageRepository;
        this.statusPageQueryService = statusPageQueryService;
    }

    /**
     * {@code POST  /status-pages} : Create a new statusPage.
     *
     * @param statusPageDTO the statusPageDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new statusPageDTO, or with status {@code 400 (Bad Request)} if the statusPage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<StatusPageDTO> createStatusPage(@Valid @RequestBody StatusPageDTO statusPageDTO) throws URISyntaxException {
        LOG.debug("REST request to save StatusPage : {}", statusPageDTO);
        if (statusPageDTO.getId() != null) {
            throw new BadRequestAlertException("A new statusPage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        statusPageDTO = statusPageService.save(statusPageDTO);
        return ResponseEntity.created(new URI("/api/status-pages/" + statusPageDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, statusPageDTO.getId().toString()))
            .body(statusPageDTO);
    }

    /**
     * {@code PUT  /status-pages/:id} : Updates an existing statusPage.
     *
     * @param id the id of the statusPageDTO to save.
     * @param statusPageDTO the statusPageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated statusPageDTO,
     * or with status {@code 400 (Bad Request)} if the statusPageDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the statusPageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StatusPageDTO> updateStatusPage(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StatusPageDTO statusPageDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update StatusPage : {}, {}", id, statusPageDTO);
        if (statusPageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, statusPageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!statusPageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        statusPageDTO = statusPageService.update(statusPageDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, statusPageDTO.getId().toString()))
            .body(statusPageDTO);
    }

    /**
     * {@code PATCH  /status-pages/:id} : Partial updates given fields of an existing statusPage, field will ignore if it is null
     *
     * @param id the id of the statusPageDTO to save.
     * @param statusPageDTO the statusPageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated statusPageDTO,
     * or with status {@code 400 (Bad Request)} if the statusPageDTO is not valid,
     * or with status {@code 404 (Not Found)} if the statusPageDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the statusPageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StatusPageDTO> partialUpdateStatusPage(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StatusPageDTO statusPageDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update StatusPage partially : {}, {}", id, statusPageDTO);
        if (statusPageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, statusPageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!statusPageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StatusPageDTO> result = statusPageService.partialUpdate(statusPageDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, statusPageDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /status-pages} : get all the statusPages.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of statusPages in body.
     */
    @GetMapping("")
    public ResponseEntity<List<StatusPageDTO>> getAllStatusPages(
        StatusPageCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get StatusPages by criteria: {}", criteria);

        Page<StatusPageDTO> page = statusPageQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /status-pages/count} : count all the statusPages.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countStatusPages(StatusPageCriteria criteria) {
        LOG.debug("REST request to count StatusPages by criteria: {}", criteria);
        return ResponseEntity.ok().body(statusPageQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /status-pages/:id} : get the "id" statusPage.
     *
     * @param id the id of the statusPageDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the statusPageDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StatusPageDTO> getStatusPage(@PathVariable("id") Long id) {
        LOG.debug("REST request to get StatusPage : {}", id);
        Optional<StatusPageDTO> statusPageDTO = statusPageService.findOne(id);
        return ResponseUtil.wrapOrNotFound(statusPageDTO);
    }

    /**
     * {@code DELETE  /status-pages/:id} : delete the "id" statusPage.
     *
     * @param id the id of the statusPageDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStatusPage(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete StatusPage : {}", id);
        statusPageService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /status-pages/_search?query=:query} : search for the statusPage corresponding
     * to the query.
     *
     * @param query the query of the statusPage search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<StatusPageDTO>> searchStatusPages(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of StatusPages for query {}", query);
        Page<StatusPageDTO> page = statusPageService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
    
    @GetMapping("/_search/prefix")
    public ResponseEntity<List<StatusPageDTO>> searchStatusPagesPrefix(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        Page<StatusPageDTO> page = statusPageService.searchPrefix(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
    
    @GetMapping("/_search/fuzzy")
    public ResponseEntity<List<StatusPageDTO>> searchStatusPagesFuzzy(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        Page<StatusPageDTO> page = statusPageService.searchFuzzy(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
    
    @GetMapping("/_search/highlight")
    public ResponseEntity<List<StatusPageSearchResultDTO>> searchStatusPagesWithHighlight(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        Page<StatusPageSearchResultDTO> page = statusPageService.searchWithHighlight(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
