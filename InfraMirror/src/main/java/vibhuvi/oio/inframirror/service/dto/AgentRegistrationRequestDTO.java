package vibhuvi.oio.inframirror.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Map;

/**
 * DTO for agent self-registration request.
 */
public class AgentRegistrationRequestDTO implements Serializable {

    @NotNull
    @Size(max = 200)
    private String name;

    @NotNull
    @Size(max = 255)
    private String hostname;

    @NotNull
    @Size(max = 45)
    private String ipAddress;

    @NotNull
    @Size(max = 50)
    private String osType;

    @Size(max = 100)
    private String osVersion;

    @NotNull
    @Size(max = 20)
    private String agentVersion;

    private Map<String, String> tags;

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

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }
}
