package vibhuvi.oio.inframirror.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static vibhuvi.oio.inframirror.domain.AgentMonitorTestSamples.*;
import static vibhuvi.oio.inframirror.domain.AgentTestSamples.*;
import static vibhuvi.oio.inframirror.domain.HttpMonitorTestSamples.*;

import org.junit.jupiter.api.Test;
import vibhuvi.oio.inframirror.web.rest.TestUtil;

class AgentMonitorTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AgentMonitor.class);
        AgentMonitor agentMonitor1 = getAgentMonitorSample1();
        AgentMonitor agentMonitor2 = new AgentMonitor();
        assertThat(agentMonitor1).isNotEqualTo(agentMonitor2);

        agentMonitor2.setId(agentMonitor1.getId());
        assertThat(agentMonitor1).isEqualTo(agentMonitor2);

        agentMonitor2 = getAgentMonitorSample2();
        assertThat(agentMonitor1).isNotEqualTo(agentMonitor2);
    }

    @Test
    void agentTest() {
        AgentMonitor agentMonitor = getAgentMonitorRandomSampleGenerator();
        Agent agentBack = getAgentRandomSampleGenerator();

        agentMonitor.setAgent(agentBack);
        assertThat(agentMonitor.getAgent()).isEqualTo(agentBack);

        agentMonitor.agent(null);
        assertThat(agentMonitor.getAgent()).isNull();
    }

    @Test
    void monitorTest() {
        AgentMonitor agentMonitor = getAgentMonitorRandomSampleGenerator();
        HttpMonitor httpMonitorBack = getHttpMonitorRandomSampleGenerator();

        agentMonitor.setMonitor(httpMonitorBack);
        assertThat(agentMonitor.getMonitor()).isEqualTo(httpMonitorBack);

        agentMonitor.monitor(null);
        assertThat(agentMonitor.getMonitor()).isNull();
    }
}
