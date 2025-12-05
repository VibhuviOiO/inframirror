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
import vibhuvi.oio.inframirror.repository.MonitoredServiceRepository;
import vibhuvi.oio.inframirror.service.MonitoredServiceQueryService;
import vibhuvi.oio.inframirror.service.MonitoredServiceService;
import vibhuvi.oio.inframirror.service.criteria.MonitoredServiceCriteria;
import vibhuvi.oio.inframirror.service.dto.MonitoredServiceDTO;
import vibhuvi.oio.inframirror.web.rest.errors.BadRequestAlertException;
import vibhuvi.oio.inframirror.web.rest.errors.ElasticsearchExceptionMapper;

/**
 * REST controller for managing {@link vibhuvi.oio.inframirror.domain.MonitoredService}.
 */
@RestController
@RequestMapping("/api/monitored-services")
public class MonitoredServiceResource {

    private static final Logger LOG = LoggerFactory.getLogger(MonitoredServiceResource.class);

    private static final String ENTITY_NAME = "monitoredService";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MonitoredServiceService monitoredServiceService;

    private final MonitoredServiceRepository monitoredServiceRepository;

    private final MonitoredServiceQueryService monitoredServiceQueryService;

    public MonitoredServiceResource(
        MonitoredServiceService monitoredServiceService,
        MonitoredServiceRepository monitoredServiceRepository,
        MonitoredServiceQueryService monitoredServiceQueryService
    ) {
        this.monitoredServiceService = monitoredServiceService;
        this.monitoredServiceRepository = monitoredServiceRepository;
        this.monitoredServiceQueryService = monitoredServiceQueryService;
    }

    /**
     * {@code POST  /monitored-services} : Create a new monitoredService.
     *
     * @param monitoredServiceDTO the monitoredServiceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new monitoredServiceDTO, or with status {@code 400 (Bad Request)} if the monitoredService has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MonitoredServiceDTO> createMonitoredService(@Valid @RequestBody MonitoredServiceDTO monitoredServiceDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save MonitoredService : {}", monitoredServiceDTO);
        if (monitoredServiceDTO.getId() != null) {
            throw new BadRequestAlertException("A new monitoredService cannot already have an ID", ENTITY_NAME, "idexists");
        }
        monitoredServiceDTO = monitoredServiceService.save(monitoredServiceDTO);
        return ResponseEntity.created(new URI("/api/monitored-services/" + monitoredServiceDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, monitoredServiceDTO.getId().toString()))
            .body(monitoredServiceDTO);
    }

    /**
     * {@code PUT  /monitored-services/:id} : Updates an existing monitoredService.
     *
     * @param id the id of the monitoredServiceDTO to save.
     * @param monitoredServiceDTO the monitoredServiceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated monitoredServiceDTO,
     * or with status {@code 400 (Bad Request)} if the monitoredServiceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the monitoredServiceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MonitoredServiceDTO> updateMonitoredService(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MonitoredServiceDTO monitoredServiceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MonitoredService : {}, {}", id, monitoredServiceDTO);
        if (monitoredServiceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, monitoredServiceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!monitoredServiceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        monitoredServiceDTO = monitoredServiceService.update(monitoredServiceDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, monitoredServiceDTO.getId().toString()))
            .body(monitoredServiceDTO);
    }

    /**
     * {@code PATCH  /monitored-services/:id} : Partial updates given fields of an existing monitoredService, field will ignore if it is null
     *
     * @param id the id of the monitoredServiceDTO to save.
     * @param monitoredServiceDTO the monitoredServiceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated monitoredServiceDTO,
     * or with status {@code 400 (Bad Request)} if the monitoredServiceDTO is not valid,
     * or with status {@code 404 (Not Found)} if the monitoredServiceDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the monitoredServiceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MonitoredServiceDTO> partialUpdateMonitoredService(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MonitoredServiceDTO monitoredServiceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update MonitoredService partially : {}, {}", id, monitoredServiceDTO);
        if (monitoredServiceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, monitoredServiceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!monitoredServiceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MonitoredServiceDTO> result = monitoredServiceService.partialUpdate(monitoredServiceDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, monitoredServiceDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /monitored-services} : get all the monitoredServices.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of monitoredServices in body.
     */
    @GetMapping("")
    public ResponseEntity<List<MonitoredServiceDTO>> getAllMonitoredServices(
        MonitoredServiceCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get MonitoredServices by criteria: {}", criteria);

        Page<MonitoredServiceDTO> page = monitoredServiceQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /monitored-services/count} : count all the monitoredServices.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countMonitoredServices(MonitoredServiceCriteria criteria) {
        LOG.debug("REST request to count MonitoredServices by criteria: {}", criteria);
        return ResponseEntity.ok().body(monitoredServiceQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /monitored-services/:id} : get the "id" monitoredService.
     *
     * @param id the id of the monitoredServiceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the monitoredServiceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MonitoredServiceDTO> getMonitoredService(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MonitoredService : {}", id);
        Optional<MonitoredServiceDTO> monitoredServiceDTO = monitoredServiceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(monitoredServiceDTO);
    }

    /**
     * {@code DELETE  /monitored-services/:id} : delete the "id" monitoredService.
     *
     * @param id the id of the monitoredServiceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMonitoredService(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MonitoredService : {}", id);
        monitoredServiceService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /monitored-services/_search?query=:query} : search for the monitoredService corresponding
     * to the query.
     *
     * @param query the query of the monitoredService search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<MonitoredServiceDTO>> searchMonitoredServices(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of MonitoredServices for query {}", query);
        try {
            Page<MonitoredServiceDTO> page = monitoredServiceService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
