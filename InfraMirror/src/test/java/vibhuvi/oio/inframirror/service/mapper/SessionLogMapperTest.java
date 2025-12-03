package vibhuvi.oio.inframirror.service.mapper;

import static vibhuvi.oio.inframirror.domain.SessionLogAsserts.*;
import static vibhuvi.oio.inframirror.domain.SessionLogTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SessionLogMapperTest {

    private SessionLogMapper sessionLogMapper;

    @BeforeEach
    void setUp() {
        sessionLogMapper = new SessionLogMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSessionLogSample1();
        var actual = sessionLogMapper.toEntity(sessionLogMapper.toDto(expected));
        assertSessionLogAllPropertiesEquals(expected, actual);
    }
}
