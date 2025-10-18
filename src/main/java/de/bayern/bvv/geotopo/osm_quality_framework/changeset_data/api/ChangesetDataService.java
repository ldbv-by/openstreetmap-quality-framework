package de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.api;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto.ChangesetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.ChangesetDataSetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.DataSetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.FeatureFilter;

/**
 * Service Provider Interface (SPI) for changeset data.
 */
public interface ChangesetDataService {

    /**
     * Moves the prepared data for a given changeset into the changeset_data schema.
     */
     void movePreparedChangeset(Long changesetId, ChangesetDto changesetDto);

    /**
     * Returns the current objects of a changeset matching the given filter.
     */
    ChangesetDataSetDto getDataSet(Long changesetId, FeatureFilter featureFilter, String coordinateReferenceSystem);

    /**
     * Returns a data set of all relation members.
     */
    DataSetDto getRelationMembers(Long changesetId, Long relationId, String role, String coordinateReferenceSystem);
}
