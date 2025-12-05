package vibhuvi.oio.inframirror.service.mapper;

import org.mapstruct.*;
import vibhuvi.oio.inframirror.domain.Branding;
import vibhuvi.oio.inframirror.service.dto.BrandingDTO;

/**
 * Mapper for the entity {@link Branding} and its DTO {@link BrandingDTO}.
 */
@Mapper(componentModel = "spring")
public interface BrandingMapper extends EntityMapper<BrandingDTO, Branding> {}
