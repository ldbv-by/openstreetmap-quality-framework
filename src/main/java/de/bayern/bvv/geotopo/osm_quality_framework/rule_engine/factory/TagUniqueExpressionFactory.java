package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.factory;

import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.util.JsonUtils;
import tools.jackson.databind.JsonNode;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.Relation;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.TaggedObject;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.parser.Expression;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Evaluates whether a tag–value pair is unique (globally or within the same object).
 */
@Component
public class TagUniqueExpressionFactory implements ExpressionFactory {

    /**
     * Defines the unique rule type.
     */
    @Override
    public String type() {
        return "tag_unique";
    }

    /**
     * Defines the possible rule parameters.
     */
    private record RuleParams (
            String tagKey,
            Integer maxLevel
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
            int level = 0;
            return this.compareTags(taggedObject, params.tagKey,
                    new ArrayList<>(), level, params.maxLevel);
        };
    }

    /**
     * Parse rule parameters.
     */
    private RuleParams parseParams(JsonNode json) {
        String tagKey = JsonUtils.asString(json, "tag_key", type());
        String maxLevelStr = JsonUtils.asOptionalString(json, "max_level");

        Integer maxLevel = null;
        if (!maxLevelStr.isEmpty()) {
            maxLevel = parseInt(maxLevelStr, "max_level");
        }

        return new RuleParams(tagKey, maxLevel);
    }

    /**
     * Recursively checks if a tag value appears more than once.
     */
    private boolean compareTags(TaggedObject taggedObject, String compareTagKey, List<String> currentValues, Integer level, Integer maxLevel) {
        for (Map.Entry<String, String> tag : taggedObject.getTags().entrySet()) {
            if (tag.getKey().equals(compareTagKey)) {
                if (currentValues.contains(tag.getValue())) return false;
                currentValues.add(tag.getValue());
            }
        }

        if (maxLevel == null || level < maxLevel) {
            level++;
            for (Relation relation : taggedObject.getRelations()) {
                if (!this.compareTags(relation, compareTagKey, currentValues, level, maxLevel)) return false;
            }
        }

        return true;
    }

    private static int parseInt(String s, String field) {
        try {
            if (s.equals("max_value")) return Integer.MAX_VALUE;
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("tag_unique: '" + field + "' must be an integer, but was '" + s + "'", e);
        }
    }
}
