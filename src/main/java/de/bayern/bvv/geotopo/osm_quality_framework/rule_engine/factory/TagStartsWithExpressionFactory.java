package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.factory;

import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.util.JsonUtils;
import tools.jackson.databind.JsonNode;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.TaggedObject;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.parser.Expression;
import org.springframework.stereotype.Component;

/**
 * Evaluates whether a tag value equals to a fixed value.
 */
@Component
public class TagStartsWithExpressionFactory implements ExpressionFactory {

    /**
     * Defines the unique rule type.
     */
    @Override
    public String type() {
        return "tag_starts_with";
    }

    /**
     * Defines the possible rule parameters.
     */
    private record RuleParams (
            String tagKey,
            String value,
            Integer substringStart,
            Integer substringLength
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

            String compareValue = this.resolveCurrentPlaceholder(taggedObject, baseTaggedObject, params.value());
            if (params.substringStart != null && params.substringLength != null) {
                compareValue = compareValue.substring(params.substringStart, params.substringLength);
            }

            return tagValue.startsWith(compareValue);
        };
    }

    /**
     * Parse rule parameters.
     */
    private RuleParams parseParams(JsonNode json) {
        String tagKey = JsonUtils.asString(json, "tag_key", type());
        String value = JsonUtils.asString(json, "value", type());
        Integer substringStart = JsonUtils.asOptionalInteger(json, "substring_start");
        Integer substringLength = JsonUtils.asOptionalInteger(json, "substring_length");
        return new RuleParams(tagKey, value,substringStart, substringLength);
    }

    private String resolveCurrentPlaceholder(TaggedObject taggedObject, TaggedObject baseTaggedObject, String value) {
        if (value.startsWith("current:")) {
            String taggedObjectTagKey = value.substring("current:".length());
            return taggedObject.getTags().get(taggedObjectTagKey);
        } else if (value.startsWith("base:")) {
            String taggedObjectTagKey = value.substring("base:".length());
            return baseTaggedObject.getTags().get(taggedObjectTagKey);
        }
        return value;
    }
}
