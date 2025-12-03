package vibhuvi.oio.inframirror.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Datacenter - Physical datacenter locations within regions
 * Table: datacenters
 */
@Entity
@Table(name = "datacenter")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "datacenter")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Datacenter implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
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
    @JsonIgnoreProperties(value = { "instances", "httpHeartbeats", "pingHeartbeats", "datacenter" }, allowSetters = true)
    private Set<Agent> agents = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "datacenter")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "pingHeartbeats", "datacenter", "agent" }, allowSetters = true)
    private Set<Instance> instances = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "datacenters" }, allowSetters = true)
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

    public Set<Agent> getAgents() {
        return this.agents;
    }

    public void setAgents(Set<Agent> agents) {
        if (this.agents != null) {
            this.agents.forEach(i -> i.setDatacenter(null));
        }
        if (agents != null) {
            agents.forEach(i -> i.setDatacenter(this));
        }
        this.agents = agents;
    }

    public Datacenter agents(Set<Agent> agents) {
        this.setAgents(agents);
        return this;
    }

    public Datacenter addAgents(Agent agent) {
        this.agents.add(agent);
        agent.setDatacenter(this);
        return this;
    }

    public Datacenter removeAgents(Agent agent) {
        this.agents.remove(agent);
        agent.setDatacenter(null);
        return this;
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

    public Datacenter addInstances(Instance instance) {
        this.instances.add(instance);
        instance.setDatacenter(this);
        return this;
    }

    public Datacenter removeInstances(Instance instance) {
        this.instances.remove(instance);
        instance.setDatacenter(null);
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
