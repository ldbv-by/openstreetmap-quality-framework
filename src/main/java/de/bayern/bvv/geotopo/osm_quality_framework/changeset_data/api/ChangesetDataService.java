package de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.api;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto.ChangesetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.ChangesetState;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.ChangesetDataSetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.DataSetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.DataSetFilter;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.FeatureFilter;

import java.util.List;
import java.util.Set;

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
    ChangesetDataSetDto getDataSet(Long changesetId, DataSetFilter dataSetFilter);

    /**
     * Returns a data set of all relation members.
     */
    DataSetDto getRelationMembers(Long changesetId, Long relationId, String role, String coordinateReferenceSystem);

    /**
     * Set state on changeset.
     */
    void setChangesetState(Long changesetId, ChangesetState state);

    /**
     * Get not finished changeset ids.
     */
    List<Long> getChangesetIds(Set<ChangesetState> states);
}
