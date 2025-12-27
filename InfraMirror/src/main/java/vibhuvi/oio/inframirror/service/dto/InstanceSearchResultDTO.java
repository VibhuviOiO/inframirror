package vibhuvi.oio.inframirror.service.dto;

import java.util.Objects;

/**
 * DTO for Instance search results with highlighting.
 * Extends InstanceDTO and adds rank and highlight fields.
 */
public class InstanceSearchResultDTO extends InstanceDTO {

    private Float rank;
    private String highlight;

    public InstanceSearchResultDTO() {
        super();
    }

    public InstanceSearchResultDTO(Long id, String name, String hostname, String description, 
                                    String privateIpAddress, String publicIpAddress, 
                                    Float rank, String highlight) {
        this.setId(id);
        this.setName(name);
        this.setHostname(hostname);
        this.setDescription(description);
        this.setPrivateIpAddress(privateIpAddress);
        this.setPublicIpAddress(publicIpAddress);
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
        if (!(o instanceof InstanceSearchResultDTO)) return false;
        if (!super.equals(o)) return false;
        InstanceSearchResultDTO that = (InstanceSearchResultDTO) o;
        return Objects.equals(rank, that.rank) && Objects.equals(highlight, that.highlight);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), rank, highlight);
    }

    @Override
    public String toString() {
        return "InstanceSearchResultDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", hostname='" + getHostname() + "'" +
            ", rank=" + rank +
            ", highlight='" + highlight + "'" +
            "}";
    }
}
