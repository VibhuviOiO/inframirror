package vibhuvi.oio.inframirror.service.mapper;

import org.mapstruct.*;
import vibhuvi.oio.inframirror.domain.HttpMonitor;
import vibhuvi.oio.inframirror.service.dto.HttpMonitorDTO;

/**
 * Mapper for the entity {@link HttpMonitor} and its DTO {@link HttpMonitorDTO}.
 */
@Mapper(componentModel = "spring")
public interface HttpMonitorMapper extends EntityMapper<HttpMonitorDTO, HttpMonitor> {
    @Mapping(target = "parent", source = "parent", qualifiedByName = "httpMonitorId")
    HttpMonitorDTO toDto(HttpMonitor s);

    @Named("httpMonitorId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    HttpMonitorDTO toDtoHttpMonitorId(HttpMonitor httpMonitor);
}
