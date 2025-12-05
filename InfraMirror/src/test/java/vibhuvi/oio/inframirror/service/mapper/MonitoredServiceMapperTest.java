package vibhuvi.oio.inframirror.service.mapper;

import static vibhuvi.oio.inframirror.domain.MonitoredServiceAsserts.*;
import static vibhuvi.oio.inframirror.domain.MonitoredServiceTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MonitoredServiceMapperTest {

    private MonitoredServiceMapper monitoredServiceMapper;

    @BeforeEach
    void setUp() {
        monitoredServiceMapper = new MonitoredServiceMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMonitoredServiceSample1();
        var actual = monitoredServiceMapper.toEntity(monitoredServiceMapper.toDto(expected));
        assertMonitoredServiceAllPropertiesEquals(expected, actual);
    }
}
