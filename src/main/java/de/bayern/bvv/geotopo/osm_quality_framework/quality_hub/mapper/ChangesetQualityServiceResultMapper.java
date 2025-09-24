package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.mapper;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.mapper.ChangesetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.model.ChangesetQualityServiceResult;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.ChangesetQualityServiceResultDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ChangesetQualityServiceResultMapper {
    /**
     * Map changeset quality result dto to domain.
     */
    public ChangesetQualityServiceResult toDomain(Long changesetId, ChangesetQualityServiceResultDto changesetQualityServiceResultDto) {
        if (changesetQualityServiceResultDto == null) return null;

        return new ChangesetQualityServiceResult(
                changesetQualityServiceResultDto.qualityServiceId(),
                changesetQualityServiceResultDto.isValid(),
                ChangesetMapper.toDomain(changesetId, changesetQualityServiceResultDto.modifiedChangesetDto()));
    }

    /**
     * Map changeset quality result domain to dto.
     */
    public ChangesetQualityServiceResultDto toDto(ChangesetQualityServiceResult changesetQualityServiceResult) {
        if (changesetQualityServiceResult == null) return null;

        return new ChangesetQualityServiceResultDto(
                changesetQualityServiceResult.getQualityServiceId(),
                changesetQualityServiceResult.isValid(),
                ChangesetMapper.toDto(changesetQualityServiceResult.getModifiedChangeset())
        );
    }


}
