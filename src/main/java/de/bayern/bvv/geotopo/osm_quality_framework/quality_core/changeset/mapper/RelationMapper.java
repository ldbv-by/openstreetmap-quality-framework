package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.mapper;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto.RelationDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto.TagDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.Relation;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.Tag;
import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Mapping between {@link Relation} and {@link RelationDto}.
 */
@UtilityClass
public class RelationMapper {

    /**
     * Map Relation dto to domain.
     */
    public Relation toDomain(RelationDto dto) {
        if (dto == null) return null;

        Relation rel = new Relation();
        rel.setId(dto.getId());
        rel.setVersion(dto.getVersion());
        rel.setChangesetId(dto.getChangesetId());

        rel.setMembers(mapMembersToDomain(dto.getMembers()));
        rel.setTags(mapTagsToDomain(dto.getTags()));
        return rel;
    }

    /**
     * Map Relation domain to dto.
     */
    public RelationDto toDto(Relation domain) {
        if (domain == null) return null;

        RelationDto dto = new RelationDto();
        dto.setId(domain.getId());
        dto.setVersion(domain.getVersion());
        dto.setChangesetId(domain.getChangesetId());

        dto.setMembers(mapMembersToDto(domain.getMembers()));
        dto.setTags(mapTagsToDto(domain.getTags()));
        return dto;
    }

    /**
     * Map Members to domain.
     */
    private List<Relation.Member> mapMembersToDomain(List<RelationDto.MemberDto> members) {
        if (members == null || members.isEmpty()) return Collections.emptyList();
        return members.stream()
                .filter(Objects::nonNull)
                .map(m -> new Relation.Member(m.getType(), m.getRef(), m.getRole()))
                .collect(Collectors.toList());
    }

    /**
     * Map Members to dto.
     */
    private List<RelationDto.MemberDto> mapMembersToDto(List<Relation.Member> members) {
        if (members == null || members.isEmpty()) return Collections.emptyList();
        return members.stream()
                .filter(Objects::nonNull)
                .map(m -> new RelationDto.MemberDto(m.getType(), m.getRef(), m.getRole()))
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
