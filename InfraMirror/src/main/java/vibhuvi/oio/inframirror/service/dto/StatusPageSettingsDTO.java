package vibhuvi.oio.inframirror.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link vibhuvi.oio.inframirror.domain.StatusPageSettings} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StatusPageSettingsDTO implements Serializable {

    private Long id;

    @NotNull
    private Long statusPageId;

    @Size(max = 500)
    private String logoUrl;

    @Size(max = 7)
    private String themeColor;

    @Size(max = 255)
    private String customDomain;

    @Size(max = 500)
    private String headerText;

    @Size(max = 500)
    private String footerText;

    private Boolean showResponseTimes;

    private Boolean showUptimePercentage;

    private Integer autoRefreshSeconds;

    private Integer sampleSize;

    private Double successThresholdHigh;

    private Double successThresholdLow;

    private Integer warningThresholdMs;

    private Integer criticalThresholdMs;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStatusPageId() {
        return statusPageId;
    }

    public void setStatusPageId(Long statusPageId) {
        this.statusPageId = statusPageId;
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

    public Double getSuccessThresholdHigh() {
        return successThresholdHigh;
    }

    public void setSuccessThresholdHigh(Double successThresholdHigh) {
        this.successThresholdHigh = successThresholdHigh;
    }

    public Double getSuccessThresholdLow() {
        return successThresholdLow;
    }

    public void setSuccessThresholdLow(Double successThresholdLow) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StatusPageSettingsDTO)) {
            return false;
        }

        StatusPageSettingsDTO statusPageSettingsDTO = (StatusPageSettingsDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, statusPageSettingsDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StatusPageSettingsDTO{" +
            "id=" + getId() +
            ", statusPageId=" + getStatusPageId() +
            ", logoUrl='" + getLogoUrl() + "'" +
            ", themeColor='" + getThemeColor() + "'" +
            ", customDomain='" + getCustomDomain() + "'" +
            ", headerText='" + getHeaderText() + "'" +
            ", footerText='" + getFooterText() + "'" +
            ", showResponseTimes='" + getShowResponseTimes() + "'" +
            ", showUptimePercentage='" + getShowUptimePercentage() + "'" +
            ", autoRefreshSeconds=" + getAutoRefreshSeconds() +
            ", sampleSize=" + getSampleSize() +
            ", successThresholdHigh=" + getSuccessThresholdHigh() +
            ", successThresholdLow=" + getSuccessThresholdLow() +
            ", warningThresholdMs=" + getWarningThresholdMs() +
            ", criticalThresholdMs=" + getCriticalThresholdMs() +
            "}";
    }
}
