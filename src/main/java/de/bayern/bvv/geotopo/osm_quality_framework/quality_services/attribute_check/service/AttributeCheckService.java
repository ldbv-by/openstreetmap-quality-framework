package de.bayern.bvv.geotopo.osm_quality_framework.quality_services.attribute_check.service;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.api.OsmSchemaService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.mapper.ChangesetDataSetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.ChangesetDataSet;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.DataSet;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.Feature;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.TaggedObject;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.mapper.ObjectTypeMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.model.ObjectType;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.model.Rule;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.model.Tag;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.Expression;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.parser.ExpressionParser;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceRequestDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceResultDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.mapper.QualityServiceResultMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.model.QualityServiceError;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.model.QualityServiceResult;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.spi.QualityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service("attribute-check")
@RequiredArgsConstructor
public class AttributeCheckService implements QualityService {

    private final OsmSchemaService osmSchemaService;
    private final ExpressionParser expressionParser;
    private QualityServiceResult qualityServiceResult;

    /**
     * Executes the quality check for the given request.
     * Check attribute consistency.
     */
    @Override
    public QualityServiceResultDto checkChangesetQuality(QualityServiceRequestDto qualityServiceRequestDto) {

        // ----- Initialize result of quality service.
        this.qualityServiceResult = new QualityServiceResult(qualityServiceRequestDto.qualityServiceId(), qualityServiceRequestDto.changesetId());

        // ----- Get tagged objects.
        ChangesetDataSet changesetDataSet = ChangesetDataSetMapper.toDomain(qualityServiceRequestDto.changesetDataSetDto());

        // ----- Check for each tagged object the attribute consistency.
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

                // ----- Check schema for tagged object.
                this.checkSchema(taggedObject, objectType);

                // ----- Check schema rules "attribute-check" for tagged object.
                if (this.qualityServiceResult.getErrors().isEmpty()) {
                    for (Rule rule : objectType.getRules().stream().filter(r -> r.getType().equals("attribute-check")).toList()) {
                        Expression conditions = this.expressionParser.parse(rule.getExpression().path("conditions"));
                        Expression checks = this.expressionParser.parse(rule.getExpression().path("checks"));

                        if (conditions.evaluate(taggedObject)) {
                            if (!checks.evaluate(taggedObject)) {
                                this.setError(taggedObject, rule.getErrorText());
                            }
                        }
                    }
                }

            } else {
                // ----- No schema configuration found for feature.
                this.setError(taggedObject,"Keine Schemaeinträge für '" + taggedObject.getObjectType() + "' gefunden.");
            }
        }

        return QualityServiceResultMapper.toDto(this.qualityServiceResult);
    }

    /**
     * Check if tagged object is conform with schema.
     */
    private void checkSchema(TaggedObject taggedObject, ObjectType objectType) {

        // Check whether required tags with valid values are set.
        for (Tag tag : objectType.getTags()) {
            validateTag("", tag, taggedObject);
        }

        // Check whether unknown tags are set.
        for (String key : taggedObject.getTags().keySet()) {
            if (!objectType.getTagKeys().contains(key) && !objectType.getTagGroups().contains(key)) {
                this.setError(taggedObject, "Tag '" + key + "' ist nicht im Schema definiert.");
            }
        }

        // Todo: Prüfen, dass die Relations lt. Schema enthalten sind

    }

    /**
     * Recursive validation of a tag/group.
     */
    private void validateTag(String prefix, Tag tag, TaggedObject taggedObject) {
        String fullKey = prefix.isEmpty() ? tag.getKey() : prefix + Tag.SUBTYPE_SEPARATOR + tag.getKey();

        // ----- Check complex tag.
        if (tag.getType() == Tag.Type.COMPLEX) {
            boolean hasDirectValue = taggedObject.getTags().containsKey(fullKey);
            boolean hasChildren = this.anyKeyWithPrefix(taggedObject.getTags(), fullKey + Tag.SUBTYPE_SEPARATOR);
            boolean groupPresent = hasDirectValue || hasChildren;

            if (hasDirectValue) {
                this.setError(taggedObject, "Das komplexe Tag '" + fullKey + "' darf keine direkten Werte enthalten.");
            }

            if (!groupPresent && tag.isRequired()) {
                this.setError(taggedObject, "Das komplexe Tag '" + fullKey + "' ist nicht vorhanden.");
            }

            if (groupPresent || tag.isRequired()) {
                for (Tag subTag : tag.getSubTags()) {
                    this.validateTag(fullKey, subTag, taggedObject);
                }
            }

            return;
        }

        // ----- Check primitive and dictionary tag.
        boolean tagPresent = taggedObject.getTags().containsKey(fullKey);

        if (!tagPresent && tag.isRequired()) {
            this.setError(taggedObject, "Das Tag '" + fullKey + "' ist nicht vorhanden.");
            return;
        }

        // ----- Check values.
        List<String> values = this.splitValues(taggedObject.getTags().get(fullKey));

        if (values.size() < tag.getMultiplicity().min()) {
            this.setError(taggedObject, "Das Tag '" + fullKey + "' erwartet mindestens " + tag.getMultiplicity().min() + " Werte.");
        }

        if (values.size() > tag.getMultiplicity().max()) {
            this.setError(taggedObject, "Das Tag '" + fullKey + "' darf maximal " + tag.getMultiplicity().max() + " Werte haben.");
        }

        if (tag.getType() == Tag.Type.DICTIONARY) {
            for (String value : values) {
                if (!tag.getDictionary().containsKey(value)) {
                    this.setError(taggedObject, "Das Tag '" + fullKey + "' hat einen ungültigen Wert.");
                    break;
                }
            }
        }
    }

    /**
     * Check if any key with prefix exists.
     */
    private boolean anyKeyWithPrefix(Map<String, String> tags, String prefix) {
        for (String key : tags.keySet()) {
            if (key.startsWith(prefix)) return true;
        }
        return false;
    }

    /**
     * Get all tag values.
     */
    private List<String> splitValues(String values) {
        if (values == null) return new ArrayList<>();

        return Arrays.stream(values.split(TaggedObject.TAG_VALUE_SEPARATOR))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
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