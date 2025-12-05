package vibhuvi.oio.inframirror.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import vibhuvi.oio.inframirror.web.rest.TestUtil;

class StatusDependencyDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(StatusDependencyDTO.class);
        StatusDependencyDTO statusDependencyDTO1 = new StatusDependencyDTO();
        statusDependencyDTO1.setId(1L);
        StatusDependencyDTO statusDependencyDTO2 = new StatusDependencyDTO();
        assertThat(statusDependencyDTO1).isNotEqualTo(statusDependencyDTO2);
        statusDependencyDTO2.setId(statusDependencyDTO1.getId());
        assertThat(statusDependencyDTO1).isEqualTo(statusDependencyDTO2);
        statusDependencyDTO2.setId(2L);
        assertThat(statusDependencyDTO1).isNotEqualTo(statusDependencyDTO2);
        statusDependencyDTO1.setId(null);
        assertThat(statusDependencyDTO1).isNotEqualTo(statusDependencyDTO2);
    }
}
