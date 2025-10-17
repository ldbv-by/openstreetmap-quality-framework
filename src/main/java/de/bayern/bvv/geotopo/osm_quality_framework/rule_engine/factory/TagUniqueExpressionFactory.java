package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.factory;

import com.fasterxml.jackson.databind.JsonNode;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.Relation;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.TaggedObject;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.Expression;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.ExpressionFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Evaluates whether a tag–value pair is unique (globally or within the same object).
 */
@Component
public class TagUniqueExpressionFactory implements ExpressionFactory {

    @Override
    public String type() {
        return "tag_unique";
    }

    @Override
    public Expression create(JsonNode json) {
        String tagKey = json.path("tag_key").asText();

        if (tagKey == null || tagKey.isBlank()) {
            throw new IllegalArgumentException("tag_unique: 'tag_key' is required");
        }

        return taggedObject -> this.compareTags(taggedObject, tagKey, new ArrayList<>());
    }

    /**
     * Recursively checks if a tag value appears more than once.
     */
    private boolean compareTags(TaggedObject taggedObject, String compareTagKey, List<String> currentValues) {
        for (Map.Entry<String, String> tag : taggedObject.getTags().entrySet()) {
            if (tag.getKey().equals(compareTagKey)) {
                if (currentValues.contains(tag.getValue())) return false;
                currentValues.add(tag.getValue());
            }
        }

        for (Relation relation : taggedObject.getRelations()) {
            if (!this.compareTags(relation, compareTagKey, currentValues)) return false;
        }

        return true;
    }
}
