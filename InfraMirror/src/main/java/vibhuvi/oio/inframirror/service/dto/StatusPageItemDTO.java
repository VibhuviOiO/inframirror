package vibhuvi.oio.inframirror.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link vibhuvi.oio.inframirror.domain.StatusPageItem} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StatusPageItemDTO implements Serializable {

    @NotNull
    private Long id;

    @NotNull
    @Size(max = 50)
    private String itemType;

    @NotNull
    private Long itemId;

    private Integer displayOrder;

    private Instant createdAt;

    @NotNull
    private StatusPageDTO statusPage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public StatusPageDTO getStatusPage() {
        return statusPage;
    }

    public void setStatusPage(StatusPageDTO statusPage) {
        this.statusPage = statusPage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StatusPageItemDTO)) {
            return false;
        }

        StatusPageItemDTO statusPageItemDTO = (StatusPageItemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, statusPageItemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StatusPageItemDTO{" +
            "id=" + getId() +
            ", itemType='" + getItemType() + "'" +
            ", itemId=" + getItemId() +
            ", displayOrder=" + getDisplayOrder() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", statusPage=" + getStatusPage() +
            "}";
    }
}
