package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.feature.dto;

/**
 * Data transfer object for a specific relation member.
 */
public record MemberDto(
        String type,
        Long ref,
        String role
) {}
