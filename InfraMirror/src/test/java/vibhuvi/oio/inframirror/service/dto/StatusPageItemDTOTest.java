package vibhuvi.oio.inframirror.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import vibhuvi.oio.inframirror.web.rest.TestUtil;

class StatusPageItemDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(StatusPageItemDTO.class);
        StatusPageItemDTO statusPageItemDTO1 = new StatusPageItemDTO();
        statusPageItemDTO1.setId(1L);
        StatusPageItemDTO statusPageItemDTO2 = new StatusPageItemDTO();
        assertThat(statusPageItemDTO1).isNotEqualTo(statusPageItemDTO2);
        statusPageItemDTO2.setId(statusPageItemDTO1.getId());
        assertThat(statusPageItemDTO1).isEqualTo(statusPageItemDTO2);
        statusPageItemDTO2.setId(2L);
        assertThat(statusPageItemDTO1).isNotEqualTo(statusPageItemDTO2);
        statusPageItemDTO1.setId(null);
        assertThat(statusPageItemDTO1).isNotEqualTo(statusPageItemDTO2);
    }
}
