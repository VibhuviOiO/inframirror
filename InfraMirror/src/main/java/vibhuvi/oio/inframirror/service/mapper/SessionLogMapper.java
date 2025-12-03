package vibhuvi.oio.inframirror.service.mapper;

import org.mapstruct.*;
import vibhuvi.oio.inframirror.domain.Agent;
import vibhuvi.oio.inframirror.domain.Instance;
import vibhuvi.oio.inframirror.domain.SessionLog;
import vibhuvi.oio.inframirror.domain.User;
import vibhuvi.oio.inframirror.service.dto.AgentDTO;
import vibhuvi.oio.inframirror.service.dto.InstanceDTO;
import vibhuvi.oio.inframirror.service.dto.SessionLogDTO;
import vibhuvi.oio.inframirror.service.dto.UserDTO;

/**
 * Mapper for the entity {@link SessionLog} and its DTO {@link SessionLogDTO}.
 */
@Mapper(componentModel = "spring")
public interface SessionLogMapper extends EntityMapper<SessionLogDTO, SessionLog> {
    @Mapping(target = "instance", source = "instance", qualifiedByName = "instanceId")
    @Mapping(target = "agent", source = "agent", qualifiedByName = "agentId")
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    SessionLogDTO toDto(SessionLog s);

    @Named("instanceId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    InstanceDTO toDtoInstanceId(Instance instance);

    @Named("agentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AgentDTO toDtoAgentId(Agent agent);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
