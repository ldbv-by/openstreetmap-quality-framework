package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.factory;

import com.fasterxml.jackson.databind.JsonNode;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.Expression;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.ExpressionFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Evaluates whether a tag value is contained in a set of allowed values.
 */
@Component
public class TagInExpressionFactory implements ExpressionFactory {

    @Override
    public String type() {
        return "tag_in";
    }

    @Override
    public Expression create(JsonNode json) {
        String tagKey = json.path("tag_key").asText();
        String values = json.path("values").asText();

        if (tagKey == null || tagKey.isBlank()) {
            throw new IllegalArgumentException("tag_in: 'tag_key' is required");
        }

        if (values == null || values.isBlank()) {
            throw new IllegalArgumentException("tag_in: 'values' is required. (separation with ';')");
        }

        Set<String> allowedValues = new HashSet<>(Arrays.asList(values.split(";")));

        return feature -> {
            String tagValue = feature.getTags().get(tagKey);
            if (tagValue == null) return false;

            for (String value : allowedValues) {
                if (tagValue.contains(value)) return true;
            }
            return false;
        };
    }
}
