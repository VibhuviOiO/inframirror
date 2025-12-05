package vibhuvi.oio.inframirror.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import vibhuvi.oio.inframirror.domain.AuditTrail;

/**
 * Spring Data JPA repository for the AuditTrail entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AuditTrailRepository extends JpaRepository<AuditTrail, Long> {}
