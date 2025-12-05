package vibhuvi.oio.inframirror.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A ApiKey.
 */
@Entity
@Table(name = "api_key")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "apikey")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ApiKey implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "name", length = 100, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String name;

    @Size(max = 500)
    @Column(name = "description", length = 500)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String description;

    @NotNull
    @Size(max = 500)
    @Column(name = "key_hash", length = 500, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String keyHash;

    @NotNull
    @Column(name = "active", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean active;

    @Column(name = "last_used_date")
    private Instant lastUsedDate;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @NotNull
    @Size(max = 50)
    @Column(name = "created_by", length = 50, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String createdBy;

    @Column(name = "created_date")
    private Instant createdDate;

    @Size(max = 50)
    @Column(name = "last_modified_by", length = 50)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    private Instant lastModifiedDate;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ApiKey id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public ApiKey name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public ApiKey description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKeyHash() {
        return this.keyHash;
    }

    public ApiKey keyHash(String keyHash) {
        this.setKeyHash(keyHash);
        return this;
    }

    public void setKeyHash(String keyHash) {
        this.keyHash = keyHash;
    }

    public Boolean getActive() {
        return this.active;
    }

    public ApiKey active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Instant getLastUsedDate() {
        return this.lastUsedDate;
    }

    public ApiKey lastUsedDate(Instant lastUsedDate) {
        this.setLastUsedDate(lastUsedDate);
        return this;
    }

    public void setLastUsedDate(Instant lastUsedDate) {
        this.lastUsedDate = lastUsedDate;
    }

    public Instant getExpiresAt() {
        return this.expiresAt;
    }

    public ApiKey expiresAt(Instant expiresAt) {
        this.setExpiresAt(expiresAt);
        return this;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public ApiKey createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return this.createdDate;
    }

    public ApiKey createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return this.lastModifiedBy;
    }

    public ApiKey lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public ApiKey lastModifiedDate(Instant lastModifiedDate) {
        this.setLastModifiedDate(lastModifiedDate);
        return this;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApiKey)) {
            return false;
        }
        return getId() != null && getId().equals(((ApiKey) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ApiKey{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", keyHash='" + getKeyHash() + "'" +
            ", active='" + getActive() + "'" +
            ", lastUsedDate='" + getLastUsedDate() + "'" +
            ", expiresAt='" + getExpiresAt() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
