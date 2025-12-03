package vibhuvi.oio.inframirror.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SessionLogTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static SessionLog getSessionLogSample1() {
        return new SessionLog()
            .id(1L)
            .sessionType("sessionType1")
            .duration(1)
            .sourceIpAddress("sourceIpAddress1")
            .status("status1")
            .terminationReason("terminationReason1")
            .commandsExecuted(1)
            .bytesTransferred(1L)
            .sessionId("sessionId1");
    }

    public static SessionLog getSessionLogSample2() {
        return new SessionLog()
            .id(2L)
            .sessionType("sessionType2")
            .duration(2)
            .sourceIpAddress("sourceIpAddress2")
            .status("status2")
            .terminationReason("terminationReason2")
            .commandsExecuted(2)
            .bytesTransferred(2L)
            .sessionId("sessionId2");
    }

    public static SessionLog getSessionLogRandomSampleGenerator() {
        return new SessionLog()
            .id(longCount.incrementAndGet())
            .sessionType(UUID.randomUUID().toString())
            .duration(intCount.incrementAndGet())
            .sourceIpAddress(UUID.randomUUID().toString())
            .status(UUID.randomUUID().toString())
            .terminationReason(UUID.randomUUID().toString())
            .commandsExecuted(intCount.incrementAndGet())
            .bytesTransferred(longCount.incrementAndGet())
            .sessionId(UUID.randomUUID().toString());
    }
}
