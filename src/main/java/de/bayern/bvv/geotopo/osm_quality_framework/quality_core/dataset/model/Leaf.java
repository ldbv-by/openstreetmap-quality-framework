package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model;

import java.util.Map;

public record Leaf(String type, Map<String, Object> params) implements Criteria {}