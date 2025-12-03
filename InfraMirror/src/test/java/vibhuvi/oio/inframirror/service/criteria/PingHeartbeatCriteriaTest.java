package vibhuvi.oio.inframirror.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class PingHeartbeatCriteriaTest {

    @Test
    void newPingHeartbeatCriteriaHasAllFiltersNullTest() {
        var pingHeartbeatCriteria = new PingHeartbeatCriteria();
        assertThat(pingHeartbeatCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void pingHeartbeatCriteriaFluentMethodsCreatesFiltersTest() {
        var pingHeartbeatCriteria = new PingHeartbeatCriteria();

        setAllFilters(pingHeartbeatCriteria);

        assertThat(pingHeartbeatCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void pingHeartbeatCriteriaCopyCreatesNullFilterTest() {
        var pingHeartbeatCriteria = new PingHeartbeatCriteria();
        var copy = pingHeartbeatCriteria.copy();

        assertThat(pingHeartbeatCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(pingHeartbeatCriteria)
        );
    }

    @Test
    void pingHeartbeatCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var pingHeartbeatCriteria = new PingHeartbeatCriteria();
        setAllFilters(pingHeartbeatCriteria);

        var copy = pingHeartbeatCriteria.copy();

        assertThat(pingHeartbeatCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(pingHeartbeatCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var pingHeartbeatCriteria = new PingHeartbeatCriteria();

        assertThat(pingHeartbeatCriteria).hasToString("PingHeartbeatCriteria{}");
    }

    private static void setAllFilters(PingHeartbeatCriteria pingHeartbeatCriteria) {
        pingHeartbeatCriteria.id();
        pingHeartbeatCriteria.executedAt();
        pingHeartbeatCriteria.heartbeatType();
        pingHeartbeatCriteria.success();
        pingHeartbeatCriteria.responseTimeMs();
        pingHeartbeatCriteria.packetLoss();
        pingHeartbeatCriteria.jitterMs();
        pingHeartbeatCriteria.cpuUsage();
        pingHeartbeatCriteria.memoryUsage();
        pingHeartbeatCriteria.diskUsage();
        pingHeartbeatCriteria.loadAverage();
        pingHeartbeatCriteria.processCount();
        pingHeartbeatCriteria.networkRxBytes();
        pingHeartbeatCriteria.networkTxBytes();
        pingHeartbeatCriteria.uptimeSeconds();
        pingHeartbeatCriteria.status();
        pingHeartbeatCriteria.errorType();
        pingHeartbeatCriteria.instanceId();
        pingHeartbeatCriteria.agentId();
        pingHeartbeatCriteria.distinct();
    }

    private static Condition<PingHeartbeatCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getExecutedAt()) &&
                condition.apply(criteria.getHeartbeatType()) &&
                condition.apply(criteria.getSuccess()) &&
                condition.apply(criteria.getResponseTimeMs()) &&
                condition.apply(criteria.getPacketLoss()) &&
                condition.apply(criteria.getJitterMs()) &&
                condition.apply(criteria.getCpuUsage()) &&
                condition.apply(criteria.getMemoryUsage()) &&
                condition.apply(criteria.getDiskUsage()) &&
                condition.apply(criteria.getLoadAverage()) &&
                condition.apply(criteria.getProcessCount()) &&
                condition.apply(criteria.getNetworkRxBytes()) &&
                condition.apply(criteria.getNetworkTxBytes()) &&
                condition.apply(criteria.getUptimeSeconds()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getErrorType()) &&
                condition.apply(criteria.getInstanceId()) &&
                condition.apply(criteria.getAgentId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<PingHeartbeatCriteria> copyFiltersAre(
        PingHeartbeatCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getExecutedAt(), copy.getExecutedAt()) &&
                condition.apply(criteria.getHeartbeatType(), copy.getHeartbeatType()) &&
                condition.apply(criteria.getSuccess(), copy.getSuccess()) &&
                condition.apply(criteria.getResponseTimeMs(), copy.getResponseTimeMs()) &&
                condition.apply(criteria.getPacketLoss(), copy.getPacketLoss()) &&
                condition.apply(criteria.getJitterMs(), copy.getJitterMs()) &&
                condition.apply(criteria.getCpuUsage(), copy.getCpuUsage()) &&
                condition.apply(criteria.getMemoryUsage(), copy.getMemoryUsage()) &&
                condition.apply(criteria.getDiskUsage(), copy.getDiskUsage()) &&
                condition.apply(criteria.getLoadAverage(), copy.getLoadAverage()) &&
                condition.apply(criteria.getProcessCount(), copy.getProcessCount()) &&
                condition.apply(criteria.getNetworkRxBytes(), copy.getNetworkRxBytes()) &&
                condition.apply(criteria.getNetworkTxBytes(), copy.getNetworkTxBytes()) &&
                condition.apply(criteria.getUptimeSeconds(), copy.getUptimeSeconds()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getErrorType(), copy.getErrorType()) &&
                condition.apply(criteria.getInstanceId(), copy.getInstanceId()) &&
                condition.apply(criteria.getAgentId(), copy.getAgentId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
