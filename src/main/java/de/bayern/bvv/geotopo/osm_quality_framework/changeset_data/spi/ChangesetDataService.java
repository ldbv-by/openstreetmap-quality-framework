package de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.spi;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto.ChangesetDto;

/**
 * Service Provider Interface (SPI) for changeset data.
 */
public interface ChangesetDataService {

    /**
     * Moves the prepared data for a given changeset into the changeset_data schema.
     */
     void movePreparedChangeset(Long changesetId, ChangesetDto changesetDto);
}
