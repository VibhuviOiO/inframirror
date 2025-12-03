package vibhuvi.oio.inframirror.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link vibhuvi.oio.inframirror.domain.Instance} entity. This class is used
 * in {@link vibhuvi.oio.inframirror.web.rest.InstanceResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /instances?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InstanceCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter hostname;

    private StringFilter description;

    private StringFilter instanceType;

    private StringFilter monitoringType;

    private StringFilter operatingSystem;

    private StringFilter platform;

    private StringFilter privateIpAddress;

    private StringFilter publicIpAddress;

    private BooleanFilter pingEnabled;

    private IntegerFilter pingInterval;

    private IntegerFilter pingTimeoutMs;

    private IntegerFilter pingRetryCount;

    private BooleanFilter hardwareMonitoringEnabled;

    private IntegerFilter hardwareMonitoringInterval;

    private IntegerFilter cpuWarningThreshold;

    private IntegerFilter cpuDangerThreshold;

    private IntegerFilter memoryWarningThreshold;

    private IntegerFilter memoryDangerThreshold;

    private IntegerFilter diskWarningThreshold;

    private IntegerFilter diskDangerThreshold;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private InstantFilter lastPingAt;

    private InstantFilter lastHardwareCheckAt;

    private LongFilter pingHeartbeatsId;

    private LongFilter datacenterId;

    private LongFilter agentId;

    private Boolean distinct;

    public InstanceCriteria() {}

    public InstanceCriteria(InstanceCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.hostname = other.optionalHostname().map(StringFilter::copy).orElse(null);
        this.description = other.optionalDescription().map(StringFilter::copy).orElse(null);
        this.instanceType = other.optionalInstanceType().map(StringFilter::copy).orElse(null);
        this.monitoringType = other.optionalMonitoringType().map(StringFilter::copy).orElse(null);
        this.operatingSystem = other.optionalOperatingSystem().map(StringFilter::copy).orElse(null);
        this.platform = other.optionalPlatform().map(StringFilter::copy).orElse(null);
        this.privateIpAddress = other.optionalPrivateIpAddress().map(StringFilter::copy).orElse(null);
        this.publicIpAddress = other.optionalPublicIpAddress().map(StringFilter::copy).orElse(null);
        this.pingEnabled = other.optionalPingEnabled().map(BooleanFilter::copy).orElse(null);
        this.pingInterval = other.optionalPingInterval().map(IntegerFilter::copy).orElse(null);
        this.pingTimeoutMs = other.optionalPingTimeoutMs().map(IntegerFilter::copy).orElse(null);
        this.pingRetryCount = other.optionalPingRetryCount().map(IntegerFilter::copy).orElse(null);
        this.hardwareMonitoringEnabled = other.optionalHardwareMonitoringEnabled().map(BooleanFilter::copy).orElse(null);
        this.hardwareMonitoringInterval = other.optionalHardwareMonitoringInterval().map(IntegerFilter::copy).orElse(null);
        this.cpuWarningThreshold = other.optionalCpuWarningThreshold().map(IntegerFilter::copy).orElse(null);
        this.cpuDangerThreshold = other.optionalCpuDangerThreshold().map(IntegerFilter::copy).orElse(null);
        this.memoryWarningThreshold = other.optionalMemoryWarningThreshold().map(IntegerFilter::copy).orElse(null);
        this.memoryDangerThreshold = other.optionalMemoryDangerThreshold().map(IntegerFilter::copy).orElse(null);
        this.diskWarningThreshold = other.optionalDiskWarningThreshold().map(IntegerFilter::copy).orElse(null);
        this.diskDangerThreshold = other.optionalDiskDangerThreshold().map(IntegerFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.lastPingAt = other.optionalLastPingAt().map(InstantFilter::copy).orElse(null);
        this.lastHardwareCheckAt = other.optionalLastHardwareCheckAt().map(InstantFilter::copy).orElse(null);
        this.pingHeartbeatsId = other.optionalPingHeartbeatsId().map(LongFilter::copy).orElse(null);
        this.datacenterId = other.optionalDatacenterId().map(LongFilter::copy).orElse(null);
        this.agentId = other.optionalAgentId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public InstanceCriteria copy() {
        return new InstanceCriteria(this);
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

    public StringFilter getName() {
        return name;
    }

    public Optional<StringFilter> optionalName() {
        return Optional.ofNullable(name);
    }

    public StringFilter name() {
        if (name == null) {
            setName(new StringFilter());
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getHostname() {
        return hostname;
    }

    public Optional<StringFilter> optionalHostname() {
        return Optional.ofNullable(hostname);
    }

    public StringFilter hostname() {
        if (hostname == null) {
            setHostname(new StringFilter());
        }
        return hostname;
    }

    public void setHostname(StringFilter hostname) {
        this.hostname = hostname;
    }

    public StringFilter getDescription() {
        return description;
    }

    public Optional<StringFilter> optionalDescription() {
        return Optional.ofNullable(description);
    }

    public StringFilter description() {
        if (description == null) {
            setDescription(new StringFilter());
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public StringFilter getInstanceType() {
        return instanceType;
    }

    public Optional<StringFilter> optionalInstanceType() {
        return Optional.ofNullable(instanceType);
    }

    public StringFilter instanceType() {
        if (instanceType == null) {
            setInstanceType(new StringFilter());
        }
        return instanceType;
    }

    public void setInstanceType(StringFilter instanceType) {
        this.instanceType = instanceType;
    }

    public StringFilter getMonitoringType() {
        return monitoringType;
    }

    public Optional<StringFilter> optionalMonitoringType() {
        return Optional.ofNullable(monitoringType);
    }

    public StringFilter monitoringType() {
        if (monitoringType == null) {
            setMonitoringType(new StringFilter());
        }
        return monitoringType;
    }

    public void setMonitoringType(StringFilter monitoringType) {
        this.monitoringType = monitoringType;
    }

    public StringFilter getOperatingSystem() {
        return operatingSystem;
    }

    public Optional<StringFilter> optionalOperatingSystem() {
        return Optional.ofNullable(operatingSystem);
    }

    public StringFilter operatingSystem() {
        if (operatingSystem == null) {
            setOperatingSystem(new StringFilter());
        }
        return operatingSystem;
    }

    public void setOperatingSystem(StringFilter operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public StringFilter getPlatform() {
        return platform;
    }

    public Optional<StringFilter> optionalPlatform() {
        return Optional.ofNullable(platform);
    }

    public StringFilter platform() {
        if (platform == null) {
            setPlatform(new StringFilter());
        }
        return platform;
    }

    public void setPlatform(StringFilter platform) {
        this.platform = platform;
    }

    public StringFilter getPrivateIpAddress() {
        return privateIpAddress;
    }

    public Optional<StringFilter> optionalPrivateIpAddress() {
        return Optional.ofNullable(privateIpAddress);
    }

    public StringFilter privateIpAddress() {
        if (privateIpAddress == null) {
            setPrivateIpAddress(new StringFilter());
        }
        return privateIpAddress;
    }

    public void setPrivateIpAddress(StringFilter privateIpAddress) {
        this.privateIpAddress = privateIpAddress;
    }

    public StringFilter getPublicIpAddress() {
        return publicIpAddress;
    }

    public Optional<StringFilter> optionalPublicIpAddress() {
        return Optional.ofNullable(publicIpAddress);
    }

    public StringFilter publicIpAddress() {
        if (publicIpAddress == null) {
            setPublicIpAddress(new StringFilter());
        }
        return publicIpAddress;
    }

    public void setPublicIpAddress(StringFilter publicIpAddress) {
        this.publicIpAddress = publicIpAddress;
    }

    public BooleanFilter getPingEnabled() {
        return pingEnabled;
    }

    public Optional<BooleanFilter> optionalPingEnabled() {
        return Optional.ofNullable(pingEnabled);
    }

    public BooleanFilter pingEnabled() {
        if (pingEnabled == null) {
            setPingEnabled(new BooleanFilter());
        }
        return pingEnabled;
    }

    public void setPingEnabled(BooleanFilter pingEnabled) {
        this.pingEnabled = pingEnabled;
    }

    public IntegerFilter getPingInterval() {
        return pingInterval;
    }

    public Optional<IntegerFilter> optionalPingInterval() {
        return Optional.ofNullable(pingInterval);
    }

    public IntegerFilter pingInterval() {
        if (pingInterval == null) {
            setPingInterval(new IntegerFilter());
        }
        return pingInterval;
    }

    public void setPingInterval(IntegerFilter pingInterval) {
        this.pingInterval = pingInterval;
    }

    public IntegerFilter getPingTimeoutMs() {
        return pingTimeoutMs;
    }

    public Optional<IntegerFilter> optionalPingTimeoutMs() {
        return Optional.ofNullable(pingTimeoutMs);
    }

    public IntegerFilter pingTimeoutMs() {
        if (pingTimeoutMs == null) {
            setPingTimeoutMs(new IntegerFilter());
        }
        return pingTimeoutMs;
    }

    public void setPingTimeoutMs(IntegerFilter pingTimeoutMs) {
        this.pingTimeoutMs = pingTimeoutMs;
    }

    public IntegerFilter getPingRetryCount() {
        return pingRetryCount;
    }

    public Optional<IntegerFilter> optionalPingRetryCount() {
        return Optional.ofNullable(pingRetryCount);
    }

    public IntegerFilter pingRetryCount() {
        if (pingRetryCount == null) {
            setPingRetryCount(new IntegerFilter());
        }
        return pingRetryCount;
    }

    public void setPingRetryCount(IntegerFilter pingRetryCount) {
        this.pingRetryCount = pingRetryCount;
    }

    public BooleanFilter getHardwareMonitoringEnabled() {
        return hardwareMonitoringEnabled;
    }

    public Optional<BooleanFilter> optionalHardwareMonitoringEnabled() {
        return Optional.ofNullable(hardwareMonitoringEnabled);
    }

    public BooleanFilter hardwareMonitoringEnabled() {
        if (hardwareMonitoringEnabled == null) {
            setHardwareMonitoringEnabled(new BooleanFilter());
        }
        return hardwareMonitoringEnabled;
    }

    public void setHardwareMonitoringEnabled(BooleanFilter hardwareMonitoringEnabled) {
        this.hardwareMonitoringEnabled = hardwareMonitoringEnabled;
    }

    public IntegerFilter getHardwareMonitoringInterval() {
        return hardwareMonitoringInterval;
    }

    public Optional<IntegerFilter> optionalHardwareMonitoringInterval() {
        return Optional.ofNullable(hardwareMonitoringInterval);
    }

    public IntegerFilter hardwareMonitoringInterval() {
        if (hardwareMonitoringInterval == null) {
            setHardwareMonitoringInterval(new IntegerFilter());
        }
        return hardwareMonitoringInterval;
    }

    public void setHardwareMonitoringInterval(IntegerFilter hardwareMonitoringInterval) {
        this.hardwareMonitoringInterval = hardwareMonitoringInterval;
    }

    public IntegerFilter getCpuWarningThreshold() {
        return cpuWarningThreshold;
    }

    public Optional<IntegerFilter> optionalCpuWarningThreshold() {
        return Optional.ofNullable(cpuWarningThreshold);
    }

    public IntegerFilter cpuWarningThreshold() {
        if (cpuWarningThreshold == null) {
            setCpuWarningThreshold(new IntegerFilter());
        }
        return cpuWarningThreshold;
    }

    public void setCpuWarningThreshold(IntegerFilter cpuWarningThreshold) {
        this.cpuWarningThreshold = cpuWarningThreshold;
    }

    public IntegerFilter getCpuDangerThreshold() {
        return cpuDangerThreshold;
    }

    public Optional<IntegerFilter> optionalCpuDangerThreshold() {
        return Optional.ofNullable(cpuDangerThreshold);
    }

    public IntegerFilter cpuDangerThreshold() {
        if (cpuDangerThreshold == null) {
            setCpuDangerThreshold(new IntegerFilter());
        }
        return cpuDangerThreshold;
    }

    public void setCpuDangerThreshold(IntegerFilter cpuDangerThreshold) {
        this.cpuDangerThreshold = cpuDangerThreshold;
    }

    public IntegerFilter getMemoryWarningThreshold() {
        return memoryWarningThreshold;
    }

    public Optional<IntegerFilter> optionalMemoryWarningThreshold() {
        return Optional.ofNullable(memoryWarningThreshold);
    }

    public IntegerFilter memoryWarningThreshold() {
        if (memoryWarningThreshold == null) {
            setMemoryWarningThreshold(new IntegerFilter());
        }
        return memoryWarningThreshold;
    }

    public void setMemoryWarningThreshold(IntegerFilter memoryWarningThreshold) {
        this.memoryWarningThreshold = memoryWarningThreshold;
    }

    public IntegerFilter getMemoryDangerThreshold() {
        return memoryDangerThreshold;
    }

    public Optional<IntegerFilter> optionalMemoryDangerThreshold() {
        return Optional.ofNullable(memoryDangerThreshold);
    }

    public IntegerFilter memoryDangerThreshold() {
        if (memoryDangerThreshold == null) {
            setMemoryDangerThreshold(new IntegerFilter());
        }
        return memoryDangerThreshold;
    }

    public void setMemoryDangerThreshold(IntegerFilter memoryDangerThreshold) {
        this.memoryDangerThreshold = memoryDangerThreshold;
    }

    public IntegerFilter getDiskWarningThreshold() {
        return diskWarningThreshold;
    }

    public Optional<IntegerFilter> optionalDiskWarningThreshold() {
        return Optional.ofNullable(diskWarningThreshold);
    }

    public IntegerFilter diskWarningThreshold() {
        if (diskWarningThreshold == null) {
            setDiskWarningThreshold(new IntegerFilter());
        }
        return diskWarningThreshold;
    }

    public void setDiskWarningThreshold(IntegerFilter diskWarningThreshold) {
        this.diskWarningThreshold = diskWarningThreshold;
    }

    public IntegerFilter getDiskDangerThreshold() {
        return diskDangerThreshold;
    }

    public Optional<IntegerFilter> optionalDiskDangerThreshold() {
        return Optional.ofNullable(diskDangerThreshold);
    }

    public IntegerFilter diskDangerThreshold() {
        if (diskDangerThreshold == null) {
            setDiskDangerThreshold(new IntegerFilter());
        }
        return diskDangerThreshold;
    }

    public void setDiskDangerThreshold(IntegerFilter diskDangerThreshold) {
        this.diskDangerThreshold = diskDangerThreshold;
    }

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public Optional<InstantFilter> optionalCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            setCreatedAt(new InstantFilter());
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public InstantFilter getUpdatedAt() {
        return updatedAt;
    }

    public Optional<InstantFilter> optionalUpdatedAt() {
        return Optional.ofNullable(updatedAt);
    }

    public InstantFilter updatedAt() {
        if (updatedAt == null) {
            setUpdatedAt(new InstantFilter());
        }
        return updatedAt;
    }

    public void setUpdatedAt(InstantFilter updatedAt) {
        this.updatedAt = updatedAt;
    }

    public InstantFilter getLastPingAt() {
        return lastPingAt;
    }

    public Optional<InstantFilter> optionalLastPingAt() {
        return Optional.ofNullable(lastPingAt);
    }

    public InstantFilter lastPingAt() {
        if (lastPingAt == null) {
            setLastPingAt(new InstantFilter());
        }
        return lastPingAt;
    }

    public void setLastPingAt(InstantFilter lastPingAt) {
        this.lastPingAt = lastPingAt;
    }

    public InstantFilter getLastHardwareCheckAt() {
        return lastHardwareCheckAt;
    }

    public Optional<InstantFilter> optionalLastHardwareCheckAt() {
        return Optional.ofNullable(lastHardwareCheckAt);
    }

    public InstantFilter lastHardwareCheckAt() {
        if (lastHardwareCheckAt == null) {
            setLastHardwareCheckAt(new InstantFilter());
        }
        return lastHardwareCheckAt;
    }

    public void setLastHardwareCheckAt(InstantFilter lastHardwareCheckAt) {
        this.lastHardwareCheckAt = lastHardwareCheckAt;
    }

    public LongFilter getPingHeartbeatsId() {
        return pingHeartbeatsId;
    }

    public Optional<LongFilter> optionalPingHeartbeatsId() {
        return Optional.ofNullable(pingHeartbeatsId);
    }

    public LongFilter pingHeartbeatsId() {
        if (pingHeartbeatsId == null) {
            setPingHeartbeatsId(new LongFilter());
        }
        return pingHeartbeatsId;
    }

    public void setPingHeartbeatsId(LongFilter pingHeartbeatsId) {
        this.pingHeartbeatsId = pingHeartbeatsId;
    }

    public LongFilter getDatacenterId() {
        return datacenterId;
    }

    public Optional<LongFilter> optionalDatacenterId() {
        return Optional.ofNullable(datacenterId);
    }

    public LongFilter datacenterId() {
        if (datacenterId == null) {
            setDatacenterId(new LongFilter());
        }
        return datacenterId;
    }

    public void setDatacenterId(LongFilter datacenterId) {
        this.datacenterId = datacenterId;
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
        final InstanceCriteria that = (InstanceCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(hostname, that.hostname) &&
            Objects.equals(description, that.description) &&
            Objects.equals(instanceType, that.instanceType) &&
            Objects.equals(monitoringType, that.monitoringType) &&
            Objects.equals(operatingSystem, that.operatingSystem) &&
            Objects.equals(platform, that.platform) &&
            Objects.equals(privateIpAddress, that.privateIpAddress) &&
            Objects.equals(publicIpAddress, that.publicIpAddress) &&
            Objects.equals(pingEnabled, that.pingEnabled) &&
            Objects.equals(pingInterval, that.pingInterval) &&
            Objects.equals(pingTimeoutMs, that.pingTimeoutMs) &&
            Objects.equals(pingRetryCount, that.pingRetryCount) &&
            Objects.equals(hardwareMonitoringEnabled, that.hardwareMonitoringEnabled) &&
            Objects.equals(hardwareMonitoringInterval, that.hardwareMonitoringInterval) &&
            Objects.equals(cpuWarningThreshold, that.cpuWarningThreshold) &&
            Objects.equals(cpuDangerThreshold, that.cpuDangerThreshold) &&
            Objects.equals(memoryWarningThreshold, that.memoryWarningThreshold) &&
            Objects.equals(memoryDangerThreshold, that.memoryDangerThreshold) &&
            Objects.equals(diskWarningThreshold, that.diskWarningThreshold) &&
            Objects.equals(diskDangerThreshold, that.diskDangerThreshold) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(lastPingAt, that.lastPingAt) &&
            Objects.equals(lastHardwareCheckAt, that.lastHardwareCheckAt) &&
            Objects.equals(pingHeartbeatsId, that.pingHeartbeatsId) &&
            Objects.equals(datacenterId, that.datacenterId) &&
            Objects.equals(agentId, that.agentId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            name,
            hostname,
            description,
            instanceType,
            monitoringType,
            operatingSystem,
            platform,
            privateIpAddress,
            publicIpAddress,
            pingEnabled,
            pingInterval,
            pingTimeoutMs,
            pingRetryCount,
            hardwareMonitoringEnabled,
            hardwareMonitoringInterval,
            cpuWarningThreshold,
            cpuDangerThreshold,
            memoryWarningThreshold,
            memoryDangerThreshold,
            diskWarningThreshold,
            diskDangerThreshold,
            createdAt,
            updatedAt,
            lastPingAt,
            lastHardwareCheckAt,
            pingHeartbeatsId,
            datacenterId,
            agentId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InstanceCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalHostname().map(f -> "hostname=" + f + ", ").orElse("") +
            optionalDescription().map(f -> "description=" + f + ", ").orElse("") +
            optionalInstanceType().map(f -> "instanceType=" + f + ", ").orElse("") +
            optionalMonitoringType().map(f -> "monitoringType=" + f + ", ").orElse("") +
            optionalOperatingSystem().map(f -> "operatingSystem=" + f + ", ").orElse("") +
            optionalPlatform().map(f -> "platform=" + f + ", ").orElse("") +
            optionalPrivateIpAddress().map(f -> "privateIpAddress=" + f + ", ").orElse("") +
            optionalPublicIpAddress().map(f -> "publicIpAddress=" + f + ", ").orElse("") +
            optionalPingEnabled().map(f -> "pingEnabled=" + f + ", ").orElse("") +
            optionalPingInterval().map(f -> "pingInterval=" + f + ", ").orElse("") +
            optionalPingTimeoutMs().map(f -> "pingTimeoutMs=" + f + ", ").orElse("") +
            optionalPingRetryCount().map(f -> "pingRetryCount=" + f + ", ").orElse("") +
            optionalHardwareMonitoringEnabled().map(f -> "hardwareMonitoringEnabled=" + f + ", ").orElse("") +
            optionalHardwareMonitoringInterval().map(f -> "hardwareMonitoringInterval=" + f + ", ").orElse("") +
            optionalCpuWarningThreshold().map(f -> "cpuWarningThreshold=" + f + ", ").orElse("") +
            optionalCpuDangerThreshold().map(f -> "cpuDangerThreshold=" + f + ", ").orElse("") +
            optionalMemoryWarningThreshold().map(f -> "memoryWarningThreshold=" + f + ", ").orElse("") +
            optionalMemoryDangerThreshold().map(f -> "memoryDangerThreshold=" + f + ", ").orElse("") +
            optionalDiskWarningThreshold().map(f -> "diskWarningThreshold=" + f + ", ").orElse("") +
            optionalDiskDangerThreshold().map(f -> "diskDangerThreshold=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalLastPingAt().map(f -> "lastPingAt=" + f + ", ").orElse("") +
            optionalLastHardwareCheckAt().map(f -> "lastHardwareCheckAt=" + f + ", ").orElse("") +
            optionalPingHeartbeatsId().map(f -> "pingHeartbeatsId=" + f + ", ").orElse("") +
            optionalDatacenterId().map(f -> "datacenterId=" + f + ", ").orElse("") +
            optionalAgentId().map(f -> "agentId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
