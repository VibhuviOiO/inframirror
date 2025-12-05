package vibhuvi.oio.inframirror.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static vibhuvi.oio.inframirror.domain.AgentTestSamples.*;
import static vibhuvi.oio.inframirror.domain.DatacenterTestSamples.*;
import static vibhuvi.oio.inframirror.domain.InstanceHeartbeatTestSamples.*;
import static vibhuvi.oio.inframirror.domain.InstanceTestSamples.*;
import static vibhuvi.oio.inframirror.domain.ServiceInstanceTestSamples.*;

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
    void heartbeatTest() {
        Instance instance = getInstanceRandomSampleGenerator();
        InstanceHeartbeat instanceHeartbeatBack = getInstanceHeartbeatRandomSampleGenerator();

        instance.addHeartbeat(instanceHeartbeatBack);
        assertThat(instance.getHeartbeats()).containsOnly(instanceHeartbeatBack);
        assertThat(instanceHeartbeatBack.getInstance()).isEqualTo(instance);

        instance.removeHeartbeat(instanceHeartbeatBack);
        assertThat(instance.getHeartbeats()).doesNotContain(instanceHeartbeatBack);
        assertThat(instanceHeartbeatBack.getInstance()).isNull();

        instance.heartbeats(new HashSet<>(Set.of(instanceHeartbeatBack)));
        assertThat(instance.getHeartbeats()).containsOnly(instanceHeartbeatBack);
        assertThat(instanceHeartbeatBack.getInstance()).isEqualTo(instance);

        instance.setHeartbeats(new HashSet<>());
        assertThat(instance.getHeartbeats()).doesNotContain(instanceHeartbeatBack);
        assertThat(instanceHeartbeatBack.getInstance()).isNull();
    }

    @Test
    void serviceInstanceTest() {
        Instance instance = getInstanceRandomSampleGenerator();
        ServiceInstance serviceInstanceBack = getServiceInstanceRandomSampleGenerator();

        instance.addServiceInstance(serviceInstanceBack);
        assertThat(instance.getServiceInstances()).containsOnly(serviceInstanceBack);
        assertThat(serviceInstanceBack.getInstance()).isEqualTo(instance);

        instance.removeServiceInstance(serviceInstanceBack);
        assertThat(instance.getServiceInstances()).doesNotContain(serviceInstanceBack);
        assertThat(serviceInstanceBack.getInstance()).isNull();

        instance.serviceInstances(new HashSet<>(Set.of(serviceInstanceBack)));
        assertThat(instance.getServiceInstances()).containsOnly(serviceInstanceBack);
        assertThat(serviceInstanceBack.getInstance()).isEqualTo(instance);

        instance.setServiceInstances(new HashSet<>());
        assertThat(instance.getServiceInstances()).doesNotContain(serviceInstanceBack);
        assertThat(serviceInstanceBack.getInstance()).isNull();
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
