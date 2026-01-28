package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.dto;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceResultDto;

import java.util.List;

/**
 * Data transfer object representing the result of a quality hub check.
 */
public record QualityHubResultDto(
        Long changesetId,
        String changesetXml,
        boolean isValid,
        List<QualityServiceResultDto> qualityServiceResults
) {}
