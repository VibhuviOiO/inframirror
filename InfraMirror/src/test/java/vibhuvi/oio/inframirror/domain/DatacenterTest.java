package vibhuvi.oio.inframirror.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static vibhuvi.oio.inframirror.domain.AgentTestSamples.*;
import static vibhuvi.oio.inframirror.domain.DatacenterTestSamples.*;
import static vibhuvi.oio.inframirror.domain.InstanceTestSamples.*;
import static vibhuvi.oio.inframirror.domain.RegionTestSamples.*;

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
    void agentsTest() {
        Datacenter datacenter = getDatacenterRandomSampleGenerator();
        Agent agentBack = getAgentRandomSampleGenerator();

        datacenter.addAgents(agentBack);
        assertThat(datacenter.getAgents()).containsOnly(agentBack);
        assertThat(agentBack.getDatacenter()).isEqualTo(datacenter);

        datacenter.removeAgents(agentBack);
        assertThat(datacenter.getAgents()).doesNotContain(agentBack);
        assertThat(agentBack.getDatacenter()).isNull();

        datacenter.agents(new HashSet<>(Set.of(agentBack)));
        assertThat(datacenter.getAgents()).containsOnly(agentBack);
        assertThat(agentBack.getDatacenter()).isEqualTo(datacenter);

        datacenter.setAgents(new HashSet<>());
        assertThat(datacenter.getAgents()).doesNotContain(agentBack);
        assertThat(agentBack.getDatacenter()).isNull();
    }

    @Test
    void instancesTest() {
        Datacenter datacenter = getDatacenterRandomSampleGenerator();
        Instance instanceBack = getInstanceRandomSampleGenerator();

        datacenter.addInstances(instanceBack);
        assertThat(datacenter.getInstances()).containsOnly(instanceBack);
        assertThat(instanceBack.getDatacenter()).isEqualTo(datacenter);

        datacenter.removeInstances(instanceBack);
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
    void regionTest() {
        Datacenter datacenter = getDatacenterRandomSampleGenerator();
        Region regionBack = getRegionRandomSampleGenerator();

        datacenter.setRegion(regionBack);
        assertThat(datacenter.getRegion()).isEqualTo(regionBack);

        datacenter.region(null);
        assertThat(datacenter.getRegion()).isNull();
    }
}
