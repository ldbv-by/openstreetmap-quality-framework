package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.factory;

import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.util.JsonUtils;
import tools.jackson.databind.JsonNode;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.parser.Expression;
import org.springframework.stereotype.Component;

/**
 * Compares a number from a tag against another tag's date or a number.
 */
@Component
public class NumberCompareExpressionFactory implements ExpressionFactory {

    /**
     * Defines the unique rule type.
     */
    @Override
    public String type() {
        return "number_compare";
    }

    /**
     * Defines the possible rule parameters.
     */
    private record RuleParams (
            String tagKey,
            String operator,
            String compareTagKey,
            String compareValue
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
            Integer tagNumber = tryParseInt(taggedObject.getTags().get(params.tagKey), params.tagKey);
            if (tagNumber == null) return false;

            Integer compareNumber = !(params.compareValue.isEmpty()) ? tryParseInt(params.compareValue, "compare_value") :
                    tryParseInt(taggedObject.getTags().get(params.compareTagKey), "compare_tag_key");
            if (compareNumber == null) return false;

            return switch (params.operator) {
                case "<" -> tagNumber < compareNumber;
                case "<=" -> tagNumber <= compareNumber;
                case ">" -> tagNumber > compareNumber;
                case ">=" -> tagNumber >= compareNumber;
                case "==" -> tagNumber.intValue() == compareNumber.intValue();
                case "!=" -> tagNumber.intValue() != compareNumber.intValue();
                case "%" -> {
                    if (compareNumber == 0) {
                        throw new IllegalArgumentException(type() + ": modulo by zero");
                    }

                    yield (tagNumber % compareNumber) == 0;
                }
                default -> throw new IllegalArgumentException(type() + ": unsupported operator: " + params.operator);
            };
        };
    }

    /**
     * Parse rule parameters.
     */
    private RuleParams parseParams(JsonNode json) {
        String tagKey = JsonUtils.asString(json, "tag_key", type());
        String operator = JsonUtils.asString(json,"operator", type());
        String compareTagKey = JsonUtils.asOptionalString(json, "compare_tag_key");
        String compareValue = JsonUtils.asOptionalString(json, "compare_value");

        boolean hasCompareTagKey = !(compareTagKey.isEmpty());
        boolean hasCompareValue = !(compareValue.isEmpty());

        if (!hasCompareTagKey && !hasCompareValue) {
            throw new IllegalArgumentException(type() + ": 'compare_tag_key' or 'compare_value' is required");
        }

        if (hasCompareTagKey && hasCompareValue) {
            throw new IllegalArgumentException(type() + ": 'compare_tag_key' and 'compare_value' together are not allowed");
        }

        return new RuleParams(
                tagKey, operator, compareTagKey, compareValue
        );
    }

    /**
     * Number helper.
     */
    private static Integer tryParseInt(String s, String field) {
        try {
            if (s.equals("max_value")) return Integer.MAX_VALUE;
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
