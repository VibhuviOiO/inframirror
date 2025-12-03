package vibhuvi.oio.inframirror.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link vibhuvi.oio.inframirror.domain.Schedule} entity. This class is used
 * in {@link vibhuvi.oio.inframirror.web.rest.ScheduleResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /schedules?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ScheduleCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private IntegerFilter interval;

    private BooleanFilter includeResponseBody;

    private IntegerFilter thresholdsWarning;

    private IntegerFilter thresholdsCritical;

    private LongFilter monitorsId;

    private Boolean distinct;

    public ScheduleCriteria() {}

    public ScheduleCriteria(ScheduleCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.interval = other.optionalInterval().map(IntegerFilter::copy).orElse(null);
        this.includeResponseBody = other.optionalIncludeResponseBody().map(BooleanFilter::copy).orElse(null);
        this.thresholdsWarning = other.optionalThresholdsWarning().map(IntegerFilter::copy).orElse(null);
        this.thresholdsCritical = other.optionalThresholdsCritical().map(IntegerFilter::copy).orElse(null);
        this.monitorsId = other.optionalMonitorsId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ScheduleCriteria copy() {
        return new ScheduleCriteria(this);
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

    public IntegerFilter getInterval() {
        return interval;
    }

    public Optional<IntegerFilter> optionalInterval() {
        return Optional.ofNullable(interval);
    }

    public IntegerFilter interval() {
        if (interval == null) {
            setInterval(new IntegerFilter());
        }
        return interval;
    }

    public void setInterval(IntegerFilter interval) {
        this.interval = interval;
    }

    public BooleanFilter getIncludeResponseBody() {
        return includeResponseBody;
    }

    public Optional<BooleanFilter> optionalIncludeResponseBody() {
        return Optional.ofNullable(includeResponseBody);
    }

    public BooleanFilter includeResponseBody() {
        if (includeResponseBody == null) {
            setIncludeResponseBody(new BooleanFilter());
        }
        return includeResponseBody;
    }

    public void setIncludeResponseBody(BooleanFilter includeResponseBody) {
        this.includeResponseBody = includeResponseBody;
    }

    public IntegerFilter getThresholdsWarning() {
        return thresholdsWarning;
    }

    public Optional<IntegerFilter> optionalThresholdsWarning() {
        return Optional.ofNullable(thresholdsWarning);
    }

    public IntegerFilter thresholdsWarning() {
        if (thresholdsWarning == null) {
            setThresholdsWarning(new IntegerFilter());
        }
        return thresholdsWarning;
    }

    public void setThresholdsWarning(IntegerFilter thresholdsWarning) {
        this.thresholdsWarning = thresholdsWarning;
    }

    public IntegerFilter getThresholdsCritical() {
        return thresholdsCritical;
    }

    public Optional<IntegerFilter> optionalThresholdsCritical() {
        return Optional.ofNullable(thresholdsCritical);
    }

    public IntegerFilter thresholdsCritical() {
        if (thresholdsCritical == null) {
            setThresholdsCritical(new IntegerFilter());
        }
        return thresholdsCritical;
    }

    public void setThresholdsCritical(IntegerFilter thresholdsCritical) {
        this.thresholdsCritical = thresholdsCritical;
    }

    public LongFilter getMonitorsId() {
        return monitorsId;
    }

    public Optional<LongFilter> optionalMonitorsId() {
        return Optional.ofNullable(monitorsId);
    }

    public LongFilter monitorsId() {
        if (monitorsId == null) {
            setMonitorsId(new LongFilter());
        }
        return monitorsId;
    }

    public void setMonitorsId(LongFilter monitorsId) {
        this.monitorsId = monitorsId;
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
        final ScheduleCriteria that = (ScheduleCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(interval, that.interval) &&
            Objects.equals(includeResponseBody, that.includeResponseBody) &&
            Objects.equals(thresholdsWarning, that.thresholdsWarning) &&
            Objects.equals(thresholdsCritical, that.thresholdsCritical) &&
            Objects.equals(monitorsId, that.monitorsId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, interval, includeResponseBody, thresholdsWarning, thresholdsCritical, monitorsId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ScheduleCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalInterval().map(f -> "interval=" + f + ", ").orElse("") +
            optionalIncludeResponseBody().map(f -> "includeResponseBody=" + f + ", ").orElse("") +
            optionalThresholdsWarning().map(f -> "thresholdsWarning=" + f + ", ").orElse("") +
            optionalThresholdsCritical().map(f -> "thresholdsCritical=" + f + ", ").orElse("") +
            optionalMonitorsId().map(f -> "monitorsId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
