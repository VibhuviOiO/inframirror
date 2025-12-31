package vibhuvi.oio.inframirror.service.dto;

import java.io.Serializable;
import java.time.Instant;

public class PublicMonitorStatusDTO implements Serializable {

    private Long id;
    private String name;
    private String url;
    private String status; // UP, DEGRADED, DOWN
    private Integer responseTimeMs;
    private Instant lastChecked;
    private Double uptimePercentage;
    private Integer statusCode;
    private String errorMessage;
    
    // Detailed metrics for internal view
    private Double avgResponseTime;
    private Integer dnsTimeMs;
    private Integer tcpTimeMs;
    private Integer tlsTimeMs;
    private Integer ttfbMs;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getResponseTimeMs() {
        return responseTimeMs;
    }

    public void setResponseTimeMs(Integer responseTimeMs) {
        this.responseTimeMs = responseTimeMs;
    }

    public Instant getLastChecked() {
        return lastChecked;
    }

    public void setLastChecked(Instant lastChecked) {
        this.lastChecked = lastChecked;
    }

    public Double getUptimePercentage() {
        return uptimePercentage;
    }

    public void setUptimePercentage(Double uptimePercentage) {
        this.uptimePercentage = uptimePercentage;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Double getAvgResponseTime() {
        return avgResponseTime;
    }

    public void setAvgResponseTime(Double avgResponseTime) {
        this.avgResponseTime = avgResponseTime;
    }

    public Integer getDnsTimeMs() {
        return dnsTimeMs;
    }

    public void setDnsTimeMs(Integer dnsTimeMs) {
        this.dnsTimeMs = dnsTimeMs;
    }

    public Integer getTcpTimeMs() {
        return tcpTimeMs;
    }

    public void setTcpTimeMs(Integer tcpTimeMs) {
        this.tcpTimeMs = tcpTimeMs;
    }

    public Integer getTlsTimeMs() {
        return tlsTimeMs;
    }

    public void setTlsTimeMs(Integer tlsTimeMs) {
        this.tlsTimeMs = tlsTimeMs;
    }

    public Integer getTtfbMs() {
        return ttfbMs;
    }

    public void setTtfbMs(Integer ttfbMs) {
        this.ttfbMs = ttfbMs;
    }
}
