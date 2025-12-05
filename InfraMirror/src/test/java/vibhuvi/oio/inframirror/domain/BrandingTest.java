package vibhuvi.oio.inframirror.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static vibhuvi.oio.inframirror.domain.BrandingTestSamples.*;

import org.junit.jupiter.api.Test;
import vibhuvi.oio.inframirror.web.rest.TestUtil;

class BrandingTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Branding.class);
        Branding branding1 = getBrandingSample1();
        Branding branding2 = new Branding();
        assertThat(branding1).isNotEqualTo(branding2);

        branding2.setId(branding1.getId());
        assertThat(branding1).isEqualTo(branding2);

        branding2 = getBrandingSample2();
        assertThat(branding1).isNotEqualTo(branding2);
    }
}
