package vibhuvi.oio.inframirror.web.rest;

import java.math.BigDecimal;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vibhuvi.oio.inframirror.domain.*;
import vibhuvi.oio.inframirror.repository.*;
import vibhuvi.oio.inframirror.service.dto.PublicStatusPageDTO;
import vibhuvi.oio.inframirror.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for public status pages (no authentication required).
 */
@RestController
@RequestMapping("/public")
public class PublicStatusPageResource {

    private static final Logger LOG = LoggerFactory.getLogger(PublicStatusPageResource.class);
    private static final String ENTITY_NAME = "statusPage";

    private final StatusPageRepository statusPageRepository;
    private final StatusPageItemRepository statusPageItemRepository;
    private final HttpMonitorRepository httpMonitorRepository;
    private final HttpHeartbeatRepository httpHeartbeatRepository;
    private final AgentRepository agentRepository;
    private final StatusPageSettingsRepository statusPageSettingsRepository;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PublicStatusPageResource(
        StatusPageRepository statusPageRepository,
        StatusPageItemRepository statusPageItemRepository,
        HttpMonitorRepository httpMonitorRepository,
        HttpHeartbeatRepository httpHeartbeatRepository,
        AgentRepository agentRepository,
        StatusPageSettingsRepository statusPageSettingsRepository,
        NamedParameterJdbcTemplate jdbcTemplate
    ) {
        this.statusPageRepository = statusPageRepository;
        this.statusPageItemRepository = statusPageItemRepository;
        this.httpMonitorRepository = httpMonitorRepository;
        this.httpHeartbeatRepository = httpHeartbeatRepository;
        this.agentRepository = agentRepository;
        this.statusPageSettingsRepository = statusPageSettingsRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/status/{slug}")
    public ResponseEntity<PublicStatusPageDTO> getPublicStatusPage(@PathVariable String slug) {
        LOG.debug("Public request to get status page by slug : {}", slug);

        StatusPage statusPage = statusPageRepository
            .findBySlug(slug)
            .orElseThrow(() -> new BadRequestAlertException("Status page not found", ENTITY_NAME, "notfound"));

        if (!Boolean.TRUE.equals(statusPage.getIsPublic())) {
            throw new BadRequestAlertException("Status page is not public", ENTITY_NAME, "notpublic");
        }

        // Get settings or use defaults
        StatusPageSettings settings = statusPageSettingsRepository
            .findByStatusPageId(statusPage.getId())
            .orElse(createDefaultSettings());

        PublicStatusPageDTO dto = new PublicStatusPageDTO();
        dto.setName(statusPage.getName());
        dto.setDescription(statusPage.getDescription());
        dto.setSlug(statusPage.getSlug());
        dto.setLogoUrl(settings.getLogoUrl());
        dto.setThemeColor(settings.getThemeColor());
        dto.setHeaderText(settings.getHeaderText());
        dto.setFooterText(settings.getFooterText());
        dto.setShowResponseTimes(settings.getShowResponseTimes());
        dto.setShowUptimePercentage(settings.getShowUptimePercentage());
        dto.setAutoRefreshSeconds(settings.getAutoRefreshSeconds());

        // Get monitor IDs from status page items
        List<StatusPageItem> items = statusPageItemRepository.findByStatusPageIdOrderByDisplayOrderAsc(statusPage.getId());
        List<Long> monitorIds = items.stream()
            .filter(item -> "HTTP".equals(item.getItemType()) || "HTTP_MONITOR".equals(item.getItemType()))
            .map(StatusPageItem::getItemId)
            .toList();

        if (monitorIds.isEmpty()) {
            dto.setRegions(new ArrayList<>());
            dto.setMonitors(new ArrayList<>());
            return ResponseEntity.ok(dto);
        }

        // Single optimized query to get all data
        String sql = """
            WITH latest_heartbeats AS (
                SELECT
                    h.monitor_id,
                    a.id as agent_id,
                    a.name as agent_name,
                    h.success,
                    h.response_time_ms,
                    h.warning_threshold_ms,
                    h.critical_threshold_ms,
                    ROW_NUMBER() OVER (PARTITION BY h.monitor_id, a.id ORDER BY h.executed_at DESC) as rn
                FROM http_heartbeat h
                JOIN agent a ON h.agent_id = a.id
                WHERE h.monitor_id IN (:monitorIds)
            ),
            aggregated AS (
                SELECT
                    monitor_id,
                    agent_id,
                    agent_name,
                    COUNT(*) as total_calls,
                    SUM(CASE WHEN success THEN 1 ELSE 0 END) as successful_calls,
                    AVG(response_time_ms) as avg_response_time,
                    MIN(warning_threshold_ms) as warning_threshold,
                    MIN(critical_threshold_ms) as critical_threshold
                FROM latest_heartbeats
                WHERE rn <= :sampleSize
                GROUP BY monitor_id, agent_id, agent_name
            )
            SELECT
                m.id as monitor_id,
                m.name as monitor_name,
                m.url as monitor_url,
                a.agent_name,
                a.total_calls,
                a.successful_calls,
                a.avg_response_time,
                a.warning_threshold,
                a.critical_threshold
            FROM http_monitor m
            LEFT JOIN aggregated a ON a.monitor_id = m.id
            WHERE m.id IN (:monitorIds)
            ORDER BY m.id, a.agent_name
            """;

        List<Map<String, Object>> results = jdbcTemplate.queryForList(
            sql,
            Map.of(
                "monitorIds", monitorIds,
                "sampleSize", settings.getSampleSize()
            )
        );

        // Group results
        Map<Long, PublicStatusPageDTO.MonitorStatus> monitorsMap = new HashMap<>();
        Set<String> regions = new LinkedHashSet<>();

        for (Map<String, Object> row : results) {
            Long monitorId = ((Number) row.get("monitor_id")).longValue();
            String agentName = (String) row.get("agent_name");

            if (agentName != null) {
                regions.add(agentName);
            }

            PublicStatusPageDTO.MonitorStatus monitor = monitorsMap.computeIfAbsent(monitorId, id -> {
                PublicStatusPageDTO.MonitorStatus m = new PublicStatusPageDTO.MonitorStatus();
                m.setMonitorId(monitorId);
                m.setMonitorName((String) row.get("monitor_name"));
                m.setUrl((String) row.get("monitor_url"));
                m.setRegionHealth(new HashMap<>());
                return m;
            });

            if (agentName != null && row.get("total_calls") != null) {
                Integer totalCalls = ((Number) row.get("total_calls")).intValue();
                Integer successfulCalls = ((Number) row.get("successful_calls")).intValue();
                Double avgResponseTime = row.get("avg_response_time") != null 
                    ? ((Number) row.get("avg_response_time")).doubleValue() : 0.0;

                double successRate = (double) successfulCalls / totalCalls;
                int successRatePercent = (int) Math.round(successRate * 100);

                Integer warningThreshold = row.get("warning_threshold") != null
                    ? ((Number) row.get("warning_threshold")).intValue()
                    : settings.getWarningThresholdMs();
                Integer criticalThreshold = row.get("critical_threshold") != null
                    ? ((Number) row.get("critical_threshold")).intValue()
                    : settings.getCriticalThresholdMs();

                String status = calculateStatus(
                    successRate,
                    avgResponseTime,
                    settings.getSuccessThresholdLow().doubleValue(),
                    settings.getSuccessThresholdHigh().doubleValue(),
                    warningThreshold,
                    criticalThreshold
                );

                PublicStatusPageDTO.RegionHealth health = new PublicStatusPageDTO.RegionHealth();
                health.setStatus(status);
                health.setResponseTimeMs((int) Math.round(avgResponseTime));
                health.setAgentName(agentName);
                health.setSuccessRate(successRatePercent);

                monitor.getRegionHealth().put(agentName, health);
            }
        }

        dto.setRegions(new ArrayList<>(regions));
        dto.setMonitors(new ArrayList<>(monitorsMap.values()));
        return ResponseEntity.ok(dto);
    }

    private StatusPageSettings createDefaultSettings() {
        StatusPageSettings settings = new StatusPageSettings();
        settings.setSampleSize(20);
        settings.setSuccessThresholdHigh(new BigDecimal("0.80"));
        settings.setSuccessThresholdLow(new BigDecimal("0.60"));
        settings.setWarningThresholdMs(500);
        settings.setCriticalThresholdMs(1000);
        settings.setShowResponseTimes(true);
        settings.setShowUptimePercentage(true);
        return settings;
    }

    private String calculateStatus(
        double successRate,
        double avgLatency,
        double lowThreshold,
        double highThreshold,
        int warningMs,
        int criticalMs
    ) {
        if (successRate < lowThreshold) return "DOWN";
        if (successRate < highThreshold) return "WARNING";
        if (avgLatency > criticalMs) return "CRITICAL";
        if (avgLatency > warningMs) return "WARNING";
        return "UP";
    }
}
