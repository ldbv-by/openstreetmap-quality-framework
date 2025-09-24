package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.feature.dto;

import org.locationtech.jts.geom.Geometry;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public record FeatureDto(
        Long osmId,
        String objectType,
        Map<String, String> tags,
        HashSet<Long> memberOf,
        Geometry geometry,
        Geometry geometryTransformed,
        List<FeatureNodeDto> nodes
) {
}
