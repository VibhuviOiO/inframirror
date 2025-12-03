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
import vibhuvi.oio.inframirror.service.HttpHeartbeatQueryService;
import vibhuvi.oio.inframirror.service.HttpHeartbeatService;
import vibhuvi.oio.inframirror.service.criteria.HttpHeartbeatCriteria;
import vibhuvi.oio.inframirror.service.dto.HttpHeartbeatDTO;
import vibhuvi.oio.inframirror.web.rest.errors.ElasticsearchExceptionMapper;

/**
 * REST controller for managing {@link vibhuvi.oio.inframirror.domain.HttpHeartbeat}.
 */
@RestController
@RequestMapping("/api/http-heartbeats")
public class HttpHeartbeatResource {

    private static final Logger LOG = LoggerFactory.getLogger(HttpHeartbeatResource.class);

    private final HttpHeartbeatService httpHeartbeatService;

    private final HttpHeartbeatQueryService httpHeartbeatQueryService;

    public HttpHeartbeatResource(HttpHeartbeatService httpHeartbeatService, HttpHeartbeatQueryService httpHeartbeatQueryService) {
        this.httpHeartbeatService = httpHeartbeatService;
        this.httpHeartbeatQueryService = httpHeartbeatQueryService;
    }

    /**
     * {@code GET  /http-heartbeats} : get all the httpHeartbeats.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of httpHeartbeats in body.
     */
    @GetMapping("")
    public ResponseEntity<List<HttpHeartbeatDTO>> getAllHttpHeartbeats(
        HttpHeartbeatCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get HttpHeartbeats by criteria: {}", criteria);

        Page<HttpHeartbeatDTO> page = httpHeartbeatQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /http-heartbeats/count} : count all the httpHeartbeats.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countHttpHeartbeats(HttpHeartbeatCriteria criteria) {
        LOG.debug("REST request to count HttpHeartbeats by criteria: {}", criteria);
        return ResponseEntity.ok().body(httpHeartbeatQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /http-heartbeats/:id} : get the "id" httpHeartbeat.
     *
     * @param id the id of the httpHeartbeatDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the httpHeartbeatDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<HttpHeartbeatDTO> getHttpHeartbeat(@PathVariable("id") Long id) {
        LOG.debug("REST request to get HttpHeartbeat : {}", id);
        Optional<HttpHeartbeatDTO> httpHeartbeatDTO = httpHeartbeatService.findOne(id);
        return ResponseUtil.wrapOrNotFound(httpHeartbeatDTO);
    }

    /**
     * {@code SEARCH  /http-heartbeats/_search?query=:query} : search for the httpHeartbeat corresponding
     * to the query.
     *
     * @param query the query of the httpHeartbeat search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<HttpHeartbeatDTO>> searchHttpHeartbeats(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of HttpHeartbeats for query {}", query);
        try {
            Page<HttpHeartbeatDTO> page = httpHeartbeatService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
