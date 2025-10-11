package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model;

import java.util.Map;

/**
 * Feature filter object.
 */
public record FeatureFilter(
    OsmIds osmIds,
    Map<String, String> tags,
    BoundingBox boundingBox
) {}
