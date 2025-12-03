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
import vibhuvi.oio.inframirror.domain.AuditTrail;
import vibhuvi.oio.inframirror.repository.AuditTrailRepository;

/**
 * Spring Data Elasticsearch repository for the {@link AuditTrail} entity.
 */
public interface AuditTrailSearchRepository extends ElasticsearchRepository<AuditTrail, Long>, AuditTrailSearchRepositoryInternal {}

interface AuditTrailSearchRepositoryInternal {
    Page<AuditTrail> search(String query, Pageable pageable);

    Page<AuditTrail> search(Query query);

    @Async
    void index(AuditTrail entity);

    @Async
    void deleteFromIndexById(Long id);
}

class AuditTrailSearchRepositoryInternalImpl implements AuditTrailSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final AuditTrailRepository repository;

    AuditTrailSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, AuditTrailRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<AuditTrail> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<AuditTrail> search(Query query) {
        SearchHits<AuditTrail> searchHits = elasticsearchTemplate.search(query, AuditTrail.class);
        List<AuditTrail> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(AuditTrail entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), AuditTrail.class);
    }
}
