package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model;

import java.util.List;

public record Any(List<Criteria> items) implements Criteria {}