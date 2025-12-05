package vibhuvi.oio.inframirror.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link vibhuvi.oio.inframirror.domain.Datacenter} entity. This class is used
 * in {@link vibhuvi.oio.inframirror.web.rest.DatacenterResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /datacenters?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DatacenterCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter code;

    private StringFilter name;

    private LongFilter instanceId;

    private LongFilter monitoredServiceId;

    private LongFilter regionId;

    private Boolean distinct;

    public DatacenterCriteria() {}

    public DatacenterCriteria(DatacenterCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.code = other.optionalCode().map(StringFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.instanceId = other.optionalInstanceId().map(LongFilter::copy).orElse(null);
        this.monitoredServiceId = other.optionalMonitoredServiceId().map(LongFilter::copy).orElse(null);
        this.regionId = other.optionalRegionId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public DatacenterCriteria copy() {
        return new DatacenterCriteria(this);
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

    public StringFilter getCode() {
        return code;
    }

    public Optional<StringFilter> optionalCode() {
        return Optional.ofNullable(code);
    }

    public StringFilter code() {
        if (code == null) {
            setCode(new StringFilter());
        }
        return code;
    }

    public void setCode(StringFilter code) {
        this.code = code;
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

    public LongFilter getInstanceId() {
        return instanceId;
    }

    public Optional<LongFilter> optionalInstanceId() {
        return Optional.ofNullable(instanceId);
    }

    public LongFilter instanceId() {
        if (instanceId == null) {
            setInstanceId(new LongFilter());
        }
        return instanceId;
    }

    public void setInstanceId(LongFilter instanceId) {
        this.instanceId = instanceId;
    }

    public LongFilter getMonitoredServiceId() {
        return monitoredServiceId;
    }

    public Optional<LongFilter> optionalMonitoredServiceId() {
        return Optional.ofNullable(monitoredServiceId);
    }

    public LongFilter monitoredServiceId() {
        if (monitoredServiceId == null) {
            setMonitoredServiceId(new LongFilter());
        }
        return monitoredServiceId;
    }

    public void setMonitoredServiceId(LongFilter monitoredServiceId) {
        this.monitoredServiceId = monitoredServiceId;
    }

    public LongFilter getRegionId() {
        return regionId;
    }

    public Optional<LongFilter> optionalRegionId() {
        return Optional.ofNullable(regionId);
    }

    public LongFilter regionId() {
        if (regionId == null) {
            setRegionId(new LongFilter());
        }
        return regionId;
    }

    public void setRegionId(LongFilter regionId) {
        this.regionId = regionId;
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
        final DatacenterCriteria that = (DatacenterCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(name, that.name) &&
            Objects.equals(instanceId, that.instanceId) &&
            Objects.equals(monitoredServiceId, that.monitoredServiceId) &&
            Objects.equals(regionId, that.regionId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, name, instanceId, monitoredServiceId, regionId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DatacenterCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCode().map(f -> "code=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalInstanceId().map(f -> "instanceId=" + f + ", ").orElse("") +
            optionalMonitoredServiceId().map(f -> "monitoredServiceId=" + f + ", ").orElse("") +
            optionalRegionId().map(f -> "regionId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
