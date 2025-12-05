package vibhuvi.oio.inframirror.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ServiceInstanceTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static ServiceInstance getServiceInstanceSample1() {
        return new ServiceInstance().id(1L).port(1);
    }

    public static ServiceInstance getServiceInstanceSample2() {
        return new ServiceInstance().id(2L).port(2);
    }

    public static ServiceInstance getServiceInstanceRandomSampleGenerator() {
        return new ServiceInstance().id(longCount.incrementAndGet()).port(intCount.incrementAndGet());
    }
}
