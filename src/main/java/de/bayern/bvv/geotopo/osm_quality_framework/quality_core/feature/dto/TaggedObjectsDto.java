package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.feature.dto;

import java.util.List;

/**
 * Data transfer object with all tagged objects.
 */
public record TaggedObjectsDto(
        List<FeatureDto> nodes,
        List<FeatureDto> ways,
        List<FeatureDto> areas,
        List<RelationDto> relations
) {}
