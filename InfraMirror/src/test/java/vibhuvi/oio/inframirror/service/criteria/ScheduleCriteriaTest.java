package vibhuvi.oio.inframirror.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ScheduleCriteriaTest {

    @Test
    void newScheduleCriteriaHasAllFiltersNullTest() {
        var scheduleCriteria = new ScheduleCriteria();
        assertThat(scheduleCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void scheduleCriteriaFluentMethodsCreatesFiltersTest() {
        var scheduleCriteria = new ScheduleCriteria();

        setAllFilters(scheduleCriteria);

        assertThat(scheduleCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void scheduleCriteriaCopyCreatesNullFilterTest() {
        var scheduleCriteria = new ScheduleCriteria();
        var copy = scheduleCriteria.copy();

        assertThat(scheduleCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(scheduleCriteria)
        );
    }

    @Test
    void scheduleCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var scheduleCriteria = new ScheduleCriteria();
        setAllFilters(scheduleCriteria);

        var copy = scheduleCriteria.copy();

        assertThat(scheduleCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(scheduleCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var scheduleCriteria = new ScheduleCriteria();

        assertThat(scheduleCriteria).hasToString("ScheduleCriteria{}");
    }

    private static void setAllFilters(ScheduleCriteria scheduleCriteria) {
        scheduleCriteria.id();
        scheduleCriteria.name();
        scheduleCriteria.interval();
        scheduleCriteria.includeResponseBody();
        scheduleCriteria.thresholdsWarning();
        scheduleCriteria.thresholdsCritical();
        scheduleCriteria.monitorsId();
        scheduleCriteria.distinct();
    }

    private static Condition<ScheduleCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getInterval()) &&
                condition.apply(criteria.getIncludeResponseBody()) &&
                condition.apply(criteria.getThresholdsWarning()) &&
                condition.apply(criteria.getThresholdsCritical()) &&
                condition.apply(criteria.getMonitorsId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ScheduleCriteria> copyFiltersAre(ScheduleCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getInterval(), copy.getInterval()) &&
                condition.apply(criteria.getIncludeResponseBody(), copy.getIncludeResponseBody()) &&
                condition.apply(criteria.getThresholdsWarning(), copy.getThresholdsWarning()) &&
                condition.apply(criteria.getThresholdsCritical(), copy.getThresholdsCritical()) &&
                condition.apply(criteria.getMonitorsId(), copy.getMonitorsId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
