package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.DataSetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.mapper.DataSetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.mapper.FeatureMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.*;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.util.CriteriaDeserializer;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.Expression;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.ExpressionFactory;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.util.RuleAlias;
import de.bayern.bvv.geotopo.osm_quality_framework.merged_geodata_view.api.MergedGeodataView;
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

    private final MergedGeodataView mergedGeodataView;

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
                        this.mergedGeodataView.getRelationMembers(relation.getOsmId(), params.referenceFeatureRole)
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
        Set<SpatialOperator> operators = this.parseOperators(json);
        DataSetFilter dataSetFilter = this.parseDataSetFilter(json);
        String referenceFeatureRole = json.path("reference_feature_role").asText();
        boolean selfCheck = Optional.of(json.path("self_check").asBoolean()).orElse(false);

        return new RuleParams(
                operators, dataSetFilter, referenceFeatureRole, selfCheck
        );
    }

    // ------ Helper function to parse operator/s.
    private Set<SpatialOperator> parseOperators(JsonNode json) {
        EnumSet<SpatialOperator> operators = EnumSet.noneOf(SpatialOperator.class);

        JsonNode operatorsJsonNode = json.path("operators");

        if (operatorsJsonNode == null || operatorsJsonNode.isEmpty()) {
            operatorsJsonNode = json.path("operator");
            if (operatorsJsonNode == null) throw new IllegalArgumentException(type() + ": 'operator(s)' is required.");
        }

        try {
            if (!operatorsJsonNode.isArray()) {
                SpatialOperator operator = SpatialOperator.valueOf(operatorsJsonNode.asText().trim().toUpperCase());
                operators.add(operator);
            } else {
                for (JsonNode operatorJson : operatorsJsonNode) {
                    SpatialOperator operator = SpatialOperator.valueOf(operatorJson.asText().trim().toUpperCase());
                    operators.add(operator);
                }
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(type() + ": 'operator(s)' not found.");
        }

        return operators;
    }

    // ----- Helper function to parse data set filter.
    private DataSetFilter parseDataSetFilter(JsonNode json) {
        JsonNode dataSetFilterJsonNode = json.path("data_set_filter");
        if (dataSetFilterJsonNode == null || dataSetFilterJsonNode.isEmpty()) {
            return new DataSetFilter(null, null, null, null, null, null);
        }

        try {
            ObjectMapper objectMapper = JsonMapper.builder()
                    .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                    .build();

            SimpleModule criteriaModule = new SimpleModule();
            criteriaModule.addDeserializer(Criteria.class, new CriteriaDeserializer());
            objectMapper.registerModule(criteriaModule);

            return objectMapper.treeToValue(dataSetFilterJsonNode, DataSetFilter.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(type() + ": 'data_set_filter' parse error.");
        }
    }

    /**
     * Executes the spatial compare.
     */
    private boolean executeSpatialCompare(Feature feature,
                                          TaggedObject baseTaggedObject,
                                          RuleParams params,
                                          DataSetFilter dataSetFilter) {

        DataSetDto spatialResult = this.mergedGeodataView.getDataSetBySpatialRelation(
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
