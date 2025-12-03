package vibhuvi.oio.inframirror.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import vibhuvi.oio.inframirror.web.rest.TestUtil;

class HttpHeartbeatDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(HttpHeartbeatDTO.class);
        HttpHeartbeatDTO httpHeartbeatDTO1 = new HttpHeartbeatDTO();
        httpHeartbeatDTO1.setId(1L);
        HttpHeartbeatDTO httpHeartbeatDTO2 = new HttpHeartbeatDTO();
        assertThat(httpHeartbeatDTO1).isNotEqualTo(httpHeartbeatDTO2);
        httpHeartbeatDTO2.setId(httpHeartbeatDTO1.getId());
        assertThat(httpHeartbeatDTO1).isEqualTo(httpHeartbeatDTO2);
        httpHeartbeatDTO2.setId(2L);
        assertThat(httpHeartbeatDTO1).isNotEqualTo(httpHeartbeatDTO2);
        httpHeartbeatDTO1.setId(null);
        assertThat(httpHeartbeatDTO1).isNotEqualTo(httpHeartbeatDTO2);
    }
}
