package vibhuvi.oio.inframirror.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import vibhuvi.oio.inframirror.web.rest.TestUtil;

class HttpMonitorDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(HttpMonitorDTO.class);
        HttpMonitorDTO httpMonitorDTO1 = new HttpMonitorDTO();
        httpMonitorDTO1.setId(1L);
        HttpMonitorDTO httpMonitorDTO2 = new HttpMonitorDTO();
        assertThat(httpMonitorDTO1).isNotEqualTo(httpMonitorDTO2);
        httpMonitorDTO2.setId(httpMonitorDTO1.getId());
        assertThat(httpMonitorDTO1).isEqualTo(httpMonitorDTO2);
        httpMonitorDTO2.setId(2L);
        assertThat(httpMonitorDTO1).isNotEqualTo(httpMonitorDTO2);
        httpMonitorDTO1.setId(null);
        assertThat(httpMonitorDTO1).isNotEqualTo(httpMonitorDTO2);
    }
}
