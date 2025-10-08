package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.rule_engine.factory;

import com.fasterxml.jackson.databind.JsonNode;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.rule_engine.api.Expression;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.rule_engine.api.ExpressionFactory;
import org.springframework.stereotype.Component;

/**
 * Evaluates whether a tag exists on a feature.
 */
@Component
public class TagExistsExpressionFactory implements ExpressionFactory {

    @Override
    public String type() {
        return "tag_exists";
    }

    @Override
    public Expression create(JsonNode json) {
        String tagKey = json.path("tag_key").asText();

        if (tagKey == null || tagKey.isBlank()) {
            throw new IllegalArgumentException("tag_exists: 'tag_key' is required");
        }

        return feature -> feature.getTags().containsKey(tagKey);
    }
}
