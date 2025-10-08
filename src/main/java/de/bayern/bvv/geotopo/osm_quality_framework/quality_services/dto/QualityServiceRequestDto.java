package de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto.ChangesetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.feature.dto.TaggedObjectsDto;

/**
 * Data transfer object used to check a changeset in a quality service.
 */
public record QualityServiceRequestDto(
        String qualityServiceId,
        Long changesetId,
        ChangesetDto changesetDto,
        TaggedObjectsDto taggedObjectsDto
) {}
