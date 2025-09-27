package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object representing an OSM Relation primitive.
 */
@EqualsAndHashCode(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"id", "version", "changesetId", "members", "tags"})
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RelationDto extends OsmPrimitiveDto{

    /**
     * Unique OSM identifier.
     */
    @XmlAttribute(name = "id")
    @Override
    public Long getId() { return super.getId(); }

    @Override
    public void setId(Long id) {
        super.setId(id);
    }

    /**
     * Version of the node.
     */
    @XmlAttribute(name = "version")
    @Override
    public Long getVersion() { return super.getVersion(); }

    @Override
    public void setVersion(Long version) {
        super.setVersion(version);
    }

    /**
     * Changeset identifier in which this relation was modified.
     */
    @XmlAttribute(name = "changeset")
    @Override
    public Long getChangesetId() { return super.getChangesetId(); }

    @Override
    public void setChangesetId(Long changesetId) {
        super.setChangesetId(changesetId);
    }

    /**
     * List of relation members.
     */
    @XmlElement(name = "member")
    private List<MemberDto> members;

    /**
     * List of tags as key-value pairs.
     */
    @XmlElement(name = "tag")
    @Override
    public List<TagDto> getTags() { return super.getTags(); }

    @Override
    public void setTags(List<TagDto> tags) {
        super.setTags(tags);
    }

    /**
     * A specific relation member.
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(propOrder = {"type", "ref", "role"})
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class MemberDto {

        /**
         * Member type, e.g. 'N', 'W', 'R'.
         */
        @XmlAttribute(name = "type")
        private String type;

        /**
         * Member id.
         */
        @XmlAttribute(name = "ref")
        private Long ref;

        /**
         * Member role, e.g. 'outer', 'inner'.
         */
        @XmlAttribute(name = "role")
        private String role;
    }

}
