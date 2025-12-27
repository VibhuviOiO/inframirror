package vibhuvi.oio.inframirror.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vibhuvi.oio.inframirror.domain.Region;
import vibhuvi.oio.inframirror.service.dto.RegionSearchResultDTO;

/**
 * Spring Data JPA repository for the Region entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RegionRepository extends JpaRepository<Region, Long>, JpaSpecificationExecutor<Region> {
    Optional<Region> findByName(String name);

    /**
     * Full-text search using PostgreSQL tsvector with prefix matching.
     * Returns results ranked by relevance.
     * Supports autocomplete by matching word prefixes.
     *
     * @param query search query string
     * @param pageable pagination parameters
     * @return page of matching regions
     */
    @Query(
        value = "SELECT * FROM region " +
                "WHERE search_vector @@ to_tsquery('simple', :query || ':*') " +
                "ORDER BY ts_rank(search_vector, to_tsquery('simple', :query || ':*')) DESC",
        countQuery = "SELECT COUNT(*) FROM region " +
                     "WHERE search_vector @@ to_tsquery('simple', :query || ':*')",
        nativeQuery = true
    )
    Page<Region> searchFullText(@Param("query") String query, Pageable pageable);

    /**
     * Prefix search for autocomplete (e.g., "eas" matches "east").
     * Uses prefix matching with tsvector for fast autocomplete.
     *
     * @param query search prefix
     * @param pageable pagination parameters
     * @return page of matching regions
     */
    @Query(
        value = "SELECT * FROM region " +
                "WHERE search_vector @@ to_tsquery('simple', :query || ':*') " +
                "ORDER BY ts_rank(search_vector, to_tsquery('simple', :query || ':*')) DESC",
        countQuery = "SELECT COUNT(*) FROM region " +
                     "WHERE search_vector @@ to_tsquery('simple', :query || ':*')",
        nativeQuery = true
    )
    Page<Region> searchPrefix(@Param("query") String query, Pageable pageable);

    /**
     * Fuzzy search using trigram similarity (handles typos).
     * Uses pg_trgm extension for similarity matching.
     *
     * @param query search query string
     * @param pageable pagination parameters
     * @return page of matching regions
     */
    @Query(
        value = "SELECT * FROM region " +
                "WHERE name % :query OR region_code % :query OR group_name % :query " +
                "ORDER BY GREATEST(" +
                "  similarity(name, :query), " +
                "  similarity(COALESCE(region_code, ''), :query), " +
                "  similarity(COALESCE(group_name, ''), :query)" +
                ") DESC",
        countQuery = "SELECT COUNT(*) FROM region " +
                     "WHERE name % :query OR region_code % :query OR group_name % :query",
        nativeQuery = true
    )
    Page<Region> searchFuzzy(@Param("query") String query, Pageable pageable);

    /**
     * Full-text search with highlighting.
     * Returns results with matched text snippets highlighted.
     *
     * @param query search query string
     * @param pageable pagination parameters
     * @return page of search results with highlights
     */
    @Query(
        value = "SELECT " +
                "  r.id, " +
                "  r.name, " +
                "  r.region_code, " +
                "  r.group_name, " +
                "  ts_rank(r.search_vector, plainto_tsquery('simple', :query)) as rank, " +
                "  ts_headline('simple', " +
                "    COALESCE(r.name, '') || ' ' || COALESCE(r.region_code, '') || ' ' || COALESCE(r.group_name, ''), " +
                "    plainto_tsquery('simple', :query), " +
                "    'StartSel=<mark>, StopSel=</mark>, MaxWords=50, MinWords=10') as highlight " +
                "FROM region r " +
                "WHERE r.search_vector @@ plainto_tsquery('simple', :query) " +
                "ORDER BY rank DESC",
        countQuery = "SELECT COUNT(*) FROM region " +
                     "WHERE search_vector @@ plainto_tsquery('simple', :query)",
        nativeQuery = true
    )
    Page<Object[]> searchWithHighlight(@Param("query") String query, Pageable pageable);
}
