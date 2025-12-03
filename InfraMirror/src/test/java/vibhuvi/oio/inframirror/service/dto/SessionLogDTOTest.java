package vibhuvi.oio.inframirror.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import vibhuvi.oio.inframirror.web.rest.TestUtil;

class SessionLogDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SessionLogDTO.class);
        SessionLogDTO sessionLogDTO1 = new SessionLogDTO();
        sessionLogDTO1.setId(1L);
        SessionLogDTO sessionLogDTO2 = new SessionLogDTO();
        assertThat(sessionLogDTO1).isNotEqualTo(sessionLogDTO2);
        sessionLogDTO2.setId(sessionLogDTO1.getId());
        assertThat(sessionLogDTO1).isEqualTo(sessionLogDTO2);
        sessionLogDTO2.setId(2L);
        assertThat(sessionLogDTO1).isNotEqualTo(sessionLogDTO2);
        sessionLogDTO1.setId(null);
        assertThat(sessionLogDTO1).isNotEqualTo(sessionLogDTO2);
    }
}
