package vibhuvi.oio.inframirror.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A StatusPage.
 */
@Entity
@Table(name = "status_page")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StatusPage implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Size(max = 200)
    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @NotNull
    @Size(max = 100)
    @Column(name = "slug", length = 100, nullable = false, unique = true)
    private String slug;

    @Size(max = 500)
    @Column(name = "description", length = 500)
    private String description;

    @NotNull
    @Column(name = "is_public", nullable = false)
    private Boolean isPublic;

    @Size(max = 255)
    @Column(name = "custom_domain", length = 255)
    private String customDomain;

    @Size(max = 500)
    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Size(max = 7)
    @Column(name = "theme_color", length = 7)
    private String themeColor;

    @Size(max = 500)
    @Column(name = "header_text", length = 500)
    private String headerText;

    @Size(max = 500)
    @Column(name = "footer_text", length = 500)
    private String footerText;

    @Column(name = "show_response_times")
    private Boolean showResponseTimes;

    @Column(name = "show_uptime_percentage")
    private Boolean showUptimePercentage;

    @Column(name = "auto_refresh_seconds")
    private Integer autoRefreshSeconds;

    @Column(name = "monitor_selection", columnDefinition = "TEXT")
    private String monitorSelection;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "is_home_page")
    private Boolean isHomePage;

    @Column(name = "allowed_roles", columnDefinition = "TEXT")
    private String allowedRoles;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "statusPage")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "statusPage" }, allowSetters = true)
    private Set<StatusPageItem> items = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "statusPage")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "statusPage" }, allowSetters = true)
    private Set<StatusDependency> statusDependencies = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public StatusPage id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public StatusPage name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return this.slug;
    }

    public StatusPage slug(String slug) {
        this.setSlug(slug);
        return this;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return this.description;
    }

    public StatusPage description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsPublic() {
        return this.isPublic;
    }

    public StatusPage isPublic(Boolean isPublic) {
        this.setIsPublic(isPublic);
        return this;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getCustomDomain() {
        return this.customDomain;
    }

    public StatusPage customDomain(String customDomain) {
        this.setCustomDomain(customDomain);
        return this;
    }

    public void setCustomDomain(String customDomain) {
        this.customDomain = customDomain;
    }

    public String getLogoUrl() {
        return this.logoUrl;
    }

    public StatusPage logoUrl(String logoUrl) {
        this.setLogoUrl(logoUrl);
        return this;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getThemeColor() {
        return this.themeColor;
    }

    public StatusPage themeColor(String themeColor) {
        this.setThemeColor(themeColor);
        return this;
    }

    public void setThemeColor(String themeColor) {
        this.themeColor = themeColor;
    }

    public String getHeaderText() {
        return this.headerText;
    }

    public StatusPage headerText(String headerText) {
        this.setHeaderText(headerText);
        return this;
    }

    public void setHeaderText(String headerText) {
        this.headerText = headerText;
    }

    public String getFooterText() {
        return this.footerText;
    }

    public StatusPage footerText(String footerText) {
        this.setFooterText(footerText);
        return this;
    }

    public void setFooterText(String footerText) {
        this.footerText = footerText;
    }

    public Boolean getShowResponseTimes() {
        return this.showResponseTimes;
    }

    public StatusPage showResponseTimes(Boolean showResponseTimes) {
        this.setShowResponseTimes(showResponseTimes);
        return this;
    }

    public void setShowResponseTimes(Boolean showResponseTimes) {
        this.showResponseTimes = showResponseTimes;
    }

    public Boolean getShowUptimePercentage() {
        return this.showUptimePercentage;
    }

    public StatusPage showUptimePercentage(Boolean showUptimePercentage) {
        this.setShowUptimePercentage(showUptimePercentage);
        return this;
    }

    public void setShowUptimePercentage(Boolean showUptimePercentage) {
        this.showUptimePercentage = showUptimePercentage;
    }

    public Integer getAutoRefreshSeconds() {
        return this.autoRefreshSeconds;
    }

    public StatusPage autoRefreshSeconds(Integer autoRefreshSeconds) {
        this.setAutoRefreshSeconds(autoRefreshSeconds);
        return this;
    }

    public void setAutoRefreshSeconds(Integer autoRefreshSeconds) {
        this.autoRefreshSeconds = autoRefreshSeconds;
    }

    public String getMonitorSelection() {
        return this.monitorSelection;
    }

    public StatusPage monitorSelection(String monitorSelection) {
        this.setMonitorSelection(monitorSelection);
        return this;
    }

    public void setMonitorSelection(String monitorSelection) {
        this.monitorSelection = monitorSelection;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public StatusPage isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsHomePage() {
        return this.isHomePage;
    }

    public StatusPage isHomePage(Boolean isHomePage) {
        this.setIsHomePage(isHomePage);
        return this;
    }

    public void setIsHomePage(Boolean isHomePage) {
        this.isHomePage = isHomePage;
    }

    public String getAllowedRoles() {
        return this.allowedRoles;
    }

    public StatusPage allowedRoles(String allowedRoles) {
        this.setAllowedRoles(allowedRoles);
        return this;
    }

    public void setAllowedRoles(String allowedRoles) {
        this.allowedRoles = allowedRoles;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public StatusPage createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public StatusPage updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<StatusPageItem> getItems() {
        return this.items;
    }

    public void setItems(Set<StatusPageItem> statusPageItems) {
        if (this.items != null) {
            this.items.forEach(i -> i.setStatusPage(null));
        }
        if (statusPageItems != null) {
            statusPageItems.forEach(i -> i.setStatusPage(this));
        }
        this.items = statusPageItems;
    }

    public StatusPage items(Set<StatusPageItem> statusPageItems) {
        this.setItems(statusPageItems);
        return this;
    }

    public StatusPage addItem(StatusPageItem statusPageItem) {
        this.items.add(statusPageItem);
        statusPageItem.setStatusPage(this);
        return this;
    }

    public StatusPage removeItem(StatusPageItem statusPageItem) {
        this.items.remove(statusPageItem);
        statusPageItem.setStatusPage(null);
        return this;
    }

    public Set<StatusDependency> getStatusDependencies() {
        return this.statusDependencies;
    }

    public void setStatusDependencies(Set<StatusDependency> statusDependencies) {
        if (this.statusDependencies != null) {
            this.statusDependencies.forEach(i -> i.setStatusPage(null));
        }
        if (statusDependencies != null) {
            statusDependencies.forEach(i -> i.setStatusPage(this));
        }
        this.statusDependencies = statusDependencies;
    }

    public StatusPage statusDependencies(Set<StatusDependency> statusDependencies) {
        this.setStatusDependencies(statusDependencies);
        return this;
    }

    public StatusPage addStatusDependency(StatusDependency statusDependency) {
        this.statusDependencies.add(statusDependency);
        statusDependency.setStatusPage(this);
        return this;
    }

    public StatusPage removeStatusDependency(StatusDependency statusDependency) {
        this.statusDependencies.remove(statusDependency);
        statusDependency.setStatusPage(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StatusPage)) {
            return false;
        }
        return getId() != null && getId().equals(((StatusPage) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StatusPage{" +
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
