package vibhuvi.oio.inframirror.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vibhuvi.oio.inframirror.domain.AgentMonitor;

/**
 * Spring Data JPA repository for the AgentMonitor entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AgentMonitorRepository extends JpaRepository<AgentMonitor, Long>, JpaSpecificationExecutor<AgentMonitor> {
    List<AgentMonitor> findByAgentId(Long agentId);
    
    List<AgentMonitor> findByMonitorId(Long monitorId);
    
    List<AgentMonitor> findByActive(Boolean active);
}
