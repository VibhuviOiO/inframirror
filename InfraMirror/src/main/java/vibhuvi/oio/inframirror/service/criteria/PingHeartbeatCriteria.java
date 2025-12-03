package vibhuvi.oio.inframirror.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link vibhuvi.oio.inframirror.domain.PingHeartbeat} entity. This class is used
 * in {@link vibhuvi.oio.inframirror.web.rest.PingHeartbeatResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /ping-heartbeats?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PingHeartbeatCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter executedAt;

    private StringFilter heartbeatType;

    private BooleanFilter success;

    private IntegerFilter responseTimeMs;

    private FloatFilter packetLoss;

    private IntegerFilter jitterMs;

    private FloatFilter cpuUsage;

    private FloatFilter memoryUsage;

    private FloatFilter diskUsage;

    private FloatFilter loadAverage;

    private IntegerFilter processCount;

    private LongFilter networkRxBytes;

    private LongFilter networkTxBytes;

    private LongFilter uptimeSeconds;

    private StringFilter status;

    private StringFilter errorType;

    private LongFilter instanceId;

    private LongFilter agentId;

    private Boolean distinct;

    public PingHeartbeatCriteria() {}

    public PingHeartbeatCriteria(PingHeartbeatCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.executedAt = other.optionalExecutedAt().map(InstantFilter::copy).orElse(null);
        this.heartbeatType = other.optionalHeartbeatType().map(StringFilter::copy).orElse(null);
        this.success = other.optionalSuccess().map(BooleanFilter::copy).orElse(null);
        this.responseTimeMs = other.optionalResponseTimeMs().map(IntegerFilter::copy).orElse(null);
        this.packetLoss = other.optionalPacketLoss().map(FloatFilter::copy).orElse(null);
        this.jitterMs = other.optionalJitterMs().map(IntegerFilter::copy).orElse(null);
        this.cpuUsage = other.optionalCpuUsage().map(FloatFilter::copy).orElse(null);
        this.memoryUsage = other.optionalMemoryUsage().map(FloatFilter::copy).orElse(null);
        this.diskUsage = other.optionalDiskUsage().map(FloatFilter::copy).orElse(null);
        this.loadAverage = other.optionalLoadAverage().map(FloatFilter::copy).orElse(null);
        this.processCount = other.optionalProcessCount().map(IntegerFilter::copy).orElse(null);
        this.networkRxBytes = other.optionalNetworkRxBytes().map(LongFilter::copy).orElse(null);
        this.networkTxBytes = other.optionalNetworkTxBytes().map(LongFilter::copy).orElse(null);
        this.uptimeSeconds = other.optionalUptimeSeconds().map(LongFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(StringFilter::copy).orElse(null);
        this.errorType = other.optionalErrorType().map(StringFilter::copy).orElse(null);
        this.instanceId = other.optionalInstanceId().map(LongFilter::copy).orElse(null);
        this.agentId = other.optionalAgentId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public PingHeartbeatCriteria copy() {
        return new PingHeartbeatCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public InstantFilter getExecutedAt() {
        return executedAt;
    }

    public Optional<InstantFilter> optionalExecutedAt() {
        return Optional.ofNullable(executedAt);
    }

    public InstantFilter executedAt() {
        if (executedAt == null) {
            setExecutedAt(new InstantFilter());
        }
        return executedAt;
    }

    public void setExecutedAt(InstantFilter executedAt) {
        this.executedAt = executedAt;
    }

    public StringFilter getHeartbeatType() {
        return heartbeatType;
    }

    public Optional<StringFilter> optionalHeartbeatType() {
        return Optional.ofNullable(heartbeatType);
    }

    public StringFilter heartbeatType() {
        if (heartbeatType == null) {
            setHeartbeatType(new StringFilter());
        }
        return heartbeatType;
    }

    public void setHeartbeatType(StringFilter heartbeatType) {
        this.heartbeatType = heartbeatType;
    }

    public BooleanFilter getSuccess() {
        return success;
    }

    public Optional<BooleanFilter> optionalSuccess() {
        return Optional.ofNullable(success);
    }

    public BooleanFilter success() {
        if (success == null) {
            setSuccess(new BooleanFilter());
        }
        return success;
    }

    public void setSuccess(BooleanFilter success) {
        this.success = success;
    }

    public IntegerFilter getResponseTimeMs() {
        return responseTimeMs;
    }

    public Optional<IntegerFilter> optionalResponseTimeMs() {
        return Optional.ofNullable(responseTimeMs);
    }

    public IntegerFilter responseTimeMs() {
        if (responseTimeMs == null) {
            setResponseTimeMs(new IntegerFilter());
        }
        return responseTimeMs;
    }

    public void setResponseTimeMs(IntegerFilter responseTimeMs) {
        this.responseTimeMs = responseTimeMs;
    }

    public FloatFilter getPacketLoss() {
        return packetLoss;
    }

    public Optional<FloatFilter> optionalPacketLoss() {
        return Optional.ofNullable(packetLoss);
    }

    public FloatFilter packetLoss() {
        if (packetLoss == null) {
            setPacketLoss(new FloatFilter());
        }
        return packetLoss;
    }

    public void setPacketLoss(FloatFilter packetLoss) {
        this.packetLoss = packetLoss;
    }

    public IntegerFilter getJitterMs() {
        return jitterMs;
    }

    public Optional<IntegerFilter> optionalJitterMs() {
        return Optional.ofNullable(jitterMs);
    }

    public IntegerFilter jitterMs() {
        if (jitterMs == null) {
            setJitterMs(new IntegerFilter());
        }
        return jitterMs;
    }

    public void setJitterMs(IntegerFilter jitterMs) {
        this.jitterMs = jitterMs;
    }

    public FloatFilter getCpuUsage() {
        return cpuUsage;
    }

    public Optional<FloatFilter> optionalCpuUsage() {
        return Optional.ofNullable(cpuUsage);
    }

    public FloatFilter cpuUsage() {
        if (cpuUsage == null) {
            setCpuUsage(new FloatFilter());
        }
        return cpuUsage;
    }

    public void setCpuUsage(FloatFilter cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public FloatFilter getMemoryUsage() {
        return memoryUsage;
    }

    public Optional<FloatFilter> optionalMemoryUsage() {
        return Optional.ofNullable(memoryUsage);
    }

    public FloatFilter memoryUsage() {
        if (memoryUsage == null) {
            setMemoryUsage(new FloatFilter());
        }
        return memoryUsage;
    }

    public void setMemoryUsage(FloatFilter memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public FloatFilter getDiskUsage() {
        return diskUsage;
    }

    public Optional<FloatFilter> optionalDiskUsage() {
        return Optional.ofNullable(diskUsage);
    }

    public FloatFilter diskUsage() {
        if (diskUsage == null) {
            setDiskUsage(new FloatFilter());
        }
        return diskUsage;
    }

    public void setDiskUsage(FloatFilter diskUsage) {
        this.diskUsage = diskUsage;
    }

    public FloatFilter getLoadAverage() {
        return loadAverage;
    }

    public Optional<FloatFilter> optionalLoadAverage() {
        return Optional.ofNullable(loadAverage);
    }

    public FloatFilter loadAverage() {
        if (loadAverage == null) {
            setLoadAverage(new FloatFilter());
        }
        return loadAverage;
    }

    public void setLoadAverage(FloatFilter loadAverage) {
        this.loadAverage = loadAverage;
    }

    public IntegerFilter getProcessCount() {
        return processCount;
    }

    public Optional<IntegerFilter> optionalProcessCount() {
        return Optional.ofNullable(processCount);
    }

    public IntegerFilter processCount() {
        if (processCount == null) {
            setProcessCount(new IntegerFilter());
        }
        return processCount;
    }

    public void setProcessCount(IntegerFilter processCount) {
        this.processCount = processCount;
    }

    public LongFilter getNetworkRxBytes() {
        return networkRxBytes;
    }

    public Optional<LongFilter> optionalNetworkRxBytes() {
        return Optional.ofNullable(networkRxBytes);
    }

    public LongFilter networkRxBytes() {
        if (networkRxBytes == null) {
            setNetworkRxBytes(new LongFilter());
        }
        return networkRxBytes;
    }

    public void setNetworkRxBytes(LongFilter networkRxBytes) {
        this.networkRxBytes = networkRxBytes;
    }

    public LongFilter getNetworkTxBytes() {
        return networkTxBytes;
    }

    public Optional<LongFilter> optionalNetworkTxBytes() {
        return Optional.ofNullable(networkTxBytes);
    }

    public LongFilter networkTxBytes() {
        if (networkTxBytes == null) {
            setNetworkTxBytes(new LongFilter());
        }
        return networkTxBytes;
    }

    public void setNetworkTxBytes(LongFilter networkTxBytes) {
        this.networkTxBytes = networkTxBytes;
    }

    public LongFilter getUptimeSeconds() {
        return uptimeSeconds;
    }

    public Optional<LongFilter> optionalUptimeSeconds() {
        return Optional.ofNullable(uptimeSeconds);
    }

    public LongFilter uptimeSeconds() {
        if (uptimeSeconds == null) {
            setUptimeSeconds(new LongFilter());
        }
        return uptimeSeconds;
    }

    public void setUptimeSeconds(LongFilter uptimeSeconds) {
        this.uptimeSeconds = uptimeSeconds;
    }

    public StringFilter getStatus() {
        return status;
    }

    public Optional<StringFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public StringFilter status() {
        if (status == null) {
            setStatus(new StringFilter());
        }
        return status;
    }

    public void setStatus(StringFilter status) {
        this.status = status;
    }

    public StringFilter getErrorType() {
        return errorType;
    }

    public Optional<StringFilter> optionalErrorType() {
        return Optional.ofNullable(errorType);
    }

    public StringFilter errorType() {
        if (errorType == null) {
            setErrorType(new StringFilter());
        }
        return errorType;
    }

    public void setErrorType(StringFilter errorType) {
        this.errorType = errorType;
    }

    public LongFilter getInstanceId() {
        return instanceId;
    }

    public Optional<LongFilter> optionalInstanceId() {
        return Optional.ofNullable(instanceId);
    }

    public LongFilter instanceId() {
        if (instanceId == null) {
            setInstanceId(new LongFilter());
        }
        return instanceId;
    }

    public void setInstanceId(LongFilter instanceId) {
        this.instanceId = instanceId;
    }

    public LongFilter getAgentId() {
        return agentId;
    }

    public Optional<LongFilter> optionalAgentId() {
        return Optional.ofNullable(agentId);
    }

    public LongFilter agentId() {
        if (agentId == null) {
            setAgentId(new LongFilter());
        }
        return agentId;
    }

    public void setAgentId(LongFilter agentId) {
        this.agentId = agentId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PingHeartbeatCriteria that = (PingHeartbeatCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(executedAt, that.executedAt) &&
            Objects.equals(heartbeatType, that.heartbeatType) &&
            Objects.equals(success, that.success) &&
            Objects.equals(responseTimeMs, that.responseTimeMs) &&
            Objects.equals(packetLoss, that.packetLoss) &&
            Objects.equals(jitterMs, that.jitterMs) &&
            Objects.equals(cpuUsage, that.cpuUsage) &&
            Objects.equals(memoryUsage, that.memoryUsage) &&
            Objects.equals(diskUsage, that.diskUsage) &&
            Objects.equals(loadAverage, that.loadAverage) &&
            Objects.equals(processCount, that.processCount) &&
            Objects.equals(networkRxBytes, that.networkRxBytes) &&
            Objects.equals(networkTxBytes, that.networkTxBytes) &&
            Objects.equals(uptimeSeconds, that.uptimeSeconds) &&
            Objects.equals(status, that.status) &&
            Objects.equals(errorType, that.errorType) &&
            Objects.equals(instanceId, that.instanceId) &&
            Objects.equals(agentId, that.agentId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            executedAt,
            heartbeatType,
            success,
            responseTimeMs,
            packetLoss,
            jitterMs,
            cpuUsage,
            memoryUsage,
            diskUsage,
            loadAverage,
            processCount,
            networkRxBytes,
            networkTxBytes,
            uptimeSeconds,
            status,
            errorType,
            instanceId,
            agentId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PingHeartbeatCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalExecutedAt().map(f -> "executedAt=" + f + ", ").orElse("") +
            optionalHeartbeatType().map(f -> "heartbeatType=" + f + ", ").orElse("") +
            optionalSuccess().map(f -> "success=" + f + ", ").orElse("") +
            optionalResponseTimeMs().map(f -> "responseTimeMs=" + f + ", ").orElse("") +
            optionalPacketLoss().map(f -> "packetLoss=" + f + ", ").orElse("") +
            optionalJitterMs().map(f -> "jitterMs=" + f + ", ").orElse("") +
            optionalCpuUsage().map(f -> "cpuUsage=" + f + ", ").orElse("") +
            optionalMemoryUsage().map(f -> "memoryUsage=" + f + ", ").orElse("") +
            optionalDiskUsage().map(f -> "diskUsage=" + f + ", ").orElse("") +
            optionalLoadAverage().map(f -> "loadAverage=" + f + ", ").orElse("") +
            optionalProcessCount().map(f -> "processCount=" + f + ", ").orElse("") +
            optionalNetworkRxBytes().map(f -> "networkRxBytes=" + f + ", ").orElse("") +
            optionalNetworkTxBytes().map(f -> "networkTxBytes=" + f + ", ").orElse("") +
            optionalUptimeSeconds().map(f -> "uptimeSeconds=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalErrorType().map(f -> "errorType=" + f + ", ").orElse("") +
            optionalInstanceId().map(f -> "instanceId=" + f + ", ").orElse("") +
            optionalAgentId().map(f -> "agentId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
