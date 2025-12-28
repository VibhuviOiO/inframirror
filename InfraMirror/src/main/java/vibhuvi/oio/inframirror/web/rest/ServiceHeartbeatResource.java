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
import vibhuvi.oio.inframirror.repository.ServiceHeartbeatRepository;
import vibhuvi.oio.inframirror.service.ServiceHeartbeatService;
import vibhuvi.oio.inframirror.service.dto.ServiceHeartbeatDTO;
import vibhuvi.oio.inframirror.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link vibhuvi.oio.inframirror.domain.ServiceHeartbeat}.
 */
@RestController
@RequestMapping("/api/service-heartbeats")
public class ServiceHeartbeatResource {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceHeartbeatResource.class);

    private static final String ENTITY_NAME = "serviceHeartbeat";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ServiceHeartbeatService serviceHeartbeatService;

    private final ServiceHeartbeatRepository serviceHeartbeatRepository;

    public ServiceHeartbeatResource(
        ServiceHeartbeatService serviceHeartbeatService,
        ServiceHeartbeatRepository serviceHeartbeatRepository
    ) {
        this.serviceHeartbeatService = serviceHeartbeatService;
        this.serviceHeartbeatRepository = serviceHeartbeatRepository;
    }

    /**
     * {@code POST  /service-heartbeats} : Create a new serviceHeartbeat.
     *
     * @param serviceHeartbeatDTO the serviceHeartbeatDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new serviceHeartbeatDTO, or with status {@code 400 (Bad Request)} if the serviceHeartbeat has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ServiceHeartbeatDTO> createServiceHeartbeat(@Valid @RequestBody ServiceHeartbeatDTO serviceHeartbeatDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ServiceHeartbeat : {}", serviceHeartbeatDTO);
        if (serviceHeartbeatDTO.getId() != null) {
            throw new BadRequestAlertException("A new serviceHeartbeat cannot already have an ID", ENTITY_NAME, "idexists");
        }
        serviceHeartbeatDTO = serviceHeartbeatService.save(serviceHeartbeatDTO);
        return ResponseEntity.created(new URI("/api/service-heartbeats/" + serviceHeartbeatDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, serviceHeartbeatDTO.getId().toString()))
            .body(serviceHeartbeatDTO);
    }

    /**
     * {@code PUT  /service-heartbeats/:id} : Updates an existing serviceHeartbeat.
     *
     * @param id the id of the serviceHeartbeatDTO to save.
     * @param serviceHeartbeatDTO the serviceHeartbeatDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated serviceHeartbeatDTO,
     * or with status {@code 400 (Bad Request)} if the serviceHeartbeatDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the serviceHeartbeatDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ServiceHeartbeatDTO> updateServiceHeartbeat(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ServiceHeartbeatDTO serviceHeartbeatDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ServiceHeartbeat : {}, {}", id, serviceHeartbeatDTO);
        if (serviceHeartbeatDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, serviceHeartbeatDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!serviceHeartbeatRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        serviceHeartbeatDTO = serviceHeartbeatService.update(serviceHeartbeatDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, serviceHeartbeatDTO.getId().toString()))
            .body(serviceHeartbeatDTO);
    }

    /**
     * {@code PATCH  /service-heartbeats/:id} : Partial updates given fields of an existing serviceHeartbeat, field will ignore if it is null
     *
     * @param id the id of the serviceHeartbeatDTO to save.
     * @param serviceHeartbeatDTO the serviceHeartbeatDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated serviceHeartbeatDTO,
     * or with status {@code 400 (Bad Request)} if the serviceHeartbeatDTO is not valid,
     * or with status {@code 404 (Not Found)} if the serviceHeartbeatDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the serviceHeartbeatDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ServiceHeartbeatDTO> partialUpdateServiceHeartbeat(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ServiceHeartbeatDTO serviceHeartbeatDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ServiceHeartbeat partially : {}, {}", id, serviceHeartbeatDTO);
        if (serviceHeartbeatDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, serviceHeartbeatDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!serviceHeartbeatRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ServiceHeartbeatDTO> result = serviceHeartbeatService.partialUpdate(serviceHeartbeatDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, serviceHeartbeatDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /service-heartbeats} : get all the serviceHeartbeats.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of serviceHeartbeats in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ServiceHeartbeatDTO>> getAllServiceHeartbeats(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of ServiceHeartbeats");
        Page<ServiceHeartbeatDTO> page = serviceHeartbeatService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /service-heartbeats/:id} : get the "id" serviceHeartbeat.
     *
     * @param id the id of the serviceHeartbeatDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the serviceHeartbeatDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ServiceHeartbeatDTO> getServiceHeartbeat(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ServiceHeartbeat : {}", id);
        Optional<ServiceHeartbeatDTO> serviceHeartbeatDTO = serviceHeartbeatService.findOne(id);
        return ResponseUtil.wrapOrNotFound(serviceHeartbeatDTO);
    }

    /**
     * {@code DELETE  /service-heartbeats/:id} : delete the "id" serviceHeartbeat.
     *
     * @param id the id of the serviceHeartbeatDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServiceHeartbeat(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ServiceHeartbeat : {}", id);
        serviceHeartbeatService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /service-heartbeats/_search?query=:query} : search for the serviceHeartbeat corresponding
     * to the query.
     *
     * @param query the query of the serviceHeartbeat search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<ServiceHeartbeatDTO>> searchServiceHeartbeats(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of ServiceHeartbeats for query {}", query);
            Page<ServiceHeartbeatDTO> page = serviceHeartbeatService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        }
}
