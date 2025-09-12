package de.bayern.bvv.geotopo.osm_quality_framework.quality_contract.spi;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_contract.dto.ChangesetQualityRequestDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_contract.dto.ChangesetQualityResultDto;

/**
 * Service Provider Interface (SPI) for running a quality check on a single changeset.
 */
public interface QualityService {

    /**
     * Executes the quality check for the given request.
     */
    ChangesetQualityResultDto checkChangesetQuality(ChangesetQualityRequestDto request);
}
