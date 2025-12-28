package vibhuvi.oio.inframirror.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A ServiceHeartbeat.
 */
@Entity
@Table(name = "service_heartbeat")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ServiceHeartbeat implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "executed_at", nullable = false)
    private Instant executedAt;

    @NotNull
    @Column(name = "success", nullable = false)
    private Boolean success;

    @NotNull
    @Size(max = 20)
    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "response_time_ms")
    private Integer responseTimeMs;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = { "instances", "httpHeartbeats", "instanceHeartbeats", "serviceHeartbeats", "region" },
        allowSetters = true
    )
    private Agent agent;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "serviceInstances", "heartbeats", "datacenter" }, allowSetters = true)
    private MonitoredService monitoredService;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "heartbeats", "instance", "monitoredService" }, allowSetters = true)
    private ServiceInstance serviceInstance;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ServiceHeartbeat id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getExecutedAt() {
        return this.executedAt;
    }

    public ServiceHeartbeat executedAt(Instant executedAt) {
        this.setExecutedAt(executedAt);
        return this;
    }

    public void setExecutedAt(Instant executedAt) {
        this.executedAt = executedAt;
    }

    public Boolean getSuccess() {
        return this.success;
    }

    public ServiceHeartbeat success(Boolean success) {
        this.setSuccess(success);
        return this;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getStatus() {
        return this.status;
    }

    public ServiceHeartbeat status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getResponseTimeMs() {
        return this.responseTimeMs;
    }

    public ServiceHeartbeat responseTimeMs(Integer responseTimeMs) {
        this.setResponseTimeMs(responseTimeMs);
        return this;
    }

    public void setResponseTimeMs(Integer responseTimeMs) {
        this.responseTimeMs = responseTimeMs;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public ServiceHeartbeat errorMessage(String errorMessage) {
        this.setErrorMessage(errorMessage);
        return this;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getMetadata() {
        return this.metadata;
    }

    public ServiceHeartbeat metadata(String metadata) {
        this.setMetadata(metadata);
        return this;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public Agent getAgent() {
        return this.agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public ServiceHeartbeat agent(Agent agent) {
        this.setAgent(agent);
        return this;
    }

    public MonitoredService getMonitoredService() {
        return this.monitoredService;
    }

    public void setMonitoredService(MonitoredService monitoredService) {
        this.monitoredService = monitoredService;
    }

    public ServiceHeartbeat monitoredService(MonitoredService monitoredService) {
        this.setMonitoredService(monitoredService);
        return this;
    }

    public ServiceInstance getServiceInstance() {
        return this.serviceInstance;
    }

    public void setServiceInstance(ServiceInstance serviceInstance) {
        this.serviceInstance = serviceInstance;
    }

    public ServiceHeartbeat serviceInstance(ServiceInstance serviceInstance) {
        this.setServiceInstance(serviceInstance);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServiceHeartbeat)) {
            return false;
        }
        return getId() != null && getId().equals(((ServiceHeartbeat) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ServiceHeartbeat{" +
            "id=" + getId() +
            ", executedAt='" + getExecutedAt() + "'" +
            ", success='" + getSuccess() + "'" +
            ", status='" + getStatus() + "'" +
            ", responseTimeMs=" + getResponseTimeMs() +
            ", errorMessage='" + getErrorMessage() + "'" +
            ", metadata='" + getMetadata() + "'" +
            "}";
    }
}
