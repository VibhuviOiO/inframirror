package vibhuvi.oio.inframirror.service.mapper;

import org.mapstruct.*;
import vibhuvi.oio.inframirror.domain.Agent;
import vibhuvi.oio.inframirror.domain.AgentMonitor;
import vibhuvi.oio.inframirror.service.dto.AgentDTO;
import vibhuvi.oio.inframirror.service.dto.AgentMonitorDTO;

/**
 * Mapper for the entity {@link AgentMonitor} and its DTO {@link AgentMonitorDTO}.
 */
@Mapper(componentModel = "spring")
public interface AgentMonitorMapper extends EntityMapper<AgentMonitorDTO, AgentMonitor> {
    @Mapping(target = "agent", source = "agent", qualifiedByName = "agentId")
    AgentMonitorDTO toDto(AgentMonitor s);

    @Named("agentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    AgentDTO toDtoAgentId(Agent agent);
}
