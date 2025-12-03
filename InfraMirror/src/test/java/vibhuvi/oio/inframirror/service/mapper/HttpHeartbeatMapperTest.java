package vibhuvi.oio.inframirror.service.mapper;

import static vibhuvi.oio.inframirror.domain.HttpHeartbeatAsserts.*;
import static vibhuvi.oio.inframirror.domain.HttpHeartbeatTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HttpHeartbeatMapperTest {

    private HttpHeartbeatMapper httpHeartbeatMapper;

    @BeforeEach
    void setUp() {
        httpHeartbeatMapper = new HttpHeartbeatMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getHttpHeartbeatSample1();
        var actual = httpHeartbeatMapper.toEntity(httpHeartbeatMapper.toDto(expected));
        assertHttpHeartbeatAllPropertiesEquals(expected, actual);
    }
}
