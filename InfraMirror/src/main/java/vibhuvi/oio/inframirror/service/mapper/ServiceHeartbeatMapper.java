package vibhuvi.oio.inframirror.service.mapper;

import org.mapstruct.*;
import vibhuvi.oio.inframirror.domain.Agent;
import vibhuvi.oio.inframirror.domain.Service;
import vibhuvi.oio.inframirror.domain.ServiceHeartbeat;
import vibhuvi.oio.inframirror.domain.ServiceInstance;
import vibhuvi.oio.inframirror.service.dto.AgentDTO;
import vibhuvi.oio.inframirror.service.dto.ServiceDTO;
import vibhuvi.oio.inframirror.service.dto.ServiceHeartbeatDTO;
import vibhuvi.oio.inframirror.service.dto.ServiceInstanceDTO;

/**
 * Mapper for the entity {@link ServiceHeartbeat} and its DTO {@link ServiceHeartbeatDTO}.
 */
@Mapper(componentModel = "spring")
public interface ServiceHeartbeatMapper extends EntityMapper<ServiceHeartbeatDTO, ServiceHeartbeat> {
    @Mapping(target = "agent", source = "agent", qualifiedByName = "agentId")
    @Mapping(target = "service", source = "service", qualifiedByName = "serviceId")
    @Mapping(target = "serviceInstance", source = "serviceInstance", qualifiedByName = "serviceInstanceId")
    ServiceHeartbeatDTO toDto(ServiceHeartbeat s);

    @Named("agentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AgentDTO toDtoAgentId(Agent agent);

    @Named("serviceId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ServiceDTO toDtoServiceId(Service service);

    @Named("serviceInstanceId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ServiceInstanceDTO toDtoServiceInstanceId(ServiceInstance serviceInstance);
}
