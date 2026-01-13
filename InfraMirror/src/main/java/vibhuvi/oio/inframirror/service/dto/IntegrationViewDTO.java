package vibhuvi.oio.inframirror.service.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * DTO for {@link vibhuvi.oio.inframirror.domain.IntegrationView}
 */
public class IntegrationViewDTO implements Serializable {

    private Long id;

    @NotNull
    private Long integrationResourceId;

    private String integrationResourceName;

    @Size(max = 30)
    private String viewType;

    @Size(max = 150)
    private String title;

    @Size(max = 100)
    private String defaultSort;

    private Boolean isDefault;

    @NotNull
    private JsonNode config;

    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIntegrationResourceId() {
        return integrationResourceId;
    }

    public void setIntegrationResourceId(Long integrationResourceId) {
        this.integrationResourceId = integrationResourceId;
    }

    public String getIntegrationResourceName() {
        return integrationResourceName;
    }

    public void setIntegrationResourceName(String integrationResourceName) {
        this.integrationResourceName = integrationResourceName;
    }

    public String getViewType() {
        return viewType;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDefaultSort() {
        return defaultSort;
    }

    public void setDefaultSort(String defaultSort) {
        this.defaultSort = defaultSort;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public JsonNode getConfig() {
        return config;
    }

    public void setConfig(JsonNode config) {
        this.config = config;
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
        if (!(o instanceof IntegrationViewDTO)) return false;
        IntegrationViewDTO that = (IntegrationViewDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "IntegrationViewDTO{" +
            "id=" + id +
            ", viewType='" + viewType + '\'' +
            ", title='" + title + '\'' +
            ", isDefault=" + isDefault +
            '}';
    }
}
