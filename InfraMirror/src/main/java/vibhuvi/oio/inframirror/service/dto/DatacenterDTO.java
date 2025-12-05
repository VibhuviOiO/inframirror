package vibhuvi.oio.inframirror.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link vibhuvi.oio.inframirror.domain.Datacenter} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DatacenterDTO implements Serializable {

    @NotNull
    private Long id;

    @NotNull
    @Size(max = 10)
    private String code;

    @NotNull
    @Size(max = 50)
    private String name;

    private RegionDTO region;

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

    public RegionDTO getRegion() {
        return region;
    }

    public void setRegion(RegionDTO region) {
        this.region = region;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DatacenterDTO)) {
            return false;
        }

        DatacenterDTO datacenterDTO = (DatacenterDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, datacenterDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DatacenterDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", region=" + getRegion() +
            "}";
    }
}
