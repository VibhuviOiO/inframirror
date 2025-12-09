package vibhuvi.oio.inframirror.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Datacenter.
 */
@Entity
@Table(name = "datacenter")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "datacenter")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Datacenter implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Size(max = 10)
    @Column(name = "code", length = 10, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String code;

    @NotNull
    @Size(max = 50)
    @Column(name = "name", length = 50, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "datacenter")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "heartbeats", "serviceInstances", "datacenter", "agent" }, allowSetters = true)
    private Set<Instance> instances = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "datacenter")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "serviceInstances", "heartbeats", "datacenter" }, allowSetters = true)
    private Set<MonitoredService> monitoredServices = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "datacenters", "agents" }, allowSetters = true)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Object)
    private Region region;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Datacenter id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public Datacenter code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public Datacenter name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Instance> getInstances() {
        return this.instances;
    }

    public void setInstances(Set<Instance> instances) {
        if (this.instances != null) {
            this.instances.forEach(i -> i.setDatacenter(null));
        }
        if (instances != null) {
            instances.forEach(i -> i.setDatacenter(this));
        }
        this.instances = instances;
    }

    public Datacenter instances(Set<Instance> instances) {
        this.setInstances(instances);
        return this;
    }

    public Datacenter addInstance(Instance instance) {
        this.instances.add(instance);
        instance.setDatacenter(this);
        return this;
    }

    public Datacenter removeInstance(Instance instance) {
        this.instances.remove(instance);
        instance.setDatacenter(null);
        return this;
    }

    public Set<MonitoredService> getMonitoredServices() {
        return this.monitoredServices;
    }

    public void setMonitoredServices(Set<MonitoredService> monitoredServices) {
        if (this.monitoredServices != null) {
            this.monitoredServices.forEach(i -> i.setDatacenter(null));
        }
        if (monitoredServices != null) {
            monitoredServices.forEach(i -> i.setDatacenter(this));
        }
        this.monitoredServices = monitoredServices;
    }

    public Datacenter monitoredServices(Set<MonitoredService> monitoredServices) {
        this.setMonitoredServices(monitoredServices);
        return this;
    }

    public Datacenter addMonitoredService(MonitoredService monitoredService) {
        this.monitoredServices.add(monitoredService);
        monitoredService.setDatacenter(this);
        return this;
    }

    public Datacenter removeMonitoredService(MonitoredService monitoredService) {
        this.monitoredServices.remove(monitoredService);
        monitoredService.setDatacenter(null);
        return this;
    }

    public Region getRegion() {
        return this.region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Datacenter region(Region region) {
        this.setRegion(region);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Datacenter)) {
            return false;
        }
        return getId() != null && getId().equals(((Datacenter) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Datacenter{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            "}";
    }
}
