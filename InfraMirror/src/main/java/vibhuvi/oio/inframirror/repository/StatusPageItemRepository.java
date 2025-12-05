package vibhuvi.oio.inframirror.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import vibhuvi.oio.inframirror.domain.StatusPageItem;

/**
 * Spring Data JPA repository for the StatusPageItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StatusPageItemRepository extends JpaRepository<StatusPageItem, Long> {}
