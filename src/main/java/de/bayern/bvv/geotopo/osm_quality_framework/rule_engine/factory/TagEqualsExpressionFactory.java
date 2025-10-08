package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.factory;

import com.fasterxml.jackson.databind.JsonNode;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.Expression;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.ExpressionFactory;
import org.springframework.stereotype.Component;

/**
 * Evaluates whether a tag value equals to a fixed value.
 */
@Component
public class TagEqualsExpressionFactory implements ExpressionFactory {

    @Override
    public String type() {
        return "tag_equals";
    }

    @Override
    public Expression create(JsonNode json) {
        String tagKey = json.path("tag_key").asText();
        String value = json.path("value").asText();

        if (tagKey == null || tagKey.isBlank()) {
            throw new IllegalArgumentException("tag_exists: 'tag_key' is required");
        }

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("tag_exists: 'value' is required");
        }

        return feature -> {
            String tagValue = feature.getTags().get(tagKey);
            if (tagValue == null) return false;

            return tagValue.equals(value);
        };
    }
}
