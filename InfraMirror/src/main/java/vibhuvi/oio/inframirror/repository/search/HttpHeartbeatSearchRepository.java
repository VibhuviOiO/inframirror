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
import vibhuvi.oio.inframirror.domain.HttpHeartbeat;
import vibhuvi.oio.inframirror.repository.HttpHeartbeatRepository;

/**
 * Spring Data Elasticsearch repository for the {@link HttpHeartbeat} entity.
 */
public interface HttpHeartbeatSearchRepository
    extends ElasticsearchRepository<HttpHeartbeat, Long>, HttpHeartbeatSearchRepositoryInternal {}

interface HttpHeartbeatSearchRepositoryInternal {
    Page<HttpHeartbeat> search(String query, Pageable pageable);

    Page<HttpHeartbeat> search(Query query);

    @Async
    void index(HttpHeartbeat entity);

    @Async
    void deleteFromIndexById(Long id);
}

class HttpHeartbeatSearchRepositoryInternalImpl implements HttpHeartbeatSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final HttpHeartbeatRepository repository;

    HttpHeartbeatSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, HttpHeartbeatRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<HttpHeartbeat> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<HttpHeartbeat> search(Query query) {
        SearchHits<HttpHeartbeat> searchHits = elasticsearchTemplate.search(query, HttpHeartbeat.class);
        List<HttpHeartbeat> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(HttpHeartbeat entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), HttpHeartbeat.class);
    }
}
