package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.factory;

import com.fasterxml.jackson.databind.JsonNode;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.Relation;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.TaggedObject;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.Expression;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.ExpressionFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Evaluates whether a tag–value pair is unique (globally or within the same object).
 */
@Component
public class UniqueCheckExpressionFactory implements ExpressionFactory {

    @Override
    public String type() {
        return "unique_check";
    }

    @Override
    public Expression create(JsonNode json) {
        String tagKey = json.path("tag_key").asText();
        String scope = json.path("scope").asText();

        if (tagKey == null || tagKey.isBlank()) {
            throw new IllegalArgumentException("unique_check: 'tag_key' is required");
        }

        if (scope == null || scope.isBlank()) {
            throw new IllegalArgumentException("unique_check: 'scope' is required. global or internal is possible.");
        }

        return taggedObject -> switch (scope) {
            case "internal" -> this.compareTagsInternal(taggedObject, tagKey, taggedObject.getTags().get(tagKey));
            case "global" -> true; // todo: db
            default -> false;
        };
    }

    /**
     * Recursively checks if a tag value appears more than once.
     */
    private boolean compareTagsInternal(TaggedObject taggedObject, String compareTagKey, String compareTagValue) {
        for (Map.Entry<String, String> tag : taggedObject.getTags().entrySet()) {
            if (tag.getKey().equals(compareTagKey)) continue;
            if (tag.getValue().equals(compareTagValue)) return false;
        }

        for (Relation relation : taggedObject.getRelations()) {
            if (!this.compareTagsInternal(relation, compareTagKey, compareTagValue)) return false;
        }

        return true;
    }
}
