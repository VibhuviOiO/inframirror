package vibhuvi.oio.inframirror.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "status_page_settings")
public class StatusPageSettings implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Size(max = 500)
    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Size(max = 20)
    @Column(name = "theme_color", length = 20)
    private String themeColor;

    @Size(max = 255)
    @Column(name = "custom_domain", length = 255)
    private String customDomain;

    @Column(name = "header_text", columnDefinition = "TEXT")
    private String headerText;

    @Column(name = "footer_text", columnDefinition = "TEXT")
    private String footerText;

    @Column(name = "show_response_times")
    private Boolean showResponseTimes;

    @Column(name = "show_uptime_percentage")
    private Boolean showUptimePercentage;

    @Column(name = "auto_refresh_seconds")
    private Integer autoRefreshSeconds;

    @NotNull
    @Column(name = "sample_size", nullable = false)
    private Integer sampleSize;

    @NotNull
    @Column(name = "success_threshold_high", precision = 3, scale = 2, nullable = false)
    private BigDecimal successThresholdHigh;

    @NotNull
    @Column(name = "success_threshold_low", precision = 3, scale = 2, nullable = false)
    private BigDecimal successThresholdLow;

    @NotNull
    @Column(name = "warning_threshold_ms", nullable = false)
    private Integer warningThresholdMs;

    @NotNull
    @Column(name = "critical_threshold_ms", nullable = false)
    private Integer criticalThresholdMs;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_page_id", unique = true, nullable = false)
    @JsonIgnoreProperties(value = {"settings", "items"}, allowSetters = true)
    private StatusPage statusPage;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getThemeColor() {
        return themeColor;
    }

    public void setThemeColor(String themeColor) {
        this.themeColor = themeColor;
    }

    public String getCustomDomain() {
        return customDomain;
    }

    public void setCustomDomain(String customDomain) {
        this.customDomain = customDomain;
    }

    public String getHeaderText() {
        return headerText;
    }

    public void setHeaderText(String headerText) {
        this.headerText = headerText;
    }

    public String getFooterText() {
        return footerText;
    }

    public void setFooterText(String footerText) {
        this.footerText = footerText;
    }

    public Boolean getShowResponseTimes() {
        return showResponseTimes;
    }

    public void setShowResponseTimes(Boolean showResponseTimes) {
        this.showResponseTimes = showResponseTimes;
    }

    public Boolean getShowUptimePercentage() {
        return showUptimePercentage;
    }

    public void setShowUptimePercentage(Boolean showUptimePercentage) {
        this.showUptimePercentage = showUptimePercentage;
    }

    public Integer getAutoRefreshSeconds() {
        return autoRefreshSeconds;
    }

    public void setAutoRefreshSeconds(Integer autoRefreshSeconds) {
        this.autoRefreshSeconds = autoRefreshSeconds;
    }

    public Integer getSampleSize() {
        return sampleSize;
    }

    public void setSampleSize(Integer sampleSize) {
        this.sampleSize = sampleSize;
    }

    public BigDecimal getSuccessThresholdHigh() {
        return successThresholdHigh;
    }

    public void setSuccessThresholdHigh(BigDecimal successThresholdHigh) {
        this.successThresholdHigh = successThresholdHigh;
    }

    public BigDecimal getSuccessThresholdLow() {
        return successThresholdLow;
    }

    public void setSuccessThresholdLow(BigDecimal successThresholdLow) {
        this.successThresholdLow = successThresholdLow;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public StatusPage getStatusPage() {
        return statusPage;
    }

    public void setStatusPage(StatusPage statusPage) {
        this.statusPage = statusPage;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StatusPageSettings)) return false;
        return id != null && id.equals(((StatusPageSettings) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "StatusPageSettings{" +
            "id=" + id +
            ", sampleSize=" + sampleSize +
            ", successThresholdHigh=" + successThresholdHigh +
            ", successThresholdLow=" + successThresholdLow +
            ", warningThresholdMs=" + warningThresholdMs +
            ", criticalThresholdMs=" + criticalThresholdMs +
            '}';
    }
}
