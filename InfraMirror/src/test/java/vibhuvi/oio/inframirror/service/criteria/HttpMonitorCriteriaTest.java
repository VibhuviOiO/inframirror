package vibhuvi.oio.inframirror.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class HttpMonitorCriteriaTest {

    @Test
    void newHttpMonitorCriteriaHasAllFiltersNullTest() {
        var httpMonitorCriteria = new HttpMonitorCriteria();
        assertThat(httpMonitorCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void httpMonitorCriteriaFluentMethodsCreatesFiltersTest() {
        var httpMonitorCriteria = new HttpMonitorCriteria();

        setAllFilters(httpMonitorCriteria);

        assertThat(httpMonitorCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void httpMonitorCriteriaCopyCreatesNullFilterTest() {
        var httpMonitorCriteria = new HttpMonitorCriteria();
        var copy = httpMonitorCriteria.copy();

        assertThat(httpMonitorCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(httpMonitorCriteria)
        );
    }

    @Test
    void httpMonitorCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var httpMonitorCriteria = new HttpMonitorCriteria();
        setAllFilters(httpMonitorCriteria);

        var copy = httpMonitorCriteria.copy();

        assertThat(httpMonitorCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(httpMonitorCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var httpMonitorCriteria = new HttpMonitorCriteria();

        assertThat(httpMonitorCriteria).hasToString("HttpMonitorCriteria{}");
    }

    private static void setAllFilters(HttpMonitorCriteria httpMonitorCriteria) {
        httpMonitorCriteria.id();
        httpMonitorCriteria.name();
        httpMonitorCriteria.method();
        httpMonitorCriteria.type();
        httpMonitorCriteria.heartbeatsId();
        httpMonitorCriteria.scheduleId();
        httpMonitorCriteria.distinct();
    }

    private static Condition<HttpMonitorCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getMethod()) &&
                condition.apply(criteria.getType()) &&
                condition.apply(criteria.getHeartbeatsId()) &&
                condition.apply(criteria.getScheduleId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<HttpMonitorCriteria> copyFiltersAre(HttpMonitorCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getMethod(), copy.getMethod()) &&
                condition.apply(criteria.getType(), copy.getType()) &&
                condition.apply(criteria.getHeartbeatsId(), copy.getHeartbeatsId()) &&
                condition.apply(criteria.getScheduleId(), copy.getScheduleId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
