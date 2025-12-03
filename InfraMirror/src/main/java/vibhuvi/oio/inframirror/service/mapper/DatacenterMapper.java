package vibhuvi.oio.inframirror.service.mapper;

import org.mapstruct.*;
import vibhuvi.oio.inframirror.domain.Datacenter;
import vibhuvi.oio.inframirror.domain.Region;
import vibhuvi.oio.inframirror.service.dto.DatacenterDTO;
import vibhuvi.oio.inframirror.service.dto.RegionDTO;

/**
 * Mapper for the entity {@link Datacenter} and its DTO {@link DatacenterDTO}.
 */
@Mapper(componentModel = "spring")
public interface DatacenterMapper extends EntityMapper<DatacenterDTO, Datacenter> {
    @Mapping(target = "region", source = "region", qualifiedByName = "regionId")
    DatacenterDTO toDto(Datacenter s);

    @Named("regionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RegionDTO toDtoRegionId(Region region);
}
