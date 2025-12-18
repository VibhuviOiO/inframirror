package vibhuvi.oio.inframirror.service.dto;

import java.io.Serializable;

/**
 * DTO for agent registration response.
 */
public class AgentRegistrationResponseDTO implements Serializable {

    private Long agentId;
    private String apiKey;
    private RegionDTO region;
    private DatacenterDTO datacenter;
    private String status;
    private String message;

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public RegionDTO getRegion() {
        return region;
    }

    public void setRegion(RegionDTO region) {
        this.region = region;
    }

    public DatacenterDTO getDatacenter() {
        return datacenter;
    }

    public void setDatacenter(DatacenterDTO datacenter) {
        this.datacenter = datacenter;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
