package vibhuvi.oio.inframirror.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link vibhuvi.oio.inframirror.domain.StatusPage} entity. This class is used
 * in {@link vibhuvi.oio.inframirror.web.rest.StatusPageResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /status-pages?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StatusPageCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter slug;

    private StringFilter description;

    private BooleanFilter isPublic;

    private StringFilter customDomain;

    private StringFilter logoUrl;

    private StringFilter themeColor;

    private StringFilter headerText;

    private StringFilter footerText;

    private BooleanFilter showResponseTimes;

    private BooleanFilter showUptimePercentage;

    private IntegerFilter autoRefreshSeconds;

    private BooleanFilter isActive;

    private BooleanFilter isHomePage;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private LongFilter itemId;

    private LongFilter statusDependencyId;

    private Boolean distinct;

    public StatusPageCriteria() {}

    public StatusPageCriteria(StatusPageCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.slug = other.optionalSlug().map(StringFilter::copy).orElse(null);
        this.description = other.optionalDescription().map(StringFilter::copy).orElse(null);
        this.isPublic = other.optionalIsPublic().map(BooleanFilter::copy).orElse(null);
        this.customDomain = other.optionalCustomDomain().map(StringFilter::copy).orElse(null);
        this.logoUrl = other.optionalLogoUrl().map(StringFilter::copy).orElse(null);
        this.themeColor = other.optionalThemeColor().map(StringFilter::copy).orElse(null);
        this.headerText = other.optionalHeaderText().map(StringFilter::copy).orElse(null);
        this.footerText = other.optionalFooterText().map(StringFilter::copy).orElse(null);
        this.showResponseTimes = other.optionalShowResponseTimes().map(BooleanFilter::copy).orElse(null);
        this.showUptimePercentage = other.optionalShowUptimePercentage().map(BooleanFilter::copy).orElse(null);
        this.autoRefreshSeconds = other.optionalAutoRefreshSeconds().map(IntegerFilter::copy).orElse(null);
        this.isActive = other.optionalIsActive().map(BooleanFilter::copy).orElse(null);
        this.isHomePage = other.optionalIsHomePage().map(BooleanFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.itemId = other.optionalItemId().map(LongFilter::copy).orElse(null);
        this.statusDependencyId = other.optionalStatusDependencyId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public StatusPageCriteria copy() {
        return new StatusPageCriteria(this);
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

    public StringFilter getSlug() {
        return slug;
    }

    public Optional<StringFilter> optionalSlug() {
        return Optional.ofNullable(slug);
    }

    public StringFilter slug() {
        if (slug == null) {
            setSlug(new StringFilter());
        }
        return slug;
    }

    public void setSlug(StringFilter slug) {
        this.slug = slug;
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

    public BooleanFilter getIsPublic() {
        return isPublic;
    }

    public Optional<BooleanFilter> optionalIsPublic() {
        return Optional.ofNullable(isPublic);
    }

    public BooleanFilter isPublic() {
        if (isPublic == null) {
            setIsPublic(new BooleanFilter());
        }
        return isPublic;
    }

    public void setIsPublic(BooleanFilter isPublic) {
        this.isPublic = isPublic;
    }

    public StringFilter getCustomDomain() {
        return customDomain;
    }

    public Optional<StringFilter> optionalCustomDomain() {
        return Optional.ofNullable(customDomain);
    }

    public StringFilter customDomain() {
        if (customDomain == null) {
            setCustomDomain(new StringFilter());
        }
        return customDomain;
    }

    public void setCustomDomain(StringFilter customDomain) {
        this.customDomain = customDomain;
    }

    public StringFilter getLogoUrl() {
        return logoUrl;
    }

    public Optional<StringFilter> optionalLogoUrl() {
        return Optional.ofNullable(logoUrl);
    }

    public StringFilter logoUrl() {
        if (logoUrl == null) {
            setLogoUrl(new StringFilter());
        }
        return logoUrl;
    }

    public void setLogoUrl(StringFilter logoUrl) {
        this.logoUrl = logoUrl;
    }

    public StringFilter getThemeColor() {
        return themeColor;
    }

    public Optional<StringFilter> optionalThemeColor() {
        return Optional.ofNullable(themeColor);
    }

    public StringFilter themeColor() {
        if (themeColor == null) {
            setThemeColor(new StringFilter());
        }
        return themeColor;
    }

    public void setThemeColor(StringFilter themeColor) {
        this.themeColor = themeColor;
    }

    public StringFilter getHeaderText() {
        return headerText;
    }

    public Optional<StringFilter> optionalHeaderText() {
        return Optional.ofNullable(headerText);
    }

    public StringFilter headerText() {
        if (headerText == null) {
            setHeaderText(new StringFilter());
        }
        return headerText;
    }

    public void setHeaderText(StringFilter headerText) {
        this.headerText = headerText;
    }

    public StringFilter getFooterText() {
        return footerText;
    }

    public Optional<StringFilter> optionalFooterText() {
        return Optional.ofNullable(footerText);
    }

    public StringFilter footerText() {
        if (footerText == null) {
            setFooterText(new StringFilter());
        }
        return footerText;
    }

    public void setFooterText(StringFilter footerText) {
        this.footerText = footerText;
    }

    public BooleanFilter getShowResponseTimes() {
        return showResponseTimes;
    }

    public Optional<BooleanFilter> optionalShowResponseTimes() {
        return Optional.ofNullable(showResponseTimes);
    }

    public BooleanFilter showResponseTimes() {
        if (showResponseTimes == null) {
            setShowResponseTimes(new BooleanFilter());
        }
        return showResponseTimes;
    }

    public void setShowResponseTimes(BooleanFilter showResponseTimes) {
        this.showResponseTimes = showResponseTimes;
    }

    public BooleanFilter getShowUptimePercentage() {
        return showUptimePercentage;
    }

    public Optional<BooleanFilter> optionalShowUptimePercentage() {
        return Optional.ofNullable(showUptimePercentage);
    }

    public BooleanFilter showUptimePercentage() {
        if (showUptimePercentage == null) {
            setShowUptimePercentage(new BooleanFilter());
        }
        return showUptimePercentage;
    }

    public void setShowUptimePercentage(BooleanFilter showUptimePercentage) {
        this.showUptimePercentage = showUptimePercentage;
    }

    public IntegerFilter getAutoRefreshSeconds() {
        return autoRefreshSeconds;
    }

    public Optional<IntegerFilter> optionalAutoRefreshSeconds() {
        return Optional.ofNullable(autoRefreshSeconds);
    }

    public IntegerFilter autoRefreshSeconds() {
        if (autoRefreshSeconds == null) {
            setAutoRefreshSeconds(new IntegerFilter());
        }
        return autoRefreshSeconds;
    }

    public void setAutoRefreshSeconds(IntegerFilter autoRefreshSeconds) {
        this.autoRefreshSeconds = autoRefreshSeconds;
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

    public BooleanFilter getIsHomePage() {
        return isHomePage;
    }

    public Optional<BooleanFilter> optionalIsHomePage() {
        return Optional.ofNullable(isHomePage);
    }

    public BooleanFilter isHomePage() {
        if (isHomePage == null) {
            setIsHomePage(new BooleanFilter());
        }
        return isHomePage;
    }

    public void setIsHomePage(BooleanFilter isHomePage) {
        this.isHomePage = isHomePage;
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

    public LongFilter getItemId() {
        return itemId;
    }

    public Optional<LongFilter> optionalItemId() {
        return Optional.ofNullable(itemId);
    }

    public LongFilter itemId() {
        if (itemId == null) {
            setItemId(new LongFilter());
        }
        return itemId;
    }

    public void setItemId(LongFilter itemId) {
        this.itemId = itemId;
    }

    public LongFilter getStatusDependencyId() {
        return statusDependencyId;
    }

    public Optional<LongFilter> optionalStatusDependencyId() {
        return Optional.ofNullable(statusDependencyId);
    }

    public LongFilter statusDependencyId() {
        if (statusDependencyId == null) {
            setStatusDependencyId(new LongFilter());
        }
        return statusDependencyId;
    }

    public void setStatusDependencyId(LongFilter statusDependencyId) {
        this.statusDependencyId = statusDependencyId;
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
        final StatusPageCriteria that = (StatusPageCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(slug, that.slug) &&
            Objects.equals(description, that.description) &&
            Objects.equals(isPublic, that.isPublic) &&
            Objects.equals(customDomain, that.customDomain) &&
            Objects.equals(logoUrl, that.logoUrl) &&
            Objects.equals(themeColor, that.themeColor) &&
            Objects.equals(headerText, that.headerText) &&
            Objects.equals(footerText, that.footerText) &&
            Objects.equals(showResponseTimes, that.showResponseTimes) &&
            Objects.equals(showUptimePercentage, that.showUptimePercentage) &&
            Objects.equals(autoRefreshSeconds, that.autoRefreshSeconds) &&
            Objects.equals(isActive, that.isActive) &&
            Objects.equals(isHomePage, that.isHomePage) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(itemId, that.itemId) &&
            Objects.equals(statusDependencyId, that.statusDependencyId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            name,
            slug,
            description,
            isPublic,
            customDomain,
            logoUrl,
            themeColor,
            headerText,
            footerText,
            showResponseTimes,
            showUptimePercentage,
            autoRefreshSeconds,
            isActive,
            isHomePage,
            createdAt,
            updatedAt,
            itemId,
            statusDependencyId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StatusPageCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalSlug().map(f -> "slug=" + f + ", ").orElse("") +
            optionalDescription().map(f -> "description=" + f + ", ").orElse("") +
            optionalIsPublic().map(f -> "isPublic=" + f + ", ").orElse("") +
            optionalCustomDomain().map(f -> "customDomain=" + f + ", ").orElse("") +
            optionalLogoUrl().map(f -> "logoUrl=" + f + ", ").orElse("") +
            optionalThemeColor().map(f -> "themeColor=" + f + ", ").orElse("") +
            optionalHeaderText().map(f -> "headerText=" + f + ", ").orElse("") +
            optionalFooterText().map(f -> "footerText=" + f + ", ").orElse("") +
            optionalShowResponseTimes().map(f -> "showResponseTimes=" + f + ", ").orElse("") +
            optionalShowUptimePercentage().map(f -> "showUptimePercentage=" + f + ", ").orElse("") +
            optionalAutoRefreshSeconds().map(f -> "autoRefreshSeconds=" + f + ", ").orElse("") +
            optionalIsActive().map(f -> "isActive=" + f + ", ").orElse("") +
            optionalIsHomePage().map(f -> "isHomePage=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalItemId().map(f -> "itemId=" + f + ", ").orElse("") +
            optionalStatusDependencyId().map(f -> "statusDependencyId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
