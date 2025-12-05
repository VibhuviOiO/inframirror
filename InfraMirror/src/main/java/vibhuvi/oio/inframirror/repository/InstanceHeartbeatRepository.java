package vibhuvi.oio.inframirror.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import vibhuvi.oio.inframirror.domain.InstanceHeartbeat;

/**
 * Spring Data JPA repository for the InstanceHeartbeat entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InstanceHeartbeatRepository extends JpaRepository<InstanceHeartbeat, Long> {}
