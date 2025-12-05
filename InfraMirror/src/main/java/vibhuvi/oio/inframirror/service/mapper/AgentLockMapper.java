package vibhuvi.oio.inframirror.service.mapper;

import org.mapstruct.*;
import vibhuvi.oio.inframirror.domain.AgentLock;
import vibhuvi.oio.inframirror.service.dto.AgentLockDTO;

/**
 * Mapper for the entity {@link AgentLock} and its DTO {@link AgentLockDTO}.
 */
@Mapper(componentModel = "spring")
public interface AgentLockMapper extends EntityMapper<AgentLockDTO, AgentLock> {}
