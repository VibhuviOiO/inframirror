package vibhuvi.oio.inframirror.service.mapper;

import org.mapstruct.*;
import vibhuvi.oio.inframirror.domain.StatusDependency;
import vibhuvi.oio.inframirror.domain.StatusPage;
import vibhuvi.oio.inframirror.service.dto.StatusDependencyDTO;
import vibhuvi.oio.inframirror.service.dto.StatusPageDTO;

/**
 * Mapper for the entity {@link StatusDependency} and its DTO {@link StatusDependencyDTO}.
 */
@Mapper(componentModel = "spring")
public interface StatusDependencyMapper extends EntityMapper<StatusDependencyDTO, StatusDependency> {
    @Mapping(target = "statusPage", source = "statusPage", qualifiedByName = "statusPageId")
    StatusDependencyDTO toDto(StatusDependency s);

    @Named("statusPageId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    StatusPageDTO toDtoStatusPageId(StatusPage statusPage);
}
