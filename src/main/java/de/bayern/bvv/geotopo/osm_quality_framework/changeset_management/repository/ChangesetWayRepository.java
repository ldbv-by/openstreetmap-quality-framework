package de.bayern.bvv.geotopo.osm_quality_framework.changeset_management.repository;

import de.bayern.bvv.geotopo.osm_quality_framework.changeset_management.entity.WayEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChangesetWayRepository extends JpaRepository<WayEntity,Long>, ChangesetWayRepositoryCustom {

    @Query(value = """
        WITH v_relation_members AS (
          SELECT DISTINCT rm.member_osm_id
            FROM changeset_data.relation_members rm
           WHERE rm.relation_osm_id = :relationOsmId
             AND rm.member_type = 'w'
             AND (:memberRole IS NULL OR :memberRole = '' OR rm.member_role = :memberRole)
             AND rm.changeset_id = :changesetId
        )
        SELECT w.*
          FROM changeset_data.ways w
          JOIN v_relation_members rm ON rm.member_osm_id = w.osm_id
        """, nativeQuery = true)
    List<WayEntity> fetchByRelationIdAndRole(@Param("changesetId")Long changesetId,
                                              @Param("relationOsmId")Long relationOsmId,
                                              @Param("memberRole") String memberRole);
}
