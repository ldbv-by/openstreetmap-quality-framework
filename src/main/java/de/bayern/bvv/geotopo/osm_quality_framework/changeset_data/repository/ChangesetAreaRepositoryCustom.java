package de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.repository;

import de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.entity.AreaEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.FeatureFilter;

import java.util.List;

public interface ChangesetAreaRepositoryCustom {
    List<AreaEntity> fetchByFeatureFilter(Long changesetId, FeatureFilter featureFilter);
}
