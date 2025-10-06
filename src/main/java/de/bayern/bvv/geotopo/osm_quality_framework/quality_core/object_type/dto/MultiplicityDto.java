package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.dto;

/**
 * Data Transfer Object of a multiplicity.
 */
public record MultiplicityDto(
        int min,
        int max
) {}