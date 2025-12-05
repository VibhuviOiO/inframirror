package vibhuvi.oio.inframirror.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link vibhuvi.oio.inframirror.domain.HttpMonitor} entity. This class is used
 * in {@link vibhuvi.oio.inframirror.web.rest.HttpMonitorResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /http-monitors?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class HttpMonitorCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter method;

    private StringFilter type;

    private IntegerFilter intervalSeconds;

    private IntegerFilter timeoutSeconds;

    private IntegerFilter retryCount;

    private IntegerFilter retryDelaySeconds;

    private IntegerFilter responseTimeWarningMs;

    private IntegerFilter responseTimeCriticalMs;

    private FloatFilter uptimeWarningPercent;

    private FloatFilter uptimeCriticalPercent;

    private BooleanFilter includeResponseBody;

    private IntegerFilter resendNotificationCount;

    private IntegerFilter certificateExpiryDays;

    private BooleanFilter ignoreTlsError;

    private BooleanFilter checkSslCertificate;

    private BooleanFilter checkDnsResolution;

    private BooleanFilter upsideDownMode;

    private IntegerFilter maxRedirects;

    private StringFilter tags;

    private BooleanFilter enabled;

    private StringFilter expectedStatusCodes;

    private IntegerFilter performanceBudgetMs;

    private IntegerFilter sizeBudgetKb;

    private LongFilter childrenId;

    private LongFilter heartbeatId;

    private LongFilter parentId;

    private Boolean distinct;

    public HttpMonitorCriteria() {}

    public HttpMonitorCriteria(HttpMonitorCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.method = other.optionalMethod().map(StringFilter::copy).orElse(null);
        this.type = other.optionalType().map(StringFilter::copy).orElse(null);
        this.intervalSeconds = other.optionalIntervalSeconds().map(IntegerFilter::copy).orElse(null);
        this.timeoutSeconds = other.optionalTimeoutSeconds().map(IntegerFilter::copy).orElse(null);
        this.retryCount = other.optionalRetryCount().map(IntegerFilter::copy).orElse(null);
        this.retryDelaySeconds = other.optionalRetryDelaySeconds().map(IntegerFilter::copy).orElse(null);
        this.responseTimeWarningMs = other.optionalResponseTimeWarningMs().map(IntegerFilter::copy).orElse(null);
        this.responseTimeCriticalMs = other.optionalResponseTimeCriticalMs().map(IntegerFilter::copy).orElse(null);
        this.uptimeWarningPercent = other.optionalUptimeWarningPercent().map(FloatFilter::copy).orElse(null);
        this.uptimeCriticalPercent = other.optionalUptimeCriticalPercent().map(FloatFilter::copy).orElse(null);
        this.includeResponseBody = other.optionalIncludeResponseBody().map(BooleanFilter::copy).orElse(null);
        this.resendNotificationCount = other.optionalResendNotificationCount().map(IntegerFilter::copy).orElse(null);
        this.certificateExpiryDays = other.optionalCertificateExpiryDays().map(IntegerFilter::copy).orElse(null);
        this.ignoreTlsError = other.optionalIgnoreTlsError().map(BooleanFilter::copy).orElse(null);
        this.checkSslCertificate = other.optionalCheckSslCertificate().map(BooleanFilter::copy).orElse(null);
        this.checkDnsResolution = other.optionalCheckDnsResolution().map(BooleanFilter::copy).orElse(null);
        this.upsideDownMode = other.optionalUpsideDownMode().map(BooleanFilter::copy).orElse(null);
        this.maxRedirects = other.optionalMaxRedirects().map(IntegerFilter::copy).orElse(null);
        this.tags = other.optionalTags().map(StringFilter::copy).orElse(null);
        this.enabled = other.optionalEnabled().map(BooleanFilter::copy).orElse(null);
        this.expectedStatusCodes = other.optionalExpectedStatusCodes().map(StringFilter::copy).orElse(null);
        this.performanceBudgetMs = other.optionalPerformanceBudgetMs().map(IntegerFilter::copy).orElse(null);
        this.sizeBudgetKb = other.optionalSizeBudgetKb().map(IntegerFilter::copy).orElse(null);
        this.childrenId = other.optionalChildrenId().map(LongFilter::copy).orElse(null);
        this.heartbeatId = other.optionalHeartbeatId().map(LongFilter::copy).orElse(null);
        this.parentId = other.optionalParentId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public HttpMonitorCriteria copy() {
        return new HttpMonitorCriteria(this);
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

    public StringFilter getMethod() {
        return method;
    }

    public Optional<StringFilter> optionalMethod() {
        return Optional.ofNullable(method);
    }

    public StringFilter method() {
        if (method == null) {
            setMethod(new StringFilter());
        }
        return method;
    }

    public void setMethod(StringFilter method) {
        this.method = method;
    }

    public StringFilter getType() {
        return type;
    }

    public Optional<StringFilter> optionalType() {
        return Optional.ofNullable(type);
    }

    public StringFilter type() {
        if (type == null) {
            setType(new StringFilter());
        }
        return type;
    }

    public void setType(StringFilter type) {
        this.type = type;
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

    public IntegerFilter getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public Optional<IntegerFilter> optionalTimeoutSeconds() {
        return Optional.ofNullable(timeoutSeconds);
    }

    public IntegerFilter timeoutSeconds() {
        if (timeoutSeconds == null) {
            setTimeoutSeconds(new IntegerFilter());
        }
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(IntegerFilter timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
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

    public IntegerFilter getRetryDelaySeconds() {
        return retryDelaySeconds;
    }

    public Optional<IntegerFilter> optionalRetryDelaySeconds() {
        return Optional.ofNullable(retryDelaySeconds);
    }

    public IntegerFilter retryDelaySeconds() {
        if (retryDelaySeconds == null) {
            setRetryDelaySeconds(new IntegerFilter());
        }
        return retryDelaySeconds;
    }

    public void setRetryDelaySeconds(IntegerFilter retryDelaySeconds) {
        this.retryDelaySeconds = retryDelaySeconds;
    }

    public IntegerFilter getResponseTimeWarningMs() {
        return responseTimeWarningMs;
    }

    public Optional<IntegerFilter> optionalResponseTimeWarningMs() {
        return Optional.ofNullable(responseTimeWarningMs);
    }

    public IntegerFilter responseTimeWarningMs() {
        if (responseTimeWarningMs == null) {
            setResponseTimeWarningMs(new IntegerFilter());
        }
        return responseTimeWarningMs;
    }

    public void setResponseTimeWarningMs(IntegerFilter responseTimeWarningMs) {
        this.responseTimeWarningMs = responseTimeWarningMs;
    }

    public IntegerFilter getResponseTimeCriticalMs() {
        return responseTimeCriticalMs;
    }

    public Optional<IntegerFilter> optionalResponseTimeCriticalMs() {
        return Optional.ofNullable(responseTimeCriticalMs);
    }

    public IntegerFilter responseTimeCriticalMs() {
        if (responseTimeCriticalMs == null) {
            setResponseTimeCriticalMs(new IntegerFilter());
        }
        return responseTimeCriticalMs;
    }

    public void setResponseTimeCriticalMs(IntegerFilter responseTimeCriticalMs) {
        this.responseTimeCriticalMs = responseTimeCriticalMs;
    }

    public FloatFilter getUptimeWarningPercent() {
        return uptimeWarningPercent;
    }

    public Optional<FloatFilter> optionalUptimeWarningPercent() {
        return Optional.ofNullable(uptimeWarningPercent);
    }

    public FloatFilter uptimeWarningPercent() {
        if (uptimeWarningPercent == null) {
            setUptimeWarningPercent(new FloatFilter());
        }
        return uptimeWarningPercent;
    }

    public void setUptimeWarningPercent(FloatFilter uptimeWarningPercent) {
        this.uptimeWarningPercent = uptimeWarningPercent;
    }

    public FloatFilter getUptimeCriticalPercent() {
        return uptimeCriticalPercent;
    }

    public Optional<FloatFilter> optionalUptimeCriticalPercent() {
        return Optional.ofNullable(uptimeCriticalPercent);
    }

    public FloatFilter uptimeCriticalPercent() {
        if (uptimeCriticalPercent == null) {
            setUptimeCriticalPercent(new FloatFilter());
        }
        return uptimeCriticalPercent;
    }

    public void setUptimeCriticalPercent(FloatFilter uptimeCriticalPercent) {
        this.uptimeCriticalPercent = uptimeCriticalPercent;
    }

    public BooleanFilter getIncludeResponseBody() {
        return includeResponseBody;
    }

    public Optional<BooleanFilter> optionalIncludeResponseBody() {
        return Optional.ofNullable(includeResponseBody);
    }

    public BooleanFilter includeResponseBody() {
        if (includeResponseBody == null) {
            setIncludeResponseBody(new BooleanFilter());
        }
        return includeResponseBody;
    }

    public void setIncludeResponseBody(BooleanFilter includeResponseBody) {
        this.includeResponseBody = includeResponseBody;
    }

    public IntegerFilter getResendNotificationCount() {
        return resendNotificationCount;
    }

    public Optional<IntegerFilter> optionalResendNotificationCount() {
        return Optional.ofNullable(resendNotificationCount);
    }

    public IntegerFilter resendNotificationCount() {
        if (resendNotificationCount == null) {
            setResendNotificationCount(new IntegerFilter());
        }
        return resendNotificationCount;
    }

    public void setResendNotificationCount(IntegerFilter resendNotificationCount) {
        this.resendNotificationCount = resendNotificationCount;
    }

    public IntegerFilter getCertificateExpiryDays() {
        return certificateExpiryDays;
    }

    public Optional<IntegerFilter> optionalCertificateExpiryDays() {
        return Optional.ofNullable(certificateExpiryDays);
    }

    public IntegerFilter certificateExpiryDays() {
        if (certificateExpiryDays == null) {
            setCertificateExpiryDays(new IntegerFilter());
        }
        return certificateExpiryDays;
    }

    public void setCertificateExpiryDays(IntegerFilter certificateExpiryDays) {
        this.certificateExpiryDays = certificateExpiryDays;
    }

    public BooleanFilter getIgnoreTlsError() {
        return ignoreTlsError;
    }

    public Optional<BooleanFilter> optionalIgnoreTlsError() {
        return Optional.ofNullable(ignoreTlsError);
    }

    public BooleanFilter ignoreTlsError() {
        if (ignoreTlsError == null) {
            setIgnoreTlsError(new BooleanFilter());
        }
        return ignoreTlsError;
    }

    public void setIgnoreTlsError(BooleanFilter ignoreTlsError) {
        this.ignoreTlsError = ignoreTlsError;
    }

    public BooleanFilter getCheckSslCertificate() {
        return checkSslCertificate;
    }

    public Optional<BooleanFilter> optionalCheckSslCertificate() {
        return Optional.ofNullable(checkSslCertificate);
    }

    public BooleanFilter checkSslCertificate() {
        if (checkSslCertificate == null) {
            setCheckSslCertificate(new BooleanFilter());
        }
        return checkSslCertificate;
    }

    public void setCheckSslCertificate(BooleanFilter checkSslCertificate) {
        this.checkSslCertificate = checkSslCertificate;
    }

    public BooleanFilter getCheckDnsResolution() {
        return checkDnsResolution;
    }

    public Optional<BooleanFilter> optionalCheckDnsResolution() {
        return Optional.ofNullable(checkDnsResolution);
    }

    public BooleanFilter checkDnsResolution() {
        if (checkDnsResolution == null) {
            setCheckDnsResolution(new BooleanFilter());
        }
        return checkDnsResolution;
    }

    public void setCheckDnsResolution(BooleanFilter checkDnsResolution) {
        this.checkDnsResolution = checkDnsResolution;
    }

    public BooleanFilter getUpsideDownMode() {
        return upsideDownMode;
    }

    public Optional<BooleanFilter> optionalUpsideDownMode() {
        return Optional.ofNullable(upsideDownMode);
    }

    public BooleanFilter upsideDownMode() {
        if (upsideDownMode == null) {
            setUpsideDownMode(new BooleanFilter());
        }
        return upsideDownMode;
    }

    public void setUpsideDownMode(BooleanFilter upsideDownMode) {
        this.upsideDownMode = upsideDownMode;
    }

    public IntegerFilter getMaxRedirects() {
        return maxRedirects;
    }

    public Optional<IntegerFilter> optionalMaxRedirects() {
        return Optional.ofNullable(maxRedirects);
    }

    public IntegerFilter maxRedirects() {
        if (maxRedirects == null) {
            setMaxRedirects(new IntegerFilter());
        }
        return maxRedirects;
    }

    public void setMaxRedirects(IntegerFilter maxRedirects) {
        this.maxRedirects = maxRedirects;
    }

    public StringFilter getTags() {
        return tags;
    }

    public Optional<StringFilter> optionalTags() {
        return Optional.ofNullable(tags);
    }

    public StringFilter tags() {
        if (tags == null) {
            setTags(new StringFilter());
        }
        return tags;
    }

    public void setTags(StringFilter tags) {
        this.tags = tags;
    }

    public BooleanFilter getEnabled() {
        return enabled;
    }

    public Optional<BooleanFilter> optionalEnabled() {
        return Optional.ofNullable(enabled);
    }

    public BooleanFilter enabled() {
        if (enabled == null) {
            setEnabled(new BooleanFilter());
        }
        return enabled;
    }

    public void setEnabled(BooleanFilter enabled) {
        this.enabled = enabled;
    }

    public StringFilter getExpectedStatusCodes() {
        return expectedStatusCodes;
    }

    public Optional<StringFilter> optionalExpectedStatusCodes() {
        return Optional.ofNullable(expectedStatusCodes);
    }

    public StringFilter expectedStatusCodes() {
        if (expectedStatusCodes == null) {
            setExpectedStatusCodes(new StringFilter());
        }
        return expectedStatusCodes;
    }

    public void setExpectedStatusCodes(StringFilter expectedStatusCodes) {
        this.expectedStatusCodes = expectedStatusCodes;
    }

    public IntegerFilter getPerformanceBudgetMs() {
        return performanceBudgetMs;
    }

    public Optional<IntegerFilter> optionalPerformanceBudgetMs() {
        return Optional.ofNullable(performanceBudgetMs);
    }

    public IntegerFilter performanceBudgetMs() {
        if (performanceBudgetMs == null) {
            setPerformanceBudgetMs(new IntegerFilter());
        }
        return performanceBudgetMs;
    }

    public void setPerformanceBudgetMs(IntegerFilter performanceBudgetMs) {
        this.performanceBudgetMs = performanceBudgetMs;
    }

    public IntegerFilter getSizeBudgetKb() {
        return sizeBudgetKb;
    }

    public Optional<IntegerFilter> optionalSizeBudgetKb() {
        return Optional.ofNullable(sizeBudgetKb);
    }

    public IntegerFilter sizeBudgetKb() {
        if (sizeBudgetKb == null) {
            setSizeBudgetKb(new IntegerFilter());
        }
        return sizeBudgetKb;
    }

    public void setSizeBudgetKb(IntegerFilter sizeBudgetKb) {
        this.sizeBudgetKb = sizeBudgetKb;
    }

    public LongFilter getChildrenId() {
        return childrenId;
    }

    public Optional<LongFilter> optionalChildrenId() {
        return Optional.ofNullable(childrenId);
    }

    public LongFilter childrenId() {
        if (childrenId == null) {
            setChildrenId(new LongFilter());
        }
        return childrenId;
    }

    public void setChildrenId(LongFilter childrenId) {
        this.childrenId = childrenId;
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

    public LongFilter getParentId() {
        return parentId;
    }

    public Optional<LongFilter> optionalParentId() {
        return Optional.ofNullable(parentId);
    }

    public LongFilter parentId() {
        if (parentId == null) {
            setParentId(new LongFilter());
        }
        return parentId;
    }

    public void setParentId(LongFilter parentId) {
        this.parentId = parentId;
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
        final HttpMonitorCriteria that = (HttpMonitorCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(method, that.method) &&
            Objects.equals(type, that.type) &&
            Objects.equals(intervalSeconds, that.intervalSeconds) &&
            Objects.equals(timeoutSeconds, that.timeoutSeconds) &&
            Objects.equals(retryCount, that.retryCount) &&
            Objects.equals(retryDelaySeconds, that.retryDelaySeconds) &&
            Objects.equals(responseTimeWarningMs, that.responseTimeWarningMs) &&
            Objects.equals(responseTimeCriticalMs, that.responseTimeCriticalMs) &&
            Objects.equals(uptimeWarningPercent, that.uptimeWarningPercent) &&
            Objects.equals(uptimeCriticalPercent, that.uptimeCriticalPercent) &&
            Objects.equals(includeResponseBody, that.includeResponseBody) &&
            Objects.equals(resendNotificationCount, that.resendNotificationCount) &&
            Objects.equals(certificateExpiryDays, that.certificateExpiryDays) &&
            Objects.equals(ignoreTlsError, that.ignoreTlsError) &&
            Objects.equals(checkSslCertificate, that.checkSslCertificate) &&
            Objects.equals(checkDnsResolution, that.checkDnsResolution) &&
            Objects.equals(upsideDownMode, that.upsideDownMode) &&
            Objects.equals(maxRedirects, that.maxRedirects) &&
            Objects.equals(tags, that.tags) &&
            Objects.equals(enabled, that.enabled) &&
            Objects.equals(expectedStatusCodes, that.expectedStatusCodes) &&
            Objects.equals(performanceBudgetMs, that.performanceBudgetMs) &&
            Objects.equals(sizeBudgetKb, that.sizeBudgetKb) &&
            Objects.equals(childrenId, that.childrenId) &&
            Objects.equals(heartbeatId, that.heartbeatId) &&
            Objects.equals(parentId, that.parentId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            name,
            method,
            type,
            intervalSeconds,
            timeoutSeconds,
            retryCount,
            retryDelaySeconds,
            responseTimeWarningMs,
            responseTimeCriticalMs,
            uptimeWarningPercent,
            uptimeCriticalPercent,
            includeResponseBody,
            resendNotificationCount,
            certificateExpiryDays,
            ignoreTlsError,
            checkSslCertificate,
            checkDnsResolution,
            upsideDownMode,
            maxRedirects,
            tags,
            enabled,
            expectedStatusCodes,
            performanceBudgetMs,
            sizeBudgetKb,
            childrenId,
            heartbeatId,
            parentId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HttpMonitorCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalMethod().map(f -> "method=" + f + ", ").orElse("") +
            optionalType().map(f -> "type=" + f + ", ").orElse("") +
            optionalIntervalSeconds().map(f -> "intervalSeconds=" + f + ", ").orElse("") +
            optionalTimeoutSeconds().map(f -> "timeoutSeconds=" + f + ", ").orElse("") +
            optionalRetryCount().map(f -> "retryCount=" + f + ", ").orElse("") +
            optionalRetryDelaySeconds().map(f -> "retryDelaySeconds=" + f + ", ").orElse("") +
            optionalResponseTimeWarningMs().map(f -> "responseTimeWarningMs=" + f + ", ").orElse("") +
            optionalResponseTimeCriticalMs().map(f -> "responseTimeCriticalMs=" + f + ", ").orElse("") +
            optionalUptimeWarningPercent().map(f -> "uptimeWarningPercent=" + f + ", ").orElse("") +
            optionalUptimeCriticalPercent().map(f -> "uptimeCriticalPercent=" + f + ", ").orElse("") +
            optionalIncludeResponseBody().map(f -> "includeResponseBody=" + f + ", ").orElse("") +
            optionalResendNotificationCount().map(f -> "resendNotificationCount=" + f + ", ").orElse("") +
            optionalCertificateExpiryDays().map(f -> "certificateExpiryDays=" + f + ", ").orElse("") +
            optionalIgnoreTlsError().map(f -> "ignoreTlsError=" + f + ", ").orElse("") +
            optionalCheckSslCertificate().map(f -> "checkSslCertificate=" + f + ", ").orElse("") +
            optionalCheckDnsResolution().map(f -> "checkDnsResolution=" + f + ", ").orElse("") +
            optionalUpsideDownMode().map(f -> "upsideDownMode=" + f + ", ").orElse("") +
            optionalMaxRedirects().map(f -> "maxRedirects=" + f + ", ").orElse("") +
            optionalTags().map(f -> "tags=" + f + ", ").orElse("") +
            optionalEnabled().map(f -> "enabled=" + f + ", ").orElse("") +
            optionalExpectedStatusCodes().map(f -> "expectedStatusCodes=" + f + ", ").orElse("") +
            optionalPerformanceBudgetMs().map(f -> "performanceBudgetMs=" + f + ", ").orElse("") +
            optionalSizeBudgetKb().map(f -> "sizeBudgetKb=" + f + ", ").orElse("") +
            optionalChildrenId().map(f -> "childrenId=" + f + ", ").orElse("") +
            optionalHeartbeatId().map(f -> "heartbeatId=" + f + ", ").orElse("") +
            optionalParentId().map(f -> "parentId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
