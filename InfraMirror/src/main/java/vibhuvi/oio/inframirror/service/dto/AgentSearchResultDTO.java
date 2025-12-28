package vibhuvi.oio.inframirror.service.dto;

import java.util.Objects;

/**
 * DTO for Agent search results with highlighting.
 * Extends AgentDTO and adds rank and highlight fields.
 */
public class AgentSearchResultDTO extends AgentDTO {

    private Float rank;
    private String highlight;

    public AgentSearchResultDTO() {
        super();
    }

    public AgentSearchResultDTO(Long id, String name, Float rank, String highlight) {
        this.setId(id);
        this.setName(name);
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
        if (!(o instanceof AgentSearchResultDTO)) return false;
        if (!super.equals(o)) return false;
        AgentSearchResultDTO that = (AgentSearchResultDTO) o;
        return Objects.equals(rank, that.rank) && Objects.equals(highlight, that.highlight);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), rank, highlight);
    }

    @Override
    public String toString() {
        return "AgentSearchResultDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", rank=" + rank +
            ", highlight='" + highlight + "'" +
            "}";
    }
}
