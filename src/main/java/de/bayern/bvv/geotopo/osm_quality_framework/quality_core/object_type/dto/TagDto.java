package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.dto;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.model.Tag;

import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object of an object type tag.
 */
public record TagDto(
        String key,
        Tag.Type type,
        MultiplicityDto multiplicity,
        Map<String, String> dictionary,
        List<TagDto> subTags
) {}