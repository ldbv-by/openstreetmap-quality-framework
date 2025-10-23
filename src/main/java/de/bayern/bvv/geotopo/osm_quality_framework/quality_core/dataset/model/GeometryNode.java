package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

/**
 * Represents a node within a geometry object.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GeometryNode {
    private Long osmId;
    private Long memberOsmId;
    private Point geometry;
    private Point geometryTransformed;
    private Integer sequence;
}