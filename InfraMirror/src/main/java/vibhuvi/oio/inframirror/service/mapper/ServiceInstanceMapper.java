package vibhuvi.oio.inframirror.service.mapper;

import org.mapstruct.*;
import vibhuvi.oio.inframirror.domain.Instance;
import vibhuvi.oio.inframirror.domain.Service;
import vibhuvi.oio.inframirror.domain.ServiceInstance;
import vibhuvi.oio.inframirror.service.dto.InstanceDTO;
import vibhuvi.oio.inframirror.service.dto.ServiceDTO;
import vibhuvi.oio.inframirror.service.dto.ServiceInstanceDTO;

/**
 * Mapper for the entity {@link ServiceInstance} and its DTO {@link ServiceInstanceDTO}.
 */
@Mapper(componentModel = "spring")
public interface ServiceInstanceMapper extends EntityMapper<ServiceInstanceDTO, ServiceInstance> {
    @Mapping(target = "instance", source = "instance", qualifiedByName = "instanceId")
    @Mapping(target = "service", source = "service", qualifiedByName = "serviceId")
    ServiceInstanceDTO toDto(ServiceInstance s);

    @Named("instanceId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    InstanceDTO toDtoInstanceId(Instance instance);

    @Named("serviceId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ServiceDTO toDtoServiceId(Service service);
}
