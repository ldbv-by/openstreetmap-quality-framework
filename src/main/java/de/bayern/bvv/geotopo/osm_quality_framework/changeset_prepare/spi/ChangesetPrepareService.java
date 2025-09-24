package de.bayern.bvv.geotopo.osm_quality_framework.changeset_prepare.spi;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto.ChangesetDto;

/**
 * Service Provider Interface (SPI) to prepare a changeset.
 */
public interface ChangesetPrepareService {

    /**
     * Prepare Changeset.
     */
     void prepareChangeset(Long changesetId, ChangesetDto changesetDto);
}
