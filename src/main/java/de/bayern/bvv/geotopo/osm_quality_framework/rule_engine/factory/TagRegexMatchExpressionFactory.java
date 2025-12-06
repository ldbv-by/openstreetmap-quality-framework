package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.factory;

import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.util.JsonUtils;
import tools.jackson.databind.JsonNode;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.TaggedObject;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.parser.Expression;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Evaluates whether a tag value matches a regular expression.
 */
@Component
public class TagRegexMatchExpressionFactory implements ExpressionFactory {

    /**
     * Defines the unique rule type.
     */
    @Override
    public String type() {
        return "tag_regex_match";
    }

    /**
     * Defines the possible rule parameters.
     */
    private record RuleParams (
            String tagKey,
            Pattern pattern,
            Integer minCount
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
            String orgTagValue = taggedObject.getTags().get(params.tagKey);
            if (orgTagValue == null) return false;

            List<String> tagValues = Arrays.stream(orgTagValue.split(TaggedObject.TAG_VALUE_SEPARATOR)).toList();
            if (tagValues.isEmpty()) return false;

            int matches = 0;
            for (String tagValue : tagValues) {
                if (!params.pattern.matcher(tagValue).matches()) return false;
                matches++;
            }

            return params.minCount == null || matches >= params.minCount;
        };
    }

    /**
     * Parse rule parameters.
     */
    private RuleParams parseParams(JsonNode json) {
        String tagKey = JsonUtils.asString(json, "tag_key", type());
        String patternStr = JsonUtils.asString(json, "pattern", type());
        Integer minCount = tryParseInt(JsonUtils.asOptionalString(json, "min_count"));

        try {
            Pattern pattern = Pattern.compile(patternStr);

            return new RuleParams(
                    tagKey, pattern, minCount
            );
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException(type() + ": invalid pattern: " + e.getMessage(), e);
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
