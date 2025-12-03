package vibhuvi.oio.inframirror.service.mapper;

import static vibhuvi.oio.inframirror.domain.AgentAsserts.*;
import static vibhuvi.oio.inframirror.domain.AgentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AgentMapperTest {

    private AgentMapper agentMapper;

    @BeforeEach
    void setUp() {
        agentMapper = new AgentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAgentSample1();
        var actual = agentMapper.toEntity(agentMapper.toDto(expected));
        assertAgentAllPropertiesEquals(expected, actual);
    }
}
