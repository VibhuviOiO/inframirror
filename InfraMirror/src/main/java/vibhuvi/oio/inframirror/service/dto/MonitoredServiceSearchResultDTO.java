package vibhuvi.oio.inframirror.service.dto;

import java.util.Objects;

/**
 * DTO for MonitoredService search results with highlighting.
 * Extends MonitoredServiceDTO and adds rank and highlight fields.
 */
public class MonitoredServiceSearchResultDTO extends MonitoredServiceDTO {

    private Float rank;
    private String highlight;

    public MonitoredServiceSearchResultDTO() {
        super();
    }

    public MonitoredServiceSearchResultDTO(Long id, String name, String description, String serviceType, String environment, Float rank, String highlight) {
        this.setId(id);
        this.setName(name);
        this.setDescription(description);
        this.setServiceType(serviceType);
        this.setEnvironment(environment);
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
        if (!(o instanceof MonitoredServiceSearchResultDTO)) return false;
        if (!super.equals(o)) return false;
        MonitoredServiceSearchResultDTO that = (MonitoredServiceSearchResultDTO) o;
        return Objects.equals(rank, that.rank) && Objects.equals(highlight, that.highlight);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), rank, highlight);
    }

    @Override
    public String toString() {
        return "MonitoredServiceSearchResultDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", serviceType='" + getServiceType() + "'" +
            ", environment='" + getEnvironment() + "'" +
            ", rank=" + rank +
            ", highlight='" + highlight + "'" +
            "}";
    }
}
