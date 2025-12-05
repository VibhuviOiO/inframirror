package vibhuvi.oio.inframirror.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import vibhuvi.oio.inframirror.web.rest.TestUtil;

class ServiceInstanceDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ServiceInstanceDTO.class);
        ServiceInstanceDTO serviceInstanceDTO1 = new ServiceInstanceDTO();
        serviceInstanceDTO1.setId(1L);
        ServiceInstanceDTO serviceInstanceDTO2 = new ServiceInstanceDTO();
        assertThat(serviceInstanceDTO1).isNotEqualTo(serviceInstanceDTO2);
        serviceInstanceDTO2.setId(serviceInstanceDTO1.getId());
        assertThat(serviceInstanceDTO1).isEqualTo(serviceInstanceDTO2);
        serviceInstanceDTO2.setId(2L);
        assertThat(serviceInstanceDTO1).isNotEqualTo(serviceInstanceDTO2);
        serviceInstanceDTO1.setId(null);
        assertThat(serviceInstanceDTO1).isNotEqualTo(serviceInstanceDTO2);
    }
}
