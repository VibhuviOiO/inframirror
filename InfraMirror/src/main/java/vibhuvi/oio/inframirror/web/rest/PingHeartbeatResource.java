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
import vibhuvi.oio.inframirror.service.PingHeartbeatQueryService;
import vibhuvi.oio.inframirror.service.PingHeartbeatService;
import vibhuvi.oio.inframirror.service.criteria.PingHeartbeatCriteria;
import vibhuvi.oio.inframirror.service.dto.PingHeartbeatDTO;
import vibhuvi.oio.inframirror.web.rest.errors.ElasticsearchExceptionMapper;

/**
 * REST controller for managing {@link vibhuvi.oio.inframirror.domain.PingHeartbeat}.
 */
@RestController
@RequestMapping("/api/ping-heartbeats")
public class PingHeartbeatResource {

    private static final Logger LOG = LoggerFactory.getLogger(PingHeartbeatResource.class);

    private final PingHeartbeatService pingHeartbeatService;

    private final PingHeartbeatQueryService pingHeartbeatQueryService;

    public PingHeartbeatResource(PingHeartbeatService pingHeartbeatService, PingHeartbeatQueryService pingHeartbeatQueryService) {
        this.pingHeartbeatService = pingHeartbeatService;
        this.pingHeartbeatQueryService = pingHeartbeatQueryService;
    }

    /**
     * {@code GET  /ping-heartbeats} : get all the pingHeartbeats.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pingHeartbeats in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PingHeartbeatDTO>> getAllPingHeartbeats(
        PingHeartbeatCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get PingHeartbeats by criteria: {}", criteria);

        Page<PingHeartbeatDTO> page = pingHeartbeatQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /ping-heartbeats/count} : count all the pingHeartbeats.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countPingHeartbeats(PingHeartbeatCriteria criteria) {
        LOG.debug("REST request to count PingHeartbeats by criteria: {}", criteria);
        return ResponseEntity.ok().body(pingHeartbeatQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /ping-heartbeats/:id} : get the "id" pingHeartbeat.
     *
     * @param id the id of the pingHeartbeatDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pingHeartbeatDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PingHeartbeatDTO> getPingHeartbeat(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PingHeartbeat : {}", id);
        Optional<PingHeartbeatDTO> pingHeartbeatDTO = pingHeartbeatService.findOne(id);
        return ResponseUtil.wrapOrNotFound(pingHeartbeatDTO);
    }

    /**
     * {@code SEARCH  /ping-heartbeats/_search?query=:query} : search for the pingHeartbeat corresponding
     * to the query.
     *
     * @param query the query of the pingHeartbeat search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<PingHeartbeatDTO>> searchPingHeartbeats(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of PingHeartbeats for query {}", query);
        try {
            Page<PingHeartbeatDTO> page = pingHeartbeatService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
