package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.DataSetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.*;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.Expression;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.ExpressionFactory;
import de.bayern.bvv.geotopo.osm_quality_framework.unified_data_provider.api.UnifiedDataProvider;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Evaluates the type of geometry.
 */
@Component
@RequiredArgsConstructor
public class ObjectExistsExpressionFactory implements ExpressionFactory {

    private final UnifiedDataProvider unifiedDataProvider;
    private final ObjectMapper objectMapper;

    @Override
    public String type() {
        return "object_exists";
    }

    @Override
    public Expression create(JsonNode json) {
        DataSetFilter dataSetFilter = this.parseDataSetFilter(json);

        return taggedObject -> {
            DataSetFilter resolvedDataSetFilter = this.resolveCurrentPlaceholders(dataSetFilter, taggedObject);
            DataSetDto resultDataSetDto = this.unifiedDataProvider.getDataSet(resolvedDataSetFilter);

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
        JsonNode dataSetFilter = json.path("data_set_filter");

        try {
            return this.objectMapper.treeToValue(dataSetFilter, DataSetFilter.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("object_exists: 'data_set_filter' is required. Error: " + e.getMessage());
        }

    }

    private DataSetFilter resolveCurrentPlaceholders(DataSetFilter dataSetFilter, TaggedObject taggedObject) {
        if (dataSetFilter != null && dataSetFilter.featureFilter() != null && dataSetFilter.featureFilter().tags() != null) {
            Map<String, String> resolvedTags = new HashMap<>();
            for (Map.Entry<String, String> entry : dataSetFilter.featureFilter().tags().entrySet()) {
                String tagValue = entry.getValue();
                if (tagValue.startsWith("current:")) {
                    String taggedObjectTagKey = tagValue.substring("current:".length());
                    tagValue = taggedObject.getTags().get(taggedObjectTagKey);
                }

                resolvedTags.put(entry.getKey(), tagValue);
            }

            return new DataSetFilter(
                    dataSetFilter.ignoreChangesetData(),
                    new FeatureFilter(dataSetFilter.featureFilter().osmIds(), resolvedTags, dataSetFilter.featureFilter().boundingBox()),
                    dataSetFilter.coordinateReferenceSystem()
            );
        }

        return dataSetFilter;
    }
}
