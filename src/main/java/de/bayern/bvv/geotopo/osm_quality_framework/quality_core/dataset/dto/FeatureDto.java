package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto;

import org.locationtech.jts.geom.Geometry;

import java.util.List;
import java.util.Map;

/**
 * Data transfer object for a spatial feature with its geometry.
 */
public record FeatureDto(
        Long osmId,
        String objectType,
        Map<String, String> tags,
        List<RelationDto> relations,
        Geometry geometry,
        Geometry geometryTransformed,
        List<GeometryNodeDto> geometryNodes
) {}
