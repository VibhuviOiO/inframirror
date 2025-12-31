package vibhuvi.oio.inframirror.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import vibhuvi.oio.inframirror.domain.StatusPageItem;

/**
 * Spring Data JPA repository for the StatusPageItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StatusPageItemRepository extends JpaRepository<StatusPageItem, Long> {
    List<StatusPageItem> findByStatusPageIdOrderByDisplayOrderAsc(Long statusPageId);
}
