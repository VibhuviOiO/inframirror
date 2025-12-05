package vibhuvi.oio.inframirror.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link vibhuvi.oio.inframirror.domain.StatusDependency} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StatusDependencyDTO implements Serializable {

    @NotNull
    private Long id;

    @NotNull
    private String parentType;

    @NotNull
    private Long parentId;

    @NotNull
    private String childType;

    @NotNull
    private Long childId;

    @Lob
    private String metadata;

    @NotNull
    private Instant createdAt;

    private StatusPageDTO statusPage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getParentType() {
        return parentType;
    }

    public void setParentType(String parentType) {
        this.parentType = parentType;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getChildType() {
        return childType;
    }

    public void setChildType(String childType) {
        this.childType = childType;
    }

    public Long getChildId() {
        return childId;
    }

    public void setChildId(Long childId) {
        this.childId = childId;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
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
        if (!(o instanceof StatusDependencyDTO)) {
            return false;
        }

        StatusDependencyDTO statusDependencyDTO = (StatusDependencyDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, statusDependencyDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StatusDependencyDTO{" +
            "id=" + getId() +
            ", parentType='" + getParentType() + "'" +
            ", parentId=" + getParentId() +
            ", childType='" + getChildType() + "'" +
            ", childId=" + getChildId() +
            ", metadata='" + getMetadata() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", statusPage=" + getStatusPage() +
            "}";
    }
}
