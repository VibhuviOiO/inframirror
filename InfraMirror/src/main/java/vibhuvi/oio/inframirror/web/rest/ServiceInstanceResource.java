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
import vibhuvi.oio.inframirror.repository.ServiceInstanceRepository;
import vibhuvi.oio.inframirror.service.ServiceInstanceService;
import vibhuvi.oio.inframirror.service.dto.ServiceInstanceDTO;
import vibhuvi.oio.inframirror.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link vibhuvi.oio.inframirror.domain.ServiceInstance}.
 */
@RestController
@RequestMapping("/api/service-instances")
public class ServiceInstanceResource {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceInstanceResource.class);

    private static final String ENTITY_NAME = "serviceInstance";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ServiceInstanceService serviceInstanceService;

    private final ServiceInstanceRepository serviceInstanceRepository;

    public ServiceInstanceResource(ServiceInstanceService serviceInstanceService, ServiceInstanceRepository serviceInstanceRepository) {
        this.serviceInstanceService = serviceInstanceService;
        this.serviceInstanceRepository = serviceInstanceRepository;
    }

    /**
     * {@code POST  /service-instances} : Create a new serviceInstance.
     *
     * @param serviceInstanceDTO the serviceInstanceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new serviceInstanceDTO, or with status {@code 400 (Bad Request)} if the serviceInstance has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ServiceInstanceDTO> createServiceInstance(@Valid @RequestBody ServiceInstanceDTO serviceInstanceDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ServiceInstance");
        if (serviceInstanceDTO.getId() != null) {
            throw new BadRequestAlertException("A new serviceInstance cannot already have an ID", ENTITY_NAME, "idexists");
        }
        serviceInstanceDTO = serviceInstanceService.save(serviceInstanceDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, serviceInstanceDTO.getId().toString()))
            .body(serviceInstanceDTO);
    }

    /**
     * {@code PUT  /service-instances/:id} : Updates an existing serviceInstance.
     *
     * @param id the id of the serviceInstanceDTO to save.
     * @param serviceInstanceDTO the serviceInstanceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated serviceInstanceDTO,
     * or with status {@code 400 (Bad Request)} if the serviceInstanceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the serviceInstanceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ServiceInstanceDTO> updateServiceInstance(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ServiceInstanceDTO serviceInstanceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ServiceInstance : {}", String.valueOf(id).replaceAll("[\\r\\n]", ""));
        if (serviceInstanceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, serviceInstanceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!serviceInstanceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        serviceInstanceDTO = serviceInstanceService.update(serviceInstanceDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, serviceInstanceDTO.getId().toString()))
            .body(serviceInstanceDTO);
    }

    /**
     * {@code PATCH  /service-instances/:id} : Partial updates given fields of an existing serviceInstance, field will ignore if it is null
     *
     * @param id the id of the serviceInstanceDTO to save.
     * @param serviceInstanceDTO the serviceInstanceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated serviceInstanceDTO,
     * or with status {@code 400 (Bad Request)} if the serviceInstanceDTO is not valid,
     * or with status {@code 404 (Not Found)} if the serviceInstanceDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the serviceInstanceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ServiceInstanceDTO> partialUpdateServiceInstance(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ServiceInstanceDTO serviceInstanceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ServiceInstance partially : {}", String.valueOf(id).replaceAll("[\\r\\n]", ""));
        if (serviceInstanceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, serviceInstanceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!serviceInstanceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ServiceInstanceDTO> result = serviceInstanceService.partialUpdate(serviceInstanceDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, serviceInstanceDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /service-instances} : get all the serviceInstances.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of serviceInstances in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ServiceInstanceDTO>> getAllServiceInstances(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of entities");
        Page<ServiceInstanceDTO> page = serviceInstanceService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /service-instances/:id} : get the "id" serviceInstance.
     *
     * @param id the id of the serviceInstanceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the serviceInstanceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ServiceInstanceDTO> getServiceInstance(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ServiceInstance : {}", String.valueOf(id).replaceAll("[\\r\\n]", ""));
        Optional<ServiceInstanceDTO> serviceInstanceDTO = serviceInstanceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(serviceInstanceDTO);
    }

    /**
     * {@code DELETE  /service-instances/:id} : delete the "id" serviceInstance.
     *
     * @param id the id of the serviceInstanceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServiceInstance(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ServiceInstance : {}", String.valueOf(id).replaceAll("[\\r\\n]", ""));
        serviceInstanceService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /service-instances/_search?query=:query} : search for the serviceInstance corresponding
     * to the query.
     *
     * @param query the query of the serviceInstance search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<ServiceInstanceDTO>> searchServiceInstances(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of ServiceInstances for query: {}", query != null ? query.replaceAll("[\\r\\n]", "") : null);
        Page<ServiceInstanceDTO> page = serviceInstanceService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
