package vibhuvi.oio.inframirror.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link vibhuvi.oio.inframirror.domain.ApiKey} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ApiKeyDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    private Boolean active;

    private Instant lastUsedDate;

    private Instant expiresAt;

    private String plainTextKey;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Instant getLastUsedDate() {
        return lastUsedDate;
    }

    public void setLastUsedDate(Instant lastUsedDate) {
        this.lastUsedDate = lastUsedDate;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getPlainTextKey() {
        return plainTextKey;
    }

    public void setPlainTextKey(String plainTextKey) {
        this.plainTextKey = plainTextKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApiKeyDTO)) {
            return false;
        }

        ApiKeyDTO apiKeyDTO = (ApiKeyDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, apiKeyDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ApiKeyDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", active='" + getActive() + "'" +
            ", lastUsedDate='" + getLastUsedDate() + "'" +
            ", expiresAt='" + getExpiresAt() + "'" +
            "}";
    }
}
