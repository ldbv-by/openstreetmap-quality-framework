package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.mapper;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto.NodeDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto.TagDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.Node;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.Tag;
import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@UtilityClass
public class NodeMapper {

    /**
     * Map Node dto to domain.
     */
    public Node toDomain(NodeDto dto) {
        if (dto == null) return null;

        Node node = new Node();
        node.setId(dto.getId());
        node.setVersion(dto.getVersion());
        node.setChangesetId(dto.getChangesetId());
        node.setLat(dto.getLat());
        node.setLon(dto.getLon());

        node.setTags(mapTagsToDomain(dto.getTags()));
        return node;
    }

    /**
     * Map Node domain to dto.
     */
    public NodeDto toDto(Node domain) {
        if (domain == null) return null;

        NodeDto dto = new NodeDto();
        dto.setId(domain.getId());
        dto.setVersion(domain.getVersion());
        dto.setChangesetId(domain.getChangesetId());
        dto.setLat(domain.getLat());
        dto.setLon(domain.getLon());

        dto.setTags(mapTagsToDto(domain.getTags()));
        return dto;
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