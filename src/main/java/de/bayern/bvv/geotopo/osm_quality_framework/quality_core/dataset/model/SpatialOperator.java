package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model;

/**
 * Possible spatial operators.
 */
public enum SpatialOperator {
    CONTAINS,
    WITHIN,
    TOUCHES,
    TOUCHES_ENDPOINT_ONLY,
    COVERS,
    COVERS_BY_MULTILINE_AS_POLYGON,
    COVERED_BY,
    COVERED_BY_BOUNDARY,
    COVERED_BY_MULTILINE_AS_POLYGON,
    EQUALS,
    EQUALS_TOPO,
    EQUALS_BY_MULTILINE_AS_POLYGON,
    EQUALS_TOPO_BY_MULTILINE_AS_POLYGON,
    INTERSECTS,
    OVERLAPS,
    OVERLAPS_BY_MULTILINE_AS_POLYGON,
    CROSSES,
    SURROUNDED_BY
}
