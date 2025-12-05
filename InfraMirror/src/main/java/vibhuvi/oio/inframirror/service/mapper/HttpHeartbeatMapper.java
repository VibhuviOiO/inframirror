package vibhuvi.oio.inframirror.service.mapper;

import org.mapstruct.*;
import vibhuvi.oio.inframirror.domain.Agent;
import vibhuvi.oio.inframirror.domain.HttpHeartbeat;
import vibhuvi.oio.inframirror.domain.HttpMonitor;
import vibhuvi.oio.inframirror.service.dto.AgentDTO;
import vibhuvi.oio.inframirror.service.dto.HttpHeartbeatDTO;
import vibhuvi.oio.inframirror.service.dto.HttpMonitorDTO;

/**
 * Mapper for the entity {@link HttpHeartbeat} and its DTO {@link HttpHeartbeatDTO}.
 */
@Mapper(componentModel = "spring")
public interface HttpHeartbeatMapper extends EntityMapper<HttpHeartbeatDTO, HttpHeartbeat> {
    @Mapping(target = "agent", source = "agent", qualifiedByName = "agentId")
    @Mapping(target = "monitor", source = "monitor", qualifiedByName = "httpMonitorId")
    HttpHeartbeatDTO toDto(HttpHeartbeat s);

    @Named("agentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AgentDTO toDtoAgentId(Agent agent);

    @Named("httpMonitorId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    HttpMonitorDTO toDtoHttpMonitorId(HttpMonitor httpMonitor);
}
