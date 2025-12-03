package vibhuvi.oio.inframirror.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static vibhuvi.oio.inframirror.domain.AgentTestSamples.*;
import static vibhuvi.oio.inframirror.domain.InstanceTestSamples.*;
import static vibhuvi.oio.inframirror.domain.PingHeartbeatTestSamples.*;

import org.junit.jupiter.api.Test;
import vibhuvi.oio.inframirror.web.rest.TestUtil;

class PingHeartbeatTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PingHeartbeat.class);
        PingHeartbeat pingHeartbeat1 = getPingHeartbeatSample1();
        PingHeartbeat pingHeartbeat2 = new PingHeartbeat();
        assertThat(pingHeartbeat1).isNotEqualTo(pingHeartbeat2);

        pingHeartbeat2.setId(pingHeartbeat1.getId());
        assertThat(pingHeartbeat1).isEqualTo(pingHeartbeat2);

        pingHeartbeat2 = getPingHeartbeatSample2();
        assertThat(pingHeartbeat1).isNotEqualTo(pingHeartbeat2);
    }

    @Test
    void instanceTest() {
        PingHeartbeat pingHeartbeat = getPingHeartbeatRandomSampleGenerator();
        Instance instanceBack = getInstanceRandomSampleGenerator();

        pingHeartbeat.setInstance(instanceBack);
        assertThat(pingHeartbeat.getInstance()).isEqualTo(instanceBack);

        pingHeartbeat.instance(null);
        assertThat(pingHeartbeat.getInstance()).isNull();
    }

    @Test
    void agentTest() {
        PingHeartbeat pingHeartbeat = getPingHeartbeatRandomSampleGenerator();
        Agent agentBack = getAgentRandomSampleGenerator();

        pingHeartbeat.setAgent(agentBack);
        assertThat(pingHeartbeat.getAgent()).isEqualTo(agentBack);

        pingHeartbeat.agent(null);
        assertThat(pingHeartbeat.getAgent()).isNull();
    }
}
