package vibhuvi.oio.inframirror.repository;

import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import vibhuvi.oio.inframirror.domain.AuditTrail;

/**
 * Spring Data JPA repository for the AuditTrail entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AuditTrailRepository extends JpaRepository<AuditTrail, Long>, JpaSpecificationExecutor<AuditTrail> {
    @Query("select auditTrail from AuditTrail auditTrail where auditTrail.user.login = ?#{authentication.name}")
    List<AuditTrail> findByUserIsCurrentUser();
}
