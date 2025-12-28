package vibhuvi.oio.inframirror.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vibhuvi.oio.inframirror.domain.ApiKey;

/**
 * Spring Data JPA repository for the ApiKey entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
    Optional<ApiKey> findFirstByKeyHashAndActiveTrue(String keyHash);
    
    @Query(
        value = "SELECT * FROM api_key WHERE search_vector @@ to_tsquery('simple', :query || ':*') " +
                "ORDER BY ts_rank(search_vector, to_tsquery('simple', :query || ':*')) DESC",
        countQuery = "SELECT COUNT(*) FROM api_key WHERE search_vector @@ to_tsquery('simple', :query || ':*')",
        nativeQuery = true
    )
    Page<ApiKey> searchFullText(@Param("query") String query, Pageable pageable);

    @Query(
        value = "SELECT * FROM api_key WHERE search_vector @@ to_tsquery('simple', :query || ':*') " +
                "ORDER BY ts_rank(search_vector, to_tsquery('simple', :query || ':*')) DESC",
        countQuery = "SELECT COUNT(*) FROM api_key WHERE search_vector @@ to_tsquery('simple', :query || ':*')",
        nativeQuery = true
    )
    Page<ApiKey> searchPrefix(@Param("query") String query, Pageable pageable);

    @Query(
        value = "SELECT * FROM api_key WHERE name % :query OR description % :query " +
                "ORDER BY GREATEST(similarity(name, :query), similarity(COALESCE(description, ''), :query)) DESC",
        countQuery = "SELECT COUNT(*) FROM api_key WHERE name % :query OR description % :query",
        nativeQuery = true
    )
    Page<ApiKey> searchFuzzy(@Param("query") String query, Pageable pageable);

    @Query(
        value = "SELECT e.id, e.name, e.description, " +
                "ts_rank(e.search_vector, to_tsquery('simple', :query || ':*')) as rank, " +
                "ts_headline('simple', COALESCE(e.name, '') || ' ' || COALESCE(e.description, ''), " +
                "to_tsquery('simple', :query || ':*'), 'StartSel=<mark>, StopSel=</mark>, MaxWords=50, MinWords=10') as highlight " +
                "FROM api_key e WHERE e.search_vector @@ to_tsquery('simple', :query || ':*') " +
                "ORDER BY rank DESC",
        countQuery = "SELECT COUNT(*) FROM api_key WHERE search_vector @@ to_tsquery('simple', :query || ':*')",
        nativeQuery = true
    )
    Page<Object[]> searchWithHighlight(@Param("query") String query, Pageable pageable);
}
