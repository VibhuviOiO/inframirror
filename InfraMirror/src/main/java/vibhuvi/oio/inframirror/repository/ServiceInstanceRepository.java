package vibhuvi.oio.inframirror.repository;

import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vibhuvi.oio.inframirror.domain.ServiceInstance;

/**
 * Spring Data JPA repository for the ServiceInstance entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ServiceInstanceRepository extends JpaRepository<ServiceInstance, Long> {
    List<ServiceInstance> findByMonitoredServiceId(Long monitoredServiceId);
    
    @Query("SELECT si FROM ServiceInstance si LEFT JOIN FETCH si.instance WHERE si.monitoredService.id = :monitoredServiceId")
    List<ServiceInstance> findByMonitoredServiceIdWithInstance(@Param("monitoredServiceId") Long monitoredServiceId);
    
    @Query("SELECT si FROM ServiceInstance si LEFT JOIN FETCH si.monitoredService WHERE si.id = :id")
    java.util.Optional<ServiceInstance> findByIdWithMonitoredService(@Param("id") Long id);
    
    void deleteByMonitoredServiceId(Long monitoredServiceId);
}
