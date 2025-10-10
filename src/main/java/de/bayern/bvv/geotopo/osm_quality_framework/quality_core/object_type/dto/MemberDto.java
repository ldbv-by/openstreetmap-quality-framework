package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.dto;

/**
 * Data Transfer Object of a relation member.
 */
public record MemberDto(
        String type,
        String role,
        MultiplicityDto multiplicity
) {}
