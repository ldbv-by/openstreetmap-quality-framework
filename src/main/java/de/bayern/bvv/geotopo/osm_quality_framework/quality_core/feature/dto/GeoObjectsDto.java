package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.feature.dto;

import java.util.List;

public record GeoObjectsDto(
        List<FeatureDto> nodes,
        List<FeatureDto> ways,
        List<FeatureDto> areas
) {
}
