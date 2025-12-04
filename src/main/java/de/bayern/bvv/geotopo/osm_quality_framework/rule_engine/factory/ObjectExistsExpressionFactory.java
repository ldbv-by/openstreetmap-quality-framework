package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.DataSetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.*;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.util.CriteriaDeserializer;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.parser.Expression;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.util.RuleAlias;
import de.bayern.bvv.geotopo.osm_quality_framework.merged_geodata_view.api.MergedGeodataView;
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

    private final MergedGeodataView mergedGeodataView;
    private final ObjectMapper objectMapper;

    @Override
    public String type() {
        return "object_exists";
    }

    @Override
    public Expression create(JsonNode json) {
        DataSetFilter dataSetFilter = this.parseDataSetFilter(json);

        return (taggedObject, baseTaggedObject) -> {
            DataSetFilter preparedDataSetFilter = RuleAlias.replaceDataSetFilter(dataSetFilter, taggedObject);
            DataSetDto resultDataSetDto = this.mergedGeodataView.getDataSet(preparedDataSetFilter);

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

}
