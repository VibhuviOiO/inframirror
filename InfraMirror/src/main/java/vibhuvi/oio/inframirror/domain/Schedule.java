package vibhuvi.oio.inframirror.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Schedule - Defines monitoring schedules and intervals
 * Table: schedules
 */
@Entity
@Table(name = "schedule")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "schedule")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Schedule implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "name", length = 50, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String name;

    @NotNull
    @Column(name = "interval", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer interval;

    @Column(name = "include_response_body")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean includeResponseBody;

    @Column(name = "thresholds_warning")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer thresholdsWarning;

    @Column(name = "thresholds_critical")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer thresholdsCritical;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "schedule")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "heartbeats", "schedule" }, allowSetters = true)
    private Set<HttpMonitor> monitors = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Schedule id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Schedule name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getInterval() {
        return this.interval;
    }

    public Schedule interval(Integer interval) {
        this.setInterval(interval);
        return this;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public Boolean getIncludeResponseBody() {
        return this.includeResponseBody;
    }

    public Schedule includeResponseBody(Boolean includeResponseBody) {
        this.setIncludeResponseBody(includeResponseBody);
        return this;
    }

    public void setIncludeResponseBody(Boolean includeResponseBody) {
        this.includeResponseBody = includeResponseBody;
    }

    public Integer getThresholdsWarning() {
        return this.thresholdsWarning;
    }

    public Schedule thresholdsWarning(Integer thresholdsWarning) {
        this.setThresholdsWarning(thresholdsWarning);
        return this;
    }

    public void setThresholdsWarning(Integer thresholdsWarning) {
        this.thresholdsWarning = thresholdsWarning;
    }

    public Integer getThresholdsCritical() {
        return this.thresholdsCritical;
    }

    public Schedule thresholdsCritical(Integer thresholdsCritical) {
        this.setThresholdsCritical(thresholdsCritical);
        return this;
    }

    public void setThresholdsCritical(Integer thresholdsCritical) {
        this.thresholdsCritical = thresholdsCritical;
    }

    public Set<HttpMonitor> getMonitors() {
        return this.monitors;
    }

    public void setMonitors(Set<HttpMonitor> httpMonitors) {
        if (this.monitors != null) {
            this.monitors.forEach(i -> i.setSchedule(null));
        }
        if (httpMonitors != null) {
            httpMonitors.forEach(i -> i.setSchedule(this));
        }
        this.monitors = httpMonitors;
    }

    public Schedule monitors(Set<HttpMonitor> httpMonitors) {
        this.setMonitors(httpMonitors);
        return this;
    }

    public Schedule addMonitors(HttpMonitor httpMonitor) {
        this.monitors.add(httpMonitor);
        httpMonitor.setSchedule(this);
        return this;
    }

    public Schedule removeMonitors(HttpMonitor httpMonitor) {
        this.monitors.remove(httpMonitor);
        httpMonitor.setSchedule(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Schedule)) {
            return false;
        }
        return getId() != null && getId().equals(((Schedule) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Schedule{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", interval=" + getInterval() +
            ", includeResponseBody='" + getIncludeResponseBody() + "'" +
            ", thresholdsWarning=" + getThresholdsWarning() +
            ", thresholdsCritical=" + getThresholdsCritical() +
            "}";
    }
}
