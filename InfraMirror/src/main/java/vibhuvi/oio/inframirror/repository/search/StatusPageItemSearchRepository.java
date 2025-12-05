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
import vibhuvi.oio.inframirror.domain.StatusPageItem;
import vibhuvi.oio.inframirror.repository.StatusPageItemRepository;

/**
 * Spring Data Elasticsearch repository for the {@link StatusPageItem} entity.
 */
public interface StatusPageItemSearchRepository
    extends ElasticsearchRepository<StatusPageItem, Long>, StatusPageItemSearchRepositoryInternal {}

interface StatusPageItemSearchRepositoryInternal {
    Page<StatusPageItem> search(String query, Pageable pageable);

    Page<StatusPageItem> search(Query query);

    @Async
    void index(StatusPageItem entity);

    @Async
    void deleteFromIndexById(Long id);
}

class StatusPageItemSearchRepositoryInternalImpl implements StatusPageItemSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final StatusPageItemRepository repository;

    StatusPageItemSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, StatusPageItemRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<StatusPageItem> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<StatusPageItem> search(Query query) {
        SearchHits<StatusPageItem> searchHits = elasticsearchTemplate.search(query, StatusPageItem.class);
        List<StatusPageItem> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(StatusPageItem entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), StatusPageItem.class);
    }
}
