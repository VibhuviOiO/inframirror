package vibhuvi.oio.inframirror.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import vibhuvi.oio.inframirror.domain.Service;

/**
 * Spring Data JPA repository for the Service entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ServiceRepository extends JpaRepository<Service, Long>, JpaSpecificationExecutor<Service> {}
