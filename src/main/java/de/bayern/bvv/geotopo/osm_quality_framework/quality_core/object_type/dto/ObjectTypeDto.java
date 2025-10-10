package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.dto;

import java.util.List;

/**
 * Data Transfer Object of an object type.
 */
public record ObjectTypeDto(
        String name,
        List<TagDto> tags,
        List<RelationDto> relations,
        List<RuleDto> rules
) {}
