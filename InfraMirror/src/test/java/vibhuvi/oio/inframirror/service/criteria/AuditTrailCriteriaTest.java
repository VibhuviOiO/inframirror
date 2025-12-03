package vibhuvi.oio.inframirror.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class AuditTrailCriteriaTest {

    @Test
    void newAuditTrailCriteriaHasAllFiltersNullTest() {
        var auditTrailCriteria = new AuditTrailCriteria();
        assertThat(auditTrailCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void auditTrailCriteriaFluentMethodsCreatesFiltersTest() {
        var auditTrailCriteria = new AuditTrailCriteria();

        setAllFilters(auditTrailCriteria);

        assertThat(auditTrailCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void auditTrailCriteriaCopyCreatesNullFilterTest() {
        var auditTrailCriteria = new AuditTrailCriteria();
        var copy = auditTrailCriteria.copy();

        assertThat(auditTrailCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(auditTrailCriteria)
        );
    }

    @Test
    void auditTrailCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var auditTrailCriteria = new AuditTrailCriteria();
        setAllFilters(auditTrailCriteria);

        var copy = auditTrailCriteria.copy();

        assertThat(auditTrailCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(auditTrailCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var auditTrailCriteria = new AuditTrailCriteria();

        assertThat(auditTrailCriteria).hasToString("AuditTrailCriteria{}");
    }

    private static void setAllFilters(AuditTrailCriteria auditTrailCriteria) {
        auditTrailCriteria.id();
        auditTrailCriteria.action();
        auditTrailCriteria.entityName();
        auditTrailCriteria.entityId();
        auditTrailCriteria.timestamp();
        auditTrailCriteria.ipAddress();
        auditTrailCriteria.userId();
        auditTrailCriteria.distinct();
    }

    private static Condition<AuditTrailCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getAction()) &&
                condition.apply(criteria.getEntityName()) &&
                condition.apply(criteria.getEntityId()) &&
                condition.apply(criteria.getTimestamp()) &&
                condition.apply(criteria.getIpAddress()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<AuditTrailCriteria> copyFiltersAre(AuditTrailCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getAction(), copy.getAction()) &&
                condition.apply(criteria.getEntityName(), copy.getEntityName()) &&
                condition.apply(criteria.getEntityId(), copy.getEntityId()) &&
                condition.apply(criteria.getTimestamp(), copy.getTimestamp()) &&
                condition.apply(criteria.getIpAddress(), copy.getIpAddress()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
