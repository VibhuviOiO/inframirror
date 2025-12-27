package vibhuvi.oio.inframirror.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vibhuvi.oio.inframirror.domain.HttpMonitor;

/**
 * Spring Data JPA repository for the HttpMonitor entity.
 */
@SuppressWarnings("unused")
@Repository
public interface HttpMonitorRepository extends JpaRepository<HttpMonitor, Long>, JpaSpecificationExecutor<HttpMonitor> {
    Optional<HttpMonitor> findByName(String name);

    @Query(
        value = "SELECT * FROM http_monitor " +
                "WHERE search_vector @@ to_tsquery('simple', :query || ':*') " +
                "ORDER BY ts_rank(search_vector, to_tsquery('simple', :query || ':*')) DESC",
        countQuery = "SELECT COUNT(*) FROM http_monitor " +
                     "WHERE search_vector @@ to_tsquery('simple', :query || ':*')",
        nativeQuery = true
    )
    Page<HttpMonitor> searchFullText(@Param("query") String query, Pageable pageable);

    @Query(
        value = "SELECT * FROM http_monitor " +
                "WHERE search_vector @@ to_tsquery('simple', :query || ':*') " +
                "ORDER BY ts_rank(search_vector, to_tsquery('simple', :query || ':*')) DESC",
        countQuery = "SELECT COUNT(*) FROM http_monitor " +
                     "WHERE search_vector @@ to_tsquery('simple', :query || ':*')",
        nativeQuery = true
    )
    Page<HttpMonitor> searchPrefix(@Param("query") String query, Pageable pageable);

    @Query(
        value = "SELECT * FROM http_monitor " +
                "WHERE name % :query OR url % :query OR description % :query " +
                "ORDER BY GREATEST(" +
                "  similarity(name, :query), " +
                "  similarity(COALESCE(url, ''), :query), " +
                "  similarity(COALESCE(description, ''), :query)" +
                ") DESC",
        countQuery = "SELECT COUNT(*) FROM http_monitor " +
                     "WHERE name % :query OR url % :query OR description % :query",
        nativeQuery = true
    )
    Page<HttpMonitor> searchFuzzy(@Param("query") String query, Pageable pageable);

    @Query(
        value = "SELECT " +
                "  h.id, " +
                "  h.name, " +
                "  h.url, " +
                "  h.description, " +
                "  ts_rank(h.search_vector, to_tsquery('simple', :query || ':*')) as rank, " +
                "  ts_headline('simple', " +
                "    COALESCE(h.name, '') || ' ' || COALESCE(h.url, '') || ' ' || COALESCE(h.description, ''), " +
                "    to_tsquery('simple', :query || ':*'), " +
                "    'StartSel=<mark>, StopSel=</mark>, MaxWords=50, MinWords=10') as highlight " +
                "FROM http_monitor h " +
                "WHERE h.search_vector @@ to_tsquery('simple', :query || ':*') " +
                "ORDER BY rank DESC",
        countQuery = "SELECT COUNT(*) FROM http_monitor " +
                     "WHERE search_vector @@ to_tsquery('simple', :query || ':*')",
        nativeQuery = true
    )
    Page<Object[]> searchWithHighlight(@Param("query") String query, Pageable pageable);
}
