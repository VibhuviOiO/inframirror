package vibhuvi.oio.inframirror.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import vibhuvi.oio.inframirror.domain.StatusDependency;
import vibhuvi.oio.inframirror.repository.StatusDependencyRepository;

/**
 * Spring Data Elasticsearch repository for the {@link StatusDependency} entity.
 */
public interface StatusDependencySearchRepository
    extends ElasticsearchRepository<StatusDependency, Long>, StatusDependencySearchRepositoryInternal {}

interface StatusDependencySearchRepositoryInternal {
    Page<StatusDependency> search(String query, Pageable pageable);

    Page<StatusDependency> search(Query query);

    @Async
    void index(StatusDependency entity);

    @Async
    void deleteFromIndexById(Long id);
}

class StatusDependencySearchRepositoryInternalImpl implements StatusDependencySearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final StatusDependencyRepository repository;

    StatusDependencySearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, StatusDependencyRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<StatusDependency> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<StatusDependency> search(Query query) {
        SearchHits<StatusDependency> searchHits = elasticsearchTemplate.search(query, StatusDependency.class);
        List<StatusDependency> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(StatusDependency entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), StatusDependency.class);
    }
}
