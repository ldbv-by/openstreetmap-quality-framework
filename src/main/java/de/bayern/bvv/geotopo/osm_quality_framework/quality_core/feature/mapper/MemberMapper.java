package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.feature.mapper;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.feature.dto.MemberDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.feature.dto.RelationDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.feature.model.Relation;
import lombok.experimental.UtilityClass;

/**
 * Mapping between {@link Relation} and {@link RelationDto}.
 */
@UtilityClass
public class MemberMapper {

    /**
     * Map relation member to domain.
     */
    public Relation.Member toDomain(MemberDto memberDto) {
        if (memberDto == null) return null;

        Relation.Member member = new Relation.Member();
        member.setRef(memberDto.ref());
        member.setType(memberDto.type());
        member.setRole(memberDto.role());

        return member;
    }

    /**
     * Map relation member to dto.
     */
    public MemberDto toDto(Relation.Member member) {
        if (member == null) return null;

        return new MemberDto(
                member.getType(),
                member.getRef(),
                member.getRole());
    }
}
