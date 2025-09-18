package de.bayern.bvv.geotopo.osm_quality_framework.quality_contract.dto;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Transferring an OSM changeset in XML format.
 */
@XmlRootElement(name = "osmChange")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"version", "generator", "createPrimitives", "modifyPrimitives", "deletePrimitives"})
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChangesetDto {
    @XmlAttribute(name = "version")
    private String version;

    @XmlAttribute(name = "generator")
    private String generator;

    @XmlElementWrapper(name = "create")
    @XmlElements({
            @XmlElement(name = "node", type = NodeDto.class),
            @XmlElement(name = "way",  type = WayDto.class),
            @XmlElement(name = "relation", type = RelationDto.class)
    })
    private List<OsmPrimitiveDto> createPrimitives = new ArrayList<>();

    @XmlElementWrapper(name = "modify")
    @XmlElements({
            @XmlElement(name = "node", type = NodeDto.class),
            @XmlElement(name = "way",  type = WayDto.class),
            @XmlElement(name = "relation", type = RelationDto.class)
    })
    private List<OsmPrimitiveDto> modifyPrimitives = new ArrayList<>();

    @XmlElementWrapper(name = "delete")
    @XmlElements({
            @XmlElement(name = "node", type = NodeDto.class),
            @XmlElement(name = "way",  type = WayDto.class),
            @XmlElement(name = "relation", type = RelationDto.class)
    })
    private List<OsmPrimitiveDto> deletePrimitives = new ArrayList<>();
}
