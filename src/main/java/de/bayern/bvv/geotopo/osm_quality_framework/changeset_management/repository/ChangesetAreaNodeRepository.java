package de.bayern.bvv.geotopo.osm_quality_framework.changeset_management.repository;

import de.bayern.bvv.geotopo.osm_quality_framework.changeset_management.entity.AreaNodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChangesetAreaNodeRepository extends JpaRepository<AreaNodeEntity,Long> {
    List<AreaNodeEntity> findByIdAreaOsmIdAndIdChangesetIdOrderByIdSeq(
            Long areaOsmId,
            Long changesetId
    );
}
