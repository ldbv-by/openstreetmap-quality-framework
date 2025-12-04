package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.api;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.DataSetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.DataSetFilter;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.FeatureFilter;

/**
 * Service Provider Interface (SPI) to get openstreetmap geometries.
 */
public interface OsmGeometriesService {

    /**
     * Append finished changeset to the schema.
     */
    void appendChangeset(Long changesetId);

    /**
     * Returns the current tagged objects.
     */
    DataSetDto getDataSet(DataSetFilter dataSetFilter);

    /**
     * Returns a data set of all relation members.
     */
    DataSetDto getRelationMembers(Long relationId, String role, String coordinateReferenceSystem);
}
