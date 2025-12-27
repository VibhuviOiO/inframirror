package vibhuvi.oio.inframirror.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Base interface for Full-Text Search operations.
 * Entity services can extend this interface to inherit standard FTS methods.
 *
 * @param <D> the DTO type
 * @param <R> the SearchResult DTO type (with highlight)
 */
public interface FullTextSearchService<D, R> {

    /**
     * Standard full-text search with prefix matching for autocomplete.
     *
     * @param query the search query
     * @param pageable pagination information
     * @return page of DTOs matching the query
     */
    Page<D> search(String query, Pageable pageable);

    /**
     * Explicit prefix search for autocomplete.
     *
     * @param query the prefix to search
     * @param pageable pagination information
     * @return page of DTOs matching the prefix
     */
    Page<D> searchPrefix(String query, Pageable pageable);

    /**
     * Fuzzy search with typo tolerance using pg_trgm similarity.
     *
     * @param query the search query
     * @param pageable pagination information
     * @return page of DTOs matching the query with typo tolerance
     */
    Page<D> searchFuzzy(String query, Pageable pageable);

    /**
     * Search with highlighted results showing matched text.
     *
     * @param query the search query
     * @param pageable pagination information
     * @return page of SearchResult DTOs with highlight field
     */
    Page<R> searchWithHighlight(String query, Pageable pageable);
}
