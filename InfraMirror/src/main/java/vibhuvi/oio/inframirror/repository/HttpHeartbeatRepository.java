package vibhuvi.oio.inframirror.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import vibhuvi.oio.inframirror.domain.HttpHeartbeat;

/**
 * Spring Data JPA repository for the HttpHeartbeat entity.
 */
@SuppressWarnings("unused")
@Repository
public interface HttpHeartbeatRepository extends JpaRepository<HttpHeartbeat, Long> {
    Optional<HttpHeartbeat> findFirstByMonitorIdOrderByExecutedAtDesc(Long monitorId);
    Optional<HttpHeartbeat> findFirstByMonitorIdAndAgentIdOrderByExecutedAtDesc(Long monitorId, Long agentId);
    List<HttpHeartbeat> findTop20ByMonitorIdOrderByExecutedAtDesc(Long monitorId);
    List<HttpHeartbeat> findTop20ByMonitorIdAndAgentIdOrderByExecutedAtDesc(Long monitorId, Long agentId);
}
