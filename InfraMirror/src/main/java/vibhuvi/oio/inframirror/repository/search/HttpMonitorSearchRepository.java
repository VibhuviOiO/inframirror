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
import vibhuvi.oio.inframirror.domain.HttpMonitor;
import vibhuvi.oio.inframirror.repository.HttpMonitorRepository;

/**
 * Spring Data Elasticsearch repository for the {@link HttpMonitor} entity.
 */
public interface HttpMonitorSearchRepository extends ElasticsearchRepository<HttpMonitor, Long>, HttpMonitorSearchRepositoryInternal {}

interface HttpMonitorSearchRepositoryInternal {
    Page<HttpMonitor> search(String query, Pageable pageable);

    Page<HttpMonitor> search(Query query);

    @Async
    void index(HttpMonitor entity);

    @Async
    void deleteFromIndexById(Long id);
}

class HttpMonitorSearchRepositoryInternalImpl implements HttpMonitorSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final HttpMonitorRepository repository;

    HttpMonitorSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, HttpMonitorRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<HttpMonitor> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<HttpMonitor> search(Query query) {
        SearchHits<HttpMonitor> searchHits = elasticsearchTemplate.search(query, HttpMonitor.class);
        List<HttpMonitor> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(HttpMonitor entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), HttpMonitor.class);
    }
}
