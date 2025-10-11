package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model;

import java.util.Set;

/**
 * Data transfer object for osm id as search filter.
 */
public record OsmIds(
        Set<Long> nodeIds,
        Set<Long> wayIds,
        Set<Long> areaIds,
        Set<Long> relationIds
) {}
