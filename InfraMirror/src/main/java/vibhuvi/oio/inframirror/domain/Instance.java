package vibhuvi.oio.inframirror.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A Instance.
 */
@Entity
@Table(name = "instance")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "instance")
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
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String name;

    @NotNull
    @Size(max = 255)
    @Column(name = "hostname", length = 255, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String hostname;

    @Size(max = 500)
    @Column(name = "description", length = 500)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String description;

    @NotNull
    @Size(max = 50)
    @Column(name = "instance_type", length = 50, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String instanceType;

    @NotNull
    @Size(max = 50)
    @Column(name = "monitoring_type", length = 50, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String monitoringType;

    @Size(max = 100)
    @Column(name = "operating_system", length = 100)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String operatingSystem;

    @Size(max = 100)
    @Column(name = "platform", length = 100)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String platform;

    @Size(max = 50)
    @Column(name = "private_ip_address", length = 50)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String privateIpAddress;

    @Size(max = 50)
    @Column(name = "public_ip_address", length = 50)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String publicIpAddress;

    @Lob
    @Column(name = "tags")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String tags;

    @NotNull
    @Column(name = "ping_enabled", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean pingEnabled;

    @NotNull
    @Column(name = "ping_interval", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer pingInterval;

    @NotNull
    @Column(name = "ping_timeout_ms", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer pingTimeoutMs;

    @NotNull
    @Column(name = "ping_retry_count", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer pingRetryCount;

    @NotNull
    @Column(name = "hardware_monitoring_enabled", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean hardwareMonitoringEnabled;

    @NotNull
    @Column(name = "hardware_monitoring_interval", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer hardwareMonitoringInterval;

    @NotNull
    @Column(name = "cpu_warning_threshold", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer cpuWarningThreshold;

    @NotNull
    @Column(name = "cpu_danger_threshold", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer cpuDangerThreshold;

    @NotNull
    @Column(name = "memory_warning_threshold", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer memoryWarningThreshold;

    @NotNull
    @Column(name = "memory_danger_threshold", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer memoryDangerThreshold;

    @NotNull
    @Column(name = "disk_warning_threshold", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer diskWarningThreshold;

    @NotNull
    @Column(name = "disk_danger_threshold", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer diskDangerThreshold;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "last_ping_at")
    private Instant lastPingAt;

    @Column(name = "last_hardware_check_at")
    private Instant lastHardwareCheckAt;

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

    public String getInstanceType() {
        return this.instanceType;
    }

    public Instance instanceType(String instanceType) {
        this.setInstanceType(instanceType);
        return this;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public String getMonitoringType() {
        return this.monitoringType;
    }

    public Instance monitoringType(String monitoringType) {
        this.setMonitoringType(monitoringType);
        return this;
    }

    public void setMonitoringType(String monitoringType) {
        this.monitoringType = monitoringType;
    }

    public String getOperatingSystem() {
        return this.operatingSystem;
    }

    public Instance operatingSystem(String operatingSystem) {
        this.setOperatingSystem(operatingSystem);
        return this;
    }

    public void setOperatingSystem(String operatingSystem) {
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

    public Boolean getPingEnabled() {
        return this.pingEnabled;
    }

    public Instance pingEnabled(Boolean pingEnabled) {
        this.setPingEnabled(pingEnabled);
        return this;
    }

    public void setPingEnabled(Boolean pingEnabled) {
        this.pingEnabled = pingEnabled;
    }

    public Integer getPingInterval() {
        return this.pingInterval;
    }

    public Instance pingInterval(Integer pingInterval) {
        this.setPingInterval(pingInterval);
        return this;
    }

    public void setPingInterval(Integer pingInterval) {
        this.pingInterval = pingInterval;
    }

    public Integer getPingTimeoutMs() {
        return this.pingTimeoutMs;
    }

    public Instance pingTimeoutMs(Integer pingTimeoutMs) {
        this.setPingTimeoutMs(pingTimeoutMs);
        return this;
    }

    public void setPingTimeoutMs(Integer pingTimeoutMs) {
        this.pingTimeoutMs = pingTimeoutMs;
    }

    public Integer getPingRetryCount() {
        return this.pingRetryCount;
    }

    public Instance pingRetryCount(Integer pingRetryCount) {
        this.setPingRetryCount(pingRetryCount);
        return this;
    }

    public void setPingRetryCount(Integer pingRetryCount) {
        this.pingRetryCount = pingRetryCount;
    }

    public Boolean getHardwareMonitoringEnabled() {
        return this.hardwareMonitoringEnabled;
    }

    public Instance hardwareMonitoringEnabled(Boolean hardwareMonitoringEnabled) {
        this.setHardwareMonitoringEnabled(hardwareMonitoringEnabled);
        return this;
    }

    public void setHardwareMonitoringEnabled(Boolean hardwareMonitoringEnabled) {
        this.hardwareMonitoringEnabled = hardwareMonitoringEnabled;
    }

    public Integer getHardwareMonitoringInterval() {
        return this.hardwareMonitoringInterval;
    }

    public Instance hardwareMonitoringInterval(Integer hardwareMonitoringInterval) {
        this.setHardwareMonitoringInterval(hardwareMonitoringInterval);
        return this;
    }

    public void setHardwareMonitoringInterval(Integer hardwareMonitoringInterval) {
        this.hardwareMonitoringInterval = hardwareMonitoringInterval;
    }

    public Integer getCpuWarningThreshold() {
        return this.cpuWarningThreshold;
    }

    public Instance cpuWarningThreshold(Integer cpuWarningThreshold) {
        this.setCpuWarningThreshold(cpuWarningThreshold);
        return this;
    }

    public void setCpuWarningThreshold(Integer cpuWarningThreshold) {
        this.cpuWarningThreshold = cpuWarningThreshold;
    }

    public Integer getCpuDangerThreshold() {
        return this.cpuDangerThreshold;
    }

    public Instance cpuDangerThreshold(Integer cpuDangerThreshold) {
        this.setCpuDangerThreshold(cpuDangerThreshold);
        return this;
    }

    public void setCpuDangerThreshold(Integer cpuDangerThreshold) {
        this.cpuDangerThreshold = cpuDangerThreshold;
    }

    public Integer getMemoryWarningThreshold() {
        return this.memoryWarningThreshold;
    }

    public Instance memoryWarningThreshold(Integer memoryWarningThreshold) {
        this.setMemoryWarningThreshold(memoryWarningThreshold);
        return this;
    }

    public void setMemoryWarningThreshold(Integer memoryWarningThreshold) {
        this.memoryWarningThreshold = memoryWarningThreshold;
    }

    public Integer getMemoryDangerThreshold() {
        return this.memoryDangerThreshold;
    }

    public Instance memoryDangerThreshold(Integer memoryDangerThreshold) {
        this.setMemoryDangerThreshold(memoryDangerThreshold);
        return this;
    }

    public void setMemoryDangerThreshold(Integer memoryDangerThreshold) {
        this.memoryDangerThreshold = memoryDangerThreshold;
    }

    public Integer getDiskWarningThreshold() {
        return this.diskWarningThreshold;
    }

    public Instance diskWarningThreshold(Integer diskWarningThreshold) {
        this.setDiskWarningThreshold(diskWarningThreshold);
        return this;
    }

    public void setDiskWarningThreshold(Integer diskWarningThreshold) {
        this.diskWarningThreshold = diskWarningThreshold;
    }

    public Integer getDiskDangerThreshold() {
        return this.diskDangerThreshold;
    }

    public Instance diskDangerThreshold(Integer diskDangerThreshold) {
        this.setDiskDangerThreshold(diskDangerThreshold);
        return this;
    }

    public void setDiskDangerThreshold(Integer diskDangerThreshold) {
        this.diskDangerThreshold = diskDangerThreshold;
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

    public Instant getLastPingAt() {
        return this.lastPingAt;
    }

    public Instance lastPingAt(Instant lastPingAt) {
        this.setLastPingAt(lastPingAt);
        return this;
    }

    public void setLastPingAt(Instant lastPingAt) {
        this.lastPingAt = lastPingAt;
    }

    public Instant getLastHardwareCheckAt() {
        return this.lastHardwareCheckAt;
    }

    public Instance lastHardwareCheckAt(Instant lastHardwareCheckAt) {
        this.setLastHardwareCheckAt(lastHardwareCheckAt);
        return this;
    }

    public void setLastHardwareCheckAt(Instant lastHardwareCheckAt) {
        this.lastHardwareCheckAt = lastHardwareCheckAt;
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
            ", monitoringType='" + getMonitoringType() + "'" +
            ", operatingSystem='" + getOperatingSystem() + "'" +
            ", platform='" + getPlatform() + "'" +
            ", privateIpAddress='" + getPrivateIpAddress() + "'" +
            ", publicIpAddress='" + getPublicIpAddress() + "'" +
            ", tags='" + getTags() + "'" +
            ", pingEnabled='" + getPingEnabled() + "'" +
            ", pingInterval=" + getPingInterval() +
            ", pingTimeoutMs=" + getPingTimeoutMs() +
            ", pingRetryCount=" + getPingRetryCount() +
            ", hardwareMonitoringEnabled='" + getHardwareMonitoringEnabled() + "'" +
            ", hardwareMonitoringInterval=" + getHardwareMonitoringInterval() +
            ", cpuWarningThreshold=" + getCpuWarningThreshold() +
            ", cpuDangerThreshold=" + getCpuDangerThreshold() +
            ", memoryWarningThreshold=" + getMemoryWarningThreshold() +
            ", memoryDangerThreshold=" + getMemoryDangerThreshold() +
            ", diskWarningThreshold=" + getDiskWarningThreshold() +
            ", diskDangerThreshold=" + getDiskDangerThreshold() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", lastPingAt='" + getLastPingAt() + "'" +
            ", lastHardwareCheckAt='" + getLastHardwareCheckAt() + "'" +
            "}";
    }
}
