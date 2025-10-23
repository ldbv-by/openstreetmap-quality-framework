package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.repository;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.entity.AreaNodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AreaNodeRepository extends JpaRepository<AreaNodeEntity,Long> {
    List<AreaNodeEntity> findById_AreaOsmIdOrderById_Seq(Long id_areaOsmId);
}
