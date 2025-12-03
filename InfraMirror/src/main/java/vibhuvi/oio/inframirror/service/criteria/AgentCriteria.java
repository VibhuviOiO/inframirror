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

    private LongFilter instancesId;

    private LongFilter httpHeartbeatsId;

    private LongFilter pingHeartbeatsId;

    private LongFilter datacenterId;

    private Boolean distinct;

    public AgentCriteria() {}

    public AgentCriteria(AgentCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.instancesId = other.optionalInstancesId().map(LongFilter::copy).orElse(null);
        this.httpHeartbeatsId = other.optionalHttpHeartbeatsId().map(LongFilter::copy).orElse(null);
        this.pingHeartbeatsId = other.optionalPingHeartbeatsId().map(LongFilter::copy).orElse(null);
        this.datacenterId = other.optionalDatacenterId().map(LongFilter::copy).orElse(null);
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

    public LongFilter getInstancesId() {
        return instancesId;
    }

    public Optional<LongFilter> optionalInstancesId() {
        return Optional.ofNullable(instancesId);
    }

    public LongFilter instancesId() {
        if (instancesId == null) {
            setInstancesId(new LongFilter());
        }
        return instancesId;
    }

    public void setInstancesId(LongFilter instancesId) {
        this.instancesId = instancesId;
    }

    public LongFilter getHttpHeartbeatsId() {
        return httpHeartbeatsId;
    }

    public Optional<LongFilter> optionalHttpHeartbeatsId() {
        return Optional.ofNullable(httpHeartbeatsId);
    }

    public LongFilter httpHeartbeatsId() {
        if (httpHeartbeatsId == null) {
            setHttpHeartbeatsId(new LongFilter());
        }
        return httpHeartbeatsId;
    }

    public void setHttpHeartbeatsId(LongFilter httpHeartbeatsId) {
        this.httpHeartbeatsId = httpHeartbeatsId;
    }

    public LongFilter getPingHeartbeatsId() {
        return pingHeartbeatsId;
    }

    public Optional<LongFilter> optionalPingHeartbeatsId() {
        return Optional.ofNullable(pingHeartbeatsId);
    }

    public LongFilter pingHeartbeatsId() {
        if (pingHeartbeatsId == null) {
            setPingHeartbeatsId(new LongFilter());
        }
        return pingHeartbeatsId;
    }

    public void setPingHeartbeatsId(LongFilter pingHeartbeatsId) {
        this.pingHeartbeatsId = pingHeartbeatsId;
    }

    public LongFilter getDatacenterId() {
        return datacenterId;
    }

    public Optional<LongFilter> optionalDatacenterId() {
        return Optional.ofNullable(datacenterId);
    }

    public LongFilter datacenterId() {
        if (datacenterId == null) {
            setDatacenterId(new LongFilter());
        }
        return datacenterId;
    }

    public void setDatacenterId(LongFilter datacenterId) {
        this.datacenterId = datacenterId;
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
            Objects.equals(instancesId, that.instancesId) &&
            Objects.equals(httpHeartbeatsId, that.httpHeartbeatsId) &&
            Objects.equals(pingHeartbeatsId, that.pingHeartbeatsId) &&
            Objects.equals(datacenterId, that.datacenterId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, instancesId, httpHeartbeatsId, pingHeartbeatsId, datacenterId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AgentCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalInstancesId().map(f -> "instancesId=" + f + ", ").orElse("") +
            optionalHttpHeartbeatsId().map(f -> "httpHeartbeatsId=" + f + ", ").orElse("") +
            optionalPingHeartbeatsId().map(f -> "pingHeartbeatsId=" + f + ", ").orElse("") +
            optionalDatacenterId().map(f -> "datacenterId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
