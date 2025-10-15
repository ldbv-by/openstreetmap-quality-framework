package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.repository;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.entity.AreaEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.FeatureFilter;

import java.util.List;

public interface AreaRepositoryCustom {
    List<AreaEntity> fetchByFeatureFilter(FeatureFilter featureFilter);
}
