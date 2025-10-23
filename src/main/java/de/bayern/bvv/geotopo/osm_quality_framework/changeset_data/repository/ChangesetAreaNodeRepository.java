package de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.repository;

import de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.entity.AreaNodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChangesetAreaNodeRepository extends JpaRepository<AreaNodeEntity,Long> {
    List<AreaNodeEntity> findById_AreaOsmIdOrderById_Seq(Long id_areaOsmId);
}
