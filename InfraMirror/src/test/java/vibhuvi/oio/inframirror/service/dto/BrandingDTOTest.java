package vibhuvi.oio.inframirror.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import vibhuvi.oio.inframirror.web.rest.TestUtil;

class BrandingDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BrandingDTO.class);
        BrandingDTO brandingDTO1 = new BrandingDTO();
        brandingDTO1.setId(1L);
        BrandingDTO brandingDTO2 = new BrandingDTO();
        assertThat(brandingDTO1).isNotEqualTo(brandingDTO2);
        brandingDTO2.setId(brandingDTO1.getId());
        assertThat(brandingDTO1).isEqualTo(brandingDTO2);
        brandingDTO2.setId(2L);
        assertThat(brandingDTO1).isNotEqualTo(brandingDTO2);
        brandingDTO1.setId(null);
        assertThat(brandingDTO1).isNotEqualTo(brandingDTO2);
    }
}
