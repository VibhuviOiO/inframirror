package vibhuvi.oio.inframirror.service.mapper;

import org.mapstruct.*;
import vibhuvi.oio.inframirror.domain.Agent;
import vibhuvi.oio.inframirror.domain.Region;
import vibhuvi.oio.inframirror.service.dto.AgentDTO;
import vibhuvi.oio.inframirror.service.dto.RegionDTO;

/**
 * Mapper for the entity {@link Agent} and its DTO {@link AgentDTO}.
 */
@Mapper(componentModel = "spring")
public interface AgentMapper extends EntityMapper<AgentDTO, Agent> {
    @Mapping(target = "region", source = "region", qualifiedByName = "regionId")
    AgentDTO toDto(Agent s);

    @Named("regionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RegionDTO toDtoRegionId(Region region);
}
