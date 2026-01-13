package vibhuvi.oio.inframirror.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * UI configuration for Integration Control Console.
 * Defines HOW resources appear in UI (columns, filters, actions).
 */
@Entity
@Table(name = "integration_view")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class IntegrationView implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Size(max = 30)
    @Column(name = "view_type", length = 30)
    private String viewType = "TABLE";

    @Size(max = 150)
    @Column(name = "title", length = 150)
    private String title;

    @Size(max = 100)
    @Column(name = "default_sort", length = 100)
    private String defaultSort;

    @Column(name = "is_default")
    private Boolean isDefault = true;

    @NotNull
    @Column(name = "config", columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode config;

    @Column(name = "created_at")
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "controlIntegration", "views" }, allowSetters = true)
    private IntegrationResource integrationResource;

    public Long getId() {
        return this.id;
    }

    public IntegrationView id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getViewType() {
        return this.viewType;
    }

    public IntegrationView viewType(String viewType) {
        this.setViewType(viewType);
        return this;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }

    public String getTitle() {
        return this.title;
    }

    public IntegrationView title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDefaultSort() {
        return this.defaultSort;
    }

    public IntegrationView defaultSort(String defaultSort) {
        this.setDefaultSort(defaultSort);
        return this;
    }

    public void setDefaultSort(String defaultSort) {
        this.defaultSort = defaultSort;
    }

    public Boolean getIsDefault() {
        return this.isDefault;
    }

    public IntegrationView isDefault(Boolean isDefault) {
        this.setIsDefault(isDefault);
        return this;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public JsonNode getConfig() {
        return this.config;
    }

    public IntegrationView config(JsonNode config) {
        this.setConfig(config);
        return this;
    }

    public void setConfig(JsonNode config) {
        this.config = config;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public IntegrationView createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public IntegrationResource getIntegrationResource() {
        return this.integrationResource;
    }

    public void setIntegrationResource(IntegrationResource integrationResource) {
        this.integrationResource = integrationResource;
    }

    public IntegrationView integrationResource(IntegrationResource integrationResource) {
        this.setIntegrationResource(integrationResource);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntegrationView)) return false;
        return getId() != null && getId().equals(((IntegrationView) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "IntegrationView{" +
            "id=" + getId() +
            ", viewType='" + getViewType() + "'" +
            ", title='" + getTitle() + "'" +
            "}";
    }
}
