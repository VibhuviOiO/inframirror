package vibhuvi.oio.inframirror.service.mapper;

import static vibhuvi.oio.inframirror.domain.ServiceInstanceAsserts.*;
import static vibhuvi.oio.inframirror.domain.ServiceInstanceTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServiceInstanceMapperTest {

    private ServiceInstanceMapper serviceInstanceMapper;

    @BeforeEach
    void setUp() {
        serviceInstanceMapper = new ServiceInstanceMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getServiceInstanceSample1();
        var actual = serviceInstanceMapper.toEntity(serviceInstanceMapper.toDto(expected));
        assertServiceInstanceAllPropertiesEquals(expected, actual);
    }
}
