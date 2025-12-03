package vibhuvi.oio.inframirror.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AgentMonitorTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static AgentMonitor getAgentMonitorSample1() {
        return new AgentMonitor().id(1L).createdBy("createdBy1").lastModifiedBy("lastModifiedBy1");
    }

    public static AgentMonitor getAgentMonitorSample2() {
        return new AgentMonitor().id(2L).createdBy("createdBy2").lastModifiedBy("lastModifiedBy2");
    }

    public static AgentMonitor getAgentMonitorRandomSampleGenerator() {
        return new AgentMonitor()
            .id(longCount.incrementAndGet())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
