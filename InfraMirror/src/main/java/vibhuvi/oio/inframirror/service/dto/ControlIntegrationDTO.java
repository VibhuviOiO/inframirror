package vibhuvi.oio.inframirror.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * DTO for {@link vibhuvi.oio.inframirror.domain.ControlIntegration}
 */
public class ControlIntegrationDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String code;

    @NotNull
    @Size(max = 100)
    private String name;

    private String description;

    @Size(max = 50)
    private String category;

    @Size(max = 100)
    private String icon;

    private Boolean supportsMultiDc;

    private Boolean supportsWrite;

    private Boolean isActive;

    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Boolean getSupportsMultiDc() {
        return supportsMultiDc;
    }

    public void setSupportsMultiDc(Boolean supportsMultiDc) {
        this.supportsMultiDc = supportsMultiDc;
    }

    public Boolean getSupportsWrite() {
        return supportsWrite;
    }

    public void setSupportsWrite(Boolean supportsWrite) {
        this.supportsWrite = supportsWrite;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ControlIntegrationDTO)) return false;
        ControlIntegrationDTO that = (ControlIntegrationDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ControlIntegrationDTO{" +
            "id=" + id +
            ", code='" + code + '\'' +
            ", name='" + name + '\'' +
            ", category='" + category + '\'' +
            ", supportsMultiDc=" + supportsMultiDc +
            ", isActive=" + isActive +
            '}';
    }
}
