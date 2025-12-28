package vibhuvi.oio.inframirror.service.dto;

public class StatusPageSearchResultDTO extends StatusPageDTO {

    private Float rank;
    private String highlight;

    public StatusPageSearchResultDTO() {
        super();
    }

    public StatusPageSearchResultDTO(Long id, String name, String slug, String description, Float rank, String highlight) {
        this.setId(id);
        this.setName(name);
        this.setSlug(slug);
        this.setDescription(description);
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
}
