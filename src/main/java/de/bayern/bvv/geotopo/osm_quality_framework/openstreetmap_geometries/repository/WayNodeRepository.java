package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.repository;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.entity.WayNodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WayNodeRepository extends JpaRepository<WayNodeEntity,Long> {
    List<WayNodeEntity> findById_WayOsmIdOrderById_Seq(Long id_wayOsmId);
}
