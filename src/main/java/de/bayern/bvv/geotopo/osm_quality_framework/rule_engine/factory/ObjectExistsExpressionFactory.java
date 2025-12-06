package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.factory;

import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.util.JsonUtils;
import tools.jackson.databind.JsonNode;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.DataSetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.*;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.parser.Expression;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.util.RuleAlias;
import de.bayern.bvv.geotopo.osm_quality_framework.geodata_view.api.GeodataViewService;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Evaluates the type of geometry.
 */
@Component
@RequiredArgsConstructor
public class ObjectExistsExpressionFactory implements ExpressionFactory {

    private final GeodataViewService geodataViewService;

    /**
     * Defines the unique rule type.
     */
    @Override
    public String type() {
        return "object_exists";
    }

    /**
     * Defines the possible rule parameters.
     */
    private record RuleParams (
            DataSetFilter dataSetFilter
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
            DataSetFilter preparedDataSetFilter = RuleAlias.replaceDataSetFilter(params.dataSetFilter, taggedObject);
            DataSetDto resultDataSetDto = this.geodataViewService.getDataSet(preparedDataSetFilter);

            if (resultDataSetDto == null) return false;

            // Nodes found ...
            if (resultDataSetDto.nodes() != null && !resultDataSetDto.nodes().isEmpty()) {
                if (taggedObject instanceof Feature feature && feature.getGeometry() instanceof Point) {
                    if (resultDataSetDto.nodes().stream()
                            .anyMatch(n -> !Objects.equals(n.osmId(), feature.getOsmId()))) {
                        return true;
                    }
                } else {
                    return true;
                }
            }

            // Ways found ...
            if (resultDataSetDto.ways() != null && !resultDataSetDto.ways().isEmpty()) {
                if (taggedObject instanceof Feature feature && feature.getGeometry() instanceof LineString) {
                    if (resultDataSetDto.ways().stream()
                            .anyMatch(w -> !Objects.equals(w.osmId(), feature.getOsmId()))) {
                        return true;
                    }
                } else {
                    return true;
                }
            }

            // Areas found ...
            if (resultDataSetDto.areas() != null && !resultDataSetDto.areas().isEmpty()) {
                if (taggedObject instanceof Feature feature && feature.getGeometry() instanceof Polygon) {
                    if (resultDataSetDto.areas().stream()
                            .anyMatch(a -> !Objects.equals(a.osmId(), feature.getOsmId()))) {
                        return true;
                    }
                } else {
                    return true;
                }
            }

            // Relations found ...
            if (resultDataSetDto.relations() != null && !resultDataSetDto.relations().isEmpty()) {
                if (taggedObject instanceof Relation relation) {
                    return resultDataSetDto.relations().stream()
                            .anyMatch(a -> !Objects.equals(a.osmId(), relation.getOsmId()));
                } else {
                    return true;
                }
            }

            return false;
        };
    }

    /**
     * Parse rule parameters.
     */
    private RuleParams parseParams(JsonNode json) {
        DataSetFilter dataSetFilter = JsonUtils.asOptionalDataSetFilter(json);
        return new RuleParams(dataSetFilter);
    }
}
