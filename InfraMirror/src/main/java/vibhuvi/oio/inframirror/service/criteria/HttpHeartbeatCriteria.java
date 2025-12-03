package vibhuvi.oio.inframirror.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link vibhuvi.oio.inframirror.domain.HttpHeartbeat} entity. This class is used
 * in {@link vibhuvi.oio.inframirror.web.rest.HttpHeartbeatResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /http-heartbeats?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class HttpHeartbeatCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter executedAt;

    private BooleanFilter success;

    private IntegerFilter responseTimeMs;

    private IntegerFilter responseSizeBytes;

    private IntegerFilter responseStatusCode;

    private StringFilter responseContentType;

    private StringFilter responseServer;

    private StringFilter responseCacheStatus;

    private IntegerFilter dnsLookupMs;

    private IntegerFilter tcpConnectMs;

    private IntegerFilter tlsHandshakeMs;

    private IntegerFilter timeToFirstByteMs;

    private IntegerFilter warningThresholdMs;

    private IntegerFilter criticalThresholdMs;

    private StringFilter errorType;

    private LongFilter monitorId;

    private LongFilter agentId;

    private Boolean distinct;

    public HttpHeartbeatCriteria() {}

    public HttpHeartbeatCriteria(HttpHeartbeatCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.executedAt = other.optionalExecutedAt().map(InstantFilter::copy).orElse(null);
        this.success = other.optionalSuccess().map(BooleanFilter::copy).orElse(null);
        this.responseTimeMs = other.optionalResponseTimeMs().map(IntegerFilter::copy).orElse(null);
        this.responseSizeBytes = other.optionalResponseSizeBytes().map(IntegerFilter::copy).orElse(null);
        this.responseStatusCode = other.optionalResponseStatusCode().map(IntegerFilter::copy).orElse(null);
        this.responseContentType = other.optionalResponseContentType().map(StringFilter::copy).orElse(null);
        this.responseServer = other.optionalResponseServer().map(StringFilter::copy).orElse(null);
        this.responseCacheStatus = other.optionalResponseCacheStatus().map(StringFilter::copy).orElse(null);
        this.dnsLookupMs = other.optionalDnsLookupMs().map(IntegerFilter::copy).orElse(null);
        this.tcpConnectMs = other.optionalTcpConnectMs().map(IntegerFilter::copy).orElse(null);
        this.tlsHandshakeMs = other.optionalTlsHandshakeMs().map(IntegerFilter::copy).orElse(null);
        this.timeToFirstByteMs = other.optionalTimeToFirstByteMs().map(IntegerFilter::copy).orElse(null);
        this.warningThresholdMs = other.optionalWarningThresholdMs().map(IntegerFilter::copy).orElse(null);
        this.criticalThresholdMs = other.optionalCriticalThresholdMs().map(IntegerFilter::copy).orElse(null);
        this.errorType = other.optionalErrorType().map(StringFilter::copy).orElse(null);
        this.monitorId = other.optionalMonitorId().map(LongFilter::copy).orElse(null);
        this.agentId = other.optionalAgentId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public HttpHeartbeatCriteria copy() {
        return new HttpHeartbeatCriteria(this);
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

    public IntegerFilter getResponseSizeBytes() {
        return responseSizeBytes;
    }

    public Optional<IntegerFilter> optionalResponseSizeBytes() {
        return Optional.ofNullable(responseSizeBytes);
    }

    public IntegerFilter responseSizeBytes() {
        if (responseSizeBytes == null) {
            setResponseSizeBytes(new IntegerFilter());
        }
        return responseSizeBytes;
    }

    public void setResponseSizeBytes(IntegerFilter responseSizeBytes) {
        this.responseSizeBytes = responseSizeBytes;
    }

    public IntegerFilter getResponseStatusCode() {
        return responseStatusCode;
    }

    public Optional<IntegerFilter> optionalResponseStatusCode() {
        return Optional.ofNullable(responseStatusCode);
    }

    public IntegerFilter responseStatusCode() {
        if (responseStatusCode == null) {
            setResponseStatusCode(new IntegerFilter());
        }
        return responseStatusCode;
    }

    public void setResponseStatusCode(IntegerFilter responseStatusCode) {
        this.responseStatusCode = responseStatusCode;
    }

    public StringFilter getResponseContentType() {
        return responseContentType;
    }

    public Optional<StringFilter> optionalResponseContentType() {
        return Optional.ofNullable(responseContentType);
    }

    public StringFilter responseContentType() {
        if (responseContentType == null) {
            setResponseContentType(new StringFilter());
        }
        return responseContentType;
    }

    public void setResponseContentType(StringFilter responseContentType) {
        this.responseContentType = responseContentType;
    }

    public StringFilter getResponseServer() {
        return responseServer;
    }

    public Optional<StringFilter> optionalResponseServer() {
        return Optional.ofNullable(responseServer);
    }

    public StringFilter responseServer() {
        if (responseServer == null) {
            setResponseServer(new StringFilter());
        }
        return responseServer;
    }

    public void setResponseServer(StringFilter responseServer) {
        this.responseServer = responseServer;
    }

    public StringFilter getResponseCacheStatus() {
        return responseCacheStatus;
    }

    public Optional<StringFilter> optionalResponseCacheStatus() {
        return Optional.ofNullable(responseCacheStatus);
    }

    public StringFilter responseCacheStatus() {
        if (responseCacheStatus == null) {
            setResponseCacheStatus(new StringFilter());
        }
        return responseCacheStatus;
    }

    public void setResponseCacheStatus(StringFilter responseCacheStatus) {
        this.responseCacheStatus = responseCacheStatus;
    }

    public IntegerFilter getDnsLookupMs() {
        return dnsLookupMs;
    }

    public Optional<IntegerFilter> optionalDnsLookupMs() {
        return Optional.ofNullable(dnsLookupMs);
    }

    public IntegerFilter dnsLookupMs() {
        if (dnsLookupMs == null) {
            setDnsLookupMs(new IntegerFilter());
        }
        return dnsLookupMs;
    }

    public void setDnsLookupMs(IntegerFilter dnsLookupMs) {
        this.dnsLookupMs = dnsLookupMs;
    }

    public IntegerFilter getTcpConnectMs() {
        return tcpConnectMs;
    }

    public Optional<IntegerFilter> optionalTcpConnectMs() {
        return Optional.ofNullable(tcpConnectMs);
    }

    public IntegerFilter tcpConnectMs() {
        if (tcpConnectMs == null) {
            setTcpConnectMs(new IntegerFilter());
        }
        return tcpConnectMs;
    }

    public void setTcpConnectMs(IntegerFilter tcpConnectMs) {
        this.tcpConnectMs = tcpConnectMs;
    }

    public IntegerFilter getTlsHandshakeMs() {
        return tlsHandshakeMs;
    }

    public Optional<IntegerFilter> optionalTlsHandshakeMs() {
        return Optional.ofNullable(tlsHandshakeMs);
    }

    public IntegerFilter tlsHandshakeMs() {
        if (tlsHandshakeMs == null) {
            setTlsHandshakeMs(new IntegerFilter());
        }
        return tlsHandshakeMs;
    }

    public void setTlsHandshakeMs(IntegerFilter tlsHandshakeMs) {
        this.tlsHandshakeMs = tlsHandshakeMs;
    }

    public IntegerFilter getTimeToFirstByteMs() {
        return timeToFirstByteMs;
    }

    public Optional<IntegerFilter> optionalTimeToFirstByteMs() {
        return Optional.ofNullable(timeToFirstByteMs);
    }

    public IntegerFilter timeToFirstByteMs() {
        if (timeToFirstByteMs == null) {
            setTimeToFirstByteMs(new IntegerFilter());
        }
        return timeToFirstByteMs;
    }

    public void setTimeToFirstByteMs(IntegerFilter timeToFirstByteMs) {
        this.timeToFirstByteMs = timeToFirstByteMs;
    }

    public IntegerFilter getWarningThresholdMs() {
        return warningThresholdMs;
    }

    public Optional<IntegerFilter> optionalWarningThresholdMs() {
        return Optional.ofNullable(warningThresholdMs);
    }

    public IntegerFilter warningThresholdMs() {
        if (warningThresholdMs == null) {
            setWarningThresholdMs(new IntegerFilter());
        }
        return warningThresholdMs;
    }

    public void setWarningThresholdMs(IntegerFilter warningThresholdMs) {
        this.warningThresholdMs = warningThresholdMs;
    }

    public IntegerFilter getCriticalThresholdMs() {
        return criticalThresholdMs;
    }

    public Optional<IntegerFilter> optionalCriticalThresholdMs() {
        return Optional.ofNullable(criticalThresholdMs);
    }

    public IntegerFilter criticalThresholdMs() {
        if (criticalThresholdMs == null) {
            setCriticalThresholdMs(new IntegerFilter());
        }
        return criticalThresholdMs;
    }

    public void setCriticalThresholdMs(IntegerFilter criticalThresholdMs) {
        this.criticalThresholdMs = criticalThresholdMs;
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

    public LongFilter getMonitorId() {
        return monitorId;
    }

    public Optional<LongFilter> optionalMonitorId() {
        return Optional.ofNullable(monitorId);
    }

    public LongFilter monitorId() {
        if (monitorId == null) {
            setMonitorId(new LongFilter());
        }
        return monitorId;
    }

    public void setMonitorId(LongFilter monitorId) {
        this.monitorId = monitorId;
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
        final HttpHeartbeatCriteria that = (HttpHeartbeatCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(executedAt, that.executedAt) &&
            Objects.equals(success, that.success) &&
            Objects.equals(responseTimeMs, that.responseTimeMs) &&
            Objects.equals(responseSizeBytes, that.responseSizeBytes) &&
            Objects.equals(responseStatusCode, that.responseStatusCode) &&
            Objects.equals(responseContentType, that.responseContentType) &&
            Objects.equals(responseServer, that.responseServer) &&
            Objects.equals(responseCacheStatus, that.responseCacheStatus) &&
            Objects.equals(dnsLookupMs, that.dnsLookupMs) &&
            Objects.equals(tcpConnectMs, that.tcpConnectMs) &&
            Objects.equals(tlsHandshakeMs, that.tlsHandshakeMs) &&
            Objects.equals(timeToFirstByteMs, that.timeToFirstByteMs) &&
            Objects.equals(warningThresholdMs, that.warningThresholdMs) &&
            Objects.equals(criticalThresholdMs, that.criticalThresholdMs) &&
            Objects.equals(errorType, that.errorType) &&
            Objects.equals(monitorId, that.monitorId) &&
            Objects.equals(agentId, that.agentId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            executedAt,
            success,
            responseTimeMs,
            responseSizeBytes,
            responseStatusCode,
            responseContentType,
            responseServer,
            responseCacheStatus,
            dnsLookupMs,
            tcpConnectMs,
            tlsHandshakeMs,
            timeToFirstByteMs,
            warningThresholdMs,
            criticalThresholdMs,
            errorType,
            monitorId,
            agentId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HttpHeartbeatCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalExecutedAt().map(f -> "executedAt=" + f + ", ").orElse("") +
            optionalSuccess().map(f -> "success=" + f + ", ").orElse("") +
            optionalResponseTimeMs().map(f -> "responseTimeMs=" + f + ", ").orElse("") +
            optionalResponseSizeBytes().map(f -> "responseSizeBytes=" + f + ", ").orElse("") +
            optionalResponseStatusCode().map(f -> "responseStatusCode=" + f + ", ").orElse("") +
            optionalResponseContentType().map(f -> "responseContentType=" + f + ", ").orElse("") +
            optionalResponseServer().map(f -> "responseServer=" + f + ", ").orElse("") +
            optionalResponseCacheStatus().map(f -> "responseCacheStatus=" + f + ", ").orElse("") +
            optionalDnsLookupMs().map(f -> "dnsLookupMs=" + f + ", ").orElse("") +
            optionalTcpConnectMs().map(f -> "tcpConnectMs=" + f + ", ").orElse("") +
            optionalTlsHandshakeMs().map(f -> "tlsHandshakeMs=" + f + ", ").orElse("") +
            optionalTimeToFirstByteMs().map(f -> "timeToFirstByteMs=" + f + ", ").orElse("") +
            optionalWarningThresholdMs().map(f -> "warningThresholdMs=" + f + ", ").orElse("") +
            optionalCriticalThresholdMs().map(f -> "criticalThresholdMs=" + f + ", ").orElse("") +
            optionalErrorType().map(f -> "errorType=" + f + ", ").orElse("") +
            optionalMonitorId().map(f -> "monitorId=" + f + ", ").orElse("") +
            optionalAgentId().map(f -> "agentId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
