package vibhuvi.oio.inframirror.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A ServiceInstance.
 */
@Entity
@Table(name = "service_instance")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "serviceinstance")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ServiceInstance implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "port", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer port;

    @Column(name = "is_active")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean isActive;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "serviceInstance")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "agent", "monitoredService", "serviceInstance" }, allowSetters = true)
    private Set<ServiceHeartbeat> heartbeats = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "heartbeats", "serviceInstances", "datacenter", "agent" }, allowSetters = true)
    private Instance instance;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "serviceInstances", "heartbeats", "datacenter" }, allowSetters = true)
    private MonitoredService monitoredService;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ServiceInstance id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPort() {
        return this.port;
    }

    public ServiceInstance port(Integer port) {
        this.setPort(port);
        return this;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public ServiceInstance isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public ServiceInstance createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public ServiceInstance updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<ServiceHeartbeat> getHeartbeats() {
        return this.heartbeats;
    }

    public void setHeartbeats(Set<ServiceHeartbeat> serviceHeartbeats) {
        if (this.heartbeats != null) {
            this.heartbeats.forEach(i -> i.setServiceInstance(null));
        }
        if (serviceHeartbeats != null) {
            serviceHeartbeats.forEach(i -> i.setServiceInstance(this));
        }
        this.heartbeats = serviceHeartbeats;
    }

    public ServiceInstance heartbeats(Set<ServiceHeartbeat> serviceHeartbeats) {
        this.setHeartbeats(serviceHeartbeats);
        return this;
    }

    public ServiceInstance addHeartbeat(ServiceHeartbeat serviceHeartbeat) {
        this.heartbeats.add(serviceHeartbeat);
        serviceHeartbeat.setServiceInstance(this);
        return this;
    }

    public ServiceInstance removeHeartbeat(ServiceHeartbeat serviceHeartbeat) {
        this.heartbeats.remove(serviceHeartbeat);
        serviceHeartbeat.setServiceInstance(null);
        return this;
    }

    public Instance getInstance() {
        return this.instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    public ServiceInstance instance(Instance instance) {
        this.setInstance(instance);
        return this;
    }

    public MonitoredService getMonitoredService() {
        return this.monitoredService;
    }

    public void setMonitoredService(MonitoredService monitoredService) {
        this.monitoredService = monitoredService;
    }

    public ServiceInstance monitoredService(MonitoredService monitoredService) {
        this.setMonitoredService(monitoredService);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServiceInstance)) {
            return false;
        }
        return getId() != null && getId().equals(((ServiceInstance) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ServiceInstance{" +
            "id=" + getId() +
            ", port=" + getPort() +
            ", isActive='" + getIsActive() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
