package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.api;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.DataSetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.DataSetFilter;

/**
 * Public API of the OpenStreetMap-Geometries bounded context.
 * <p>
 * Provides access to the authoritative geometry dataset representing
 * the current OSM state, and allows incorporating finalized changesets
 * into this dataset.
 */
public interface OsmGeometriesService {

    /**
     * Applies the given changeset to the OpenStreetMap-Geometries dataset.
     **/
    void appendChangeset(Long changesetId);

    /**
     * Retrieves all current OSM objects that match the supplied filter criteria.
     **/
    DataSetDto getDataSet(DataSetFilter dataSetFilter);

    /**
     * Resolves the members of the specified OSM relation, including their geometry and attributes.
     **/
    DataSetDto getRelationMembers(Long relationId, String role, String coordinateReferenceSystem);
}
