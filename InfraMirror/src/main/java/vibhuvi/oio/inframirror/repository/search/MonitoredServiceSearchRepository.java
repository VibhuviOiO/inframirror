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
import vibhuvi.oio.inframirror.domain.MonitoredService;
import vibhuvi.oio.inframirror.repository.MonitoredServiceRepository;

/**
 * Spring Data Elasticsearch repository for the {@link MonitoredService} entity.
 */
public interface MonitoredServiceSearchRepository
    extends ElasticsearchRepository<MonitoredService, Long>, MonitoredServiceSearchRepositoryInternal {}

interface MonitoredServiceSearchRepositoryInternal {
    Page<MonitoredService> search(String query, Pageable pageable);

    Page<MonitoredService> search(Query query);

    @Async
    void index(MonitoredService entity);

    @Async
    void deleteFromIndexById(Long id);
}

class MonitoredServiceSearchRepositoryInternalImpl implements MonitoredServiceSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final MonitoredServiceRepository repository;

    MonitoredServiceSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, MonitoredServiceRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<MonitoredService> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<MonitoredService> search(Query query) {
        SearchHits<MonitoredService> searchHits = elasticsearchTemplate.search(query, MonitoredService.class);
        List<MonitoredService> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(MonitoredService entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), MonitoredService.class);
    }
}
