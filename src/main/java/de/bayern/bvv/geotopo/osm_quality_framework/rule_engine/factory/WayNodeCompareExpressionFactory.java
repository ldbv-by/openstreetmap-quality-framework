package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.factory;

import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.util.JsonUtils;
import tools.jackson.databind.JsonNode;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.Feature;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.GeometryNode;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.parser.Expression;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Compares index of way node with way nodes of tagged object.
 * Index semantics:
 *   1  -> erstes Element
 *   2  -> zweites Element
 *  -1  -> letztes Element
 *  -2  -> vorletztes Element
 *   0  -> ungültig
 */
@Component
public class WayNodeCompareExpressionFactory implements ExpressionFactory {

    /**
     * Defines the unique rule type.
     */
    @Override
    public String type() {
        return "way_node_compare";
    }

    /**
     * Defines the possible rule parameters.
     */
    private record RuleParams (
            Integer index
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

            if (!(baseTaggedObject instanceof Feature way) ||
                    (way.getGeometryNodes() == null || way.getGeometryNodes().isEmpty())) {
                return false;
            }

            if (Math.abs(params.index) > way.getGeometryNodes().size()) return false;

            GeometryNode geometryNode = way.getGeometryNodes().get(
                    (params.index > 0) ? (params.index -1) : (way.getGeometryNodes().size() + params.index));

            if (geometryNode == null) return false;

            return Objects.equals(geometryNode.getOsmId(), taggedObject.getOsmId());
        };
    }

    /**
     * Parse rule parameters.
     */
    private RuleParams parseParams(JsonNode json) {
        String indexStr = JsonUtils.asOptionalString(json, "index");
        int index = parseInt(indexStr, "index");

        if (index == 0) {
            throw new IllegalArgumentException(type() + ": 'index' > 0 or index < 0 is required.");
        }

        return new RuleParams(index);
    }

    /**
     * Number Helper.
     */
    private static int parseInt(String s, String field) {
        try {
            if (s.equals("max_value")) return Integer.MAX_VALUE;
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("way_node_compare: '" + field + "' must be an integer, but was '" + s + "'", e);
        }
    }
}
