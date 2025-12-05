package vibhuvi.oio.inframirror.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class StatusDependencyTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static StatusDependency getStatusDependencySample1() {
        return new StatusDependency().id(1L).parentType("parentType1").parentId(1L).childType("childType1").childId(1L);
    }

    public static StatusDependency getStatusDependencySample2() {
        return new StatusDependency().id(2L).parentType("parentType2").parentId(2L).childType("childType2").childId(2L);
    }

    public static StatusDependency getStatusDependencyRandomSampleGenerator() {
        return new StatusDependency()
            .id(longCount.incrementAndGet())
            .parentType(UUID.randomUUID().toString())
            .parentId(longCount.incrementAndGet())
            .childType(UUID.randomUUID().toString())
            .childId(longCount.incrementAndGet());
    }
}
