package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.feature.mapper;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.feature.dto.MemberDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.feature.dto.RelationDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.feature.model.Relation;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapping between {@link Relation} and {@link RelationDto}.
 */
@UtilityClass
public class RelationMapper {

    /**
     * Map relation to domain.
     */
    public Relation toDomain(RelationDto relationDto) {
        if (relationDto == null) return null;

        Relation relation = new Relation();
        relation.setOsmId(relationDto.osmId());
        relation.setObjectType(relationDto.objectType());
        relation.setTags(relationDto.tags());
        relation.setMemberOf(relationDto.memberOf());

        for (MemberDto memberDto : relationDto.members()) {
            relation.getMembers().add(MemberMapper.toDomain(memberDto));
        }

        return relation;
    }

    /**
     * Map relation to dto.
     */
    public RelationDto toDto(Relation relation) {
        if (relation == null) return null;

        List<MemberDto> membersDto = new ArrayList<>();
        for (Relation.Member member : relation.getMembers()) {
            membersDto.add(MemberMapper.toDto(member));
        }

        return new RelationDto(
                relation.getOsmId(),
                relation.getObjectType(),
                relation.getTags(),
                relation.getMemberOf(),
                membersDto);
    }
}
