package de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto.ChangesetDto;

import java.util.List;

/**
 * Data transfer object representing the result of a quality service check.
 */
public record QualityServiceResultDto(
        String qualityServiceId,
        Long changesetId,
        ChangesetDto modifiedChangesetDto,
        boolean isValid,
        List<QualityServiceErrorDto> errors
) {}
