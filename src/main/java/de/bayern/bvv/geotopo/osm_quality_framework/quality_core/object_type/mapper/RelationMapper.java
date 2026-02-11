package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.mapper;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.dto.*;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.model.Multiplicity;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.model.Relation;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapping between {@link Relation} and {@link RelationDto}.
 */
@UtilityClass
public class RelationMapper {

    /**
     * Map relation dto to domain.
     */
    public Relation toDomain(RelationDto relationDto) {
        if (relationDto == null) return null;

        Relation relation = new Relation();
        relation.setObjectType(ObjectTypeMapper.toDomain(relationDto.objectType()));
        relation.setMultiplicity(new Multiplicity(relationDto.multiplicity().min(), relationDto.multiplicity().max()));

        List<Relation.Member> members = new ArrayList<>();
        for (MemberDto memberDto : relationDto.members()) {
            members.add(MemberMapper.toDomain(memberDto));
        }
        relation.setMembers(members);

        return relation;
    }

    /**
     * Map relation domain to dto.
     */
    public RelationDto toDto(Relation relation, boolean flattingTags) {
        return toDto(relation, flattingTags, true);
    }

    /**
     * Map relation domain to dto.
     */
    public RelationDto toDto(Relation relation) {
        return toDto(relation, false);
    }

    /**
     * Map relation domain to dto.
     */
    public RelationDto toDto(Relation relation, boolean flattingTags, boolean withRules) {
        if (relation == null) return null;

        List<MemberDto> membersDto = new ArrayList<>();
        for (Relation.Member member : relation.getMembers()) {
            membersDto.add(MemberMapper.toDto(member));
        }

        return new RelationDto(
                ObjectTypeMapper.toDto(relation.getObjectType(), flattingTags, withRules),
                new MultiplicityDto(relation.getMultiplicity().min(), relation.getMultiplicity().max()),
                membersDto);
    }
}
