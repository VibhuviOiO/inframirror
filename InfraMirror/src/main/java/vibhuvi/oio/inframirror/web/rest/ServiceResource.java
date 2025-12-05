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
import vibhuvi.oio.inframirror.repository.ServiceRepository;
import vibhuvi.oio.inframirror.service.ServiceQueryService;
import vibhuvi.oio.inframirror.service.ServiceService;
import vibhuvi.oio.inframirror.service.criteria.ServiceCriteria;
import vibhuvi.oio.inframirror.service.dto.ServiceDTO;
import vibhuvi.oio.inframirror.web.rest.errors.BadRequestAlertException;
import vibhuvi.oio.inframirror.web.rest.errors.ElasticsearchExceptionMapper;

/**
 * REST controller for managing {@link vibhuvi.oio.inframirror.domain.Service}.
 */
@RestController
@RequestMapping("/api/services")
public class ServiceResource {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceResource.class);

    private static final String ENTITY_NAME = "service";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ServiceService serviceService;

    private final ServiceRepository serviceRepository;

    private final ServiceQueryService serviceQueryService;

    public ServiceResource(ServiceService serviceService, ServiceRepository serviceRepository, ServiceQueryService serviceQueryService) {
        this.serviceService = serviceService;
        this.serviceRepository = serviceRepository;
        this.serviceQueryService = serviceQueryService;
    }

    /**
     * {@code POST  /services} : Create a new service.
     *
     * @param serviceDTO the serviceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new serviceDTO, or with status {@code 400 (Bad Request)} if the service has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ServiceDTO> createService(@Valid @RequestBody ServiceDTO serviceDTO) throws URISyntaxException {
        LOG.debug("REST request to save Service : {}", serviceDTO);
        if (serviceDTO.getId() != null) {
            throw new BadRequestAlertException("A new service cannot already have an ID", ENTITY_NAME, "idexists");
        }
        serviceDTO = serviceService.save(serviceDTO);
        return ResponseEntity.created(new URI("/api/services/" + serviceDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, serviceDTO.getId().toString()))
            .body(serviceDTO);
    }

    /**
     * {@code PUT  /services/:id} : Updates an existing service.
     *
     * @param id the id of the serviceDTO to save.
     * @param serviceDTO the serviceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated serviceDTO,
     * or with status {@code 400 (Bad Request)} if the serviceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the serviceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ServiceDTO> updateService(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ServiceDTO serviceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Service : {}, {}", id, serviceDTO);
        if (serviceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, serviceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!serviceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        serviceDTO = serviceService.update(serviceDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, serviceDTO.getId().toString()))
            .body(serviceDTO);
    }

    /**
     * {@code PATCH  /services/:id} : Partial updates given fields of an existing service, field will ignore if it is null
     *
     * @param id the id of the serviceDTO to save.
     * @param serviceDTO the serviceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated serviceDTO,
     * or with status {@code 400 (Bad Request)} if the serviceDTO is not valid,
     * or with status {@code 404 (Not Found)} if the serviceDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the serviceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ServiceDTO> partialUpdateService(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ServiceDTO serviceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Service partially : {}, {}", id, serviceDTO);
        if (serviceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, serviceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!serviceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ServiceDTO> result = serviceService.partialUpdate(serviceDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, serviceDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /services} : get all the services.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of services in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ServiceDTO>> getAllServices(
        ServiceCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Services by criteria: {}", criteria);

        Page<ServiceDTO> page = serviceQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /services/count} : count all the services.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countServices(ServiceCriteria criteria) {
        LOG.debug("REST request to count Services by criteria: {}", criteria);
        return ResponseEntity.ok().body(serviceQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /services/:id} : get the "id" service.
     *
     * @param id the id of the serviceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the serviceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ServiceDTO> getService(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Service : {}", id);
        Optional<ServiceDTO> serviceDTO = serviceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(serviceDTO);
    }

    /**
     * {@code DELETE  /services/:id} : delete the "id" service.
     *
     * @param id the id of the serviceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Service : {}", id);
        serviceService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /services/_search?query=:query} : search for the service corresponding
     * to the query.
     *
     * @param query the query of the service search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<ServiceDTO>> searchServices(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of Services for query {}", query);
        try {
            Page<ServiceDTO> page = serviceService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
