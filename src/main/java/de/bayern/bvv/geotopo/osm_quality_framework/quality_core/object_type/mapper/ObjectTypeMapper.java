package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.mapper;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.dto.ObjectTypeDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.dto.TagDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.model.ObjectType;
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

        return objectType;
    }

    /**
     * Map object_type domain to dto.
     */
    public ObjectTypeDto toDto(ObjectType objectType) {
        if (objectType == null) return null;

        return new ObjectTypeDto(
                objectType.getName(),
                mapTagsToDto(objectType.getTags())
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
}
