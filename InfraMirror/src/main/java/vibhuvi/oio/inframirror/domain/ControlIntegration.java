package vibhuvi.oio.inframirror.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * System type definition for Integration Control Console.
 * Defines WHAT kind of system (Marathon, Kubernetes, Solr, Custom).
 */
@Entity
@Table(name = "control_integration")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ControlIntegration implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "code", length = 50, nullable = false, unique = true)
    private String code;

    @NotNull
    @Size(max = 100)
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Size(max = 50)
    @Column(name = "category", length = 50)
    private String category;

    @Size(max = 100)
    @Column(name = "icon", length = 100)
    private String icon;

    @Column(name = "supports_multi_dc")
    private Boolean supportsMultiDc = false;

    @Column(name = "supports_write")
    private Boolean supportsWrite = true;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private Instant createdAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "controlIntegration")
    @JsonIgnoreProperties(value = { "controlIntegration", "monitoredService", "httpMonitor", "datacenter" }, allowSetters = true)
    private Set<IntegrationInstance> instances = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "controlIntegration")
    @JsonIgnoreProperties(value = { "controlIntegration", "views" }, allowSetters = true)
    private Set<IntegrationResource> resources = new HashSet<>();

    public Long getId() {
        return this.id;
    }

    public ControlIntegration id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public ControlIntegration code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public ControlIntegration name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public ControlIntegration description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return this.category;
    }

    public ControlIntegration category(String category) {
        this.setCategory(category);
        return this;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIcon() {
        return this.icon;
    }

    public ControlIntegration icon(String icon) {
        this.setIcon(icon);
        return this;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Boolean getSupportsMultiDc() {
        return this.supportsMultiDc;
    }

    public ControlIntegration supportsMultiDc(Boolean supportsMultiDc) {
        this.setSupportsMultiDc(supportsMultiDc);
        return this;
    }

    public void setSupportsMultiDc(Boolean supportsMultiDc) {
        this.supportsMultiDc = supportsMultiDc;
    }

    public Boolean getSupportsWrite() {
        return this.supportsWrite;
    }

    public ControlIntegration supportsWrite(Boolean supportsWrite) {
        this.setSupportsWrite(supportsWrite);
        return this;
    }

    public void setSupportsWrite(Boolean supportsWrite) {
        this.supportsWrite = supportsWrite;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public ControlIntegration isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public ControlIntegration createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Set<IntegrationInstance> getInstances() {
        return this.instances;
    }

    public void setInstances(Set<IntegrationInstance> integrationInstances) {
        if (this.instances != null) {
            this.instances.forEach(i -> i.setControlIntegration(null));
        }
        if (integrationInstances != null) {
            integrationInstances.forEach(i -> i.setControlIntegration(this));
        }
        this.instances = integrationInstances;
    }

    public ControlIntegration instances(Set<IntegrationInstance> integrationInstances) {
        this.setInstances(integrationInstances);
        return this;
    }

    public ControlIntegration addInstance(IntegrationInstance integrationInstance) {
        this.instances.add(integrationInstance);
        integrationInstance.setControlIntegration(this);
        return this;
    }

    public ControlIntegration removeInstance(IntegrationInstance integrationInstance) {
        this.instances.remove(integrationInstance);
        integrationInstance.setControlIntegration(null);
        return this;
    }

    public Set<IntegrationResource> getResources() {
        return this.resources;
    }

    public void setResources(Set<IntegrationResource> integrationResources) {
        if (this.resources != null) {
            this.resources.forEach(i -> i.setControlIntegration(null));
        }
        if (integrationResources != null) {
            integrationResources.forEach(i -> i.setControlIntegration(this));
        }
        this.resources = integrationResources;
    }

    public ControlIntegration resources(Set<IntegrationResource> integrationResources) {
        this.setResources(integrationResources);
        return this;
    }

    public ControlIntegration addResource(IntegrationResource integrationResource) {
        this.resources.add(integrationResource);
        integrationResource.setControlIntegration(this);
        return this;
    }

    public ControlIntegration removeResource(IntegrationResource integrationResource) {
        this.resources.remove(integrationResource);
        integrationResource.setControlIntegration(null);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ControlIntegration)) return false;
        return getId() != null && getId().equals(((ControlIntegration) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ControlIntegration{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", category='" + getCategory() + "'" +
            "}";
    }
}
