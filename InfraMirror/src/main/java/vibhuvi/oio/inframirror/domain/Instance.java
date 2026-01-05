package vibhuvi.oio.inframirror.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import vibhuvi.oio.inframirror.domain.enumeration.InstanceType;
import vibhuvi.oio.inframirror.domain.enumeration.OperatingSystem;

/**
 * A Instance.
 */
@Entity
@Table(name = "instance")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Instance implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Size(max = 255)
    @Column(name = "name", length = 255, nullable = false)
    
    private String name;

    @NotNull
    @Size(max = 255)
    @Column(name = "hostname", length = 255, nullable = false)
    
    private String hostname;

    @Size(max = 500)
    @Column(name = "description", length = 500)
    
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "instance_type", nullable = false)
    
    private InstanceType instanceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "operating_system")
    
    private OperatingSystem operatingSystem;

    @Size(max = 100)
    @Column(name = "platform", length = 100)
    
    private String platform;

    @Size(max = 50)
    @Column(name = "private_ip_address", length = 50)
    
    private String privateIpAddress;

    @Size(max = 50)
    @Column(name = "public_ip_address", length = 50)
    
    private String publicIpAddress;

    @Column(name = "tags", columnDefinition = "TEXT")
    
    private String tags;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @NotNull
    @Column(name = "is_active", nullable = false)
    
    private Boolean isActive = true;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "instance")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "agent", "instance" }, allowSetters = true)
    private Set<InstanceHeartbeat> heartbeats = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "instance")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "heartbeats", "instance", "monitoredService" }, allowSetters = true)
    private Set<ServiceInstance> serviceInstances = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "instances", "monitoredServices", "region" }, allowSetters = true)
    private Datacenter datacenter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = { "instances", "httpHeartbeats", "instanceHeartbeats", "serviceHeartbeats", "region" },
        allowSetters = true
    )
    private Agent agent;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Instance id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Instance name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHostname() {
        return this.hostname;
    }

    public Instance hostname(String hostname) {
        this.setHostname(hostname);
        return this;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getDescription() {
        return this.description;
    }

    public Instance description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public InstanceType getInstanceType() {
        return this.instanceType;
    }

    public Instance instanceType(InstanceType instanceType) {
        this.setInstanceType(instanceType);
        return this;
    }

    public void setInstanceType(InstanceType instanceType) {
        this.instanceType = instanceType;
    }

    public OperatingSystem getOperatingSystem() {
        return this.operatingSystem;
    }

    public Instance operatingSystem(OperatingSystem operatingSystem) {
        this.setOperatingSystem(operatingSystem);
        return this;
    }

    public void setOperatingSystem(OperatingSystem operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public String getPlatform() {
        return this.platform;
    }

    public Instance platform(String platform) {
        this.setPlatform(platform);
        return this;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getPrivateIpAddress() {
        return this.privateIpAddress;
    }

    public Instance privateIpAddress(String privateIpAddress) {
        this.setPrivateIpAddress(privateIpAddress);
        return this;
    }

    public void setPrivateIpAddress(String privateIpAddress) {
        this.privateIpAddress = privateIpAddress;
    }

    public String getPublicIpAddress() {
        return this.publicIpAddress;
    }

    public Instance publicIpAddress(String publicIpAddress) {
        this.setPublicIpAddress(publicIpAddress);
        return this;
    }

    public void setPublicIpAddress(String publicIpAddress) {
        this.publicIpAddress = publicIpAddress;
    }

    public String getTags() {
        return this.tags;
    }

    public Instance tags(String tags) {
        this.setTags(tags);
        return this;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Instance createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public Instance updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<InstanceHeartbeat> getHeartbeats() {
        return this.heartbeats;
    }

    public void setHeartbeats(Set<InstanceHeartbeat> instanceHeartbeats) {
        if (this.heartbeats != null) {
            this.heartbeats.forEach(i -> i.setInstance(null));
        }
        if (instanceHeartbeats != null) {
            instanceHeartbeats.forEach(i -> i.setInstance(this));
        }
        this.heartbeats = instanceHeartbeats;
    }

    public Instance heartbeats(Set<InstanceHeartbeat> instanceHeartbeats) {
        this.setHeartbeats(instanceHeartbeats);
        return this;
    }

    public Instance addHeartbeat(InstanceHeartbeat instanceHeartbeat) {
        this.heartbeats.add(instanceHeartbeat);
        instanceHeartbeat.setInstance(this);
        return this;
    }

    public Instance removeHeartbeat(InstanceHeartbeat instanceHeartbeat) {
        this.heartbeats.remove(instanceHeartbeat);
        instanceHeartbeat.setInstance(null);
        return this;
    }

    public Set<ServiceInstance> getServiceInstances() {
        return this.serviceInstances;
    }

    public void setServiceInstances(Set<ServiceInstance> serviceInstances) {
        if (this.serviceInstances != null) {
            this.serviceInstances.forEach(i -> i.setInstance(null));
        }
        if (serviceInstances != null) {
            serviceInstances.forEach(i -> i.setInstance(this));
        }
        this.serviceInstances = serviceInstances;
    }

    public Instance serviceInstances(Set<ServiceInstance> serviceInstances) {
        this.setServiceInstances(serviceInstances);
        return this;
    }

    public Instance addServiceInstance(ServiceInstance serviceInstance) {
        this.serviceInstances.add(serviceInstance);
        serviceInstance.setInstance(this);
        return this;
    }

    public Instance removeServiceInstance(ServiceInstance serviceInstance) {
        this.serviceInstances.remove(serviceInstance);
        serviceInstance.setInstance(null);
        return this;
    }

    public Datacenter getDatacenter() {
        return this.datacenter;
    }

    public void setDatacenter(Datacenter datacenter) {
        this.datacenter = datacenter;
    }

    public Instance datacenter(Datacenter datacenter) {
        this.setDatacenter(datacenter);
        return this;
    }

    public Agent getAgent() {
        return this.agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public Instance agent(Agent agent) {
        this.setAgent(agent);
        return this;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public Instance isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Instance)) {
            return false;
        }
        return getId() != null && getId().equals(((Instance) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Instance{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", hostname='" + getHostname() + "'" +
            ", description='" + getDescription() + "'" +
            ", instanceType='" + getInstanceType() + "'" +
            ", operatingSystem='" + getOperatingSystem() + "'" +
            ", platform='" + getPlatform() + "'" +
            ", privateIpAddress='" + getPrivateIpAddress() + "'" +
            ", publicIpAddress='" + getPublicIpAddress() + "'" +
            ", tags='" + getTags() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
