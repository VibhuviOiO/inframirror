package vibhuvi.oio.inframirror.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class AgentLockTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static AgentLock getAgentLockSample1() {
        return new AgentLock().id(1L).agentId(1L);
    }

    public static AgentLock getAgentLockSample2() {
        return new AgentLock().id(2L).agentId(2L);
    }

    public static AgentLock getAgentLockRandomSampleGenerator() {
        return new AgentLock().id(longCount.incrementAndGet()).agentId(longCount.incrementAndGet());
    }
}
