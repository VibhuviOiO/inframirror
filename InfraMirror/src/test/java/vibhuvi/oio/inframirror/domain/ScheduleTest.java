package vibhuvi.oio.inframirror.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static vibhuvi.oio.inframirror.domain.HttpMonitorTestSamples.*;
import static vibhuvi.oio.inframirror.domain.ScheduleTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import vibhuvi.oio.inframirror.web.rest.TestUtil;

class ScheduleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Schedule.class);
        Schedule schedule1 = getScheduleSample1();
        Schedule schedule2 = new Schedule();
        assertThat(schedule1).isNotEqualTo(schedule2);

        schedule2.setId(schedule1.getId());
        assertThat(schedule1).isEqualTo(schedule2);

        schedule2 = getScheduleSample2();
        assertThat(schedule1).isNotEqualTo(schedule2);
    }

    @Test
    void monitorsTest() {
        Schedule schedule = getScheduleRandomSampleGenerator();
        HttpMonitor httpMonitorBack = getHttpMonitorRandomSampleGenerator();

        schedule.addMonitors(httpMonitorBack);
        assertThat(schedule.getMonitors()).containsOnly(httpMonitorBack);
        assertThat(httpMonitorBack.getSchedule()).isEqualTo(schedule);

        schedule.removeMonitors(httpMonitorBack);
        assertThat(schedule.getMonitors()).doesNotContain(httpMonitorBack);
        assertThat(httpMonitorBack.getSchedule()).isNull();

        schedule.monitors(new HashSet<>(Set.of(httpMonitorBack)));
        assertThat(schedule.getMonitors()).containsOnly(httpMonitorBack);
        assertThat(httpMonitorBack.getSchedule()).isEqualTo(schedule);

        schedule.setMonitors(new HashSet<>());
        assertThat(schedule.getMonitors()).doesNotContain(httpMonitorBack);
        assertThat(httpMonitorBack.getSchedule()).isNull();
    }
}
