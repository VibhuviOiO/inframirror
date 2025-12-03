package vibhuvi.oio.inframirror.domain;

import static org.assertj.core.api.Assertions.assertThat;
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
    void datacentersTest() {
        Region region = getRegionRandomSampleGenerator();
        Datacenter datacenterBack = getDatacenterRandomSampleGenerator();

        region.addDatacenters(datacenterBack);
        assertThat(region.getDatacenters()).containsOnly(datacenterBack);
        assertThat(datacenterBack.getRegion()).isEqualTo(region);

        region.removeDatacenters(datacenterBack);
        assertThat(region.getDatacenters()).doesNotContain(datacenterBack);
        assertThat(datacenterBack.getRegion()).isNull();

        region.datacenters(new HashSet<>(Set.of(datacenterBack)));
        assertThat(region.getDatacenters()).containsOnly(datacenterBack);
        assertThat(datacenterBack.getRegion()).isEqualTo(region);

        region.setDatacenters(new HashSet<>());
        assertThat(region.getDatacenters()).doesNotContain(datacenterBack);
        assertThat(datacenterBack.getRegion()).isNull();
    }
}
