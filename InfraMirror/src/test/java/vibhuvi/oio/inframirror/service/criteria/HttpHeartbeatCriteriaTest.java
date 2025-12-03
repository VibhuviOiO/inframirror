package vibhuvi.oio.inframirror.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class HttpHeartbeatCriteriaTest {

    @Test
    void newHttpHeartbeatCriteriaHasAllFiltersNullTest() {
        var httpHeartbeatCriteria = new HttpHeartbeatCriteria();
        assertThat(httpHeartbeatCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void httpHeartbeatCriteriaFluentMethodsCreatesFiltersTest() {
        var httpHeartbeatCriteria = new HttpHeartbeatCriteria();

        setAllFilters(httpHeartbeatCriteria);

        assertThat(httpHeartbeatCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void httpHeartbeatCriteriaCopyCreatesNullFilterTest() {
        var httpHeartbeatCriteria = new HttpHeartbeatCriteria();
        var copy = httpHeartbeatCriteria.copy();

        assertThat(httpHeartbeatCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(httpHeartbeatCriteria)
        );
    }

    @Test
    void httpHeartbeatCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var httpHeartbeatCriteria = new HttpHeartbeatCriteria();
        setAllFilters(httpHeartbeatCriteria);

        var copy = httpHeartbeatCriteria.copy();

        assertThat(httpHeartbeatCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(httpHeartbeatCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var httpHeartbeatCriteria = new HttpHeartbeatCriteria();

        assertThat(httpHeartbeatCriteria).hasToString("HttpHeartbeatCriteria{}");
    }

    private static void setAllFilters(HttpHeartbeatCriteria httpHeartbeatCriteria) {
        httpHeartbeatCriteria.id();
        httpHeartbeatCriteria.executedAt();
        httpHeartbeatCriteria.success();
        httpHeartbeatCriteria.responseTimeMs();
        httpHeartbeatCriteria.responseSizeBytes();
        httpHeartbeatCriteria.responseStatusCode();
        httpHeartbeatCriteria.responseContentType();
        httpHeartbeatCriteria.responseServer();
        httpHeartbeatCriteria.responseCacheStatus();
        httpHeartbeatCriteria.dnsLookupMs();
        httpHeartbeatCriteria.tcpConnectMs();
        httpHeartbeatCriteria.tlsHandshakeMs();
        httpHeartbeatCriteria.timeToFirstByteMs();
        httpHeartbeatCriteria.warningThresholdMs();
        httpHeartbeatCriteria.criticalThresholdMs();
        httpHeartbeatCriteria.errorType();
        httpHeartbeatCriteria.monitorId();
        httpHeartbeatCriteria.agentId();
        httpHeartbeatCriteria.distinct();
    }

    private static Condition<HttpHeartbeatCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getExecutedAt()) &&
                condition.apply(criteria.getSuccess()) &&
                condition.apply(criteria.getResponseTimeMs()) &&
                condition.apply(criteria.getResponseSizeBytes()) &&
                condition.apply(criteria.getResponseStatusCode()) &&
                condition.apply(criteria.getResponseContentType()) &&
                condition.apply(criteria.getResponseServer()) &&
                condition.apply(criteria.getResponseCacheStatus()) &&
                condition.apply(criteria.getDnsLookupMs()) &&
                condition.apply(criteria.getTcpConnectMs()) &&
                condition.apply(criteria.getTlsHandshakeMs()) &&
                condition.apply(criteria.getTimeToFirstByteMs()) &&
                condition.apply(criteria.getWarningThresholdMs()) &&
                condition.apply(criteria.getCriticalThresholdMs()) &&
                condition.apply(criteria.getErrorType()) &&
                condition.apply(criteria.getMonitorId()) &&
                condition.apply(criteria.getAgentId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<HttpHeartbeatCriteria> copyFiltersAre(
        HttpHeartbeatCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getExecutedAt(), copy.getExecutedAt()) &&
                condition.apply(criteria.getSuccess(), copy.getSuccess()) &&
                condition.apply(criteria.getResponseTimeMs(), copy.getResponseTimeMs()) &&
                condition.apply(criteria.getResponseSizeBytes(), copy.getResponseSizeBytes()) &&
                condition.apply(criteria.getResponseStatusCode(), copy.getResponseStatusCode()) &&
                condition.apply(criteria.getResponseContentType(), copy.getResponseContentType()) &&
                condition.apply(criteria.getResponseServer(), copy.getResponseServer()) &&
                condition.apply(criteria.getResponseCacheStatus(), copy.getResponseCacheStatus()) &&
                condition.apply(criteria.getDnsLookupMs(), copy.getDnsLookupMs()) &&
                condition.apply(criteria.getTcpConnectMs(), copy.getTcpConnectMs()) &&
                condition.apply(criteria.getTlsHandshakeMs(), copy.getTlsHandshakeMs()) &&
                condition.apply(criteria.getTimeToFirstByteMs(), copy.getTimeToFirstByteMs()) &&
                condition.apply(criteria.getWarningThresholdMs(), copy.getWarningThresholdMs()) &&
                condition.apply(criteria.getCriticalThresholdMs(), copy.getCriticalThresholdMs()) &&
                condition.apply(criteria.getErrorType(), copy.getErrorType()) &&
                condition.apply(criteria.getMonitorId(), copy.getMonitorId()) &&
                condition.apply(criteria.getAgentId(), copy.getAgentId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
