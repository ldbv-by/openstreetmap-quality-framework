package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.DataSetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.mapper.FeatureMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.*;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.Expression;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.ExpressionFactory;
import de.bayern.bvv.geotopo.osm_quality_framework.unified_data_provider.spi.UnifiedDataProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Set;

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

        return taggedObject -> {

            if (taggedObject instanceof Feature feature) {
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
}
