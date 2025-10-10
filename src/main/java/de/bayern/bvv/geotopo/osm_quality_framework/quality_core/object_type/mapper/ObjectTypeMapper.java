package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.mapper;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.dto.ObjectTypeDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.dto.RelationDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.dto.RuleDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.dto.TagDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.model.ObjectType;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.model.Relation;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.model.Rule;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.model.Tag;
import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Mapping between {@link ObjectType} and {@link ObjectTypeDto}.
 */
@UtilityClass
public class ObjectTypeMapper {

    /**
     * Map object_type dto to domain.
     */
    public ObjectType toDomain(ObjectTypeDto objectTypeDto) {
        if (objectTypeDto == null) return null;

        ObjectType objectType = new ObjectType();
        objectType.setName(objectTypeDto.name());
        objectType.setTags(mapTagsToDomain(objectTypeDto.tags()));
        objectType.setRelations(mapRelationsToDomain(objectTypeDto.relations()));
        objectType.setRules(mapRulesToDomain(objectTypeDto.rules()));

        return objectType;
    }

    /**
     * Map object_type domain to dto.
     */
    public ObjectTypeDto toDto(ObjectType objectType) {
        if (objectType == null) return null;

        return new ObjectTypeDto(
                objectType.getName(),
                mapTagsToDto(objectType.getTags()),
                mapRelationsToDto(objectType.getRelations()),
                mapRulesToDto(objectType.getRules())
        );
    }

    /**
     * Map tags to dto.
     */
    private List<TagDto> mapTagsToDto(List<Tag> tags) {
        if (tags == null || tags.isEmpty()) return Collections.emptyList();
        return tags.stream()
                .filter(Objects::nonNull)
                .map(TagMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Map tags to domain.
     */
    private List<Tag> mapTagsToDomain(List<TagDto> tags) {
        if (tags == null || tags.isEmpty()) return Collections.emptyList();

        return tags.stream()
                .filter(Objects::nonNull)
                .map(TagMapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Map relations to dto.
     */
    private List<RelationDto> mapRelationsToDto(List<Relation> relations) {
        if (relations == null || relations.isEmpty()) return Collections.emptyList();
        return relations.stream()
                .filter(Objects::nonNull)
                .map(RelationMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Map relations to domain.
     */
    private List<Relation> mapRelationsToDomain(List<RelationDto> tags) {
        if (tags == null || tags.isEmpty()) return Collections.emptyList();

        return tags.stream()
                .filter(Objects::nonNull)
                .map(RelationMapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Map rules to dto.
     */
    private List<RuleDto> mapRulesToDto(List<Rule> rules) {
        if (rules == null || rules.isEmpty()) return Collections.emptyList();
        return rules.stream()
                .filter(Objects::nonNull)
                .map(RuleMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Map rules to domain.
     */
    private List<Rule> mapRulesToDomain(List<RuleDto> rules) {
        if (rules == null || rules.isEmpty()) return Collections.emptyList();

        return rules.stream()
                .filter(Objects::nonNull)
                .map(RuleMapper::toDomain)
                .collect(Collectors.toList());
    }
}
