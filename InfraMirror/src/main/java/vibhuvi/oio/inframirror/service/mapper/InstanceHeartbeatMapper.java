package vibhuvi.oio.inframirror.service.mapper;

import org.mapstruct.*;
import vibhuvi.oio.inframirror.domain.Agent;
import vibhuvi.oio.inframirror.domain.Instance;
import vibhuvi.oio.inframirror.domain.InstanceHeartbeat;
import vibhuvi.oio.inframirror.service.dto.AgentDTO;
import vibhuvi.oio.inframirror.service.dto.InstanceDTO;
import vibhuvi.oio.inframirror.service.dto.InstanceHeartbeatDTO;

/**
 * Mapper for the entity {@link InstanceHeartbeat} and its DTO {@link InstanceHeartbeatDTO}.
 */
@Mapper(componentModel = "spring")
public interface InstanceHeartbeatMapper extends EntityMapper<InstanceHeartbeatDTO, InstanceHeartbeat> {
    @Mapping(target = "agent", source = "agent", qualifiedByName = "agentId")
    @Mapping(target = "instance", source = "instance", qualifiedByName = "instanceId")
    InstanceHeartbeatDTO toDto(InstanceHeartbeat s);

    @Named("agentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AgentDTO toDtoAgentId(Agent agent);

    @Named("instanceId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    InstanceDTO toDtoInstanceId(Instance instance);
}
