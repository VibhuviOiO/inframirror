package vibhuvi.oio.inframirror.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static vibhuvi.oio.inframirror.domain.AgentTestSamples.*;
import static vibhuvi.oio.inframirror.domain.ServiceHeartbeatTestSamples.*;
import static vibhuvi.oio.inframirror.domain.ServiceInstanceTestSamples.*;
import static vibhuvi.oio.inframirror.domain.ServiceTestSamples.*;

import org.junit.jupiter.api.Test;
import vibhuvi.oio.inframirror.web.rest.TestUtil;

class ServiceHeartbeatTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ServiceHeartbeat.class);
        ServiceHeartbeat serviceHeartbeat1 = getServiceHeartbeatSample1();
        ServiceHeartbeat serviceHeartbeat2 = new ServiceHeartbeat();
        assertThat(serviceHeartbeat1).isNotEqualTo(serviceHeartbeat2);

        serviceHeartbeat2.setId(serviceHeartbeat1.getId());
        assertThat(serviceHeartbeat1).isEqualTo(serviceHeartbeat2);

        serviceHeartbeat2 = getServiceHeartbeatSample2();
        assertThat(serviceHeartbeat1).isNotEqualTo(serviceHeartbeat2);
    }

    @Test
    void agentTest() {
        ServiceHeartbeat serviceHeartbeat = getServiceHeartbeatRandomSampleGenerator();
        Agent agentBack = getAgentRandomSampleGenerator();

        serviceHeartbeat.setAgent(agentBack);
        assertThat(serviceHeartbeat.getAgent()).isEqualTo(agentBack);

        serviceHeartbeat.agent(null);
        assertThat(serviceHeartbeat.getAgent()).isNull();
    }

    @Test
    void serviceTest() {
        ServiceHeartbeat serviceHeartbeat = getServiceHeartbeatRandomSampleGenerator();
        Service serviceBack = getServiceRandomSampleGenerator();

        serviceHeartbeat.setService(serviceBack);
        assertThat(serviceHeartbeat.getService()).isEqualTo(serviceBack);

        serviceHeartbeat.service(null);
        assertThat(serviceHeartbeat.getService()).isNull();
    }

    @Test
    void serviceInstanceTest() {
        ServiceHeartbeat serviceHeartbeat = getServiceHeartbeatRandomSampleGenerator();
        ServiceInstance serviceInstanceBack = getServiceInstanceRandomSampleGenerator();

        serviceHeartbeat.setServiceInstance(serviceInstanceBack);
        assertThat(serviceHeartbeat.getServiceInstance()).isEqualTo(serviceInstanceBack);

        serviceHeartbeat.serviceInstance(null);
        assertThat(serviceHeartbeat.getServiceInstance()).isNull();
    }
}
