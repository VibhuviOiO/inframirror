package vibhuvi.oio.inframirror.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static vibhuvi.oio.inframirror.domain.StatusDependencyTestSamples.*;
import static vibhuvi.oio.inframirror.domain.StatusPageTestSamples.*;

import org.junit.jupiter.api.Test;
import vibhuvi.oio.inframirror.web.rest.TestUtil;

class StatusDependencyTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StatusDependency.class);
        StatusDependency statusDependency1 = getStatusDependencySample1();
        StatusDependency statusDependency2 = new StatusDependency();
        assertThat(statusDependency1).isNotEqualTo(statusDependency2);

        statusDependency2.setId(statusDependency1.getId());
        assertThat(statusDependency1).isEqualTo(statusDependency2);

        statusDependency2 = getStatusDependencySample2();
        assertThat(statusDependency1).isNotEqualTo(statusDependency2);
    }

    @Test
    void statusPageTest() {
        StatusDependency statusDependency = getStatusDependencyRandomSampleGenerator();
        StatusPage statusPageBack = getStatusPageRandomSampleGenerator();

        statusDependency.setStatusPage(statusPageBack);
        assertThat(statusDependency.getStatusPage()).isEqualTo(statusPageBack);

        statusDependency.statusPage(null);
        assertThat(statusDependency.getStatusPage()).isNull();
    }
}
