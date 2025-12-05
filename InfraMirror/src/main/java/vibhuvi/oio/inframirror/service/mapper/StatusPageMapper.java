package vibhuvi.oio.inframirror.service.mapper;

import org.mapstruct.*;
import vibhuvi.oio.inframirror.domain.StatusPage;
import vibhuvi.oio.inframirror.service.dto.StatusPageDTO;

/**
 * Mapper for the entity {@link StatusPage} and its DTO {@link StatusPageDTO}.
 */
@Mapper(componentModel = "spring")
public interface StatusPageMapper extends EntityMapper<StatusPageDTO, StatusPage> {}
