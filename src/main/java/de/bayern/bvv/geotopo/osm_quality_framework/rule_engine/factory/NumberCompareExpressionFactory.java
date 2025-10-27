package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.factory;

import com.fasterxml.jackson.databind.JsonNode;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.Expression;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.ExpressionFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

/**
 * Compares a number from a tag against another tag's date or a number.
 */
@Component
public class NumberCompareExpressionFactory implements ExpressionFactory {

    @Override
    public String type() {
        return "number_compare";
    }

    @Override
    public Expression create(JsonNode json) {
        String tagKey = json.path("tag_key").asText();
        String operator = json.path("operator").asText();
        String compareTagKey = json.path("compare_tag_key").asText();
        String compareValue = json.path("compare_value").asText();

        if (tagKey == null || tagKey.isBlank()) {
            throw new IllegalArgumentException("number_compare: 'tag_key' is required");
        }

        if (operator == null || operator.isBlank()) {
            throw new IllegalArgumentException("number_compare: 'operator' is required");
        }

        boolean hasCompareTagKey = !(compareTagKey == null || compareTagKey.isBlank());
        boolean hasCompareValue = !(compareValue == null || compareValue.isBlank());

        if (!hasCompareTagKey && !hasCompareValue) {
            throw new IllegalArgumentException("number_compare: 'compare_tag_key' or 'compare_value' is required");
        }

        if (hasCompareTagKey && hasCompareValue) {
            throw new IllegalArgumentException("number_compare: 'compare_tag_key' and 'compare_value' together are not allowed");
        }

        return (taggedObject, baseTaggedObject) -> {
            int tagNumber = parseInt(taggedObject.getTags().get(tagKey), tagKey);
            int compareNumber = hasCompareValue ? parseInt(compareValue, "compare_value") :
                    parseInt(taggedObject.getTags().get(compareTagKey), compareTagKey);

            return switch (operator) {
                case "<" -> tagNumber < compareNumber;
                case "<=" -> tagNumber <= compareNumber;
                case ">" -> tagNumber > compareNumber;
                case ">=" -> tagNumber >= compareNumber;
                case "==" -> tagNumber == compareNumber;
                case "!=" -> tagNumber != compareNumber;
                case "%" -> {
                    if (compareNumber == 0) {
                        throw new IllegalArgumentException("number_compare: modulo by zero");
                    }

                    yield (tagNumber % compareNumber) == 0;
                }
                default -> throw new IllegalArgumentException("date_compare: unsupported operator: " + operator);
            };
        };
    }

    /**
     * Number helper.
     */
    private static int parseInt(String s, String field) {
        try {
            if (s.equals("max_value")) return Integer.MAX_VALUE;
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("number_compare: '" + field + "' must be an integer, but was '" + s + "'", e);
        }
    }
}
