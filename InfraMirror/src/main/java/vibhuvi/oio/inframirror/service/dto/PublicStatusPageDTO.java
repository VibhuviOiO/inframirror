package vibhuvi.oio.inframirror.service.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class PublicStatusPageDTO implements Serializable {

    private String name;
    private String description;
    private String slug;
    private String logoUrl;
    private String themeColor;
    private String headerText;
    private String footerText;
    private Boolean showResponseTimes;
    private Boolean showUptimePercentage;
    private Integer autoRefreshSeconds;
    private List<String> regions;
    private List<MonitorStatus> monitors;

    public static class RegionHealth implements Serializable {
        private String status;
        private Integer responseTimeMs;
        private String agentName;
        private Integer successRate;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Integer getResponseTimeMs() {
            return responseTimeMs;
        }

        public void setResponseTimeMs(Integer responseTimeMs) {
            this.responseTimeMs = responseTimeMs;
        }

        public String getAgentName() {
            return agentName;
        }

        public void setAgentName(String agentName) {
            this.agentName = agentName;
        }

        public Integer getSuccessRate() {
            return successRate;
        }

        public void setSuccessRate(Integer successRate) {
            this.successRate = successRate;
        }
    }

    public static class MonitorStatus implements Serializable {
        private Long monitorId;
        private String monitorName;
        private String url;
        private Map<String, RegionHealth> regionHealth;

        public Long getMonitorId() {
            return monitorId;
        }

        public void setMonitorId(Long monitorId) {
            this.monitorId = monitorId;
        }

        public String getMonitorName() {
            return monitorName;
        }

        public void setMonitorName(String monitorName) {
            this.monitorName = monitorName;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Map<String, RegionHealth> getRegionHealth() {
            return regionHealth;
        }

        public void setRegionHealth(Map<String, RegionHealth> regionHealth) {
            this.regionHealth = regionHealth;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getThemeColor() {
        return themeColor;
    }

    public void setThemeColor(String themeColor) {
        this.themeColor = themeColor;
    }

    public String getHeaderText() {
        return headerText;
    }

    public void setHeaderText(String headerText) {
        this.headerText = headerText;
    }

    public String getFooterText() {
        return footerText;
    }

    public void setFooterText(String footerText) {
        this.footerText = footerText;
    }

    public Boolean getShowResponseTimes() {
        return showResponseTimes;
    }

    public void setShowResponseTimes(Boolean showResponseTimes) {
        this.showResponseTimes = showResponseTimes;
    }

    public Boolean getShowUptimePercentage() {
        return showUptimePercentage;
    }

    public void setShowUptimePercentage(Boolean showUptimePercentage) {
        this.showUptimePercentage = showUptimePercentage;
    }

    public Integer getAutoRefreshSeconds() {
        return autoRefreshSeconds;
    }

    public void setAutoRefreshSeconds(Integer autoRefreshSeconds) {
        this.autoRefreshSeconds = autoRefreshSeconds;
    }

    public List<String> getRegions() {
        return regions;
    }

    public void setRegions(List<String> regions) {
        this.regions = regions;
    }

    public List<MonitorStatus> getMonitors() {
        return monitors;
    }

    public void setMonitors(List<MonitorStatus> monitors) {
        this.monitors = monitors;
    }
}
