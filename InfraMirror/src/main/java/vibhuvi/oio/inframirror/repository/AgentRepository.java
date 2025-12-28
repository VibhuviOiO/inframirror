package vibhuvi.oio.inframirror.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vibhuvi.oio.inframirror.domain.Agent;

/**
 * Spring Data JPA repository for the Agent entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AgentRepository extends JpaRepository<Agent, Long>, JpaSpecificationExecutor<Agent> {
    Optional<Agent> findByName(String name);

    @Query(
        value = "SELECT * FROM agent " +
                "WHERE search_vector @@ to_tsquery('simple', :query || ':*') " +
                "ORDER BY ts_rank(search_vector, to_tsquery('simple', :query || ':*')) DESC",
        countQuery = "SELECT COUNT(*) FROM agent " +
                     "WHERE search_vector @@ to_tsquery('simple', :query || ':*')",
        nativeQuery = true
    )
    Page<Agent> searchFullText(@Param("query") String query, Pageable pageable);

    @Query(
        value = "SELECT * FROM agent " +
                "WHERE search_vector @@ to_tsquery('simple', :query || ':*') " +
                "ORDER BY ts_rank(search_vector, to_tsquery('simple', :query || ':*')) DESC",
        countQuery = "SELECT COUNT(*) FROM agent " +
                     "WHERE search_vector @@ to_tsquery('simple', :query || ':*')",
        nativeQuery = true
    )
    Page<Agent> searchPrefix(@Param("query") String query, Pageable pageable);

    @Query(
        value = "SELECT * FROM agent " +
                "WHERE name % :query " +
                "ORDER BY similarity(name, :query) DESC",
        countQuery = "SELECT COUNT(*) FROM agent " +
                     "WHERE name % :query",
        nativeQuery = true
    )
    Page<Agent> searchFuzzy(@Param("query") String query, Pageable pageable);

    @Query(
        value = "SELECT " +
                "  a.id, " +
                "  a.name, " +
                "  a.last_seen_at, " +
                "  a.status, " +
                "  ts_rank(a.search_vector, to_tsquery('simple', :query || ':*')) as rank, " +
                "  ts_headline('simple', " +
                "    COALESCE(a.name, ''), " +
                "    to_tsquery('simple', :query || ':*'), " +
                "    'StartSel=<mark>, StopSel=</mark>, MaxWords=50, MinWords=10') as highlight " +
                "FROM agent a " +
                "WHERE a.search_vector @@ to_tsquery('simple', :query || ':*') " +
                "ORDER BY rank DESC",
        countQuery = "SELECT COUNT(*) FROM agent " +
                     "WHERE search_vector @@ to_tsquery('simple', :query || ':*')",
        nativeQuery = true
    )
    Page<Object[]> searchWithHighlight(@Param("query") String query, Pageable pageable);
}
