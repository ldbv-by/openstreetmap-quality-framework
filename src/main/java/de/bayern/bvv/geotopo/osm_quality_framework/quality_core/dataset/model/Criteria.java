package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model;

public sealed interface Criteria permits All, Any, Not, Leaf {}
