package de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.repository;

import de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.entity.AreaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChangesetAreaRepository extends JpaRepository<AreaEntity,Long>, ChangesetAreaRepositoryCustom {

    @Query(value = """
        WITH v_relation_members AS (
          SELECT DISTINCT rm.member_osm_id
            FROM changeset_data.relation_members rm
           WHERE rm.relation_osm_id = :relationOsmId
             AND rm.member_type in ('w', 'r')
             AND (:memberRole IS NULL OR rm.member_role = :memberRole)
             AND rm.changeset_id = :changesetId
        )
        SELECT a.*
          FROM changeset_data.areas a
          JOIN v_relation_members rm ON rm.member_osm_id = a.osm_id
        """, nativeQuery = true)
    List<AreaEntity> fetchByRelationIdAndRole(@Param("changesetId")Long changesetId,
                                              @Param("relationOsmId")Long relationOsmId,
                                              @Param("memberRole") String memberRole);
}
