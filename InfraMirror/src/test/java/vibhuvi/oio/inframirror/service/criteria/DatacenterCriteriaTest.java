package vibhuvi.oio.inframirror.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class DatacenterCriteriaTest {

    @Test
    void newDatacenterCriteriaHasAllFiltersNullTest() {
        var datacenterCriteria = new DatacenterCriteria();
        assertThat(datacenterCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void datacenterCriteriaFluentMethodsCreatesFiltersTest() {
        var datacenterCriteria = new DatacenterCriteria();

        setAllFilters(datacenterCriteria);

        assertThat(datacenterCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void datacenterCriteriaCopyCreatesNullFilterTest() {
        var datacenterCriteria = new DatacenterCriteria();
        var copy = datacenterCriteria.copy();

        assertThat(datacenterCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(datacenterCriteria)
        );
    }

    @Test
    void datacenterCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var datacenterCriteria = new DatacenterCriteria();
        setAllFilters(datacenterCriteria);

        var copy = datacenterCriteria.copy();

        assertThat(datacenterCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(datacenterCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var datacenterCriteria = new DatacenterCriteria();

        assertThat(datacenterCriteria).hasToString("DatacenterCriteria{}");
    }

    private static void setAllFilters(DatacenterCriteria datacenterCriteria) {
        datacenterCriteria.id();
        datacenterCriteria.code();
        datacenterCriteria.name();
        datacenterCriteria.instanceId();
        datacenterCriteria.serviceId();
        datacenterCriteria.regionId();
        datacenterCriteria.distinct();
    }

    private static Condition<DatacenterCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getInstanceId()) &&
                condition.apply(criteria.getServiceId()) &&
                condition.apply(criteria.getRegionId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<DatacenterCriteria> copyFiltersAre(DatacenterCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getInstanceId(), copy.getInstanceId()) &&
                condition.apply(criteria.getServiceId(), copy.getServiceId()) &&
                condition.apply(criteria.getRegionId(), copy.getRegionId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
