package vibhuvi.oio.inframirror.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link vibhuvi.oio.inframirror.domain.SessionLog} entity. This class is used
 * in {@link vibhuvi.oio.inframirror.web.rest.SessionLogResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /session-logs?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SessionLogCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter sessionType;

    private InstantFilter startTime;

    private InstantFilter endTime;

    private IntegerFilter duration;

    private StringFilter sourceIpAddress;

    private StringFilter status;

    private StringFilter terminationReason;

    private IntegerFilter commandsExecuted;

    private LongFilter bytesTransferred;

    private StringFilter sessionId;

    private LongFilter instanceId;

    private LongFilter agentId;

    private StringFilter userId;

    private Boolean distinct;

    public SessionLogCriteria() {}

    public SessionLogCriteria(SessionLogCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.sessionType = other.optionalSessionType().map(StringFilter::copy).orElse(null);
        this.startTime = other.optionalStartTime().map(InstantFilter::copy).orElse(null);
        this.endTime = other.optionalEndTime().map(InstantFilter::copy).orElse(null);
        this.duration = other.optionalDuration().map(IntegerFilter::copy).orElse(null);
        this.sourceIpAddress = other.optionalSourceIpAddress().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(StringFilter::copy).orElse(null);
        this.terminationReason = other.optionalTerminationReason().map(StringFilter::copy).orElse(null);
        this.commandsExecuted = other.optionalCommandsExecuted().map(IntegerFilter::copy).orElse(null);
        this.bytesTransferred = other.optionalBytesTransferred().map(LongFilter::copy).orElse(null);
        this.sessionId = other.optionalSessionId().map(StringFilter::copy).orElse(null);
        this.instanceId = other.optionalInstanceId().map(LongFilter::copy).orElse(null);
        this.agentId = other.optionalAgentId().map(LongFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(StringFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public SessionLogCriteria copy() {
        return new SessionLogCriteria(this);
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

    public StringFilter getSessionType() {
        return sessionType;
    }

    public Optional<StringFilter> optionalSessionType() {
        return Optional.ofNullable(sessionType);
    }

    public StringFilter sessionType() {
        if (sessionType == null) {
            setSessionType(new StringFilter());
        }
        return sessionType;
    }

    public void setSessionType(StringFilter sessionType) {
        this.sessionType = sessionType;
    }

    public InstantFilter getStartTime() {
        return startTime;
    }

    public Optional<InstantFilter> optionalStartTime() {
        return Optional.ofNullable(startTime);
    }

    public InstantFilter startTime() {
        if (startTime == null) {
            setStartTime(new InstantFilter());
        }
        return startTime;
    }

    public void setStartTime(InstantFilter startTime) {
        this.startTime = startTime;
    }

    public InstantFilter getEndTime() {
        return endTime;
    }

    public Optional<InstantFilter> optionalEndTime() {
        return Optional.ofNullable(endTime);
    }

    public InstantFilter endTime() {
        if (endTime == null) {
            setEndTime(new InstantFilter());
        }
        return endTime;
    }

    public void setEndTime(InstantFilter endTime) {
        this.endTime = endTime;
    }

    public IntegerFilter getDuration() {
        return duration;
    }

    public Optional<IntegerFilter> optionalDuration() {
        return Optional.ofNullable(duration);
    }

    public IntegerFilter duration() {
        if (duration == null) {
            setDuration(new IntegerFilter());
        }
        return duration;
    }

    public void setDuration(IntegerFilter duration) {
        this.duration = duration;
    }

    public StringFilter getSourceIpAddress() {
        return sourceIpAddress;
    }

    public Optional<StringFilter> optionalSourceIpAddress() {
        return Optional.ofNullable(sourceIpAddress);
    }

    public StringFilter sourceIpAddress() {
        if (sourceIpAddress == null) {
            setSourceIpAddress(new StringFilter());
        }
        return sourceIpAddress;
    }

    public void setSourceIpAddress(StringFilter sourceIpAddress) {
        this.sourceIpAddress = sourceIpAddress;
    }

    public StringFilter getStatus() {
        return status;
    }

    public Optional<StringFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public StringFilter status() {
        if (status == null) {
            setStatus(new StringFilter());
        }
        return status;
    }

    public void setStatus(StringFilter status) {
        this.status = status;
    }

    public StringFilter getTerminationReason() {
        return terminationReason;
    }

    public Optional<StringFilter> optionalTerminationReason() {
        return Optional.ofNullable(terminationReason);
    }

    public StringFilter terminationReason() {
        if (terminationReason == null) {
            setTerminationReason(new StringFilter());
        }
        return terminationReason;
    }

    public void setTerminationReason(StringFilter terminationReason) {
        this.terminationReason = terminationReason;
    }

    public IntegerFilter getCommandsExecuted() {
        return commandsExecuted;
    }

    public Optional<IntegerFilter> optionalCommandsExecuted() {
        return Optional.ofNullable(commandsExecuted);
    }

    public IntegerFilter commandsExecuted() {
        if (commandsExecuted == null) {
            setCommandsExecuted(new IntegerFilter());
        }
        return commandsExecuted;
    }

    public void setCommandsExecuted(IntegerFilter commandsExecuted) {
        this.commandsExecuted = commandsExecuted;
    }

    public LongFilter getBytesTransferred() {
        return bytesTransferred;
    }

    public Optional<LongFilter> optionalBytesTransferred() {
        return Optional.ofNullable(bytesTransferred);
    }

    public LongFilter bytesTransferred() {
        if (bytesTransferred == null) {
            setBytesTransferred(new LongFilter());
        }
        return bytesTransferred;
    }

    public void setBytesTransferred(LongFilter bytesTransferred) {
        this.bytesTransferred = bytesTransferred;
    }

    public StringFilter getSessionId() {
        return sessionId;
    }

    public Optional<StringFilter> optionalSessionId() {
        return Optional.ofNullable(sessionId);
    }

    public StringFilter sessionId() {
        if (sessionId == null) {
            setSessionId(new StringFilter());
        }
        return sessionId;
    }

    public void setSessionId(StringFilter sessionId) {
        this.sessionId = sessionId;
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

    public StringFilter getUserId() {
        return userId;
    }

    public Optional<StringFilter> optionalUserId() {
        return Optional.ofNullable(userId);
    }

    public StringFilter userId() {
        if (userId == null) {
            setUserId(new StringFilter());
        }
        return userId;
    }

    public void setUserId(StringFilter userId) {
        this.userId = userId;
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
        final SessionLogCriteria that = (SessionLogCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(sessionType, that.sessionType) &&
            Objects.equals(startTime, that.startTime) &&
            Objects.equals(endTime, that.endTime) &&
            Objects.equals(duration, that.duration) &&
            Objects.equals(sourceIpAddress, that.sourceIpAddress) &&
            Objects.equals(status, that.status) &&
            Objects.equals(terminationReason, that.terminationReason) &&
            Objects.equals(commandsExecuted, that.commandsExecuted) &&
            Objects.equals(bytesTransferred, that.bytesTransferred) &&
            Objects.equals(sessionId, that.sessionId) &&
            Objects.equals(instanceId, that.instanceId) &&
            Objects.equals(agentId, that.agentId) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            sessionType,
            startTime,
            endTime,
            duration,
            sourceIpAddress,
            status,
            terminationReason,
            commandsExecuted,
            bytesTransferred,
            sessionId,
            instanceId,
            agentId,
            userId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SessionLogCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalSessionType().map(f -> "sessionType=" + f + ", ").orElse("") +
            optionalStartTime().map(f -> "startTime=" + f + ", ").orElse("") +
            optionalEndTime().map(f -> "endTime=" + f + ", ").orElse("") +
            optionalDuration().map(f -> "duration=" + f + ", ").orElse("") +
            optionalSourceIpAddress().map(f -> "sourceIpAddress=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalTerminationReason().map(f -> "terminationReason=" + f + ", ").orElse("") +
            optionalCommandsExecuted().map(f -> "commandsExecuted=" + f + ", ").orElse("") +
            optionalBytesTransferred().map(f -> "bytesTransferred=" + f + ", ").orElse("") +
            optionalSessionId().map(f -> "sessionId=" + f + ", ").orElse("") +
            optionalInstanceId().map(f -> "instanceId=" + f + ", ").orElse("") +
            optionalAgentId().map(f -> "agentId=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
