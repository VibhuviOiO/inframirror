package vibhuvi.oio.inframirror.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link vibhuvi.oio.inframirror.domain.HttpMonitor} entity. This class is used
 * in {@link vibhuvi.oio.inframirror.web.rest.HttpMonitorResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /http-monitors?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class HttpMonitorCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter method;

    private StringFilter type;

    private LongFilter heartbeatsId;

    private LongFilter scheduleId;

    private Boolean distinct;

    public HttpMonitorCriteria() {}

    public HttpMonitorCriteria(HttpMonitorCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.method = other.optionalMethod().map(StringFilter::copy).orElse(null);
        this.type = other.optionalType().map(StringFilter::copy).orElse(null);
        this.heartbeatsId = other.optionalHeartbeatsId().map(LongFilter::copy).orElse(null);
        this.scheduleId = other.optionalScheduleId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public HttpMonitorCriteria copy() {
        return new HttpMonitorCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public Optional<StringFilter> optionalName() {
        return Optional.ofNullable(name);
    }

    public StringFilter name() {
        if (name == null) {
            setName(new StringFilter());
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getMethod() {
        return method;
    }

    public Optional<StringFilter> optionalMethod() {
        return Optional.ofNullable(method);
    }

    public StringFilter method() {
        if (method == null) {
            setMethod(new StringFilter());
        }
        return method;
    }

    public void setMethod(StringFilter method) {
        this.method = method;
    }

    public StringFilter getType() {
        return type;
    }

    public Optional<StringFilter> optionalType() {
        return Optional.ofNullable(type);
    }

    public StringFilter type() {
        if (type == null) {
            setType(new StringFilter());
        }
        return type;
    }

    public void setType(StringFilter type) {
        this.type = type;
    }

    public LongFilter getHeartbeatsId() {
        return heartbeatsId;
    }

    public Optional<LongFilter> optionalHeartbeatsId() {
        return Optional.ofNullable(heartbeatsId);
    }

    public LongFilter heartbeatsId() {
        if (heartbeatsId == null) {
            setHeartbeatsId(new LongFilter());
        }
        return heartbeatsId;
    }

    public void setHeartbeatsId(LongFilter heartbeatsId) {
        this.heartbeatsId = heartbeatsId;
    }

    public LongFilter getScheduleId() {
        return scheduleId;
    }

    public Optional<LongFilter> optionalScheduleId() {
        return Optional.ofNullable(scheduleId);
    }

    public LongFilter scheduleId() {
        if (scheduleId == null) {
            setScheduleId(new LongFilter());
        }
        return scheduleId;
    }

    public void setScheduleId(LongFilter scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final HttpMonitorCriteria that = (HttpMonitorCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(method, that.method) &&
            Objects.equals(type, that.type) &&
            Objects.equals(heartbeatsId, that.heartbeatsId) &&
            Objects.equals(scheduleId, that.scheduleId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, method, type, heartbeatsId, scheduleId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HttpMonitorCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalMethod().map(f -> "method=" + f + ", ").orElse("") +
            optionalType().map(f -> "type=" + f + ", ").orElse("") +
            optionalHeartbeatsId().map(f -> "heartbeatsId=" + f + ", ").orElse("") +
            optionalScheduleId().map(f -> "scheduleId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
