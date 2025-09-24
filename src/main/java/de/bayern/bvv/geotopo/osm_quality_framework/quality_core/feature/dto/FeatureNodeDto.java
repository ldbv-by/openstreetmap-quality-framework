package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.feature.dto;

import org.locationtech.jts.geom.Point;

public record FeatureNodeDto(
        Long osmId,
        Long lat,
        Long lon,
        Point geometry,
        Point geometryTransformed
) {
}
