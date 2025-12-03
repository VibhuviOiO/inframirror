package vibhuvi.oio.inframirror.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link vibhuvi.oio.inframirror.domain.SessionLog} entity.
 */
@Schema(
    description = "SessionLog - Tracks user connections to instances (SSH, RDP, Console)\nCritical for security auditing and compliance\n\nLinks to JHipster User entity for audit trail"
)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SessionLogDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 20)
    private String sessionType;

    @NotNull
    private Instant startTime;

    private Instant endTime;

    private Integer duration;

    @NotNull
    @Size(max = 45)
    private String sourceIpAddress;

    @NotNull
    @Size(max = 20)
    private String status;

    @Size(max = 200)
    private String terminationReason;

    private Integer commandsExecuted;

    private Long bytesTransferred;

    @NotNull
    @Size(max = 100)
    private String sessionId;

    @Lob
    private String metadata;

    @NotNull
    private InstanceDTO instance;

    private AgentDTO agent;

    @NotNull
    private UserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getSourceIpAddress() {
        return sourceIpAddress;
    }

    public void setSourceIpAddress(String sourceIpAddress) {
        this.sourceIpAddress = sourceIpAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTerminationReason() {
        return terminationReason;
    }

    public void setTerminationReason(String terminationReason) {
        this.terminationReason = terminationReason;
    }

    public Integer getCommandsExecuted() {
        return commandsExecuted;
    }

    public void setCommandsExecuted(Integer commandsExecuted) {
        this.commandsExecuted = commandsExecuted;
    }

    public Long getBytesTransferred() {
        return bytesTransferred;
    }

    public void setBytesTransferred(Long bytesTransferred) {
        this.bytesTransferred = bytesTransferred;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public InstanceDTO getInstance() {
        return instance;
    }

    public void setInstance(InstanceDTO instance) {
        this.instance = instance;
    }

    public AgentDTO getAgent() {
        return agent;
    }

    public void setAgent(AgentDTO agent) {
        this.agent = agent;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SessionLogDTO)) {
            return false;
        }

        SessionLogDTO sessionLogDTO = (SessionLogDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, sessionLogDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SessionLogDTO{" +
            "id=" + getId() +
            ", sessionType='" + getSessionType() + "'" +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", duration=" + getDuration() +
            ", sourceIpAddress='" + getSourceIpAddress() + "'" +
            ", status='" + getStatus() + "'" +
            ", terminationReason='" + getTerminationReason() + "'" +
            ", commandsExecuted=" + getCommandsExecuted() +
            ", bytesTransferred=" + getBytesTransferred() +
            ", sessionId='" + getSessionId() + "'" +
            ", metadata='" + getMetadata() + "'" +
            ", instance=" + getInstance() +
            ", agent=" + getAgent() +
            ", user=" + getUser() +
            "}";
    }
}
