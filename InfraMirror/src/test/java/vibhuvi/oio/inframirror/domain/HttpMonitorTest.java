package vibhuvi.oio.inframirror.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static vibhuvi.oio.inframirror.domain.HttpHeartbeatTestSamples.*;
import static vibhuvi.oio.inframirror.domain.HttpMonitorTestSamples.*;
import static vibhuvi.oio.inframirror.domain.ScheduleTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import vibhuvi.oio.inframirror.web.rest.TestUtil;

class HttpMonitorTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(HttpMonitor.class);
        HttpMonitor httpMonitor1 = getHttpMonitorSample1();
        HttpMonitor httpMonitor2 = new HttpMonitor();
        assertThat(httpMonitor1).isNotEqualTo(httpMonitor2);

        httpMonitor2.setId(httpMonitor1.getId());
        assertThat(httpMonitor1).isEqualTo(httpMonitor2);

        httpMonitor2 = getHttpMonitorSample2();
        assertThat(httpMonitor1).isNotEqualTo(httpMonitor2);
    }

    @Test
    void heartbeatsTest() {
        HttpMonitor httpMonitor = getHttpMonitorRandomSampleGenerator();
        HttpHeartbeat httpHeartbeatBack = getHttpHeartbeatRandomSampleGenerator();

        httpMonitor.addHeartbeats(httpHeartbeatBack);
        assertThat(httpMonitor.getHeartbeats()).containsOnly(httpHeartbeatBack);
        assertThat(httpHeartbeatBack.getMonitor()).isEqualTo(httpMonitor);

        httpMonitor.removeHeartbeats(httpHeartbeatBack);
        assertThat(httpMonitor.getHeartbeats()).doesNotContain(httpHeartbeatBack);
        assertThat(httpHeartbeatBack.getMonitor()).isNull();

        httpMonitor.heartbeats(new HashSet<>(Set.of(httpHeartbeatBack)));
        assertThat(httpMonitor.getHeartbeats()).containsOnly(httpHeartbeatBack);
        assertThat(httpHeartbeatBack.getMonitor()).isEqualTo(httpMonitor);

        httpMonitor.setHeartbeats(new HashSet<>());
        assertThat(httpMonitor.getHeartbeats()).doesNotContain(httpHeartbeatBack);
        assertThat(httpHeartbeatBack.getMonitor()).isNull();
    }

    @Test
    void scheduleTest() {
        HttpMonitor httpMonitor = getHttpMonitorRandomSampleGenerator();
        Schedule scheduleBack = getScheduleRandomSampleGenerator();

        httpMonitor.setSchedule(scheduleBack);
        assertThat(httpMonitor.getSchedule()).isEqualTo(scheduleBack);

        httpMonitor.schedule(null);
        assertThat(httpMonitor.getSchedule()).isNull();
    }
}
