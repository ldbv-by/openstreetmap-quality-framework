package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.repository;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.entity.AreaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AreaRepository extends JpaRepository<AreaEntity,Long>, AreaRepositoryCustom {

    @Query(value = """
        WITH v_relation_members AS (
          SELECT DISTINCT rm.member_osm_id
            FROM openstreetmap_geometries.relation_members rm
           WHERE rm.relation_osm_id = :relationOsmId
             AND rm.member_type in ('w', 'r')
             AND (:memberRole IS NULL OR :memberRole = '' OR rm.member_role = :memberRole)
        )
        SELECT a.*
          FROM openstreetmap_geometries.areas a
          JOIN v_relation_members rm ON rm.member_osm_id = a.osm_id
        """, nativeQuery = true)
    List<AreaEntity> fetchByRelationIdAndRole(@Param("relationOsmId")Long relationOsmId, @Param("memberRole") String memberRole);
}
