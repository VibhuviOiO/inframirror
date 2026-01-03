package vibhuvi.oio.inframirror.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vibhuvi.oio.inframirror.domain.Datacenter;
import vibhuvi.oio.inframirror.repository.base.SearchableRepository;

/**
 * Spring Data JPA repository for the Datacenter entity.
 */
@Repository
public interface DatacenterRepository extends SearchableRepository<Datacenter>, JpaSpecificationExecutor<Datacenter> {
    Optional<Datacenter> findByNameAndRegionId(String name, Long regionId);
    
    boolean existsByNameIgnoreCase(String name);

    @Query(
        value = "SELECT * FROM datacenter " +
                "WHERE search_vector @@ to_tsquery('simple', :query || ':*') " +
                "ORDER BY ts_rank(search_vector, to_tsquery('simple', :query || ':*')) DESC",
        countQuery = "SELECT COUNT(*) FROM datacenter " +
                     "WHERE search_vector @@ to_tsquery('simple', :query || ':*')",
        nativeQuery = true
    )
    Page<Datacenter> searchFullText(@Param("query") String query, Pageable pageable);

    @Query(
        value = "SELECT * FROM datacenter " +
                "WHERE search_vector @@ to_tsquery('simple', :query || ':*') " +
                "ORDER BY ts_rank(search_vector, to_tsquery('simple', :query || ':*')) DESC",
        countQuery = "SELECT COUNT(*) FROM datacenter " +
                     "WHERE search_vector @@ to_tsquery('simple', :query || ':*')",
        nativeQuery = true
    )
    Page<Datacenter> searchPrefix(@Param("query") String query, Pageable pageable);

    @Query(
        value = "SELECT * FROM datacenter " +
                "WHERE name % :query OR code % :query " +
                "ORDER BY GREATEST(" +
                "  similarity(name, :query), " +
                "  similarity(COALESCE(code, ''), :query)" +
                ") DESC",
        countQuery = "SELECT COUNT(*) FROM datacenter " +
                     "WHERE name % :query OR code % :query",
        nativeQuery = true
    )
    Page<Datacenter> searchFuzzy(@Param("query") String query, Pageable pageable);

    @Query(value = """
        SELECT d.id, d.name, d.code,
               ts_rank(d.search_vector, plainto_tsquery('english', :query)) as rank,
               ts_headline('english', coalesce(d.name, '') || ' ' || coalesce(d.code, ''), plainto_tsquery('english', :query)) as highlight
        FROM datacenter d
        WHERE d.search_vector @@ plainto_tsquery('english', :query)
        ORDER BY rank DESC
        """, nativeQuery = true)
    Page<Object[]> searchWithHighlight(@Param("query") String query, Pageable pageable);
}
