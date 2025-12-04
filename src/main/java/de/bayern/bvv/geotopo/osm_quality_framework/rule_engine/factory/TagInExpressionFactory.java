package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.factory;

import com.fasterxml.jackson.databind.JsonNode;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.TaggedObject;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.parser.Expression;
import org.springframework.stereotype.Component;

import java.util.*;

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
        String value = json.path("value").asText();
        JsonNode values = json.path("values");

        if (tagKey == null || tagKey.isBlank()) {
            throw new IllegalArgumentException("tag_in: 'tag_key' is required");
        }

        if ((!values.isArray() || values.isEmpty()) && (value.isEmpty())) {
            throw new IllegalArgumentException("tag_in: 'values' or 'value' must be a non-empty array");
        }

        Set<String> allowedValues = new HashSet<>();
        if (values.isArray()) {
            for (JsonNode val : values) {
                String v = val.asText(null);
                if (v != null && !v.isBlank()) allowedValues.add(v);
            }
        }

        return (taggedObject, baseTaggedObject) -> {
            String orgTagValue = taggedObject.getTags().get(tagKey);
            if (orgTagValue == null) return false;

            List<String> tagValues = Arrays.stream(orgTagValue.split(TaggedObject.TAG_VALUE_SEPARATOR)).toList();
            if (tagValues.isEmpty()) return false;

            if (value != null && !value.isEmpty()) {
                allowedValues.addAll(Arrays.asList(resolveCurrentPlaceholder(taggedObject, value).split(TaggedObject.TAG_VALUE_SEPARATOR)));
            }

            for (String tagValue : tagValues) {
                for (String allowedValue : allowedValues) {
                    if (tagValue.contains(allowedValue)) return true;
                }
            }

            return false;
        };
    }

    private String resolveCurrentPlaceholder(TaggedObject taggedObject, String value) {
        if (value.startsWith("current:")) {
            String taggedObjectTagKey = value.substring("current:".length());
            return taggedObject.getTags().get(taggedObjectTagKey);
        }

        return value;
    }
}
