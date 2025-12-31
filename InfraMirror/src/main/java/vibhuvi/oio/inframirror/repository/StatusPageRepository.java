package vibhuvi.oio.inframirror.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vibhuvi.oio.inframirror.domain.StatusPage;

/**
 * Spring Data JPA repository for the StatusPage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StatusPageRepository extends JpaRepository<StatusPage, Long>, JpaSpecificationExecutor<StatusPage> {
    
    Optional<StatusPage> findBySlug(String slug);
    
    @Query(
        value = "SELECT sp.id, COUNT(spi.id) FROM status_page sp " +
                "LEFT JOIN status_page_item spi ON sp.id = spi.status_page_id " +
                "WHERE sp.id IN :ids GROUP BY sp.id",
        nativeQuery = true
    )
    java.util.List<Object[]> findItemCountsByStatusPageIds(@Param("ids") java.util.List<Long> ids);
    
    @Query(
        value = "SELECT * FROM status_page WHERE search_vector @@ to_tsquery('simple', :query || ':*') " +
                "ORDER BY ts_rank(search_vector, to_tsquery('simple', :query || ':*')) DESC",
        countQuery = "SELECT COUNT(*) FROM status_page WHERE search_vector @@ to_tsquery('simple', :query || ':*')",
        nativeQuery = true
    )
    Page<StatusPage> searchFullText(@Param("query") String query, Pageable pageable);

    @Query(
        value = "SELECT * FROM status_page WHERE search_vector @@ to_tsquery('simple', :query || ':*') " +
                "ORDER BY ts_rank(search_vector, to_tsquery('simple', :query || ':*')) DESC",
        countQuery = "SELECT COUNT(*) FROM status_page WHERE search_vector @@ to_tsquery('simple', :query || ':*')",
        nativeQuery = true
    )
    Page<StatusPage> searchPrefix(@Param("query") String query, Pageable pageable);

    @Query(
        value = "SELECT * FROM status_page WHERE name % :query OR slug % :query OR description % :query " +
                "ORDER BY GREATEST(similarity(name, :query), similarity(COALESCE(slug, ''), :query), " +
                "similarity(COALESCE(description, ''), :query)) DESC",
        countQuery = "SELECT COUNT(*) FROM status_page WHERE name % :query OR slug % :query OR description % :query",
        nativeQuery = true
    )
    Page<StatusPage> searchFuzzy(@Param("query") String query, Pageable pageable);

    @Query(
        value = "SELECT e.id, e.name, e.slug, e.description, " +
                "ts_rank(e.search_vector, to_tsquery('simple', :query || ':*')) as rank, " +
                "ts_headline('simple', COALESCE(e.name, '') || ' ' || COALESCE(e.slug, '') || ' ' || COALESCE(e.description, ''), " +
                "to_tsquery('simple', :query || ':*'), 'StartSel=<mark>, StopSel=</mark>, MaxWords=50, MinWords=10') as highlight " +
                "FROM status_page e WHERE e.search_vector @@ to_tsquery('simple', :query || ':*') " +
                "ORDER BY rank DESC",
        countQuery = "SELECT COUNT(*) FROM status_page WHERE search_vector @@ to_tsquery('simple', :query || ':*')",
        nativeQuery = true
    )
    Page<Object[]> searchWithHighlight(@Param("query") String query, Pageable pageable);
}
