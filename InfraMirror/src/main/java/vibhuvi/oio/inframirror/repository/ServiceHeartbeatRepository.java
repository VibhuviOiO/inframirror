package vibhuvi.oio.inframirror.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import vibhuvi.oio.inframirror.domain.ServiceHeartbeat;

/**
 * Spring Data JPA repository for the ServiceHeartbeat entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ServiceHeartbeatRepository extends JpaRepository<ServiceHeartbeat, Long> {}
