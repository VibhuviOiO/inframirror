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
import vibhuvi.oio.inframirror.domain.Datacenter;
import vibhuvi.oio.inframirror.repository.DatacenterRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Datacenter} entity.
 */
public interface DatacenterSearchRepository extends ElasticsearchRepository<Datacenter, Long>, DatacenterSearchRepositoryInternal {}

interface DatacenterSearchRepositoryInternal {
    Page<Datacenter> search(String query, Pageable pageable);

    Page<Datacenter> search(Query query);

    @Async
    void index(Datacenter entity);

    @Async
    void deleteFromIndexById(Long id);
}

class DatacenterSearchRepositoryInternalImpl implements DatacenterSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final DatacenterRepository repository;

    DatacenterSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, DatacenterRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Datacenter> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Datacenter> search(Query query) {
        SearchHits<Datacenter> searchHits = elasticsearchTemplate.search(query, Datacenter.class);
        List<Datacenter> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Datacenter entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Datacenter.class);
    }
}
