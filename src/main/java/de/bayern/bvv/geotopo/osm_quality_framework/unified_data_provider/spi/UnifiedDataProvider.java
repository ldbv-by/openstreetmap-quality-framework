package de.bayern.bvv.geotopo.osm_quality_framework.unified_data_provider.spi;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.DataSetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.DataSetFilter;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.FeatureDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.SpatialOperator;

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
                                           DataSetFilter dataSetFilter);
}
