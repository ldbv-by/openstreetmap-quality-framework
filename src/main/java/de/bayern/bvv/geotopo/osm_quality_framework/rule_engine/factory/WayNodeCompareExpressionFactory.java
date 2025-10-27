package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.factory;

import com.fasterxml.jackson.databind.JsonNode;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.Feature;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.GeometryNode;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.Expression;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.ExpressionFactory;
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

    @Override
    public String type() {
        return "way_node_compare";
    }

    @Override
    public Expression create(JsonNode json) {
        String indexStr = json.path("index").asText();
        int index = parseInt(indexStr, "index");

        if (index == 0) {
            throw new IllegalArgumentException("way_node_compare: 'index' > 0 or index < 0 is required.");
        }

        return (taggedObject, baseTaggedObject) -> {

            if (!(baseTaggedObject instanceof Feature way) ||
                    (way.getGeometryNodes() == null || way.getGeometryNodes().isEmpty())) {
                return false;
            }

            if (Math.abs(index) > way.getGeometryNodes().size()) return false;

            GeometryNode geometryNode = way.getGeometryNodes().get(
                    (index > 0) ? (index -1) : (way.getGeometryNodes().size() + index));

            if (geometryNode == null) return false;

            return Objects.equals(geometryNode.getOsmId(), taggedObject.getOsmId());
        };
    }

    /**
     * Number helper.
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
