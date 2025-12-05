package vibhuvi.oio.inframirror.service.mapper;

import org.mapstruct.*;
import vibhuvi.oio.inframirror.domain.AuditTrail;
import vibhuvi.oio.inframirror.service.dto.AuditTrailDTO;

/**
 * Mapper for the entity {@link AuditTrail} and its DTO {@link AuditTrailDTO}.
 */
@Mapper(componentModel = "spring")
public interface AuditTrailMapper extends EntityMapper<AuditTrailDTO, AuditTrail> {}
