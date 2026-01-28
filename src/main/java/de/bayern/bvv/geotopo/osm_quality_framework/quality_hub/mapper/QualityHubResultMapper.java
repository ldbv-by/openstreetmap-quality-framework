package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.mapper;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.mapper.ChangesetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.util.ChangesetXml;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.dto.QualityHubResultDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.model.QualityHubResult;
import lombok.experimental.UtilityClass;

/**
 * Mapping between {@link QualityHubResult} and {@link QualityHubResultDto}.
 */
@UtilityClass
public class QualityHubResultMapper {

    /**
     * Map quality hub result domain to dto.
     */
    public QualityHubResultDto toDto(QualityHubResult qualityHubResult) {
        if (qualityHubResult == null) return null;

        return new QualityHubResultDto(
                qualityHubResult.getChangeset().getId(),
                ChangesetXml.toXml(qualityHubResult.getChangeset()),
                qualityHubResult.isValid(),
                qualityHubResult.getQualityServiceResults()
        );
    }
}
