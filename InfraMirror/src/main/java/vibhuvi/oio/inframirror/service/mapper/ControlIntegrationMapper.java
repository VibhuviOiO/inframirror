package vibhuvi.oio.inframirror.service.mapper;

import org.mapstruct.*;
import vibhuvi.oio.inframirror.domain.ControlIntegration;
import vibhuvi.oio.inframirror.service.dto.ControlIntegrationDTO;

/**
 * Mapper for {@link ControlIntegration} and {@link ControlIntegrationDTO}.
 */
@Mapper(componentModel = "spring")
public interface ControlIntegrationMapper extends EntityMapper<ControlIntegrationDTO, ControlIntegration> {}
