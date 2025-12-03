package vibhuvi.oio.inframirror.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Agent - Monitoring agents deployed in datacenters to collect metrics
 * Table: agents
 */
@Entity
@Table(name = "agent")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "agent")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Agent implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "name", length = 50, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "agent")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "pingHeartbeats", "datacenter", "agent" }, allowSetters = true)
    private Set<Instance> instances = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "agent")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "monitor", "agent" }, allowSetters = true)
    private Set<HttpHeartbeat> httpHeartbeats = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "agent")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "instance", "agent" }, allowSetters = true)
    private Set<PingHeartbeat> pingHeartbeats = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "agents", "instances", "region" }, allowSetters = true)
    private Datacenter datacenter;

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

    public Agent addInstances(Instance instance) {
        this.instances.add(instance);
        instance.setAgent(this);
        return this;
    }

    public Agent removeInstances(Instance instance) {
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

    public Agent addHttpHeartbeats(HttpHeartbeat httpHeartbeat) {
        this.httpHeartbeats.add(httpHeartbeat);
        httpHeartbeat.setAgent(this);
        return this;
    }

    public Agent removeHttpHeartbeats(HttpHeartbeat httpHeartbeat) {
        this.httpHeartbeats.remove(httpHeartbeat);
        httpHeartbeat.setAgent(null);
        return this;
    }

    public Set<PingHeartbeat> getPingHeartbeats() {
        return this.pingHeartbeats;
    }

    public void setPingHeartbeats(Set<PingHeartbeat> pingHeartbeats) {
        if (this.pingHeartbeats != null) {
            this.pingHeartbeats.forEach(i -> i.setAgent(null));
        }
        if (pingHeartbeats != null) {
            pingHeartbeats.forEach(i -> i.setAgent(this));
        }
        this.pingHeartbeats = pingHeartbeats;
    }

    public Agent pingHeartbeats(Set<PingHeartbeat> pingHeartbeats) {
        this.setPingHeartbeats(pingHeartbeats);
        return this;
    }

    public Agent addPingHeartbeats(PingHeartbeat pingHeartbeat) {
        this.pingHeartbeats.add(pingHeartbeat);
        pingHeartbeat.setAgent(this);
        return this;
    }

    public Agent removePingHeartbeats(PingHeartbeat pingHeartbeat) {
        this.pingHeartbeats.remove(pingHeartbeat);
        pingHeartbeat.setAgent(null);
        return this;
    }

    public Datacenter getDatacenter() {
        return this.datacenter;
    }

    public void setDatacenter(Datacenter datacenter) {
        this.datacenter = datacenter;
    }

    public Agent datacenter(Datacenter datacenter) {
        this.setDatacenter(datacenter);
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
