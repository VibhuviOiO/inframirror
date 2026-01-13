package vibhuvi.oio.inframirror.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import vibhuvi.oio.inframirror.domain.IntegrationInstance;
import vibhuvi.oio.inframirror.domain.IntegrationResource;
import vibhuvi.oio.inframirror.repository.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Service for proxying API calls to external integrations.
 */
@Service
public class IntegrationProxyService {

    private static final Logger LOG = LoggerFactory.getLogger(IntegrationProxyService.class);

    private final IntegrationInstanceRepository integrationInstanceRepository;
    private final IntegrationResourceRepository integrationResourceRepository;
    private final IntegrationViewRepository integrationViewRepository;
    private final MonitoredServiceRepository monitoredServiceRepository;
    private final HttpMonitorRepository httpMonitorRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public IntegrationProxyService(
        IntegrationInstanceRepository integrationInstanceRepository,
        IntegrationResourceRepository integrationResourceRepository,
        IntegrationViewRepository integrationViewRepository,
        MonitoredServiceRepository monitoredServiceRepository,
        HttpMonitorRepository httpMonitorRepository,
        ObjectMapper objectMapper
    ) {
        this.integrationInstanceRepository = integrationInstanceRepository;
        this.integrationResourceRepository = integrationResourceRepository;
        this.integrationViewRepository = integrationViewRepository;
        this.monitoredServiceRepository = monitoredServiceRepository;
        this.httpMonitorRepository = httpMonitorRepository;
        this.restTemplate = new RestTemplate();
        this.objectMapper = objectMapper;
    }

    /**
     * Fetch data from all instances of an integration and merge results.
     */
    public List<Map<String, Object>> fetchMergedData(String integrationCode, String resourceName) {
        LOG.debug("Fetching merged data for integration: {}, resource: {}", integrationCode, resourceName);

        List<IntegrationInstance> instances = integrationInstanceRepository.findByControlIntegrationCode(integrationCode);
        if (instances.isEmpty()) {
            LOG.warn("No instances found for integration code: {}", integrationCode);
            return Collections.emptyList();
        }

        LOG.debug("Found {} instances for integration {}", instances.size(), integrationCode);

        IntegrationResource resource = integrationResourceRepository
            .findByControlIntegrationIdAndName(instances.get(0).getControlIntegration().getId(), resourceName)
            .orElseThrow(() -> new RuntimeException("Resource not found: " + resourceName));

        LOG.debug("Resource found: {}, API path: {}, Response path: {}", resource.getName(), resource.getApiPath(), resource.getResponsePath());

        List<CompletableFuture<List<Map<String, Object>>>> futures = instances.stream()
            .map(instance -> CompletableFuture.supplyAsync(() -> fetchDataFromInstance(instance, resource)))
            .collect(Collectors.toList());

        List<Map<String, Object>> results = futures.stream()
            .map(CompletableFuture::join)
            .flatMap(List::stream)
            .collect(Collectors.toList());

        LOG.debug("Total results fetched: {}", results.size());
        return results;
    }

    /**
     * Fetch data with view configuration.
     */
    public Map<String, Object> fetchMergedDataWithView(String integrationCode, String resourceName) {
        List<IntegrationInstance> instances = integrationInstanceRepository.findByControlIntegrationCode(integrationCode);
        if (instances.isEmpty()) {
            return Map.of("data", Collections.emptyList());
        }

        IntegrationResource resource = integrationResourceRepository
            .findByControlIntegrationIdAndName(instances.get(0).getControlIntegration().getId(), resourceName)
            .orElseThrow(() -> new RuntimeException("Resource not found: " + resourceName));

        List<Map<String, Object>> data = fetchMergedData(integrationCode, resourceName);
        
        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        
        integrationViewRepository.findByIntegrationResourceIdAndIsDefaultTrue(resource.getId())
            .ifPresent(view -> result.put("view", view.getConfig()));
        
        return result;
    }

