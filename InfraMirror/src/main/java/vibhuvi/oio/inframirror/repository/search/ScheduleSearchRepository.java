package vibhuvi.oio.inframirror.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import vibhuvi.oio.inframirror.domain.Schedule;
import vibhuvi.oio.inframirror.repository.ScheduleRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Schedule} entity.
 */
public interface ScheduleSearchRepository extends ElasticsearchRepository<Schedule, Long>, ScheduleSearchRepositoryInternal {}

interface ScheduleSearchRepositoryInternal {
    Stream<Schedule> search(String query);

    Stream<Schedule> search(Query query);

    @Async
    void index(Schedule entity);

    @Async
    void deleteFromIndexById(Long id);
}

class ScheduleSearchRepositoryInternalImpl implements ScheduleSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ScheduleRepository repository;

    ScheduleSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ScheduleRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Schedule> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<Schedule> search(Query query) {
        return elasticsearchTemplate.search(query, Schedule.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Schedule entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Schedule.class);
    }
}
