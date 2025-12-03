package vibhuvi.oio.inframirror.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import vibhuvi.oio.inframirror.domain.ApiKey;
import vibhuvi.oio.inframirror.repository.ApiKeyRepository;

/**
 * Spring Data Elasticsearch repository for the {@link ApiKey} entity.
 */
public interface ApiKeySearchRepository extends ElasticsearchRepository<ApiKey, Long>, ApiKeySearchRepositoryInternal {}

interface ApiKeySearchRepositoryInternal {
    Stream<ApiKey> search(String query);

    Stream<ApiKey> search(Query query);

    @Async
    void index(ApiKey entity);

    @Async
    void deleteFromIndexById(Long id);
}

class ApiKeySearchRepositoryInternalImpl implements ApiKeySearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ApiKeyRepository repository;

    ApiKeySearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ApiKeyRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<ApiKey> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<ApiKey> search(Query query) {
        return elasticsearchTemplate.search(query, ApiKey.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(ApiKey entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), ApiKey.class);
    }
}
