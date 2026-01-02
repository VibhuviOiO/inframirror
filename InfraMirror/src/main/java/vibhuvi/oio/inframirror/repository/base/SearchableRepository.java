package vibhuvi.oio.inframirror.repository.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Base repository interface for entities with full-text search capabilities.
 *
 * @param <E> Entity type
 */
@NoRepositoryBean
public interface SearchableRepository<E> extends JpaRepository<E, Long> {

    /**
     * Full-text search using PostgreSQL tsvector.
     */
    Page<E> searchFullText(String query, Pageable pageable);

    /**
     * Prefix search for autocomplete.
     */
    Page<E> searchPrefix(String query, Pageable pageable);

    /**
     * Fuzzy search with typo tolerance.
     */
    Page<E> searchFuzzy(String query, Pageable pageable);
}
