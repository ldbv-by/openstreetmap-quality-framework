package de.bayern.bvv.geotopo.osm_quality_framework.geodata_view.api;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.DataSetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.*;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.FeatureDto;

import java.util.List;
import java.util.Set;

/**
 * Public API of the Geodata-View bounded context.
 * <p>
 * Provides access to a unified geospatial view that combines OSM base data with changeset content.
 * This view serves as a queryable data source for quality checks and spatial validation workflows.
 */
public interface GeodataViewService {

    /**
     * Retrieves all features matching the specified filter criteria.
     */
    DataSetDto getDataSet(DataSetFilter dataSetFilter);

    /**
     * Performs a spatial query against the geodata view to retrieve all features
     * that satisfy the specified topological relations (e.g., CONTAINS, WITHIN, INTERSECTS)
     * relative to the provided feature.
     */
    DataSetDto getDataSetBySpatialRelation(FeatureDto featureDto,
                                           Set<SpatialOperator> operators,
                                           DataSetFilter dataSetFilter,
                                           boolean selfCheck);

    /**
     * Returns all members of the specified OSM relation, including their current
     * geometry and attributes as represented in the unified geodata view.
     */
    DataSetDto getRelationMembers(Long relationId, String role);
    DataSetDto getRelationMembers(Long relationId, String role, String coordinateReferenceSystem);

    /**
     * Retrieves the geometry nodes of a way.
     */
    List<Feature> getWayNodesAsFeature(TaggedObject taggedObject);
}
