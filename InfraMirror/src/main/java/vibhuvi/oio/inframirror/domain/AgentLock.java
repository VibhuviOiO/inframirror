package vibhuvi.oio.inframirror.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A AgentLock.
 */
@Entity
@Table(name = "agent_lock")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AgentLock implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "agent_id", nullable = false)
    private Long agentId;

    @NotNull
    @Column(name = "acquired_at", nullable = false)
    private Instant acquiredAt;

    @NotNull
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AgentLock id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAgentId() {
        return this.agentId;
    }

    public AgentLock agentId(Long agentId) {
        this.setAgentId(agentId);
        return this;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public Instant getAcquiredAt() {
        return this.acquiredAt;
    }

    public AgentLock acquiredAt(Instant acquiredAt) {
        this.setAcquiredAt(acquiredAt);
        return this;
    }

    public void setAcquiredAt(Instant acquiredAt) {
        this.acquiredAt = acquiredAt;
    }

    public Instant getExpiresAt() {
        return this.expiresAt;
    }

    public AgentLock expiresAt(Instant expiresAt) {
        this.setExpiresAt(expiresAt);
        return this;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AgentLock)) {
            return false;
        }
        return getId() != null && getId().equals(((AgentLock) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AgentLock{" +
            "id=" + getId() +
            ", agentId=" + getAgentId() +
            ", acquiredAt='" + getAcquiredAt() + "'" +
            ", expiresAt='" + getExpiresAt() + "'" +
            "}";
    }
}
