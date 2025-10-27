package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.factory;

import com.fasterxml.jackson.databind.JsonNode;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.DataSetFilter;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.FeatureFilter;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.TaggedObject;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.Expression;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.ExpressionFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Evaluates whether a tag value equals to a fixed value.
 */
@Component
public class TagStartsWithExpressionFactory implements ExpressionFactory {

    @Override
    public String type() {
        return "tag_starts_with";
    }

    @Override
    public Expression create(JsonNode json) {
        String tagKey = json.path("tag_key").asText();
        String value = json.path("value").asText();

        if (tagKey == null || tagKey.isBlank()) {
            throw new IllegalArgumentException("tag_starts_with: 'tag_key' is required");
        }

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("tag_starts_with: 'value' is required");
        }

        return (taggedObject, baseTaggedObject) -> {
            String tagValue = taggedObject.getTags().get(tagKey);
            if (tagValue == null) return false;

            return tagValue.startsWith(this.resolveCurrentPlaceholder(taggedObject, value));
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
