package vibhuvi.oio.inframirror.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import vibhuvi.oio.inframirror.domain.AgentMonitor;

/**
 * Spring Data JPA repository for the AgentMonitor entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AgentMonitorRepository extends JpaRepository<AgentMonitor, Long> {}
