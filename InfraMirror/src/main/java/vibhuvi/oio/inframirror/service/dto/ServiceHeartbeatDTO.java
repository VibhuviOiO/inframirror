package vibhuvi.oio.inframirror.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link vibhuvi.oio.inframirror.domain.ServiceHeartbeat} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ServiceHeartbeatDTO implements Serializable {

    @NotNull
    private Long id;

    @NotNull
    private Instant executedAt;

    @NotNull
    private Boolean success;

    @NotNull
    @Size(max = 20)
    private String status;

    private Integer responseTimeMs;

    @Lob
    private String errorMessage;

    @Lob
    private String metadata;

    private AgentDTO agent;

    @NotNull
    private ServiceDTO service;

    private ServiceInstanceDTO serviceInstance;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(Instant executedAt) {
        this.executedAt = executedAt;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

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

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public AgentDTO getAgent() {
        return agent;
    }

    public void setAgent(AgentDTO agent) {
        this.agent = agent;
    }

    public ServiceDTO getService() {
        return service;
    }

    public void setService(ServiceDTO service) {
        this.service = service;
    }

    public ServiceInstanceDTO getServiceInstance() {
        return serviceInstance;
    }

    public void setServiceInstance(ServiceInstanceDTO serviceInstance) {
        this.serviceInstance = serviceInstance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServiceHeartbeatDTO)) {
            return false;
        }

        ServiceHeartbeatDTO serviceHeartbeatDTO = (ServiceHeartbeatDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, serviceHeartbeatDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ServiceHeartbeatDTO{" +
            "id=" + getId() +
            ", executedAt='" + getExecutedAt() + "'" +
            ", success='" + getSuccess() + "'" +
            ", status='" + getStatus() + "'" +
            ", responseTimeMs=" + getResponseTimeMs() +
            ", errorMessage='" + getErrorMessage() + "'" +
            ", metadata='" + getMetadata() + "'" +
            ", agent=" + getAgent() +
            ", service=" + getService() +
            ", serviceInstance=" + getServiceInstance() +
            "}";
    }
}
