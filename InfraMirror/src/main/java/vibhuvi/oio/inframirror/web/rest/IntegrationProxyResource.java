package vibhuvi.oio.inframirror.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vibhuvi.oio.inframirror.service.IntegrationProxyService;

import java.util.List;
import java.util.Map;

/**
 * REST controller for proxying API calls to external integrations.
 */
@RestController
@RequestMapping("/api/icc/proxy")
public class IntegrationProxyResource {

    private static final Logger LOG = LoggerFactory.getLogger(IntegrationProxyResource.class);

    private final IntegrationProxyService integrationProxyService;

    public IntegrationProxyResource(IntegrationProxyService integrationProxyService) {
        this.integrationProxyService = integrationProxyService;
    }

    /**
     * GET /api/icc/proxy/{integrationCode}/{resourceName} : Fetch merged data from all instances.
     *
     * @param integrationCode the integration code (e.g., MARATHON, K8S)
     * @param resourceName the resource name (e.g., apps, pods)
     * @return the merged data with view configuration
     */
    @GetMapping("/{integrationCode}/{resourceName}")
    public ResponseEntity<Map<String, Object>> fetchMergedData(
        @PathVariable String integrationCode,
        @PathVariable String resourceName
    ) {
        LOG.debug("REST request to fetch merged data for integration: {}, resource: {}", integrationCode, resourceName);
        Map<String, Object> result = integrationProxyService.fetchMergedDataWithView(integrationCode, resourceName);
        return ResponseEntity.ok(result);
    }
}
