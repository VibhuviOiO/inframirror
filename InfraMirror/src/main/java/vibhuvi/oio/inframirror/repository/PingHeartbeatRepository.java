package vibhuvi.oio.inframirror.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import vibhuvi.oio.inframirror.domain.PingHeartbeat;

/**
 * Spring Data JPA repository for the PingHeartbeat entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PingHeartbeatRepository extends JpaRepository<PingHeartbeat, Long>, JpaSpecificationExecutor<PingHeartbeat> {}
