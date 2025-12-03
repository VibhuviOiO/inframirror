package vibhuvi.oio.inframirror.service.mapper;

import org.mapstruct.*;
import vibhuvi.oio.inframirror.domain.Agent;
import vibhuvi.oio.inframirror.domain.Instance;
import vibhuvi.oio.inframirror.domain.PingHeartbeat;
import vibhuvi.oio.inframirror.service.dto.AgentDTO;
import vibhuvi.oio.inframirror.service.dto.InstanceDTO;
import vibhuvi.oio.inframirror.service.dto.PingHeartbeatDTO;

/**
 * Mapper for the entity {@link PingHeartbeat} and its DTO {@link PingHeartbeatDTO}.
 */
@Mapper(componentModel = "spring")
public interface PingHeartbeatMapper extends EntityMapper<PingHeartbeatDTO, PingHeartbeat> {
    @Mapping(target = "instance", source = "instance", qualifiedByName = "instanceId")
    @Mapping(target = "agent", source = "agent", qualifiedByName = "agentId")
    PingHeartbeatDTO toDto(PingHeartbeat s);

    @Named("instanceId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    InstanceDTO toDtoInstanceId(Instance instance);

    @Named("agentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AgentDTO toDtoAgentId(Agent agent);
}
