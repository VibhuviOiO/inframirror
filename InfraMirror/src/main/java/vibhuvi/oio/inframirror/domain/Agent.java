package vibhuvi.oio.inframirror.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Agent.
 */
@Entity
@Table(name = "agent")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "agent")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Agent implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "name", length = 50, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "agent")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "heartbeats", "serviceInstances", "datacenter", "agent" }, allowSetters = true)
    private Set<Instance> instances = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "agent")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "agent", "monitor" }, allowSetters = true)
    private Set<HttpHeartbeat> httpHeartbeats = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "agent")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "agent", "instance" }, allowSetters = true)
    private Set<InstanceHeartbeat> instanceHeartbeats = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "agent")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "agent", "service", "serviceInstance" }, allowSetters = true)
    private Set<ServiceHeartbeat> serviceHeartbeats = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "datacenters", "agents" }, allowSetters = true)
    private Region region;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Agent id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Agent name(String name) {
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
            this.instances.forEach(i -> i.setAgent(null));
        }
        if (instances != null) {
            instances.forEach(i -> i.setAgent(this));
        }
        this.instances = instances;
    }

    public Agent instances(Set<Instance> instances) {
        this.setInstances(instances);
        return this;
    }

    public Agent addInstance(Instance instance) {
        this.instances.add(instance);
        instance.setAgent(this);
        return this;
    }

    public Agent removeInstance(Instance instance) {
        this.instances.remove(instance);
        instance.setAgent(null);
        return this;
    }

    public Set<HttpHeartbeat> getHttpHeartbeats() {
        return this.httpHeartbeats;
    }

    public void setHttpHeartbeats(Set<HttpHeartbeat> httpHeartbeats) {
        if (this.httpHeartbeats != null) {
            this.httpHeartbeats.forEach(i -> i.setAgent(null));
        }
        if (httpHeartbeats != null) {
            httpHeartbeats.forEach(i -> i.setAgent(this));
        }
        this.httpHeartbeats = httpHeartbeats;
    }

    public Agent httpHeartbeats(Set<HttpHeartbeat> httpHeartbeats) {
        this.setHttpHeartbeats(httpHeartbeats);
        return this;
    }

    public Agent addHttpHeartbeat(HttpHeartbeat httpHeartbeat) {
        this.httpHeartbeats.add(httpHeartbeat);
        httpHeartbeat.setAgent(this);
        return this;
    }

    public Agent removeHttpHeartbeat(HttpHeartbeat httpHeartbeat) {
        this.httpHeartbeats.remove(httpHeartbeat);
        httpHeartbeat.setAgent(null);
        return this;
    }

    public Set<InstanceHeartbeat> getInstanceHeartbeats() {
        return this.instanceHeartbeats;
    }

    public void setInstanceHeartbeats(Set<InstanceHeartbeat> instanceHeartbeats) {
        if (this.instanceHeartbeats != null) {
            this.instanceHeartbeats.forEach(i -> i.setAgent(null));
        }
        if (instanceHeartbeats != null) {
            instanceHeartbeats.forEach(i -> i.setAgent(this));
        }
        this.instanceHeartbeats = instanceHeartbeats;
    }

    public Agent instanceHeartbeats(Set<InstanceHeartbeat> instanceHeartbeats) {
        this.setInstanceHeartbeats(instanceHeartbeats);
        return this;
    }

    public Agent addInstanceHeartbeat(InstanceHeartbeat instanceHeartbeat) {
        this.instanceHeartbeats.add(instanceHeartbeat);
        instanceHeartbeat.setAgent(this);
        return this;
    }

    public Agent removeInstanceHeartbeat(InstanceHeartbeat instanceHeartbeat) {
        this.instanceHeartbeats.remove(instanceHeartbeat);
        instanceHeartbeat.setAgent(null);
        return this;
    }

    public Set<ServiceHeartbeat> getServiceHeartbeats() {
        return this.serviceHeartbeats;
    }

    public void setServiceHeartbeats(Set<ServiceHeartbeat> serviceHeartbeats) {
        if (this.serviceHeartbeats != null) {
            this.serviceHeartbeats.forEach(i -> i.setAgent(null));
        }
        if (serviceHeartbeats != null) {
            serviceHeartbeats.forEach(i -> i.setAgent(this));
        }
        this.serviceHeartbeats = serviceHeartbeats;
    }

    public Agent serviceHeartbeats(Set<ServiceHeartbeat> serviceHeartbeats) {
        this.setServiceHeartbeats(serviceHeartbeats);
        return this;
    }

    public Agent addServiceHeartbeat(ServiceHeartbeat serviceHeartbeat) {
        this.serviceHeartbeats.add(serviceHeartbeat);
        serviceHeartbeat.setAgent(this);
        return this;
    }

    public Agent removeServiceHeartbeat(ServiceHeartbeat serviceHeartbeat) {
        this.serviceHeartbeats.remove(serviceHeartbeat);
        serviceHeartbeat.setAgent(null);
        return this;
    }

    public Region getRegion() {
        return this.region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Agent region(Region region) {
        this.setRegion(region);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Agent)) {
            return false;
        }
        return getId() != null && getId().equals(((Agent) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Agent{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
