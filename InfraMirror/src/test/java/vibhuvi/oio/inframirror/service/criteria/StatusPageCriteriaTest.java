package vibhuvi.oio.inframirror.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class StatusPageCriteriaTest {

    @Test
    void newStatusPageCriteriaHasAllFiltersNullTest() {
        var statusPageCriteria = new StatusPageCriteria();
        assertThat(statusPageCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void statusPageCriteriaFluentMethodsCreatesFiltersTest() {
        var statusPageCriteria = new StatusPageCriteria();

        setAllFilters(statusPageCriteria);

        assertThat(statusPageCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void statusPageCriteriaCopyCreatesNullFilterTest() {
        var statusPageCriteria = new StatusPageCriteria();
        var copy = statusPageCriteria.copy();

        assertThat(statusPageCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(statusPageCriteria)
        );
    }

    @Test
    void statusPageCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var statusPageCriteria = new StatusPageCriteria();
        setAllFilters(statusPageCriteria);

        var copy = statusPageCriteria.copy();

        assertThat(statusPageCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(statusPageCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var statusPageCriteria = new StatusPageCriteria();

        assertThat(statusPageCriteria).hasToString("StatusPageCriteria{}");
    }

    private static void setAllFilters(StatusPageCriteria statusPageCriteria) {
        statusPageCriteria.id();
        statusPageCriteria.name();
        statusPageCriteria.slug();
        statusPageCriteria.description();
        statusPageCriteria.isPublic();
        statusPageCriteria.customDomain();
        statusPageCriteria.logoUrl();
        statusPageCriteria.themeColor();
        statusPageCriteria.headerText();
        statusPageCriteria.footerText();
        statusPageCriteria.showResponseTimes();
        statusPageCriteria.showUptimePercentage();
        statusPageCriteria.autoRefreshSeconds();
        statusPageCriteria.isActive();
        statusPageCriteria.isHomePage();
        statusPageCriteria.createdAt();
        statusPageCriteria.updatedAt();
        statusPageCriteria.itemId();
        statusPageCriteria.statusDependencyId();
        statusPageCriteria.distinct();
    }

    private static Condition<StatusPageCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getSlug()) &&
                condition.apply(criteria.getDescription()) &&
                condition.apply(criteria.getIsPublic()) &&
                condition.apply(criteria.getCustomDomain()) &&
                condition.apply(criteria.getLogoUrl()) &&
                condition.apply(criteria.getThemeColor()) &&
                condition.apply(criteria.getHeaderText()) &&
                condition.apply(criteria.getFooterText()) &&
                condition.apply(criteria.getShowResponseTimes()) &&
                condition.apply(criteria.getShowUptimePercentage()) &&
                condition.apply(criteria.getAutoRefreshSeconds()) &&
                condition.apply(criteria.getIsActive()) &&
                condition.apply(criteria.getIsHomePage()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getItemId()) &&
                condition.apply(criteria.getStatusDependencyId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<StatusPageCriteria> copyFiltersAre(StatusPageCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getSlug(), copy.getSlug()) &&
                condition.apply(criteria.getDescription(), copy.getDescription()) &&
                condition.apply(criteria.getIsPublic(), copy.getIsPublic()) &&
                condition.apply(criteria.getCustomDomain(), copy.getCustomDomain()) &&
                condition.apply(criteria.getLogoUrl(), copy.getLogoUrl()) &&
                condition.apply(criteria.getThemeColor(), copy.getThemeColor()) &&
                condition.apply(criteria.getHeaderText(), copy.getHeaderText()) &&
                condition.apply(criteria.getFooterText(), copy.getFooterText()) &&
                condition.apply(criteria.getShowResponseTimes(), copy.getShowResponseTimes()) &&
                condition.apply(criteria.getShowUptimePercentage(), copy.getShowUptimePercentage()) &&
                condition.apply(criteria.getAutoRefreshSeconds(), copy.getAutoRefreshSeconds()) &&
                condition.apply(criteria.getIsActive(), copy.getIsActive()) &&
                condition.apply(criteria.getIsHomePage(), copy.getIsHomePage()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getItemId(), copy.getItemId()) &&
                condition.apply(criteria.getStatusDependencyId(), copy.getStatusDependencyId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
