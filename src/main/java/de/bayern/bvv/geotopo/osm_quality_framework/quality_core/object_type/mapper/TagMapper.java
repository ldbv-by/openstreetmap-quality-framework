package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.mapper;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.dto.MultiplicityDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.dto.ObjectTypeDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.dto.TagDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.model.Multiplicity;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.model.ObjectType;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.model.Tag;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapping between {@link ObjectType} and {@link ObjectTypeDto}.
 */
@UtilityClass
public class TagMapper {

    /**
     * Map tag dto to domain.
     */
    public Tag toDomain(TagDto tagDto) {
        if (tagDto == null) return null;

        Tag tag = new Tag();
        tag.setKey(tagDto.key());
        tag.setType(tagDto.type());
        tag.setMultiplicity(new Multiplicity(tagDto.multiplicity().min(), tagDto.multiplicity().max()));
        tag.setDictionary(tagDto.dictionary());

        List<Tag> subTags = new ArrayList<>();
        for (TagDto subTag : tagDto.subTags()) {
            subTags.add(toDomain(subTag));
        }
        tag.setSubTags(subTags);

        return tag;
    }

    /**
     * Map tag domain to dto.
     */
    public TagDto toDto(Tag tag) {
        if (tag == null) return null;

        List<TagDto> subTagDtos = new ArrayList<>();
        for (Tag subTag : tag.getSubTags()) {
            subTagDtos.add(toDto(subTag));
        }

        return new TagDto(
                tag.getKey(),
                tag.getType(),
                new MultiplicityDto(tag.getMultiplicity().min(), tag.getMultiplicity().max()),
                tag.getDictionary(),
                subTagDtos
        );
    }
}
