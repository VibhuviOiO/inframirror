package vibhuvi.oio.inframirror.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class SessionLogCriteriaTest {

    @Test
    void newSessionLogCriteriaHasAllFiltersNullTest() {
        var sessionLogCriteria = new SessionLogCriteria();
        assertThat(sessionLogCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void sessionLogCriteriaFluentMethodsCreatesFiltersTest() {
        var sessionLogCriteria = new SessionLogCriteria();

        setAllFilters(sessionLogCriteria);

        assertThat(sessionLogCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void sessionLogCriteriaCopyCreatesNullFilterTest() {
        var sessionLogCriteria = new SessionLogCriteria();
        var copy = sessionLogCriteria.copy();

        assertThat(sessionLogCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(sessionLogCriteria)
        );
    }

    @Test
    void sessionLogCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var sessionLogCriteria = new SessionLogCriteria();
        setAllFilters(sessionLogCriteria);

        var copy = sessionLogCriteria.copy();

        assertThat(sessionLogCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(sessionLogCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var sessionLogCriteria = new SessionLogCriteria();

        assertThat(sessionLogCriteria).hasToString("SessionLogCriteria{}");
    }

    private static void setAllFilters(SessionLogCriteria sessionLogCriteria) {
        sessionLogCriteria.id();
        sessionLogCriteria.sessionType();
        sessionLogCriteria.startTime();
        sessionLogCriteria.endTime();
        sessionLogCriteria.duration();
        sessionLogCriteria.sourceIpAddress();
        sessionLogCriteria.status();
        sessionLogCriteria.terminationReason();
        sessionLogCriteria.commandsExecuted();
        sessionLogCriteria.bytesTransferred();
        sessionLogCriteria.sessionId();
        sessionLogCriteria.instanceId();
        sessionLogCriteria.agentId();
        sessionLogCriteria.userId();
        sessionLogCriteria.distinct();
    }

    private static Condition<SessionLogCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getSessionType()) &&
                condition.apply(criteria.getStartTime()) &&
                condition.apply(criteria.getEndTime()) &&
                condition.apply(criteria.getDuration()) &&
                condition.apply(criteria.getSourceIpAddress()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getTerminationReason()) &&
                condition.apply(criteria.getCommandsExecuted()) &&
                condition.apply(criteria.getBytesTransferred()) &&
                condition.apply(criteria.getSessionId()) &&
                condition.apply(criteria.getInstanceId()) &&
                condition.apply(criteria.getAgentId()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<SessionLogCriteria> copyFiltersAre(SessionLogCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getSessionType(), copy.getSessionType()) &&
                condition.apply(criteria.getStartTime(), copy.getStartTime()) &&
                condition.apply(criteria.getEndTime(), copy.getEndTime()) &&
                condition.apply(criteria.getDuration(), copy.getDuration()) &&
                condition.apply(criteria.getSourceIpAddress(), copy.getSourceIpAddress()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getTerminationReason(), copy.getTerminationReason()) &&
                condition.apply(criteria.getCommandsExecuted(), copy.getCommandsExecuted()) &&
                condition.apply(criteria.getBytesTransferred(), copy.getBytesTransferred()) &&
                condition.apply(criteria.getSessionId(), copy.getSessionId()) &&
                condition.apply(criteria.getInstanceId(), copy.getInstanceId()) &&
                condition.apply(criteria.getAgentId(), copy.getAgentId()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
