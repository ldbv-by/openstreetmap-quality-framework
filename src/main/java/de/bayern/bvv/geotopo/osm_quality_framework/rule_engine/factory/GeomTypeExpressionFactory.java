package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.factory;

import com.fasterxml.jackson.databind.JsonNode;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.Feature;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.Expression;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.ExpressionFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

/**
 * Evaluates the type of geometry.
 */
@Component
public class GeomTypeExpressionFactory implements ExpressionFactory {

    @Override
    public String type() {
        return "geom_type";
    }

    @Override
    public Expression create(JsonNode json) {
        String value = json.path("value").asText();

        if (value == null || value.isBlank() || !Set.of("Point", "LineString", "Polygon").contains(value)) {
            throw new IllegalArgumentException("geom_type: 'value' is required. Possible values are Point, LineString and Polygon.");
        }

        return taggedObject -> {

            if (taggedObject instanceof Feature feature) {
                return switch (value.trim()) {
                    case "Point"      -> feature.getGeometry() instanceof Point;
                    case "LineString" -> feature.getGeometry() instanceof LineString;
                    case "Polygon"    -> feature.getGeometry() instanceof Polygon;
                    default           -> false;
                };
            }

            return false;
        };
    }
}
