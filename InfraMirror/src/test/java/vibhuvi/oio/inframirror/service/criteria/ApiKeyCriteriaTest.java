package vibhuvi.oio.inframirror.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ApiKeyCriteriaTest {

    @Test
    void newApiKeyCriteriaHasAllFiltersNullTest() {
        var apiKeyCriteria = new ApiKeyCriteria();
        assertThat(apiKeyCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void apiKeyCriteriaFluentMethodsCreatesFiltersTest() {
        var apiKeyCriteria = new ApiKeyCriteria();

        setAllFilters(apiKeyCriteria);

        assertThat(apiKeyCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void apiKeyCriteriaCopyCreatesNullFilterTest() {
        var apiKeyCriteria = new ApiKeyCriteria();
        var copy = apiKeyCriteria.copy();

        assertThat(apiKeyCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(apiKeyCriteria)
        );
    }

    @Test
    void apiKeyCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var apiKeyCriteria = new ApiKeyCriteria();
        setAllFilters(apiKeyCriteria);

        var copy = apiKeyCriteria.copy();

        assertThat(apiKeyCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(apiKeyCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var apiKeyCriteria = new ApiKeyCriteria();

        assertThat(apiKeyCriteria).hasToString("ApiKeyCriteria{}");
    }

    private static void setAllFilters(ApiKeyCriteria apiKeyCriteria) {
        apiKeyCriteria.id();
        apiKeyCriteria.name();
        apiKeyCriteria.description();
        apiKeyCriteria.keyHash();
        apiKeyCriteria.active();
        apiKeyCriteria.lastUsedDate();
        apiKeyCriteria.expiresAt();
        apiKeyCriteria.createdBy();
        apiKeyCriteria.createdDate();
        apiKeyCriteria.lastModifiedBy();
        apiKeyCriteria.lastModifiedDate();
        apiKeyCriteria.distinct();
    }

    private static Condition<ApiKeyCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getDescription()) &&
                condition.apply(criteria.getKeyHash()) &&
                condition.apply(criteria.getActive()) &&
                condition.apply(criteria.getLastUsedDate()) &&
                condition.apply(criteria.getExpiresAt()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ApiKeyCriteria> copyFiltersAre(ApiKeyCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getDescription(), copy.getDescription()) &&
                condition.apply(criteria.getKeyHash(), copy.getKeyHash()) &&
                condition.apply(criteria.getActive(), copy.getActive()) &&
                condition.apply(criteria.getLastUsedDate(), copy.getLastUsedDate()) &&
                condition.apply(criteria.getExpiresAt(), copy.getExpiresAt()) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate(), copy.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy(), copy.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate(), copy.getLastModifiedDate()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
