package vibhuvi.oio.inframirror.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import vibhuvi.oio.inframirror.web.rest.TestUtil;

class AgentLockDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AgentLockDTO.class);
        AgentLockDTO agentLockDTO1 = new AgentLockDTO();
        agentLockDTO1.setId(1L);
        AgentLockDTO agentLockDTO2 = new AgentLockDTO();
        assertThat(agentLockDTO1).isNotEqualTo(agentLockDTO2);
        agentLockDTO2.setId(agentLockDTO1.getId());
        assertThat(agentLockDTO1).isEqualTo(agentLockDTO2);
        agentLockDTO2.setId(2L);
        assertThat(agentLockDTO1).isNotEqualTo(agentLockDTO2);
        agentLockDTO1.setId(null);
        assertThat(agentLockDTO1).isNotEqualTo(agentLockDTO2);
    }
}
