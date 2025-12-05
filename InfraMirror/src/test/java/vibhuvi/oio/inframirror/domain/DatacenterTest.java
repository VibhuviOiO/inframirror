package vibhuvi.oio.inframirror.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static vibhuvi.oio.inframirror.domain.DatacenterTestSamples.*;
import static vibhuvi.oio.inframirror.domain.InstanceTestSamples.*;
import static vibhuvi.oio.inframirror.domain.RegionTestSamples.*;
import static vibhuvi.oio.inframirror.domain.ServiceTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import vibhuvi.oio.inframirror.web.rest.TestUtil;

class DatacenterTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Datacenter.class);
        Datacenter datacenter1 = getDatacenterSample1();
        Datacenter datacenter2 = new Datacenter();
        assertThat(datacenter1).isNotEqualTo(datacenter2);

        datacenter2.setId(datacenter1.getId());
        assertThat(datacenter1).isEqualTo(datacenter2);

        datacenter2 = getDatacenterSample2();
        assertThat(datacenter1).isNotEqualTo(datacenter2);
    }

    @Test
    void instanceTest() {
        Datacenter datacenter = getDatacenterRandomSampleGenerator();
        Instance instanceBack = getInstanceRandomSampleGenerator();

        datacenter.addInstance(instanceBack);
        assertThat(datacenter.getInstances()).containsOnly(instanceBack);
        assertThat(instanceBack.getDatacenter()).isEqualTo(datacenter);

        datacenter.removeInstance(instanceBack);
        assertThat(datacenter.getInstances()).doesNotContain(instanceBack);
        assertThat(instanceBack.getDatacenter()).isNull();

        datacenter.instances(new HashSet<>(Set.of(instanceBack)));
        assertThat(datacenter.getInstances()).containsOnly(instanceBack);
        assertThat(instanceBack.getDatacenter()).isEqualTo(datacenter);

        datacenter.setInstances(new HashSet<>());
        assertThat(datacenter.getInstances()).doesNotContain(instanceBack);
        assertThat(instanceBack.getDatacenter()).isNull();
    }

    @Test
    void serviceTest() {
        Datacenter datacenter = getDatacenterRandomSampleGenerator();
        Service serviceBack = getServiceRandomSampleGenerator();

        datacenter.addService(serviceBack);
        assertThat(datacenter.getServices()).containsOnly(serviceBack);
        assertThat(serviceBack.getDatacenter()).isEqualTo(datacenter);

        datacenter.removeService(serviceBack);
        assertThat(datacenter.getServices()).doesNotContain(serviceBack);
        assertThat(serviceBack.getDatacenter()).isNull();

        datacenter.services(new HashSet<>(Set.of(serviceBack)));
        assertThat(datacenter.getServices()).containsOnly(serviceBack);
        assertThat(serviceBack.getDatacenter()).isEqualTo(datacenter);

        datacenter.setServices(new HashSet<>());
        assertThat(datacenter.getServices()).doesNotContain(serviceBack);
        assertThat(serviceBack.getDatacenter()).isNull();
    }

    @Test
    void regionTest() {
        Datacenter datacenter = getDatacenterRandomSampleGenerator();
        Region regionBack = getRegionRandomSampleGenerator();

        datacenter.setRegion(regionBack);
        assertThat(datacenter.getRegion()).isEqualTo(regionBack);

        datacenter.region(null);
        assertThat(datacenter.getRegion()).isNull();
    }
}
