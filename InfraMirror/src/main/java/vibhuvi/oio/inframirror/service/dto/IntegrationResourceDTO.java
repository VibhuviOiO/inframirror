package vibhuvi.oio.inframirror.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * DTO for {@link vibhuvi.oio.inframirror.domain.IntegrationResource}
 */
public class IntegrationResourceDTO implements Serializable {

    private Long id;

    @NotNull
    private Long controlIntegrationId;

    private String controlIntegrationName;

    @NotNull
    @Size(max = 100)
    private String name;

    @Size(max = 100)
    private String displayName;

    @NotNull
    private String apiPath;

    private String responsePath;

    private String identifierPath;

    private Boolean supportsNamespace;

    private Integer refreshIntervalSec;

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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getApiPath() {
        return apiPath;
    }

    public void setApiPath(String apiPath) {
        this.apiPath = apiPath;
    }

    public String getResponsePath() {
        return responsePath;
    }

    public void setResponsePath(String responsePath) {
        this.responsePath = responsePath;
    }

    public String getIdentifierPath() {
        return identifierPath;
    }

    public void setIdentifierPath(String identifierPath) {
        this.identifierPath = identifierPath;
    }

    public Boolean getSupportsNamespace() {
        return supportsNamespace;
    }

    public void setSupportsNamespace(Boolean supportsNamespace) {
        this.supportsNamespace = supportsNamespace;
    }

    public Integer getRefreshIntervalSec() {
        return refreshIntervalSec;
    }

    public void setRefreshIntervalSec(Integer refreshIntervalSec) {
        this.refreshIntervalSec = refreshIntervalSec;
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
        if (!(o instanceof IntegrationResourceDTO)) return false;
        IntegrationResourceDTO that = (IntegrationResourceDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "IntegrationResourceDTO{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", displayName='" + displayName + '\'' +
            ", apiPath='" + apiPath + '\'' +
            ", isActive=" + isActive +
            '}';
    }
}
