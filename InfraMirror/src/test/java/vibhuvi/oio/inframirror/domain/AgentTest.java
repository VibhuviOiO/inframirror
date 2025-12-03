package vibhuvi.oio.inframirror.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static vibhuvi.oio.inframirror.domain.AgentTestSamples.*;
import static vibhuvi.oio.inframirror.domain.DatacenterTestSamples.*;
import static vibhuvi.oio.inframirror.domain.HttpHeartbeatTestSamples.*;
import static vibhuvi.oio.inframirror.domain.InstanceTestSamples.*;
import static vibhuvi.oio.inframirror.domain.PingHeartbeatTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import vibhuvi.oio.inframirror.web.rest.TestUtil;

class AgentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Agent.class);
        Agent agent1 = getAgentSample1();
        Agent agent2 = new Agent();
        assertThat(agent1).isNotEqualTo(agent2);

        agent2.setId(agent1.getId());
        assertThat(agent1).isEqualTo(agent2);

        agent2 = getAgentSample2();
        assertThat(agent1).isNotEqualTo(agent2);
    }

    @Test
    void instancesTest() {
        Agent agent = getAgentRandomSampleGenerator();
        Instance instanceBack = getInstanceRandomSampleGenerator();

        agent.addInstances(instanceBack);
        assertThat(agent.getInstances()).containsOnly(instanceBack);
        assertThat(instanceBack.getAgent()).isEqualTo(agent);

        agent.removeInstances(instanceBack);
        assertThat(agent.getInstances()).doesNotContain(instanceBack);
        assertThat(instanceBack.getAgent()).isNull();

        agent.instances(new HashSet<>(Set.of(instanceBack)));
        assertThat(agent.getInstances()).containsOnly(instanceBack);
        assertThat(instanceBack.getAgent()).isEqualTo(agent);

        agent.setInstances(new HashSet<>());
        assertThat(agent.getInstances()).doesNotContain(instanceBack);
        assertThat(instanceBack.getAgent()).isNull();
    }

    @Test
    void httpHeartbeatsTest() {
        Agent agent = getAgentRandomSampleGenerator();
        HttpHeartbeat httpHeartbeatBack = getHttpHeartbeatRandomSampleGenerator();

        agent.addHttpHeartbeats(httpHeartbeatBack);
        assertThat(agent.getHttpHeartbeats()).containsOnly(httpHeartbeatBack);
        assertThat(httpHeartbeatBack.getAgent()).isEqualTo(agent);

        agent.removeHttpHeartbeats(httpHeartbeatBack);
        assertThat(agent.getHttpHeartbeats()).doesNotContain(httpHeartbeatBack);
        assertThat(httpHeartbeatBack.getAgent()).isNull();

        agent.httpHeartbeats(new HashSet<>(Set.of(httpHeartbeatBack)));
        assertThat(agent.getHttpHeartbeats()).containsOnly(httpHeartbeatBack);
        assertThat(httpHeartbeatBack.getAgent()).isEqualTo(agent);

        agent.setHttpHeartbeats(new HashSet<>());
        assertThat(agent.getHttpHeartbeats()).doesNotContain(httpHeartbeatBack);
        assertThat(httpHeartbeatBack.getAgent()).isNull();
    }

    @Test
    void pingHeartbeatsTest() {
        Agent agent = getAgentRandomSampleGenerator();
        PingHeartbeat pingHeartbeatBack = getPingHeartbeatRandomSampleGenerator();

        agent.addPingHeartbeats(pingHeartbeatBack);
        assertThat(agent.getPingHeartbeats()).containsOnly(pingHeartbeatBack);
        assertThat(pingHeartbeatBack.getAgent()).isEqualTo(agent);

        agent.removePingHeartbeats(pingHeartbeatBack);
        assertThat(agent.getPingHeartbeats()).doesNotContain(pingHeartbeatBack);
        assertThat(pingHeartbeatBack.getAgent()).isNull();

        agent.pingHeartbeats(new HashSet<>(Set.of(pingHeartbeatBack)));
        assertThat(agent.getPingHeartbeats()).containsOnly(pingHeartbeatBack);
        assertThat(pingHeartbeatBack.getAgent()).isEqualTo(agent);

        agent.setPingHeartbeats(new HashSet<>());
        assertThat(agent.getPingHeartbeats()).doesNotContain(pingHeartbeatBack);
        assertThat(pingHeartbeatBack.getAgent()).isNull();
    }

    @Test
    void datacenterTest() {
        Agent agent = getAgentRandomSampleGenerator();
        Datacenter datacenterBack = getDatacenterRandomSampleGenerator();

        agent.setDatacenter(datacenterBack);
        assertThat(agent.getDatacenter()).isEqualTo(datacenterBack);

        agent.datacenter(null);
        assertThat(agent.getDatacenter()).isNull();
    }
}
