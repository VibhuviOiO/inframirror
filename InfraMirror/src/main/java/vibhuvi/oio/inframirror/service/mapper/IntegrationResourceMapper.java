package vibhuvi.oio.inframirror.service.mapper;

import org.mapstruct.*;
import vibhuvi.oio.inframirror.domain.IntegrationResource;
import vibhuvi.oio.inframirror.service.dto.IntegrationResourceDTO;

/**
 * Mapper for {@link IntegrationResource} and {@link IntegrationResourceDTO}.
 */
@Mapper(componentModel = "spring")
public interface IntegrationResourceMapper extends EntityMapper<IntegrationResourceDTO, IntegrationResource> {
    @Mapping(target = "controlIntegrationId", source = "controlIntegration.id")
    @Mapping(target = "controlIntegrationName", source = "controlIntegration.name")
    IntegrationResourceDTO toDto(IntegrationResource s);

    @Mapping(target = "controlIntegration", source = "controlIntegrationId", qualifiedByName = "controlIntegrationId")
    @Mapping(target = "views", ignore = true)
    IntegrationResource toEntity(IntegrationResourceDTO dto);

    @Named("controlIntegrationId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    vibhuvi.oio.inframirror.domain.ControlIntegration controlIntegrationFromId(Long id);
}
