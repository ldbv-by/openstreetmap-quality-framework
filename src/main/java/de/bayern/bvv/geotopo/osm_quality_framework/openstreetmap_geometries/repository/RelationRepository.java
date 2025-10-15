package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.repository;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.entity.RelationEntity;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelationRepository extends JpaRepository<RelationEntity,Long>, RelationRepositoryCustom {

    @Query(value = """
        SELECT r.*
          FROM openstreetmap_geometries.relations r
          JOIN openstreetmap_geometries.relation_members rm
            ON rm.relation_osm_id = r.osm_id
         WHERE rm.member_osm_id = :memberOsmId
           AND lower(rm.member_type) = lower(:memberType)
        """, nativeQuery = true)
    List<RelationEntity> findAllByMember(@Param("memberType") String memberType, @Param("memberOsmId")Long memberOsmId);
}
