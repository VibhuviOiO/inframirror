package vibhuvi.oio.inframirror.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import vibhuvi.oio.inframirror.web.rest.TestUtil;

class MonitoredServiceDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MonitoredServiceDTO.class);
        MonitoredServiceDTO monitoredServiceDTO1 = new MonitoredServiceDTO();
        monitoredServiceDTO1.setId(1L);
        MonitoredServiceDTO monitoredServiceDTO2 = new MonitoredServiceDTO();
        assertThat(monitoredServiceDTO1).isNotEqualTo(monitoredServiceDTO2);
        monitoredServiceDTO2.setId(monitoredServiceDTO1.getId());
        assertThat(monitoredServiceDTO1).isEqualTo(monitoredServiceDTO2);
        monitoredServiceDTO2.setId(2L);
        assertThat(monitoredServiceDTO1).isNotEqualTo(monitoredServiceDTO2);
        monitoredServiceDTO1.setId(null);
        assertThat(monitoredServiceDTO1).isNotEqualTo(monitoredServiceDTO2);
    }
}
