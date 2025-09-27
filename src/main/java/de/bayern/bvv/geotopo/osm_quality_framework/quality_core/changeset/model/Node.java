package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Node primitive.
 */
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Node extends OsmPrimitive {

    /**
     * Latitude of the node.
     */
    private String lat;

    /**
     * Longitude of the node.
     */
    private String lon;
}
