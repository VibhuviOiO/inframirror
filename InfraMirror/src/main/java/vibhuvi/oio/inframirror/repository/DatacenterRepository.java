package vibhuvi.oio.inframirror.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import vibhuvi.oio.inframirror.domain.Datacenter;

/**
 * Spring Data JPA repository for the Datacenter entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DatacenterRepository extends JpaRepository<Datacenter, Long>, JpaSpecificationExecutor<Datacenter> {
    Optional<Datacenter> findByNameAndRegionId(String name, Long regionId);
}
