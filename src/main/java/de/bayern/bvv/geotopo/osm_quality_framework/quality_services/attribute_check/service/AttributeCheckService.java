package de.bayern.bvv.geotopo.osm_quality_framework.quality_services.attribute_check.service;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.api.OsmSchemaService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.mapper.ChangesetDataSetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.*;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service("attribute-check")
@RequiredArgsConstructor
@Slf4j
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
        long startTime = System.currentTimeMillis();

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
            long checkSchemaStartTime = System.currentTimeMillis();
            ObjectType objectType = Optional.ofNullable(this.osmSchemaService.getObjectTypeInfo(taggedObject.getObjectType()))
                    .map(ObjectTypeMapper::toDomain)
                    .orElse(null);

            if (objectType != null) {

                // ----- Check schema for tagged object.

                this.checkSchema(taggedObject, objectType);

                log.info("attribute-check({}): object-type={}, checkSchemaTime={} ms",
                        qualityServiceRequestDto.changesetId(), objectType.getName(), System.currentTimeMillis() - checkSchemaStartTime);

                // ----- Check schema rules "attribute-check" for tagged object.
                if (this.qualityServiceResult.getErrors().isEmpty()) {
                    for (Rule rule : objectType.getRules().stream().filter(r -> r.getType().equals("attribute-check")).toList()) {
                        long ruleStartTime = System.currentTimeMillis();
                        Expression conditions = this.expressionParser.parse(rule.getExpression().path("conditions"));
                        Expression checks = this.expressionParser.parse(rule.getExpression().path("checks"));

                        if (conditions.evaluate(taggedObject, taggedObject)) {
                            if (!checks.evaluate(taggedObject, taggedObject)) {
                                this.setError(taggedObject, rule.getErrorText());
                            }
                        }

                        log.info("attribute-check({}): rule={}, time={} ms",
                                qualityServiceRequestDto.changesetId(), rule.getId(), System.currentTimeMillis() - ruleStartTime);
                    }
                }

            } else {
                // ----- No schema configuration found for feature.
                this.setError(taggedObject,"Keine Schemaeinträge für '" + taggedObject.getObjectType() + "' gefunden.");
            }
        }

        long totalTime = System.currentTimeMillis() - startTime;

        log.info("attribute-check({}): totalTime={} ms",
                qualityServiceRequestDto.changesetId(), totalTime);

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

        // Check relations
        validateRelation(taggedObject, objectType);
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

    /**
     * Validation of a relation.
     */
    private void validateRelation(TaggedObject taggedObject, ObjectType objectType) {

        // Checks whether required relations with valid values are set.
        for (de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.model.Relation relation :
                objectType.getRelations()) {

            List<Relation> relationsOfTaggedObject = taggedObject.getRelations()
                    .stream().filter(r -> r.getObjectType().equals(relation.getObjectType().getName())).toList();

            if (relationsOfTaggedObject.size() < relation.getMultiplicity().min()) {
                this.setError(taggedObject, "Die Objektart '" + taggedObject.getObjectType() + "' erwartet mindestens " + relation.getMultiplicity().min() + " Relation/en '" + relation.getObjectType().getName() + "'.");
            }

            if (relationsOfTaggedObject.size() > relation.getMultiplicity().max()) {
                this.setError(taggedObject, "Die Objektart '" + taggedObject.getObjectType() + "' darf maximal " + relation.getMultiplicity().max() + " Relation/en '" + relation.getObjectType().getName() + "' haben.");
            }

            for (de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.model.Relation.Member member : relation.getMembers()) {
                for (Relation relationOfTaggedObject : relationsOfTaggedObject) {
                    List<Relation.Member> memberOfTaggedObject = relationOfTaggedObject.getMembers()
                            .stream().filter(m -> m.getRole().equals(member.getRole()) &&
                                    (member.getType().equals("*") || m.getType().equalsIgnoreCase(member.getType())))
                            .toList();

                    if (memberOfTaggedObject.size() < member.getMultiplicity().min()) {
                        this.setError(taggedObject, "Die Relation '" + relationOfTaggedObject.getObjectType() + "' erwartet mindestens " + member.getMultiplicity().min() + " Members mit der Rolle '" + member.getRole() + "'.");
                    }

                    if (memberOfTaggedObject.size() > member.getMultiplicity().max()) {
                        this.setError(taggedObject, "Die Relation '" + relationOfTaggedObject.getObjectType() + "' darf maximal " + member.getMultiplicity().max() + " Members mit der Rolle '" + member.getRole() + "' haben.");
                    }
                }
            }
        }

        // Check whether unknown relations are set.
        for (Relation relationOfTaggedObject : taggedObject.getRelations()) {
            if (relationOfTaggedObject.getObjectType() == null || relationOfTaggedObject.getObjectType().isEmpty()) {
                this.setError(taggedObject, "Relation ohne Tag 'object_type' ist nicht erlaubt.");
            }

            if (objectType.getRelations().stream()
                    .noneMatch(r -> r.getObjectType().getName()
                            .equals(relationOfTaggedObject.getObjectType()))) {
                this.setError(taggedObject, "Die Objektart '" + taggedObject.getObjectType() + "' darf keine Relation '" + relationOfTaggedObject.getObjectType() + "' haben.");
            }

            for (Relation.Member memberOfTaggedObject : relationOfTaggedObject.getMembers()) {
                if (objectType.getRelations().stream().filter(r -> r.getObjectType().getName()
                        .equals(relationOfTaggedObject.getObjectType())).findFirst()
                        .stream().noneMatch(r -> r.getMembers().stream()
                                .anyMatch(m -> memberOfTaggedObject.getRole().equals(m.getRole()) &&
                                        (m.getType().equals("*") || memberOfTaggedObject.getType().equalsIgnoreCase(m.getType()))))) {
                    this.setError(taggedObject, "Die Relation '" + relationOfTaggedObject.getObjectType() + "' darf keine Members mit der Rolle '" + memberOfTaggedObject.getRole() + "' haben.");
                }
            }
        }
    }
}