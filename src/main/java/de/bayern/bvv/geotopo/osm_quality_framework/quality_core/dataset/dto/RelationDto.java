package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto;

import java.util.List;
import java.util.Map;

/**
 * Data transfer object for an OSM object without a direct geometric reference.
 */
public record RelationDto(
        Long osmId,
        String objectType,
        Map<String, String> tags,
        List<MemberDto> members,
        List<RelationDto> relations
) {}
