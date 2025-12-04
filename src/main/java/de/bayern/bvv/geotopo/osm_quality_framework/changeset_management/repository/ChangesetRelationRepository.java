package de.bayern.bvv.geotopo.osm_quality_framework.changeset_management.repository;

import de.bayern.bvv.geotopo.osm_quality_framework.changeset_management.entity.RelationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChangesetRelationRepository extends JpaRepository<RelationEntity,Long>, ChangesetRelationRepositoryCustom {

    @Query(value = """
        SELECT r.*
          FROM changeset_data.relations r
          JOIN changeset_data.relation_members rm
            ON rm.relation_osm_id = r.osm_id
         WHERE rm.member_osm_id = :memberOsmId
           AND lower(rm.member_type) = lower(:memberType)
           AND rm.changeset_id = :changesetId
        """, nativeQuery = true)
    List<RelationEntity> findAllByMember(@Param("changesetId") Long changesetId, @Param("memberType") String memberType, @Param("memberOsmId")Long memberOsmId);

    @Query(value = """
        WITH v_relation_members AS (
          SELECT DISTINCT rm.member_osm_id
            FROM changeset_data.relation_members rm
           WHERE rm.relation_osm_id = :relationOsmId
             AND rm.member_type = 'r'
             AND (:memberRole IS NULL OR :memberRole = '' OR rm.member_role = :memberRole)
             AND rm.changeset_id = :changesetId
        )
        SELECT r.*
          FROM changeset_data.relations r
          JOIN v_relation_members rm ON rm.member_osm_id = r.osm_id
        """, nativeQuery = true)
    List<RelationEntity> fetchByRelationIdAndRole(@Param("changesetId")Long changesetId,
                                                  @Param("relationOsmId")Long relationOsmId,
                                                  @Param("memberRole") String memberRole);
}
