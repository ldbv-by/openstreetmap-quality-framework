package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.repository;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.entity.NodeEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.entity.WayEntity;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WayRepository extends JpaRepository<WayEntity,Long>, WayRepositoryCustom {

    @Query(value = """
        WITH v_relation_members AS (
          SELECT DISTINCT rm.member_osm_id
            FROM openstreetmap_geometries.relation_members rm
           WHERE rm.relation_osm_id = :relationOsmId
             AND rm.member_type = 'w'
             AND (:memberRole IS NULL OR :memberRole = '' OR rm.member_role = :memberRole)
        )
        SELECT w.*
          FROM openstreetmap_geometries.ways w
          JOIN v_relation_members rm ON rm.member_osm_id = w.osm_id
        """, nativeQuery = true)
    List<WayEntity> fetchByRelationIdAndRole(@Param("relationOsmId")Long relationOsmId, @Param("memberRole") String memberRole);

}
