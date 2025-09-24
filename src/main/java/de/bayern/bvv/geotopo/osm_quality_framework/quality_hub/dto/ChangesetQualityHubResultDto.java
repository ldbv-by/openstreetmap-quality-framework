package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.dto;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto.ChangesetDto;

public record ChangesetQualityHubResultDto(
        String qualityServiceId,
        boolean isValid,
        ChangesetDto modifiedChangesetDto
) {}
