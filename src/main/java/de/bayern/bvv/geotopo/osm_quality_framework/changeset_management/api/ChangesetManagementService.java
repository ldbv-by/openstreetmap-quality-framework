package de.bayern.bvv.geotopo.osm_quality_framework.changeset_management.api;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto.ChangesetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.ChangesetState;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.ChangesetDataSetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.DataSetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.DataSetFilter;

import java.util.List;
import java.util.Set;

/**
 * Public API for managing changeset data within the Changeset-Management bounded context.
 * This interface defines the contract for persisting incoming changesets,
 * managing their lifecycle state and exposing filtered views of the data
 */
public interface ChangesetManagementService {

    /**
     * Persists the incoming changeset and prepares its geometry and attribute data
     * for downstream quality validation.
     */
    void persistChangeset(Long changesetId, ChangesetDto changesetDto);

    /**
     * Updates the lifecycle state of a changeset.
     */
    void setChangesetState(Long changesetId, ChangesetState state);

    /**
     * Retrieves the current data of the changeset’s OSM objects filtered by a domain-specific data filter.
     */
    ChangesetDataSetDto getDataSet(Long changesetId, DataSetFilter dataSetFilter);

    /**
     * Provides the relation members of a given OSM relation, including their geometric representation.
     */
    DataSetDto getRelationMembers(Long changesetId, Long relationId, String role, String coordinateReferenceSystem);

    /**
     * Returns the identifiers of all changesets currently in one of the given states.
     */
    List<Long> getChangesetIds(Set<ChangesetState> states);
}
