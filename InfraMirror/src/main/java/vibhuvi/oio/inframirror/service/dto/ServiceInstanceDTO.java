package vibhuvi.oio.inframirror.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link vibhuvi.oio.inframirror.domain.ServiceInstance} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ServiceInstanceDTO implements Serializable {

    private Long id;

    @NotNull
    private Integer port;

    private Boolean isActive;

    private Instant createdAt;

    private Instant updatedAt;

    @NotNull
    private InstanceDTO instance;

    @NotNull
    private MonitoredServiceDTO monitoredService;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
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

    public InstanceDTO getInstance() {
        return instance;
    }

    public void setInstance(InstanceDTO instance) {
        this.instance = instance;
    }

    public MonitoredServiceDTO getMonitoredService() {
        return monitoredService;
    }

    public void setMonitoredService(MonitoredServiceDTO monitoredService) {
        this.monitoredService = monitoredService;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServiceInstanceDTO)) {
            return false;
        }

        ServiceInstanceDTO serviceInstanceDTO = (ServiceInstanceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, serviceInstanceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ServiceInstanceDTO{" +
            "id=" + getId() +
            ", port=" + getPort() +
            ", isActive='" + getIsActive() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", instance=" + getInstance() +
            ", monitoredService=" + getMonitoredService() +
            "}";
    }
}
