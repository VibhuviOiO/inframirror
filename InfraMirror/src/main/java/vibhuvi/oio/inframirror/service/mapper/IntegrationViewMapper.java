package vibhuvi.oio.inframirror.service.mapper;

import org.mapstruct.*;
import vibhuvi.oio.inframirror.domain.IntegrationView;
import vibhuvi.oio.inframirror.service.dto.IntegrationViewDTO;

/**
 * Mapper for {@link IntegrationView} and {@link IntegrationViewDTO}.
 */
@Mapper(componentModel = "spring")
public interface IntegrationViewMapper extends EntityMapper<IntegrationViewDTO, IntegrationView> {
    @Mapping(target = "integrationResourceId", source = "integrationResource.id")
    @Mapping(target = "integrationResourceName", source = "integrationResource.name")
    IntegrationViewDTO toDto(IntegrationView s);

    @Mapping(target = "integrationResource", source = "integrationResourceId", qualifiedByName = "integrationResourceId")
    IntegrationView toEntity(IntegrationViewDTO dto);

    @Named("integrationResourceId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    vibhuvi.oio.inframirror.domain.IntegrationResource integrationResourceFromId(Long id);
}
