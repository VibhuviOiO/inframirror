package vibhuvi.oio.inframirror.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import vibhuvi.oio.inframirror.web.rest.TestUtil;

class InstanceHeartbeatDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(InstanceHeartbeatDTO.class);
        InstanceHeartbeatDTO instanceHeartbeatDTO1 = new InstanceHeartbeatDTO();
        instanceHeartbeatDTO1.setId(1L);
        InstanceHeartbeatDTO instanceHeartbeatDTO2 = new InstanceHeartbeatDTO();
        assertThat(instanceHeartbeatDTO1).isNotEqualTo(instanceHeartbeatDTO2);
        instanceHeartbeatDTO2.setId(instanceHeartbeatDTO1.getId());
        assertThat(instanceHeartbeatDTO1).isEqualTo(instanceHeartbeatDTO2);
        instanceHeartbeatDTO2.setId(2L);
        assertThat(instanceHeartbeatDTO1).isNotEqualTo(instanceHeartbeatDTO2);
        instanceHeartbeatDTO1.setId(null);
        assertThat(instanceHeartbeatDTO1).isNotEqualTo(instanceHeartbeatDTO2);
    }
}
