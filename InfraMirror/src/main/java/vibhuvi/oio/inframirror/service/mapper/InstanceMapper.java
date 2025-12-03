package vibhuvi.oio.inframirror.service.mapper;

import org.mapstruct.*;
import vibhuvi.oio.inframirror.domain.Agent;
import vibhuvi.oio.inframirror.domain.Datacenter;
import vibhuvi.oio.inframirror.domain.Instance;
import vibhuvi.oio.inframirror.service.dto.AgentDTO;
import vibhuvi.oio.inframirror.service.dto.DatacenterDTO;
import vibhuvi.oio.inframirror.service.dto.InstanceDTO;

/**
 * Mapper for the entity {@link Instance} and its DTO {@link InstanceDTO}.
 */
@Mapper(componentModel = "spring")
public interface InstanceMapper extends EntityMapper<InstanceDTO, Instance> {
    @Mapping(target = "datacenter", source = "datacenter", qualifiedByName = "datacenterId")
    @Mapping(target = "agent", source = "agent", qualifiedByName = "agentId")
    InstanceDTO toDto(Instance s);

    @Named("datacenterId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DatacenterDTO toDtoDatacenterId(Datacenter datacenter);

    @Named("agentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AgentDTO toDtoAgentId(Agent agent);
}
