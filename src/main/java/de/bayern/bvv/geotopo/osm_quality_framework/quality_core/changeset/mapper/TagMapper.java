package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.mapper;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto.TagDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.Tag;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TagMapper {

    /**
     * Map Tag to domain.
     */
    public Tag toDomain(TagDto dto) {
        if (dto == null) return null;
        return new Tag(dto.getK(), dto.getV());
    }

    /**
     * Map Tag to dto.
     */
    public TagDto toDto(Tag domain) {
        if (domain == null) return null;
        return new TagDto(domain.getK(), domain.getV());
    }
}
