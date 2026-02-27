package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.mapper;


import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.entity.DatatypeComplexEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.entity.DatatypeDictionaryEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.entity.DatatypeEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.entity.TagEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.dto.TagType;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.model.Multiplicity;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.model.Tag;
import lombok.experimental.UtilityClass;

import java.util.*;

/**
 * Mapping between {@link TagEntity} and {@link Tag}.
 */
@UtilityClass
public class TagEntityMapper {

    /**
     * Map tag to domain.
     */
    public Tag toDomain(TagEntity tagEntity) {
        return resolveTag(tagEntity.getId().getTagKey(), tagEntity.getMultiplicity(), tagEntity.getTagDatatype(), tagEntity.getIsSystem());
    }

    private Tag resolveTag(String tagKey, String multiplicity, DatatypeEntity datatype, boolean isSystem) {
        if (datatype.getDatatypeType() == DatatypeEntity.DatatypeType.PRIMITIVE) {
            return new Tag(tagKey, TagType.PRIMITIVE, parseMultiplicity(multiplicity), new HashMap<>(), new ArrayList<>(), isSystem);
        }

        if (datatype.getDatatypeType() == DatatypeEntity.DatatypeType.DICTIONARY) {
            Map<String, String> dictionary = new LinkedHashMap<>();

            if (datatype.getDictionaries() != null) {
                for (DatatypeDictionaryEntity e : datatype.getDictionaries()) {
                    if (e != null && e.getId() != null && e.getId().getDictionaryKey() != null) {
                        dictionary.put(e.getId().getDictionaryKey(), e.getDescription());
                    }
                }
            }

            return new Tag(tagKey, TagType.DICTIONARY, parseMultiplicity(multiplicity), dictionary, new ArrayList<>(), isSystem);
        }

        if (datatype.getDatatypeType() == DatatypeEntity.DatatypeType.COMPLEX && datatype.getComplexTags() != null) {
            Tag resultTag = new Tag(tagKey, TagType.COMPLEX, parseMultiplicity(multiplicity), new HashMap<>(), new ArrayList<>(), isSystem);

            for (DatatypeComplexEntity ct : datatype.getComplexTags()) {
                Tag subTag = resolveTag(ct.getId().getTagKey(), ct.getMultiplicity(), ct.getTagDatatype(), isSystem);
                resultTag.getSubTags().add(subTag);
            }

            return resultTag;
        }

        return null;
    }

    private Multiplicity parseMultiplicity(String multiplicity) {
        if (multiplicity == null || multiplicity.isBlank()) return new Multiplicity(1, 1);

        try {
            if (multiplicity.contains("..")) {
                String[] parts = multiplicity.split("\\.\\.");
                if (parts.length != 2) throw new IllegalArgumentException("Not valid multiplicity: " + multiplicity);

                int min = Integer.parseInt(parts[0].trim());
                int max = "*".equals(parts[1].trim()) ? Integer.MAX_VALUE : Integer.parseInt(parts[1].trim());

                return new Multiplicity(min, max);
            }

            int val = Integer.parseInt(multiplicity.trim());
            return new Multiplicity(val, val);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Not valid number in multiplicity: " + multiplicity);
        }
    }
}
