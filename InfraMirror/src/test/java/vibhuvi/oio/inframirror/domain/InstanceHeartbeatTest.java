package vibhuvi.oio.inframirror.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static vibhuvi.oio.inframirror.domain.AgentTestSamples.*;
import static vibhuvi.oio.inframirror.domain.InstanceHeartbeatTestSamples.*;
import static vibhuvi.oio.inframirror.domain.InstanceTestSamples.*;

import org.junit.jupiter.api.Test;
import vibhuvi.oio.inframirror.web.rest.TestUtil;

class InstanceHeartbeatTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(InstanceHeartbeat.class);
        InstanceHeartbeat instanceHeartbeat1 = getInstanceHeartbeatSample1();
        InstanceHeartbeat instanceHeartbeat2 = new InstanceHeartbeat();
        assertThat(instanceHeartbeat1).isNotEqualTo(instanceHeartbeat2);

        instanceHeartbeat2.setId(instanceHeartbeat1.getId());
        assertThat(instanceHeartbeat1).isEqualTo(instanceHeartbeat2);

        instanceHeartbeat2 = getInstanceHeartbeatSample2();
        assertThat(instanceHeartbeat1).isNotEqualTo(instanceHeartbeat2);
    }

    @Test
    void agentTest() {
        InstanceHeartbeat instanceHeartbeat = getInstanceHeartbeatRandomSampleGenerator();
        Agent agentBack = getAgentRandomSampleGenerator();

        instanceHeartbeat.setAgent(agentBack);
        assertThat(instanceHeartbeat.getAgent()).isEqualTo(agentBack);

        instanceHeartbeat.agent(null);
        assertThat(instanceHeartbeat.getAgent()).isNull();
    }

    @Test
    void instanceTest() {
        InstanceHeartbeat instanceHeartbeat = getInstanceHeartbeatRandomSampleGenerator();
        Instance instanceBack = getInstanceRandomSampleGenerator();

        instanceHeartbeat.setInstance(instanceBack);
        assertThat(instanceHeartbeat.getInstance()).isEqualTo(instanceBack);

        instanceHeartbeat.instance(null);
        assertThat(instanceHeartbeat.getInstance()).isNull();
    }
}
