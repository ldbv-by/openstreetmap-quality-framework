package de.bayern.bvv.geotopo.osm_quality_framework.changeset_prepare.api;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto.ChangesetDto;

/**
 * Service Provider Interface (SPI) to prepare a changeset.
 */
public interface ChangesetPrepareService {

    /**
     * Prepare the changeset and persist its data to the database.
     */
     void prepareChangeset(Long changesetId, ChangesetDto changesetDto);
}
