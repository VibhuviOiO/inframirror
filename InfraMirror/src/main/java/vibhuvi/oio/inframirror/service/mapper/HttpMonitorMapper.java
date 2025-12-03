package vibhuvi.oio.inframirror.service.mapper;

import org.mapstruct.*;
import vibhuvi.oio.inframirror.domain.HttpMonitor;
import vibhuvi.oio.inframirror.domain.Schedule;
import vibhuvi.oio.inframirror.service.dto.HttpMonitorDTO;
import vibhuvi.oio.inframirror.service.dto.ScheduleDTO;

/**
 * Mapper for the entity {@link HttpMonitor} and its DTO {@link HttpMonitorDTO}.
 */
@Mapper(componentModel = "spring")
public interface HttpMonitorMapper extends EntityMapper<HttpMonitorDTO, HttpMonitor> {
    @Mapping(target = "schedule", source = "schedule", qualifiedByName = "scheduleId")
    HttpMonitorDTO toDto(HttpMonitor s);

    @Named("scheduleId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ScheduleDTO toDtoScheduleId(Schedule schedule);
}
