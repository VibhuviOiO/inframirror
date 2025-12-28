package vibhuvi.oio.inframirror.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vibhuvi.oio.inframirror.domain.MonitoredService;

/**
 * Spring Data JPA repository for the MonitoredService entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MonitoredServiceRepository extends JpaRepository<MonitoredService, Long>, JpaSpecificationExecutor<MonitoredService> {
    Optional<MonitoredService> findFirstByName(String name);

    @Query(
        value = "SELECT * FROM monitored_service " +
                "WHERE search_vector @@ to_tsquery('simple', :query || ':*') " +
                "ORDER BY ts_rank(search_vector, to_tsquery('simple', :query || ':*')) DESC",
        countQuery = "SELECT COUNT(*) FROM monitored_service " +
                     "WHERE search_vector @@ to_tsquery('simple', :query || ':*')",
        nativeQuery = true
    )
    Page<MonitoredService> searchFullText(@Param("query") String query, Pageable pageable);

    @Query(
        value = "SELECT * FROM monitored_service " +
                "WHERE search_vector @@ to_tsquery('simple', :query || ':*') " +
                "ORDER BY ts_rank(search_vector, to_tsquery('simple', :query || ':*')) DESC",
        countQuery = "SELECT COUNT(*) FROM monitored_service " +
                     "WHERE search_vector @@ to_tsquery('simple', :query || ':*')",
        nativeQuery = true
    )
    Page<MonitoredService> searchPrefix(@Param("query") String query, Pageable pageable);

    @Query(
        value = "SELECT * FROM monitored_service " +
                "WHERE name % :query OR description % :query OR service_type % :query OR environment % :query " +
                "ORDER BY GREATEST(" +
                "  similarity(name, :query), " +
                "  similarity(COALESCE(description, ''), :query), " +
                "  similarity(service_type, :query), " +
                "  similarity(environment, :query)" +
                ") DESC",
        countQuery = "SELECT COUNT(*) FROM monitored_service " +
                     "WHERE name % :query OR description % :query OR service_type % :query OR environment % :query",
        nativeQuery = true
    )
    Page<MonitoredService> searchFuzzy(@Param("query") String query, Pageable pageable);

    @Query(
        value = "SELECT " +
                "  m.id, " +
                "  m.name, " +
                "  m.description, " +
                "  m.service_type, " +
                "  m.environment, " +
                "  ts_rank(m.search_vector, to_tsquery('simple', :query || ':*')) as rank, " +
                "  ts_headline('simple', " +
                "    COALESCE(m.name, '') || ' ' || COALESCE(m.description, '') || ' ' || COALESCE(m.service_type, '') || ' ' || COALESCE(m.environment, ''), " +
                "    to_tsquery('simple', :query || ':*'), " +
                "    'StartSel=<mark>, StopSel=</mark>, MaxWords=50, MinWords=10') as highlight " +
                "FROM monitored_service m " +
                "WHERE m.search_vector @@ to_tsquery('simple', :query || ':*') " +
                "ORDER BY rank DESC",
        countQuery = "SELECT COUNT(*) FROM monitored_service " +
                     "WHERE search_vector @@ to_tsquery('simple', :query || ':*')",
        nativeQuery = true
    )
    Page<Object[]> searchWithHighlight(@Param("query") String query, Pageable pageable);
}
