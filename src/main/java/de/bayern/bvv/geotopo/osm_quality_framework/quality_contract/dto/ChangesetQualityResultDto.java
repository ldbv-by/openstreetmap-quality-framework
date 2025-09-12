package de.bayern.bvv.geotopo.osm_quality_framework.quality_contract.dto;

public record ChangesetQualityResultDto(
        String qualityServiceId,
        boolean isValid,
        ChangesetDto modifiedChangesetDto
) {}
