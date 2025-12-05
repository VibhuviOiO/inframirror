package vibhuvi.oio.inframirror.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class InstanceCriteriaTest {

    @Test
    void newInstanceCriteriaHasAllFiltersNullTest() {
        var instanceCriteria = new InstanceCriteria();
        assertThat(instanceCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void instanceCriteriaFluentMethodsCreatesFiltersTest() {
        var instanceCriteria = new InstanceCriteria();

        setAllFilters(instanceCriteria);

        assertThat(instanceCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void instanceCriteriaCopyCreatesNullFilterTest() {
        var instanceCriteria = new InstanceCriteria();
        var copy = instanceCriteria.copy();

        assertThat(instanceCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(instanceCriteria)
        );
    }

    @Test
    void instanceCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var instanceCriteria = new InstanceCriteria();
        setAllFilters(instanceCriteria);

        var copy = instanceCriteria.copy();

        assertThat(instanceCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(instanceCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var instanceCriteria = new InstanceCriteria();

        assertThat(instanceCriteria).hasToString("InstanceCriteria{}");
    }

    private static void setAllFilters(InstanceCriteria instanceCriteria) {
        instanceCriteria.id();
        instanceCriteria.name();
        instanceCriteria.hostname();
        instanceCriteria.description();
        instanceCriteria.instanceType();
        instanceCriteria.monitoringType();
        instanceCriteria.operatingSystem();
        instanceCriteria.platform();
        instanceCriteria.privateIpAddress();
        instanceCriteria.publicIpAddress();
        instanceCriteria.pingEnabled();
        instanceCriteria.pingInterval();
        instanceCriteria.pingTimeoutMs();
        instanceCriteria.pingRetryCount();
        instanceCriteria.hardwareMonitoringEnabled();
        instanceCriteria.hardwareMonitoringInterval();
        instanceCriteria.cpuWarningThreshold();
        instanceCriteria.cpuDangerThreshold();
        instanceCriteria.memoryWarningThreshold();
        instanceCriteria.memoryDangerThreshold();
        instanceCriteria.diskWarningThreshold();
        instanceCriteria.diskDangerThreshold();
        instanceCriteria.createdAt();
        instanceCriteria.updatedAt();
        instanceCriteria.lastPingAt();
        instanceCriteria.lastHardwareCheckAt();
        instanceCriteria.heartbeatId();
        instanceCriteria.serviceInstanceId();
        instanceCriteria.datacenterId();
        instanceCriteria.agentId();
        instanceCriteria.distinct();
    }

    private static Condition<InstanceCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getHostname()) &&
                condition.apply(criteria.getDescription()) &&
                condition.apply(criteria.getInstanceType()) &&
                condition.apply(criteria.getMonitoringType()) &&
                condition.apply(criteria.getOperatingSystem()) &&
                condition.apply(criteria.getPlatform()) &&
                condition.apply(criteria.getPrivateIpAddress()) &&
                condition.apply(criteria.getPublicIpAddress()) &&
                condition.apply(criteria.getPingEnabled()) &&
                condition.apply(criteria.getPingInterval()) &&
                condition.apply(criteria.getPingTimeoutMs()) &&
                condition.apply(criteria.getPingRetryCount()) &&
                condition.apply(criteria.getHardwareMonitoringEnabled()) &&
                condition.apply(criteria.getHardwareMonitoringInterval()) &&
                condition.apply(criteria.getCpuWarningThreshold()) &&
                condition.apply(criteria.getCpuDangerThreshold()) &&
                condition.apply(criteria.getMemoryWarningThreshold()) &&
                condition.apply(criteria.getMemoryDangerThreshold()) &&
                condition.apply(criteria.getDiskWarningThreshold()) &&
                condition.apply(criteria.getDiskDangerThreshold()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getLastPingAt()) &&
                condition.apply(criteria.getLastHardwareCheckAt()) &&
                condition.apply(criteria.getHeartbeatId()) &&
                condition.apply(criteria.getServiceInstanceId()) &&
                condition.apply(criteria.getDatacenterId()) &&
                condition.apply(criteria.getAgentId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<InstanceCriteria> copyFiltersAre(InstanceCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getHostname(), copy.getHostname()) &&
                condition.apply(criteria.getDescription(), copy.getDescription()) &&
                condition.apply(criteria.getInstanceType(), copy.getInstanceType()) &&
                condition.apply(criteria.getMonitoringType(), copy.getMonitoringType()) &&
                condition.apply(criteria.getOperatingSystem(), copy.getOperatingSystem()) &&
                condition.apply(criteria.getPlatform(), copy.getPlatform()) &&
                condition.apply(criteria.getPrivateIpAddress(), copy.getPrivateIpAddress()) &&
                condition.apply(criteria.getPublicIpAddress(), copy.getPublicIpAddress()) &&
                condition.apply(criteria.getPingEnabled(), copy.getPingEnabled()) &&
                condition.apply(criteria.getPingInterval(), copy.getPingInterval()) &&
                condition.apply(criteria.getPingTimeoutMs(), copy.getPingTimeoutMs()) &&
                condition.apply(criteria.getPingRetryCount(), copy.getPingRetryCount()) &&
                condition.apply(criteria.getHardwareMonitoringEnabled(), copy.getHardwareMonitoringEnabled()) &&
                condition.apply(criteria.getHardwareMonitoringInterval(), copy.getHardwareMonitoringInterval()) &&
                condition.apply(criteria.getCpuWarningThreshold(), copy.getCpuWarningThreshold()) &&
                condition.apply(criteria.getCpuDangerThreshold(), copy.getCpuDangerThreshold()) &&
                condition.apply(criteria.getMemoryWarningThreshold(), copy.getMemoryWarningThreshold()) &&
                condition.apply(criteria.getMemoryDangerThreshold(), copy.getMemoryDangerThreshold()) &&
                condition.apply(criteria.getDiskWarningThreshold(), copy.getDiskWarningThreshold()) &&
                condition.apply(criteria.getDiskDangerThreshold(), copy.getDiskDangerThreshold()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getLastPingAt(), copy.getLastPingAt()) &&
                condition.apply(criteria.getLastHardwareCheckAt(), copy.getLastHardwareCheckAt()) &&
                condition.apply(criteria.getHeartbeatId(), copy.getHeartbeatId()) &&
                condition.apply(criteria.getServiceInstanceId(), copy.getServiceInstanceId()) &&
                condition.apply(criteria.getDatacenterId(), copy.getDatacenterId()) &&
                condition.apply(criteria.getAgentId(), copy.getAgentId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
