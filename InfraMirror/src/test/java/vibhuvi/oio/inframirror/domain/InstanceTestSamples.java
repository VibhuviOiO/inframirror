package vibhuvi.oio.inframirror.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import vibhuvi.oio.inframirror.domain.enumeration.InstanceType;
import vibhuvi.oio.inframirror.domain.enumeration.OperatingSystem;

public class InstanceTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Instance getInstanceSample1() {
        return new Instance()
            .id(1L)
            .name("name1")
            .hostname("hostname1")
            .description("description1")
            .instanceType(InstanceType.VM)
            .operatingSystem(OperatingSystem.LINUX)
            .platform("platform1")
            .privateIpAddress("privateIpAddress1")
            .publicIpAddress("publicIpAddress1");
    }

    public static Instance getInstanceSample2() {
        return new Instance()
            .id(2L)
            .name("name2")
            .hostname("hostname2")
            .description("description2")
            .instanceType(InstanceType.BARE_METAL)
            .operatingSystem(OperatingSystem.WINDOWS)
            .platform("platform2")
            .privateIpAddress("privateIpAddress2")
            .publicIpAddress("publicIpAddress2");
    }

    public static Instance getInstanceRandomSampleGenerator() {
        return new Instance()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .hostname(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .instanceType(InstanceType.values()[random.nextInt(InstanceType.values().length)])
            .operatingSystem(OperatingSystem.values()[random.nextInt(OperatingSystem.values().length)])
            .platform(UUID.randomUUID().toString())
            .privateIpAddress(UUID.randomUUID().toString())
            .publicIpAddress(UUID.randomUUID().toString());
    }
}
