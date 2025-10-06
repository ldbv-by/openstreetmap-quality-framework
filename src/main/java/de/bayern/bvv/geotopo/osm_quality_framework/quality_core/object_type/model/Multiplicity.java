package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.model;

/**
 * Representing the multiplicity of a tag.
 */
public record Multiplicity(
        int min,
        int max
) {}
