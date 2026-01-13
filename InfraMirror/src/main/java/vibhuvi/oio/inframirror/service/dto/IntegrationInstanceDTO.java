package vibhuvi.oio.inframirror.service.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * DTO for {@link vibhuvi.oio.inframirror.domain.IntegrationInstance}
 */
public class IntegrationInstanceDTO implements Serializable {

    private Long id;

    @NotNull
    private Long controlIntegrationId;

    private String controlIntegrationName;

    @NotNull
    @Size(max = 150)
    private String name;

    @NotNull
    @Size(max = 30)
    private String instanceType;

    private Long monitoredServiceId;

    private String monitoredServiceName;

    private Long httpMonitorId;

    private String httpMonitorName;

    private String baseUrl;

    @Size(max = 30)
    private String authType;

    private JsonNode authConfig;

    @Size(max = 20)
    private String environment;

    private Long datacenterId;

    private String datacenterName;

    private Integer timeoutMs;

    private Boolean isActive;

    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getControlIntegrationId() {
        return controlIntegrationId;
    }

    public void setControlIntegrationId(Long controlIntegrationId) {
        this.controlIntegrationId = controlIntegrationId;
    }

    public String getControlIntegrationName() {
        return controlIntegrationName;
    }

    public void setControlIntegrationName(String controlIntegrationName) {
        this.controlIntegrationName = controlIntegrationName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public Long getMonitoredServiceId() {
        return monitoredServiceId;
    }

    public void setMonitoredServiceId(Long monitoredServiceId) {
        this.monitoredServiceId = monitoredServiceId;
    }

    public String getMonitoredServiceName() {
        return monitoredServiceName;
    }

    public void setMonitoredServiceName(String monitoredServiceName) {
        this.monitoredServiceName = monitoredServiceName;
    }

    public Long getHttpMonitorId() {
        return httpMonitorId;
    }

    public void setHttpMonitorId(Long httpMonitorId) {
        this.httpMonitorId = httpMonitorId;
    }

    public String getHttpMonitorName() {
        return httpMonitorName;
    }

    public void setHttpMonitorName(String httpMonitorName) {
        this.httpMonitorName = httpMonitorName;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public JsonNode getAuthConfig() {
        return authConfig;
    }

    public void setAuthConfig(JsonNode authConfig) {
        this.authConfig = authConfig;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public Long getDatacenterId() {
        return datacenterId;
    }

    public void setDatacenterId(Long datacenterId) {
        this.datacenterId = datacenterId;
    }

    public String getDatacenterName() {
        return datacenterName;
    }

    public void setDatacenterName(String datacenterName) {
        this.datacenterName = datacenterName;
    }

    public Integer getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(Integer timeoutMs) {
        this.timeoutMs = timeoutMs;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntegrationInstanceDTO)) return false;
        IntegrationInstanceDTO that = (IntegrationInstanceDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "IntegrationInstanceDTO{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", instanceType='" + instanceType + '\'' +
            ", environment='" + environment + '\'' +
            ", datacenterName='" + datacenterName + '\'' +
            ", isActive=" + isActive +
            '}';
    }
}
