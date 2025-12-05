package vibhuvi.oio.inframirror.service.mapper;

import static vibhuvi.oio.inframirror.domain.StatusDependencyAsserts.*;
import static vibhuvi.oio.inframirror.domain.StatusDependencyTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StatusDependencyMapperTest {

    private StatusDependencyMapper statusDependencyMapper;

    @BeforeEach
    void setUp() {
        statusDependencyMapper = new StatusDependencyMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getStatusDependencySample1();
        var actual = statusDependencyMapper.toEntity(statusDependencyMapper.toDto(expected));
        assertStatusDependencyAllPropertiesEquals(expected, actual);
    }
}
