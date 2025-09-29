package de.bayern.bvv.geotopo.osm_quality_framework.quality_services.mapper;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceErrorDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.model.QualityServiceError;
import lombok.experimental.UtilityClass;

/**
 * Mapping between {@link QualityServiceError} and {@link QualityServiceErrorDto}.
 */
@UtilityClass
public class QualityServiceErrorMapper {

    /**
     * Map quality service result domain to dto.
     */
    public QualityServiceErrorDto toDto(QualityServiceError qualityServiceError) {
        if (qualityServiceError == null) return null;

        return new QualityServiceErrorDto(
                qualityServiceError.getErrorText(),
                qualityServiceError.getErrorGeometry()
        );
    }
}
