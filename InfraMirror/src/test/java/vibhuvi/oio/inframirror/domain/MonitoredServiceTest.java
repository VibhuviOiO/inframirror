package vibhuvi.oio.inframirror.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static vibhuvi.oio.inframirror.domain.DatacenterTestSamples.*;
import static vibhuvi.oio.inframirror.domain.MonitoredServiceTestSamples.*;
import static vibhuvi.oio.inframirror.domain.ServiceHeartbeatTestSamples.*;
import static vibhuvi.oio.inframirror.domain.ServiceInstanceTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import vibhuvi.oio.inframirror.web.rest.TestUtil;

class MonitoredServiceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MonitoredService.class);
        MonitoredService monitoredService1 = getMonitoredServiceSample1();
        MonitoredService monitoredService2 = new MonitoredService();
        assertThat(monitoredService1).isNotEqualTo(monitoredService2);

        monitoredService2.setId(monitoredService1.getId());
        assertThat(monitoredService1).isEqualTo(monitoredService2);

        monitoredService2 = getMonitoredServiceSample2();
        assertThat(monitoredService1).isNotEqualTo(monitoredService2);
    }

    @Test
    void serviceInstanceTest() {
        MonitoredService monitoredService = getMonitoredServiceRandomSampleGenerator();
        ServiceInstance serviceInstanceBack = getServiceInstanceRandomSampleGenerator();

        monitoredService.addServiceInstance(serviceInstanceBack);
        assertThat(monitoredService.getServiceInstances()).containsOnly(serviceInstanceBack);
        assertThat(serviceInstanceBack.getMonitoredService()).isEqualTo(monitoredService);

        monitoredService.removeServiceInstance(serviceInstanceBack);
        assertThat(monitoredService.getServiceInstances()).doesNotContain(serviceInstanceBack);
        assertThat(serviceInstanceBack.getMonitoredService()).isNull();

        monitoredService.serviceInstances(new HashSet<>(Set.of(serviceInstanceBack)));
        assertThat(monitoredService.getServiceInstances()).containsOnly(serviceInstanceBack);
        assertThat(serviceInstanceBack.getMonitoredService()).isEqualTo(monitoredService);

        monitoredService.setServiceInstances(new HashSet<>());
        assertThat(monitoredService.getServiceInstances()).doesNotContain(serviceInstanceBack);
        assertThat(serviceInstanceBack.getMonitoredService()).isNull();
    }

    @Test
    void heartbeatTest() {
        MonitoredService monitoredService = getMonitoredServiceRandomSampleGenerator();
        ServiceHeartbeat serviceHeartbeatBack = getServiceHeartbeatRandomSampleGenerator();

        monitoredService.addHeartbeat(serviceHeartbeatBack);
        assertThat(monitoredService.getHeartbeats()).containsOnly(serviceHeartbeatBack);
        assertThat(serviceHeartbeatBack.getMonitoredService()).isEqualTo(monitoredService);

        monitoredService.removeHeartbeat(serviceHeartbeatBack);
        assertThat(monitoredService.getHeartbeats()).doesNotContain(serviceHeartbeatBack);
        assertThat(serviceHeartbeatBack.getMonitoredService()).isNull();

        monitoredService.heartbeats(new HashSet<>(Set.of(serviceHeartbeatBack)));
        assertThat(monitoredService.getHeartbeats()).containsOnly(serviceHeartbeatBack);
        assertThat(serviceHeartbeatBack.getMonitoredService()).isEqualTo(monitoredService);

        monitoredService.setHeartbeats(new HashSet<>());
        assertThat(monitoredService.getHeartbeats()).doesNotContain(serviceHeartbeatBack);
        assertThat(serviceHeartbeatBack.getMonitoredService()).isNull();
    }

    @Test
    void datacenterTest() {
        MonitoredService monitoredService = getMonitoredServiceRandomSampleGenerator();
        Datacenter datacenterBack = getDatacenterRandomSampleGenerator();

        monitoredService.setDatacenter(datacenterBack);
        assertThat(monitoredService.getDatacenter()).isEqualTo(datacenterBack);

        monitoredService.datacenter(null);
        assertThat(monitoredService.getDatacenter()).isNull();
    }
}
