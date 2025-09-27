package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.mapper;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto.TagDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto.WayDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.Tag;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.Way;
import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Mapping between {@link Way} and {@link WayDto}.
 */
@UtilityClass
public class WayMapper {

    /**
     * Map Way dto to domain.
     */
    public Way toDomain(WayDto dto) {
        if (dto == null) return null;

        Way way = new Way();
        way.setId(dto.getId());
        way.setVersion(dto.getVersion());
        way.setChangesetId(dto.getChangesetId());

        // nd -> Way.Nd
        way.setNodeRefs(mapNdsToDomain(dto.getNodeRefs()));

        // Tags
        way.setTags(mapTagsToDomain(dto.getTags()));
        return way;
    }

    /**
     * Map Way domain to dto.
     */
    public WayDto toDto(Way domain) {
        if (domain == null) return null;

        WayDto dto = new WayDto();
        dto.setId(domain.getId());
        dto.setVersion(domain.getVersion());
        dto.setChangesetId(domain.getChangesetId());

        // Way.Nd -> nd
        dto.setNodeRefs(mapNdsToDto(domain.getNodeRefs()));

        // Tags
        dto.setTags(mapTagsToDto(domain.getTags()));
        return dto;
    }

    /**
     * Map Nds to domain.
     */
    private List<Way.Nd> mapNdsToDomain(List<WayDto.Nd> nds) {
        if (nds == null || nds.isEmpty()) return Collections.emptyList();
        return nds.stream()
                .filter(Objects::nonNull)
                .map(nd -> new Way.Nd(nd.getRef()))
                .collect(Collectors.toList());
    }

    /**
     * Map Nds to dto.
     */
    private List<WayDto.Nd> mapNdsToDto(List<Way.Nd> nds) {
        if (nds == null || nds.isEmpty()) return Collections.emptyList();
        return nds.stream()
                .filter(Objects::nonNull)
                .map(nd -> new WayDto.Nd(nd.getRef()))
                .collect(Collectors.toList());
    }

    /**
     * Map Tags to domain.
     */
    private List<Tag> mapTagsToDomain(List<TagDto> tags) {
        if (tags == null || tags.isEmpty()) return Collections.emptyList();
        return tags.stream().filter(Objects::nonNull).map(TagMapper::toDomain).collect(Collectors.toList());
    }

    /**
     * Map Tags to dto.
     */
    private List<TagDto> mapTagsToDto(List<Tag> tags) {
        if (tags == null || tags.isEmpty()) return Collections.emptyList();
        return tags.stream().filter(Objects::nonNull).map(TagMapper::toDto).collect(Collectors.toList());
    }
}
