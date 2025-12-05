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
import vibhuvi.oio.inframirror.domain.StatusPage;
import vibhuvi.oio.inframirror.repository.StatusPageRepository;

/**
 * Spring Data Elasticsearch repository for the {@link StatusPage} entity.
 */
public interface StatusPageSearchRepository extends ElasticsearchRepository<StatusPage, Long>, StatusPageSearchRepositoryInternal {}

interface StatusPageSearchRepositoryInternal {
    Page<StatusPage> search(String query, Pageable pageable);

    Page<StatusPage> search(Query query);

    @Async
    void index(StatusPage entity);

    @Async
    void deleteFromIndexById(Long id);
}

class StatusPageSearchRepositoryInternalImpl implements StatusPageSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final StatusPageRepository repository;

    StatusPageSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, StatusPageRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<StatusPage> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<StatusPage> search(Query query) {
        SearchHits<StatusPage> searchHits = elasticsearchTemplate.search(query, StatusPage.class);
        List<StatusPage> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(StatusPage entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), StatusPage.class);
    }
}
