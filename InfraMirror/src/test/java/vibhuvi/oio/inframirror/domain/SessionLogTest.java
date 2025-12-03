package vibhuvi.oio.inframirror.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static vibhuvi.oio.inframirror.domain.AgentTestSamples.*;
import static vibhuvi.oio.inframirror.domain.InstanceTestSamples.*;
import static vibhuvi.oio.inframirror.domain.SessionLogTestSamples.*;

import org.junit.jupiter.api.Test;
import vibhuvi.oio.inframirror.web.rest.TestUtil;

class SessionLogTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SessionLog.class);
        SessionLog sessionLog1 = getSessionLogSample1();
        SessionLog sessionLog2 = new SessionLog();
        assertThat(sessionLog1).isNotEqualTo(sessionLog2);

        sessionLog2.setId(sessionLog1.getId());
        assertThat(sessionLog1).isEqualTo(sessionLog2);

        sessionLog2 = getSessionLogSample2();
        assertThat(sessionLog1).isNotEqualTo(sessionLog2);
    }

    @Test
    void instanceTest() {
        SessionLog sessionLog = getSessionLogRandomSampleGenerator();
        Instance instanceBack = getInstanceRandomSampleGenerator();

        sessionLog.setInstance(instanceBack);
        assertThat(sessionLog.getInstance()).isEqualTo(instanceBack);

        sessionLog.instance(null);
        assertThat(sessionLog.getInstance()).isNull();
    }

    @Test
    void agentTest() {
        SessionLog sessionLog = getSessionLogRandomSampleGenerator();
        Agent agentBack = getAgentRandomSampleGenerator();

        sessionLog.setAgent(agentBack);
        assertThat(sessionLog.getAgent()).isEqualTo(agentBack);

        sessionLog.agent(null);
        assertThat(sessionLog.getAgent()).isNull();
    }
}
