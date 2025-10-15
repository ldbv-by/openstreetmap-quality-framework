package de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.repository;

import de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.entity.ChangesetObjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChangesetObjectRepository extends JpaRepository<ChangesetObjectEntity,Long> {
    List<ChangesetObjectEntity> findByChangesetId(Long changesetId);
}
