package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.repository;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.entity.NodeEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.entity.RelationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RelationRepository extends JpaRepository<RelationEntity,Long>, RelationRepositoryCustom {

    @Query(value = """
        SELECT r.*
          FROM openstreetmap_geometries.relations r
          JOIN openstreetmap_geometries.relation_members rm
            ON rm.relation_osm_id = r.osm_id
         WHERE rm.member_osm_id = :memberOsmId
           AND rm.member_type = :memberType
        """, nativeQuery = true)
    List<RelationEntity> findAllByMember(@Param("memberType") String memberType, @Param("memberOsmId")Long memberOsmId);
}
