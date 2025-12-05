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
import vibhuvi.oio.inframirror.domain.ServiceInstance;
import vibhuvi.oio.inframirror.repository.ServiceInstanceRepository;

/**
 * Spring Data Elasticsearch repository for the {@link ServiceInstance} entity.
 */
public interface ServiceInstanceSearchRepository
    extends ElasticsearchRepository<ServiceInstance, Long>, ServiceInstanceSearchRepositoryInternal {}

interface ServiceInstanceSearchRepositoryInternal {
    Page<ServiceInstance> search(String query, Pageable pageable);

    Page<ServiceInstance> search(Query query);

    @Async
    void index(ServiceInstance entity);

    @Async
    void deleteFromIndexById(Long id);
}

class ServiceInstanceSearchRepositoryInternalImpl implements ServiceInstanceSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ServiceInstanceRepository repository;

    ServiceInstanceSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ServiceInstanceRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<ServiceInstance> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<ServiceInstance> search(Query query) {
        SearchHits<ServiceInstance> searchHits = elasticsearchTemplate.search(query, ServiceInstance.class);
        List<ServiceInstance> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(ServiceInstance entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), ServiceInstance.class);
    }
}
