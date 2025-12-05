package vibhuvi.oio.inframirror.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A StatusDependency.
 */
@Entity
@Table(name = "status_dependency")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "statusdependency")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StatusDependency implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "parent_type", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String parentType;

    @NotNull
    @Column(name = "parent_id", nullable = false)
    private Long parentId;

    @NotNull
    @Column(name = "child_type", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String childType;

    @NotNull
    @Column(name = "child_id", nullable = false)
    private Long childId;

    @Lob
    @Column(name = "metadata")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String metadata;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "items", "statusDependencies" }, allowSetters = true)
    private StatusPage statusPage;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public StatusDependency id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getParentType() {
        return this.parentType;
    }

    public StatusDependency parentType(String parentType) {
        this.setParentType(parentType);
        return this;
    }

    public void setParentType(String parentType) {
        this.parentType = parentType;
    }

    public Long getParentId() {
        return this.parentId;
    }

    public StatusDependency parentId(Long parentId) {
        this.setParentId(parentId);
        return this;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getChildType() {
        return this.childType;
    }

    public StatusDependency childType(String childType) {
        this.setChildType(childType);
        return this;
    }

    public void setChildType(String childType) {
        this.childType = childType;
    }

    public Long getChildId() {
        return this.childId;
    }

    public StatusDependency childId(Long childId) {
        this.setChildId(childId);
        return this;
    }

    public void setChildId(Long childId) {
        this.childId = childId;
    }

    public String getMetadata() {
        return this.metadata;
    }

    public StatusDependency metadata(String metadata) {
        this.setMetadata(metadata);
        return this;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public StatusDependency createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public StatusPage getStatusPage() {
        return this.statusPage;
    }

    public void setStatusPage(StatusPage statusPage) {
        this.statusPage = statusPage;
    }

    public StatusDependency statusPage(StatusPage statusPage) {
        this.setStatusPage(statusPage);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StatusDependency)) {
            return false;
        }
        return getId() != null && getId().equals(((StatusDependency) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StatusDependency{" +
            "id=" + getId() +
            ", parentType='" + getParentType() + "'" +
            ", parentId=" + getParentId() +
            ", childType='" + getChildType() + "'" +
            ", childId=" + getChildId() +
            ", metadata='" + getMetadata() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
