package vibhuvi.oio.inframirror.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link vibhuvi.oio.inframirror.domain.Agent} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AgentDTO implements Serializable {

    @NotNull
    private Long id;

    @NotNull
    @Size(max = 50)
    private String name;

    @Size(max = 255)
    private String hostname;

    @Size(max = 45)
    private String ipAddress;

    @Size(max = 50)
    private String osType;

    @Size(max = 100)
    private String osVersion;

    @Size(max = 20)
    private String agentVersion;

    private java.time.Instant lastSeenAt;

    @Size(max = 20)
    private String status;

    private com.fasterxml.jackson.databind.JsonNode tags;

    private Long datacenterId;

    private DatacenterDTO datacenter;

    private RegionDTO region;

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

    public RegionDTO getRegion() {
        return region;
    }

    public void setRegion(RegionDTO region) {
        this.region = region;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getOsType() {
        return osType;
    }

    public void setOsType(String osType) {
        this.osType = osType;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getAgentVersion() {
        return agentVersion;
    }

    public void setAgentVersion(String agentVersion) {
        this.agentVersion = agentVersion;
    }

    public java.time.Instant getLastSeenAt() {
        return lastSeenAt;
    }

    public void setLastSeenAt(java.time.Instant lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public com.fasterxml.jackson.databind.JsonNode getTags() {
        return tags;
    }

    public void setTags(com.fasterxml.jackson.databind.JsonNode tags) {
        this.tags = tags;
    }

    public Long getDatacenterId() {
        return datacenterId;
    }

    public void setDatacenterId(Long datacenterId) {
        this.datacenterId = datacenterId;
    }

    public DatacenterDTO getDatacenter() {
        return datacenter;
    }

    public void setDatacenter(DatacenterDTO datacenter) {
        this.datacenter = datacenter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AgentDTO)) {
            return false;
        }

        AgentDTO agentDTO = (AgentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, agentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AgentDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", region=" + getRegion() +
            "}";
    }
}
