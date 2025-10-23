package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object of an OSM changeset.
 */
@XmlRootElement(name = "osmChange")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"version", "generator", "createBlocks", "modifyBlocks", "deleteBlocks"})
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChangesetDto {
    /**
     * Changeset version.
     */
    @XmlAttribute(name = "version")
    private String version;

    /**
     * Changeset generator e.g. JOSM.
     */
    @XmlAttribute(name = "generator")
    private String generator;

    /**
     * OSM primitives created in this changeset.
     */
    @XmlElement(name = "create")
    private List<OsmPrimitiveBlockDto> createBlocks = new ArrayList<>();

    /**
     * OSM primitives modified in this changeset.
     */
    @XmlElement(name = "modify")
    private List<OsmPrimitiveBlockDto> modifyBlocks = new ArrayList<>();

    /**
     * OSM primitives deleted in this changeset.
     */
    @XmlElement(name = "delete")
    private List<OsmPrimitiveBlockDto> deleteBlocks = new ArrayList<>();


    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OsmPrimitiveBlockDto {
        @XmlElements({
                @XmlElement(name = "node", type = NodeDto.class),
                @XmlElement(name = "way", type = WayDto.class),
                @XmlElement(name = "relation", type = RelationDto.class)
        })
        private List<OsmPrimitiveDto> primitives = new ArrayList<>();
    }
}
