package vibhuvi.oio.inframirror.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link vibhuvi.oio.inframirror.domain.HttpHeartbeat} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class HttpHeartbeatDTO implements Serializable {

    @NotNull
    private Long id;

    @NotNull
    private Instant executedAt;

    private Boolean success;

    private Integer responseTimeMs;

    private Integer responseSizeBytes;

    private Integer responseStatusCode;

    @Size(max = 50)
    private String responseContentType;

    @Size(max = 50)
    private String responseServer;

    @Size(max = 50)
    private String responseCacheStatus;

    private Integer dnsLookupMs;

    @Size(max = 100)
    private String dnsResolvedIp;

    private Integer tcpConnectMs;

    private Integer tlsHandshakeMs;

    private Boolean sslCertificateValid;

    private Instant sslCertificateExpiry;

    @Size(max = 500)
    private String sslCertificateIssuer;

    private Integer sslDaysUntilExpiry;

    private Integer timeToFirstByteMs;

    private Integer warningThresholdMs;

    private Integer criticalThresholdMs;

    @Size(max = 50)
    private String errorType;

    @Lob
    private String errorMessage;

    @Lob
    private String rawRequestHeaders;

    @Lob
    private String rawResponseHeaders;

    @Lob
    private String rawResponseBody;

    @Lob
    private String dnsDetails;

    @Lob
    private String tlsDetails;

    @Size(max = 10)
    private String httpVersion;

    @Size(max = 20)
    private String contentEncoding;

    private Float compressionRatio;

    @Size(max = 20)
    private String transferEncoding;

    @Size(max = 64)
    private String responseBodyHash;

    @Lob
    private String responseBodySample;

    private Boolean responseBodyValid;

    private Integer responseBodyUncompressedBytes;

    @Lob
    private String redirectDetails;

    @Size(max = 255)
    private String cacheControl;

    @Size(max = 255)
    private String etag;

    private Integer cacheAge;

    @Size(max = 50)
    private String cdnProvider;

    @Size(max = 10)
    private String cdnPop;

    @Lob
    private String rateLimitDetails;

    @Lob
    private String networkPath;

    @Lob
    private String agentMetrics;

    @Lob
    private String phaseLatencies;

    private AgentDTO agent;

    private HttpMonitorDTO monitor;

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

    public Integer getResponseSizeBytes() {
        return responseSizeBytes;
    }

    public void setResponseSizeBytes(Integer responseSizeBytes) {
        this.responseSizeBytes = responseSizeBytes;
    }

    public Integer getResponseStatusCode() {
        return responseStatusCode;
    }

    public void setResponseStatusCode(Integer responseStatusCode) {
        this.responseStatusCode = responseStatusCode;
    }

    public String getResponseContentType() {
        return responseContentType;
    }

    public void setResponseContentType(String responseContentType) {
        this.responseContentType = responseContentType;
    }

    public String getResponseServer() {
        return responseServer;
    }

    public void setResponseServer(String responseServer) {
        this.responseServer = responseServer;
    }

    public String getResponseCacheStatus() {
        return responseCacheStatus;
    }

    public void setResponseCacheStatus(String responseCacheStatus) {
        this.responseCacheStatus = responseCacheStatus;
    }

    public Integer getDnsLookupMs() {
        return dnsLookupMs;
    }

    public void setDnsLookupMs(Integer dnsLookupMs) {
        this.dnsLookupMs = dnsLookupMs;
    }

    public String getDnsResolvedIp() {
        return dnsResolvedIp;
    }

    public void setDnsResolvedIp(String dnsResolvedIp) {
        this.dnsResolvedIp = dnsResolvedIp;
    }

    public Integer getTcpConnectMs() {
        return tcpConnectMs;
    }

    public void setTcpConnectMs(Integer tcpConnectMs) {
        this.tcpConnectMs = tcpConnectMs;
    }

    public Integer getTlsHandshakeMs() {
        return tlsHandshakeMs;
    }

    public void setTlsHandshakeMs(Integer tlsHandshakeMs) {
        this.tlsHandshakeMs = tlsHandshakeMs;
    }

    public Boolean getSslCertificateValid() {
        return sslCertificateValid;
    }

    public void setSslCertificateValid(Boolean sslCertificateValid) {
        this.sslCertificateValid = sslCertificateValid;
    }

    public Instant getSslCertificateExpiry() {
        return sslCertificateExpiry;
    }

    public void setSslCertificateExpiry(Instant sslCertificateExpiry) {
        this.sslCertificateExpiry = sslCertificateExpiry;
    }

    public String getSslCertificateIssuer() {
        return sslCertificateIssuer;
    }

    public void setSslCertificateIssuer(String sslCertificateIssuer) {
        this.sslCertificateIssuer = sslCertificateIssuer;
    }

    public Integer getSslDaysUntilExpiry() {
        return sslDaysUntilExpiry;
    }

    public void setSslDaysUntilExpiry(Integer sslDaysUntilExpiry) {
        this.sslDaysUntilExpiry = sslDaysUntilExpiry;
    }

    public Integer getTimeToFirstByteMs() {
        return timeToFirstByteMs;
    }

    public void setTimeToFirstByteMs(Integer timeToFirstByteMs) {
        this.timeToFirstByteMs = timeToFirstByteMs;
    }

    public Integer getWarningThresholdMs() {
        return warningThresholdMs;
    }

    public void setWarningThresholdMs(Integer warningThresholdMs) {
        this.warningThresholdMs = warningThresholdMs;
    }

    public Integer getCriticalThresholdMs() {
        return criticalThresholdMs;
    }

    public void setCriticalThresholdMs(Integer criticalThresholdMs) {
        this.criticalThresholdMs = criticalThresholdMs;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getRawRequestHeaders() {
        return rawRequestHeaders;
    }

    public void setRawRequestHeaders(String rawRequestHeaders) {
        this.rawRequestHeaders = rawRequestHeaders;
    }

    public String getRawResponseHeaders() {
        return rawResponseHeaders;
    }

    public void setRawResponseHeaders(String rawResponseHeaders) {
        this.rawResponseHeaders = rawResponseHeaders;
    }

    public String getRawResponseBody() {
        return rawResponseBody;
    }

    public void setRawResponseBody(String rawResponseBody) {
        this.rawResponseBody = rawResponseBody;
    }

    public String getDnsDetails() {
        return dnsDetails;
    }

    public void setDnsDetails(String dnsDetails) {
        this.dnsDetails = dnsDetails;
    }

    public String getTlsDetails() {
        return tlsDetails;
    }

    public void setTlsDetails(String tlsDetails) {
        this.tlsDetails = tlsDetails;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    public String getContentEncoding() {
        return contentEncoding;
    }

    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    public Float getCompressionRatio() {
        return compressionRatio;
    }

    public void setCompressionRatio(Float compressionRatio) {
        this.compressionRatio = compressionRatio;
    }

    public String getTransferEncoding() {
        return transferEncoding;
    }

    public void setTransferEncoding(String transferEncoding) {
        this.transferEncoding = transferEncoding;
    }

    public String getResponseBodyHash() {
        return responseBodyHash;
    }

    public void setResponseBodyHash(String responseBodyHash) {
        this.responseBodyHash = responseBodyHash;
    }

    public String getResponseBodySample() {
        return responseBodySample;
    }

    public void setResponseBodySample(String responseBodySample) {
        this.responseBodySample = responseBodySample;
    }

    public Boolean getResponseBodyValid() {
        return responseBodyValid;
    }

    public void setResponseBodyValid(Boolean responseBodyValid) {
        this.responseBodyValid = responseBodyValid;
    }

    public Integer getResponseBodyUncompressedBytes() {
        return responseBodyUncompressedBytes;
    }

    public void setResponseBodyUncompressedBytes(Integer responseBodyUncompressedBytes) {
        this.responseBodyUncompressedBytes = responseBodyUncompressedBytes;
    }

    public String getRedirectDetails() {
        return redirectDetails;
    }

    public void setRedirectDetails(String redirectDetails) {
        this.redirectDetails = redirectDetails;
    }

    public String getCacheControl() {
        return cacheControl;
    }

    public void setCacheControl(String cacheControl) {
        this.cacheControl = cacheControl;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public Integer getCacheAge() {
        return cacheAge;
    }

    public void setCacheAge(Integer cacheAge) {
        this.cacheAge = cacheAge;
    }

    public String getCdnProvider() {
        return cdnProvider;
    }

    public void setCdnProvider(String cdnProvider) {
        this.cdnProvider = cdnProvider;
    }

    public String getCdnPop() {
        return cdnPop;
    }

    public void setCdnPop(String cdnPop) {
        this.cdnPop = cdnPop;
    }

    public String getRateLimitDetails() {
        return rateLimitDetails;
    }

    public void setRateLimitDetails(String rateLimitDetails) {
        this.rateLimitDetails = rateLimitDetails;
    }

    public String getNetworkPath() {
        return networkPath;
    }

    public void setNetworkPath(String networkPath) {
        this.networkPath = networkPath;
    }

    public String getAgentMetrics() {
        return agentMetrics;
    }

    public void setAgentMetrics(String agentMetrics) {
        this.agentMetrics = agentMetrics;
    }

    public String getPhaseLatencies() {
        return phaseLatencies;
    }

    public void setPhaseLatencies(String phaseLatencies) {
        this.phaseLatencies = phaseLatencies;
    }

    public AgentDTO getAgent() {
        return agent;
    }

    public void setAgent(AgentDTO agent) {
        this.agent = agent;
    }

    public HttpMonitorDTO getMonitor() {
        return monitor;
    }

    public void setMonitor(HttpMonitorDTO monitor) {
        this.monitor = monitor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HttpHeartbeatDTO)) {
            return false;
        }

        HttpHeartbeatDTO httpHeartbeatDTO = (HttpHeartbeatDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, httpHeartbeatDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HttpHeartbeatDTO{" +
            "id=" + getId() +
            ", executedAt='" + getExecutedAt() + "'" +
            ", success='" + getSuccess() + "'" +
            ", responseTimeMs=" + getResponseTimeMs() +
            ", responseSizeBytes=" + getResponseSizeBytes() +
            ", responseStatusCode=" + getResponseStatusCode() +
            ", responseContentType='" + getResponseContentType() + "'" +
            ", responseServer='" + getResponseServer() + "'" +
            ", responseCacheStatus='" + getResponseCacheStatus() + "'" +
            ", dnsLookupMs=" + getDnsLookupMs() +
            ", dnsResolvedIp='" + getDnsResolvedIp() + "'" +
            ", tcpConnectMs=" + getTcpConnectMs() +
            ", tlsHandshakeMs=" + getTlsHandshakeMs() +
            ", sslCertificateValid='" + getSslCertificateValid() + "'" +
            ", sslCertificateExpiry='" + getSslCertificateExpiry() + "'" +
            ", sslCertificateIssuer='" + getSslCertificateIssuer() + "'" +
            ", sslDaysUntilExpiry=" + getSslDaysUntilExpiry() +
            ", timeToFirstByteMs=" + getTimeToFirstByteMs() +
            ", warningThresholdMs=" + getWarningThresholdMs() +
            ", criticalThresholdMs=" + getCriticalThresholdMs() +
            ", errorType='" + getErrorType() + "'" +
            ", errorMessage='" + getErrorMessage() + "'" +
            ", rawRequestHeaders='" + getRawRequestHeaders() + "'" +
            ", rawResponseHeaders='" + getRawResponseHeaders() + "'" +
            ", rawResponseBody='" + getRawResponseBody() + "'" +
            ", dnsDetails='" + getDnsDetails() + "'" +
            ", tlsDetails='" + getTlsDetails() + "'" +
            ", httpVersion='" + getHttpVersion() + "'" +
            ", contentEncoding='" + getContentEncoding() + "'" +
            ", compressionRatio=" + getCompressionRatio() +
            ", transferEncoding='" + getTransferEncoding() + "'" +
            ", responseBodyHash='" + getResponseBodyHash() + "'" +
            ", responseBodySample='" + getResponseBodySample() + "'" +
            ", responseBodyValid='" + getResponseBodyValid() + "'" +
            ", responseBodyUncompressedBytes=" + getResponseBodyUncompressedBytes() +
            ", redirectDetails='" + getRedirectDetails() + "'" +
            ", cacheControl='" + getCacheControl() + "'" +
            ", etag='" + getEtag() + "'" +
            ", cacheAge=" + getCacheAge() +
            ", cdnProvider='" + getCdnProvider() + "'" +
            ", cdnPop='" + getCdnPop() + "'" +
            ", rateLimitDetails='" + getRateLimitDetails() + "'" +
            ", networkPath='" + getNetworkPath() + "'" +
            ", agentMetrics='" + getAgentMetrics() + "'" +
            ", phaseLatencies='" + getPhaseLatencies() + "'" +
            ", agent=" + getAgent() +
            ", monitor=" + getMonitor() +
            "}";
    }
}
