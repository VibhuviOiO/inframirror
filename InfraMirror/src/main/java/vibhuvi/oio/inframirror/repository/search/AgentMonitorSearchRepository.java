package vibhuvi.oio.inframirror.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import vibhuvi.oio.inframirror.domain.AgentMonitor;
import vibhuvi.oio.inframirror.repository.AgentMonitorRepository;

/**
 * Spring Data Elasticsearch repository for the {@link AgentMonitor} entity.
 */
public interface AgentMonitorSearchRepository extends ElasticsearchRepository<AgentMonitor, Long>, AgentMonitorSearchRepositoryInternal {}

interface AgentMonitorSearchRepositoryInternal {
    Stream<AgentMonitor> search(String query);

    Stream<AgentMonitor> search(Query query);

    @Async
    void index(AgentMonitor entity);

    @Async
    void deleteFromIndexById(Long id);
}

class AgentMonitorSearchRepositoryInternalImpl implements AgentMonitorSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final AgentMonitorRepository repository;

    AgentMonitorSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, AgentMonitorRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<AgentMonitor> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<AgentMonitor> search(Query query) {
        return elasticsearchTemplate.search(query, AgentMonitor.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(AgentMonitor entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), AgentMonitor.class);
    }
}
