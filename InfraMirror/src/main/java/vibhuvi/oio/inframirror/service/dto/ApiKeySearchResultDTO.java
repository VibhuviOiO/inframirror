package vibhuvi.oio.inframirror.service.dto;

public class ApiKeySearchResultDTO extends ApiKeyDTO {

    private Float rank;
    private String highlight;

    public ApiKeySearchResultDTO() {
        super();
    }

    public ApiKeySearchResultDTO(Long id, String name, String description, Float rank, String highlight) {
        this.setId(id);
        this.setName(name);
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
