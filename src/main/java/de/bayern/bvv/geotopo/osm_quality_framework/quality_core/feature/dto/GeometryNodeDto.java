package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.feature.dto;

import org.locationtech.jts.geom.Point;

/**
 * Data transfer object for a node within a geometry object.
 */
public record GeometryNodeDto(
        Long osmId,
        Point geometry,
        Point geometryTransformed,
        Integer sequence
) {}
