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
import vibhuvi.oio.inframirror.repository.InstanceRepository;
import vibhuvi.oio.inframirror.service.InstanceQueryService;
import vibhuvi.oio.inframirror.service.InstanceSearchService;
import vibhuvi.oio.inframirror.service.InstanceService;
import vibhuvi.oio.inframirror.service.criteria.InstanceCriteria;
import vibhuvi.oio.inframirror.service.dto.InstanceDTO;
import vibhuvi.oio.inframirror.service.dto.InstanceSearchResultDTO;
import vibhuvi.oio.inframirror.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link vibhuvi.oio.inframirror.domain.Instance}.
 */
@RestController
@RequestMapping("/api/instances")
public class InstanceResource {

    private static final Logger LOG = LoggerFactory.getLogger(InstanceResource.class);

    private static final String ENTITY_NAME = "instance";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InstanceService instanceService;
    private final InstanceSearchService instanceSearchService;
    private final InstanceRepository instanceRepository;
    private final InstanceQueryService instanceQueryService;

    public InstanceResource(
        InstanceService instanceService,
        InstanceSearchService instanceSearchService,
        InstanceRepository instanceRepository,
        InstanceQueryService instanceQueryService
    ) {
        this.instanceService = instanceService;
        this.instanceSearchService = instanceSearchService;
        this.instanceRepository = instanceRepository;
        this.instanceQueryService = instanceQueryService;
    }

    /**
     * {@code POST  /instances} : Create a new instance.
     *
     * @param instanceDTO the instanceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new instanceDTO, or with status {@code 400 (Bad Request)} if the instance has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<InstanceDTO> createInstance(@Valid @RequestBody InstanceDTO instanceDTO) throws URISyntaxException {
        LOG.debug("REST request to save Instance");
        if (instanceDTO.getId() != null) {
            throw new BadRequestAlertException("A new instance cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (instanceRepository.existsByNameIgnoreCase(instanceDTO.getName())) {
            throw new BadRequestAlertException("Instance with this name already exists", ENTITY_NAME, "nameexists");
        }
        instanceDTO = instanceService.save(instanceDTO);
        return ResponseEntity.created(new URI("/api/instances/" + instanceDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, instanceDTO.getId().toString()))
            .body(instanceDTO);
    }

    /**
     * {@code PUT  /instances/:id} : Updates an existing instance.
     *
     * @param id the id of the instanceDTO to save.
     * @param instanceDTO the instanceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated instanceDTO,
     * or with status {@code 400 (Bad Request)} if the instanceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the instanceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<InstanceDTO> updateInstance(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody InstanceDTO instanceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Instance  : {}", String.valueOf(id).replaceAll("[\r\n]", ""));
        if (instanceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, instanceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!instanceRepository.findById(id).isPresent()) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        instanceDTO = instanceService.update(instanceDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, instanceDTO.getId().toString()))
            .body(instanceDTO);
    }

    /**
     * {@code PATCH  /instances/:id} : Partial updates given fields of an existing instance, field will ignore if it is null
     *
     * @param id the id of the instanceDTO to save.
     * @param instanceDTO the instanceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated instanceDTO,
     * or with status {@code 400 (Bad Request)} if the instanceDTO is not valid,
     * or with status {@code 404 (Not Found)} if the instanceDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the instanceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<InstanceDTO> partialUpdateInstance(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody InstanceDTO instanceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Instance partially  : {}", String.valueOf(id).replaceAll("[\r\n]", ""));
        if (instanceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, instanceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!instanceRepository.findById(id).isPresent()) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<InstanceDTO> result = instanceService.partialUpdate(instanceDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, instanceDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /instances} : get all the instances.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of instances in body.
     */
    @GetMapping("")
    public ResponseEntity<List<InstanceDTO>> getAllInstances(
        InstanceCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Instances by criteria: {}", criteria);

        Page<InstanceDTO> page = instanceQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /instances/count} : count all the instances.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countInstances(InstanceCriteria criteria) {
        LOG.debug("REST request to count Instances by criteria: {}", criteria);
        return ResponseEntity.ok().body(instanceQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /instances/:id} : get the "id" instance.
     *
     * @param id the id of the instanceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the instanceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<InstanceDTO> getInstance(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Instance  : {}", String.valueOf(id).replaceAll("[\r\n]", ""));
        Optional<InstanceDTO> instanceDTO = instanceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(instanceDTO);
    }

    /**
     * {@code DELETE  /instances/:id} : delete the "id" instance.
     *
     * @param id the id of the instanceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInstance(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Instance  : {}", String.valueOf(id).replaceAll("[\r\n]", ""));
        instanceService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /instances/_search?query=:query} : search for the instance corresponding
     * to the query.
     *
     * @param query the query of the instance search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<InstanceDTO>> searchInstances(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of Instances for for query: {}", query != null ? query.replaceAll("[\r\n]", "") : null);
        Page<InstanceDTO> page = instanceSearchService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/_search/prefix")
    public ResponseEntity<List<InstanceDTO>> searchInstancesPrefix(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to prefix search Instances for for query: {}", query != null ? query.replaceAll("[\r\n]", "") : null);
        Page<InstanceDTO> page = instanceSearchService.searchPrefix(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/_search/fuzzy")
    public ResponseEntity<List<InstanceDTO>> searchInstancesFuzzy(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to fuzzy search Instances for for query: {}", query != null ? query.replaceAll("[\r\n]", "") : null);
        Page<InstanceDTO> page = instanceSearchService.searchFuzzy(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/_search/highlight")
    public ResponseEntity<List<InstanceSearchResultDTO>> searchInstancesWithHighlight(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search Instances with highlight for for query: {}", query != null ? query.replaceAll("[\r\n]", "") : null);
        Page<InstanceSearchResultDTO> page = instanceSearchService.searchWithHighlight(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
