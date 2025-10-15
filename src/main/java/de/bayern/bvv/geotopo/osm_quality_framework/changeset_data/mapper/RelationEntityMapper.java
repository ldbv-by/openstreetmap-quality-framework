package de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.mapper;


import de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.entity.RelationEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.Relation;
import lombok.experimental.UtilityClass;

import java.util.List;

/**
 * Mapping between {@link RelationEntity} and {@link Relation}.
 */
@UtilityClass
public class RelationEntityMapper {

    /**
     * Map node entity to feature.
     */
    public Relation toRelation(RelationEntity relationEntity, List<Relation> relations) {
        if (relationEntity == null) return null;

        Relation relation = new Relation();
        relation.setOsmId(relationEntity.getOsmId());
        relation.setObjectType(relationEntity.getObjectType());
        relation.setTags(relationEntity.getTags());
        relation.setRelations(relations);

        for (RelationEntity.Member member : relationEntity.getMembers()) {
            relation.getMembers().add(new Relation.Member(member.getType(), member.getRef(), member.getRole()));
        }

        return relation;
    }
}
