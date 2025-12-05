package vibhuvi.oio.inframirror.service.mapper;

import static vibhuvi.oio.inframirror.domain.StatusPageItemAsserts.*;
import static vibhuvi.oio.inframirror.domain.StatusPageItemTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StatusPageItemMapperTest {

    private StatusPageItemMapper statusPageItemMapper;

    @BeforeEach
    void setUp() {
        statusPageItemMapper = new StatusPageItemMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getStatusPageItemSample1();
        var actual = statusPageItemMapper.toEntity(statusPageItemMapper.toDto(expected));
        assertStatusPageItemAllPropertiesEquals(expected, actual);
    }
}
