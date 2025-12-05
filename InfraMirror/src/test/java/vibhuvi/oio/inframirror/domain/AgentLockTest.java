package vibhuvi.oio.inframirror.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static vibhuvi.oio.inframirror.domain.AgentLockTestSamples.*;

import org.junit.jupiter.api.Test;
import vibhuvi.oio.inframirror.web.rest.TestUtil;

class AgentLockTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AgentLock.class);
        AgentLock agentLock1 = getAgentLockSample1();
        AgentLock agentLock2 = new AgentLock();
        assertThat(agentLock1).isNotEqualTo(agentLock2);

        agentLock2.setId(agentLock1.getId());
        assertThat(agentLock1).isEqualTo(agentLock2);

        agentLock2 = getAgentLockSample2();
        assertThat(agentLock1).isNotEqualTo(agentLock2);
    }
}
