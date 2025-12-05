package vibhuvi.oio.inframirror.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class HttpMonitorCriteriaTest {

    @Test
    void newHttpMonitorCriteriaHasAllFiltersNullTest() {
        var httpMonitorCriteria = new HttpMonitorCriteria();
        assertThat(httpMonitorCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void httpMonitorCriteriaFluentMethodsCreatesFiltersTest() {
        var httpMonitorCriteria = new HttpMonitorCriteria();

        setAllFilters(httpMonitorCriteria);

        assertThat(httpMonitorCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void httpMonitorCriteriaCopyCreatesNullFilterTest() {
        var httpMonitorCriteria = new HttpMonitorCriteria();
        var copy = httpMonitorCriteria.copy();

        assertThat(httpMonitorCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(httpMonitorCriteria)
        );
    }

    @Test
    void httpMonitorCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var httpMonitorCriteria = new HttpMonitorCriteria();
        setAllFilters(httpMonitorCriteria);

        var copy = httpMonitorCriteria.copy();

        assertThat(httpMonitorCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(httpMonitorCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var httpMonitorCriteria = new HttpMonitorCriteria();

        assertThat(httpMonitorCriteria).hasToString("HttpMonitorCriteria{}");
    }

    private static void setAllFilters(HttpMonitorCriteria httpMonitorCriteria) {
        httpMonitorCriteria.id();
        httpMonitorCriteria.name();
        httpMonitorCriteria.method();
        httpMonitorCriteria.type();
        httpMonitorCriteria.intervalSeconds();
        httpMonitorCriteria.timeoutSeconds();
        httpMonitorCriteria.retryCount();
        httpMonitorCriteria.retryDelaySeconds();
        httpMonitorCriteria.responseTimeWarningMs();
        httpMonitorCriteria.responseTimeCriticalMs();
        httpMonitorCriteria.uptimeWarningPercent();
        httpMonitorCriteria.uptimeCriticalPercent();
        httpMonitorCriteria.includeResponseBody();
        httpMonitorCriteria.resendNotificationCount();
        httpMonitorCriteria.certificateExpiryDays();
        httpMonitorCriteria.ignoreTlsError();
        httpMonitorCriteria.checkSslCertificate();
        httpMonitorCriteria.checkDnsResolution();
        httpMonitorCriteria.upsideDownMode();
        httpMonitorCriteria.maxRedirects();
        httpMonitorCriteria.tags();
        httpMonitorCriteria.enabled();
        httpMonitorCriteria.expectedStatusCodes();
        httpMonitorCriteria.performanceBudgetMs();
        httpMonitorCriteria.sizeBudgetKb();
        httpMonitorCriteria.childrenId();
        httpMonitorCriteria.heartbeatId();
        httpMonitorCriteria.parentId();
        httpMonitorCriteria.distinct();
    }

    private static Condition<HttpMonitorCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getMethod()) &&
                condition.apply(criteria.getType()) &&
                condition.apply(criteria.getIntervalSeconds()) &&
                condition.apply(criteria.getTimeoutSeconds()) &&
                condition.apply(criteria.getRetryCount()) &&
                condition.apply(criteria.getRetryDelaySeconds()) &&
                condition.apply(criteria.getResponseTimeWarningMs()) &&
                condition.apply(criteria.getResponseTimeCriticalMs()) &&
                condition.apply(criteria.getUptimeWarningPercent()) &&
                condition.apply(criteria.getUptimeCriticalPercent()) &&
                condition.apply(criteria.getIncludeResponseBody()) &&
                condition.apply(criteria.getResendNotificationCount()) &&
                condition.apply(criteria.getCertificateExpiryDays()) &&
                condition.apply(criteria.getIgnoreTlsError()) &&
                condition.apply(criteria.getCheckSslCertificate()) &&
                condition.apply(criteria.getCheckDnsResolution()) &&
                condition.apply(criteria.getUpsideDownMode()) &&
                condition.apply(criteria.getMaxRedirects()) &&
                condition.apply(criteria.getTags()) &&
                condition.apply(criteria.getEnabled()) &&
                condition.apply(criteria.getExpectedStatusCodes()) &&
                condition.apply(criteria.getPerformanceBudgetMs()) &&
                condition.apply(criteria.getSizeBudgetKb()) &&
                condition.apply(criteria.getChildrenId()) &&
                condition.apply(criteria.getHeartbeatId()) &&
                condition.apply(criteria.getParentId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<HttpMonitorCriteria> copyFiltersAre(HttpMonitorCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getMethod(), copy.getMethod()) &&
                condition.apply(criteria.getType(), copy.getType()) &&
                condition.apply(criteria.getIntervalSeconds(), copy.getIntervalSeconds()) &&
                condition.apply(criteria.getTimeoutSeconds(), copy.getTimeoutSeconds()) &&
                condition.apply(criteria.getRetryCount(), copy.getRetryCount()) &&
                condition.apply(criteria.getRetryDelaySeconds(), copy.getRetryDelaySeconds()) &&
                condition.apply(criteria.getResponseTimeWarningMs(), copy.getResponseTimeWarningMs()) &&
                condition.apply(criteria.getResponseTimeCriticalMs(), copy.getResponseTimeCriticalMs()) &&
                condition.apply(criteria.getUptimeWarningPercent(), copy.getUptimeWarningPercent()) &&
                condition.apply(criteria.getUptimeCriticalPercent(), copy.getUptimeCriticalPercent()) &&
                condition.apply(criteria.getIncludeResponseBody(), copy.getIncludeResponseBody()) &&
                condition.apply(criteria.getResendNotificationCount(), copy.getResendNotificationCount()) &&
                condition.apply(criteria.getCertificateExpiryDays(), copy.getCertificateExpiryDays()) &&
                condition.apply(criteria.getIgnoreTlsError(), copy.getIgnoreTlsError()) &&
                condition.apply(criteria.getCheckSslCertificate(), copy.getCheckSslCertificate()) &&
                condition.apply(criteria.getCheckDnsResolution(), copy.getCheckDnsResolution()) &&
                condition.apply(criteria.getUpsideDownMode(), copy.getUpsideDownMode()) &&
                condition.apply(criteria.getMaxRedirects(), copy.getMaxRedirects()) &&
                condition.apply(criteria.getTags(), copy.getTags()) &&
                condition.apply(criteria.getEnabled(), copy.getEnabled()) &&
                condition.apply(criteria.getExpectedStatusCodes(), copy.getExpectedStatusCodes()) &&
                condition.apply(criteria.getPerformanceBudgetMs(), copy.getPerformanceBudgetMs()) &&
                condition.apply(criteria.getSizeBudgetKb(), copy.getSizeBudgetKb()) &&
                condition.apply(criteria.getChildrenId(), copy.getChildrenId()) &&
                condition.apply(criteria.getHeartbeatId(), copy.getHeartbeatId()) &&
                condition.apply(criteria.getParentId(), copy.getParentId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
