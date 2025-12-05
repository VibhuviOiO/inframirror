package vibhuvi.oio.inframirror.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link vibhuvi.oio.inframirror.domain.Agent} entity. This class is used
 * in {@link vibhuvi.oio.inframirror.web.rest.AgentResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /agents?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AgentCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private LongFilter instanceId;

    private LongFilter httpHeartbeatId;

    private LongFilter instanceHeartbeatId;

    private LongFilter serviceHeartbeatId;

    private LongFilter regionId;

    private Boolean distinct;

    public AgentCriteria() {}

    public AgentCriteria(AgentCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.instanceId = other.optionalInstanceId().map(LongFilter::copy).orElse(null);
        this.httpHeartbeatId = other.optionalHttpHeartbeatId().map(LongFilter::copy).orElse(null);
        this.instanceHeartbeatId = other.optionalInstanceHeartbeatId().map(LongFilter::copy).orElse(null);
        this.serviceHeartbeatId = other.optionalServiceHeartbeatId().map(LongFilter::copy).orElse(null);
        this.regionId = other.optionalRegionId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public AgentCriteria copy() {
        return new AgentCriteria(this);
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

    public LongFilter getHttpHeartbeatId() {
        return httpHeartbeatId;
    }

    public Optional<LongFilter> optionalHttpHeartbeatId() {
        return Optional.ofNullable(httpHeartbeatId);
    }

    public LongFilter httpHeartbeatId() {
        if (httpHeartbeatId == null) {
            setHttpHeartbeatId(new LongFilter());
        }
        return httpHeartbeatId;
    }

    public void setHttpHeartbeatId(LongFilter httpHeartbeatId) {
        this.httpHeartbeatId = httpHeartbeatId;
    }

    public LongFilter getInstanceHeartbeatId() {
        return instanceHeartbeatId;
    }

    public Optional<LongFilter> optionalInstanceHeartbeatId() {
        return Optional.ofNullable(instanceHeartbeatId);
    }

    public LongFilter instanceHeartbeatId() {
        if (instanceHeartbeatId == null) {
            setInstanceHeartbeatId(new LongFilter());
        }
        return instanceHeartbeatId;
    }

    public void setInstanceHeartbeatId(LongFilter instanceHeartbeatId) {
        this.instanceHeartbeatId = instanceHeartbeatId;
    }

    public LongFilter getServiceHeartbeatId() {
        return serviceHeartbeatId;
    }

    public Optional<LongFilter> optionalServiceHeartbeatId() {
        return Optional.ofNullable(serviceHeartbeatId);
    }

    public LongFilter serviceHeartbeatId() {
        if (serviceHeartbeatId == null) {
            setServiceHeartbeatId(new LongFilter());
        }
        return serviceHeartbeatId;
    }

    public void setServiceHeartbeatId(LongFilter serviceHeartbeatId) {
        this.serviceHeartbeatId = serviceHeartbeatId;
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
        final AgentCriteria that = (AgentCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(instanceId, that.instanceId) &&
            Objects.equals(httpHeartbeatId, that.httpHeartbeatId) &&
            Objects.equals(instanceHeartbeatId, that.instanceHeartbeatId) &&
            Objects.equals(serviceHeartbeatId, that.serviceHeartbeatId) &&
            Objects.equals(regionId, that.regionId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, instanceId, httpHeartbeatId, instanceHeartbeatId, serviceHeartbeatId, regionId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AgentCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalInstanceId().map(f -> "instanceId=" + f + ", ").orElse("") +
            optionalHttpHeartbeatId().map(f -> "httpHeartbeatId=" + f + ", ").orElse("") +
            optionalInstanceHeartbeatId().map(f -> "instanceHeartbeatId=" + f + ", ").orElse("") +
            optionalServiceHeartbeatId().map(f -> "serviceHeartbeatId=" + f + ", ").orElse("") +
            optionalRegionId().map(f -> "regionId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
