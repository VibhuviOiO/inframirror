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
import vibhuvi.oio.inframirror.service.SessionLogQueryService;
import vibhuvi.oio.inframirror.service.SessionLogService;
import vibhuvi.oio.inframirror.service.criteria.SessionLogCriteria;
import vibhuvi.oio.inframirror.service.dto.SessionLogDTO;
import vibhuvi.oio.inframirror.web.rest.errors.ElasticsearchExceptionMapper;

/**
 * REST controller for managing {@link vibhuvi.oio.inframirror.domain.SessionLog}.
 */
@RestController
@RequestMapping("/api/session-logs")
public class SessionLogResource {

    private static final Logger LOG = LoggerFactory.getLogger(SessionLogResource.class);

    private final SessionLogService sessionLogService;

    private final SessionLogQueryService sessionLogQueryService;

    public SessionLogResource(SessionLogService sessionLogService, SessionLogQueryService sessionLogQueryService) {
        this.sessionLogService = sessionLogService;
        this.sessionLogQueryService = sessionLogQueryService;
    }

    /**
     * {@code GET  /session-logs} : get all the sessionLogs.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sessionLogs in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SessionLogDTO>> getAllSessionLogs(
        SessionLogCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get SessionLogs by criteria: {}", criteria);

        Page<SessionLogDTO> page = sessionLogQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /session-logs/count} : count all the sessionLogs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countSessionLogs(SessionLogCriteria criteria) {
        LOG.debug("REST request to count SessionLogs by criteria: {}", criteria);
        return ResponseEntity.ok().body(sessionLogQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /session-logs/:id} : get the "id" sessionLog.
     *
     * @param id the id of the sessionLogDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sessionLogDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SessionLogDTO> getSessionLog(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SessionLog : {}", id);
        Optional<SessionLogDTO> sessionLogDTO = sessionLogService.findOne(id);
        return ResponseUtil.wrapOrNotFound(sessionLogDTO);
    }

    /**
     * {@code SEARCH  /session-logs/_search?query=:query} : search for the sessionLog corresponding
     * to the query.
     *
     * @param query the query of the sessionLog search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<SessionLogDTO>> searchSessionLogs(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of SessionLogs for query {}", query);
        try {
            Page<SessionLogDTO> page = sessionLogService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
