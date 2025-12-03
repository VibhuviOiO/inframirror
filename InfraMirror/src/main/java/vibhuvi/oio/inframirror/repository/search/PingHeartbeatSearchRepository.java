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
import vibhuvi.oio.inframirror.domain.PingHeartbeat;
import vibhuvi.oio.inframirror.repository.PingHeartbeatRepository;

/**
 * Spring Data Elasticsearch repository for the {@link PingHeartbeat} entity.
 */
public interface PingHeartbeatSearchRepository
    extends ElasticsearchRepository<PingHeartbeat, Long>, PingHeartbeatSearchRepositoryInternal {}

interface PingHeartbeatSearchRepositoryInternal {
    Page<PingHeartbeat> search(String query, Pageable pageable);

    Page<PingHeartbeat> search(Query query);

    @Async
    void index(PingHeartbeat entity);

    @Async
    void deleteFromIndexById(Long id);
}

class PingHeartbeatSearchRepositoryInternalImpl implements PingHeartbeatSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final PingHeartbeatRepository repository;

    PingHeartbeatSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, PingHeartbeatRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<PingHeartbeat> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<PingHeartbeat> search(Query query) {
        SearchHits<PingHeartbeat> searchHits = elasticsearchTemplate.search(query, PingHeartbeat.class);
        List<PingHeartbeat> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(PingHeartbeat entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), PingHeartbeat.class);
    }
}
