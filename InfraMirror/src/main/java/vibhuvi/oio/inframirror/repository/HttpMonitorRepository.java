package vibhuvi.oio.inframirror.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import vibhuvi.oio.inframirror.domain.HttpMonitor;

/**
 * Spring Data JPA repository for the HttpMonitor entity.
 */
@SuppressWarnings("unused")
@Repository
public interface HttpMonitorRepository extends JpaRepository<HttpMonitor, Long>, JpaSpecificationExecutor<HttpMonitor> {}
