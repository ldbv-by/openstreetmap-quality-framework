package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.dto;

import java.util.List;

/**
 * Data Transfer Object of a relation.
 */
public record RelationDto(
        ObjectTypeDto objectType,
        MultiplicityDto multiplicity,
        List<MemberDto> members
) {}