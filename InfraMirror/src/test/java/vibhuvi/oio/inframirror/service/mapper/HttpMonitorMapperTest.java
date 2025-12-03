package vibhuvi.oio.inframirror.service.mapper;

import static vibhuvi.oio.inframirror.domain.HttpMonitorAsserts.*;
import static vibhuvi.oio.inframirror.domain.HttpMonitorTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HttpMonitorMapperTest {

    private HttpMonitorMapper httpMonitorMapper;

    @BeforeEach
    void setUp() {
        httpMonitorMapper = new HttpMonitorMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getHttpMonitorSample1();
        var actual = httpMonitorMapper.toEntity(httpMonitorMapper.toDto(expected));
        assertHttpMonitorAllPropertiesEquals(expected, actual);
    }
}
