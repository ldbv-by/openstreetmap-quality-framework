package de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.repository;

import de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.entity.ChangesetEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.ChangesetState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ChangesetRepository extends JpaRepository<ChangesetEntity,Long> {
    @Query("""
         SELECT c.id
           FROM ChangesetEntity c
          WHERE c.state in :states
          ORDER BY c.createdAt asc
      """)
    List<Long> findIdsByStatesOrderByCreatedAtAsc(@Param("states") Collection<ChangesetState> states);
}
