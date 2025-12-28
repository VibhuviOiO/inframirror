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
import vibhuvi.oio.inframirror.repository.BrandingRepository;
import vibhuvi.oio.inframirror.service.BrandingService;
import vibhuvi.oio.inframirror.service.dto.BrandingDTO;
import vibhuvi.oio.inframirror.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link vibhuvi.oio.inframirror.domain.Branding}.
 */
@RestController
@RequestMapping("/api/brandings")
public class BrandingResource {

    private static final Logger LOG = LoggerFactory.getLogger(BrandingResource.class);

    private static final String ENTITY_NAME = "branding";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BrandingService brandingService;

    private final BrandingRepository brandingRepository;

    public BrandingResource(BrandingService brandingService, BrandingRepository brandingRepository) {
        this.brandingService = brandingService;
        this.brandingRepository = brandingRepository;
    }

    /**
     * {@code POST  /brandings} : Create a new branding.
     *
     * @param brandingDTO the brandingDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new brandingDTO, or with status {@code 400 (Bad Request)} if the branding has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<BrandingDTO> createBranding(@Valid @RequestBody BrandingDTO brandingDTO) throws URISyntaxException {
        LOG.debug("REST request to save Branding");
        if (brandingDTO.getId() != null) {
            throw new BadRequestAlertException("A new branding cannot already have an ID", ENTITY_NAME, "idexists");
        }
        brandingDTO = brandingService.save(brandingDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, brandingDTO.getId().toString()))
            .body(brandingDTO);
    }

    /**
     * {@code PUT  /brandings/:id} : Updates an existing branding.
     *
     * @param id the id of the brandingDTO to save.
     * @param brandingDTO the brandingDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated brandingDTO,
     * or with status {@code 400 (Bad Request)} if the brandingDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the brandingDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BrandingDTO> updateBranding(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BrandingDTO brandingDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Branding  : {}", String.valueOf(id).replaceAll("[\r\n]", ""));
        if (brandingDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, brandingDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!brandingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        brandingDTO = brandingService.update(brandingDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, brandingDTO.getId().toString()))
            .body(brandingDTO);
    }

    /**
     * {@code PATCH  /brandings/:id} : Partial updates given fields of an existing branding, field will ignore if it is null
     *
     * @param id the id of the brandingDTO to save.
     * @param brandingDTO the brandingDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated brandingDTO,
     * or with status {@code 400 (Bad Request)} if the brandingDTO is not valid,
     * or with status {@code 404 (Not Found)} if the brandingDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the brandingDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BrandingDTO> partialUpdateBranding(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BrandingDTO brandingDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Branding partially  : {}", String.valueOf(id).replaceAll("[\r\n]", ""));
        if (brandingDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, brandingDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!brandingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BrandingDTO> result = brandingService.partialUpdate(brandingDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, brandingDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /brandings} : get all the brandings.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of brandings in body.
     */
    @GetMapping("")
    public ResponseEntity<List<BrandingDTO>> getAllBrandings(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of entities");
        Page<BrandingDTO> page = brandingService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /brandings/:id} : get the "id" branding.
     *
     * @param id the id of the brandingDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the brandingDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BrandingDTO> getBranding(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Branding  : {}", String.valueOf(id).replaceAll("[\r\n]", ""));
        Optional<BrandingDTO> brandingDTO = brandingService.findOne(id);
        return ResponseUtil.wrapOrNotFound(brandingDTO);
    }

    /**
     * {@code DELETE  /brandings/:id} : delete the "id" branding.
     *
     * @param id the id of the brandingDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBranding(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Branding  : {}", String.valueOf(id).replaceAll("[\r\n]", ""));
        brandingService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /brandings/_search?query=:query} : search for the branding corresponding
     * to the query.
     *
     * @param query the query of the branding search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<BrandingDTO>> searchBrandings(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of Brandings for for query: {}", query != null ? query.replaceAll("[\r\n]", "") : null);
            Page<BrandingDTO> page = brandingService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        }
}
