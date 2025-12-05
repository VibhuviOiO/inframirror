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
import vibhuvi.oio.inframirror.domain.ServiceHeartbeat;
import vibhuvi.oio.inframirror.repository.ServiceHeartbeatRepository;

/**
 * Spring Data Elasticsearch repository for the {@link ServiceHeartbeat} entity.
 */
public interface ServiceHeartbeatSearchRepository
    extends ElasticsearchRepository<ServiceHeartbeat, Long>, ServiceHeartbeatSearchRepositoryInternal {}

interface ServiceHeartbeatSearchRepositoryInternal {
    Page<ServiceHeartbeat> search(String query, Pageable pageable);

    Page<ServiceHeartbeat> search(Query query);

    @Async
    void index(ServiceHeartbeat entity);

    @Async
    void deleteFromIndexById(Long id);
}

class ServiceHeartbeatSearchRepositoryInternalImpl implements ServiceHeartbeatSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ServiceHeartbeatRepository repository;

    ServiceHeartbeatSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ServiceHeartbeatRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<ServiceHeartbeat> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<ServiceHeartbeat> search(Query query) {
        SearchHits<ServiceHeartbeat> searchHits = elasticsearchTemplate.search(query, ServiceHeartbeat.class);
        List<ServiceHeartbeat> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(ServiceHeartbeat entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), ServiceHeartbeat.class);
    }
}
