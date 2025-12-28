package vibhuvi.oio.inframirror.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AgentMonitorCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BooleanFilter active;

    private StringFilter monitorType;

    private LongFilter monitorId;

    private LongFilter agentId;

    private Boolean distinct;

    public AgentMonitorCriteria() {}

    public AgentMonitorCriteria(AgentMonitorCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.active = other.optionalActive().map(BooleanFilter::copy).orElse(null);
        this.monitorType = other.optionalMonitorType().map(StringFilter::copy).orElse(null);
        this.monitorId = other.optionalMonitorId().map(LongFilter::copy).orElse(null);
        this.agentId = other.optionalAgentId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public AgentMonitorCriteria copy() {
        return new AgentMonitorCriteria(this);
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

    public BooleanFilter getActive() {
        return active;
    }

    public Optional<BooleanFilter> optionalActive() {
        return Optional.ofNullable(active);
    }

    public BooleanFilter active() {
        if (active == null) {
            setActive(new BooleanFilter());
        }
        return active;
    }

    public void setActive(BooleanFilter active) {
        this.active = active;
    }

    public StringFilter getMonitorType() {
        return monitorType;
    }

    public Optional<StringFilter> optionalMonitorType() {
        return Optional.ofNullable(monitorType);
    }

    public StringFilter monitorType() {
        if (monitorType == null) {
            setMonitorType(new StringFilter());
        }
        return monitorType;
    }

    public void setMonitorType(StringFilter monitorType) {
        this.monitorType = monitorType;
    }

    public LongFilter getMonitorId() {
        return monitorId;
    }

    public Optional<LongFilter> optionalMonitorId() {
        return Optional.ofNullable(monitorId);
    }

    public LongFilter monitorId() {
        if (monitorId == null) {
            setMonitorId(new LongFilter());
        }
        return monitorId;
    }

    public void setMonitorId(LongFilter monitorId) {
        this.monitorId = monitorId;
    }

    public LongFilter getAgentId() {
        return agentId;
    }

    public Optional<LongFilter> optionalAgentId() {
        return Optional.ofNullable(agentId);
    }

    public LongFilter agentId() {
        if (agentId == null) {
            setAgentId(new LongFilter());
        }
        return agentId;
    }

    public void setAgentId(LongFilter agentId) {
        this.agentId = agentId;
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
        final AgentMonitorCriteria that = (AgentMonitorCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(active, that.active) &&
            Objects.equals(monitorType, that.monitorType) &&
            Objects.equals(monitorId, that.monitorId) &&
            Objects.equals(agentId, that.agentId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, active, monitorType, monitorId, agentId, distinct);
    }

    @Override
    public String toString() {
        return "AgentMonitorCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalActive().map(f -> "active=" + f + ", ").orElse("") +
            optionalMonitorType().map(f -> "monitorType=" + f + ", ").orElse("") +
            optionalMonitorId().map(f -> "monitorId=" + f + ", ").orElse("") +
            optionalAgentId().map(f -> "agentId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
