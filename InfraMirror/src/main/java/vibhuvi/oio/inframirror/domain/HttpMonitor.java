package vibhuvi.oio.inframirror.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A HttpMonitor.
 */
@Entity
@Table(name = "http_monitor")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "httpmonitor")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class HttpMonitor implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "name", length = 100, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String name;

    @NotNull
    @Size(max = 10)
    @Column(name = "method", length = 10, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String method;

    @NotNull
    @Size(max = 10)
    @Column(name = "type", length = 10, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String type;

    @Lob
    @Column(name = "url")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String url;

    @Lob
    @Column(name = "headers")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String headers;

    @Lob
    @Column(name = "body")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String body;

    @NotNull
    @Column(name = "interval_seconds", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer intervalSeconds;

    @NotNull
    @Column(name = "timeout_seconds", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer timeoutSeconds;

    @NotNull
    @Column(name = "retry_count", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer retryCount;

    @NotNull
    @Column(name = "retry_delay_seconds", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer retryDelaySeconds;

    @Column(name = "response_time_warning_ms")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer responseTimeWarningMs;

    @Column(name = "response_time_critical_ms")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer responseTimeCriticalMs;

    @Column(name = "uptime_warning_percent")
    private Float uptimeWarningPercent;

    @Column(name = "uptime_critical_percent")
    private Float uptimeCriticalPercent;

    @Column(name = "include_response_body")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean includeResponseBody;

    @Column(name = "resend_notification_count")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer resendNotificationCount;

    @Column(name = "certificate_expiry_days")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer certificateExpiryDays;

    @Column(name = "ignore_tls_error")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean ignoreTlsError;

    @Column(name = "check_ssl_certificate")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean checkSslCertificate;

    @Column(name = "check_dns_resolution")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean checkDnsResolution;

    @Column(name = "upside_down_mode")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean upsideDownMode;

    @Column(name = "max_redirects")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer maxRedirects;

    @Lob
    @Column(name = "description")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String description;

    @Size(max = 500)
    @Column(name = "tags", length = 500)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String tags;

    @Column(name = "enabled")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean enabled;

    @Size(max = 100)
    @Column(name = "expected_status_codes", length = 100)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String expectedStatusCodes;

    @Column(name = "performance_budget_ms")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer performanceBudgetMs;

    @Column(name = "size_budget_kb")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer sizeBudgetKb;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "children", "heartbeats", "parent" }, allowSetters = true)
    private Set<HttpMonitor> children = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "monitor")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "agent", "monitor" }, allowSetters = true)
    private Set<HttpHeartbeat> heartbeats = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "children", "heartbeats", "parent" }, allowSetters = true)
    private HttpMonitor parent;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public HttpMonitor id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public HttpMonitor name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMethod() {
        return this.method;
    }

    public HttpMonitor method(String method) {
        this.setMethod(method);
        return this;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getType() {
        return this.type;
    }

    public HttpMonitor type(String type) {
        this.setType(type);
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return this.url;
    }

    public HttpMonitor url(String url) {
        this.setUrl(url);
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHeaders() {
        return this.headers;
    }

    public HttpMonitor headers(String headers) {
        this.setHeaders(headers);
        return this;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public String getBody() {
        return this.body;
    }

    public HttpMonitor body(String body) {
        this.setBody(body);
        return this;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Integer getIntervalSeconds() {
        return this.intervalSeconds;
    }

    public HttpMonitor intervalSeconds(Integer intervalSeconds) {
        this.setIntervalSeconds(intervalSeconds);
        return this;
    }

    public void setIntervalSeconds(Integer intervalSeconds) {
        this.intervalSeconds = intervalSeconds;
    }

    public Integer getTimeoutSeconds() {
        return this.timeoutSeconds;
    }

    public HttpMonitor timeoutSeconds(Integer timeoutSeconds) {
        this.setTimeoutSeconds(timeoutSeconds);
        return this;
    }

    public void setTimeoutSeconds(Integer timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    public Integer getRetryCount() {
        return this.retryCount;
    }

    public HttpMonitor retryCount(Integer retryCount) {
        this.setRetryCount(retryCount);
        return this;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public Integer getRetryDelaySeconds() {
        return this.retryDelaySeconds;
    }

    public HttpMonitor retryDelaySeconds(Integer retryDelaySeconds) {
        this.setRetryDelaySeconds(retryDelaySeconds);
        return this;
    }

    public void setRetryDelaySeconds(Integer retryDelaySeconds) {
        this.retryDelaySeconds = retryDelaySeconds;
    }

    public Integer getResponseTimeWarningMs() {
        return this.responseTimeWarningMs;
    }

    public HttpMonitor responseTimeWarningMs(Integer responseTimeWarningMs) {
        this.setResponseTimeWarningMs(responseTimeWarningMs);
        return this;
    }

    public void setResponseTimeWarningMs(Integer responseTimeWarningMs) {
        this.responseTimeWarningMs = responseTimeWarningMs;
    }

    public Integer getResponseTimeCriticalMs() {
        return this.responseTimeCriticalMs;
    }

    public HttpMonitor responseTimeCriticalMs(Integer responseTimeCriticalMs) {
        this.setResponseTimeCriticalMs(responseTimeCriticalMs);
        return this;
    }

    public void setResponseTimeCriticalMs(Integer responseTimeCriticalMs) {
        this.responseTimeCriticalMs = responseTimeCriticalMs;
    }

    public Float getUptimeWarningPercent() {
        return this.uptimeWarningPercent;
    }

    public HttpMonitor uptimeWarningPercent(Float uptimeWarningPercent) {
        this.setUptimeWarningPercent(uptimeWarningPercent);
        return this;
    }

    public void setUptimeWarningPercent(Float uptimeWarningPercent) {
        this.uptimeWarningPercent = uptimeWarningPercent;
    }

    public Float getUptimeCriticalPercent() {
        return this.uptimeCriticalPercent;
    }

    public HttpMonitor uptimeCriticalPercent(Float uptimeCriticalPercent) {
        this.setUptimeCriticalPercent(uptimeCriticalPercent);
        return this;
    }

    public void setUptimeCriticalPercent(Float uptimeCriticalPercent) {
        this.uptimeCriticalPercent = uptimeCriticalPercent;
    }

    public Boolean getIncludeResponseBody() {
        return this.includeResponseBody;
    }

    public HttpMonitor includeResponseBody(Boolean includeResponseBody) {
        this.setIncludeResponseBody(includeResponseBody);
        return this;
    }

    public void setIncludeResponseBody(Boolean includeResponseBody) {
        this.includeResponseBody = includeResponseBody;
    }

    public Integer getResendNotificationCount() {
        return this.resendNotificationCount;
    }

    public HttpMonitor resendNotificationCount(Integer resendNotificationCount) {
        this.setResendNotificationCount(resendNotificationCount);
        return this;
    }

    public void setResendNotificationCount(Integer resendNotificationCount) {
        this.resendNotificationCount = resendNotificationCount;
    }

    public Integer getCertificateExpiryDays() {
        return this.certificateExpiryDays;
    }

    public HttpMonitor certificateExpiryDays(Integer certificateExpiryDays) {
        this.setCertificateExpiryDays(certificateExpiryDays);
        return this;
    }

    public void setCertificateExpiryDays(Integer certificateExpiryDays) {
        this.certificateExpiryDays = certificateExpiryDays;
    }

    public Boolean getIgnoreTlsError() {
        return this.ignoreTlsError;
    }

    public HttpMonitor ignoreTlsError(Boolean ignoreTlsError) {
        this.setIgnoreTlsError(ignoreTlsError);
        return this;
    }

    public void setIgnoreTlsError(Boolean ignoreTlsError) {
        this.ignoreTlsError = ignoreTlsError;
    }

    public Boolean getCheckSslCertificate() {
        return this.checkSslCertificate;
    }

    public HttpMonitor checkSslCertificate(Boolean checkSslCertificate) {
        this.setCheckSslCertificate(checkSslCertificate);
        return this;
    }

    public void setCheckSslCertificate(Boolean checkSslCertificate) {
        this.checkSslCertificate = checkSslCertificate;
    }

    public Boolean getCheckDnsResolution() {
        return this.checkDnsResolution;
    }

    public HttpMonitor checkDnsResolution(Boolean checkDnsResolution) {
        this.setCheckDnsResolution(checkDnsResolution);
        return this;
    }

    public void setCheckDnsResolution(Boolean checkDnsResolution) {
        this.checkDnsResolution = checkDnsResolution;
    }

    public Boolean getUpsideDownMode() {
        return this.upsideDownMode;
    }

    public HttpMonitor upsideDownMode(Boolean upsideDownMode) {
        this.setUpsideDownMode(upsideDownMode);
        return this;
    }

    public void setUpsideDownMode(Boolean upsideDownMode) {
        this.upsideDownMode = upsideDownMode;
    }

    public Integer getMaxRedirects() {
        return this.maxRedirects;
    }

    public HttpMonitor maxRedirects(Integer maxRedirects) {
        this.setMaxRedirects(maxRedirects);
        return this;
    }

    public void setMaxRedirects(Integer maxRedirects) {
        this.maxRedirects = maxRedirects;
    }

    public String getDescription() {
        return this.description;
    }

    public HttpMonitor description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTags() {
        return this.tags;
    }

    public HttpMonitor tags(String tags) {
        this.setTags(tags);
        return this;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Boolean getEnabled() {
        return this.enabled;
    }

    public HttpMonitor enabled(Boolean enabled) {
        this.setEnabled(enabled);
        return this;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getExpectedStatusCodes() {
        return this.expectedStatusCodes;
    }

    public HttpMonitor expectedStatusCodes(String expectedStatusCodes) {
        this.setExpectedStatusCodes(expectedStatusCodes);
        return this;
    }

    public void setExpectedStatusCodes(String expectedStatusCodes) {
        this.expectedStatusCodes = expectedStatusCodes;
    }

    public Integer getPerformanceBudgetMs() {
        return this.performanceBudgetMs;
    }

    public HttpMonitor performanceBudgetMs(Integer performanceBudgetMs) {
        this.setPerformanceBudgetMs(performanceBudgetMs);
        return this;
    }

    public void setPerformanceBudgetMs(Integer performanceBudgetMs) {
        this.performanceBudgetMs = performanceBudgetMs;
    }

    public Integer getSizeBudgetKb() {
        return this.sizeBudgetKb;
    }

    public HttpMonitor sizeBudgetKb(Integer sizeBudgetKb) {
        this.setSizeBudgetKb(sizeBudgetKb);
        return this;
    }

    public void setSizeBudgetKb(Integer sizeBudgetKb) {
        this.sizeBudgetKb = sizeBudgetKb;
    }

    public Set<HttpMonitor> getChildren() {
        return this.children;
    }

    public void setChildren(Set<HttpMonitor> httpMonitors) {
        if (this.children != null) {
            this.children.forEach(i -> i.setParent(null));
        }
        if (httpMonitors != null) {
            httpMonitors.forEach(i -> i.setParent(this));
        }
        this.children = httpMonitors;
    }

    public HttpMonitor children(Set<HttpMonitor> httpMonitors) {
        this.setChildren(httpMonitors);
        return this;
    }

    public HttpMonitor addChildren(HttpMonitor httpMonitor) {
        this.children.add(httpMonitor);
        httpMonitor.setParent(this);
        return this;
    }

    public HttpMonitor removeChildren(HttpMonitor httpMonitor) {
        this.children.remove(httpMonitor);
        httpMonitor.setParent(null);
        return this;
    }

    public Set<HttpHeartbeat> getHeartbeats() {
        return this.heartbeats;
    }

    public void setHeartbeats(Set<HttpHeartbeat> httpHeartbeats) {
        if (this.heartbeats != null) {
            this.heartbeats.forEach(i -> i.setMonitor(null));
        }
        if (httpHeartbeats != null) {
            httpHeartbeats.forEach(i -> i.setMonitor(this));
        }
        this.heartbeats = httpHeartbeats;
    }

    public HttpMonitor heartbeats(Set<HttpHeartbeat> httpHeartbeats) {
        this.setHeartbeats(httpHeartbeats);
        return this;
    }

    public HttpMonitor addHeartbeat(HttpHeartbeat httpHeartbeat) {
        this.heartbeats.add(httpHeartbeat);
        httpHeartbeat.setMonitor(this);
        return this;
    }

    public HttpMonitor removeHeartbeat(HttpHeartbeat httpHeartbeat) {
        this.heartbeats.remove(httpHeartbeat);
        httpHeartbeat.setMonitor(null);
        return this;
    }

    public HttpMonitor getParent() {
        return this.parent;
    }

    public void setParent(HttpMonitor httpMonitor) {
        this.parent = httpMonitor;
    }

    public HttpMonitor parent(HttpMonitor httpMonitor) {
        this.setParent(httpMonitor);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HttpMonitor)) {
            return false;
        }
        return getId() != null && getId().equals(((HttpMonitor) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HttpMonitor{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", method='" + getMethod() + "'" +
            ", type='" + getType() + "'" +
            ", url='" + getUrl() + "'" +
            ", headers='" + getHeaders() + "'" +
            ", body='" + getBody() + "'" +
            ", intervalSeconds=" + getIntervalSeconds() +
            ", timeoutSeconds=" + getTimeoutSeconds() +
            ", retryCount=" + getRetryCount() +
            ", retryDelaySeconds=" + getRetryDelaySeconds() +
            ", responseTimeWarningMs=" + getResponseTimeWarningMs() +
            ", responseTimeCriticalMs=" + getResponseTimeCriticalMs() +
            ", uptimeWarningPercent=" + getUptimeWarningPercent() +
            ", uptimeCriticalPercent=" + getUptimeCriticalPercent() +
            ", includeResponseBody='" + getIncludeResponseBody() + "'" +
            ", resendNotificationCount=" + getResendNotificationCount() +
            ", certificateExpiryDays=" + getCertificateExpiryDays() +
            ", ignoreTlsError='" + getIgnoreTlsError() + "'" +
            ", checkSslCertificate='" + getCheckSslCertificate() + "'" +
            ", checkDnsResolution='" + getCheckDnsResolution() + "'" +
            ", upsideDownMode='" + getUpsideDownMode() + "'" +
            ", maxRedirects=" + getMaxRedirects() +
            ", description='" + getDescription() + "'" +
            ", tags='" + getTags() + "'" +
            ", enabled='" + getEnabled() + "'" +
            ", expectedStatusCodes='" + getExpectedStatusCodes() + "'" +
            ", performanceBudgetMs=" + getPerformanceBudgetMs() +
            ", sizeBudgetKb=" + getSizeBudgetKb() +
            "}";
    }
}
