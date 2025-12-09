package vibhuvi.oio.inframirror.service.mapper;

import org.mapstruct.*;
import vibhuvi.oio.inframirror.domain.Instance;
import vibhuvi.oio.inframirror.domain.MonitoredService;
import vibhuvi.oio.inframirror.domain.ServiceInstance;
import vibhuvi.oio.inframirror.service.dto.InstanceDTO;
import vibhuvi.oio.inframirror.service.dto.MonitoredServiceDTO;
import vibhuvi.oio.inframirror.service.dto.ServiceInstanceDTO;

/**
 * Mapper for the entity {@link ServiceInstance} and its DTO {@link ServiceInstanceDTO}.
 */
@Mapper(componentModel = "spring", uses = {InstanceMapper.class})
public interface ServiceInstanceMapper extends EntityMapper<ServiceInstanceDTO, ServiceInstance> {
    @Mapping(target = "instance", source = "instance", qualifiedByName = "instanceId")
    @Mapping(target = "monitoredService", source = "monitoredService", qualifiedByName = "monitoredServiceId")
    ServiceInstanceDTO toDto(ServiceInstance s);
    
    @Named("withFullInstance")
    @Mapping(target = "instance", source = "instance")
    @Mapping(target = "monitoredService", source = "monitoredService", qualifiedByName = "monitoredServiceId")
    ServiceInstanceDTO toDtoWithFullInstance(ServiceInstance s);

    @Named("instanceId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    InstanceDTO toDtoInstanceId(Instance instance);

    @Named("monitoredServiceId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MonitoredServiceDTO toDtoMonitoredServiceId(MonitoredService monitoredService);
}
