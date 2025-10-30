package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.factory;

import com.fasterxml.jackson.databind.JsonNode;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.TaggedObject;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.Expression;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.ExpressionFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

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

        return (taggedObject, baseTaggedObject) -> {
            String orgTagValue = taggedObject.getTags().get(tagKey);
            if (orgTagValue == null) return false;

            List<String> tagValues = Arrays.stream(orgTagValue.split(TaggedObject.TAG_VALUE_SEPARATOR)).toList();
            if (tagValues.isEmpty()) return false;

            for (String tagValue : tagValues) {
                if (tagValue.equals(this.resolveCurrentPlaceholder(taggedObject, value))) return true;
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
