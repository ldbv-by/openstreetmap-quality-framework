package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.repository;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.entity.NodeEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.entity.RelationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeRepository extends JpaRepository<NodeEntity,Long>, NodeRepositoryCustom {

    @Query(value = """
        WITH v_relation_members AS (
          SELECT DISTINCT rm.member_osm_id
            FROM openstreetmap_geometries.relation_members rm
           WHERE rm.relation_osm_id = :relationOsmId
             AND rm.member_type = 'n'
             AND (:memberRole IS NULL OR :memberRole = '' OR rm.member_role = :memberRole)
        )
        SELECT n.*
          FROM openstreetmap_geometries.nodes n
          JOIN v_relation_members r ON r.member_osm_id = n.osm_id
        """, nativeQuery = true)
    List<NodeEntity> fetchByRelationIdAndRole(@Param("relationOsmId")Long relationOsmId, @Param("memberRole") String memberRole);

}
