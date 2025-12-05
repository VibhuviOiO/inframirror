package vibhuvi.oio.inframirror.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static vibhuvi.oio.inframirror.domain.StatusDependencyTestSamples.*;
import static vibhuvi.oio.inframirror.domain.StatusPageItemTestSamples.*;
import static vibhuvi.oio.inframirror.domain.StatusPageTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import vibhuvi.oio.inframirror.web.rest.TestUtil;

class StatusPageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StatusPage.class);
        StatusPage statusPage1 = getStatusPageSample1();
        StatusPage statusPage2 = new StatusPage();
        assertThat(statusPage1).isNotEqualTo(statusPage2);

        statusPage2.setId(statusPage1.getId());
        assertThat(statusPage1).isEqualTo(statusPage2);

        statusPage2 = getStatusPageSample2();
        assertThat(statusPage1).isNotEqualTo(statusPage2);
    }

    @Test
    void itemTest() {
        StatusPage statusPage = getStatusPageRandomSampleGenerator();
        StatusPageItem statusPageItemBack = getStatusPageItemRandomSampleGenerator();

        statusPage.addItem(statusPageItemBack);
        assertThat(statusPage.getItems()).containsOnly(statusPageItemBack);
        assertThat(statusPageItemBack.getStatusPage()).isEqualTo(statusPage);

        statusPage.removeItem(statusPageItemBack);
        assertThat(statusPage.getItems()).doesNotContain(statusPageItemBack);
        assertThat(statusPageItemBack.getStatusPage()).isNull();

        statusPage.items(new HashSet<>(Set.of(statusPageItemBack)));
        assertThat(statusPage.getItems()).containsOnly(statusPageItemBack);
        assertThat(statusPageItemBack.getStatusPage()).isEqualTo(statusPage);

        statusPage.setItems(new HashSet<>());
        assertThat(statusPage.getItems()).doesNotContain(statusPageItemBack);
        assertThat(statusPageItemBack.getStatusPage()).isNull();
    }

    @Test
    void statusDependencyTest() {
        StatusPage statusPage = getStatusPageRandomSampleGenerator();
        StatusDependency statusDependencyBack = getStatusDependencyRandomSampleGenerator();

        statusPage.addStatusDependency(statusDependencyBack);
        assertThat(statusPage.getStatusDependencies()).containsOnly(statusDependencyBack);
        assertThat(statusDependencyBack.getStatusPage()).isEqualTo(statusPage);

        statusPage.removeStatusDependency(statusDependencyBack);
        assertThat(statusPage.getStatusDependencies()).doesNotContain(statusDependencyBack);
        assertThat(statusDependencyBack.getStatusPage()).isNull();

        statusPage.statusDependencies(new HashSet<>(Set.of(statusDependencyBack)));
        assertThat(statusPage.getStatusDependencies()).containsOnly(statusDependencyBack);
        assertThat(statusDependencyBack.getStatusPage()).isEqualTo(statusPage);

        statusPage.setStatusDependencies(new HashSet<>());
        assertThat(statusPage.getStatusDependencies()).doesNotContain(statusDependencyBack);
        assertThat(statusDependencyBack.getStatusPage()).isNull();
    }
}
