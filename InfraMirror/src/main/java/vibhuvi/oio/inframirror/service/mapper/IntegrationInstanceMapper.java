package vibhuvi.oio.inframirror.service.mapper;

import org.mapstruct.*;
import vibhuvi.oio.inframirror.domain.IntegrationInstance;
import vibhuvi.oio.inframirror.service.dto.IntegrationInstanceDTO;

/**
 * Mapper for {@link IntegrationInstance} and {@link IntegrationInstanceDTO}.
 */
@Mapper(componentModel = "spring")
public interface IntegrationInstanceMapper extends EntityMapper<IntegrationInstanceDTO, IntegrationInstance> {
    @Mapping(target = "controlIntegrationId", source = "controlIntegration.id")
    @Mapping(target = "controlIntegrationName", source = "controlIntegration.name")
    @Mapping(target = "monitoredServiceId", source = "monitoredService.id")
    @Mapping(target = "monitoredServiceName", source = "monitoredService.name")
    @Mapping(target = "httpMonitorId", source = "httpMonitor.id")
    @Mapping(target = "httpMonitorName", source = "httpMonitor.name")
    @Mapping(target = "datacenterId", source = "datacenter.id")
    @Mapping(target = "datacenterName", source = "datacenter.name")
    IntegrationInstanceDTO toDto(IntegrationInstance s);

    @Mapping(target = "controlIntegration", source = "controlIntegrationId", qualifiedByName = "controlIntegrationId")
    @Mapping(target = "monitoredService", source = "monitoredServiceId", qualifiedByName = "monitoredServiceId")
    @Mapping(target = "httpMonitor", source = "httpMonitorId", qualifiedByName = "httpMonitorId")
    @Mapping(target = "datacenter", source = "datacenterId", qualifiedByName = "datacenterId")
    IntegrationInstance toEntity(IntegrationInstanceDTO dto);

    @Named("controlIntegrationId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    vibhuvi.oio.inframirror.domain.ControlIntegration controlIntegrationFromId(Long id);

    @Named("monitoredServiceId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    vibhuvi.oio.inframirror.domain.MonitoredService monitoredServiceFromId(Long id);

    @Named("httpMonitorId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    vibhuvi.oio.inframirror.domain.HttpMonitor httpMonitorFromId(Long id);

    @Named("datacenterId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    vibhuvi.oio.inframirror.domain.Datacenter datacenterFromId(Long id);
}
