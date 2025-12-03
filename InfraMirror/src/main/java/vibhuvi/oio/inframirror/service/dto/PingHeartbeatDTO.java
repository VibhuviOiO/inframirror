package vibhuvi.oio.inframirror.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link vibhuvi.oio.inframirror.domain.PingHeartbeat} entity.
 */
@Schema(description = "PingHeartbeat - Results from ping and hardware monitoring checks\nTable: ping_heartbeat")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PingHeartbeatDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant executedAt;

    @NotNull
    @Size(max = 20)
    private String heartbeatType;

    @NotNull
    private Boolean success;

    private Integer responseTimeMs;

    private Float packetLoss;

    private Integer jitterMs;

    private Float cpuUsage;

    private Float memoryUsage;

    private Float diskUsage;

    private Float loadAverage;

    private Integer processCount;

    private Long networkRxBytes;

    private Long networkTxBytes;

    private Long uptimeSeconds;

    @NotNull
    @Size(max = 20)
    private String status;

    @Lob
    private String errorMessage;

    @Size(max = 100)
    private String errorType;

    @Lob
    private String metadata;

    @NotNull
    private InstanceDTO instance;

    private AgentDTO agent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(Instant executedAt) {
        this.executedAt = executedAt;
    }

    public String getHeartbeatType() {
        return heartbeatType;
    }

    public void setHeartbeatType(String heartbeatType) {
        this.heartbeatType = heartbeatType;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getResponseTimeMs() {
        return responseTimeMs;
    }

    public void setResponseTimeMs(Integer responseTimeMs) {
        this.responseTimeMs = responseTimeMs;
    }

    public Float getPacketLoss() {
        return packetLoss;
    }

    public void setPacketLoss(Float packetLoss) {
        this.packetLoss = packetLoss;
    }

    public Integer getJitterMs() {
        return jitterMs;
    }

    public void setJitterMs(Integer jitterMs) {
        this.jitterMs = jitterMs;
    }

    public Float getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(Float cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public Float getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(Float memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public Float getDiskUsage() {
        return diskUsage;
    }

    public void setDiskUsage(Float diskUsage) {
        this.diskUsage = diskUsage;
    }

    public Float getLoadAverage() {
        return loadAverage;
    }

    public void setLoadAverage(Float loadAverage) {
        this.loadAverage = loadAverage;
    }

    public Integer getProcessCount() {
        return processCount;
    }

    public void setProcessCount(Integer processCount) {
        this.processCount = processCount;
    }

    public Long getNetworkRxBytes() {
        return networkRxBytes;
    }

    public void setNetworkRxBytes(Long networkRxBytes) {
        this.networkRxBytes = networkRxBytes;
    }

    public Long getNetworkTxBytes() {
        return networkTxBytes;
    }

    public void setNetworkTxBytes(Long networkTxBytes) {
        this.networkTxBytes = networkTxBytes;
    }

    public Long getUptimeSeconds() {
        return uptimeSeconds;
    }

    public void setUptimeSeconds(Long uptimeSeconds) {
        this.uptimeSeconds = uptimeSeconds;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public InstanceDTO getInstance() {
        return instance;
    }

    public void setInstance(InstanceDTO instance) {
        this.instance = instance;
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
        if (!(o instanceof PingHeartbeatDTO)) {
            return false;
        }

        PingHeartbeatDTO pingHeartbeatDTO = (PingHeartbeatDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, pingHeartbeatDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PingHeartbeatDTO{" +
            "id=" + getId() +
            ", executedAt='" + getExecutedAt() + "'" +
            ", heartbeatType='" + getHeartbeatType() + "'" +
            ", success='" + getSuccess() + "'" +
            ", responseTimeMs=" + getResponseTimeMs() +
            ", packetLoss=" + getPacketLoss() +
            ", jitterMs=" + getJitterMs() +
            ", cpuUsage=" + getCpuUsage() +
            ", memoryUsage=" + getMemoryUsage() +
            ", diskUsage=" + getDiskUsage() +
            ", loadAverage=" + getLoadAverage() +
            ", processCount=" + getProcessCount() +
            ", networkRxBytes=" + getNetworkRxBytes() +
            ", networkTxBytes=" + getNetworkTxBytes() +
            ", uptimeSeconds=" + getUptimeSeconds() +
            ", status='" + getStatus() + "'" +
            ", errorMessage='" + getErrorMessage() + "'" +
            ", errorType='" + getErrorType() + "'" +
            ", metadata='" + getMetadata() + "'" +
            ", instance=" + getInstance() +
            ", agent=" + getAgent() +
            "}";
    }
}
