package vibhuvi.oio.inframirror.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import vibhuvi.oio.inframirror.domain.IntegrationResource;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the IntegrationResource entity.
 */
@Repository
public interface IntegrationResourceRepository extends JpaRepository<IntegrationResource, Long> {
    List<IntegrationResource> findByControlIntegrationId(Long controlIntegrationId);
    
    Optional<IntegrationResource> findByControlIntegrationIdAndName(Long controlIntegrationId, String name);
    
    boolean existsByControlIntegrationIdAndName(Long controlIntegrationId, String name);
}
