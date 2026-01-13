package vibhuvi.oio.inframirror.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import vibhuvi.oio.inframirror.domain.ControlIntegration;

import java.util.Optional;

/**
 * Spring Data JPA repository for the ControlIntegration entity.
 */
@Repository
public interface ControlIntegrationRepository extends JpaRepository<ControlIntegration, Long> {
    Optional<ControlIntegration> findByCode(String code);
    
    boolean existsByCode(String code);
}
