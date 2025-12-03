package vibhuvi.oio.inframirror.service.mapper;

import static vibhuvi.oio.inframirror.domain.AgentMonitorAsserts.*;
import static vibhuvi.oio.inframirror.domain.AgentMonitorTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AgentMonitorMapperTest {

    private AgentMonitorMapper agentMonitorMapper;

    @BeforeEach
    void setUp() {
        agentMonitorMapper = new AgentMonitorMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAgentMonitorSample1();
        var actual = agentMonitorMapper.toEntity(agentMonitorMapper.toDto(expected));
        assertAgentMonitorAllPropertiesEquals(expected, actual);
    }
}
