package vibhuvi.oio.inframirror.service.mapper;

import static vibhuvi.oio.inframirror.domain.PingHeartbeatAsserts.*;
import static vibhuvi.oio.inframirror.domain.PingHeartbeatTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PingHeartbeatMapperTest {

    private PingHeartbeatMapper pingHeartbeatMapper;

    @BeforeEach
    void setUp() {
        pingHeartbeatMapper = new PingHeartbeatMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPingHeartbeatSample1();
        var actual = pingHeartbeatMapper.toEntity(pingHeartbeatMapper.toDto(expected));
        assertPingHeartbeatAllPropertiesEquals(expected, actual);
    }
}
