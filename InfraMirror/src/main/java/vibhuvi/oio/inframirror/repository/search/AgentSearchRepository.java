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
import vibhuvi.oio.inframirror.domain.Agent;
import vibhuvi.oio.inframirror.repository.AgentRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Agent} entity.
 */
public interface AgentSearchRepository extends ElasticsearchRepository<Agent, Long>, AgentSearchRepositoryInternal {}

interface AgentSearchRepositoryInternal {
    Page<Agent> search(String query, Pageable pageable);

    Page<Agent> search(Query query);

    @Async
    void index(Agent entity);

    @Async
    void deleteFromIndexById(Long id);
}

class AgentSearchRepositoryInternalImpl implements AgentSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final AgentRepository repository;

    AgentSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, AgentRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Agent> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Agent> search(Query query) {
        SearchHits<Agent> searchHits = elasticsearchTemplate.search(query, Agent.class);
        List<Agent> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Agent entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Agent.class);
    }
}
