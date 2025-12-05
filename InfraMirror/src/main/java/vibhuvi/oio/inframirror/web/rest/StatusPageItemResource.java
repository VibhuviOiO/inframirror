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
import vibhuvi.oio.inframirror.repository.StatusPageItemRepository;
import vibhuvi.oio.inframirror.service.StatusPageItemService;
import vibhuvi.oio.inframirror.service.dto.StatusPageItemDTO;
import vibhuvi.oio.inframirror.web.rest.errors.BadRequestAlertException;
import vibhuvi.oio.inframirror.web.rest.errors.ElasticsearchExceptionMapper;

/**
 * REST controller for managing {@link vibhuvi.oio.inframirror.domain.StatusPageItem}.
 */
@RestController
@RequestMapping("/api/status-page-items")
public class StatusPageItemResource {

    private static final Logger LOG = LoggerFactory.getLogger(StatusPageItemResource.class);

    private static final String ENTITY_NAME = "statusPageItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StatusPageItemService statusPageItemService;

    private final StatusPageItemRepository statusPageItemRepository;

    public StatusPageItemResource(StatusPageItemService statusPageItemService, StatusPageItemRepository statusPageItemRepository) {
        this.statusPageItemService = statusPageItemService;
        this.statusPageItemRepository = statusPageItemRepository;
    }

    /**
     * {@code POST  /status-page-items} : Create a new statusPageItem.
     *
     * @param statusPageItemDTO the statusPageItemDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new statusPageItemDTO, or with status {@code 400 (Bad Request)} if the statusPageItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<StatusPageItemDTO> createStatusPageItem(@Valid @RequestBody StatusPageItemDTO statusPageItemDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save StatusPageItem : {}", statusPageItemDTO);
        if (statusPageItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new statusPageItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        statusPageItemDTO = statusPageItemService.save(statusPageItemDTO);
        return ResponseEntity.created(new URI("/api/status-page-items/" + statusPageItemDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, statusPageItemDTO.getId().toString()))
            .body(statusPageItemDTO);
    }

    /**
     * {@code PUT  /status-page-items/:id} : Updates an existing statusPageItem.
     *
     * @param id the id of the statusPageItemDTO to save.
     * @param statusPageItemDTO the statusPageItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated statusPageItemDTO,
     * or with status {@code 400 (Bad Request)} if the statusPageItemDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the statusPageItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StatusPageItemDTO> updateStatusPageItem(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StatusPageItemDTO statusPageItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update StatusPageItem : {}, {}", id, statusPageItemDTO);
        if (statusPageItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, statusPageItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!statusPageItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        statusPageItemDTO = statusPageItemService.update(statusPageItemDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, statusPageItemDTO.getId().toString()))
            .body(statusPageItemDTO);
    }

    /**
     * {@code PATCH  /status-page-items/:id} : Partial updates given fields of an existing statusPageItem, field will ignore if it is null
     *
     * @param id the id of the statusPageItemDTO to save.
     * @param statusPageItemDTO the statusPageItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated statusPageItemDTO,
     * or with status {@code 400 (Bad Request)} if the statusPageItemDTO is not valid,
     * or with status {@code 404 (Not Found)} if the statusPageItemDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the statusPageItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StatusPageItemDTO> partialUpdateStatusPageItem(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StatusPageItemDTO statusPageItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update StatusPageItem partially : {}, {}", id, statusPageItemDTO);
        if (statusPageItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, statusPageItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!statusPageItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StatusPageItemDTO> result = statusPageItemService.partialUpdate(statusPageItemDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, statusPageItemDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /status-page-items} : get all the statusPageItems.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of statusPageItems in body.
     */
    @GetMapping("")
    public ResponseEntity<List<StatusPageItemDTO>> getAllStatusPageItems(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of StatusPageItems");
        Page<StatusPageItemDTO> page = statusPageItemService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /status-page-items/:id} : get the "id" statusPageItem.
     *
     * @param id the id of the statusPageItemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the statusPageItemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StatusPageItemDTO> getStatusPageItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to get StatusPageItem : {}", id);
        Optional<StatusPageItemDTO> statusPageItemDTO = statusPageItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(statusPageItemDTO);
    }

    /**
     * {@code DELETE  /status-page-items/:id} : delete the "id" statusPageItem.
     *
     * @param id the id of the statusPageItemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStatusPageItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete StatusPageItem : {}", id);
        statusPageItemService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /status-page-items/_search?query=:query} : search for the statusPageItem corresponding
     * to the query.
     *
     * @param query the query of the statusPageItem search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<StatusPageItemDTO>> searchStatusPageItems(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of StatusPageItems for query {}", query);
        try {
            Page<StatusPageItemDTO> page = statusPageItemService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
