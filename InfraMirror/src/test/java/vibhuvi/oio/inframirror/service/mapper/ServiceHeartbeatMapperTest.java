package vibhuvi.oio.inframirror.service.mapper;

import static vibhuvi.oio.inframirror.domain.ServiceHeartbeatAsserts.*;
import static vibhuvi.oio.inframirror.domain.ServiceHeartbeatTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServiceHeartbeatMapperTest {

    private ServiceHeartbeatMapper serviceHeartbeatMapper;

    @BeforeEach
    void setUp() {
        serviceHeartbeatMapper = new ServiceHeartbeatMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getServiceHeartbeatSample1();
        var actual = serviceHeartbeatMapper.toEntity(serviceHeartbeatMapper.toDto(expected));
        assertServiceHeartbeatAllPropertiesEquals(expected, actual);
    }
}
