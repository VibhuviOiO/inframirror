package vibhuvi.oio.inframirror.service.mapper;

import static vibhuvi.oio.inframirror.domain.InstanceHeartbeatAsserts.*;
import static vibhuvi.oio.inframirror.domain.InstanceHeartbeatTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InstanceHeartbeatMapperTest {

    private InstanceHeartbeatMapper instanceHeartbeatMapper;

    @BeforeEach
    void setUp() {
        instanceHeartbeatMapper = new InstanceHeartbeatMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getInstanceHeartbeatSample1();
        var actual = instanceHeartbeatMapper.toEntity(instanceHeartbeatMapper.toDto(expected));
        assertInstanceHeartbeatAllPropertiesEquals(expected, actual);
    }
}
