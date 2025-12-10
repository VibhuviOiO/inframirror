package vibhuvi.oio.inframirror.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vibhuvi.oio.inframirror.service.dto.AuditTrailDTO;

/**
 * Service Interface for managing {@link vibhuvi.oio.inframirror.domain.AuditTrail}.
 */
public interface AuditTrailService {
    AuditTrailDTO save(AuditTrailDTO auditTrailDTO);

    AuditTrailDTO update(AuditTrailDTO auditTrailDTO);

    Optional<AuditTrailDTO> partialUpdate(AuditTrailDTO auditTrailDTO);

    Page<AuditTrailDTO> findAll(Pageable pageable);

    Optional<AuditTrailDTO> findOne(Long id);

    void delete(Long id);

    Page<AuditTrailDTO> search(String query, Pageable pageable);
}
