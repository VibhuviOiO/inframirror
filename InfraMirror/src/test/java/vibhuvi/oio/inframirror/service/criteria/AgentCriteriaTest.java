package vibhuvi.oio.inframirror.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class AgentCriteriaTest {

    @Test
    void newAgentCriteriaHasAllFiltersNullTest() {
        var agentCriteria = new AgentCriteria();
        assertThat(agentCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void agentCriteriaFluentMethodsCreatesFiltersTest() {
        var agentCriteria = new AgentCriteria();

        setAllFilters(agentCriteria);

        assertThat(agentCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void agentCriteriaCopyCreatesNullFilterTest() {
        var agentCriteria = new AgentCriteria();
        var copy = agentCriteria.copy();

        assertThat(agentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(agentCriteria)
        );
    }

    @Test
    void agentCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var agentCriteria = new AgentCriteria();
        setAllFilters(agentCriteria);

        var copy = agentCriteria.copy();

        assertThat(agentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(agentCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var agentCriteria = new AgentCriteria();

        assertThat(agentCriteria).hasToString("AgentCriteria{}");
    }

    private static void setAllFilters(AgentCriteria agentCriteria) {
        agentCriteria.id();
        agentCriteria.name();
        agentCriteria.instancesId();
        agentCriteria.httpHeartbeatsId();
        agentCriteria.pingHeartbeatsId();
        agentCriteria.datacenterId();
        agentCriteria.distinct();
    }

    private static Condition<AgentCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getInstancesId()) &&
                condition.apply(criteria.getHttpHeartbeatsId()) &&
                condition.apply(criteria.getPingHeartbeatsId()) &&
                condition.apply(criteria.getDatacenterId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<AgentCriteria> copyFiltersAre(AgentCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getInstancesId(), copy.getInstancesId()) &&
                condition.apply(criteria.getHttpHeartbeatsId(), copy.getHttpHeartbeatsId()) &&
                condition.apply(criteria.getPingHeartbeatsId(), copy.getPingHeartbeatsId()) &&
                condition.apply(criteria.getDatacenterId(), copy.getDatacenterId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
