package vibhuvi.oio.inframirror.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import vibhuvi.oio.inframirror.web.rest.TestUtil;

class AgentMonitorDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AgentMonitorDTO.class);
        AgentMonitorDTO agentMonitorDTO1 = new AgentMonitorDTO();
        agentMonitorDTO1.setId(1L);
        AgentMonitorDTO agentMonitorDTO2 = new AgentMonitorDTO();
        assertThat(agentMonitorDTO1).isNotEqualTo(agentMonitorDTO2);
        agentMonitorDTO2.setId(agentMonitorDTO1.getId());
        assertThat(agentMonitorDTO1).isEqualTo(agentMonitorDTO2);
        agentMonitorDTO2.setId(2L);
        assertThat(agentMonitorDTO1).isNotEqualTo(agentMonitorDTO2);
        agentMonitorDTO1.setId(null);
        assertThat(agentMonitorDTO1).isNotEqualTo(agentMonitorDTO2);
    }
}
