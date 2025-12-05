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
import vibhuvi.oio.inframirror.domain.Service;
import vibhuvi.oio.inframirror.repository.ServiceRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Service} entity.
 */
public interface ServiceSearchRepository extends ElasticsearchRepository<Service, Long>, ServiceSearchRepositoryInternal {}

interface ServiceSearchRepositoryInternal {
    Page<Service> search(String query, Pageable pageable);

    Page<Service> search(Query query);

    @Async
    void index(Service entity);

    @Async
    void deleteFromIndexById(Long id);
}

class ServiceSearchRepositoryInternalImpl implements ServiceSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ServiceRepository repository;

    ServiceSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ServiceRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Service> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Service> search(Query query) {
        SearchHits<Service> searchHits = elasticsearchTemplate.search(query, Service.class);
        List<Service> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Service entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Service.class);
    }
}
