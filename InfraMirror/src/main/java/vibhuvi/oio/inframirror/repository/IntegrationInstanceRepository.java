package vibhuvi.oio.inframirror.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import vibhuvi.oio.inframirror.domain.IntegrationInstance;

import java.util.List;

/**
 * Spring Data JPA repository for the IntegrationInstance entity.
 */
@Repository
public interface IntegrationInstanceRepository extends JpaRepository<IntegrationInstance, Long> {
    List<IntegrationInstance> findByControlIntegrationId(Long controlIntegrationId);
    
    List<IntegrationInstance> findByControlIntegrationCode(String code);
    
    List<IntegrationInstance> findByDatacenterId(Long datacenterId);
    
    List<IntegrationInstance> findByMonitoredServiceId(Long monitoredServiceId);
    
    List<IntegrationInstance> findByHttpMonitorId(Long httpMonitorId);
}
