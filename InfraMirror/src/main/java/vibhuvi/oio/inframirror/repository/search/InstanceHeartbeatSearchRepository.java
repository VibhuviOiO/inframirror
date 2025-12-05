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
import vibhuvi.oio.inframirror.domain.InstanceHeartbeat;
import vibhuvi.oio.inframirror.repository.InstanceHeartbeatRepository;

/**
 * Spring Data Elasticsearch repository for the {@link InstanceHeartbeat} entity.
 */
public interface InstanceHeartbeatSearchRepository
    extends ElasticsearchRepository<InstanceHeartbeat, Long>, InstanceHeartbeatSearchRepositoryInternal {}

interface InstanceHeartbeatSearchRepositoryInternal {
    Page<InstanceHeartbeat> search(String query, Pageable pageable);

    Page<InstanceHeartbeat> search(Query query);

    @Async
    void index(InstanceHeartbeat entity);

    @Async
    void deleteFromIndexById(Long id);
}

class InstanceHeartbeatSearchRepositoryInternalImpl implements InstanceHeartbeatSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final InstanceHeartbeatRepository repository;

    InstanceHeartbeatSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, InstanceHeartbeatRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<InstanceHeartbeat> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<InstanceHeartbeat> search(Query query) {
        SearchHits<InstanceHeartbeat> searchHits = elasticsearchTemplate.search(query, InstanceHeartbeat.class);
        List<InstanceHeartbeat> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(InstanceHeartbeat entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), InstanceHeartbeat.class);
    }
}
