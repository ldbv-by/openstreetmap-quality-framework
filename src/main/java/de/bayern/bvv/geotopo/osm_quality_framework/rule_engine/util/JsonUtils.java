package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.util;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.Criteria;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.DataSetFilter;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.SpatialOperator;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.util.CriteriaDeserializer;
import lombok.experimental.UtilityClass;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class JsonUtils {

    public String asString(JsonNode json, String field, String expressionType) {
        JsonNode node = json.get(field);
        if (node != null && node.isString()) {
            return node.asString();
        }

        throw new IllegalArgumentException(expressionType + ": '" + field + "' is required");
    }

    public String asOptionalString(JsonNode json, String field) {
        JsonNode node = json.get(field);
        if (node != null && node.isString()) {
            return node.asString();
        }
        return "";
    }

    public Set<String> asOptionalStringSet(JsonNode json, String field) {
        JsonNode node = json.get(field);

        Set<String> values = new HashSet<>();
        if (node.isArray()) {
            for (JsonNode value : node) {
                if (value.isString()) {
                    values.add(value.asString());
                }
            }
        }

        return values;
    }

    public Boolean asOptionalBoolean(JsonNode json, String field) {
        JsonNode node = json.get(field);
        if (node != null && node.isBoolean()) {
            return node.asBoolean();
        }
        return false;
    }

    public Double asDouble(JsonNode json, String field, String expressionType) {
        JsonNode node = json.get(field);

        if (node != null && node.isString()) {
            return Double.parseDouble(node.asString());
        }

        throw new IllegalArgumentException(expressionType + ": '" + field + "' is required");
    }

    public Integer asOptionalInteger(JsonNode json, String field) {
        JsonNode node = json.get(field);
        if (node != null && node.isString()) {
            return Integer.parseInt(node.asString());
        }
        return null;
    }

    public DataSetFilter asOptionalDataSetFilter(JsonNode json) {
        JsonNode node = json.get("data_set_filter");
        if (node != null && !node.isEmpty()) {
            try {
                ObjectMapper mapper = JsonMapper.builder()
                        .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                        .addModule(
                                new SimpleModule()
                                        .addDeserializer(Criteria.class, new CriteriaDeserializer())
                        )
                        .build();

                return mapper.convertValue(node, DataSetFilter.class);
            } catch (JacksonException e) {
                throw new IllegalArgumentException("'data_set_filter' parse error.");
            }
        }

        return new DataSetFilter(null, null, null, null, null, null);
    }

    public Set<SpatialOperator> asSpatialOperators(JsonNode json, String expressionType) {
        JsonNode node = json.get("operators");

        if (node == null || node.isEmpty()) {
            node = json.get("operator");

            if (node == null) throw new IllegalArgumentException(expressionType + ": 'operator(s)' is required.");
        }

        EnumSet<SpatialOperator> operators = EnumSet.noneOf(SpatialOperator.class);

        try {
            if (!node.isArray()) {
                SpatialOperator operator = SpatialOperator.valueOf(node.asString().trim().toUpperCase());
                operators.add(operator);
            } else {
                for (JsonNode operatorJson : node) {
                    SpatialOperator operator = SpatialOperator.valueOf(operatorJson.asString().trim().toUpperCase());
                    operators.add(operator);
                }
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(expressionType + ": 'operator(s)' not found.");
        }

        return operators;
    }


}
