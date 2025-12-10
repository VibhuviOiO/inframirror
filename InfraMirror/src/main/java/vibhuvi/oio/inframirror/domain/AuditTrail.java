package vibhuvi.oio.inframirror.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A AuditTrail.
 */
@Entity
@Table(name = "audit_trail")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "audittrail")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AuditTrail implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "action", length = 100, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String action;

    @NotNull
    @Size(max = 100)
    @Column(name = "entity_name", length = 100, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String entityName;

    @NotNull
    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(name = "old_value", columnDefinition = "TEXT")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "TEXT")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String newValue;

    @NotNull
    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    @NotNull
    @Size(max = 50)
    @Column(name = "performed_by", length = 50, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String performedBy;

    @Size(max = 45)
    @Column(name = "ip_address", length = 45)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String userAgent;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AuditTrail id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAction() {
        return this.action;
    }

    public AuditTrail action(String action) {
        this.setAction(action);
        return this;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEntityName() {
        return this.entityName;
    }

    public AuditTrail entityName(String entityName) {
        this.setEntityName(entityName);
        return this;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Long getEntityId() {
        return this.entityId;
    }

    public AuditTrail entityId(Long entityId) {
        this.setEntityId(entityId);
        return this;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getOldValue() {
        return this.oldValue;
    }

    public AuditTrail oldValue(String oldValue) {
        this.setOldValue(oldValue);
        return this;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return this.newValue;
    }

    public AuditTrail newValue(String newValue) {
        this.setNewValue(newValue);
        return this;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public Instant getTimestamp() {
        return this.timestamp;
    }

    public AuditTrail timestamp(Instant timestamp) {
        this.setTimestamp(timestamp);
        return this;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getPerformedBy() {
        return this.performedBy;
    }

    public AuditTrail performedBy(String performedBy) {
        this.setPerformedBy(performedBy);
        return this;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public AuditTrail ipAddress(String ipAddress) {
        this.setIpAddress(ipAddress);
        return this;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return this.userAgent;
    }

    public AuditTrail userAgent(String userAgent) {
        this.setUserAgent(userAgent);
        return this;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuditTrail)) {
            return false;
        }
        return getId() != null && getId().equals(((AuditTrail) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AuditTrail{" +
            "id=" + getId() +
            ", action='" + getAction() + "'" +
            ", entityName='" + getEntityName() + "'" +
            ", entityId=" + getEntityId() +
            ", oldValue='" + getOldValue() + "'" +
            ", newValue='" + getNewValue() + "'" +
            ", timestamp='" + getTimestamp() + "'" +
            ", performedBy='" + getPerformedBy() + "'" +
            ", ipAddress='" + getIpAddress() + "'" +
            ", userAgent='" + getUserAgent() + "'" +
            "}";
    }
}
