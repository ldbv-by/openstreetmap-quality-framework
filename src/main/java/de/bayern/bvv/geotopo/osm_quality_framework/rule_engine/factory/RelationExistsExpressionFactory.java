package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.factory;

import com.fasterxml.jackson.databind.JsonNode;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.Expression;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.ExpressionFactory;
import org.springframework.stereotype.Component;

/**
 * Evaluates whether a relation exists on a tagged object.
 */
@Component
public class RelationExistsExpressionFactory implements ExpressionFactory {

    @Override
    public String type() {
        return "relation_exists";
    }

    @Override
    public Expression create(JsonNode json) {
        String objectType = json.path("object_type").asText();

        if (objectType == null || objectType.isBlank()) {
            throw new IllegalArgumentException("relation_exists: 'object_type' is required");
        }

        return taggedObject -> taggedObject.getRelations().stream()
                .anyMatch(r -> r.getObjectType().equals(objectType));
    }
}
