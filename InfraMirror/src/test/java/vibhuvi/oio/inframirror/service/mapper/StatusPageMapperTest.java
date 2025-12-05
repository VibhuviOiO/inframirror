package vibhuvi.oio.inframirror.service.mapper;

import static vibhuvi.oio.inframirror.domain.StatusPageAsserts.*;
import static vibhuvi.oio.inframirror.domain.StatusPageTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StatusPageMapperTest {

    private StatusPageMapper statusPageMapper;

    @BeforeEach
    void setUp() {
        statusPageMapper = new StatusPageMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getStatusPageSample1();
        var actual = statusPageMapper.toEntity(statusPageMapper.toDto(expected));
        assertStatusPageAllPropertiesEquals(expected, actual);
    }
}
