package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.DataSetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.mapper.DataSetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.mapper.FeatureMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.*;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.Expression;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.ExpressionFactory;
import de.bayern.bvv.geotopo.osm_quality_framework.unified_data_provider.api.UnifiedDataProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Evaluates the type of geometry.
 */
@Component
@RequiredArgsConstructor
public class SpatialCompareExpressionFactory implements ExpressionFactory {

    private final UnifiedDataProvider unifiedDataProvider;
    private final ObjectMapper objectMapper;

    @Override
    public String type() {
        return "spatial_compare";
    }

    @Override
    public Expression create(JsonNode json) {
        Set<SpatialOperator> operators = this.parseOperators(json);
        DataSetFilter dataSetFilter = this.parseDataSetFilter(json);
        String relationMasterRole = json.path("relation_master_role").asText();
        String relationCompareRole = json.path("relation_compare_role").asText();

        return taggedObject -> {

            if (taggedObject instanceof Feature feature) {
                return this.spatialCompareFeature(feature, operators, dataSetFilter);
            }

            if (taggedObject instanceof Relation relation) {
                DataSet masterDataSet = Optional.ofNullable(this.unifiedDataProvider.getRelationMembers(relation.getOsmId(), relationMasterRole, null))
                        .map(DataSetMapper::toDomain).orElse(null);

                boolean isCorrect = false;
                if (masterDataSet != null) {
                    for (TaggedObject masterTaggedObject : masterDataSet.getAll()) {
                        if (!(masterTaggedObject instanceof Feature masterFeature)) return false;

                        DataSetFilter relationDataSetFilter = dataSetFilter;
                        if (relationCompareRole != null) {
                            DataSet compareDataSet = Optional.ofNullable(this.unifiedDataProvider.getRelationMembers(relation.getOsmId(), relationCompareRole, null))
                                    .map(DataSetMapper::toDomain).orElse(null);

                            if (compareDataSet == null) return false;

                            relationDataSetFilter = new DataSetFilter(
                                    List.of(1L),
                                    new FeatureFilter(
                                            new OsmIds(compareDataSet.getNodes() == null ? null : compareDataSet.getNodes().stream().map(Feature::getOsmId).collect(Collectors.toSet()),
                                                       compareDataSet.getWays()  == null ? null : compareDataSet.getWays().stream().map(Feature::getOsmId).collect(Collectors.toSet()),
                                                       compareDataSet.getAreas()  == null ? null : compareDataSet.getAreas().stream().map(Feature::getOsmId).collect(Collectors.toSet()),
                                                       null), null, null), null);
                        }

                        if (!this.spatialCompareFeature(masterFeature, operators, relationDataSetFilter)) return false;
                        isCorrect = true;
                    }
                }

                return isCorrect;
            }

            return false;
        };
    }

    private Set<SpatialOperator> parseOperators(JsonNode json) {
        JsonNode operatorsArray = json.path("operators");
        EnumSet<SpatialOperator> result = EnumSet.noneOf(SpatialOperator.class);

        if (operatorsArray != null && operatorsArray.isArray() && !operatorsArray.isEmpty()) {
            for (JsonNode operator : operatorsArray) {
                try {
                    SpatialOperator spatialOperator = SpatialOperator.valueOf(operator.asText().trim().toUpperCase());
                    result.add(spatialOperator);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("spatial_compare: 'operator' " + operator.asText() + " not found.");
                }
            }
        } else {
            String operator = json.path("operator").asText(null);
            if (operator == null || operator.isBlank()) {
                throw new IllegalArgumentException("spatial_compare: 'operator(s)' is required.");
            }

            try {
                SpatialOperator spatialOperator = SpatialOperator.valueOf(operator.trim().toUpperCase());
                result.add(spatialOperator);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("spatial_compare: 'operator' " + operator + " not found.");
            }
        }

        if (result.isEmpty()) {
            throw new IllegalArgumentException("spatial_compare: 'operator(s)' is required.");
        }
        return result;
    }

    private DataSetFilter parseDataSetFilter(JsonNode json) {
        JsonNode dataSetFilter = json.path("data_set_filter");

        try {
            return this.objectMapper.treeToValue(dataSetFilter, DataSetFilter.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("spatial_compare: 'data_set_filter' is required. Error: " + e.getMessage());
        }
    }

    private boolean spatialCompareFeature(Feature feature, Set<SpatialOperator> operators, DataSetFilter dataSetFilter) {
        DataSetDto spatialResult = this.unifiedDataProvider.getDataSetBySpatialRelation(
                FeatureMapper.toDto(feature),
                operators,
                dataSetFilter
        );

        // True if a spatial results are found.
        return spatialResult != null &&
                ((spatialResult.nodes() != null && !spatialResult.nodes().isEmpty()) ||
                        (spatialResult.ways() != null && !spatialResult.ways().isEmpty()) ||
                        (spatialResult.areas() != null && !spatialResult.areas().isEmpty()) ||
                        (spatialResult.relations() != null && !spatialResult.relations().isEmpty()));
    }
}
