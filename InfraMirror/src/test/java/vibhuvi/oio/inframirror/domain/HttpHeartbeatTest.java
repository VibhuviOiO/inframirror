package vibhuvi.oio.inframirror.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static vibhuvi.oio.inframirror.domain.AgentTestSamples.*;
import static vibhuvi.oio.inframirror.domain.HttpHeartbeatTestSamples.*;
import static vibhuvi.oio.inframirror.domain.HttpMonitorTestSamples.*;

import org.junit.jupiter.api.Test;
import vibhuvi.oio.inframirror.web.rest.TestUtil;

class HttpHeartbeatTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(HttpHeartbeat.class);
        HttpHeartbeat httpHeartbeat1 = getHttpHeartbeatSample1();
        HttpHeartbeat httpHeartbeat2 = new HttpHeartbeat();
        assertThat(httpHeartbeat1).isNotEqualTo(httpHeartbeat2);

        httpHeartbeat2.setId(httpHeartbeat1.getId());
        assertThat(httpHeartbeat1).isEqualTo(httpHeartbeat2);

        httpHeartbeat2 = getHttpHeartbeatSample2();
        assertThat(httpHeartbeat1).isNotEqualTo(httpHeartbeat2);
    }

    @Test
    void monitorTest() {
        HttpHeartbeat httpHeartbeat = getHttpHeartbeatRandomSampleGenerator();
        HttpMonitor httpMonitorBack = getHttpMonitorRandomSampleGenerator();

        httpHeartbeat.setMonitor(httpMonitorBack);
        assertThat(httpHeartbeat.getMonitor()).isEqualTo(httpMonitorBack);

        httpHeartbeat.monitor(null);
        assertThat(httpHeartbeat.getMonitor()).isNull();
    }

    @Test
    void agentTest() {
        HttpHeartbeat httpHeartbeat = getHttpHeartbeatRandomSampleGenerator();
        Agent agentBack = getAgentRandomSampleGenerator();

        httpHeartbeat.setAgent(agentBack);
        assertThat(httpHeartbeat.getAgent()).isEqualTo(agentBack);

        httpHeartbeat.agent(null);
        assertThat(httpHeartbeat.getAgent()).isNull();
    }
}
