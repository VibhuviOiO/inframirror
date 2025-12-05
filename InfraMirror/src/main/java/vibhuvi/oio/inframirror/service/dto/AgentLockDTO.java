package vibhuvi.oio.inframirror.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link vibhuvi.oio.inframirror.domain.AgentLock} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AgentLockDTO implements Serializable {

    private Long id;

    @NotNull
    private Long agentId;

    @NotNull
    private Instant acquiredAt;

    @NotNull
    private Instant expiresAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public Instant getAcquiredAt() {
        return acquiredAt;
    }

    public void setAcquiredAt(Instant acquiredAt) {
        this.acquiredAt = acquiredAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AgentLockDTO)) {
            return false;
        }

        AgentLockDTO agentLockDTO = (AgentLockDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, agentLockDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AgentLockDTO{" +
            "id=" + getId() +
            ", agentId=" + getAgentId() +
            ", acquiredAt='" + getAcquiredAt() + "'" +
            ", expiresAt='" + getExpiresAt() + "'" +
            "}";
    }
}
