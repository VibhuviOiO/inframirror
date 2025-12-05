package vibhuvi.oio.inframirror.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static vibhuvi.oio.inframirror.domain.AgentTestSamples.*;
import static vibhuvi.oio.inframirror.domain.DatacenterTestSamples.*;
import static vibhuvi.oio.inframirror.domain.RegionTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import vibhuvi.oio.inframirror.web.rest.TestUtil;

class RegionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Region.class);
        Region region1 = getRegionSample1();
        Region region2 = new Region();
        assertThat(region1).isNotEqualTo(region2);

        region2.setId(region1.getId());
        assertThat(region1).isEqualTo(region2);

        region2 = getRegionSample2();
        assertThat(region1).isNotEqualTo(region2);
    }

    @Test
    void datacenterTest() {
        Region region = getRegionRandomSampleGenerator();
        Datacenter datacenterBack = getDatacenterRandomSampleGenerator();

        region.addDatacenter(datacenterBack);
        assertThat(region.getDatacenters()).containsOnly(datacenterBack);
        assertThat(datacenterBack.getRegion()).isEqualTo(region);

        region.removeDatacenter(datacenterBack);
        assertThat(region.getDatacenters()).doesNotContain(datacenterBack);
        assertThat(datacenterBack.getRegion()).isNull();

        region.datacenters(new HashSet<>(Set.of(datacenterBack)));
        assertThat(region.getDatacenters()).containsOnly(datacenterBack);
        assertThat(datacenterBack.getRegion()).isEqualTo(region);

        region.setDatacenters(new HashSet<>());
        assertThat(region.getDatacenters()).doesNotContain(datacenterBack);
        assertThat(datacenterBack.getRegion()).isNull();
    }

    @Test
    void agentTest() {
        Region region = getRegionRandomSampleGenerator();
        Agent agentBack = getAgentRandomSampleGenerator();

        region.addAgent(agentBack);
        assertThat(region.getAgents()).containsOnly(agentBack);
        assertThat(agentBack.getRegion()).isEqualTo(region);

        region.removeAgent(agentBack);
        assertThat(region.getAgents()).doesNotContain(agentBack);
        assertThat(agentBack.getRegion()).isNull();

        region.agents(new HashSet<>(Set.of(agentBack)));
        assertThat(region.getAgents()).containsOnly(agentBack);
        assertThat(agentBack.getRegion()).isEqualTo(region);

        region.setAgents(new HashSet<>());
        assertThat(region.getAgents()).doesNotContain(agentBack);
        assertThat(agentBack.getRegion()).isNull();
    }
}
