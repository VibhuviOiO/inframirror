package vibhuvi.oio.inframirror.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TagTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Tag getTagSample1() {
        return new Tag().id(1L).key("key1").value("value1").entityType("entityType1").entityId(1L).createdBy("createdBy1");
    }

    public static Tag getTagSample2() {
        return new Tag().id(2L).key("key2").value("value2").entityType("entityType2").entityId(2L).createdBy("createdBy2");
    }

    public static Tag getTagRandomSampleGenerator() {
        return new Tag()
            .id(longCount.incrementAndGet())
            .key(UUID.randomUUID().toString())
            .value(UUID.randomUUID().toString())
            .entityType(UUID.randomUUID().toString())
            .entityId(longCount.incrementAndGet())
            .createdBy(UUID.randomUUID().toString());
    }
}
