package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.factory;

import com.fasterxml.jackson.databind.JsonNode;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.DataSetFilter;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.Feature;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.SpatialOperator;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.parser.Expression;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

/**
 * Evaluates the type of geometry.
 */
@Component
public class GeomTypeExpressionFactory implements ExpressionFactory {

    /**
     * Defines the unique rule type.
     */
    @Override
    public String type() {
        return "geom_type";
    }

    /**
     * Defines the possible rule parameters.
     */
    private record RuleParams (
            String value // Point, LineString, Polygon
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

            if (taggedObject instanceof Feature feature) {
                return switch (params.value.trim()) {
                    case "Point"      -> feature.getGeometry() instanceof Point;
                    case "LineString" -> feature.getGeometry() instanceof LineString;
                    case "Polygon"    -> feature.getGeometry() instanceof Polygon;
                    default           -> false;
                };
            }

            return false;
        };
    }

    /**
     * Parse rule parameters.
     */
    private RuleParams parseParams(JsonNode json) {
        String value = json.path("value").asText();
        if (value == null || value.isBlank() || !Set.of("Point", "LineString", "Polygon").contains(value)) {
            throw new IllegalArgumentException(type() + ": ''value' is required. Possible values are Point, LineString and Polygon.");
        }

        return new RuleParams(value);
    }
}
