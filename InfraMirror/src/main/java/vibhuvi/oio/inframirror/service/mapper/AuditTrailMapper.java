package vibhuvi.oio.inframirror.service.mapper;

import org.mapstruct.*;
import vibhuvi.oio.inframirror.domain.AuditTrail;
import vibhuvi.oio.inframirror.domain.User;
import vibhuvi.oio.inframirror.service.dto.AuditTrailDTO;
import vibhuvi.oio.inframirror.service.dto.UserDTO;

/**
 * Mapper for the entity {@link AuditTrail} and its DTO {@link AuditTrailDTO}.
 */
@Mapper(componentModel = "spring")
public interface AuditTrailMapper extends EntityMapper<AuditTrailDTO, AuditTrail> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    AuditTrailDTO toDto(AuditTrail s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
