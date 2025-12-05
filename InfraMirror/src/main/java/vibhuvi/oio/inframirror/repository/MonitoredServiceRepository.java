package vibhuvi.oio.inframirror.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import vibhuvi.oio.inframirror.domain.MonitoredService;

/**
 * Spring Data JPA repository for the MonitoredService entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MonitoredServiceRepository extends JpaRepository<MonitoredService, Long>, JpaSpecificationExecutor<MonitoredService> {}
