package vibhuvi.oio.inframirror.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static vibhuvi.oio.inframirror.domain.DatacenterTestSamples.*;
import static vibhuvi.oio.inframirror.domain.ServiceHeartbeatTestSamples.*;
import static vibhuvi.oio.inframirror.domain.ServiceInstanceTestSamples.*;
import static vibhuvi.oio.inframirror.domain.ServiceTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import vibhuvi.oio.inframirror.web.rest.TestUtil;

class ServiceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Service.class);
        Service service1 = getServiceSample1();
        Service service2 = new Service();
        assertThat(service1).isNotEqualTo(service2);

        service2.setId(service1.getId());
        assertThat(service1).isEqualTo(service2);

        service2 = getServiceSample2();
        assertThat(service1).isNotEqualTo(service2);
    }

    @Test
    void serviceInstanceTest() {
        Service service = getServiceRandomSampleGenerator();
        ServiceInstance serviceInstanceBack = getServiceInstanceRandomSampleGenerator();

        service.addServiceInstance(serviceInstanceBack);
        assertThat(service.getServiceInstances()).containsOnly(serviceInstanceBack);
        assertThat(serviceInstanceBack.getService()).isEqualTo(service);

        service.removeServiceInstance(serviceInstanceBack);
        assertThat(service.getServiceInstances()).doesNotContain(serviceInstanceBack);
        assertThat(serviceInstanceBack.getService()).isNull();

        service.serviceInstances(new HashSet<>(Set.of(serviceInstanceBack)));
        assertThat(service.getServiceInstances()).containsOnly(serviceInstanceBack);
        assertThat(serviceInstanceBack.getService()).isEqualTo(service);

        service.setServiceInstances(new HashSet<>());
        assertThat(service.getServiceInstances()).doesNotContain(serviceInstanceBack);
        assertThat(serviceInstanceBack.getService()).isNull();
    }

    @Test
    void heartbeatTest() {
        Service service = getServiceRandomSampleGenerator();
        ServiceHeartbeat serviceHeartbeatBack = getServiceHeartbeatRandomSampleGenerator();

        service.addHeartbeat(serviceHeartbeatBack);
        assertThat(service.getHeartbeats()).containsOnly(serviceHeartbeatBack);
        assertThat(serviceHeartbeatBack.getService()).isEqualTo(service);

        service.removeHeartbeat(serviceHeartbeatBack);
        assertThat(service.getHeartbeats()).doesNotContain(serviceHeartbeatBack);
        assertThat(serviceHeartbeatBack.getService()).isNull();

        service.heartbeats(new HashSet<>(Set.of(serviceHeartbeatBack)));
        assertThat(service.getHeartbeats()).containsOnly(serviceHeartbeatBack);
        assertThat(serviceHeartbeatBack.getService()).isEqualTo(service);

        service.setHeartbeats(new HashSet<>());
        assertThat(service.getHeartbeats()).doesNotContain(serviceHeartbeatBack);
        assertThat(serviceHeartbeatBack.getService()).isNull();
    }

    @Test
    void datacenterTest() {
        Service service = getServiceRandomSampleGenerator();
        Datacenter datacenterBack = getDatacenterRandomSampleGenerator();

        service.setDatacenter(datacenterBack);
        assertThat(service.getDatacenter()).isEqualTo(datacenterBack);

        service.datacenter(null);
        assertThat(service.getDatacenter()).isNull();
    }
}
