package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.factory;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.Feature;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.parser.Expression;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.util.JsonUtils;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.util.Set;

/**
 * Evaluates if the geometry is valid.
 */
@Component
public class GeomCheckExpressionFactory implements ExpressionFactory {

    /**
     * Defines the unique rule type.
     */
    @Override
    public String type() {
        return "geom_check";
    }

    /**
     * Defines the possible rule parameters.
     */
    private record RuleParams (
            Double minLat, // minimal coordinate range
            Double minLon, // minimal coordinate range
            Double maxLat, // maximal coordinate range
            Double maxLon  // maximal coordinate range
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

                // Check if the geometry is in allowed coordinate range.
                Envelope geometryEnvelope = feature.getGeometryTransformed().getEnvelopeInternal();
                return geometryEnvelope.getMinX() >= params.minLon() &&
                        geometryEnvelope.getMaxX() <= params.maxLon() &&
                        geometryEnvelope.getMinY() >= params.minLat() &&
                        geometryEnvelope.getMaxY() <= params.maxLat();
            }

            return true;
        };
    }

    /**
     * Parse rule parameters.
     */
    private RuleParams parseParams(JsonNode json) {
        Double minLat = JsonUtils.asDouble(json, "min_lat", type());
        Double minLon = JsonUtils.asDouble(json, "min_lon", type());
        Double maxLat = JsonUtils.asDouble(json, "max_lat", type());
        Double maxLon = JsonUtils.asDouble(json, "max_lon", type());

        return new RuleParams(minLat, minLon, maxLat, maxLon);
    }
}
