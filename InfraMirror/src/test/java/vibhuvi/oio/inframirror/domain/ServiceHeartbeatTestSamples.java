package vibhuvi.oio.inframirror.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ServiceHeartbeatTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static ServiceHeartbeat getServiceHeartbeatSample1() {
        return new ServiceHeartbeat().id(1L).status("status1").responseTimeMs(1);
    }

    public static ServiceHeartbeat getServiceHeartbeatSample2() {
        return new ServiceHeartbeat().id(2L).status("status2").responseTimeMs(2);
    }

    public static ServiceHeartbeat getServiceHeartbeatRandomSampleGenerator() {
        return new ServiceHeartbeat()
            .id(longCount.incrementAndGet())
            .status(UUID.randomUUID().toString())
            .responseTimeMs(intCount.incrementAndGet());
    }
}
