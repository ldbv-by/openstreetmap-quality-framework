package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.factory;

import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.util.JsonUtils;
import tools.jackson.databind.JsonNode;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.DataSetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.mapper.DataSetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.mapper.FeatureMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.*;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.parser.Expression;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.util.RuleAlias;
import de.bayern.bvv.geotopo.osm_quality_framework.geodata_view.api.GeodataViewService;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.operation.union.UnaryUnionOp;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Evaluates the type of geometry.
 */
@Component
@RequiredArgsConstructor
public class SpatialCompareExpressionFactory implements ExpressionFactory {

    private final GeodataViewService geodataViewService;

    /**
     * Defines the unique rule type.
     */
    @Override
    public String type() { return "spatial_compare"; }

    /**
     * Defines the possible rule parameters.
     */
    private record RuleParams (
        Set<SpatialOperator> operators, // covered, covered_by, intersects, surrounded_by, ...
        DataSetFilter dataSetFilter,    // feature set used for comparison with the reference feature.
        String referenceFeatureRole,    // only relation members with this role are considered as the reference feature.
        boolean selfCheck               // checks only the reference object (default = false).
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

            // Defines the reference feature. For regular features, the given object is used.
            // For relations, a union geometry of all relation members is created and used as the reference feature.
            // The referenceFeatureRole parameter can be used to restrict which members are considered.
            Feature referenceFeature = new Feature();
            DataSetFilter preparedDataSetFilter = RuleAlias.replaceDataSetFilter(params.dataSetFilter, taggedObject, params.selfCheck);

            if (taggedObject instanceof Feature feature) {
                referenceFeature = feature;

            } else if (taggedObject instanceof Relation relation) {
                // Get reference feature dataset.
                DataSet referenceFeaturesDataSet = Optional.ofNullable(
                        this.geodataViewService.getRelationMembers(relation.getOsmId(), params.referenceFeatureRole)
                ) .map(DataSetMapper::toDomain).orElse(null);

                if (referenceFeaturesDataSet == null) return false;

                // Aggregates the reference feature dataset and returns a cumulative feature.
                referenceFeature = this.getUnionReferenceFeature(referenceFeaturesDataSet, relation);
            }

            // Execute spatial compare
            return this.executeSpatialCompare(
                    referenceFeature, baseTaggedObject, params, preparedDataSetFilter);
        };
    }

    /**
     * Parse rule parameters.
     */
    private RuleParams parseParams(JsonNode json) {
        Set<SpatialOperator> operators = JsonUtils.asSpatialOperators(json, type());
        DataSetFilter dataSetFilter = JsonUtils.asOptionalDataSetFilter(json);
        String referenceFeatureRole = JsonUtils.asOptionalString(json,"reference_feature_role");
        boolean selfCheck = JsonUtils.asOptionalBoolean(json, "self_check");

        return new RuleParams(
                operators, dataSetFilter, referenceFeatureRole, selfCheck
        );
    }

    /**
     * Executes the spatial compare.
     */
    private boolean executeSpatialCompare(Feature feature,
                                          TaggedObject baseTaggedObject,
                                          RuleParams params,
                                          DataSetFilter dataSetFilter) {

        DataSetDto spatialResult = this.geodataViewService.getDataSetBySpatialRelation(
                FeatureMapper.toDto(feature),
                params.operators,
                dataSetFilter,
                params.selfCheck
        );

        // Filters the base object if it should be excluded.
        if (!params.selfCheck) {
            if (baseTaggedObject instanceof Feature baseFeature) {
                if (baseFeature.getGeometry() instanceof Point) {
                    spatialResult.nodes().removeIf(f -> f.osmId().equals(baseFeature.getOsmId()));
                } else if (baseFeature.getGeometry() instanceof LineString) {
                    spatialResult.ways().removeIf(f -> f.osmId().equals(baseFeature.getOsmId()));
                } else if (baseFeature.getGeometry() instanceof Polygon) {
                    spatialResult.areas().removeIf(f -> f.osmId().equals(baseFeature.getOsmId()));
                }
            } else if (baseTaggedObject instanceof Relation baseRelation) {
                spatialResult.relations().removeIf(f -> f.osmId().equals(baseRelation.getOsmId()));
            }
        }

        // Rule is true if spatial results are found.
        return spatialResult != null &&
                ((spatialResult.nodes() != null && !spatialResult.nodes().isEmpty()) ||
                        (spatialResult.ways() != null && !spatialResult.ways().isEmpty()) ||
                        (spatialResult.areas() != null && !spatialResult.areas().isEmpty()) ||
                        (spatialResult.relations() != null && !spatialResult.relations().isEmpty()));
    }

    /**
     * Aggregates the reference feature dataset and returns a cumulative feature.
     */
    private Feature getUnionReferenceFeature(DataSet referenceFeatureDataSet, Relation relation) {

        Geometry unionGeometry = UnaryUnionOp.union(
            referenceFeatureDataSet.getAll().stream()
                    .filter(f -> f instanceof Feature)
                    .map(Feature.class::cast)
                    .map(Feature::getGeometry)
                    .toList()
        );

        Feature unionFeature = new Feature();
        unionFeature.setOsmId(relation.getOsmId());
        unionFeature.setObjectType(relation.getObjectType());
        unionFeature.setGeometry(unionGeometry);

        return unionFeature;
    }
}
