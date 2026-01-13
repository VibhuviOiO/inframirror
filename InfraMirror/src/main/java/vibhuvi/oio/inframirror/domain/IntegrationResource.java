package vibhuvi.oio.inframirror.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * API Resource definition for Integration Control Console.
 * Defines WHAT objects exist (apps, pods, collections).
 */
@Entity
@Table(name = "integration_resource")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class IntegrationResource implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Size(max = 100)
    @Column(name = "display_name", length = 100)
    private String displayName;

    @NotNull
    @Column(name = "api_path", nullable = false)
    private String apiPath;

    @Column(name = "response_path")
    private String responsePath;

    @Column(name = "identifier_path")
    private String identifierPath;

    @Column(name = "supports_namespace")
    private Boolean supportsNamespace = false;

    @Column(name = "refresh_interval_sec")
    private Integer refreshIntervalSec = 30;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "instances", "resources" }, allowSetters = true)
    private ControlIntegration controlIntegration;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "integrationResource")
    @JsonIgnoreProperties(value = { "integrationResource" }, allowSetters = true)
    private Set<IntegrationView> views = new HashSet<>();

    public Long getId() {
        return this.id;
    }

    public IntegrationResource id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public IntegrationResource name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public IntegrationResource displayName(String displayName) {
        this.setDisplayName(displayName);
        return this;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getApiPath() {
        return this.apiPath;
    }

    public IntegrationResource apiPath(String apiPath) {
        this.setApiPath(apiPath);
        return this;
    }

    public void setApiPath(String apiPath) {
        this.apiPath = apiPath;
    }

    public String getResponsePath() {
        return this.responsePath;
    }

    public IntegrationResource responsePath(String responsePath) {
        this.setResponsePath(responsePath);
        return this;
    }

    public void setResponsePath(String responsePath) {
        this.responsePath = responsePath;
    }

    public String getIdentifierPath() {
        return this.identifierPath;
    }

    public IntegrationResource identifierPath(String identifierPath) {
        this.setIdentifierPath(identifierPath);
        return this;
    }

    public void setIdentifierPath(String identifierPath) {
        this.identifierPath = identifierPath;
    }

    public Boolean getSupportsNamespace() {
        return this.supportsNamespace;
    }

    public IntegrationResource supportsNamespace(Boolean supportsNamespace) {
        this.setSupportsNamespace(supportsNamespace);
        return this;
    }

    public void setSupportsNamespace(Boolean supportsNamespace) {
        this.supportsNamespace = supportsNamespace;
    }

    public Integer getRefreshIntervalSec() {
        return this.refreshIntervalSec;
    }

    public IntegrationResource refreshIntervalSec(Integer refreshIntervalSec) {
        this.setRefreshIntervalSec(refreshIntervalSec);
        return this;
    }

    public void setRefreshIntervalSec(Integer refreshIntervalSec) {
        this.refreshIntervalSec = refreshIntervalSec;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public IntegrationResource isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public IntegrationResource createdAt(Instant createdAt) {
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

    public IntegrationResource controlIntegration(ControlIntegration controlIntegration) {
        this.setControlIntegration(controlIntegration);
        return this;
    }

    public Set<IntegrationView> getViews() {
        return this.views;
    }

    public void setViews(Set<IntegrationView> integrationViews) {
        if (this.views != null) {
            this.views.forEach(i -> i.setIntegrationResource(null));
        }
        if (integrationViews != null) {
            integrationViews.forEach(i -> i.setIntegrationResource(this));
        }
        this.views = integrationViews;
    }

    public IntegrationResource views(Set<IntegrationView> integrationViews) {
        this.setViews(integrationViews);
        return this;
    }

    public IntegrationResource addView(IntegrationView integrationView) {
        this.views.add(integrationView);
        integrationView.setIntegrationResource(this);
        return this;
    }

    public IntegrationResource removeView(IntegrationView integrationView) {
        this.views.remove(integrationView);
        integrationView.setIntegrationResource(null);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntegrationResource)) return false;
        return getId() != null && getId().equals(((IntegrationResource) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "IntegrationResource{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", displayName='" + getDisplayName() + "'" +
            ", apiPath='" + getApiPath() + "'" +
            "}";
    }
}
