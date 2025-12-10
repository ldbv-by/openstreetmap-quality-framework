package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model;

import java.util.List;

/**
 * Member filter object.
 */
public record MemberFilter(
    String role,
    List<String> objectTypes
) {}
