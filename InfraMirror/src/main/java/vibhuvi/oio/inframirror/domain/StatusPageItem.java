package vibhuvi.oio.inframirror.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A StatusPageItem.
 */
@Entity
@Table(name = "status_page_item")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "statuspageitem")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StatusPageItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "item_type", length = 50, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String itemType;

    @NotNull
    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "display_order")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer displayOrder;

    @Column(name = "created_at")
    private Instant createdAt;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "items", "statusDependencies" }, allowSetters = true)
    private StatusPage statusPage;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public StatusPageItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemType() {
        return this.itemType;
    }

    public StatusPageItem itemType(String itemType) {
        this.setItemType(itemType);
        return this;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public Long getItemId() {
        return this.itemId;
    }

    public StatusPageItem itemId(Long itemId) {
        this.setItemId(itemId);
        return this;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Integer getDisplayOrder() {
        return this.displayOrder;
    }

    public StatusPageItem displayOrder(Integer displayOrder) {
        this.setDisplayOrder(displayOrder);
        return this;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public StatusPageItem createdAt(Instant createdAt) {
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

    public StatusPageItem statusPage(StatusPage statusPage) {
        this.setStatusPage(statusPage);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StatusPageItem)) {
            return false;
        }
        return getId() != null && getId().equals(((StatusPageItem) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StatusPageItem{" +
            "id=" + getId() +
            ", itemType='" + getItemType() + "'" +
            ", itemId=" + getItemId() +
            ", displayOrder=" + getDisplayOrder() +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
