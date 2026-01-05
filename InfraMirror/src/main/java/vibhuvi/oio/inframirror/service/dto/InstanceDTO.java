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
