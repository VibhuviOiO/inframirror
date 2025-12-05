package vibhuvi.oio.inframirror.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A Service.
 */
@Entity
@Table(name = "service")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "service")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Service implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Size(max = 200)
    @Column(name = "name", length = 200, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String name;

    @Size(max = 500)
    @Column(name = "description", length = 500)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String description;

    @NotNull
    @Size(max = 50)
    @Column(name = "service_type", length = 50, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String serviceType;

    @NotNull
    @Size(max = 20)
    @Column(name = "environment", length = 20, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String environment;

    @Column(name = "monitoring_enabled")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean monitoringEnabled;

    @Column(name = "cluster_monitoring_enabled")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean clusterMonitoringEnabled;

    @NotNull
    @Column(name = "interval_seconds", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer intervalSeconds;

    @NotNull
    @Column(name = "timeout_ms", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer timeoutMs;

    @NotNull
    @Column(name = "retry_count", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer retryCount;

    @Column(name = "latency_warning_ms")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer latencyWarningMs;

    @Column(name = "latency_critical_ms")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer latencyCriticalMs;

    @Lob
    @Column(name = "advanced_config")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String advancedConfig;

    @Column(name = "is_active")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean isActive;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "service")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "heartbeats", "instance", "service" }, allowSetters = true)
    private Set<ServiceInstance> serviceInstances = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "service")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "agent", "service", "serviceInstance" }, allowSetters = true)
    private Set<ServiceHeartbeat> heartbeats = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "instances", "services", "region" }, allowSetters = true)
    private Datacenter datacenter;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Service id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Service name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Service description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getServiceType() {
        return this.serviceType;
    }

    public Service serviceType(String serviceType) {
        this.setServiceType(serviceType);
        return this;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getEnvironment() {
        return this.environment;
    }

    public Service environment(String environment) {
        this.setEnvironment(environment);
        return this;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public Boolean getMonitoringEnabled() {
        return this.monitoringEnabled;
    }

    public Service monitoringEnabled(Boolean monitoringEnabled) {
        this.setMonitoringEnabled(monitoringEnabled);
        return this;
    }

    public void setMonitoringEnabled(Boolean monitoringEnabled) {
        this.monitoringEnabled = monitoringEnabled;
    }

    public Boolean getClusterMonitoringEnabled() {
        return this.clusterMonitoringEnabled;
    }

    public Service clusterMonitoringEnabled(Boolean clusterMonitoringEnabled) {
        this.setClusterMonitoringEnabled(clusterMonitoringEnabled);
        return this;
    }

    public void setClusterMonitoringEnabled(Boolean clusterMonitoringEnabled) {
        this.clusterMonitoringEnabled = clusterMonitoringEnabled;
    }

    public Integer getIntervalSeconds() {
        return this.intervalSeconds;
    }

    public Service intervalSeconds(Integer intervalSeconds) {
        this.setIntervalSeconds(intervalSeconds);
        return this;
    }

    public void setIntervalSeconds(Integer intervalSeconds) {
        this.intervalSeconds = intervalSeconds;
    }

    public Integer getTimeoutMs() {
        return this.timeoutMs;
    }

    public Service timeoutMs(Integer timeoutMs) {
        this.setTimeoutMs(timeoutMs);
        return this;
    }

    public void setTimeoutMs(Integer timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public Integer getRetryCount() {
        return this.retryCount;
    }

    public Service retryCount(Integer retryCount) {
        this.setRetryCount(retryCount);
        return this;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public Integer getLatencyWarningMs() {
        return this.latencyWarningMs;
    }

    public Service latencyWarningMs(Integer latencyWarningMs) {
        this.setLatencyWarningMs(latencyWarningMs);
        return this;
    }

    public void setLatencyWarningMs(Integer latencyWarningMs) {
        this.latencyWarningMs = latencyWarningMs;
    }

    public Integer getLatencyCriticalMs() {
        return this.latencyCriticalMs;
    }

    public Service latencyCriticalMs(Integer latencyCriticalMs) {
        this.setLatencyCriticalMs(latencyCriticalMs);
        return this;
    }

    public void setLatencyCriticalMs(Integer latencyCriticalMs) {
        this.latencyCriticalMs = latencyCriticalMs;
    }

    public String getAdvancedConfig() {
        return this.advancedConfig;
    }

    public Service advancedConfig(String advancedConfig) {
        this.setAdvancedConfig(advancedConfig);
        return this;
    }

    public void setAdvancedConfig(String advancedConfig) {
        this.advancedConfig = advancedConfig;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public Service isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Service createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public Service updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<ServiceInstance> getServiceInstances() {
        return this.serviceInstances;
    }

    public void setServiceInstances(Set<ServiceInstance> serviceInstances) {
        if (this.serviceInstances != null) {
            this.serviceInstances.forEach(i -> i.setService(null));
        }
        if (serviceInstances != null) {
            serviceInstances.forEach(i -> i.setService(this));
        }
        this.serviceInstances = serviceInstances;
    }

    public Service serviceInstances(Set<ServiceInstance> serviceInstances) {
        this.setServiceInstances(serviceInstances);
        return this;
    }

    public Service addServiceInstance(ServiceInstance serviceInstance) {
        this.serviceInstances.add(serviceInstance);
        serviceInstance.setService(this);
        return this;
    }

    public Service removeServiceInstance(ServiceInstance serviceInstance) {
        this.serviceInstances.remove(serviceInstance);
        serviceInstance.setService(null);
        return this;
    }

    public Set<ServiceHeartbeat> getHeartbeats() {
        return this.heartbeats;
    }

    public void setHeartbeats(Set<ServiceHeartbeat> serviceHeartbeats) {
        if (this.heartbeats != null) {
            this.heartbeats.forEach(i -> i.setService(null));
        }
        if (serviceHeartbeats != null) {
            serviceHeartbeats.forEach(i -> i.setService(this));
        }
        this.heartbeats = serviceHeartbeats;
    }

    public Service heartbeats(Set<ServiceHeartbeat> serviceHeartbeats) {
        this.setHeartbeats(serviceHeartbeats);
        return this;
    }

    public Service addHeartbeat(ServiceHeartbeat serviceHeartbeat) {
        this.heartbeats.add(serviceHeartbeat);
        serviceHeartbeat.setService(this);
        return this;
    }

    public Service removeHeartbeat(ServiceHeartbeat serviceHeartbeat) {
        this.heartbeats.remove(serviceHeartbeat);
        serviceHeartbeat.setService(null);
        return this;
    }

    public Datacenter getDatacenter() {
        return this.datacenter;
    }

    public void setDatacenter(Datacenter datacenter) {
        this.datacenter = datacenter;
    }

    public Service datacenter(Datacenter datacenter) {
        this.setDatacenter(datacenter);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Service)) {
            return false;
        }
        return getId() != null && getId().equals(((Service) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Service{" +
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
            "}";
    }
}
