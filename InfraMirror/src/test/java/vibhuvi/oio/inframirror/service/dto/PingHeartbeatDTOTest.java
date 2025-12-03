package vibhuvi.oio.inframirror.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import vibhuvi.oio.inframirror.web.rest.TestUtil;

class PingHeartbeatDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PingHeartbeatDTO.class);
        PingHeartbeatDTO pingHeartbeatDTO1 = new PingHeartbeatDTO();
        pingHeartbeatDTO1.setId(1L);
        PingHeartbeatDTO pingHeartbeatDTO2 = new PingHeartbeatDTO();
        assertThat(pingHeartbeatDTO1).isNotEqualTo(pingHeartbeatDTO2);
        pingHeartbeatDTO2.setId(pingHeartbeatDTO1.getId());
        assertThat(pingHeartbeatDTO1).isEqualTo(pingHeartbeatDTO2);
        pingHeartbeatDTO2.setId(2L);
        assertThat(pingHeartbeatDTO1).isNotEqualTo(pingHeartbeatDTO2);
        pingHeartbeatDTO1.setId(null);
        assertThat(pingHeartbeatDTO1).isNotEqualTo(pingHeartbeatDTO2);
    }
}
