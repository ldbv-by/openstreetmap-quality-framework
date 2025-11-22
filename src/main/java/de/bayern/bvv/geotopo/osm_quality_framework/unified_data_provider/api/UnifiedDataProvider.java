package de.bayern.bvv.geotopo.osm_quality_framework.unified_data_provider.api;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.DataSetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.*;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.FeatureDto;

import java.util.List;
import java.util.Set;

/**
 * Service Provider Interface (SPI) to get feature infos.
 */
public interface UnifiedDataProvider {

    /**
     * Returns features from the data source that match the given filter.
     */
    DataSetDto getDataSet(DataSetFilter dataSetFilter);

    /**
     * Returns all tagged objects from the dataset that satisfy the given spatial relation
     * (e.g., contains, within, intersects) with the provided feature.
     */
    DataSetDto getDataSetBySpatialRelation(FeatureDto featureDto,
                                           Set<SpatialOperator> operators,
                                           DataSetFilter dataSetFilter,
                                           boolean selfCheck);

    /**
     * Returns a data set of all relation members.
     */
    DataSetDto getRelationMembers(Long relationId, String role);
    DataSetDto getRelationMembers(Long relationId, String role, String coordinateReferenceSystem);


    /**
     * Get way nodes as feature.
     */
    List<Feature> getWayNodesAsFeature(TaggedObject taggedObject);
}
