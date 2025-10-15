package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.api;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.DataSetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.FeatureFilter;

/**
 * Service Provider Interface (SPI) to get openstreetmap geometries.
 */
public interface OsmGeometriesService {

    /**
     * Returns the current tagged objects.
     */
    DataSetDto getDataSet(FeatureFilter featureFilter, String coordinateReferenceSystem);
}
