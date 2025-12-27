package vibhuvi.oio.inframirror.service.dto;

/**
 * DTO for HttpMonitor search results with highlighting.
 * Extends HttpMonitorDTO and adds rank and highlight fields.
 */
public class HttpMonitorSearchResultDTO extends HttpMonitorDTO {

    private Float rank;
    private String highlight;

    public HttpMonitorSearchResultDTO() {
        super();
    }

    public HttpMonitorSearchResultDTO(Long id, String name, String url, String description, Float rank, String highlight) {
        this.setId(id);
        this.setName(name);
        this.setUrl(url);
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
