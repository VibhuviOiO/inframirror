package vibhuvi.oio.inframirror.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import vibhuvi.oio.inframirror.domain.ServiceInstance;

/**
 * Spring Data JPA repository for the ServiceInstance entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ServiceInstanceRepository extends JpaRepository<ServiceInstance, Long> {}
