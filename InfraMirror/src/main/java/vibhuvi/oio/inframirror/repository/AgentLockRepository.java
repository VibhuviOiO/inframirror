package vibhuvi.oio.inframirror.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import vibhuvi.oio.inframirror.domain.AgentLock;

/**
 * Spring Data JPA repository for the AgentLock entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AgentLockRepository extends JpaRepository<AgentLock, Long> {}
