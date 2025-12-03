package vibhuvi.oio.inframirror.service.mapper;

import static vibhuvi.oio.inframirror.domain.InstanceAsserts.*;
import static vibhuvi.oio.inframirror.domain.InstanceTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InstanceMapperTest {

    private InstanceMapper instanceMapper;

    @BeforeEach
    void setUp() {
        instanceMapper = new InstanceMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getInstanceSample1();
        var actual = instanceMapper.toEntity(instanceMapper.toDto(expected));
        assertInstanceAllPropertiesEquals(expected, actual);
    }
}
