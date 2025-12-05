package vibhuvi.oio.inframirror.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static vibhuvi.oio.inframirror.domain.StatusPageItemTestSamples.*;
import static vibhuvi.oio.inframirror.domain.StatusPageTestSamples.*;

import org.junit.jupiter.api.Test;
import vibhuvi.oio.inframirror.web.rest.TestUtil;

class StatusPageItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StatusPageItem.class);
        StatusPageItem statusPageItem1 = getStatusPageItemSample1();
        StatusPageItem statusPageItem2 = new StatusPageItem();
        assertThat(statusPageItem1).isNotEqualTo(statusPageItem2);

        statusPageItem2.setId(statusPageItem1.getId());
        assertThat(statusPageItem1).isEqualTo(statusPageItem2);

        statusPageItem2 = getStatusPageItemSample2();
        assertThat(statusPageItem1).isNotEqualTo(statusPageItem2);
    }

    @Test
    void statusPageTest() {
        StatusPageItem statusPageItem = getStatusPageItemRandomSampleGenerator();
        StatusPage statusPageBack = getStatusPageRandomSampleGenerator();

        statusPageItem.setStatusPage(statusPageBack);
        assertThat(statusPageItem.getStatusPage()).isEqualTo(statusPageBack);

        statusPageItem.statusPage(null);
        assertThat(statusPageItem.getStatusPage()).isNull();
    }
}
