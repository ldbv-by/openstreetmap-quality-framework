package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"id", "version", "changesetId", "lat", "lon", "tags"})
@AllArgsConstructor
@NoArgsConstructor
@Data
public class NodeDto extends OsmPrimitiveDto{
    @XmlAttribute(name = "lat")
    private String lat;

    @XmlAttribute(name = "lon")
    private String lon;

    @XmlAttribute(name = "id")
    @Override
    public Long getId() { return super.getId(); }

    @Override
    public void setId(Long id) {
        super.setId(id);
    }

    @XmlAttribute(name = "version")
    @Override
    public Long getVersion() { return super.getVersion(); }

    @Override
    public void setVersion(Long version) {
        super.setVersion(version);
    }

    @XmlAttribute(name = "changeset")
    @Override
    public Long getChangesetId() { return super.getChangesetId(); }

    @Override
    public void setChangesetId(Long changesetId) {
        super.setChangesetId(changesetId);
    }

    @XmlElement(name = "tag")
    @Override
    public List<TagDto> getTags() { return super.getTags(); }

    @Override
    public void setTags(List<TagDto> tags) {
        super.setTags(tags);
    }
}
