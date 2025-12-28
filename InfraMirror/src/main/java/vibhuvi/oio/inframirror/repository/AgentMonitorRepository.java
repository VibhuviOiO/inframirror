package vibhuvi.oio.inframirror.repository;

import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vibhuvi.oio.inframirror.domain.AgentMonitor;

/**
 * Spring Data JPA repository for the AgentMonitor entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AgentMonitorRepository extends JpaRepository<AgentMonitor, Long> {
    List<AgentMonitor> findByAgentId(Long agentId);
    
    List<AgentMonitor> findByMonitorId(Long monitorId);
    
    List<AgentMonitor> findByActive(Boolean active);
    
    @Query("SELECT am FROM AgentMonitor am WHERE am.agent.name LIKE %:agentName% OR am.monitor.name LIKE %:monitorName%")
    List<AgentMonitor> searchByAgentOrMonitorName(@Param("agentName") String agentName, @Param("monitorName") String monitorName);
}
