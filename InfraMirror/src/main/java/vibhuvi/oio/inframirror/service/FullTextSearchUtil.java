package vibhuvi.oio.inframirror.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Utility class for PostgreSQL Full-Text Search operations.
 * Provides common functionality for query sanitization and pagination.
 */
public final class FullTextSearchUtil {

    private static final int MAX_SEARCH_RESULTS = 50;

    private FullTextSearchUtil() {
        // Utility class
    }

    /**
     * Sanitizes search query by extracting the last word for prefix matching.
     * This enables autocomplete functionality as users type.
     *
     * @param query the raw search query
     * @return sanitized query (last word, trimmed, lowercase)
     */
    public static String sanitizeQuery(String query) {
        if (query == null || query.isBlank()) {
            return "";
        }
        String sanitized = query.trim().toLowerCase();
        String[] words = sanitized.split("\\s+");
        return words[words.length - 1];
    }

    /**
     * Creates a limited pageable with unsorted order to prevent duplicate ORDER BY clauses.
     * Native queries already include ORDER BY ts_rank, so we must use Sort.unsorted().
     *
     * @param pageable the original pageable
     * @return limited pageable with max results and unsorted order
     */
    public static Pageable createLimitedPageable(Pageable pageable) {
        return PageRequest.of(
            pageable.getPageNumber(),
            Math.min(pageable.getPageSize(), MAX_SEARCH_RESULTS),
            Sort.unsorted()
        );
    }

    /**
     * Normalizes query for standard search (trim and lowercase).
     *
     * @param query the raw search query
     * @return normalized query
     */
    public static String normalizeQuery(String query) {
        return query == null ? "" : query.trim().toLowerCase();
    }

    /**
     * Checks if query is empty or blank.
     *
     * @param query the search query
     * @return true if query is null or blank
     */
    public static boolean isEmptyQuery(String query) {
        return query == null || query.isBlank();
    }
}
