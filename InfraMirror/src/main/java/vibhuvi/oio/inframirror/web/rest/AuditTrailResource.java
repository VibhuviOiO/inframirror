package vibhuvi.oio.inframirror.web.rest;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;
import vibhuvi.oio.inframirror.service.AuditTrailQueryService;
import vibhuvi.oio.inframirror.service.AuditTrailService;
import vibhuvi.oio.inframirror.service.criteria.AuditTrailCriteria;
import vibhuvi.oio.inframirror.service.dto.AuditTrailDTO;
import vibhuvi.oio.inframirror.web.rest.errors.ElasticsearchExceptionMapper;

/**
 * REST controller for managing {@link vibhuvi.oio.inframirror.domain.AuditTrail}.
 */
@RestController
@RequestMapping("/api/audit-trails")
public class AuditTrailResource {

    private static final Logger LOG = LoggerFactory.getLogger(AuditTrailResource.class);

    private final AuditTrailService auditTrailService;

    private final AuditTrailQueryService auditTrailQueryService;

    public AuditTrailResource(AuditTrailService auditTrailService, AuditTrailQueryService auditTrailQueryService) {
        this.auditTrailService = auditTrailService;
        this.auditTrailQueryService = auditTrailQueryService;
    }

    /**
     * {@code GET  /audit-trails} : get all the auditTrails.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of auditTrails in body.
     */
    @GetMapping("")
    public ResponseEntity<List<AuditTrailDTO>> getAllAuditTrails(
        AuditTrailCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get AuditTrails by criteria: {}", criteria);

        Page<AuditTrailDTO> page = auditTrailQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /audit-trails/count} : count all the auditTrails.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countAuditTrails(AuditTrailCriteria criteria) {
        LOG.debug("REST request to count AuditTrails by criteria: {}", criteria);
        return ResponseEntity.ok().body(auditTrailQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /audit-trails/:id} : get the "id" auditTrail.
     *
     * @param id the id of the auditTrailDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the auditTrailDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AuditTrailDTO> getAuditTrail(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AuditTrail : {}", id);
        Optional<AuditTrailDTO> auditTrailDTO = auditTrailService.findOne(id);
        return ResponseUtil.wrapOrNotFound(auditTrailDTO);
    }

    /**
     * {@code SEARCH  /audit-trails/_search?query=:query} : search for the auditTrail corresponding
     * to the query.
     *
     * @param query the query of the auditTrail search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<AuditTrailDTO>> searchAuditTrails(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of AuditTrails for query {}", query);
        try {
            Page<AuditTrailDTO> page = auditTrailService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
