package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.repository;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.entity.WayEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WayRepository extends JpaRepository<WayEntity,Long>, WayRepositoryCustom {}
