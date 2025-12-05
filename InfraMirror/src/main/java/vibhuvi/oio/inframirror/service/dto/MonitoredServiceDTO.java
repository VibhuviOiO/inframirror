package vibhuvi.oio.inframirror.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link vibhuvi.oio.inframirror.domain.MonitoredService} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MonitoredServiceDTO implements Serializable {

    @NotNull
    private Long id;

    @NotNull
    @Size(max = 200)
    private String name;

    @Size(max = 500)
    private String description;

    @NotNull
    @Size(max = 50)
    private String serviceType;

    @NotNull
    @Size(max = 20)
    private String environment;

    private Boolean monitoringEnabled;

    private Boolean clusterMonitoringEnabled;

    @NotNull
    private Integer intervalSeconds;

    @NotNull
    private Integer timeoutMs;

    @NotNull
    private Integer retryCount;

    private Integer latencyWarningMs;

    private Integer latencyCriticalMs;

    @Lob
    private String advancedConfig;

    private Boolean isActive;

    private Instant createdAt;

    private Instant updatedAt;

    private DatacenterDTO datacenter;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public Boolean getMonitoringEnabled() {
        return monitoringEnabled;
    }

    public void setMonitoringEnabled(Boolean monitoringEnabled) {
        this.monitoringEnabled = monitoringEnabled;
    }

    public Boolean getClusterMonitoringEnabled() {
        return clusterMonitoringEnabled;
    }

    public void setClusterMonitoringEnabled(Boolean clusterMonitoringEnabled) {
        this.clusterMonitoringEnabled = clusterMonitoringEnabled;
    }

    public Integer getIntervalSeconds() {
        return intervalSeconds;
    }

    public void setIntervalSeconds(Integer intervalSeconds) {
        this.intervalSeconds = intervalSeconds;
    }

    public Integer getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(Integer timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public Integer getLatencyWarningMs() {
        return latencyWarningMs;
    }

    public void setLatencyWarningMs(Integer latencyWarningMs) {
        this.latencyWarningMs = latencyWarningMs;
    }

    public Integer getLatencyCriticalMs() {
        return latencyCriticalMs;
    }

    public void setLatencyCriticalMs(Integer latencyCriticalMs) {
        this.latencyCriticalMs = latencyCriticalMs;
    }

    public String getAdvancedConfig() {
        return advancedConfig;
    }

    public void setAdvancedConfig(String advancedConfig) {
        this.advancedConfig = advancedConfig;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public DatacenterDTO getDatacenter() {
        return datacenter;
    }

    public void setDatacenter(DatacenterDTO datacenter) {
        this.datacenter = datacenter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MonitoredServiceDTO)) {
            return false;
        }

        MonitoredServiceDTO monitoredServiceDTO = (MonitoredServiceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, monitoredServiceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MonitoredServiceDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", serviceType='" + getServiceType() + "'" +
            ", environment='" + getEnvironment() + "'" +
            ", monitoringEnabled='" + getMonitoringEnabled() + "'" +
            ", clusterMonitoringEnabled='" + getClusterMonitoringEnabled() + "'" +
            ", intervalSeconds=" + getIntervalSeconds() +
            ", timeoutMs=" + getTimeoutMs() +
            ", retryCount=" + getRetryCount() +
            ", latencyWarningMs=" + getLatencyWarningMs() +
            ", latencyCriticalMs=" + getLatencyCriticalMs() +
            ", advancedConfig='" + getAdvancedConfig() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", datacenter=" + getDatacenter() +
            "}";
    }
}
