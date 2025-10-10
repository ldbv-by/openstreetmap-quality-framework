package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.mapper;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.dto.MemberDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.dto.MultiplicityDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.model.Multiplicity;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.model.Relation;
import lombok.experimental.UtilityClass;

/**
 * Mapping between {@link Relation.Member} and {@link MemberDto}.
 */
@UtilityClass
public class MemberMapper {

    /**
     * Map relation member to domain.
     */
    public Relation.Member toDomain(MemberDto memberDto) {
        if (memberDto == null) return null;

        Relation.Member member = new Relation.Member();
        member.setType(memberDto.type());
        member.setRole(memberDto.role());
        member.setMultiplicity(new Multiplicity(memberDto.multiplicity().min(), memberDto.multiplicity().max()));

        return member;
    }

    /**
     * Map relation member to dto.
     */
    public MemberDto toDto(Relation.Member member) {
        if (member == null) return null;



        return new MemberDto(
                member.getType(),
                member.getRole(),
                new MultiplicityDto(member.getMultiplicity().min(), member.getMultiplicity().max()));
    }
}
