package vibhuvi.oio.inframirror.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import vibhuvi.oio.inframirror.domain.IntegrationView;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the IntegrationView entity.
 */
@Repository
public interface IntegrationViewRepository extends JpaRepository<IntegrationView, Long> {
    List<IntegrationView> findByIntegrationResourceId(Long integrationResourceId);
    
    Optional<IntegrationView> findByIntegrationResourceIdAndIsDefaultTrue(Long integrationResourceId);
}
