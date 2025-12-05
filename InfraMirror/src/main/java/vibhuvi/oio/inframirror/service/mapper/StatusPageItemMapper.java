package vibhuvi.oio.inframirror.service.mapper;

import org.mapstruct.*;
import vibhuvi.oio.inframirror.domain.StatusPage;
import vibhuvi.oio.inframirror.domain.StatusPageItem;
import vibhuvi.oio.inframirror.service.dto.StatusPageDTO;
import vibhuvi.oio.inframirror.service.dto.StatusPageItemDTO;

/**
 * Mapper for the entity {@link StatusPageItem} and its DTO {@link StatusPageItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface StatusPageItemMapper extends EntityMapper<StatusPageItemDTO, StatusPageItem> {
    @Mapping(target = "statusPage", source = "statusPage", qualifiedByName = "statusPageId")
    StatusPageItemDTO toDto(StatusPageItem s);

    @Named("statusPageId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    StatusPageDTO toDtoStatusPageId(StatusPage statusPage);
}
