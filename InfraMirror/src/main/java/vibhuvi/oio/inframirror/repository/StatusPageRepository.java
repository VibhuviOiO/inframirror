package vibhuvi.oio.inframirror.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import vibhuvi.oio.inframirror.domain.StatusPage;

/**
 * Spring Data JPA repository for the StatusPage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StatusPageRepository extends JpaRepository<StatusPage, Long>, JpaSpecificationExecutor<StatusPage> {}
