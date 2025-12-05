package vibhuvi.oio.inframirror.service.mapper;

import static vibhuvi.oio.inframirror.domain.AgentLockAsserts.*;
import static vibhuvi.oio.inframirror.domain.AgentLockTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AgentLockMapperTest {

    private AgentLockMapper agentLockMapper;

    @BeforeEach
    void setUp() {
        agentLockMapper = new AgentLockMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAgentLockSample1();
        var actual = agentLockMapper.toEntity(agentLockMapper.toDto(expected));
        assertAgentLockAllPropertiesEquals(expected, actual);
    }
}
