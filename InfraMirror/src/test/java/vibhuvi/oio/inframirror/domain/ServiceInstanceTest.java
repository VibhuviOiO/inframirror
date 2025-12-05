package vibhuvi.oio.inframirror.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static vibhuvi.oio.inframirror.domain.InstanceTestSamples.*;
import static vibhuvi.oio.inframirror.domain.ServiceHeartbeatTestSamples.*;
import static vibhuvi.oio.inframirror.domain.ServiceInstanceTestSamples.*;
import static vibhuvi.oio.inframirror.domain.ServiceTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import vibhuvi.oio.inframirror.web.rest.TestUtil;

class ServiceInstanceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ServiceInstance.class);
        ServiceInstance serviceInstance1 = getServiceInstanceSample1();
        ServiceInstance serviceInstance2 = new ServiceInstance();
        assertThat(serviceInstance1).isNotEqualTo(serviceInstance2);

        serviceInstance2.setId(serviceInstance1.getId());
        assertThat(serviceInstance1).isEqualTo(serviceInstance2);

        serviceInstance2 = getServiceInstanceSample2();
        assertThat(serviceInstance1).isNotEqualTo(serviceInstance2);
    }

    @Test
    void heartbeatTest() {
        ServiceInstance serviceInstance = getServiceInstanceRandomSampleGenerator();
        ServiceHeartbeat serviceHeartbeatBack = getServiceHeartbeatRandomSampleGenerator();

        serviceInstance.addHeartbeat(serviceHeartbeatBack);
        assertThat(serviceInstance.getHeartbeats()).containsOnly(serviceHeartbeatBack);
        assertThat(serviceHeartbeatBack.getServiceInstance()).isEqualTo(serviceInstance);

        serviceInstance.removeHeartbeat(serviceHeartbeatBack);
        assertThat(serviceInstance.getHeartbeats()).doesNotContain(serviceHeartbeatBack);
        assertThat(serviceHeartbeatBack.getServiceInstance()).isNull();

        serviceInstance.heartbeats(new HashSet<>(Set.of(serviceHeartbeatBack)));
        assertThat(serviceInstance.getHeartbeats()).containsOnly(serviceHeartbeatBack);
        assertThat(serviceHeartbeatBack.getServiceInstance()).isEqualTo(serviceInstance);

        serviceInstance.setHeartbeats(new HashSet<>());
        assertThat(serviceInstance.getHeartbeats()).doesNotContain(serviceHeartbeatBack);
        assertThat(serviceHeartbeatBack.getServiceInstance()).isNull();
    }

    @Test
    void instanceTest() {
        ServiceInstance serviceInstance = getServiceInstanceRandomSampleGenerator();
        Instance instanceBack = getInstanceRandomSampleGenerator();

        serviceInstance.setInstance(instanceBack);
        assertThat(serviceInstance.getInstance()).isEqualTo(instanceBack);

        serviceInstance.instance(null);
        assertThat(serviceInstance.getInstance()).isNull();
    }

    @Test
    void serviceTest() {
        ServiceInstance serviceInstance = getServiceInstanceRandomSampleGenerator();
        Service serviceBack = getServiceRandomSampleGenerator();

        serviceInstance.setService(serviceBack);
        assertThat(serviceInstance.getService()).isEqualTo(serviceBack);

        serviceInstance.service(null);
        assertThat(serviceInstance.getService()).isNull();
    }
}
