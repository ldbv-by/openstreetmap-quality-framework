package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.dto;

import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object of an object type tag.
 */
public record TagDto(
        String key,
        TagType type,
        MultiplicityDto multiplicity,
        Map<String, String> dictionary,
        List<TagDto> subTags,
        Boolean isSystem
) {}