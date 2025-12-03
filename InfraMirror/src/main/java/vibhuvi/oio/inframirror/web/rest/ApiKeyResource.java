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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;
import vibhuvi.oio.inframirror.repository.ApiKeyRepository;
import vibhuvi.oio.inframirror.service.ApiKeyQueryService;
import vibhuvi.oio.inframirror.service.ApiKeyService;
import vibhuvi.oio.inframirror.service.criteria.ApiKeyCriteria;
import vibhuvi.oio.inframirror.service.dto.ApiKeyDTO;
import vibhuvi.oio.inframirror.web.rest.errors.BadRequestAlertException;
import vibhuvi.oio.inframirror.web.rest.errors.ElasticsearchExceptionMapper;

/**
 * REST controller for managing {@link vibhuvi.oio.inframirror.domain.ApiKey}.
 */
@RestController
@RequestMapping("/api/api-keys")
public class ApiKeyResource {

    private static final Logger LOG = LoggerFactory.getLogger(ApiKeyResource.class);

    private static final String ENTITY_NAME = "apiKey";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ApiKeyService apiKeyService;

    private final ApiKeyRepository apiKeyRepository;

    private final ApiKeyQueryService apiKeyQueryService;

    public ApiKeyResource(ApiKeyService apiKeyService, ApiKeyRepository apiKeyRepository, ApiKeyQueryService apiKeyQueryService) {
        this.apiKeyService = apiKeyService;
        this.apiKeyRepository = apiKeyRepository;
        this.apiKeyQueryService = apiKeyQueryService;
    }

    /**
     * {@code POST  /api-keys} : Create a new apiKey.
     *
     * @param apiKeyDTO the apiKeyDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new apiKeyDTO, or with status {@code 400 (Bad Request)} if the apiKey has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ApiKeyDTO> createApiKey(@Valid @RequestBody ApiKeyDTO apiKeyDTO) throws URISyntaxException {
        LOG.debug("REST request to save ApiKey : {}", apiKeyDTO);
        if (apiKeyDTO.getId() != null) {
            throw new BadRequestAlertException("A new apiKey cannot already have an ID", ENTITY_NAME, "idexists");
        }
        apiKeyDTO = apiKeyService.save(apiKeyDTO);
        return ResponseEntity.created(new URI("/api/api-keys/" + apiKeyDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, apiKeyDTO.getId().toString()))
            .body(apiKeyDTO);
    }

    /**
     * {@code PUT  /api-keys/:id} : Updates an existing apiKey.
     *
     * @param id the id of the apiKeyDTO to save.
     * @param apiKeyDTO the apiKeyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated apiKeyDTO,
     * or with status {@code 400 (Bad Request)} if the apiKeyDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the apiKeyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiKeyDTO> updateApiKey(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ApiKeyDTO apiKeyDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ApiKey : {}, {}", id, apiKeyDTO);
        if (apiKeyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, apiKeyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!apiKeyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        apiKeyDTO = apiKeyService.update(apiKeyDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, apiKeyDTO.getId().toString()))
            .body(apiKeyDTO);
    }

    /**
     * {@code PATCH  /api-keys/:id} : Partial updates given fields of an existing apiKey, field will ignore if it is null
     *
     * @param id the id of the apiKeyDTO to save.
     * @param apiKeyDTO the apiKeyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated apiKeyDTO,
     * or with status {@code 400 (Bad Request)} if the apiKeyDTO is not valid,
     * or with status {@code 404 (Not Found)} if the apiKeyDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the apiKeyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ApiKeyDTO> partialUpdateApiKey(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ApiKeyDTO apiKeyDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ApiKey partially : {}, {}", id, apiKeyDTO);
        if (apiKeyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, apiKeyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!apiKeyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ApiKeyDTO> result = apiKeyService.partialUpdate(apiKeyDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, apiKeyDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /api-keys} : get all the apiKeys.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of apiKeys in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ApiKeyDTO>> getAllApiKeys(ApiKeyCriteria criteria) {
        LOG.debug("REST request to get ApiKeys by criteria: {}", criteria);

        List<ApiKeyDTO> entityList = apiKeyQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /api-keys/count} : count all the apiKeys.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countApiKeys(ApiKeyCriteria criteria) {
        LOG.debug("REST request to count ApiKeys by criteria: {}", criteria);
        return ResponseEntity.ok().body(apiKeyQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /api-keys/:id} : get the "id" apiKey.
     *
     * @param id the id of the apiKeyDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the apiKeyDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiKeyDTO> getApiKey(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ApiKey : {}", id);
        Optional<ApiKeyDTO> apiKeyDTO = apiKeyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(apiKeyDTO);
    }

    /**
     * {@code DELETE  /api-keys/:id} : delete the "id" apiKey.
     *
     * @param id the id of the apiKeyDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApiKey(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ApiKey : {}", id);
        apiKeyService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /api-keys/_search?query=:query} : search for the apiKey corresponding
     * to the query.
     *
     * @param query the query of the apiKey search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<ApiKeyDTO> searchApiKeys(@RequestParam("query") String query) {
        LOG.debug("REST request to search ApiKeys for query {}", query);
        try {
            return apiKeyService.search(query);
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
