package vibhuvi.oio.inframirror.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link vibhuvi.oio.inframirror.domain.StatusPage} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StatusPageDTO implements Serializable {

    @NotNull
    private Long id;

    @NotNull
    @Size(max = 200)
    private String name;

    @NotNull
    @Size(max = 100)
    private String slug;

    @Size(max = 500)
    private String description;

    @NotNull
    private Boolean isPublic;

    @Size(max = 255)
    private String customDomain;

    @Size(max = 500)
    private String logoUrl;

    @Size(max = 7)
    private String themeColor;

    @Size(max = 500)
    private String headerText;

    @Size(max = 500)
    private String footerText;

    private Boolean showResponseTimes;

    private Boolean showUptimePercentage;

    private Integer autoRefreshSeconds;

    @Lob
    private String monitorSelection;

    private Boolean isActive;

    private Boolean isHomePage;

    @Lob
    private String allowedRoles;

    @NotNull
    private Instant createdAt;

    @NotNull
    private Instant updatedAt;

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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getCustomDomain() {
        return customDomain;
    }

    public void setCustomDomain(String customDomain) {
        this.customDomain = customDomain;
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

    public String getMonitorSelection() {
        return monitorSelection;
    }

    public void setMonitorSelection(String monitorSelection) {
        this.monitorSelection = monitorSelection;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsHomePage() {
        return isHomePage;
    }

    public void setIsHomePage(Boolean isHomePage) {
        this.isHomePage = isHomePage;
    }

    public String getAllowedRoles() {
        return allowedRoles;
    }

    public void setAllowedRoles(String allowedRoles) {
        this.allowedRoles = allowedRoles;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StatusPageDTO)) {
            return false;
        }

        StatusPageDTO statusPageDTO = (StatusPageDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, statusPageDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StatusPageDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", slug='" + getSlug() + "'" +
            ", description='" + getDescription() + "'" +
            ", isPublic='" + getIsPublic() + "'" +
            ", customDomain='" + getCustomDomain() + "'" +
            ", logoUrl='" + getLogoUrl() + "'" +
            ", themeColor='" + getThemeColor() + "'" +
            ", headerText='" + getHeaderText() + "'" +
            ", footerText='" + getFooterText() + "'" +
            ", showResponseTimes='" + getShowResponseTimes() + "'" +
            ", showUptimePercentage='" + getShowUptimePercentage() + "'" +
            ", autoRefreshSeconds=" + getAutoRefreshSeconds() +
            ", monitorSelection='" + getMonitorSelection() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", isHomePage='" + getIsHomePage() + "'" +
            ", allowedRoles='" + getAllowedRoles() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
