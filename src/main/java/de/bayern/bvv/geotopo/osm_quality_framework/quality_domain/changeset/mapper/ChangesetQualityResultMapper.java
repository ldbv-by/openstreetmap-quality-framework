package de.bayern.bvv.geotopo.osm_quality_framework.quality_domain.changeset.mapper;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_contract.dto.ChangesetQualityResultDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_domain.changeset.model.ChangesetQualityResult;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ChangesetQualityResultMapper {
    /**
     * Map changeset quality result dto to domain.
     */
    public ChangesetQualityResult toDomain(ChangesetQualityResultDto changesetQualityResultDto) {
        if (changesetQualityResultDto == null) return null;

        return new ChangesetQualityResult(
                changesetQualityResultDto.qualityServiceId(),
                changesetQualityResultDto.isValid(),
                ChangesetMapper.toDomain(changesetQualityResultDto.modifiedChangesetDto()));
    }

    /**
     * Map changeset quality result domain to dto.
     */
    public ChangesetQualityResultDto toDto(ChangesetQualityResult changesetQualityResult) {
        if (changesetQualityResult == null) return null;

        return new ChangesetQualityResultDto(
                changesetQualityResult.getQualityServiceId(),
                changesetQualityResult.isValid(),
                ChangesetMapper.toDto(changesetQualityResult.getModifiedChangeset())
        );
    }


}
