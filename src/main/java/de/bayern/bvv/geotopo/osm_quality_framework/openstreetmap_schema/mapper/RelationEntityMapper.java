package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.mapper;


import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.entity.*;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.model.*;
import lombok.experimental.UtilityClass;

/**
 * Mapping between {@link RelationEntity} and {@link Relation}.
 */
@UtilityClass
public class RelationEntityMapper {

    /**
     * Map relation to domain.
     */
    public Relation toDomain(RelationEntity relationEntity) {
        if (relationEntity == null) return null;

        Relation relation = new Relation();
        relation.setObjectType(ObjectTypeEntityMapper.toDomain(relationEntity.getRelationObjectType()));
        relation.setMultiplicity(parseMultiplicity(relationEntity.getMultiplicity()));

        for (RelationMemberEntity member : relationEntity.getMembers()) {
            relation.getMembers().add(
                    new Relation.Member(member.getType(), member.getId().getRole(), parseMultiplicity(member.getMultiplicity()))
            );
        }

        return relation;
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
