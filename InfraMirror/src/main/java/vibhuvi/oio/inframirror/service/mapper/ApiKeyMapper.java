package vibhuvi.oio.inframirror.service.mapper;

import org.mapstruct.*;
import vibhuvi.oio.inframirror.domain.ApiKey;
import vibhuvi.oio.inframirror.service.dto.ApiKeyDTO;

/**
 * Mapper for the entity {@link ApiKey} and its DTO {@link ApiKeyDTO}.
 */
@Mapper(componentModel = "spring")
public interface ApiKeyMapper extends EntityMapper<ApiKeyDTO, ApiKey> {}
