package vibhuvi.oio.inframirror.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import vibhuvi.oio.inframirror.web.rest.TestUtil;

class StatusPageDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(StatusPageDTO.class);
        StatusPageDTO statusPageDTO1 = new StatusPageDTO();
        statusPageDTO1.setId(1L);
        StatusPageDTO statusPageDTO2 = new StatusPageDTO();
        assertThat(statusPageDTO1).isNotEqualTo(statusPageDTO2);
        statusPageDTO2.setId(statusPageDTO1.getId());
        assertThat(statusPageDTO1).isEqualTo(statusPageDTO2);
        statusPageDTO2.setId(2L);
        assertThat(statusPageDTO1).isNotEqualTo(statusPageDTO2);
        statusPageDTO1.setId(null);
        assertThat(statusPageDTO1).isNotEqualTo(statusPageDTO2);
    }
}
