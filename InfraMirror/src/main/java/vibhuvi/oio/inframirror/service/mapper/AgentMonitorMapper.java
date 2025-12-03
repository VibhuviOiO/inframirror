package vibhuvi.oio.inframirror.service.mapper;

import org.mapstruct.*;
import vibhuvi.oio.inframirror.domain.Agent;
import vibhuvi.oio.inframirror.domain.AgentMonitor;
import vibhuvi.oio.inframirror.domain.HttpMonitor;
import vibhuvi.oio.inframirror.service.dto.AgentDTO;
import vibhuvi.oio.inframirror.service.dto.AgentMonitorDTO;
import vibhuvi.oio.inframirror.service.dto.HttpMonitorDTO;

/**
 * Mapper for the entity {@link AgentMonitor} and its DTO {@link AgentMonitorDTO}.
 */
@Mapper(componentModel = "spring")
public interface AgentMonitorMapper extends EntityMapper<AgentMonitorDTO, AgentMonitor> {
    @Mapping(target = "agent", source = "agent", qualifiedByName = "agentId")
    @Mapping(target = "monitor", source = "monitor", qualifiedByName = "httpMonitorId")
    AgentMonitorDTO toDto(AgentMonitor s);

    @Named("agentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AgentDTO toDtoAgentId(Agent agent);

    @Named("httpMonitorId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    HttpMonitorDTO toDtoHttpMonitorId(HttpMonitor httpMonitor);
}
