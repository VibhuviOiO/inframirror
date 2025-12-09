package vibhuvi.oio.inframirror.service.mapper;

import org.mapstruct.*;
import vibhuvi.oio.inframirror.domain.HttpMonitor;
import vibhuvi.oio.inframirror.service.dto.HttpMonitorDTO;

/**
 * Mapper for the entity {@link HttpMonitor} and its DTO {@link HttpMonitorDTO}.
 */
@Mapper(componentModel = "spring")
public interface HttpMonitorMapper extends EntityMapper<HttpMonitorDTO, HttpMonitor> {
    @Mapping(target = "parentId", source = "parent.id")
    HttpMonitorDTO toDto(HttpMonitor s);

    @Mapping(target = "parent", source = "parentId", qualifiedByName = "httpMonitorId")
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "removeChildren", ignore = true)
    @Mapping(target = "heartbeats", ignore = true)
    @Mapping(target = "removeHeartbeat", ignore = true)
    HttpMonitor toEntity(HttpMonitorDTO dto);

    @Named("httpMonitorId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    HttpMonitor fromId(Long id);
}
