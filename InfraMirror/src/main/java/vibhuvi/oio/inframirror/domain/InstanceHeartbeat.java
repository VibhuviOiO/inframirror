package vibhuvi.oio.inframirror.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A InstanceHeartbeat.
 */
@Entity
@Table(name = "instance_heartbeat")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InstanceHeartbeat implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "executed_at", nullable = false)
    private Instant executedAt;

    @NotNull
    @Size(max = 20)
    @Column(name = "heartbeat_type", length = 20, nullable = false)
    private String heartbeatType;

    @NotNull
    @Column(name = "success", nullable = false)
    private Boolean success;

    @Column(name = "response_time_ms")
    private Integer responseTimeMs;

    @Column(name = "packet_loss")
    private Float packetLoss;

    @Column(name = "jitter_ms")
    private Integer jitterMs;

    @Column(name = "cpu_usage")
    private Float cpuUsage;

    @Column(name = "memory_usage")
    private Float memoryUsage;

    @Column(name = "disk_usage")
    private Float diskUsage;

    @Column(name = "load_average")
    private Float loadAverage;

    @Column(name = "process_count")
    private Integer processCount;

    @Column(name = "network_rx_bytes")
    private Long networkRxBytes;

    @Column(name = "network_tx_bytes")
    private Long networkTxBytes;

    @Column(name = "uptime_seconds")
    private Long uptimeSeconds;

    @NotNull
    @Size(max = 20)
    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Size(max = 100)
    @Column(name = "error_type", length = 100)
    private String errorType;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = { "instances", "httpHeartbeats", "instanceHeartbeats", "serviceHeartbeats", "region" },
        allowSetters = true
    )
    private Agent agent;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "heartbeats", "serviceInstances", "datacenter", "agent" }, allowSetters = true)
    private Instance instance;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public InstanceHeartbeat id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getExecutedAt() {
        return this.executedAt;
    }

    public InstanceHeartbeat executedAt(Instant executedAt) {
        this.setExecutedAt(executedAt);
        return this;
    }

    public void setExecutedAt(Instant executedAt) {
        this.executedAt = executedAt;
    }

    public String getHeartbeatType() {
        return this.heartbeatType;
    }

    public InstanceHeartbeat heartbeatType(String heartbeatType) {
        this.setHeartbeatType(heartbeatType);
        return this;
    }

    public void setHeartbeatType(String heartbeatType) {
        this.heartbeatType = heartbeatType;
    }

    public Boolean getSuccess() {
        return this.success;
    }

    public InstanceHeartbeat success(Boolean success) {
        this.setSuccess(success);
        return this;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getResponseTimeMs() {
        return this.responseTimeMs;
    }

    public InstanceHeartbeat responseTimeMs(Integer responseTimeMs) {
        this.setResponseTimeMs(responseTimeMs);
        return this;
    }

    public void setResponseTimeMs(Integer responseTimeMs) {
        this.responseTimeMs = responseTimeMs;
    }

    public Float getPacketLoss() {
        return this.packetLoss;
    }

    public InstanceHeartbeat packetLoss(Float packetLoss) {
        this.setPacketLoss(packetLoss);
        return this;
    }

    public void setPacketLoss(Float packetLoss) {
        this.packetLoss = packetLoss;
    }

    public Integer getJitterMs() {
        return this.jitterMs;
    }

    public InstanceHeartbeat jitterMs(Integer jitterMs) {
        this.setJitterMs(jitterMs);
        return this;
    }

    public void setJitterMs(Integer jitterMs) {
        this.jitterMs = jitterMs;
    }

    public Float getCpuUsage() {
        return this.cpuUsage;
    }

    public InstanceHeartbeat cpuUsage(Float cpuUsage) {
        this.setCpuUsage(cpuUsage);
        return this;
    }

    public void setCpuUsage(Float cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public Float getMemoryUsage() {
        return this.memoryUsage;
    }

    public InstanceHeartbeat memoryUsage(Float memoryUsage) {
        this.setMemoryUsage(memoryUsage);
        return this;
    }

    public void setMemoryUsage(Float memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public Float getDiskUsage() {
        return this.diskUsage;
    }

    public InstanceHeartbeat diskUsage(Float diskUsage) {
        this.setDiskUsage(diskUsage);
        return this;
    }

    public void setDiskUsage(Float diskUsage) {
        this.diskUsage = diskUsage;
    }

    public Float getLoadAverage() {
        return this.loadAverage;
    }

    public InstanceHeartbeat loadAverage(Float loadAverage) {
        this.setLoadAverage(loadAverage);
        return this;
    }

    public void setLoadAverage(Float loadAverage) {
        this.loadAverage = loadAverage;
    }

    public Integer getProcessCount() {
        return this.processCount;
    }

    public InstanceHeartbeat processCount(Integer processCount) {
        this.setProcessCount(processCount);
        return this;
    }

    public void setProcessCount(Integer processCount) {
        this.processCount = processCount;
    }

    public Long getNetworkRxBytes() {
        return this.networkRxBytes;
    }

    public InstanceHeartbeat networkRxBytes(Long networkRxBytes) {
        this.setNetworkRxBytes(networkRxBytes);
        return this;
    }

    public void setNetworkRxBytes(Long networkRxBytes) {
        this.networkRxBytes = networkRxBytes;
    }

    public Long getNetworkTxBytes() {
        return this.networkTxBytes;
    }

    public InstanceHeartbeat networkTxBytes(Long networkTxBytes) {
        this.setNetworkTxBytes(networkTxBytes);
        return this;
    }

    public void setNetworkTxBytes(Long networkTxBytes) {
        this.networkTxBytes = networkTxBytes;
    }

    public Long getUptimeSeconds() {
        return this.uptimeSeconds;
    }

    public InstanceHeartbeat uptimeSeconds(Long uptimeSeconds) {
        this.setUptimeSeconds(uptimeSeconds);
        return this;
    }

    public void setUptimeSeconds(Long uptimeSeconds) {
        this.uptimeSeconds = uptimeSeconds;
    }

    public String getStatus() {
        return this.status;
    }

    public InstanceHeartbeat status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public InstanceHeartbeat errorMessage(String errorMessage) {
        this.setErrorMessage(errorMessage);
        return this;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorType() {
        return this.errorType;
    }

    public InstanceHeartbeat errorType(String errorType) {
        this.setErrorType(errorType);
        return this;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getMetadata() {
        return this.metadata;
    }

    public InstanceHeartbeat metadata(String metadata) {
        this.setMetadata(metadata);
        return this;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public Agent getAgent() {
        return this.agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public InstanceHeartbeat agent(Agent agent) {
        this.setAgent(agent);
        return this;
    }

    public Instance getInstance() {
        return this.instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    public InstanceHeartbeat instance(Instance instance) {
        this.setInstance(instance);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InstanceHeartbeat)) {
            return false;
        }
        return getId() != null && getId().equals(((InstanceHeartbeat) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InstanceHeartbeat{" +
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
            "}";
    }
}
