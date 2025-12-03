package vibhuvi.oio.inframirror.repository;

import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import vibhuvi.oio.inframirror.domain.SessionLog;

/**
 * Spring Data JPA repository for the SessionLog entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SessionLogRepository extends JpaRepository<SessionLog, Long>, JpaSpecificationExecutor<SessionLog> {
    @Query("select sessionLog from SessionLog sessionLog where sessionLog.user.login = ?#{authentication.name}")
    List<SessionLog> findByUserIsCurrentUser();
}
