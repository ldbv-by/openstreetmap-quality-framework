package de.bayern.bvv.geotopo.osm_quality_framework.quality_services.geometry_check.service;

import de.bayern.bvv.geotopo.osm_quality_framework.geodata_view.api.GeodataViewService;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.api.OsmSchemaService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.DataSetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.mapper.ChangesetDataSetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.mapper.DataSetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.mapper.FeatureMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.*;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.mapper.ObjectTypeMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.model.ObjectType;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.model.Rule;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceRequestDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceResultDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.mapper.QualityServiceResultMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.model.QualityServiceError;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.model.QualityServiceResult;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.spi.QualityService;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.RuleEngine;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.dto.RuleEvaluationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service(GeometryCheckService.QUALITY_SERVICE_NAME)
@RequiredArgsConstructor
public class GeometryCheckService implements QualityService {

    public static final String QUALITY_SERVICE_NAME = "geometry-check";
    private QualityServiceResult qualityServiceResult;

    private final OsmSchemaService osmSchemaService;
    private final RuleEngine ruleEngine;
    private final GeodataViewService geodataViewService;

    /**
     * Executes the quality check for the given request.
     * Performs geometric checks, e.g. for overlaps.
     */
    @Override
    public QualityServiceResultDto checkChangesetQuality(
            QualityServiceRequestDto qualityServiceRequestDto) {

        ChangesetDataSet changesetDataSet = ChangesetDataSetMapper.toDomain(qualityServiceRequestDto.changesetDataSetDto());
        Set<String> rulesToValidate = qualityServiceRequestDto.rulesToValidate();
        boolean hasRuleFilter = rulesToValidate != null && !rulesToValidate.isEmpty();

        // ----- Initialize result of quality service.
        this.qualityServiceResult = new QualityServiceResult(
                qualityServiceRequestDto.qualityServiceId(), qualityServiceRequestDto.changesetId());

        // ----- Check the geometric consistency for each new or modified changeset object.
        for (TaggedObject changedObject : changesetDataSet.getCreatedAndModified()) {

            // ----- Get rules from openstreetmap-schema for the object_type.
            List<Rule> rules = Optional.ofNullable(
                            this.osmSchemaService.getObjectTypeInfo(changedObject.getObjectType()))
                    .map(ObjectTypeMapper::toDomain)
                    .map(ObjectType::getRules)
                    .orElse(List.of());

            // ----- Check geometry-check rules for changed object.
            for (Rule rule : rules.stream()
                    .filter(r -> QUALITY_SERVICE_NAME.equals(r.getType()))
                    .filter(r -> !hasRuleFilter || rulesToValidate.contains(r.getId()))
                    .toList()) {

                if (!this.ruleEngine.evaluate(new RuleEvaluationDto(changedObject, rule))) {
                    this.setError(changedObject, rule.getErrorText());
                }
            }
        }

        // ----- Check the geometric consistency for neighbour objects of deleted changeset object.
        for (TaggedObject deletedObject : changesetDataSet.getDelete().getAll()) {

            if (deletedObject instanceof Feature deletedFeature) {

                DataSet neighbourObjects = DataSetMapper.toDomain(this.geodataViewService.getDataSetBySpatialRelation(
                        FeatureMapper.toDto(deletedFeature), Set.of(SpatialOperator.INTERSECTS),
                        new DataSetFilter(false, "EPSG:25832", null, null, null, null),
                        false
                ));

                for (TaggedObject neighbourObject : neighbourObjects.getAll()) {
                    // ----- Get rules from openstreetmap-schema for the object_type.
                    List<Rule> rules = Optional.ofNullable(
                                    this.osmSchemaService.getObjectTypeInfo(neighbourObject.getObjectType()))
                            .map(ObjectTypeMapper::toDomain)
                            .map(ObjectType::getRules)
                            .orElse(List.of());

                    // ----- Check geometry-check rules for changed object.
                    for (Rule rule : rules.stream()
                            .filter(r -> QUALITY_SERVICE_NAME.equals(r.getType()))
                            .filter(r -> !hasRuleFilter || rulesToValidate.contains(r.getId()))
                            .toList()) {

                        if (!this.ruleEngine.evaluate(new RuleEvaluationDto(neighbourObject, rule))) {
                            this.setError(deletedObject, rule.getErrorText());
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
