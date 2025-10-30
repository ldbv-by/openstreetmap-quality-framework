package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model;


/**
 * Data Transfer Object that specifies the dataset scope and criteria used for searches.
 */
public record DataSetFilter(
        Boolean ignoreChangesetData,
        String coordinateReferenceSystem,
        SpatialAggregator aggregator,
        FeatureFilter featureFilter
) {}
