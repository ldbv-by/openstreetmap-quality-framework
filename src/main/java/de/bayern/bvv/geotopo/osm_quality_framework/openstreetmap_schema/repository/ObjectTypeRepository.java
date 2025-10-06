package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.repository;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.entity.ObjectTypeEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ObjectType repository.
 */
@Repository
public interface ObjectTypeRepository extends CrudRepository<ObjectTypeEntity, String> {
    Optional<ObjectTypeEntity> findByObjectType(String objectType);
}
