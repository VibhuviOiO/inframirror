package vibhuvi.oio.inframirror.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * SessionLog - Tracks user connections to instances (SSH, RDP, Console)
 * Critical for security auditing and compliance
 *
 * Links to JHipster User entity for audit trail
 */
@Entity
@Table(name = "session_log")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "sessionlog")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SessionLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 20)
    @Column(name = "session_type", length = 20, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String sessionType;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @Column(name = "duration")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer duration;

    @NotNull
    @Size(max = 45)
    @Column(name = "source_ip_address", length = 45, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String sourceIpAddress;

    @NotNull
    @Size(max = 20)
    @Column(name = "status", length = 20, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String status;

    @Size(max = 200)
    @Column(name = "termination_reason", length = 200)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String terminationReason;

    @Column(name = "commands_executed")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer commandsExecuted;

    @Column(name = "bytes_transferred")
    private Long bytesTransferred;

    @NotNull
    @Size(max = 100)
    @Column(name = "session_id", length = 100, nullable = false, unique = true)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String sessionId;

    @Lob
    @Column(name = "metadata")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String metadata;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "pingHeartbeats", "datacenter", "agent" }, allowSetters = true)
    private Instance instance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "instances", "httpHeartbeats", "pingHeartbeats", "datacenter" }, allowSetters = true)
    private Agent agent;

    @ManyToOne(optional = false)
    @NotNull
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SessionLog id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSessionType() {
        return this.sessionType;
    }

    public SessionLog sessionType(String sessionType) {
        this.setSessionType(sessionType);
        return this;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public Instant getStartTime() {
        return this.startTime;
    }

    public SessionLog startTime(Instant startTime) {
        this.setStartTime(startTime);
        return this;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return this.endTime;
    }

    public SessionLog endTime(Instant endTime) {
        this.setEndTime(endTime);
        return this;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Integer getDuration() {
        return this.duration;
    }

    public SessionLog duration(Integer duration) {
        this.setDuration(duration);
        return this;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getSourceIpAddress() {
        return this.sourceIpAddress;
    }

    public SessionLog sourceIpAddress(String sourceIpAddress) {
        this.setSourceIpAddress(sourceIpAddress);
        return this;
    }

    public void setSourceIpAddress(String sourceIpAddress) {
        this.sourceIpAddress = sourceIpAddress;
    }

    public String getStatus() {
        return this.status;
    }

    public SessionLog status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTerminationReason() {
        return this.terminationReason;
    }

    public SessionLog terminationReason(String terminationReason) {
        this.setTerminationReason(terminationReason);
        return this;
    }

    public void setTerminationReason(String terminationReason) {
        this.terminationReason = terminationReason;
    }

    public Integer getCommandsExecuted() {
        return this.commandsExecuted;
    }

    public SessionLog commandsExecuted(Integer commandsExecuted) {
        this.setCommandsExecuted(commandsExecuted);
        return this;
    }

    public void setCommandsExecuted(Integer commandsExecuted) {
        this.commandsExecuted = commandsExecuted;
    }

    public Long getBytesTransferred() {
        return this.bytesTransferred;
    }

    public SessionLog bytesTransferred(Long bytesTransferred) {
        this.setBytesTransferred(bytesTransferred);
        return this;
    }

    public void setBytesTransferred(Long bytesTransferred) {
        this.bytesTransferred = bytesTransferred;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public SessionLog sessionId(String sessionId) {
        this.setSessionId(sessionId);
        return this;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getMetadata() {
        return this.metadata;
    }

    public SessionLog metadata(String metadata) {
        this.setMetadata(metadata);
        return this;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public Instance getInstance() {
        return this.instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    public SessionLog instance(Instance instance) {
        this.setInstance(instance);
        return this;
    }

    public Agent getAgent() {
        return this.agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public SessionLog agent(Agent agent) {
        this.setAgent(agent);
        return this;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public SessionLog user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SessionLog)) {
            return false;
        }
        return getId() != null && getId().equals(((SessionLog) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SessionLog{" +
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
            "}";
    }
}
