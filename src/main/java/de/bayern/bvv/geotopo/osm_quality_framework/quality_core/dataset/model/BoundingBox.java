package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model;

public record BoundingBox(
        double minX,
        double minY,
        double maxX,
        double maxY
) {}
