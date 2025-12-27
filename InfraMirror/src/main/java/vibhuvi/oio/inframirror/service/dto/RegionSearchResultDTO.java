package vibhuvi.oio.inframirror.service.dto;

/**
 * DTO for Region search results with highlighting.
 * Extends RegionDTO and adds rank and highlight fields.
 */
public class RegionSearchResultDTO extends RegionDTO {

    private Float rank;
    private String highlight;

    public RegionSearchResultDTO() {
        super();
    }

    public RegionSearchResultDTO(Long id, String name, String regionCode, String groupName, Float rank, String highlight) {
        this.setId(id);
        this.setName(name);
        this.setRegionCode(regionCode);
        this.setGroupName(groupName);
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
    public String toString() {
        return "RegionSearchResultDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", regionCode='" + getRegionCode() + "'" +
            ", groupName='" + getGroupName() + "'" +
            ", rank=" + rank +
            ", highlight='" + highlight + "'" +
            "}";
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        if (!(o instanceof RegionSearchResultDTO)) return false;
        RegionSearchResultDTO that = (RegionSearchResultDTO) o;
        return java.util.Objects.equals(rank, that.rank) && 
               java.util.Objects.equals(highlight, that.highlight);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(super.hashCode(), rank, highlight);
    }
}
