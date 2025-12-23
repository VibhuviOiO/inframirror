package vibhuvi.oio.inframirror.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import vibhuvi.oio.inframirror.domain.Instance;

/**
 * Spring Data JPA repository for the Instance entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InstanceRepository extends JpaRepository<Instance, Long>, JpaSpecificationExecutor<Instance> {
    Optional<Instance> findByHostname(String hostname);
}
