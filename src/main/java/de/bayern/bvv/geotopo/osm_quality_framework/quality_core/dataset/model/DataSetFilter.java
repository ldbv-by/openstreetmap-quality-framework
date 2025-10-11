package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model;

import java.util.List;

/**
 * Data Transfer Object that specifies the dataset scope and criteria used for searches.
 */
public record DataSetFilter(
        List<Long> includedChangesetIds,
        FeatureFilter featureFilter,
        String coordinateReferenceSystem
) {}
