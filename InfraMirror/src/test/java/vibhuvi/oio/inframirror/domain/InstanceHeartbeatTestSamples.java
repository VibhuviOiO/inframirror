package vibhuvi.oio.inframirror.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class InstanceHeartbeatTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static InstanceHeartbeat getInstanceHeartbeatSample1() {
        return new InstanceHeartbeat()
            .id(1L)
            .heartbeatType("heartbeatType1")
            .responseTimeMs(1)
            .jitterMs(1)
            .processCount(1)
            .networkRxBytes(1L)
            .networkTxBytes(1L)
            .uptimeSeconds(1L)
            .status("status1")
            .errorType("errorType1");
    }

    public static InstanceHeartbeat getInstanceHeartbeatSample2() {
        return new InstanceHeartbeat()
            .id(2L)
            .heartbeatType("heartbeatType2")
            .responseTimeMs(2)
            .jitterMs(2)
            .processCount(2)
            .networkRxBytes(2L)
            .networkTxBytes(2L)
            .uptimeSeconds(2L)
            .status("status2")
            .errorType("errorType2");
    }

    public static InstanceHeartbeat getInstanceHeartbeatRandomSampleGenerator() {
        return new InstanceHeartbeat()
            .id(longCount.incrementAndGet())
            .heartbeatType(UUID.randomUUID().toString())
            .responseTimeMs(intCount.incrementAndGet())
            .jitterMs(intCount.incrementAndGet())
            .processCount(intCount.incrementAndGet())
            .networkRxBytes(longCount.incrementAndGet())
            .networkTxBytes(longCount.incrementAndGet())
            .uptimeSeconds(longCount.incrementAndGet())
            .status(UUID.randomUUID().toString())
            .errorType(UUID.randomUUID().toString());
    }
}
