package vibhuvi.oio.inframirror.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class HttpMonitorTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static HttpMonitor getHttpMonitorSample1() {
        return new HttpMonitor()
            .id(1L)
            .name("name1")
            .method("method1")
            .type("type1")
            .intervalSeconds(1)
            .timeoutSeconds(1)
            .retryCount(1)
            .retryDelaySeconds(1)
            .responseTimeWarningMs(1)
            .responseTimeCriticalMs(1)
            .resendNotificationCount(1)
            .certificateExpiryDays(1)
            .maxRedirects(1)
            .tags("tags1")
            .expectedStatusCodes("expectedStatusCodes1")
            .performanceBudgetMs(1)
            .sizeBudgetKb(1);
    }

    public static HttpMonitor getHttpMonitorSample2() {
        return new HttpMonitor()
            .id(2L)
            .name("name2")
            .method("method2")
            .type("type2")
            .intervalSeconds(2)
            .timeoutSeconds(2)
            .retryCount(2)
            .retryDelaySeconds(2)
            .responseTimeWarningMs(2)
            .responseTimeCriticalMs(2)
            .resendNotificationCount(2)
            .certificateExpiryDays(2)
            .maxRedirects(2)
            .tags("tags2")
            .expectedStatusCodes("expectedStatusCodes2")
            .performanceBudgetMs(2)
            .sizeBudgetKb(2);
    }

    public static HttpMonitor getHttpMonitorRandomSampleGenerator() {
        return new HttpMonitor()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .method(UUID.randomUUID().toString())
            .type(UUID.randomUUID().toString())
            .intervalSeconds(intCount.incrementAndGet())
            .timeoutSeconds(intCount.incrementAndGet())
            .retryCount(intCount.incrementAndGet())
            .retryDelaySeconds(intCount.incrementAndGet())
            .responseTimeWarningMs(intCount.incrementAndGet())
            .responseTimeCriticalMs(intCount.incrementAndGet())
            .resendNotificationCount(intCount.incrementAndGet())
            .certificateExpiryDays(intCount.incrementAndGet())
            .maxRedirects(intCount.incrementAndGet())
            .tags(UUID.randomUUID().toString())
            .expectedStatusCodes(UUID.randomUUID().toString())
            .performanceBudgetMs(intCount.incrementAndGet())
            .sizeBudgetKb(intCount.incrementAndGet());
    }
}
