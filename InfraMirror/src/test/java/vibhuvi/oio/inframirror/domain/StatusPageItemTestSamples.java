package vibhuvi.oio.inframirror.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class StatusPageItemTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static StatusPageItem getStatusPageItemSample1() {
        return new StatusPageItem().id(1L).itemType("itemType1").itemId(1L).displayOrder(1);
    }

    public static StatusPageItem getStatusPageItemSample2() {
        return new StatusPageItem().id(2L).itemType("itemType2").itemId(2L).displayOrder(2);
    }

    public static StatusPageItem getStatusPageItemRandomSampleGenerator() {
        return new StatusPageItem()
            .id(longCount.incrementAndGet())
            .itemType(UUID.randomUUID().toString())
            .itemId(longCount.incrementAndGet())
            .displayOrder(intCount.incrementAndGet());
    }
}
