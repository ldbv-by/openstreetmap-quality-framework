package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.factory;

import com.fasterxml.jackson.databind.JsonNode;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.parser.Expression;

public interface ExpressionFactory {
    String type(); // e.g. "tag_exists", "tag_regex_match", ...
    Expression create(JsonNode json);
}
