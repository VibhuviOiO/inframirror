package vibhuvi.oio.inframirror.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AuditTrailTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static AuditTrail getAuditTrailSample1() {
        return new AuditTrail().id(1L).action("action1").entityName("entityName1").entityId(1L).ipAddress("ipAddress1");
    }

    public static AuditTrail getAuditTrailSample2() {
        return new AuditTrail().id(2L).action("action2").entityName("entityName2").entityId(2L).ipAddress("ipAddress2");
    }

    public static AuditTrail getAuditTrailRandomSampleGenerator() {
        return new AuditTrail()
            .id(longCount.incrementAndGet())
            .action(UUID.randomUUID().toString())
            .entityName(UUID.randomUUID().toString())
            .entityId(longCount.incrementAndGet())
            .ipAddress(UUID.randomUUID().toString());
    }
}
