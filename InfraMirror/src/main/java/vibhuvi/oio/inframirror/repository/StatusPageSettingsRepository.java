package vibhuvi.oio.inframirror.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import vibhuvi.oio.inframirror.domain.StatusPageSettings;

@Repository
public interface StatusPageSettingsRepository extends JpaRepository<StatusPageSettings, Long> {
    Optional<StatusPageSettings> findByStatusPageId(Long statusPageId);
}
