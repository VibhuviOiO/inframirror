package vibhuvi.oio.inframirror.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link vibhuvi.oio.inframirror.domain.AuditTrail} entity.
 */
@Schema(
    description = "AuditTrail - Comprehensive audit logging for all system changes\nTable: audit_logs\nNote: Renamed from AuditLog to AuditTrail as requested\n\nLinks to JHipster User entity for tracking who made changes"
)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AuditTrailDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 100)
    private String action;

    @NotNull
    @Size(max = 100)
    private String entityName;

    @NotNull
    private Long entityId;

    @Lob
    private String oldValue;

    @Lob
    private String newValue;

    @NotNull
    private Instant timestamp;

    @Size(max = 45)
    private String ipAddress;

    @Lob
    private String userAgent;

    private UserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
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
        if (!(o instanceof AuditTrailDTO)) {
            return false;
        }

        AuditTrailDTO auditTrailDTO = (AuditTrailDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, auditTrailDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AuditTrailDTO{" +
            "id=" + getId() +
            ", action='" + getAction() + "'" +
            ", entityName='" + getEntityName() + "'" +
            ", entityId=" + getEntityId() +
            ", oldValue='" + getOldValue() + "'" +
            ", newValue='" + getNewValue() + "'" +
            ", timestamp='" + getTimestamp() + "'" +
            ", ipAddress='" + getIpAddress() + "'" +
            ", userAgent='" + getUserAgent() + "'" +
            ", user=" + getUser() +
            "}";
    }
}
