package vibhuvi.oio.inframirror.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Cluster/Endpoint reference for Integration Control Console.
 * Links to existing MonitoredService or HttpMonitor, or stores custom API config.
 */
@Entity
@Table(name = "integration_instance")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class IntegrationInstance implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 150)
    @Column(name = "name", length = 150, nullable = false)
    private String name;

    @NotNull
    @Size(max = 30)
    @Column(name = "instance_type", length = 30, nullable = false)
    private String instanceType;

    @Column(name = "base_url")
    private String baseUrl;

    @Size(max = 30)
    @Column(name = "auth_type", length = 30)
    private String authType;

    @Column(name = "auth_config", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode authConfig;

    @Size(max = 20)
    @Column(name = "environment", length = 20)
    private String environment;

    @Column(name = "timeout_ms")
    private Integer timeoutMs = 5000;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "instances", "resources" }, allowSetters = true)
    private ControlIntegration controlIntegration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "datacenter", "serviceInstances" }, allowSetters = true)
    private MonitoredService monitoredService;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "statusPageItems" }, allowSetters = true)
    private HttpMonitor httpMonitor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "region", "instances" }, allowSetters = true)
    private Datacenter datacenter;

    public Long getId() {
        return this.id;
    }

    public IntegrationInstance id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public IntegrationInstance name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstanceType() {
        return this.instanceType;
    }

    public IntegrationInstance instanceType(String instanceType) {
        this.setInstanceType(instanceType);
        return this;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public IntegrationInstance baseUrl(String baseUrl) {
        this.setBaseUrl(baseUrl);
        return this;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getAuthType() {
        return this.authType;
    }

    public IntegrationInstance authType(String authType) {
        this.setAuthType(authType);
        return this;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public JsonNode getAuthConfig() {
        return this.authConfig;
    }

    public IntegrationInstance authConfig(JsonNode authConfig) {
        this.setAuthConfig(authConfig);
        return this;
    }

    public void setAuthConfig(JsonNode authConfig) {
        this.authConfig = authConfig;
    }

    public String getEnvironment() {
        return this.environment;
    }

    public IntegrationInstance environment(String environment) {
        this.setEnvironment(environment);
        return this;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public Integer getTimeoutMs() {
        return this.timeoutMs;
    }

    public IntegrationInstance timeoutMs(Integer timeoutMs) {
        this.setTimeoutMs(timeoutMs);
        return this;
    }

    public void setTimeoutMs(Integer timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public IntegrationInstance isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public IntegrationInstance createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public ControlIntegration getControlIntegration() {
        return this.controlIntegration;
    }

    public void setControlIntegration(ControlIntegration controlIntegration) {
        this.controlIntegration = controlIntegration;
    }

    public IntegrationInstance controlIntegration(ControlIntegration controlIntegration) {
        this.setControlIntegration(controlIntegration);
        return this;
    }

    public MonitoredService getMonitoredService() {
        return this.monitoredService;
    }

    public void setMonitoredService(MonitoredService monitoredService) {
        this.monitoredService = monitoredService;
    }

    public IntegrationInstance monitoredService(MonitoredService monitoredService) {
        this.setMonitoredService(monitoredService);
        return this;
    }

    public HttpMonitor getHttpMonitor() {
        return this.httpMonitor;
    }

    public void setHttpMonitor(HttpMonitor httpMonitor) {
        this.httpMonitor = httpMonitor;
    }

    public IntegrationInstance httpMonitor(HttpMonitor httpMonitor) {
        this.setHttpMonitor(httpMonitor);
        return this;
    }

    public Datacenter getDatacenter() {
        return this.datacenter;
    }

    public void setDatacenter(Datacenter datacenter) {
        this.datacenter = datacenter;
    }

    public IntegrationInstance datacenter(Datacenter datacenter) {
        this.setDatacenter(datacenter);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntegrationInstance)) return false;
        return getId() != null && getId().equals(((IntegrationInstance) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "IntegrationInstance{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", instanceType='" + getInstanceType() + "'" +
            "}";
    }
}
