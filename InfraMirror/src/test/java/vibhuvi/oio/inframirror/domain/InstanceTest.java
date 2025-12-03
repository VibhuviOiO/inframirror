package vibhuvi.oio.inframirror.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static vibhuvi.oio.inframirror.domain.AgentTestSamples.*;
import static vibhuvi.oio.inframirror.domain.DatacenterTestSamples.*;
import static vibhuvi.oio.inframirror.domain.InstanceTestSamples.*;
import static vibhuvi.oio.inframirror.domain.PingHeartbeatTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import vibhuvi.oio.inframirror.web.rest.TestUtil;

class InstanceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Instance.class);
        Instance instance1 = getInstanceSample1();
        Instance instance2 = new Instance();
        assertThat(instance1).isNotEqualTo(instance2);

        instance2.setId(instance1.getId());
        assertThat(instance1).isEqualTo(instance2);

        instance2 = getInstanceSample2();
        assertThat(instance1).isNotEqualTo(instance2);
    }

    @Test
    void pingHeartbeatsTest() {
        Instance instance = getInstanceRandomSampleGenerator();
        PingHeartbeat pingHeartbeatBack = getPingHeartbeatRandomSampleGenerator();

        instance.addPingHeartbeats(pingHeartbeatBack);
        assertThat(instance.getPingHeartbeats()).containsOnly(pingHeartbeatBack);
        assertThat(pingHeartbeatBack.getInstance()).isEqualTo(instance);

        instance.removePingHeartbeats(pingHeartbeatBack);
        assertThat(instance.getPingHeartbeats()).doesNotContain(pingHeartbeatBack);
        assertThat(pingHeartbeatBack.getInstance()).isNull();

        instance.pingHeartbeats(new HashSet<>(Set.of(pingHeartbeatBack)));
        assertThat(instance.getPingHeartbeats()).containsOnly(pingHeartbeatBack);
        assertThat(pingHeartbeatBack.getInstance()).isEqualTo(instance);

        instance.setPingHeartbeats(new HashSet<>());
        assertThat(instance.getPingHeartbeats()).doesNotContain(pingHeartbeatBack);
        assertThat(pingHeartbeatBack.getInstance()).isNull();
    }

    @Test
    void datacenterTest() {
        Instance instance = getInstanceRandomSampleGenerator();
        Datacenter datacenterBack = getDatacenterRandomSampleGenerator();

        instance.setDatacenter(datacenterBack);
        assertThat(instance.getDatacenter()).isEqualTo(datacenterBack);

        instance.datacenter(null);
        assertThat(instance.getDatacenter()).isNull();
    }

    @Test
    void agentTest() {
        Instance instance = getInstanceRandomSampleGenerator();
        Agent agentBack = getAgentRandomSampleGenerator();

        instance.setAgent(agentBack);
        assertThat(instance.getAgent()).isEqualTo(agentBack);

        instance.agent(null);
        assertThat(instance.getAgent()).isNull();
    }
}
