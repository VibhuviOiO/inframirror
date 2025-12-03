package vibhuvi.oio.inframirror.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TagCriteriaTest {

    @Test
    void newTagCriteriaHasAllFiltersNullTest() {
        var tagCriteria = new TagCriteria();
        assertThat(tagCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void tagCriteriaFluentMethodsCreatesFiltersTest() {
        var tagCriteria = new TagCriteria();

        setAllFilters(tagCriteria);

        assertThat(tagCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void tagCriteriaCopyCreatesNullFilterTest() {
        var tagCriteria = new TagCriteria();
        var copy = tagCriteria.copy();

        assertThat(tagCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(tagCriteria)
        );
    }

    @Test
    void tagCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var tagCriteria = new TagCriteria();
        setAllFilters(tagCriteria);

        var copy = tagCriteria.copy();

        assertThat(tagCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(tagCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var tagCriteria = new TagCriteria();

        assertThat(tagCriteria).hasToString("TagCriteria{}");
    }

    private static void setAllFilters(TagCriteria tagCriteria) {
        tagCriteria.id();
        tagCriteria.key();
        tagCriteria.value();
        tagCriteria.entityType();
        tagCriteria.entityId();
        tagCriteria.createdBy();
        tagCriteria.createdDate();
        tagCriteria.distinct();
    }

    private static Condition<TagCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getKey()) &&
                condition.apply(criteria.getValue()) &&
                condition.apply(criteria.getEntityType()) &&
                condition.apply(criteria.getEntityId()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TagCriteria> copyFiltersAre(TagCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getKey(), copy.getKey()) &&
                condition.apply(criteria.getValue(), copy.getValue()) &&
                condition.apply(criteria.getEntityType(), copy.getEntityType()) &&
                condition.apply(criteria.getEntityId(), copy.getEntityId()) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate(), copy.getCreatedDate()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
