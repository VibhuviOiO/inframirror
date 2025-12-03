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
import vibhuvi.oio.inframirror.domain.Instance;
import vibhuvi.oio.inframirror.repository.InstanceRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Instance} entity.
 */
public interface InstanceSearchRepository extends ElasticsearchRepository<Instance, Long>, InstanceSearchRepositoryInternal {}

interface InstanceSearchRepositoryInternal {
    Page<Instance> search(String query, Pageable pageable);

    Page<Instance> search(Query query);

    @Async
    void index(Instance entity);

    @Async
    void deleteFromIndexById(Long id);
}

class InstanceSearchRepositoryInternalImpl implements InstanceSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final InstanceRepository repository;

    InstanceSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, InstanceRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Instance> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Instance> search(Query query) {
        SearchHits<Instance> searchHits = elasticsearchTemplate.search(query, Instance.class);
        List<Instance> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Instance entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Instance.class);
    }
}
