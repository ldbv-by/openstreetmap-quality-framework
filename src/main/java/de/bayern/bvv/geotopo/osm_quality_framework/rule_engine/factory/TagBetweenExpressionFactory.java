package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.factory;

import com.fasterxml.jackson.databind.JsonNode;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.Expression;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.ExpressionFactory;
import org.springframework.stereotype.Component;

/**
 * Evaluates whether a tag's (numeric) value lies between two bounds.
 */
@Component
public class TagBetweenExpressionFactory implements ExpressionFactory {

    @Override
    public String type() {
        return "tag_between";
    }

    @Override
    public Expression create(JsonNode json) {
        String tagKey = json.path("tag_key").asText();
        String fromValue = json.path("from_value").asText();
        String toValue = json.path("to_value").asText();

        if (tagKey == null || tagKey.isBlank()) {
            throw new IllegalArgumentException("tag_between: 'tag_key' is required");
        }

        if (fromValue == null || fromValue.isBlank()) {
            throw new IllegalArgumentException("tag_between: 'from_value' is required.");
        }

        if (toValue == null || toValue.isBlank()) {
            throw new IllegalArgumentException("tag_between: 'to_value' is required.");
        }

        final int from = parseInt(fromValue.trim(), "from_value");
        final int to   = parseInt(toValue.trim(),   "to_value");

        if (from > to) {
            throw new IllegalArgumentException("tag_between: 'from_value' must be <= 'to_value'");
        }

        return (taggedObject, baseTaggedObject) -> {
            String tagValue = taggedObject.getTags().get(tagKey);
            if (tagValue == null) return false;

            for (String valueStr : tagValue.split(";")) {
                String str = valueStr.trim();
                if (str.isEmpty()) continue;

                Integer v = tryParseInt(str);
                if (v == null) continue;            // ignoriert nicht-numerische Tokens
                if (v >= from && v <= to) return true;
            }
            return false;
        };
    }

    private static int parseInt(String s, String field) {
        try {
            if (s.equals("max_value")) return Integer.MAX_VALUE;
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("tag_between: '" + field + "' must be an integer, but was '" + s + "'", e);
        }
    }

    private static Integer tryParseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}
