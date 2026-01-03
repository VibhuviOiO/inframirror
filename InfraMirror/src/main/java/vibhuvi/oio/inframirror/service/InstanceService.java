package vibhuvi.oio.inframirror.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vibhuvi.oio.inframirror.service.dto.InstanceDTO;
import vibhuvi.oio.inframirror.service.dto.InstanceSearchResultDTO;

/**
 * Service Interface for managing {@link vibhuvi.oio.inframirror.domain.Instance}.
 */
public interface InstanceService {
    InstanceDTO save(InstanceDTO instanceDTO);

    InstanceDTO update(InstanceDTO instanceDTO);

    Optional<InstanceDTO> partialUpdate(InstanceDTO instanceDTO);

    Optional<InstanceDTO> findOne(Long id);

    void delete(Long id);

    InstanceDTO findOrCreate(InstanceDTO instanceDTO);
}
