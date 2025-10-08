package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.rule_engine.api;

import com.fasterxml.jackson.databind.JsonNode;

public interface ExpressionFactory {
    String type(); // e.g. "tag_exists", "regex_match", ...
    Expression create(JsonNode json);
}
