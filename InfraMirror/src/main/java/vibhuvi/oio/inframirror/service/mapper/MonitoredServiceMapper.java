package vibhuvi.oio.inframirror.service.mapper;

import org.mapstruct.*;
import vibhuvi.oio.inframirror.domain.Datacenter;
import vibhuvi.oio.inframirror.domain.MonitoredService;
import vibhuvi.oio.inframirror.service.dto.DatacenterDTO;
import vibhuvi.oio.inframirror.service.dto.MonitoredServiceDTO;

/**
 * Mapper for the entity {@link MonitoredService} and its DTO {@link MonitoredServiceDTO}.
 */
@Mapper(componentModel = "spring")
public interface MonitoredServiceMapper extends EntityMapper<MonitoredServiceDTO, MonitoredService> {
    @Mapping(target = "datacenter", source = "datacenter", qualifiedByName = "datacenterId")
    MonitoredServiceDTO toDto(MonitoredService s);

    @Named("datacenterId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DatacenterDTO toDtoDatacenterId(Datacenter datacenter);
}