    /**
     * Fetch data from a single instance.
     */
    private List<Map<String, Object>> fetchDataFromInstance(IntegrationInstance instance, IntegrationResource resource) {
        String baseUrls = resolveBaseUrl(instance);
        LOG.debug("Base URLs for instance {}: {}", instance.getName(), baseUrls);
        String[] urls = baseUrls.split(",");
        
        for (String baseUrl : urls) {
            try {
                String url = baseUrl.trim() + resource.getApiPath();
                LOG.debug("Calling API: {}", url);

                HttpHeaders headers = buildHeaders(instance);
                HttpEntity<String> entity = new HttpEntity<>(headers);

                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
                LOG.debug("Response status: {}, body length: {}", response.getStatusCode(), response.getBody() != null ? response.getBody().length() : 0);

                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    return extractData(response.getBody(), resource, instance);
                }
            } catch (Exception e) {
                LOG.warn("Failed to fetch from {}: {}", baseUrl.trim(), e.getMessage());
            }
        }
        
        LOG.error("All URLs failed for instance {}", instance.getName());
        return Collections.emptyList();
    }

    /**
     * Resolve base URL from instance configuration.
     */
    private String resolveBaseUrl(IntegrationInstance instance) {
        if ("MONITORED_SERVICE".equals(instance.getInstanceType())) {
            return monitoredServiceRepository.findById(instance.getMonitoredService().getId())
                .map(service -> service.getAdvancedConfig())
                .orElseThrow(() -> new RuntimeException("MonitoredService not found"));
        } else if ("HTTP_MONITOR".equals(instance.getInstanceType())) {
            return httpMonitorRepository.findById(instance.getHttpMonitor().getId())
                .map(monitor -> extractBaseUrl(monitor.getUrl()))
                .orElseThrow(() -> new RuntimeException("HttpMonitor not found"));
        } else {
            return instance.getBaseUrl();
        }
    }

    /**
     * Extract base URL from full URL.
     */
    private String extractBaseUrl(String fullUrl) {
        try {
            java.net.URL url = new java.net.URL(fullUrl);
            return url.getProtocol() + "://" + url.getHost() + (url.getPort() > 0 ? ":" + url.getPort() : "");
        } catch (Exception e) {
            return fullUrl;
        }
    }

    /**
     * Build HTTP headers with authentication.
     */
    private HttpHeaders buildHeaders(IntegrationInstance instance) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (instance.getAuthType() != null && instance.getAuthConfig() != null) {
            switch (instance.getAuthType()) {
                case "BEARER":
                    String token = instance.getAuthConfig().get("token").asText();
                    headers.setBearerAuth(token);
                    break;
                case "BASIC":
                    String username = instance.getAuthConfig().get("username").asText();
                    String password = instance.getAuthConfig().get("password").asText();
                    headers.setBasicAuth(username, password);
                    break;
                case "API_KEY":
                    String headerName = instance.getAuthConfig().get("header").asText();
                    String value = instance.getAuthConfig().get("value").asText();
                    headers.set(headerName, value);
                    break;
            }
        }

        return headers;
    }

    /**
     * Extract data from JSON response using JSONPath.
     */
    private List<Map<String, Object>> extractData(String responseBody, IntegrationResource resource, IntegrationInstance instance) {
        try {
            LOG.debug("Extracting data with response path: {}", resource.getResponsePath());
            Object data;
            if (resource.getResponsePath() != null && !resource.getResponsePath().isEmpty()) {
                data = JsonPath.read(responseBody, resource.getResponsePath());
            } else {
                data = JsonPath.read(responseBody, "$");
            }

            LOG.debug("Extracted data type: {}", data != null ? data.getClass().getName() : "null");

            List<Map<String, Object>> results = new ArrayList<>();
            String datacenterName = "N/A";
            String instanceName = instance.getName();
            
            if (data instanceof List) {
                LOG.debug("Processing list with {} items", ((List<?>) data).size());
                for (Object item : (List<?>) data) {
                    Map<String, Object> map = objectMapper.convertValue(item, Map.class);
                    map.put("_datacenter", datacenterName);
                    map.put("_instance", instanceName);
                    results.add(map);
                }
            } else if (data instanceof Map) {
                LOG.debug("Processing single map");
                Map<String, Object> map = objectMapper.convertValue(data, Map.class);
                map.put("_datacenter", datacenterName);
                map.put("_instance", instanceName);
                results.add(map);
            }

            LOG.debug("Extracted {} results", results.size());
            return results;
        } catch (Exception e) {
            LOG.error("Error extracting data: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
