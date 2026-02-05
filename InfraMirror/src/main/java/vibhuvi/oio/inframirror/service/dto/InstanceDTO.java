package vibhuvi.oio.inframirror.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link vibhuvi.oio.inframirror.domain.Instance} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InstanceDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String name;

    @NotNull
    @Size(max = 255)
    private String hostname;

    @Size(max = 500)
    private String description;

    @NotNull
    @Size(max = 50)
    private String instanceType;

    @Size(max = 100)
    private String operatingSystem;

    @Size(max = 100)
    private String platform;

    @Size(max = 50)
    private String privateIpAddress;

    @Size(max = 50)
    private String publicIpAddress;

    @Lob
    private String tags;

    private Instant createdAt;

    private Instant updatedAt;

    @NotNull
    private DatacenterDTO datacenter;

    private AgentDTO agent;

    private Integer pingInterval;

    private Integer pingTimeoutMs;

    private Integer pingRetryCount;

    private Boolean hardwareMonitoringEnabled;

    private Integer hardwareMonitoringInterval;

    private Integer cpuWarningThreshold;

    private Integer cpuDangerThreshold;

    private Integer memoryWarningThreshold;

    private Integer memoryDangerThreshold;

    private Integer diskWarningThreshold;

    private Integer diskDangerThreshold;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getPrivateIpAddress() {
        return privateIpAddress;
    }

    public void setPrivateIpAddress(String privateIpAddress) {
        this.privateIpAddress = privateIpAddress;
    }

    public String getPublicIpAddress() {
        return publicIpAddress;
    }

    public void setPublicIpAddress(String publicIpAddress) {
        this.publicIpAddress = publicIpAddress;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public DatacenterDTO getDatacenter() {
        return datacenter;
    }

    public void setDatacenter(DatacenterDTO datacenter) {
        this.datacenter = datacenter;
    }

    public AgentDTO getAgent() {
        return agent;
    }

    public void setAgent(AgentDTO agent) {
        this.agent = agent;
    }

    public Integer getPingInterval() {
        return pingInterval;
    }

    public void setPingInterval(Integer pingInterval) {
        this.pingInterval = pingInterval;
    }

    public Integer getPingTimeoutMs() {
        return pingTimeoutMs;
    }

    public void setPingTimeoutMs(Integer pingTimeoutMs) {
        this.pingTimeoutMs = pingTimeoutMs;
    }

    public Integer getPingRetryCount() {
        return pingRetryCount;
    }

    public void setPingRetryCount(Integer pingRetryCount) {
        this.pingRetryCount = pingRetryCount;
    }

    public Boolean getHardwareMonitoringEnabled() {
        return hardwareMonitoringEnabled;
    }

    public void setHardwareMonitoringEnabled(Boolean hardwareMonitoringEnabled) {
        this.hardwareMonitoringEnabled = hardwareMonitoringEnabled;
    }

    public Integer getHardwareMonitoringInterval() {
        return hardwareMonitoringInterval;
    }

    public void setHardwareMonitoringInterval(Integer hardwareMonitoringInterval) {
        this.hardwareMonitoringInterval = hardwareMonitoringInterval;
    }

    public Integer getCpuWarningThreshold() {
        return cpuWarningThreshold;
    }

    public void setCpuWarningThreshold(Integer cpuWarningThreshold) {
        this.cpuWarningThreshold = cpuWarningThreshold;
    }

    public Integer getCpuDangerThreshold() {
        return cpuDangerThreshold;
    }

    public void setCpuDangerThreshold(Integer cpuDangerThreshold) {
        this.cpuDangerThreshold = cpuDangerThreshold;
    }

    public Integer getMemoryWarningThreshold() {
        return memoryWarningThreshold;
    }

    public void setMemoryWarningThreshold(Integer memoryWarningThreshold) {
        this.memoryWarningThreshold = memoryWarningThreshold;
    }

    public Integer getMemoryDangerThreshold() {
        return memoryDangerThreshold;
    }

    public void setMemoryDangerThreshold(Integer memoryDangerThreshold) {
        this.memoryDangerThreshold = memoryDangerThreshold;
    }

    public Integer getDiskWarningThreshold() {
        return diskWarningThreshold;
    }

    public void setDiskWarningThreshold(Integer diskWarningThreshold) {
        this.diskWarningThreshold = diskWarningThreshold;
    }

    public Integer getDiskDangerThreshold() {
        return diskDangerThreshold;
    }

    public void setDiskDangerThreshold(Integer diskDangerThreshold) {
        this.diskDangerThreshold = diskDangerThreshold;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InstanceDTO)) {
            return false;
        }

        InstanceDTO instanceDTO = (InstanceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, instanceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InstanceDTO{" +
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
            ", datacenter=" + getDatacenter() +
            ", agent=" + getAgent() +
            "}";
    }
}
