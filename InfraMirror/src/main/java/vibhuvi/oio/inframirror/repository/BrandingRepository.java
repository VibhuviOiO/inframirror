package vibhuvi.oio.inframirror.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import vibhuvi.oio.inframirror.domain.Branding;

/**
 * Spring Data JPA repository for the Branding entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BrandingRepository extends JpaRepository<Branding, Long> {}
