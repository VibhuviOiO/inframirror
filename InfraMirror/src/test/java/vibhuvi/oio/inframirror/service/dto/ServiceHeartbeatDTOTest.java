package vibhuvi.oio.inframirror.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import vibhuvi.oio.inframirror.web.rest.TestUtil;

class ServiceHeartbeatDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ServiceHeartbeatDTO.class);
        ServiceHeartbeatDTO serviceHeartbeatDTO1 = new ServiceHeartbeatDTO();
        serviceHeartbeatDTO1.setId(1L);
        ServiceHeartbeatDTO serviceHeartbeatDTO2 = new ServiceHeartbeatDTO();
        assertThat(serviceHeartbeatDTO1).isNotEqualTo(serviceHeartbeatDTO2);
        serviceHeartbeatDTO2.setId(serviceHeartbeatDTO1.getId());
        assertThat(serviceHeartbeatDTO1).isEqualTo(serviceHeartbeatDTO2);
        serviceHeartbeatDTO2.setId(2L);
        assertThat(serviceHeartbeatDTO1).isNotEqualTo(serviceHeartbeatDTO2);
        serviceHeartbeatDTO1.setId(null);
        assertThat(serviceHeartbeatDTO1).isNotEqualTo(serviceHeartbeatDTO2);
    }
}
