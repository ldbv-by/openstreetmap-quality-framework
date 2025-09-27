package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * OSM primitive.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OsmPrimitive {

    /**
     * Unique OSM identifier.
     */
    private Long id;

    /**
     * Version of the node.
     */
    private Long version;

    /**
     * Changeset identifier in which this osm primitive was modified.
     */
    private Long changesetId;

    /**
     * List of tags as key-value pairs.
     */
    private List<Tag> tags;
}
