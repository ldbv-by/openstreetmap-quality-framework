package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.factory;

import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.util.JsonUtils;
import tools.jackson.databind.JsonNode;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.parser.Expression;
import org.springframework.stereotype.Component;

/**
 * Evaluates whether a tag exists on a feature.
 */
@Component
public class TagExistsExpressionFactory implements ExpressionFactory {

    /**
     * Defines the unique rule type.
     */
    @Override
    public String type() {
        return "tag_exists";
    }

    /**
     * Defines the possible rule parameters.
     */
    private record RuleParams (
            String tagKey
    ) {}

    /**
     * Defines the rule parameters and the execution block of a rule.
     */
    @Override
    public Expression create(JsonNode json) {

        // ----- Parse rule params ------
        RuleParams params = this.parseParams(json);

        // ----- Execute rule ------
        return (taggedObject, baseTaggedObject) ->
                taggedObject.getTags().containsKey(params.tagKey);
    }

    /**
     * Parse rule parameters.
     */
    private RuleParams parseParams(JsonNode json) {
        String tagKey = JsonUtils.asString(json, "tag_key", type());
        return new RuleParams(tagKey);
    }
}
