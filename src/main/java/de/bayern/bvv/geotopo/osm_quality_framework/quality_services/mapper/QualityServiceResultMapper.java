package de.bayern.bvv.geotopo.osm_quality_framework.quality_services.mapper;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.mapper.ChangesetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceResultDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.model.QualityServiceResult;
import lombok.experimental.UtilityClass;

/**
 * Mapping between {@link QualityServiceResult} and {@link QualityServiceResultDto}.
 */
@UtilityClass
public class QualityServiceResultMapper {

    /**
     * Map quality service result domain to dto.
     */
    public QualityServiceResultDto toDto(QualityServiceResult qualityServiceResult) {
        if (qualityServiceResult == null) return null;

        return new QualityServiceResultDto(
                qualityServiceResult.getQualityServiceId(),
                qualityServiceResult.getChangesetId(),
                ChangesetMapper.toDto(qualityServiceResult.getModifiedChangeset()),
                qualityServiceResult.getErrors().isEmpty(),
                qualityServiceResult.getErrors().stream().map(QualityServiceErrorMapper::toDto).toList()
        );
    }
}
