package vibhuvi.oio.inframirror.service.mapper;

import org.mapstruct.*;
import vibhuvi.oio.inframirror.domain.Datacenter;
import vibhuvi.oio.inframirror.domain.Service;
import vibhuvi.oio.inframirror.service.dto.DatacenterDTO;
import vibhuvi.oio.inframirror.service.dto.ServiceDTO;

/**
 * Mapper for the entity {@link Service} and its DTO {@link ServiceDTO}.
 */
@Mapper(componentModel = "spring")
public interface ServiceMapper extends EntityMapper<ServiceDTO, Service> {
    @Mapping(target = "datacenter", source = "datacenter", qualifiedByName = "datacenterId")
    ServiceDTO toDto(Service s);

    @Named("datacenterId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DatacenterDTO toDtoDatacenterId(Datacenter datacenter);
}
