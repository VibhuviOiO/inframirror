package vibhuvi.oio.inframirror.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static vibhuvi.oio.inframirror.domain.AgentTestSamples.*;
import static vibhuvi.oio.inframirror.domain.HttpHeartbeatTestSamples.*;
import static vibhuvi.oio.inframirror.domain.InstanceHeartbeatTestSamples.*;
import static vibhuvi.oio.inframirror.domain.InstanceTestSamples.*;
import static vibhuvi.oio.inframirror.domain.RegionTestSamples.*;
import static vibhuvi.oio.inframirror.domain.ServiceHeartbeatTestSamples.*;

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
    void instanceTest() {
        Agent agent = getAgentRandomSampleGenerator();
        Instance instanceBack = getInstanceRandomSampleGenerator();

        agent.addInstance(instanceBack);
        assertThat(agent.getInstances()).containsOnly(instanceBack);
        assertThat(instanceBack.getAgent()).isEqualTo(agent);

        agent.removeInstance(instanceBack);
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
    void httpHeartbeatTest() {
        Agent agent = getAgentRandomSampleGenerator();
        HttpHeartbeat httpHeartbeatBack = getHttpHeartbeatRandomSampleGenerator();

        agent.addHttpHeartbeat(httpHeartbeatBack);
        assertThat(agent.getHttpHeartbeats()).containsOnly(httpHeartbeatBack);
        assertThat(httpHeartbeatBack.getAgent()).isEqualTo(agent);

        agent.removeHttpHeartbeat(httpHeartbeatBack);
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
    void instanceHeartbeatTest() {
        Agent agent = getAgentRandomSampleGenerator();
        InstanceHeartbeat instanceHeartbeatBack = getInstanceHeartbeatRandomSampleGenerator();

        agent.addInstanceHeartbeat(instanceHeartbeatBack);
        assertThat(agent.getInstanceHeartbeats()).containsOnly(instanceHeartbeatBack);
        assertThat(instanceHeartbeatBack.getAgent()).isEqualTo(agent);

        agent.removeInstanceHeartbeat(instanceHeartbeatBack);
        assertThat(agent.getInstanceHeartbeats()).doesNotContain(instanceHeartbeatBack);
        assertThat(instanceHeartbeatBack.getAgent()).isNull();

        agent.instanceHeartbeats(new HashSet<>(Set.of(instanceHeartbeatBack)));
        assertThat(agent.getInstanceHeartbeats()).containsOnly(instanceHeartbeatBack);
        assertThat(instanceHeartbeatBack.getAgent()).isEqualTo(agent);

        agent.setInstanceHeartbeats(new HashSet<>());
        assertThat(agent.getInstanceHeartbeats()).doesNotContain(instanceHeartbeatBack);
        assertThat(instanceHeartbeatBack.getAgent()).isNull();
    }

    @Test
    void serviceHeartbeatTest() {
        Agent agent = getAgentRandomSampleGenerator();
        ServiceHeartbeat serviceHeartbeatBack = getServiceHeartbeatRandomSampleGenerator();

        agent.addServiceHeartbeat(serviceHeartbeatBack);
        assertThat(agent.getServiceHeartbeats()).containsOnly(serviceHeartbeatBack);
        assertThat(serviceHeartbeatBack.getAgent()).isEqualTo(agent);

        agent.removeServiceHeartbeat(serviceHeartbeatBack);
        assertThat(agent.getServiceHeartbeats()).doesNotContain(serviceHeartbeatBack);
        assertThat(serviceHeartbeatBack.getAgent()).isNull();

        agent.serviceHeartbeats(new HashSet<>(Set.of(serviceHeartbeatBack)));
        assertThat(agent.getServiceHeartbeats()).containsOnly(serviceHeartbeatBack);
        assertThat(serviceHeartbeatBack.getAgent()).isEqualTo(agent);

        agent.setServiceHeartbeats(new HashSet<>());
        assertThat(agent.getServiceHeartbeats()).doesNotContain(serviceHeartbeatBack);
        assertThat(serviceHeartbeatBack.getAgent()).isNull();
    }

    @Test
    void regionTest() {
        Agent agent = getAgentRandomSampleGenerator();
}
}
