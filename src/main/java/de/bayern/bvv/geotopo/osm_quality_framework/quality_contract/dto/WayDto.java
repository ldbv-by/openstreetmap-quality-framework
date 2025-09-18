package de.bayern.bvv.geotopo.osm_quality_framework.quality_contract.dto;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"id", "version", "changesetId", "nodeRefs", "tags"})
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WayDto extends OsmPrimitiveDto{
    @XmlAttribute(name = "id")
    @Override
    public Long getId() { return super.getId(); }

    @Override
    public void setId(Long id) {
        super.setId(id);
    }

    @XmlAttribute(name = "version")
    @Override
    public String getVersion() { return super.getVersion(); }

    @Override
    public void setVersion(String version) {
        super.setVersion(version);
    }

    @XmlAttribute(name = "changeset")
    @Override
    public Integer getChangesetId() { return super.getChangesetId(); }

    @Override
    public void setChangesetId(Integer changesetId) {
        super.setChangesetId(changesetId);
    }

    @XmlElement(name = "nd")
    private List<Nd> nodeRefs;

    @XmlElement(name = "tag")
    @Override
    public List<TagDto> getTags() { return super.getTags(); }

    @Override
    public void setTags(List<TagDto> tags) {
        super.setTags(tags);
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Nd {
        @XmlAttribute(name = "ref")
        private Long ref;
    }

}
