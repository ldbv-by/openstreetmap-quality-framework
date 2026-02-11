package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.mapper;


import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.entity.ObjectTypeEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.entity.RelationEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.entity.RuleEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.entity.TagEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.model.ObjectType;
import lombok.experimental.UtilityClass;

/**
 * Mapping between {@link ObjectTypeEntity} and {@link ObjectType}.
 */
@UtilityClass
public class ObjectTypeEntityMapper {

    /**
     * Map object type entity to domain.
     */
    public ObjectType toDomain(ObjectTypeEntity objectTypeEntity) {
        if (objectTypeEntity == null) return null;

        ObjectType objectType = new ObjectType();
        objectType.setName(objectTypeEntity.getObjectType());
        objectType.setIsRelation(objectTypeEntity.getIsRelation());
        objectType.setIsAbstract(objectTypeEntity.getIsAbstract());
        objectType.setIsSystem(objectTypeEntity.getIsSystem());

        addAllTags(objectType, objectTypeEntity);
        addAllRelations(objectType, objectTypeEntity);
        addAllRules(objectType, objectTypeEntity);

        return objectType;
    }

    /**
     * Recursively add all tags to the object type (flat mapping).
     */
    private void addAllTags(ObjectType objectType, ObjectTypeEntity objectTypeEntity) {
        for (TagEntity tagEntity : objectTypeEntity.getTags()) {
            objectType.getTags().add(TagEntityMapper.toDomain(tagEntity));
        }

        for (ObjectTypeEntity parent : objectTypeEntity.getParents()) {
            addAllTags(objectType, parent);
        }
    }

    /**
     * Recursively add all relations to the object type (flat mapping).
     */
    private void addAllRelations(ObjectType objectType, ObjectTypeEntity objectTypeEntity) {
        for (RelationEntity relationEntity : objectTypeEntity.getRelations()) {
            objectType.getRelations().add(RelationEntityMapper.toDomain(relationEntity));
        }

        for (ObjectTypeEntity parent : objectTypeEntity.getParents()) {
            addAllRelations(objectType, parent);
        }
    }

    /**
     * Add all rules to the object type.
     */
    private void addAllRules(ObjectType objectType, ObjectTypeEntity objectTypeEntity) {
        for (RuleEntity ruleEntity : objectTypeEntity.getRules()) {
            objectType.getRules().add(RuleEntityMapper.toDomain(ruleEntity));
        }

        for (ObjectTypeEntity parent : objectTypeEntity.getParents()) {
            addAllRules(objectType, parent);
        }
    }

}
