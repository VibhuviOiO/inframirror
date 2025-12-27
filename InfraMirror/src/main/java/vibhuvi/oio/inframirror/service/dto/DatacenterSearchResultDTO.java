package vibhuvi.oio.inframirror.service.dto;

import java.util.Objects;

/**
 * DTO for Datacenter search results with highlighting.
 * Extends DatacenterDTO and adds rank and highlight fields.
 */
public class DatacenterSearchResultDTO extends DatacenterDTO {

    private Float rank;
    private String highlight;

    public DatacenterSearchResultDTO() {
        super();
    }

    public DatacenterSearchResultDTO(Long id, String name, String code, Float rank, String highlight) {
        this.setId(id);
        this.setName(name);
        this.setCode(code);
        this.rank = rank;
        this.highlight = highlight;
    }

    public Float getRank() {
        return rank;
    }

    public void setRank(Float rank) {
        this.rank = rank;
    }

    public String getHighlight() {
        return highlight;
    }

    public void setHighlight(String highlight) {
        this.highlight = highlight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DatacenterSearchResultDTO)) return false;
        if (!super.equals(o)) return false;
        DatacenterSearchResultDTO that = (DatacenterSearchResultDTO) o;
        return Objects.equals(rank, that.rank) && Objects.equals(highlight, that.highlight);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), rank, highlight);
    }

    @Override
    public String toString() {
        return "DatacenterSearchResultDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", code='" + getCode() + "'" +
            ", rank=" + rank +
            ", highlight='" + highlight + "'" +
            "}";
    }
}
