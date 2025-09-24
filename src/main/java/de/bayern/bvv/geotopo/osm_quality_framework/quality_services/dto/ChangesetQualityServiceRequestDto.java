package de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto.ChangesetDto;

public record ChangesetQualityServiceRequestDto(
        ChangesetDto changesetDto
) {
}
