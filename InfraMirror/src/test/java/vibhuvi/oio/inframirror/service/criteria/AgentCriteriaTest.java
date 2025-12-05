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
        agentCriteria.instanceId();
        agentCriteria.httpHeartbeatId();
        agentCriteria.instanceHeartbeatId();
        agentCriteria.serviceHeartbeatId();
        agentCriteria.regionId();
        agentCriteria.distinct();
    }

    private static Condition<AgentCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getInstanceId()) &&
                condition.apply(criteria.getHttpHeartbeatId()) &&
                condition.apply(criteria.getInstanceHeartbeatId()) &&
                condition.apply(criteria.getServiceHeartbeatId()) &&
                condition.apply(criteria.getRegionId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<AgentCriteria> copyFiltersAre(AgentCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getInstanceId(), copy.getInstanceId()) &&
                condition.apply(criteria.getHttpHeartbeatId(), copy.getHttpHeartbeatId()) &&
                condition.apply(criteria.getInstanceHeartbeatId(), copy.getInstanceHeartbeatId()) &&
                condition.apply(criteria.getServiceHeartbeatId(), copy.getServiceHeartbeatId()) &&
                condition.apply(criteria.getRegionId(), copy.getRegionId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
