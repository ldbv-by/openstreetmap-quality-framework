package de.bayern.bvv.geotopo.osm_quality_framework.quality_services.geometry_check.service;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.api.OsmSchemaService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.mapper.ChangesetDataSetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.ChangesetDataSet;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.DataSet;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.Feature;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.TaggedObject;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.mapper.ObjectTypeMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.model.ObjectType;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.model.Rule;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceRequestDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceResultDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.mapper.QualityServiceResultMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.model.QualityServiceError;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.model.QualityServiceResult;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.spi.QualityService;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.Expression;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.parser.ExpressionParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service("geometry-check")
@RequiredArgsConstructor
public class GeometryCheckService implements QualityService {

    private final OsmSchemaService osmSchemaService;
    private final ExpressionParser expressionParser;
    private QualityServiceResult qualityServiceResult;

    /**
     * Executes the quality check for the given request.
     * Check overlays.
     */
    @Override
    public QualityServiceResultDto checkChangesetQuality(QualityServiceRequestDto qualityServiceRequestDto) {
        // ----- Initialize result of quality service.
        this.qualityServiceResult = new QualityServiceResult(
                qualityServiceRequestDto.qualityServiceId(), qualityServiceRequestDto.changesetId());

        // ----- Get tagged objects.
        ChangesetDataSet changesetDataSet = ChangesetDataSetMapper.toDomain(qualityServiceRequestDto.changesetDataSetDto());

        // ----- Check for each tagged object the geometry consistency.
        for (TaggedObject taggedObject : Stream.concat(
                        Optional.ofNullable(changesetDataSet.getCreate())
                                .map(DataSet::getAll).stream().flatMap(Collection::stream),
                        Optional.ofNullable(changesetDataSet.getModify())
                                .map(DataSet::getAll).stream().flatMap(Collection::stream)
                )
                .filter(Objects::nonNull)
                .collect(Collectors.toSet())
        ) {

            // ----- Get schema configuration for tagged object.
            ObjectType objectType = Optional.ofNullable(this.osmSchemaService.getObjectTypeInfo(taggedObject.getObjectType()))
                    .map(ObjectTypeMapper::toDomain)
                    .orElse(null);

            if (objectType != null) {
                // ----- Check schema rules "geometry-check" for tagged object.
                for (Rule rule : objectType.getRules().stream().filter(r -> r.getType().equals("geometry-check")).toList()) {
                    Expression conditions = this.expressionParser.parse(rule.getExpression().path("conditions"));
                    Expression checks = this.expressionParser.parse(rule.getExpression().path("checks"));

                    if (conditions.evaluate(taggedObject)) {
                        if (!checks.evaluate(taggedObject)) {
                            this.setError(taggedObject, rule.getErrorText());
                        }
                    }
                }
            }
        }

        return QualityServiceResultMapper.toDto(this.qualityServiceResult);
    }

    /**
     * Set error on quality service result.
     */
    private void setError(TaggedObject taggedObject, String errorText) {
        if (taggedObject instanceof Feature feature) {
            this.qualityServiceResult.addError(
                    new QualityServiceError(errorText, feature.getGeometry())
            );
        } else {
            this.qualityServiceResult.addError(new QualityServiceError(errorText));
        }
    }
}
