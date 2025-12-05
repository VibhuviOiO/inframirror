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
import vibhuvi.oio.inframirror.domain.Branding;
import vibhuvi.oio.inframirror.repository.BrandingRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Branding} entity.
 */
public interface BrandingSearchRepository extends ElasticsearchRepository<Branding, Long>, BrandingSearchRepositoryInternal {}

interface BrandingSearchRepositoryInternal {
    Page<Branding> search(String query, Pageable pageable);

    Page<Branding> search(Query query);

    @Async
    void index(Branding entity);

    @Async
    void deleteFromIndexById(Long id);
}

class BrandingSearchRepositoryInternalImpl implements BrandingSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final BrandingRepository repository;

    BrandingSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, BrandingRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Branding> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Branding> search(Query query) {
        SearchHits<Branding> searchHits = elasticsearchTemplate.search(query, Branding.class);
        List<Branding> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Branding entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Branding.class);
    }
}
