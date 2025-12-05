package vibhuvi.oio.inframirror.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import vibhuvi.oio.inframirror.domain.StatusDependency;

/**
 * Spring Data JPA repository for the StatusDependency entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StatusDependencyRepository extends JpaRepository<StatusDependency, Long> {}
