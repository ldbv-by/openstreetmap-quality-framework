package de.bayern.bvv.geotopo.osm_quality_framework.quality_services.spi;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.ChangesetQualityServiceRequestDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.ChangesetQualityServiceResultDto;

/**
 * Service Provider Interface (SPI) for running a quality check on a single changeset.
 */
public interface QualityService {

    /**
     * Executes the quality check for the given request.
     */
    ChangesetQualityServiceResultDto checkChangesetQuality(ChangesetQualityServiceRequestDto request);
}
