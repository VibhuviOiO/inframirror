package vibhuvi.oio.inframirror.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class MonitoredServiceCriteriaTest {

    @Test
    void newMonitoredServiceCriteriaHasAllFiltersNullTest() {
        var monitoredServiceCriteria = new MonitoredServiceCriteria();
        assertThat(monitoredServiceCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void monitoredServiceCriteriaFluentMethodsCreatesFiltersTest() {
        var monitoredServiceCriteria = new MonitoredServiceCriteria();

        setAllFilters(monitoredServiceCriteria);

        assertThat(monitoredServiceCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void monitoredServiceCriteriaCopyCreatesNullFilterTest() {
        var monitoredServiceCriteria = new MonitoredServiceCriteria();
        var copy = monitoredServiceCriteria.copy();

        assertThat(monitoredServiceCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(monitoredServiceCriteria)
        );
    }

    @Test
    void monitoredServiceCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var monitoredServiceCriteria = new MonitoredServiceCriteria();
        setAllFilters(monitoredServiceCriteria);

        var copy = monitoredServiceCriteria.copy();

        assertThat(monitoredServiceCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(monitoredServiceCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var monitoredServiceCriteria = new MonitoredServiceCriteria();

        assertThat(monitoredServiceCriteria).hasToString("MonitoredServiceCriteria{}");
    }

    private static void setAllFilters(MonitoredServiceCriteria monitoredServiceCriteria) {
        monitoredServiceCriteria.id();
        monitoredServiceCriteria.name();
        monitoredServiceCriteria.description();
        monitoredServiceCriteria.serviceType();
        monitoredServiceCriteria.environment();
        monitoredServiceCriteria.monitoringEnabled();
        monitoredServiceCriteria.clusterMonitoringEnabled();
        monitoredServiceCriteria.intervalSeconds();
        monitoredServiceCriteria.timeoutMs();
        monitoredServiceCriteria.retryCount();
        monitoredServiceCriteria.latencyWarningMs();
        monitoredServiceCriteria.latencyCriticalMs();
        monitoredServiceCriteria.isActive();
        monitoredServiceCriteria.createdAt();
        monitoredServiceCriteria.updatedAt();
        monitoredServiceCriteria.serviceInstanceId();
        monitoredServiceCriteria.heartbeatId();
        monitoredServiceCriteria.datacenterId();
        monitoredServiceCriteria.distinct();
    }

    private static Condition<MonitoredServiceCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getDescription()) &&
                condition.apply(criteria.getServiceType()) &&
                condition.apply(criteria.getEnvironment()) &&
                condition.apply(criteria.getMonitoringEnabled()) &&
                condition.apply(criteria.getClusterMonitoringEnabled()) &&
                condition.apply(criteria.getIntervalSeconds()) &&
                condition.apply(criteria.getTimeoutMs()) &&
                condition.apply(criteria.getRetryCount()) &&
                condition.apply(criteria.getLatencyWarningMs()) &&
                condition.apply(criteria.getLatencyCriticalMs()) &&
                condition.apply(criteria.getIsActive()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getServiceInstanceId()) &&
                condition.apply(criteria.getHeartbeatId()) &&
                condition.apply(criteria.getDatacenterId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<MonitoredServiceCriteria> copyFiltersAre(
        MonitoredServiceCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getDescription(), copy.getDescription()) &&
                condition.apply(criteria.getServiceType(), copy.getServiceType()) &&
                condition.apply(criteria.getEnvironment(), copy.getEnvironment()) &&
                condition.apply(criteria.getMonitoringEnabled(), copy.getMonitoringEnabled()) &&
                condition.apply(criteria.getClusterMonitoringEnabled(), copy.getClusterMonitoringEnabled()) &&
                condition.apply(criteria.getIntervalSeconds(), copy.getIntervalSeconds()) &&
                condition.apply(criteria.getTimeoutMs(), copy.getTimeoutMs()) &&
                condition.apply(criteria.getRetryCount(), copy.getRetryCount()) &&
                condition.apply(criteria.getLatencyWarningMs(), copy.getLatencyWarningMs()) &&
                condition.apply(criteria.getLatencyCriticalMs(), copy.getLatencyCriticalMs()) &&
                condition.apply(criteria.getIsActive(), copy.getIsActive()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getServiceInstanceId(), copy.getServiceInstanceId()) &&
                condition.apply(criteria.getHeartbeatId(), copy.getHeartbeatId()) &&
                condition.apply(criteria.getDatacenterId(), copy.getDatacenterId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
