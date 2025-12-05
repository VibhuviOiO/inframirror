package vibhuvi.oio.inframirror.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link vibhuvi.oio.inframirror.domain.MonitoredService} entity. This class is used
 * in {@link vibhuvi.oio.inframirror.web.rest.MonitoredServiceResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /monitored-services?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MonitoredServiceCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter description;

    private StringFilter serviceType;

    private StringFilter environment;

    private BooleanFilter monitoringEnabled;

    private BooleanFilter clusterMonitoringEnabled;

    private IntegerFilter intervalSeconds;

    private IntegerFilter timeoutMs;

    private IntegerFilter retryCount;

    private IntegerFilter latencyWarningMs;

    private IntegerFilter latencyCriticalMs;

    private BooleanFilter isActive;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private LongFilter serviceInstanceId;

    private LongFilter heartbeatId;

    private LongFilter datacenterId;

    private Boolean distinct;

    public MonitoredServiceCriteria() {}

    public MonitoredServiceCriteria(MonitoredServiceCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.description = other.optionalDescription().map(StringFilter::copy).orElse(null);
        this.serviceType = other.optionalServiceType().map(StringFilter::copy).orElse(null);
        this.environment = other.optionalEnvironment().map(StringFilter::copy).orElse(null);
        this.monitoringEnabled = other.optionalMonitoringEnabled().map(BooleanFilter::copy).orElse(null);
        this.clusterMonitoringEnabled = other.optionalClusterMonitoringEnabled().map(BooleanFilter::copy).orElse(null);
        this.intervalSeconds = other.optionalIntervalSeconds().map(IntegerFilter::copy).orElse(null);
        this.timeoutMs = other.optionalTimeoutMs().map(IntegerFilter::copy).orElse(null);
        this.retryCount = other.optionalRetryCount().map(IntegerFilter::copy).orElse(null);
        this.latencyWarningMs = other.optionalLatencyWarningMs().map(IntegerFilter::copy).orElse(null);
        this.latencyCriticalMs = other.optionalLatencyCriticalMs().map(IntegerFilter::copy).orElse(null);
        this.isActive = other.optionalIsActive().map(BooleanFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.serviceInstanceId = other.optionalServiceInstanceId().map(LongFilter::copy).orElse(null);
        this.heartbeatId = other.optionalHeartbeatId().map(LongFilter::copy).orElse(null);
        this.datacenterId = other.optionalDatacenterId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public MonitoredServiceCriteria copy() {
        return new MonitoredServiceCriteria(this);
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

    public StringFilter getServiceType() {
        return serviceType;
    }

    public Optional<StringFilter> optionalServiceType() {
        return Optional.ofNullable(serviceType);
    }

    public StringFilter serviceType() {
        if (serviceType == null) {
            setServiceType(new StringFilter());
        }
        return serviceType;
    }

    public void setServiceType(StringFilter serviceType) {
        this.serviceType = serviceType;
    }

    public StringFilter getEnvironment() {
        return environment;
    }

    public Optional<StringFilter> optionalEnvironment() {
        return Optional.ofNullable(environment);
    }

    public StringFilter environment() {
        if (environment == null) {
            setEnvironment(new StringFilter());
        }
        return environment;
    }

    public void setEnvironment(StringFilter environment) {
        this.environment = environment;
    }

    public BooleanFilter getMonitoringEnabled() {
        return monitoringEnabled;
    }

    public Optional<BooleanFilter> optionalMonitoringEnabled() {
        return Optional.ofNullable(monitoringEnabled);
    }

    public BooleanFilter monitoringEnabled() {
        if (monitoringEnabled == null) {
            setMonitoringEnabled(new BooleanFilter());
        }
        return monitoringEnabled;
    }

    public void setMonitoringEnabled(BooleanFilter monitoringEnabled) {
        this.monitoringEnabled = monitoringEnabled;
    }

    public BooleanFilter getClusterMonitoringEnabled() {
        return clusterMonitoringEnabled;
    }

    public Optional<BooleanFilter> optionalClusterMonitoringEnabled() {
        return Optional.ofNullable(clusterMonitoringEnabled);
    }

    public BooleanFilter clusterMonitoringEnabled() {
        if (clusterMonitoringEnabled == null) {
            setClusterMonitoringEnabled(new BooleanFilter());
        }
        return clusterMonitoringEnabled;
    }

    public void setClusterMonitoringEnabled(BooleanFilter clusterMonitoringEnabled) {
        this.clusterMonitoringEnabled = clusterMonitoringEnabled;
    }

    public IntegerFilter getIntervalSeconds() {
        return intervalSeconds;
    }

    public Optional<IntegerFilter> optionalIntervalSeconds() {
        return Optional.ofNullable(intervalSeconds);
    }

    public IntegerFilter intervalSeconds() {
        if (intervalSeconds == null) {
            setIntervalSeconds(new IntegerFilter());
        }
        return intervalSeconds;
    }

    public void setIntervalSeconds(IntegerFilter intervalSeconds) {
        this.intervalSeconds = intervalSeconds;
    }

    public IntegerFilter getTimeoutMs() {
        return timeoutMs;
    }

    public Optional<IntegerFilter> optionalTimeoutMs() {
        return Optional.ofNullable(timeoutMs);
    }

    public IntegerFilter timeoutMs() {
        if (timeoutMs == null) {
            setTimeoutMs(new IntegerFilter());
        }
        return timeoutMs;
    }

    public void setTimeoutMs(IntegerFilter timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public IntegerFilter getRetryCount() {
        return retryCount;
    }

    public Optional<IntegerFilter> optionalRetryCount() {
        return Optional.ofNullable(retryCount);
    }

    public IntegerFilter retryCount() {
        if (retryCount == null) {
            setRetryCount(new IntegerFilter());
        }
        return retryCount;
    }

    public void setRetryCount(IntegerFilter retryCount) {
        this.retryCount = retryCount;
    }

    public IntegerFilter getLatencyWarningMs() {
        return latencyWarningMs;
    }

    public Optional<IntegerFilter> optionalLatencyWarningMs() {
        return Optional.ofNullable(latencyWarningMs);
    }

    public IntegerFilter latencyWarningMs() {
        if (latencyWarningMs == null) {
            setLatencyWarningMs(new IntegerFilter());
        }
        return latencyWarningMs;
    }

    public void setLatencyWarningMs(IntegerFilter latencyWarningMs) {
        this.latencyWarningMs = latencyWarningMs;
    }

    public IntegerFilter getLatencyCriticalMs() {
        return latencyCriticalMs;
    }

    public Optional<IntegerFilter> optionalLatencyCriticalMs() {
        return Optional.ofNullable(latencyCriticalMs);
    }

    public IntegerFilter latencyCriticalMs() {
        if (latencyCriticalMs == null) {
            setLatencyCriticalMs(new IntegerFilter());
        }
        return latencyCriticalMs;
    }

    public void setLatencyCriticalMs(IntegerFilter latencyCriticalMs) {
        this.latencyCriticalMs = latencyCriticalMs;
    }

    public BooleanFilter getIsActive() {
        return isActive;
    }

    public Optional<BooleanFilter> optionalIsActive() {
        return Optional.ofNullable(isActive);
    }

    public BooleanFilter isActive() {
        if (isActive == null) {
            setIsActive(new BooleanFilter());
        }
        return isActive;
    }

    public void setIsActive(BooleanFilter isActive) {
        this.isActive = isActive;
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

    public LongFilter getServiceInstanceId() {
        return serviceInstanceId;
    }

    public Optional<LongFilter> optionalServiceInstanceId() {
        return Optional.ofNullable(serviceInstanceId);
    }

    public LongFilter serviceInstanceId() {
        if (serviceInstanceId == null) {
            setServiceInstanceId(new LongFilter());
        }
        return serviceInstanceId;
    }

    public void setServiceInstanceId(LongFilter serviceInstanceId) {
        this.serviceInstanceId = serviceInstanceId;
    }

    public LongFilter getHeartbeatId() {
        return heartbeatId;
    }

    public Optional<LongFilter> optionalHeartbeatId() {
        return Optional.ofNullable(heartbeatId);
    }

    public LongFilter heartbeatId() {
        if (heartbeatId == null) {
            setHeartbeatId(new LongFilter());
        }
        return heartbeatId;
    }

    public void setHeartbeatId(LongFilter heartbeatId) {
        this.heartbeatId = heartbeatId;
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
        final MonitoredServiceCriteria that = (MonitoredServiceCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(description, that.description) &&
            Objects.equals(serviceType, that.serviceType) &&
            Objects.equals(environment, that.environment) &&
            Objects.equals(monitoringEnabled, that.monitoringEnabled) &&
            Objects.equals(clusterMonitoringEnabled, that.clusterMonitoringEnabled) &&
            Objects.equals(intervalSeconds, that.intervalSeconds) &&
            Objects.equals(timeoutMs, that.timeoutMs) &&
            Objects.equals(retryCount, that.retryCount) &&
            Objects.equals(latencyWarningMs, that.latencyWarningMs) &&
            Objects.equals(latencyCriticalMs, that.latencyCriticalMs) &&
            Objects.equals(isActive, that.isActive) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(serviceInstanceId, that.serviceInstanceId) &&
            Objects.equals(heartbeatId, that.heartbeatId) &&
            Objects.equals(datacenterId, that.datacenterId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            name,
            description,
            serviceType,
            environment,
            monitoringEnabled,
            clusterMonitoringEnabled,
            intervalSeconds,
            timeoutMs,
            retryCount,
            latencyWarningMs,
            latencyCriticalMs,
            isActive,
            createdAt,
            updatedAt,
            serviceInstanceId,
            heartbeatId,
            datacenterId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MonitoredServiceCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalDescription().map(f -> "description=" + f + ", ").orElse("") +
            optionalServiceType().map(f -> "serviceType=" + f + ", ").orElse("") +
            optionalEnvironment().map(f -> "environment=" + f + ", ").orElse("") +
            optionalMonitoringEnabled().map(f -> "monitoringEnabled=" + f + ", ").orElse("") +
            optionalClusterMonitoringEnabled().map(f -> "clusterMonitoringEnabled=" + f + ", ").orElse("") +
            optionalIntervalSeconds().map(f -> "intervalSeconds=" + f + ", ").orElse("") +
            optionalTimeoutMs().map(f -> "timeoutMs=" + f + ", ").orElse("") +
            optionalRetryCount().map(f -> "retryCount=" + f + ", ").orElse("") +
            optionalLatencyWarningMs().map(f -> "latencyWarningMs=" + f + ", ").orElse("") +
            optionalLatencyCriticalMs().map(f -> "latencyCriticalMs=" + f + ", ").orElse("") +
            optionalIsActive().map(f -> "isActive=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalServiceInstanceId().map(f -> "serviceInstanceId=" + f + ", ").orElse("") +
            optionalHeartbeatId().map(f -> "heartbeatId=" + f + ", ").orElse("") +
            optionalDatacenterId().map(f -> "datacenterId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
