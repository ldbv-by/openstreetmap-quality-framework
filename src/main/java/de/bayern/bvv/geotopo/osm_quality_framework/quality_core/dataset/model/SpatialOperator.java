package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model;

/**
 * Possible spatial operators.
 */
public enum SpatialOperator {
    CONTAINS,
    WITHIN,
    TOUCHES,
    COVERED_BY,
    COVERED_BY_BOUNDARY,
    EQUALS,
    EQUALS_TOPO,
    INTERSECTS,
    OVERLAPS,
    CROSSES,
    SURROUNDED_BY
}
