package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.feature.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Geometry;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a spatial feature with its geometry.
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Feature extends TaggedObject {
    private Geometry geometry;
    private Geometry geometryTransformed;
    private List<GeometryNode> geometryNodes = new ArrayList<>();
}
