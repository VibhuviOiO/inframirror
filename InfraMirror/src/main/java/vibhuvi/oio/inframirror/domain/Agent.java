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
    @JsonIgnoreProperties(value = { "agent", "monitoredService", "serviceInstance" }, allowSetters = true)
    private Set<ServiceHeartbeat> serviceHeartbeats = new HashSet<>();

    @Size(max = 255)
    @Column(name = "hostname", length = 255)
    private String hostname;

    @Size(max = 45)
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Size(max = 50)
    @Column(name = "os_type", length = 50)
    private String osType;

    @Size(max = 100)
    @Column(name = "os_version", length = 100)
    private String osVersion;

    @Size(max = 20)
    @Column(name = "agent_version", length = 20)
    private String agentVersion;

    @Column(name = "last_seen_at")
    private java.time.Instant lastSeenAt;

    @Size(max = 20)
    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "tags", columnDefinition = "jsonb")
    @org.hibernate.annotations.Type(JsonNodeType.class)
    private com.fasterxml.jackson.databind.JsonNode tags;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "region", "instances" }, allowSetters = true)
    private Datacenter datacenter;

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

    public String getHostname() {
        return this.hostname;
    }

    public Agent hostname(String hostname) {
        this.setHostname(hostname);
        return this;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public Agent ipAddress(String ipAddress) {
        this.setIpAddress(ipAddress);
        return this;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getOsType() {
        return this.osType;
    }

    public Agent osType(String osType) {
        this.setOsType(osType);
        return this;
    }

    public void setOsType(String osType) {
        this.osType = osType;
    }

    public String getOsVersion() {
        return this.osVersion;
    }

    public Agent osVersion(String osVersion) {
        this.setOsVersion(osVersion);
        return this;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getAgentVersion() {
        return this.agentVersion;
    }

    public Agent agentVersion(String agentVersion) {
        this.setAgentVersion(agentVersion);
        return this;
    }

    public void setAgentVersion(String agentVersion) {
        this.agentVersion = agentVersion;
    }

    public java.time.Instant getLastSeenAt() {
        return this.lastSeenAt;
    }

    public Agent lastSeenAt(java.time.Instant lastSeenAt) {
        this.setLastSeenAt(lastSeenAt);
        return this;
    }

    public void setLastSeenAt(java.time.Instant lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }

    public String getStatus() {
        return this.status;
    }

    public Agent status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public com.fasterxml.jackson.databind.JsonNode getTags() {
        return this.tags;
    }

    public Agent tags(com.fasterxml.jackson.databind.JsonNode tags) {
        this.setTags(tags);
        return this;
    }

    public void setTags(com.fasterxml.jackson.databind.JsonNode tags) {
        this.tags = tags;
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
