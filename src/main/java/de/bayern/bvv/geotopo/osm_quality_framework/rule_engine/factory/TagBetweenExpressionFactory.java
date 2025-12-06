package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.factory;

import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.util.JsonUtils;
import tools.jackson.databind.JsonNode;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.parser.Expression;
import org.springframework.stereotype.Component;

/**
 * Evaluates whether a tag's (numeric) value lies between two bounds.
 */
@Component
public class TagBetweenExpressionFactory implements ExpressionFactory {

    /**
     * Defines the unique rule type.
     */
    @Override
    public String type() {
        return "tag_between";
    }

    /**
     * Defines the possible rule parameters.
     */
    private record RuleParams (
            String tagKey,
            String fromValue,
            String toValue
    ) {}

    /**
     * Defines the rule parameters and the execution block of a rule.
     */
    @Override
    public Expression create(JsonNode json) {

        // ----- Parse rule params ------
        RuleParams params = this.parseParams(json);

        // ----- Execute rule ------
        return (taggedObject, baseTaggedObject) -> {
            String tagValue = taggedObject.getTags().get(params.tagKey);
            if (tagValue == null) return false;

            for (String valueStr : tagValue.split(";")) {
                String str = valueStr.trim();
                if (str.isEmpty()) continue;

                Integer v = tryParseInt(str);
                if (v == null) continue;
                if (v >= parseInt(params.fromValue, "from_value") && v <= parseInt(params.toValue, "to_value")) return true;
            }
            return false;
        };
    }

    /**
     * Parse rule parameters.
     */
    private RuleParams parseParams(JsonNode json) {
        String tagKey = JsonUtils.asString(json, "tag_key", type());
        String fromValue = JsonUtils.asString(json, "from_value", type());
        String toValue = JsonUtils.asString(json, "to_value", type());

        final int from = parseInt(fromValue.trim(), "from_value");
        final int to   = parseInt(toValue.trim(),   "to_value");

        if (from > to) {
            throw new IllegalArgumentException(type() + ": 'from_value' must be <= 'to_value'");
        }

        return new RuleParams(
                tagKey, fromValue, toValue
        );
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
