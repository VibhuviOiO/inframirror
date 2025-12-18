package vibhuvi.oio.inframirror.service.mapper;

import org.mapstruct.*;
import vibhuvi.oio.inframirror.domain.Agent;
import vibhuvi.oio.inframirror.domain.Datacenter;
import vibhuvi.oio.inframirror.domain.Region;
import vibhuvi.oio.inframirror.service.dto.AgentDTO;
import vibhuvi.oio.inframirror.service.dto.RegionDTO;

/**
 * Mapper for the entity {@link Agent} and its DTO {@link AgentDTO}.
 */
@Mapper(componentModel = "spring")
public interface AgentMapper extends EntityMapper<AgentDTO, Agent> {
    @Mapping(target = "region", source = "region", qualifiedByName = "regionId")
    @Mapping(target = "datacenter", source = "datacenter", qualifiedByName = "datacenterId")
    @Mapping(target = "datacenterId", source = "datacenter.id")
    AgentDTO toDto(Agent s);

    @Mapping(target = "datacenter.id", source = "datacenterId")
    Agent toEntity(AgentDTO agentDTO);

    default Datacenter map(Long id) {
        if (id == null) {
            return null;
        }
        Datacenter datacenter = new Datacenter();
        datacenter.setId(id);
        return datacenter;
    }

    @Named("regionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RegionDTO toDtoRegionId(vibhuvi.oio.inframirror.domain.Region region);

    @Named("datacenterId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    vibhuvi.oio.inframirror.service.dto.DatacenterDTO toDtoDatacenterId(vibhuvi.oio.inframirror.domain.Datacenter datacenter);
}
