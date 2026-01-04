package vibhuvi.oio.inframirror.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vibhuvi.oio.inframirror.domain.Instance;
import vibhuvi.oio.inframirror.repository.base.SearchableRepository;

@Repository
public interface InstanceRepository extends SearchableRepository<Instance>, JpaSpecificationExecutor<Instance> {
    
    @EntityGraph(attributePaths = {"datacenter"})
    Page<Instance> findAll(Pageable pageable);
    
    Optional<Instance> findByHostname(String hostname);
    
    boolean existsByNameIgnoreCase(String name);

    @Query(
        value = "SELECT * FROM instance " +
                "WHERE search_vector @@ to_tsquery('simple', :query || ':*') " +
                "ORDER BY ts_rank(search_vector, to_tsquery('simple', :query || ':*')) DESC",
        countQuery = "SELECT COUNT(*) FROM instance " +
                     "WHERE search_vector @@ to_tsquery('simple', :query || ':*')",
        nativeQuery = true
    )
    Page<Instance> searchFullText(@Param("query") String query, Pageable pageable);

    @Query(
        value = "SELECT * FROM instance " +
                "WHERE search_vector @@ to_tsquery('simple', :query || ':*') " +
                "ORDER BY ts_rank(search_vector, to_tsquery('simple', :query || ':*')) DESC",
        countQuery = "SELECT COUNT(*) FROM instance " +
                     "WHERE search_vector @@ to_tsquery('simple', :query || ':*')",
        nativeQuery = true
    )
    Page<Instance> searchPrefix(@Param("query") String query, Pageable pageable);

    @Query(
        value = "SELECT * FROM instance " +
                "WHERE name % :query OR hostname % :query OR description % :query " +
                "OR private_ip_address % :query OR public_ip_address % :query " +
                "ORDER BY GREATEST(" +
                "  similarity(name, :query), " +
                "  similarity(hostname, :query), " +
                "  similarity(COALESCE(description, ''), :query), " +
                "  similarity(COALESCE(private_ip_address, ''), :query), " +
                "  similarity(COALESCE(public_ip_address, ''), :query)" +
                ") DESC",
        countQuery = "SELECT COUNT(*) FROM instance " +
                     "WHERE name % :query OR hostname % :query OR description % :query " +
                     "OR private_ip_address % :query OR public_ip_address % :query",
        nativeQuery = true
    )
    Page<Instance> searchFuzzy(@Param("query") String query, Pageable pageable);

    @Query(
        value = "SELECT " +
                "  i.id, " +
                "  i.name, " +
                "  i.hostname, " +
                "  i.description, " +
                "  i.private_ip_address, " +
                "  i.public_ip_address, " +
                "  ts_rank(i.search_vector, to_tsquery('simple', :query || ':*')) as rank, " +
                "  ts_headline('simple', " +
                "    COALESCE(i.name, '') || ' ' || COALESCE(i.hostname, '') || ' ' || " +
                "    COALESCE(i.description, '') || ' ' || COALESCE(i.private_ip_address, '') || ' ' || " +
                "    COALESCE(i.public_ip_address, ''), " +
                "    to_tsquery('simple', :query || ':*'), " +
                "    'StartSel=<mark>, StopSel=</mark>, MaxWords=50, MinWords=10') as highlight " +
                "FROM instance i " +
                "WHERE i.search_vector @@ to_tsquery('simple', :query || ':*') " +
                "ORDER BY rank DESC",
        countQuery = "SELECT COUNT(*) FROM instance " +
                     "WHERE search_vector @@ to_tsquery('simple', :query || ':*')",
        nativeQuery = true
    )
    Page<Object[]> searchWithHighlight(@Param("query") String query, Pageable pageable);
}
