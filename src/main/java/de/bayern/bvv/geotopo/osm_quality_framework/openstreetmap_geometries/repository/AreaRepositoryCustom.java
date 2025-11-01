package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.repository;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.entity.AreaEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.Criteria;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.OsmIds;

import java.util.List;

public interface AreaRepositoryCustom {
    List<AreaEntity> fetchByFeatureFilter(OsmIds osmIds, Criteria criteria);
}
