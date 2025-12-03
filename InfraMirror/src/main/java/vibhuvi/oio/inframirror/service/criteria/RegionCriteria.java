package vibhuvi.oio.inframirror.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link vibhuvi.oio.inframirror.domain.Region} entity. This class is used
 * in {@link vibhuvi.oio.inframirror.web.rest.RegionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /regions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RegionCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter regionCode;

    private StringFilter groupName;

    private LongFilter datacentersId;

    private Boolean distinct;

    public RegionCriteria() {}

    public RegionCriteria(RegionCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.regionCode = other.optionalRegionCode().map(StringFilter::copy).orElse(null);
        this.groupName = other.optionalGroupName().map(StringFilter::copy).orElse(null);
        this.datacentersId = other.optionalDatacentersId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public RegionCriteria copy() {
        return new RegionCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public Optional<StringFilter> optionalName() {
        return Optional.ofNullable(name);
    }

    public StringFilter name() {
        if (name == null) {
            setName(new StringFilter());
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getRegionCode() {
        return regionCode;
    }

    public Optional<StringFilter> optionalRegionCode() {
        return Optional.ofNullable(regionCode);
    }

    public StringFilter regionCode() {
        if (regionCode == null) {
            setRegionCode(new StringFilter());
        }
        return regionCode;
    }

    public void setRegionCode(StringFilter regionCode) {
        this.regionCode = regionCode;
    }

    public StringFilter getGroupName() {
        return groupName;
    }

    public Optional<StringFilter> optionalGroupName() {
        return Optional.ofNullable(groupName);
    }

    public StringFilter groupName() {
        if (groupName == null) {
            setGroupName(new StringFilter());
        }
        return groupName;
    }

    public void setGroupName(StringFilter groupName) {
        this.groupName = groupName;
    }

    public LongFilter getDatacentersId() {
        return datacentersId;
    }

    public Optional<LongFilter> optionalDatacentersId() {
        return Optional.ofNullable(datacentersId);
    }

    public LongFilter datacentersId() {
        if (datacentersId == null) {
            setDatacentersId(new LongFilter());
        }
        return datacentersId;
    }

    public void setDatacentersId(LongFilter datacentersId) {
        this.datacentersId = datacentersId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final RegionCriteria that = (RegionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(regionCode, that.regionCode) &&
            Objects.equals(groupName, that.groupName) &&
            Objects.equals(datacentersId, that.datacentersId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, regionCode, groupName, datacentersId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RegionCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalRegionCode().map(f -> "regionCode=" + f + ", ").orElse("") +
            optionalGroupName().map(f -> "groupName=" + f + ", ").orElse("") +
            optionalDatacentersId().map(f -> "datacentersId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
