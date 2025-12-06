package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.factory;

import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.util.JsonUtils;
import tools.jackson.databind.JsonNode;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.TaggedObject;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.parser.Expression;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Evaluates whether a tag value is contained in a set of allowed values.
 */
@Component
public class TagInExpressionFactory implements ExpressionFactory {

    /**
     * Defines the unique rule type.
     */
    @Override
    public String type() {
        return "tag_in";
    }

    /**
     * Defines the possible rule parameters.
     */
    private record RuleParams (
            String tagKey,
            String value,
            Set<String> values
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

            Set<String> allowedValues = new HashSet<>();
            if (!params.value.isEmpty()) {
                allowedValues.addAll(Arrays.asList(resolveCurrentPlaceholder(taggedObject, params.value).split(TaggedObject.TAG_VALUE_SEPARATOR)));
            } else {
                allowedValues.addAll(params.values);
            }

            for (String tagValue : tagValues) {
                for (String allowedValue : allowedValues) {
                    if (tagValue.contains(allowedValue)) return true;
                }
            }

            return false;
        };
    }

    /**
     * Parse rule parameters.
     */
    private RuleParams parseParams(JsonNode json) {
        String tagKey = JsonUtils.asString(json, "tag_key", type());
        String value = JsonUtils.asOptionalString(json, "value");
        Set<String> values = JsonUtils.asOptionalStringSet(json, "values");

        if (value.isEmpty() && values.isEmpty()) {
            throw new IllegalArgumentException(type() + ": 'value' or 'values' must be a non-empty array");
        }

        return new RuleParams(tagKey, value, values);
    }

    private String resolveCurrentPlaceholder(TaggedObject taggedObject, String value) {
        if (value.startsWith("current:")) {
            String taggedObjectTagKey = value.substring("current:".length());
            return taggedObject.getTags().get(taggedObjectTagKey);
        }

        return value;
    }
}
