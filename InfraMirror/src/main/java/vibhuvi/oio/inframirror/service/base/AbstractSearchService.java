package vibhuvi.oio.inframirror.service.base;

import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.repository.base.SearchableRepository;
import vibhuvi.oio.inframirror.service.FullTextSearchUtil;
import vibhuvi.oio.inframirror.service.mapper.EntityMapper;

/**
 * Generic base service for search operations.
 * Eliminates code duplication across all entity search services.
 *
 * @param <D> DTO type
 * @param <E> Entity type
 */
public abstract class AbstractSearchService<D, E> {

    protected static final int MAX_QUERY_LENGTH = 100;

    protected abstract Logger getLogger();
    protected abstract SearchableRepository<E> getRepository();
    protected abstract EntityMapper<D, E> getMapper();
    protected abstract String getEntityName();

    protected void validateSearchParams(String query, Pageable pageable) {
        if (query != null && query.length() > MAX_QUERY_LENGTH) {
            throw new IllegalArgumentException("Search query too long (max " + MAX_QUERY_LENGTH + " characters)");
        }
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
    }

    @Transactional(readOnly = true)
    public Page<D> search(String query, Pageable pageable) {
        validateSearchParams(query, pageable);
        getLogger().debug("Request to search {} : {}", getEntityName(), query);

        if (FullTextSearchUtil.isEmptyQuery(query)) {
            return getRepository().findAll(pageable).map(getMapper()::toDto);
        }

        String searchTerm = FullTextSearchUtil.normalizeQuery(query);
        Pageable limitedPageable = FullTextSearchUtil.createLimitedPageable(pageable);

        return getRepository().searchFullText(searchTerm, limitedPageable).map(getMapper()::toDto);
    }

    @Transactional(readOnly = true)
    public Page<D> searchPrefix(String query, Pageable pageable) {
        validateSearchParams(query, pageable);
        getLogger().debug("Request to prefix search {} : {}", getEntityName(), query);

        if (FullTextSearchUtil.isEmptyQuery(query)) {
            return Page.empty(pageable);
        }

        String normalizedQuery = FullTextSearchUtil.normalizeQuery(query);
        Pageable limitedPageable = FullTextSearchUtil.createLimitedPageable(pageable);

        return getRepository().searchPrefix(normalizedQuery, limitedPageable).map(getMapper()::toDto);
    }

    @Transactional(readOnly = true)
    public Page<D> searchFuzzy(String query, Pageable pageable) {
        validateSearchParams(query, pageable);
        getLogger().debug("Request to fuzzy search {} : {}", getEntityName(), query);

        if (FullTextSearchUtil.isEmptyQuery(query)) {
            return Page.empty(pageable);
        }

        String normalizedQuery = FullTextSearchUtil.normalizeQuery(query);
        Pageable limitedPageable = FullTextSearchUtil.createLimitedPageable(pageable);

        return getRepository().searchFuzzy(normalizedQuery, limitedPageable).map(getMapper()::toDto);
    }
}
